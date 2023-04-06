/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.KilledByCrossbowTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractArrow
extends Projectile {
    private static final EntityDataAccessor<Byte> ID_FLAGS = SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> PIERCE_LEVEL = SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BYTE);
    @Nullable
    private BlockState lastState;
    protected boolean inGround;
    protected int inGroundTime;
    public Pickup pickup = Pickup.DISALLOWED;
    public int shakeTime;
    private int life;
    private double baseDamage = 2.0;
    private int knockback;
    private SoundEvent soundEvent = this.getDefaultHitGroundSoundEvent();
    private IntOpenHashSet piercingIgnoreEntityIds;
    private List<Entity> piercedAndKilledEntities;

    protected AbstractArrow(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    protected AbstractArrow(EntityType<? extends AbstractArrow> entityType, double d, double d2, double d3, Level level) {
        this(entityType, level);
        this.setPos(d, d2, d3);
    }

    protected AbstractArrow(EntityType<? extends AbstractArrow> entityType, LivingEntity livingEntity, Level level) {
        this(entityType, livingEntity.getX(), livingEntity.getEyeY() - 0.10000000149011612, livingEntity.getZ(), level);
        this.setOwner(livingEntity);
        if (livingEntity instanceof Player) {
            this.pickup = Pickup.ALLOWED;
        }
    }

    public void setSoundEvent(SoundEvent soundEvent) {
        this.soundEvent = soundEvent;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double d) {
        double d2 = this.getBoundingBox().getSize() * 10.0;
        if (Double.isNaN(d2)) {
            d2 = 1.0;
        }
        return d < (d2 *= 64.0 * AbstractArrow.getViewScale()) * d2;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ID_FLAGS, (byte)0);
        this.entityData.define(PIERCE_LEVEL, (byte)0);
    }

    @Override
    public void shoot(double d, double d2, double d3, float f, float f2) {
        super.shoot(d, d2, d3, f, f2);
        this.life = 0;
    }

    @Override
    public void lerpTo(double d, double d2, double d3, float f, float f2, int n, boolean bl) {
        this.setPos(d, d2, d3);
        this.setRot(f, f2);
    }

    @Override
    public void lerpMotion(double d, double d2, double d3) {
        super.lerpMotion(d, d2, d3);
        this.life = 0;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void tick() {
        BlockPos blockPos;
        Vec3 vec3;
        BlockState blockState;
        Object object;
        super.tick();
        boolean bl = this.isNoPhysics();
        Vec3 vec32 = this.getDeltaMovement();
        if (this.xRotO == 0.0f && this.yRotO == 0.0f) {
            float f = Mth.sqrt(AbstractArrow.getHorizontalDistanceSqr(vec32));
            this.yRot = (float)(Mth.atan2(vec32.x, vec32.z) * 57.2957763671875);
            this.xRot = (float)(Mth.atan2(vec32.y, f) * 57.2957763671875);
            this.yRotO = this.yRot;
            this.xRotO = this.xRot;
        }
        if (!((blockState = this.level.getBlockState(blockPos = this.blockPosition())).isAir() || bl || ((VoxelShape)(object = blockState.getCollisionShape(this.level, blockPos))).isEmpty())) {
            vec3 = this.position();
            for (AABB object2 : ((VoxelShape)object).toAabbs()) {
                if (!object2.move(blockPos).contains(vec3)) continue;
                this.inGround = true;
                break;
            }
        }
        if (this.shakeTime > 0) {
            --this.shakeTime;
        }
        if (this.isInWaterOrRain()) {
            this.clearFire();
        }
        if (this.inGround && !bl) {
            if (this.lastState != blockState && this.shouldFall()) {
                this.startFalling();
            } else if (!this.level.isClientSide) {
                this.tickDespawn();
            }
            ++this.inGroundTime;
            return;
        }
        this.inGroundTime = 0;
        object = this.position();
        Object object3 = this.level.clip(new ClipContext((Vec3)object, vec3 = ((Vec3)object).add(vec32), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (((HitResult)object3).getType() != HitResult.Type.MISS) {
            vec3 = ((HitResult)object3).getLocation();
        }
        while (!this.removed) {
            void var8_13;
            EntityHitResult entityHitResult = this.findHitEntity((Vec3)object, vec3);
            if (entityHitResult != null) {
                object3 = entityHitResult;
            }
            if (object3 != null && ((HitResult)object3).getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult)object3).getEntity();
                Entity entity2 = this.getOwner();
                if (entity instanceof Player && entity2 instanceof Player && !((Player)entity2).canHarmPlayer((Player)entity)) {
                    object3 = null;
                    Object var8_12 = null;
                }
            }
            if (object3 != null && !bl) {
                this.onHit((HitResult)object3);
                this.hasImpulse = true;
            }
            if (var8_13 == null || this.getPierceLevel() <= 0) break;
            object3 = null;
        }
        vec32 = this.getDeltaMovement();
        double d = vec32.x;
        double d2 = vec32.y;
        double d3 = vec32.z;
        if (this.isCritArrow()) {
            for (int i = 0; i < 4; ++i) {
                this.level.addParticle(ParticleTypes.CRIT, this.getX() + d * (double)i / 4.0, this.getY() + d2 * (double)i / 4.0, this.getZ() + d3 * (double)i / 4.0, -d, -d2 + 0.2, -d3);
            }
        }
        double d4 = this.getX() + d;
        double d5 = this.getY() + d2;
        double d6 = this.getZ() + d3;
        float f = Mth.sqrt(AbstractArrow.getHorizontalDistanceSqr(vec32));
        this.yRot = bl ? (float)(Mth.atan2(-d, -d3) * 57.2957763671875) : (float)(Mth.atan2(d, d3) * 57.2957763671875);
        this.xRot = (float)(Mth.atan2(d2, f) * 57.2957763671875);
        this.xRot = AbstractArrow.lerpRotation(this.xRotO, this.xRot);
        this.yRot = AbstractArrow.lerpRotation(this.yRotO, this.yRot);
        float f2 = 0.99f;
        float f3 = 0.05f;
        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                float f4 = 0.25f;
                this.level.addParticle(ParticleTypes.BUBBLE, d4 - d * 0.25, d5 - d2 * 0.25, d6 - d3 * 0.25, d, d2, d3);
            }
            f2 = this.getWaterInertia();
        }
        this.setDeltaMovement(vec32.scale(f2));
        if (!this.isNoGravity() && !bl) {
            Vec3 vec33 = this.getDeltaMovement();
            this.setDeltaMovement(vec33.x, vec33.y - 0.05000000074505806, vec33.z);
        }
        this.setPos(d4, d5, d6);
        this.checkInsideBlocks();
    }

    private boolean shouldFall() {
        return this.inGround && this.level.noCollision(new AABB(this.position(), this.position()).inflate(0.06));
    }

    private void startFalling() {
        this.inGround = false;
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.multiply(this.random.nextFloat() * 0.2f, this.random.nextFloat() * 0.2f, this.random.nextFloat() * 0.2f));
        this.life = 0;
    }

    @Override
    public void move(MoverType moverType, Vec3 vec3) {
        super.move(moverType, vec3);
        if (moverType != MoverType.SELF && this.shouldFall()) {
            this.startFalling();
        }
    }

    protected void tickDespawn() {
        ++this.life;
        if (this.life >= 1200) {
            this.remove();
        }
    }

    private void resetPiercedEntities() {
        if (this.piercedAndKilledEntities != null) {
            this.piercedAndKilledEntities.clear();
        }
        if (this.piercingIgnoreEntityIds != null) {
            this.piercingIgnoreEntityIds.clear();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity entity;
        DamageSource damageSource;
        super.onHitEntity(entityHitResult);
        Entity entity2 = entityHitResult.getEntity();
        float f = (float)this.getDeltaMovement().length();
        int n = Mth.ceil(Mth.clamp((double)f * this.baseDamage, 0.0, 2.147483647E9));
        if (this.getPierceLevel() > 0) {
            if (this.piercingIgnoreEntityIds == null) {
                this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
            }
            if (this.piercedAndKilledEntities == null) {
                this.piercedAndKilledEntities = Lists.newArrayListWithCapacity((int)5);
            }
            if (this.piercingIgnoreEntityIds.size() < this.getPierceLevel() + 1) {
                this.piercingIgnoreEntityIds.add(entity2.getId());
            } else {
                this.remove();
                return;
            }
        }
        if (this.isCritArrow()) {
            long l = this.random.nextInt(n / 2 + 2);
            n = (int)Math.min(l + (long)n, Integer.MAX_VALUE);
        }
        if ((entity = this.getOwner()) == null) {
            damageSource = DamageSource.arrow(this, this);
        } else {
            damageSource = DamageSource.arrow(this, entity);
            if (entity instanceof LivingEntity) {
                ((LivingEntity)entity).setLastHurtMob(entity2);
            }
        }
        boolean bl = entity2.getType() == EntityType.ENDERMAN;
        int n2 = entity2.getRemainingFireTicks();
        if (this.isOnFire() && !bl) {
            entity2.setSecondsOnFire(5);
        }
        if (entity2.hurt(damageSource, n)) {
            if (bl) {
                return;
            }
            if (entity2 instanceof LivingEntity) {
                Object object;
                LivingEntity livingEntity = (LivingEntity)entity2;
                if (!this.level.isClientSide && this.getPierceLevel() <= 0) {
                    livingEntity.setArrowCount(livingEntity.getArrowCount() + 1);
                }
                if (this.knockback > 0 && ((Vec3)(object = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale((double)this.knockback * 0.6))).lengthSqr() > 0.0) {
                    livingEntity.push(((Vec3)object).x, 0.1, ((Vec3)object).z);
                }
                if (!this.level.isClientSide && entity instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingEntity, entity);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity)entity, livingEntity);
                }
                this.doPostHurtEffects(livingEntity);
                if (entity != null && livingEntity != entity && livingEntity instanceof Player && entity instanceof ServerPlayer && !this.isSilent()) {
                    ((ServerPlayer)entity).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0f));
                }
                if (!entity2.isAlive() && this.piercedAndKilledEntities != null) {
                    this.piercedAndKilledEntities.add(livingEntity);
                }
                if (!this.level.isClientSide && entity instanceof ServerPlayer) {
                    object = (ServerPlayer)entity;
                    if (this.piercedAndKilledEntities != null && this.shotFromCrossbow()) {
                        CriteriaTriggers.KILLED_BY_CROSSBOW.trigger((ServerPlayer)object, this.piercedAndKilledEntities);
                    } else if (!entity2.isAlive() && this.shotFromCrossbow()) {
                        CriteriaTriggers.KILLED_BY_CROSSBOW.trigger((ServerPlayer)object, Arrays.asList(entity2));
                    }
                }
            }
            this.playSound(this.soundEvent, 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
            if (this.getPierceLevel() <= 0) {
                this.remove();
            }
        } else {
            entity2.setRemainingFireTicks(n2);
            this.setDeltaMovement(this.getDeltaMovement().scale(-0.1));
            this.yRot += 180.0f;
            this.yRotO += 180.0f;
            if (!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7) {
                if (this.pickup == Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1f);
                }
                this.remove();
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        this.lastState = this.level.getBlockState(blockHitResult.getBlockPos());
        super.onHitBlock(blockHitResult);
        Vec3 vec3 = blockHitResult.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vec3);
        Vec3 vec32 = vec3.normalize().scale(0.05000000074505806);
        this.setPosRaw(this.getX() - vec32.x, this.getY() - vec32.y, this.getZ() - vec32.z);
        this.playSound(this.getHitGroundSoundEvent(), 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
        this.inGround = true;
        this.shakeTime = 7;
        this.setCritArrow(false);
        this.setPierceLevel((byte)0);
        this.setSoundEvent(SoundEvents.ARROW_HIT);
        this.setShotFromCrossbow(false);
        this.resetPiercedEntities();
    }

    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.ARROW_HIT;
    }

    protected final SoundEvent getHitGroundSoundEvent() {
        return this.soundEvent;
    }

    protected void doPostHurtEffects(LivingEntity livingEntity) {
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 vec3, Vec3 vec32) {
        return ProjectileUtil.getEntityHitResult(this.level, this, vec3, vec32, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), this::canHitEntity);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(entity.getId()));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putShort("life", (short)this.life);
        if (this.lastState != null) {
            compoundTag.put("inBlockState", NbtUtils.writeBlockState(this.lastState));
        }
        compoundTag.putByte("shake", (byte)this.shakeTime);
        compoundTag.putBoolean("inGround", this.inGround);
        compoundTag.putByte("pickup", (byte)this.pickup.ordinal());
        compoundTag.putDouble("damage", this.baseDamage);
        compoundTag.putBoolean("crit", this.isCritArrow());
        compoundTag.putByte("PierceLevel", this.getPierceLevel());
        compoundTag.putString("SoundEvent", Registry.SOUND_EVENT.getKey(this.soundEvent).toString());
        compoundTag.putBoolean("ShotFromCrossbow", this.shotFromCrossbow());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.life = compoundTag.getShort("life");
        if (compoundTag.contains("inBlockState", 10)) {
            this.lastState = NbtUtils.readBlockState(compoundTag.getCompound("inBlockState"));
        }
        this.shakeTime = compoundTag.getByte("shake") & 0xFF;
        this.inGround = compoundTag.getBoolean("inGround");
        if (compoundTag.contains("damage", 99)) {
            this.baseDamage = compoundTag.getDouble("damage");
        }
        if (compoundTag.contains("pickup", 99)) {
            this.pickup = Pickup.byOrdinal(compoundTag.getByte("pickup"));
        } else if (compoundTag.contains("player", 99)) {
            this.pickup = compoundTag.getBoolean("player") ? Pickup.ALLOWED : Pickup.DISALLOWED;
        }
        this.setCritArrow(compoundTag.getBoolean("crit"));
        this.setPierceLevel(compoundTag.getByte("PierceLevel"));
        if (compoundTag.contains("SoundEvent", 8)) {
            this.soundEvent = Registry.SOUND_EVENT.getOptional(new ResourceLocation(compoundTag.getString("SoundEvent"))).orElse(this.getDefaultHitGroundSoundEvent());
        }
        this.setShotFromCrossbow(compoundTag.getBoolean("ShotFromCrossbow"));
    }

    @Override
    public void setOwner(@Nullable Entity entity) {
        super.setOwner(entity);
        if (entity instanceof Player) {
            this.pickup = ((Player)entity).abilities.instabuild ? Pickup.CREATIVE_ONLY : Pickup.ALLOWED;
        }
    }

    @Override
    public void playerTouch(Player player) {
        boolean bl;
        if (this.level.isClientSide || !this.inGround && !this.isNoPhysics() || this.shakeTime > 0) {
            return;
        }
        boolean bl2 = bl = this.pickup == Pickup.ALLOWED || this.pickup == Pickup.CREATIVE_ONLY && player.abilities.instabuild || this.isNoPhysics() && this.getOwner().getUUID() == player.getUUID();
        if (this.pickup == Pickup.ALLOWED && !player.inventory.add(this.getPickupItem())) {
            bl = false;
        }
        if (bl) {
            player.take(this, 1);
            this.remove();
        }
    }

    protected abstract ItemStack getPickupItem();

    @Override
    protected boolean isMovementNoisy() {
        return false;
    }

    public void setBaseDamage(double d) {
        this.baseDamage = d;
    }

    public double getBaseDamage() {
        return this.baseDamage;
    }

    public void setKnockback(int n) {
        this.knockback = n;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    protected float getEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return 0.13f;
    }

    public void setCritArrow(boolean bl) {
        this.setFlag(1, bl);
    }

    public void setPierceLevel(byte by) {
        this.entityData.set(PIERCE_LEVEL, by);
    }

    private void setFlag(int n, boolean bl) {
        byte by = this.entityData.get(ID_FLAGS);
        if (bl) {
            this.entityData.set(ID_FLAGS, (byte)(by | n));
        } else {
            this.entityData.set(ID_FLAGS, (byte)(by & ~n));
        }
    }

    public boolean isCritArrow() {
        byte by = this.entityData.get(ID_FLAGS);
        return (by & 1) != 0;
    }

    public boolean shotFromCrossbow() {
        byte by = this.entityData.get(ID_FLAGS);
        return (by & 4) != 0;
    }

    public byte getPierceLevel() {
        return this.entityData.get(PIERCE_LEVEL);
    }

    public void setEnchantmentEffectsFromEntity(LivingEntity livingEntity, float f) {
        int n = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER_ARROWS, livingEntity);
        int n2 = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH_ARROWS, livingEntity);
        this.setBaseDamage((double)(f * 2.0f) + (this.random.nextGaussian() * 0.25 + (double)((float)this.level.getDifficulty().getId() * 0.11f)));
        if (n > 0) {
            this.setBaseDamage(this.getBaseDamage() + (double)n * 0.5 + 0.5);
        }
        if (n2 > 0) {
            this.setKnockback(n2);
        }
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAMING_ARROWS, livingEntity) > 0) {
            this.setSecondsOnFire(100);
        }
    }

    protected float getWaterInertia() {
        return 0.6f;
    }

    public void setNoPhysics(boolean bl) {
        this.noPhysics = bl;
        this.setFlag(2, bl);
    }

    public boolean isNoPhysics() {
        if (!this.level.isClientSide) {
            return this.noPhysics;
        }
        return (this.entityData.get(ID_FLAGS) & 2) != 0;
    }

    public void setShotFromCrossbow(boolean bl) {
        this.setFlag(4, bl);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        Entity entity = this.getOwner();
        return new ClientboundAddEntityPacket(this, entity == null ? 0 : entity.getId());
    }

    public static enum Pickup {
        DISALLOWED,
        ALLOWED,
        CREATIVE_ONLY;
        

        public static Pickup byOrdinal(int n) {
            if (n < 0 || n > Pickup.values().length) {
                n = 0;
            }
            return Pickup.values()[n];
        }
    }

}

