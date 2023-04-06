/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ambient;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Bat
extends AmbientCreature {
    private static final EntityDataAccessor<Byte> DATA_ID_FLAGS = SynchedEntityData.defineId(Bat.class, EntityDataSerializers.BYTE);
    private static final TargetingConditions BAT_RESTING_TARGETING = new TargetingConditions().range(4.0).allowSameTeam();
    private BlockPos targetPosition;

    public Bat(EntityType<? extends Bat> entityType, Level level) {
        super(entityType, level);
        this.setResting(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_FLAGS, (byte)0);
    }

    @Override
    protected float getSoundVolume() {
        return 0.1f;
    }

    @Override
    protected float getVoicePitch() {
        return super.getVoicePitch() * 0.95f;
    }

    @Nullable
    @Override
    public SoundEvent getAmbientSound() {
        if (this.isResting() && this.random.nextInt(4) != 0) {
            return null;
        }
        return SoundEvents.BAT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.BAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BAT_DEATH;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    protected void pushEntities() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0);
    }

    public boolean isResting() {
        return (this.entityData.get(DATA_ID_FLAGS) & 1) != 0;
    }

    public void setResting(boolean bl) {
        byte by = this.entityData.get(DATA_ID_FLAGS);
        if (bl) {
            this.entityData.set(DATA_ID_FLAGS, (byte)(by | 1));
        } else {
            this.entityData.set(DATA_ID_FLAGS, (byte)(by & 0xFFFFFFFE));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isResting()) {
            this.setDeltaMovement(Vec3.ZERO);
            this.setPosRaw(this.getX(), (double)Mth.floor(this.getY()) + 1.0 - (double)this.getBbHeight(), this.getZ());
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6, 1.0));
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        BlockPos blockPos = this.blockPosition();
        BlockPos blockPos2 = blockPos.above();
        if (this.isResting()) {
            boolean bl = this.isSilent();
            if (this.level.getBlockState(blockPos2).isRedstoneConductor(this.level, blockPos)) {
                if (this.random.nextInt(200) == 0) {
                    this.yHeadRot = this.random.nextInt(360);
                }
                if (this.level.getNearestPlayer(BAT_RESTING_TARGETING, this) != null) {
                    this.setResting(false);
                    if (!bl) {
                        this.level.levelEvent(null, 1025, blockPos, 0);
                    }
                }
            } else {
                this.setResting(false);
                if (!bl) {
                    this.level.levelEvent(null, 1025, blockPos, 0);
                }
            }
        } else {
            if (!(this.targetPosition == null || this.level.isEmptyBlock(this.targetPosition) && this.targetPosition.getY() >= 1)) {
                this.targetPosition = null;
            }
            if (this.targetPosition == null || this.random.nextInt(30) == 0 || this.targetPosition.closerThan(this.position(), 2.0)) {
                this.targetPosition = new BlockPos(this.getX() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7), this.getY() + (double)this.random.nextInt(6) - 2.0, this.getZ() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7));
            }
            double d = (double)this.targetPosition.getX() + 0.5 - this.getX();
            double d2 = (double)this.targetPosition.getY() + 0.1 - this.getY();
            double d3 = (double)this.targetPosition.getZ() + 0.5 - this.getZ();
            Vec3 vec3 = this.getDeltaMovement();
            Vec3 vec32 = vec3.add((Math.signum(d) * 0.5 - vec3.x) * 0.10000000149011612, (Math.signum(d2) * 0.699999988079071 - vec3.y) * 0.10000000149011612, (Math.signum(d3) * 0.5 - vec3.z) * 0.10000000149011612);
            this.setDeltaMovement(vec32);
            float f = (float)(Mth.atan2(vec32.z, vec32.x) * 57.2957763671875) - 90.0f;
            float f2 = Mth.wrapDegrees(f - this.yRot);
            this.zza = 0.5f;
            this.yRot += f2;
            if (this.random.nextInt(100) == 0 && this.level.getBlockState(blockPos2).isRedstoneConductor(this.level, blockPos2)) {
                this.setResting(true);
            }
        }
    }

    @Override
    protected boolean isMovementNoisy() {
        return false;
    }

    @Override
    public boolean causeFallDamage(float f, float f2) {
        return false;
    }

    @Override
    protected void checkFallDamage(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        if (!this.level.isClientSide && this.isResting()) {
            this.setResting(false);
        }
        return super.hurt(damageSource, f);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.entityData.set(DATA_ID_FLAGS, compoundTag.getByte("BatFlags"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putByte("BatFlags", this.entityData.get(DATA_ID_FLAGS));
    }

    public static boolean checkBatSpawnRules(EntityType<Bat> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
        if (blockPos.getY() >= levelAccessor.getSeaLevel()) {
            return false;
        }
        int n = levelAccessor.getMaxLocalRawBrightness(blockPos);
        int n2 = 4;
        if (Bat.isHalloween()) {
            n2 = 7;
        } else if (random.nextBoolean()) {
            return false;
        }
        if (n > random.nextInt(n2)) {
            return false;
        }
        return Bat.checkMobSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, random);
    }

    private static boolean isHalloween() {
        LocalDate localDate = LocalDate.now();
        int n = localDate.get(ChronoField.DAY_OF_MONTH);
        int n2 = localDate.get(ChronoField.MONTH_OF_YEAR);
        return n2 == 10 && n >= 20 || n2 == 11 && n <= 3;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return entityDimensions.height / 2.0f;
    }
}

