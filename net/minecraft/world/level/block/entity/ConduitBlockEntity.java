/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ConduitBlockEntity
extends BlockEntity
implements TickableBlockEntity {
    private static final Block[] VALID_BLOCKS = new Block[]{Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.SEA_LANTERN, Blocks.DARK_PRISMARINE};
    public int tickCount;
    private float activeRotation;
    private boolean isActive;
    private boolean isHunting;
    private final List<BlockPos> effectBlocks = Lists.newArrayList();
    @Nullable
    private LivingEntity destroyTarget;
    @Nullable
    private UUID destroyTargetUUID;
    private long nextAmbientSoundActivation;

    public ConduitBlockEntity() {
        this(BlockEntityType.CONDUIT);
    }

    public ConduitBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    @Override
    public void load(BlockState blockState, CompoundTag compoundTag) {
        super.load(blockState, compoundTag);
        this.destroyTargetUUID = compoundTag.hasUUID("Target") ? compoundTag.getUUID("Target") : null;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        super.save(compoundTag);
        if (this.destroyTarget != null) {
            compoundTag.putUUID("Target", this.destroyTarget.getUUID());
        }
        return compoundTag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 5, this.getUpdateTag());
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    @Override
    public void tick() {
        ++this.tickCount;
        long l = this.level.getGameTime();
        if (l % 40L == 0L) {
            this.setActive(this.updateShape());
            if (!this.level.isClientSide && this.isActive()) {
                this.applyEffects();
                this.updateDestroyTarget();
            }
        }
        if (l % 80L == 0L && this.isActive()) {
            this.playSound(SoundEvents.CONDUIT_AMBIENT);
        }
        if (l > this.nextAmbientSoundActivation && this.isActive()) {
            this.nextAmbientSoundActivation = l + 60L + (long)this.level.getRandom().nextInt(40);
            this.playSound(SoundEvents.CONDUIT_AMBIENT_SHORT);
        }
        if (this.level.isClientSide) {
            this.updateClientTarget();
            this.animationTick();
            if (this.isActive()) {
                this.activeRotation += 1.0f;
            }
        }
    }

    private boolean updateShape() {
        Object object;
        int n;
        int n2;
        int n3;
        this.effectBlocks.clear();
        for (n2 = -1; n2 <= 1; ++n2) {
            for (n = -1; n <= 1; ++n) {
                for (n3 = -1; n3 <= 1; ++n3) {
                    object = this.worldPosition.offset(n2, n, n3);
                    if (this.level.isWaterAt((BlockPos)object)) continue;
                    return false;
                }
            }
        }
        for (n2 = -2; n2 <= 2; ++n2) {
            for (n = -2; n <= 2; ++n) {
                for (n3 = -2; n3 <= 2; ++n3) {
                    object = Math.abs(n2);
                    int n4 = Math.abs(n);
                    int n5 = Math.abs(n3);
                    if (object <= true && n4 <= 1 && n5 <= 1 || (n2 != 0 || n4 != 2 && n5 != 2) && (n != 0 || object != 2 && n5 != 2) && (n3 != 0 || object != 2 && n4 != 2)) continue;
                    BlockPos blockPos = this.worldPosition.offset(n2, n, n3);
                    BlockState blockState = this.level.getBlockState(blockPos);
                    for (Block block : VALID_BLOCKS) {
                        if (!blockState.is(block)) continue;
                        this.effectBlocks.add(blockPos);
                    }
                }
            }
        }
        this.setHunting(this.effectBlocks.size() >= 42);
        return this.effectBlocks.size() >= 16;
    }

    private void applyEffects() {
        int n;
        int n2;
        int n3 = this.effectBlocks.size();
        int n4 = n3 / 7 * 16;
        int n5 = this.worldPosition.getX();
        AABB aABB = new AABB(n5, n2 = this.worldPosition.getY(), n = this.worldPosition.getZ(), n5 + 1, n2 + 1, n + 1).inflate(n4).expandTowards(0.0, this.level.getMaxBuildHeight(), 0.0);
        List<Player> list = this.level.getEntitiesOfClass(Player.class, aABB);
        if (list.isEmpty()) {
            return;
        }
        for (Player player : list) {
            if (!this.worldPosition.closerThan(player.blockPosition(), (double)n4) || !player.isInWaterOrRain()) continue;
            player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 260, 0, true, true));
        }
    }

    private void updateDestroyTarget() {
        List<LivingEntity> list;
        LivingEntity livingEntity2 = this.destroyTarget;
        int n = this.effectBlocks.size();
        if (n < 42) {
            this.destroyTarget = null;
        } else if (this.destroyTarget == null && this.destroyTargetUUID != null) {
            this.destroyTarget = this.findDestroyTarget();
            this.destroyTargetUUID = null;
        } else if (this.destroyTarget == null) {
            list = this.level.getEntitiesOfClass(LivingEntity.class, this.getDestroyRangeAABB(), livingEntity -> livingEntity instanceof Enemy && livingEntity.isInWaterOrRain());
            if (!list.isEmpty()) {
                this.destroyTarget = (LivingEntity)list.get(this.level.random.nextInt(list.size()));
            }
        } else if (!this.destroyTarget.isAlive() || !this.worldPosition.closerThan(this.destroyTarget.blockPosition(), 8.0)) {
            this.destroyTarget = null;
        }
        if (this.destroyTarget != null) {
            this.level.playSound(null, this.destroyTarget.getX(), this.destroyTarget.getY(), this.destroyTarget.getZ(), SoundEvents.CONDUIT_ATTACK_TARGET, SoundSource.BLOCKS, 1.0f, 1.0f);
            this.destroyTarget.hurt(DamageSource.MAGIC, 4.0f);
        }
        if (livingEntity2 != this.destroyTarget) {
            list = this.getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, (BlockState)((Object)list), (BlockState)((Object)list), 2);
        }
    }

    private void updateClientTarget() {
        if (this.destroyTargetUUID == null) {
            this.destroyTarget = null;
        } else if (this.destroyTarget == null || !this.destroyTarget.getUUID().equals(this.destroyTargetUUID)) {
            this.destroyTarget = this.findDestroyTarget();
            if (this.destroyTarget == null) {
                this.destroyTargetUUID = null;
            }
        }
    }

    private AABB getDestroyRangeAABB() {
        int n = this.worldPosition.getX();
        int n2 = this.worldPosition.getY();
        int n3 = this.worldPosition.getZ();
        return new AABB(n, n2, n3, n + 1, n2 + 1, n3 + 1).inflate(8.0);
    }

    @Nullable
    private LivingEntity findDestroyTarget() {
        List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, this.getDestroyRangeAABB(), livingEntity -> livingEntity.getUUID().equals(this.destroyTargetUUID));
        if (list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    private void animationTick() {
        float f;
        float f2;
        Random random = this.level.random;
        double d = Mth.sin((float)(this.tickCount + 35) * 0.1f) / 2.0f + 0.5f;
        d = (d * d + d) * 0.30000001192092896;
        Vec3 vec3 = new Vec3((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 1.5 + d, (double)this.worldPosition.getZ() + 0.5);
        for (BlockPos blockPos : this.effectBlocks) {
            if (random.nextInt(50) != 0) continue;
            f2 = -0.5f + random.nextFloat();
            f = -2.0f + random.nextFloat();
            float f3 = -0.5f + random.nextFloat();
            BlockPos blockPos2 = blockPos.subtract(this.worldPosition);
            Vec3 vec32 = new Vec3(f2, f, f3).add(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
            this.level.addParticle(ParticleTypes.NAUTILUS, vec3.x, vec3.y, vec3.z, vec32.x, vec32.y, vec32.z);
        }
        if (this.destroyTarget != null) {
            Vec3 vec33 = new Vec3(this.destroyTarget.getX(), this.destroyTarget.getEyeY(), this.destroyTarget.getZ());
            float f4 = (-0.5f + random.nextFloat()) * (3.0f + this.destroyTarget.getBbWidth());
            f2 = -1.0f + random.nextFloat() * this.destroyTarget.getBbHeight();
            f = (-0.5f + random.nextFloat()) * (3.0f + this.destroyTarget.getBbWidth());
            Vec3 vec34 = new Vec3(f4, f2, f);
            this.level.addParticle(ParticleTypes.NAUTILUS, vec33.x, vec33.y, vec33.z, vec34.x, vec34.y, vec34.z);
        }
    }

    public boolean isActive() {
        return this.isActive;
    }

    public boolean isHunting() {
        return this.isHunting;
    }

    private void setActive(boolean bl) {
        if (bl != this.isActive) {
            this.playSound(bl ? SoundEvents.CONDUIT_ACTIVATE : SoundEvents.CONDUIT_DEACTIVATE);
        }
        this.isActive = bl;
    }

    private void setHunting(boolean bl) {
        this.isHunting = bl;
    }

    public float getActiveRotation(float f) {
        return (this.activeRotation + f) * -0.0375f;
    }

    public void playSound(SoundEvent soundEvent) {
        this.level.playSound(null, this.worldPosition, soundEvent, SoundSource.BLOCKS, 1.0f, 1.0f);
    }
}

