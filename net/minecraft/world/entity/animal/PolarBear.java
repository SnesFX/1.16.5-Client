/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.IntRange;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class PolarBear
extends Animal
implements NeutralMob {
    private static final EntityDataAccessor<Boolean> DATA_STANDING_ID = SynchedEntityData.defineId(PolarBear.class, EntityDataSerializers.BOOLEAN);
    private float clientSideStandAnimationO;
    private float clientSideStandAnimation;
    private int warningSoundTicks;
    private static final IntRange PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    private UUID persistentAngerTarget;

    public PolarBear(EntityType<? extends PolarBear> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public AgableMob getBreedOffspring(ServerLevel serverLevel, AgableMob agableMob) {
        return EntityType.POLAR_BEAR.create(serverLevel);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PolarBearMeleeAttackGoal());
        this.goalSelector.addGoal(1, new PolarBearPanicGoal());
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new PolarBearHurtByTargetGoal());
        this.targetSelector.addGoal(2, new PolarBearAttackPlayersGoal());
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<Player>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<Fox>(this, Fox.class, 10, true, true, null));
        this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<PolarBear>(this, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0).add(Attributes.FOLLOW_RANGE, 20.0).add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.ATTACK_DAMAGE, 6.0);
    }

    public static boolean checkPolarBearSpawnRules(EntityType<PolarBear> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
        Optional<ResourceKey<Biome>> optional = levelAccessor.getBiomeName(blockPos);
        if (Objects.equals(optional, Optional.of(Biomes.FROZEN_OCEAN)) || Objects.equals(optional, Optional.of(Biomes.DEEP_FROZEN_OCEAN))) {
            return levelAccessor.getRawBrightness(blockPos, 0) > 8 && levelAccessor.getBlockState(blockPos.below()).is(Blocks.ICE);
        }
        return PolarBear.checkAnimalSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, random);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.readPersistentAngerSaveData((ServerLevel)this.level, compoundTag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        this.addPersistentAngerSaveData(compoundTag);
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.randomValue(this.random));
    }

    @Override
    public void setRemainingPersistentAngerTime(int n) {
        this.remainingPersistentAngerTime = n;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID uUID) {
        this.persistentAngerTarget = uUID;
    }

    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isBaby()) {
            return SoundEvents.POLAR_BEAR_AMBIENT_BABY;
        }
        return SoundEvents.POLAR_BEAR_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.POLAR_BEAR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.POLAR_BEAR_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(SoundEvents.POLAR_BEAR_STEP, 0.15f, 1.0f);
    }

    protected void playWarningSound() {
        if (this.warningSoundTicks <= 0) {
            this.playSound(SoundEvents.POLAR_BEAR_WARNING, 1.0f, this.getVoicePitch());
            this.warningSoundTicks = 40;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_STANDING_ID, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            if (this.clientSideStandAnimation != this.clientSideStandAnimationO) {
                this.refreshDimensions();
            }
            this.clientSideStandAnimationO = this.clientSideStandAnimation;
            this.clientSideStandAnimation = this.isStanding() ? Mth.clamp(this.clientSideStandAnimation + 1.0f, 0.0f, 6.0f) : Mth.clamp(this.clientSideStandAnimation - 1.0f, 0.0f, 6.0f);
        }
        if (this.warningSoundTicks > 0) {
            --this.warningSoundTicks;
        }
        if (!this.level.isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level, true);
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        if (this.clientSideStandAnimation > 0.0f) {
            float f = this.clientSideStandAnimation / 6.0f;
            float f2 = 1.0f + f;
            return super.getDimensions(pose).scale(1.0f, f2);
        }
        return super.getDimensions(pose);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean bl = entity.hurt(DamageSource.mobAttack(this), (int)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
        if (bl) {
            this.doEnchantDamageEffects(this, entity);
        }
        return bl;
    }

    public boolean isStanding() {
        return this.entityData.get(DATA_STANDING_ID);
    }

    public void setStanding(boolean bl) {
        this.entityData.set(DATA_STANDING_ID, bl);
    }

    public float getStandingAnimationScale(float f) {
        return Mth.lerp(f, this.clientSideStandAnimationO, this.clientSideStandAnimation) / 6.0f;
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.98f;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        if (spawnGroupData == null) {
            spawnGroupData = new AgableMob.AgableMobGroupData(1.0f);
        }
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    class PolarBearPanicGoal
    extends PanicGoal {
        public PolarBearPanicGoal() {
            super(PolarBear.this, 2.0);
        }

        @Override
        public boolean canUse() {
            if (!PolarBear.this.isBaby() && !PolarBear.this.isOnFire()) {
                return false;
            }
            return super.canUse();
        }
    }

    class PolarBearMeleeAttackGoal
    extends MeleeAttackGoal {
        public PolarBearMeleeAttackGoal() {
            super(PolarBear.this, 1.25, true);
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity livingEntity, double d) {
            double d2 = this.getAttackReachSqr(livingEntity);
            if (d <= d2 && this.isTimeToAttack()) {
                this.resetAttackCooldown();
                this.mob.doHurtTarget(livingEntity);
                PolarBear.this.setStanding(false);
            } else if (d <= d2 * 2.0) {
                if (this.isTimeToAttack()) {
                    PolarBear.this.setStanding(false);
                    this.resetAttackCooldown();
                }
                if (this.getTicksUntilNextAttack() <= 10) {
                    PolarBear.this.setStanding(true);
                    PolarBear.this.playWarningSound();
                }
            } else {
                this.resetAttackCooldown();
                PolarBear.this.setStanding(false);
            }
        }

        @Override
        public void stop() {
            PolarBear.this.setStanding(false);
            super.stop();
        }

        @Override
        protected double getAttackReachSqr(LivingEntity livingEntity) {
            return 4.0f + livingEntity.getBbWidth();
        }
    }

    class PolarBearAttackPlayersGoal
    extends NearestAttackableTargetGoal<Player> {
        public PolarBearAttackPlayersGoal() {
            super(PolarBear.this, Player.class, 20, true, true, null);
        }

        @Override
        public boolean canUse() {
            if (PolarBear.this.isBaby()) {
                return false;
            }
            if (super.canUse()) {
                List<PolarBear> list = PolarBear.this.level.getEntitiesOfClass(PolarBear.class, PolarBear.this.getBoundingBox().inflate(8.0, 4.0, 8.0));
                for (PolarBear polarBear : list) {
                    if (!polarBear.isBaby()) continue;
                    return true;
                }
            }
            return false;
        }

        @Override
        protected double getFollowDistance() {
            return super.getFollowDistance() * 0.5;
        }
    }

    class PolarBearHurtByTargetGoal
    extends HurtByTargetGoal {
        public PolarBearHurtByTargetGoal() {
            super(PolarBear.this, new Class[0]);
        }

        @Override
        public void start() {
            super.start();
            if (PolarBear.this.isBaby()) {
                this.alertOthers();
                this.stop();
            }
        }

        @Override
        protected void alertOther(Mob mob, LivingEntity livingEntity) {
            if (mob instanceof PolarBear && !mob.isBaby()) {
                super.alertOther(mob, livingEntity);
            }
        }
    }

}

