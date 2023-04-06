/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Explosion {
    private static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new ExplosionDamageCalculator();
    private final boolean fire;
    private final BlockInteraction blockInteraction;
    private final Random random = new Random();
    private final Level level;
    private final double x;
    private final double y;
    private final double z;
    @Nullable
    private final Entity source;
    private final float radius;
    private final DamageSource damageSource;
    private final ExplosionDamageCalculator damageCalculator;
    private final List<BlockPos> toBlow = Lists.newArrayList();
    private final Map<Player, Vec3> hitPlayers = Maps.newHashMap();

    public Explosion(Level level, @Nullable Entity entity, double d, double d2, double d3, float f, List<BlockPos> list) {
        this(level, entity, d, d2, d3, f, false, BlockInteraction.DESTROY, list);
    }

    public Explosion(Level level, @Nullable Entity entity, double d, double d2, double d3, float f, boolean bl, BlockInteraction blockInteraction, List<BlockPos> list) {
        this(level, entity, d, d2, d3, f, bl, blockInteraction);
        this.toBlow.addAll(list);
    }

    public Explosion(Level level, @Nullable Entity entity, double d, double d2, double d3, float f, boolean bl, BlockInteraction blockInteraction) {
        this(level, entity, null, null, d, d2, d3, f, bl, blockInteraction);
    }

    public Explosion(Level level, @Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator explosionDamageCalculator, double d, double d2, double d3, float f, boolean bl, BlockInteraction blockInteraction) {
        this.level = level;
        this.source = entity;
        this.radius = f;
        this.x = d;
        this.y = d2;
        this.z = d3;
        this.fire = bl;
        this.blockInteraction = blockInteraction;
        this.damageSource = damageSource == null ? DamageSource.explosion(this) : damageSource;
        this.damageCalculator = explosionDamageCalculator == null ? this.makeDamageCalculator(entity) : explosionDamageCalculator;
    }

    private ExplosionDamageCalculator makeDamageCalculator(@Nullable Entity entity) {
        return entity == null ? EXPLOSION_DAMAGE_CALCULATOR : new EntityBasedExplosionDamageCalculator(entity);
    }

    public static float getSeenPercent(Vec3 vec3, Entity entity) {
        AABB aABB = entity.getBoundingBox();
        double d = 1.0 / ((aABB.maxX - aABB.minX) * 2.0 + 1.0);
        double d2 = 1.0 / ((aABB.maxY - aABB.minY) * 2.0 + 1.0);
        double d3 = 1.0 / ((aABB.maxZ - aABB.minZ) * 2.0 + 1.0);
        double d4 = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
        double d5 = (1.0 - Math.floor(1.0 / d3) * d3) / 2.0;
        if (d < 0.0 || d2 < 0.0 || d3 < 0.0) {
            return 0.0f;
        }
        int n = 0;
        int n2 = 0;
        float f = 0.0f;
        while (f <= 1.0f) {
            float f2 = 0.0f;
            while (f2 <= 1.0f) {
                float f3 = 0.0f;
                while (f3 <= 1.0f) {
                    double d6;
                    double d7;
                    double d8 = Mth.lerp((double)f, aABB.minX, aABB.maxX);
                    Vec3 vec32 = new Vec3(d8 + d4, d6 = Mth.lerp((double)f2, aABB.minY, aABB.maxY), (d7 = Mth.lerp((double)f3, aABB.minZ, aABB.maxZ)) + d5);
                    if (entity.level.clip(new ClipContext(vec32, vec3, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() == HitResult.Type.MISS) {
                        ++n;
                    }
                    ++n2;
                    f3 = (float)((double)f3 + d3);
                }
                f2 = (float)((double)f2 + d2);
            }
            f = (float)((double)f + d);
        }
        return (float)n / (float)n2;
    }

    public void explode() {
        Object object;
        int n;
        Object object2;
        int n2;
        HashSet hashSet = Sets.newHashSet();
        int n3 = 16;
        for (int i = 0; i < 16; ++i) {
            for (n2 = 0; n2 < 16; ++n2) {
                for (n = 0; n < 16; ++n) {
                    if (i != 0 && i != 15 && n2 != 0 && n2 != 15 && n != 0 && n != 15) continue;
                    double d = (float)i / 15.0f * 2.0f - 1.0f;
                    double d2 = (float)n2 / 15.0f * 2.0f - 1.0f;
                    double d3 = (float)n / 15.0f * 2.0f - 1.0f;
                    double d4 = Math.sqrt(d * d + d2 * d2 + d3 * d3);
                    d /= d4;
                    d2 /= d4;
                    d3 /= d4;
                    double d5 = this.x;
                    double d6 = this.y;
                    double d7 = this.z;
                    float f = 0.3f;
                    for (float f2 = this.radius * (0.7f + this.level.random.nextFloat() * 0.6f); f2 > 0.0f; f2 -= 0.22500001f) {
                        object = new BlockPos(d5, d6, d7);
                        BlockState blockState = this.level.getBlockState((BlockPos)object);
                        Optional<Float> optional = this.damageCalculator.getBlockExplosionResistance(this, this.level, (BlockPos)object, blockState, (FluidState)(object2 = this.level.getFluidState((BlockPos)object)));
                        if (optional.isPresent()) {
                            f2 -= (optional.get().floatValue() + 0.3f) * 0.3f;
                        }
                        if (f2 > 0.0f && this.damageCalculator.shouldBlockExplode(this, this.level, (BlockPos)object, blockState, f2)) {
                            hashSet.add(object);
                        }
                        d5 += d * 0.30000001192092896;
                        d6 += d2 * 0.30000001192092896;
                        d7 += d3 * 0.30000001192092896;
                    }
                }
            }
        }
        this.toBlow.addAll(hashSet);
        float f = this.radius * 2.0f;
        n2 = Mth.floor(this.x - (double)f - 1.0);
        n = Mth.floor(this.x + (double)f + 1.0);
        int n4 = Mth.floor(this.y - (double)f - 1.0);
        int n5 = Mth.floor(this.y + (double)f + 1.0);
        int n6 = Mth.floor(this.z - (double)f - 1.0);
        int n7 = Mth.floor(this.z + (double)f + 1.0);
        List<Entity> list = this.level.getEntities(this.source, new AABB(n2, n4, n6, n, n5, n7));
        Vec3 vec3 = new Vec3(this.x, this.y, this.z);
        for (int i = 0; i < list.size(); ++i) {
            double d;
            double d8;
            double d9;
            Player player;
            double d10;
            Entity entity = list.get(i);
            if (entity.ignoreExplosion() || !((d = (double)(Mth.sqrt(entity.distanceToSqr(vec3)) / f)) <= 1.0) || (object = (Object)Mth.sqrt((d10 = entity.getX() - this.x) * d10 + (d8 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.y) * d8 + (d9 = entity.getZ() - this.z) * d9)) == 0.0) continue;
            d10 /= object;
            d8 /= object;
            d9 /= object;
            object2 = (double)Explosion.getSeenPercent(vec3, entity);
            double d11 = (1.0 - d) * object2;
            entity.hurt(this.getDamageSource(), (int)((d11 * d11 + d11) / 2.0 * 7.0 * (double)f + 1.0));
            double d12 = d11;
            if (entity instanceof LivingEntity) {
                d12 = ProtectionEnchantment.getExplosionKnockbackAfterDampener((LivingEntity)entity, d11);
            }
            entity.setDeltaMovement(entity.getDeltaMovement().add(d10 * d12, d8 * d12, d9 * d12));
            if (!(entity instanceof Player) || (player = (Player)entity).isSpectator() || player.isCreative() && player.abilities.flying) continue;
            this.hitPlayers.put(player, new Vec3(d10 * d11, d8 * d11, d9 * d11));
        }
    }

    public void finalizeExplosion(boolean bl) {
        boolean bl2;
        if (this.level.isClientSide) {
            this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0f, (1.0f + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2f) * 0.7f, false);
        }
        boolean bl3 = bl2 = this.blockInteraction != BlockInteraction.NONE;
        if (bl) {
            if (this.radius < 2.0f || !bl2) {
                this.level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0, 0.0, 0.0);
            } else {
                this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0, 0.0, 0.0);
            }
        }
        if (bl2) {
            ObjectArrayList objectArrayList = new ObjectArrayList();
            Collections.shuffle(this.toBlow, this.level.random);
            for (BlockPos blockPos : this.toBlow) {
                BlockState blockState = this.level.getBlockState(blockPos);
                Block block = blockState.getBlock();
                if (blockState.isAir()) continue;
                BlockPos blockPos2 = blockPos.immutable();
                this.level.getProfiler().push("explosion_blocks");
                if (block.dropFromExplosion(this) && this.level instanceof ServerLevel) {
                    BlockEntity blockEntity = block.isEntityBlock() ? this.level.getBlockEntity(blockPos) : null;
                    LootContext.Builder builder = new LootContext.Builder((ServerLevel)this.level).withRandom(this.level.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockPos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity).withOptionalParameter(LootContextParams.THIS_ENTITY, this.source);
                    if (this.blockInteraction == BlockInteraction.DESTROY) {
                        builder.withParameter(LootContextParams.EXPLOSION_RADIUS, Float.valueOf(this.radius));
                    }
                    blockState.getDrops(builder).forEach(itemStack -> Explosion.addBlockDrops((ObjectArrayList<Pair<ItemStack, BlockPos>>)objectArrayList, itemStack, blockPos2));
                }
                this.level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                block.wasExploded(this.level, blockPos, this);
                this.level.getProfiler().pop();
            }
            ObjectListIterator objectListIterator = objectArrayList.iterator();
            while (objectListIterator.hasNext()) {
                BlockPos blockPos;
                blockPos = (Pair)objectListIterator.next();
                Block.popResource(this.level, (BlockPos)blockPos.getSecond(), (ItemStack)blockPos.getFirst());
            }
        }
        if (this.fire) {
            for (BlockPos blockPos : this.toBlow) {
                if (this.random.nextInt(3) != 0 || !this.level.getBlockState(blockPos).isAir() || !this.level.getBlockState(blockPos.below()).isSolidRender(this.level, blockPos.below())) continue;
                this.level.setBlockAndUpdate(blockPos, BaseFireBlock.getState(this.level, blockPos));
            }
        }
    }

    private static void addBlockDrops(ObjectArrayList<Pair<ItemStack, BlockPos>> objectArrayList, ItemStack itemStack, BlockPos blockPos) {
        int n = objectArrayList.size();
        for (int i = 0; i < n; ++i) {
            Pair pair = (Pair)objectArrayList.get(i);
            ItemStack itemStack2 = (ItemStack)pair.getFirst();
            if (!ItemEntity.areMergable(itemStack2, itemStack)) continue;
            ItemStack itemStack3 = ItemEntity.merge(itemStack2, itemStack, 16);
            objectArrayList.set(i, (Object)Pair.of((Object)itemStack3, (Object)pair.getSecond()));
            if (!itemStack.isEmpty()) continue;
            return;
        }
        objectArrayList.add((Object)Pair.of((Object)itemStack, (Object)blockPos));
    }

    public DamageSource getDamageSource() {
        return this.damageSource;
    }

    public Map<Player, Vec3> getHitPlayers() {
        return this.hitPlayers;
    }

    @Nullable
    public LivingEntity getSourceMob() {
        Entity entity;
        if (this.source == null) {
            return null;
        }
        if (this.source instanceof PrimedTnt) {
            return ((PrimedTnt)this.source).getOwner();
        }
        if (this.source instanceof LivingEntity) {
            return (LivingEntity)this.source;
        }
        if (this.source instanceof Projectile && (entity = ((Projectile)this.source).getOwner()) instanceof LivingEntity) {
            return (LivingEntity)entity;
        }
        return null;
    }

    public void clearToBlow() {
        this.toBlow.clear();
    }

    public List<BlockPos> getToBlow() {
        return this.toBlow;
    }

    public static enum BlockInteraction {
        NONE,
        BREAK,
        DESTROY;
        
    }

}

