/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.BredAnimalsTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.TurtleNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class Turtle
extends Animal {
    private static final EntityDataAccessor<BlockPos> HOME_POS = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LAYING_EGG = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<BlockPos> TRAVEL_POS = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Boolean> GOING_HOME = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TRAVELLING = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
    private int layEggCounter;
    public static final Predicate<LivingEntity> BABY_ON_LAND_SELECTOR = livingEntity -> livingEntity.isBaby() && !livingEntity.isInWater();

    public Turtle(EntityType<? extends Turtle> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
        this.moveControl = new TurtleMoveControl(this);
        this.maxUpStep = 1.0f;
    }

    public void setHomePos(BlockPos blockPos) {
        this.entityData.set(HOME_POS, blockPos);
    }

    private BlockPos getHomePos() {
        return this.entityData.get(HOME_POS);
    }

    private void setTravelPos(BlockPos blockPos) {
        this.entityData.set(TRAVEL_POS, blockPos);
    }

    private BlockPos getTravelPos() {
        return this.entityData.get(TRAVEL_POS);
    }

    public boolean hasEgg() {
        return this.entityData.get(HAS_EGG);
    }

    private void setHasEgg(boolean bl) {
        this.entityData.set(HAS_EGG, bl);
    }

    public boolean isLayingEgg() {
        return this.entityData.get(LAYING_EGG);
    }

    private void setLayingEgg(boolean bl) {
        this.layEggCounter = bl ? 1 : 0;
        this.entityData.set(LAYING_EGG, bl);
    }

    private boolean isGoingHome() {
        return this.entityData.get(GOING_HOME);
    }

    private void setGoingHome(boolean bl) {
        this.entityData.set(GOING_HOME, bl);
    }

    private boolean isTravelling() {
        return this.entityData.get(TRAVELLING);
    }

    private void setTravelling(boolean bl) {
        this.entityData.set(TRAVELLING, bl);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HOME_POS, BlockPos.ZERO);
        this.entityData.define(HAS_EGG, false);
        this.entityData.define(TRAVEL_POS, BlockPos.ZERO);
        this.entityData.define(GOING_HOME, false);
        this.entityData.define(TRAVELLING, false);
        this.entityData.define(LAYING_EGG, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("HomePosX", this.getHomePos().getX());
        compoundTag.putInt("HomePosY", this.getHomePos().getY());
        compoundTag.putInt("HomePosZ", this.getHomePos().getZ());
        compoundTag.putBoolean("HasEgg", this.hasEgg());
        compoundTag.putInt("TravelPosX", this.getTravelPos().getX());
        compoundTag.putInt("TravelPosY", this.getTravelPos().getY());
        compoundTag.putInt("TravelPosZ", this.getTravelPos().getZ());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        int n = compoundTag.getInt("HomePosX");
        int n2 = compoundTag.getInt("HomePosY");
        int n3 = compoundTag.getInt("HomePosZ");
        this.setHomePos(new BlockPos(n, n2, n3));
        super.readAdditionalSaveData(compoundTag);
        this.setHasEgg(compoundTag.getBoolean("HasEgg"));
        int n4 = compoundTag.getInt("TravelPosX");
        int n5 = compoundTag.getInt("TravelPosY");
        int n6 = compoundTag.getInt("TravelPosZ");
        this.setTravelPos(new BlockPos(n4, n5, n6));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        this.setHomePos(this.blockPosition());
        this.setTravelPos(BlockPos.ZERO);
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    public static boolean checkTurtleSpawnRules(EntityType<Turtle> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
        return blockPos.getY() < levelAccessor.getSeaLevel() + 4 && TurtleEggBlock.onSand(levelAccessor, blockPos) && levelAccessor.getRawBrightness(blockPos, 0) > 8;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new TurtlePanicGoal(this, 1.2));
        this.goalSelector.addGoal(1, new TurtleBreedGoal(this, 1.0));
        this.goalSelector.addGoal(1, new TurtleLayEggGoal(this, 1.0));
        this.goalSelector.addGoal(2, new TurtleTemptGoal(this, 1.1, Blocks.SEAGRASS.asItem()));
        this.goalSelector.addGoal(3, new TurtleGoToWaterGoal(this, 1.0));
        this.goalSelector.addGoal(4, new TurtleGoHomeGoal(this, 1.0));
        this.goalSelector.addGoal(7, new TurtleTravelGoal(this, 1.0));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(9, new TurtleRandomStrollGoal(this, 1.0, 100));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0).add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public MobType getMobType() {
        return MobType.WATER;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 200;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (!this.isInWater() && this.onGround && !this.isBaby()) {
            return SoundEvents.TURTLE_AMBIENT_LAND;
        }
        return super.getAmbientSound();
    }

    @Override
    protected void playSwimSound(float f) {
        super.playSwimSound(f * 1.5f);
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.TURTLE_SWIM;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        if (this.isBaby()) {
            return SoundEvents.TURTLE_HURT_BABY;
        }
        return SoundEvents.TURTLE_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        if (this.isBaby()) {
            return SoundEvents.TURTLE_DEATH_BABY;
        }
        return SoundEvents.TURTLE_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        SoundEvent soundEvent = this.isBaby() ? SoundEvents.TURTLE_SHAMBLE_BABY : SoundEvents.TURTLE_SHAMBLE;
        this.playSound(soundEvent, 0.15f, 1.0f);
    }

    @Override
    public boolean canFallInLove() {
        return super.canFallInLove() && !this.hasEgg();
    }

    @Override
    protected float nextStep() {
        return this.moveDist + 0.15f;
    }

    @Override
    public float getScale() {
        return this.isBaby() ? 0.3f : 1.0f;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new TurtlePathNavigation(this, level);
    }

    @Nullable
    @Override
    public AgableMob getBreedOffspring(ServerLevel serverLevel, AgableMob agableMob) {
        return EntityType.TURTLE.create(serverLevel);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.getItem() == Blocks.SEAGRASS.asItem();
    }

    @Override
    public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
        if (!this.isGoingHome() && levelReader.getFluidState(blockPos).is(FluidTags.WATER)) {
            return 10.0f;
        }
        if (TurtleEggBlock.onSand(levelReader, blockPos)) {
            return 10.0f;
        }
        return levelReader.getBrightness(blockPos) - 0.5f;
    }

    @Override
    public void aiStep() {
        BlockPos blockPos;
        super.aiStep();
        if (this.isAlive() && this.isLayingEgg() && this.layEggCounter >= 1 && this.layEggCounter % 5 == 0 && TurtleEggBlock.onSand(this.level, blockPos = this.blockPosition())) {
            this.level.levelEvent(2001, blockPos, Block.getId(Blocks.SAND.defaultBlockState()));
        }
    }

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        if (!this.isBaby() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.spawnAtLocation(Items.SCUTE, 1);
        }
    }

    @Override
    public void travel(Vec3 vec3) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(0.1f, vec3);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
            if (!(this.getTarget() != null || this.isGoingHome() && this.getHomePos().closerThan(this.position(), 20.0))) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
            }
        } else {
            super.travel(vec3);
        }
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return false;
    }

    @Override
    public void thunderHit(ServerLevel serverLevel, LightningBolt lightningBolt) {
        this.hurt(DamageSource.LIGHTNING_BOLT, Float.MAX_VALUE);
    }

    static class TurtlePathNavigation
    extends WaterBoundPathNavigation {
        TurtlePathNavigation(Turtle turtle, Level level) {
            super(turtle, level);
        }

        @Override
        protected boolean canUpdatePath() {
            return true;
        }

        @Override
        protected PathFinder createPathFinder(int n) {
            this.nodeEvaluator = new TurtleNodeEvaluator();
            return new PathFinder(this.nodeEvaluator, n);
        }

        @Override
        public boolean isStableDestination(BlockPos blockPos) {
            Turtle turtle;
            if (this.mob instanceof Turtle && (turtle = (Turtle)this.mob).isTravelling()) {
                return this.level.getBlockState(blockPos).is(Blocks.WATER);
            }
            return !this.level.getBlockState(blockPos.below()).isAir();
        }
    }

    static class TurtleMoveControl
    extends MoveControl {
        private final Turtle turtle;

        TurtleMoveControl(Turtle turtle) {
            super(turtle);
            this.turtle = turtle;
        }

        private void updateSpeed() {
            if (this.turtle.isInWater()) {
                this.turtle.setDeltaMovement(this.turtle.getDeltaMovement().add(0.0, 0.005, 0.0));
                if (!this.turtle.getHomePos().closerThan(this.turtle.position(), 16.0)) {
                    this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 2.0f, 0.08f));
                }
                if (this.turtle.isBaby()) {
                    this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 3.0f, 0.06f));
                }
            } else if (this.turtle.onGround) {
                this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 2.0f, 0.06f));
            }
        }

        @Override
        public void tick() {
            this.updateSpeed();
            if (this.operation != MoveControl.Operation.MOVE_TO || this.turtle.getNavigation().isDone()) {
                this.turtle.setSpeed(0.0f);
                return;
            }
            double d = this.wantedX - this.turtle.getX();
            double d2 = this.wantedY - this.turtle.getY();
            double d3 = this.wantedZ - this.turtle.getZ();
            double d4 = Mth.sqrt(d * d + d2 * d2 + d3 * d3);
            float f = (float)(Mth.atan2(d3, d) * 57.2957763671875) - 90.0f;
            this.turtle.yBodyRot = this.turtle.yRot = this.rotlerp(this.turtle.yRot, f, 90.0f);
            float f2 = (float)(this.speedModifier * this.turtle.getAttributeValue(Attributes.MOVEMENT_SPEED));
            this.turtle.setSpeed(Mth.lerp(0.125f, this.turtle.getSpeed(), f2));
            this.turtle.setDeltaMovement(this.turtle.getDeltaMovement().add(0.0, (double)this.turtle.getSpeed() * (d2 /= d4) * 0.1, 0.0));
        }
    }

    static class TurtleGoToWaterGoal
    extends MoveToBlockGoal {
        private final Turtle turtle;

        private TurtleGoToWaterGoal(Turtle turtle, double d) {
            super(turtle, turtle.isBaby() ? 2.0 : d, 24);
            this.turtle = turtle;
            this.verticalSearchStart = -1;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.turtle.isInWater() && this.tryTicks <= 1200 && this.isValidTarget(this.turtle.level, this.blockPos);
        }

        @Override
        public boolean canUse() {
            if (this.turtle.isBaby() && !this.turtle.isInWater()) {
                return super.canUse();
            }
            if (!(this.turtle.isGoingHome() || this.turtle.isInWater() || this.turtle.hasEgg())) {
                return super.canUse();
            }
            return false;
        }

        @Override
        public boolean shouldRecalculatePath() {
            return this.tryTicks % 160 == 0;
        }

        @Override
        protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
            return levelReader.getBlockState(blockPos).is(Blocks.WATER);
        }
    }

    static class TurtleRandomStrollGoal
    extends RandomStrollGoal {
        private final Turtle turtle;

        private TurtleRandomStrollGoal(Turtle turtle, double d, int n) {
            super(turtle, d, n);
            this.turtle = turtle;
        }

        @Override
        public boolean canUse() {
            if (!(this.mob.isInWater() || this.turtle.isGoingHome() || this.turtle.hasEgg())) {
                return super.canUse();
            }
            return false;
        }
    }

    static class TurtleLayEggGoal
    extends MoveToBlockGoal {
        private final Turtle turtle;

        TurtleLayEggGoal(Turtle turtle, double d) {
            super(turtle, d, 16);
            this.turtle = turtle;
        }

        @Override
        public boolean canUse() {
            if (this.turtle.hasEgg() && this.turtle.getHomePos().closerThan(this.turtle.position(), 9.0)) {
                return super.canUse();
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.turtle.hasEgg() && this.turtle.getHomePos().closerThan(this.turtle.position(), 9.0);
        }

        @Override
        public void tick() {
            super.tick();
            BlockPos blockPos = this.turtle.blockPosition();
            if (!this.turtle.isInWater() && this.isReachedTarget()) {
                if (this.turtle.layEggCounter < 1) {
                    this.turtle.setLayingEgg(true);
                } else if (this.turtle.layEggCounter > 200) {
                    Level level = this.turtle.level;
                    level.playSound(null, blockPos, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3f, 0.9f + level.random.nextFloat() * 0.2f);
                    level.setBlock(this.blockPos.above(), (BlockState)Blocks.TURTLE_EGG.defaultBlockState().setValue(TurtleEggBlock.EGGS, this.turtle.random.nextInt(4) + 1), 3);
                    this.turtle.setHasEgg(false);
                    this.turtle.setLayingEgg(false);
                    this.turtle.setInLoveTime(600);
                }
                if (this.turtle.isLayingEgg()) {
                    this.turtle.layEggCounter++;
                }
            }
        }

        @Override
        protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
            if (!levelReader.isEmptyBlock(blockPos.above())) {
                return false;
            }
            return TurtleEggBlock.isSand(levelReader, blockPos);
        }
    }

    static class TurtleBreedGoal
    extends BreedGoal {
        private final Turtle turtle;

        TurtleBreedGoal(Turtle turtle, double d) {
            super(turtle, d);
            this.turtle = turtle;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !this.turtle.hasEgg();
        }

        @Override
        protected void breed() {
            ServerPlayer serverPlayer = this.animal.getLoveCause();
            if (serverPlayer == null && this.partner.getLoveCause() != null) {
                serverPlayer = this.partner.getLoveCause();
            }
            if (serverPlayer != null) {
                serverPlayer.awardStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverPlayer, this.animal, this.partner, null);
            }
            this.turtle.setHasEgg(true);
            this.animal.resetLove();
            this.partner.resetLove();
            Random random = this.animal.getRandom();
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), random.nextInt(7) + 1));
            }
        }
    }

    static class TurtleTemptGoal
    extends Goal {
        private static final TargetingConditions TEMPT_TARGETING = new TargetingConditions().range(10.0).allowSameTeam().allowInvulnerable();
        private final Turtle turtle;
        private final double speedModifier;
        private Player player;
        private int calmDown;
        private final Set<Item> items;

        TurtleTemptGoal(Turtle turtle, double d, Item item) {
            this.turtle = turtle;
            this.speedModifier = d;
            this.items = Sets.newHashSet((Object[])new Item[]{item});
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.calmDown > 0) {
                --this.calmDown;
                return false;
            }
            this.player = this.turtle.level.getNearestPlayer(TEMPT_TARGETING, this.turtle);
            if (this.player == null) {
                return false;
            }
            return this.shouldFollowItem(this.player.getMainHandItem()) || this.shouldFollowItem(this.player.getOffhandItem());
        }

        private boolean shouldFollowItem(ItemStack itemStack) {
            return this.items.contains(itemStack.getItem());
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void stop() {
            this.player = null;
            this.turtle.getNavigation().stop();
            this.calmDown = 100;
        }

        @Override
        public void tick() {
            this.turtle.getLookControl().setLookAt(this.player, this.turtle.getMaxHeadYRot() + 20, this.turtle.getMaxHeadXRot());
            if (this.turtle.distanceToSqr(this.player) < 6.25) {
                this.turtle.getNavigation().stop();
            } else {
                this.turtle.getNavigation().moveTo(this.player, this.speedModifier);
            }
        }
    }

    static class TurtleGoHomeGoal
    extends Goal {
        private final Turtle turtle;
        private final double speedModifier;
        private boolean stuck;
        private int closeToHomeTryTicks;

        TurtleGoHomeGoal(Turtle turtle, double d) {
            this.turtle = turtle;
            this.speedModifier = d;
        }

        @Override
        public boolean canUse() {
            if (this.turtle.isBaby()) {
                return false;
            }
            if (this.turtle.hasEgg()) {
                return true;
            }
            if (this.turtle.getRandom().nextInt(700) != 0) {
                return false;
            }
            return !this.turtle.getHomePos().closerThan(this.turtle.position(), 64.0);
        }

        @Override
        public void start() {
            this.turtle.setGoingHome(true);
            this.stuck = false;
            this.closeToHomeTryTicks = 0;
        }

        @Override
        public void stop() {
            this.turtle.setGoingHome(false);
        }

        @Override
        public boolean canContinueToUse() {
            return !this.turtle.getHomePos().closerThan(this.turtle.position(), 7.0) && !this.stuck && this.closeToHomeTryTicks <= 600;
        }

        @Override
        public void tick() {
            BlockPos blockPos = this.turtle.getHomePos();
            boolean bl = blockPos.closerThan(this.turtle.position(), 16.0);
            if (bl) {
                ++this.closeToHomeTryTicks;
            }
            if (this.turtle.getNavigation().isDone()) {
                Vec3 vec3 = Vec3.atBottomCenterOf(blockPos);
                Vec3 vec32 = RandomPos.getPosTowards(this.turtle, 16, 3, vec3, 0.3141592741012573);
                if (vec32 == null) {
                    vec32 = RandomPos.getPosTowards(this.turtle, 8, 7, vec3);
                }
                if (vec32 != null && !bl && !this.turtle.level.getBlockState(new BlockPos(vec32)).is(Blocks.WATER)) {
                    vec32 = RandomPos.getPosTowards(this.turtle, 16, 5, vec3);
                }
                if (vec32 == null) {
                    this.stuck = true;
                    return;
                }
                this.turtle.getNavigation().moveTo(vec32.x, vec32.y, vec32.z, this.speedModifier);
            }
        }
    }

    static class TurtleTravelGoal
    extends Goal {
        private final Turtle turtle;
        private final double speedModifier;
        private boolean stuck;

        TurtleTravelGoal(Turtle turtle, double d) {
            this.turtle = turtle;
            this.speedModifier = d;
        }

        @Override
        public boolean canUse() {
            return !this.turtle.isGoingHome() && !this.turtle.hasEgg() && this.turtle.isInWater();
        }

        @Override
        public void start() {
            int n = 512;
            int n2 = 4;
            Random random = this.turtle.random;
            int n3 = random.nextInt(1025) - 512;
            int n4 = random.nextInt(9) - 4;
            int n5 = random.nextInt(1025) - 512;
            if ((double)n4 + this.turtle.getY() > (double)(this.turtle.level.getSeaLevel() - 1)) {
                n4 = 0;
            }
            BlockPos blockPos = new BlockPos((double)n3 + this.turtle.getX(), (double)n4 + this.turtle.getY(), (double)n5 + this.turtle.getZ());
            this.turtle.setTravelPos(blockPos);
            this.turtle.setTravelling(true);
            this.stuck = false;
        }

        @Override
        public void tick() {
            if (this.turtle.getNavigation().isDone()) {
                Vec3 vec3 = Vec3.atBottomCenterOf(this.turtle.getTravelPos());
                Vec3 vec32 = RandomPos.getPosTowards(this.turtle, 16, 3, vec3, 0.3141592741012573);
                if (vec32 == null) {
                    vec32 = RandomPos.getPosTowards(this.turtle, 8, 7, vec3);
                }
                if (vec32 != null) {
                    int n = Mth.floor(vec32.x);
                    int n2 = Mth.floor(vec32.z);
                    int n3 = 34;
                    if (!this.turtle.level.hasChunksAt(n - 34, 0, n2 - 34, n + 34, 0, n2 + 34)) {
                        vec32 = null;
                    }
                }
                if (vec32 == null) {
                    this.stuck = true;
                    return;
                }
                this.turtle.getNavigation().moveTo(vec32.x, vec32.y, vec32.z, this.speedModifier);
            }
        }

        @Override
        public boolean canContinueToUse() {
            return !this.turtle.getNavigation().isDone() && !this.stuck && !this.turtle.isGoingHome() && !this.turtle.isInLove() && !this.turtle.hasEgg();
        }

        @Override
        public void stop() {
            this.turtle.setTravelling(false);
            super.stop();
        }
    }

    static class TurtlePanicGoal
    extends PanicGoal {
        TurtlePanicGoal(Turtle turtle, double d) {
            super(turtle, d);
        }

        @Override
        public boolean canUse() {
            if (this.mob.getLastHurtByMob() == null && !this.mob.isOnFire()) {
                return false;
            }
            BlockPos blockPos = this.lookForWater(this.mob.level, this.mob, 7, 4);
            if (blockPos != null) {
                this.posX = blockPos.getX();
                this.posY = blockPos.getY();
                this.posZ = blockPos.getZ();
                return true;
            }
            return this.findRandomPosition();
        }
    }

}

