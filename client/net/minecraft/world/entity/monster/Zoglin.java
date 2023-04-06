/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.world.entity.monster;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.IntRange;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunIf;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.RunSometimes;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.sensing.NearestLivingEntitySensor;
import net.minecraft.world.entity.ai.sensing.PlayerSensor;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.hoglin.HoglinBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;

public class Zoglin
extends Monster
implements Enemy,
HoglinBase {
    private static final EntityDataAccessor<Boolean> DATA_BABY_ID = SynchedEntityData.defineId(Zoglin.class, EntityDataSerializers.BOOLEAN);
    private int attackAnimationRemainingTicks;
    protected static final ImmutableList<? extends SensorType<? extends Sensor<? super Zoglin>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS);
    protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN);

    public Zoglin(EntityType<? extends Zoglin> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 5;
    }

    protected Brain.Provider<Zoglin> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        Brain<Zoglin> brain = this.brainProvider().makeBrain(dynamic);
        Zoglin.initCoreActivity(brain);
        Zoglin.initIdleActivity(brain);
        Zoglin.initFightActivity(brain);
        brain.setCoreActivities((Set<Activity>)ImmutableSet.of((Object)Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initCoreActivity(Brain<Zoglin> brain) {
        brain.addActivity(Activity.CORE, 0, (ImmutableList<Behavior<Zoglin>>)ImmutableList.of((Object)new LookAtTargetSink(45, 90), (Object)new MoveToTargetSink()));
    }

    private static void initIdleActivity(Brain<Zoglin> brain) {
        brain.addActivity(Activity.IDLE, 10, (ImmutableList<Behavior<Zoglin>>)ImmutableList.of(new StartAttacking<Zoglin>(Zoglin::findNearestValidAttackTarget), new RunSometimes<LivingEntity>(new SetEntityLookTarget(8.0f), IntRange.of(30, 60)), new RunOne(ImmutableList.of((Object)Pair.of((Object)new RandomStroll(0.4f), (Object)2), (Object)Pair.of((Object)new SetWalkTargetFromLookTarget(0.4f, 3), (Object)2), (Object)Pair.of((Object)new DoNothing(30, 60), (Object)1)))));
    }

    private static void initFightActivity(Brain<Zoglin> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, (ImmutableList<Behavior<Zoglin>>)ImmutableList.of((Object)new SetWalkTargetFromAttackTargetIfTargetOutOfReach(1.0f), new RunIf<Mob>(Zoglin::isAdult, new MeleeAttack(40)), new RunIf<Mob>(Zoglin::isBaby, new MeleeAttack(15)), new StopAttackingIfTargetInvalid()), MemoryModuleType.ATTACK_TARGET);
    }

    private Optional<? extends LivingEntity> findNearestValidAttackTarget() {
        return this.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).orElse((List<LivingEntity>)ImmutableList.of()).stream().filter(Zoglin::isTargetable).findFirst();
    }

    private static boolean isTargetable(LivingEntity livingEntity) {
        EntityType<?> entityType = livingEntity.getType();
        return entityType != EntityType.ZOGLIN && entityType != EntityType.CREEPER && EntitySelector.ATTACK_ALLOWED.test(livingEntity);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_BABY_ID, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        super.onSyncedDataUpdated(entityDataAccessor);
        if (DATA_BABY_ID.equals(entityDataAccessor)) {
            this.refreshDimensions();
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40.0).add(Attributes.MOVEMENT_SPEED, 0.30000001192092896).add(Attributes.KNOCKBACK_RESISTANCE, 0.6000000238418579).add(Attributes.ATTACK_KNOCKBACK, 1.0).add(Attributes.ATTACK_DAMAGE, 6.0);
    }

    public boolean isAdult() {
        return !this.isBaby();
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return false;
        }
        this.attackAnimationRemainingTicks = 10;
        this.level.broadcastEntityEvent(this, (byte)4);
        this.playSound(SoundEvents.ZOGLIN_ATTACK, 1.0f, this.getVoicePitch());
        return HoglinBase.hurtAndThrowTarget(this, (LivingEntity)entity);
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return !this.isLeashed();
    }

    @Override
    protected void blockedByShield(LivingEntity livingEntity) {
        if (!this.isBaby()) {
            HoglinBase.throwTarget(this, livingEntity);
        }
    }

    @Override
    public double getPassengersRidingOffset() {
        return (double)this.getBbHeight() - (this.isBaby() ? 0.2 : 0.15);
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        boolean bl = super.hurt(damageSource, f);
        if (this.level.isClientSide) {
            return false;
        }
        if (!bl || !(damageSource.getEntity() instanceof LivingEntity)) {
            return bl;
        }
        LivingEntity livingEntity = (LivingEntity)damageSource.getEntity();
        if (EntitySelector.ATTACK_ALLOWED.test(livingEntity) && !BehaviorUtils.isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(this, livingEntity, 4.0)) {
            this.setAttackTarget(livingEntity);
        }
        return bl;
    }

    private void setAttackTarget(LivingEntity livingEntity) {
        this.brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        this.brain.setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, livingEntity, 200L);
    }

    public Brain<Zoglin> getBrain() {
        return super.getBrain();
    }

    protected void updateActivity() {
        Activity activity = this.brain.getActiveNonCoreActivity().orElse(null);
        this.brain.setActiveActivityToFirstValid((List<Activity>)ImmutableList.of((Object)Activity.FIGHT, (Object)Activity.IDLE));
        Activity activity2 = this.brain.getActiveNonCoreActivity().orElse(null);
        if (activity2 == Activity.FIGHT && activity != Activity.FIGHT) {
            this.playAngrySound();
        }
        this.setAggressive(this.brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("zoglinBrain");
        this.getBrain().tick((ServerLevel)this.level, this);
        this.level.getProfiler().pop();
        this.updateActivity();
    }

    @Override
    public void setBaby(boolean bl) {
        this.getEntityData().set(DATA_BABY_ID, bl);
        if (!this.level.isClientSide && bl) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(0.5);
        }
    }

    @Override
    public boolean isBaby() {
        return this.getEntityData().get(DATA_BABY_ID);
    }

    @Override
    public void aiStep() {
        if (this.attackAnimationRemainingTicks > 0) {
            --this.attackAnimationRemainingTicks;
        }
        super.aiStep();
    }

    @Override
    public void handleEntityEvent(byte by) {
        if (by == 4) {
            this.attackAnimationRemainingTicks = 10;
            this.playSound(SoundEvents.ZOGLIN_ATTACK, 1.0f, this.getVoicePitch());
        } else {
            super.handleEntityEvent(by);
        }
    }

    @Override
    public int getAttackAnimationRemainingTicks() {
        return this.attackAnimationRemainingTicks;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.level.isClientSide) {
            return null;
        }
        if (this.brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET)) {
            return SoundEvents.ZOGLIN_ANGRY;
        }
        return SoundEvents.ZOGLIN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ZOGLIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOGLIN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(SoundEvents.ZOGLIN_STEP, 0.15f, 1.0f);
    }

    protected void playAngrySound() {
        this.playSound(SoundEvents.ZOGLIN_ANGRY, 1.0f, this.getVoicePitch());
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        if (this.isBaby()) {
            compoundTag.putBoolean("IsBaby", true);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.getBoolean("IsBaby")) {
            this.setBaby(true);
        }
    }
}

