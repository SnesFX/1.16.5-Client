/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.monster;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ItemBasedSteering;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Strider
extends Animal
implements ItemSteerable,
Saddleable {
    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.WARPED_FUNGUS);
    private static final Ingredient TEMPT_ITEMS = Ingredient.of(Items.WARPED_FUNGUS, Items.WARPED_FUNGUS_ON_A_STICK);
    private static final EntityDataAccessor<Integer> DATA_BOOST_TIME = SynchedEntityData.defineId(Strider.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_SUFFOCATING = SynchedEntityData.defineId(Strider.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SADDLE_ID = SynchedEntityData.defineId(Strider.class, EntityDataSerializers.BOOLEAN);
    private final ItemBasedSteering steering;
    private TemptGoal temptGoal;
    private PanicGoal panicGoal;

    public Strider(EntityType<? extends Strider> entityType, Level level) {
        super(entityType, level);
        this.steering = new ItemBasedSteering(this.entityData, DATA_BOOST_TIME, DATA_SADDLE_ID);
        this.blocksBuilding = true;
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0f);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0f);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0f);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0f);
    }

    public static boolean checkStriderSpawnRules(EntityType<Strider> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
        BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();
        do {
            mutableBlockPos.move(Direction.UP);
        } while (levelAccessor.getFluidState(mutableBlockPos).is(FluidTags.LAVA));
        return levelAccessor.getBlockState(mutableBlockPos).isAir();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_BOOST_TIME.equals(entityDataAccessor) && this.level.isClientSide) {
            this.steering.onSynced();
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_BOOST_TIME, 0);
        this.entityData.define(DATA_SUFFOCATING, false);
        this.entityData.define(DATA_SADDLE_ID, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        this.steering.addAdditionalSaveData(compoundTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.steering.readAdditionalSaveData(compoundTag);
    }

    @Override
    public boolean isSaddled() {
        return this.steering.hasSaddle();
    }

    @Override
    public boolean isSaddleable() {
        return this.isAlive() && !this.isBaby();
    }

    @Override
    public void equipSaddle(@Nullable SoundSource soundSource) {
        this.steering.setSaddle(true);
        if (soundSource != null) {
            this.level.playSound(null, this, SoundEvents.STRIDER_SADDLE, soundSource, 0.5f, 1.0f);
        }
    }

    @Override
    protected void registerGoals() {
        this.panicGoal = new PanicGoal(this, 1.65);
        this.goalSelector.addGoal(1, this.panicGoal);
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.temptGoal = new TemptGoal((PathfinderMob)this, 1.4, false, TEMPT_ITEMS);
        this.goalSelector.addGoal(3, this.temptGoal);
        this.goalSelector.addGoal(4, new StriderGoToLavaGoal(this, 1.5));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0, 60));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Strider.class, 8.0f));
    }

    public void setSuffocating(boolean bl) {
        this.entityData.set(DATA_SUFFOCATING, bl);
    }

    public boolean isSuffocating() {
        if (this.getVehicle() instanceof Strider) {
            return ((Strider)this.getVehicle()).isSuffocating();
        }
        return this.entityData.get(DATA_SUFFOCATING);
    }

    @Override
    public boolean canStandOnFluid(Fluid fluid) {
        return fluid.is(FluidTags.LAVA);
    }

    @Override
    public double getPassengersRidingOffset() {
        float f = Math.min(0.25f, this.animationSpeed);
        float f2 = this.animationPosition;
        return (double)this.getBbHeight() - 0.19 + (double)(0.12f * Mth.cos(f2 * 1.5f) * 2.0f * f);
    }

    @Override
    public boolean canBeControlledByRider() {
        Entity entity = this.getControllingPassenger();
        if (!(entity instanceof Player)) {
            return false;
        }
        Player player = (Player)entity;
        return player.getMainHandItem().getItem() == Items.WARPED_FUNGUS_ON_A_STICK || player.getOffhandItem().getItem() == Items.WARPED_FUNGUS_ON_A_STICK;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader levelReader) {
        return levelReader.isUnobstructed(this);
    }

    @Nullable
    @Override
    public Entity getControllingPassenger() {
        if (this.getPassengers().isEmpty()) {
            return null;
        }
        return this.getPassengers().get(0);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
        Vec3[] arrvec3 = new Vec3[]{Strider.getCollisionHorizontalEscapeVector(this.getBbWidth(), livingEntity.getBbWidth(), livingEntity.yRot), Strider.getCollisionHorizontalEscapeVector(this.getBbWidth(), livingEntity.getBbWidth(), livingEntity.yRot - 22.5f), Strider.getCollisionHorizontalEscapeVector(this.getBbWidth(), livingEntity.getBbWidth(), livingEntity.yRot + 22.5f), Strider.getCollisionHorizontalEscapeVector(this.getBbWidth(), livingEntity.getBbWidth(), livingEntity.yRot - 45.0f), Strider.getCollisionHorizontalEscapeVector(this.getBbWidth(), livingEntity.getBbWidth(), livingEntity.yRot + 45.0f)};
        LinkedHashSet linkedHashSet = Sets.newLinkedHashSet();
        double d = this.getBoundingBox().maxY;
        double d2 = this.getBoundingBox().minY - 0.5;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (Vec3 vec3 : arrvec3) {
            mutableBlockPos.set(this.getX() + vec3.x, d, this.getZ() + vec3.z);
            for (double d3 = d; d3 > d2; d3 -= 1.0) {
                linkedHashSet.add(mutableBlockPos.immutable());
                mutableBlockPos.move(Direction.DOWN);
            }
        }
        for (BlockPos blockPos : linkedHashSet) {
            double d4;
            if (this.level.getFluidState(blockPos).is(FluidTags.LAVA) || !DismountHelper.isBlockFloorValid(d4 = this.level.getBlockFloorHeight(blockPos))) continue;
            Vec3 vec3 = Vec3.upFromBottomCenterOf(blockPos, d4);
            for (Pose pose : livingEntity.getDismountPoses()) {
                AABB aABB = livingEntity.getLocalBoundsForPose(pose);
                if (!DismountHelper.canDismountTo(this.level, livingEntity, aABB.move(vec3))) continue;
                livingEntity.setPose(pose);
                return vec3;
            }
        }
        return new Vec3(this.getX(), this.getBoundingBox().maxY, this.getZ());
    }

    @Override
    public void travel(Vec3 vec3) {
        this.setSpeed(this.getMoveSpeed());
        this.travel(this, this.steering, vec3);
    }

    public float getMoveSpeed() {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (this.isSuffocating() ? 0.66f : 1.0f);
    }

    @Override
    public float getSteeringSpeed() {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (this.isSuffocating() ? 0.23f : 0.55f);
    }

    @Override
    public void travelWithInput(Vec3 vec3) {
        super.travel(vec3);
    }

    @Override
    protected float nextStep() {
        return this.moveDist + 0.6f;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(this.isInLava() ? SoundEvents.STRIDER_STEP_LAVA : SoundEvents.STRIDER_STEP, 1.0f, 1.0f);
    }

    @Override
    public boolean boost() {
        return this.steering.boost(this.getRandom());
    }

    @Override
    protected void checkFallDamage(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
        this.checkInsideBlocks();
        if (this.isInLava()) {
            this.fallDistance = 0.0f;
            return;
        }
        super.checkFallDamage(d, bl, blockState, blockPos);
    }

    @Override
    public void tick() {
        if (this.isBeingTempted() && this.random.nextInt(140) == 0) {
            this.playSound(SoundEvents.STRIDER_HAPPY, 1.0f, this.getVoicePitch());
        } else if (this.isPanicking() && this.random.nextInt(60) == 0) {
            this.playSound(SoundEvents.STRIDER_RETREAT, 1.0f, this.getVoicePitch());
        }
        BlockState blockState = this.level.getBlockState(this.blockPosition());
        BlockState blockState2 = this.getBlockStateOn();
        boolean bl = blockState.is(BlockTags.STRIDER_WARM_BLOCKS) || blockState2.is(BlockTags.STRIDER_WARM_BLOCKS) || this.getFluidHeight(FluidTags.LAVA) > 0.0;
        this.setSuffocating(!bl);
        super.tick();
        this.floatStrider();
        this.checkInsideBlocks();
    }

    private boolean isPanicking() {
        return this.panicGoal != null && this.panicGoal.isRunning();
    }

    private boolean isBeingTempted() {
        return this.temptGoal != null && this.temptGoal.isRunning();
    }

    @Override
    protected boolean shouldPassengersInheritMalus() {
        return true;
    }

    private void floatStrider() {
        if (this.isInLava()) {
            CollisionContext collisionContext = CollisionContext.of(this);
            if (!collisionContext.isAbove(LiquidBlock.STABLE_SHAPE, this.blockPosition(), true) || this.level.getFluidState(this.blockPosition().above()).is(FluidTags.LAVA)) {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5).add(0.0, 0.05, 0.0));
            } else {
                this.onGround = true;
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.17499999701976776).add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isPanicking() || this.isBeingTempted()) {
            return null;
        }
        return SoundEvents.STRIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.STRIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.STRIDER_DEATH;
    }

    @Override
    protected boolean canAddPassenger(Entity entity) {
        return this.getPassengers().isEmpty() && !this.isEyeInFluid(FluidTags.LAVA);
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new StriderPathNavigation(this, level);
    }

    @Override
    public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
        if (levelReader.getBlockState(blockPos).getFluidState().is(FluidTags.LAVA)) {
            return 10.0f;
        }
        return this.isInLava() ? Float.NEGATIVE_INFINITY : 0.0f;
    }

    @Override
    public Strider getBreedOffspring(ServerLevel serverLevel, AgableMob agableMob) {
        return EntityType.STRIDER.create(serverLevel);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return FOOD_ITEMS.test(itemStack);
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.isSaddled()) {
            this.spawnAtLocation(Items.SADDLE);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        boolean bl = this.isFood(player.getItemInHand(interactionHand));
        if (!bl && this.isSaddled() && !this.isVehicle() && !player.isSecondaryUseActive()) {
            if (!this.level.isClientSide) {
                player.startRiding(this);
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        InteractionResult interactionResult = super.mobInteract(player, interactionHand);
        if (!interactionResult.consumesAction()) {
            ItemStack itemStack = player.getItemInHand(interactionHand);
            if (itemStack.getItem() == Items.SADDLE) {
                return itemStack.interactLivingEntity(player, this, interactionHand);
            }
            return InteractionResult.PASS;
        }
        if (bl && !this.isSilent()) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.STRIDER_EAT, this.getSoundSource(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
        return interactionResult;
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.6f * this.getEyeHeight(), this.getBbWidth() * 0.4f);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        if (this.isBaby()) {
            return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
        }
        if (this.random.nextInt(30) == 0) {
            Mob mob = EntityType.ZOMBIFIED_PIGLIN.create(serverLevelAccessor.getLevel());
            spawnGroupData = this.spawnJockey(serverLevelAccessor, difficultyInstance, mob, new Zombie.ZombieGroupData(Zombie.getSpawnAsBabyOdds(this.random), false));
            mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.WARPED_FUNGUS_ON_A_STICK));
            this.equipSaddle(null);
        } else if (this.random.nextInt(10) == 0) {
            AgableMob agableMob = EntityType.STRIDER.create(serverLevelAccessor.getLevel());
            agableMob.setAge(-24000);
            spawnGroupData = this.spawnJockey(serverLevelAccessor, difficultyInstance, agableMob, null);
        } else {
            spawnGroupData = new AgableMob.AgableMobGroupData(0.5f);
        }
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    private SpawnGroupData spawnJockey(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, Mob mob, @Nullable SpawnGroupData spawnGroupData) {
        mob.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, 0.0f);
        mob.finalizeSpawn(serverLevelAccessor, difficultyInstance, MobSpawnType.JOCKEY, spawnGroupData, null);
        mob.startRiding(this, true);
        return new AgableMob.AgableMobGroupData(0.0f);
    }

    @Override
    public /* synthetic */ AgableMob getBreedOffspring(ServerLevel serverLevel, AgableMob agableMob) {
        return this.getBreedOffspring(serverLevel, agableMob);
    }

    static class StriderGoToLavaGoal
    extends MoveToBlockGoal {
        private final Strider strider;

        private StriderGoToLavaGoal(Strider strider, double d) {
            super(strider, d, 8, 2);
            this.strider = strider;
        }

        @Override
        public BlockPos getMoveToTarget() {
            return this.blockPos;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.strider.isInLava() && this.isValidTarget(this.strider.level, this.blockPos);
        }

        @Override
        public boolean canUse() {
            return !this.strider.isInLava() && super.canUse();
        }

        @Override
        public boolean shouldRecalculatePath() {
            return this.tryTicks % 20 == 0;
        }

        @Override
        protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
            return levelReader.getBlockState(blockPos).is(Blocks.LAVA) && levelReader.getBlockState(blockPos.above()).isPathfindable(levelReader, blockPos, PathComputationType.LAND);
        }
    }

    static class StriderPathNavigation
    extends GroundPathNavigation {
        StriderPathNavigation(Strider strider, Level level) {
            super(strider, level);
        }

        @Override
        protected PathFinder createPathFinder(int n) {
            this.nodeEvaluator = new WalkNodeEvaluator();
            return new PathFinder(this.nodeEvaluator, n);
        }

        @Override
        protected boolean hasValidPathType(BlockPathTypes blockPathTypes) {
            if (blockPathTypes == BlockPathTypes.LAVA || blockPathTypes == BlockPathTypes.DAMAGE_FIRE || blockPathTypes == BlockPathTypes.DANGER_FIRE) {
                return true;
            }
            return super.hasValidPathType(blockPathTypes);
        }

        @Override
        public boolean isStableDestination(BlockPos blockPos) {
            return this.level.getBlockState(blockPos).is(Blocks.LAVA) || super.isStableDestination(blockPos);
        }
    }

}

