/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class Drowned
extends Zombie
implements RangedAttackMob {
    private boolean searchingForLand;
    protected final WaterBoundPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;

    public Drowned(EntityType<? extends Drowned> entityType, Level level) {
        super(entityType, level);
        this.maxUpStep = 1.0f;
        this.moveControl = new DrownedMoveControl(this);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
        this.waterNavigation = new WaterBoundPathNavigation(this, level);
        this.groundNavigation = new GroundPathNavigation(this, level);
    }

    @Override
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(1, new DrownedGoToWaterGoal(this, 1.0));
        this.goalSelector.addGoal(2, new DrownedTridentAttackGoal(this, 1.0, 40, 10.0f));
        this.goalSelector.addGoal(2, new DrownedAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new DrownedGoToBeachGoal(this, 1.0));
        this.goalSelector.addGoal(6, new DrownedSwimUpGoal(this, 1.0, this.level.getSeaLevel()));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Drowned.class).setAlertOthers(ZombifiedPiglin.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>(this, Player.class, 10, true, false, this::okTarget));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<AbstractVillager>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<IronGolem>(this, IronGolem.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<Turtle>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        spawnGroupData = super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
        if (this.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty() && this.random.nextFloat() < 0.03f) {
            this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.NAUTILUS_SHELL));
            this.handDropChances[EquipmentSlot.OFFHAND.getIndex()] = 2.0f;
        }
        return spawnGroupData;
    }

    public static boolean checkDrownedSpawnRules(EntityType<Drowned> entityType, ServerLevelAccessor serverLevelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
        boolean bl;
        Optional<ResourceKey<Biome>> optional = serverLevelAccessor.getBiomeName(blockPos);
        boolean bl2 = bl = serverLevelAccessor.getDifficulty() != Difficulty.PEACEFUL && Drowned.isDarkEnoughToSpawn(serverLevelAccessor, blockPos, random) && (mobSpawnType == MobSpawnType.SPAWNER || serverLevelAccessor.getFluidState(blockPos).is(FluidTags.WATER));
        if (Objects.equals(optional, Optional.of(Biomes.RIVER)) || Objects.equals(optional, Optional.of(Biomes.FROZEN_RIVER))) {
            return random.nextInt(15) == 0 && bl;
        }
        return random.nextInt(40) == 0 && Drowned.isDeepEnoughToSpawn(serverLevelAccessor, blockPos) && bl;
    }

    private static boolean isDeepEnoughToSpawn(LevelAccessor levelAccessor, BlockPos blockPos) {
        return blockPos.getY() < levelAccessor.getSeaLevel() - 5;
    }

    @Override
    protected boolean supportsBreakDoorGoal() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isInWater()) {
            return SoundEvents.DROWNED_AMBIENT_WATER;
        }
        return SoundEvents.DROWNED_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        if (this.isInWater()) {
            return SoundEvents.DROWNED_HURT_WATER;
        }
        return SoundEvents.DROWNED_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        if (this.isInWater()) {
            return SoundEvents.DROWNED_DEATH_WATER;
        }
        return SoundEvents.DROWNED_DEATH;
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.DROWNED_STEP;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.DROWNED_SWIM;
    }

    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficultyInstance) {
        if ((double)this.random.nextFloat() > 0.9) {
            int n = this.random.nextInt(16);
            if (n < 10) {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
            } else {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
            }
        }
    }

    @Override
    protected boolean canReplaceCurrentItem(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack2.getItem() == Items.NAUTILUS_SHELL) {
            return false;
        }
        if (itemStack2.getItem() == Items.TRIDENT) {
            if (itemStack.getItem() == Items.TRIDENT) {
                return itemStack.getDamageValue() < itemStack2.getDamageValue();
            }
            return false;
        }
        if (itemStack.getItem() == Items.TRIDENT) {
            return true;
        }
        return super.canReplaceCurrentItem(itemStack, itemStack2);
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader levelReader) {
        return levelReader.isUnobstructed(this);
    }

    public boolean okTarget(@Nullable LivingEntity livingEntity) {
        if (livingEntity != null) {
            return !this.level.isDay() || livingEntity.isInWater();
        }
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return !this.isSwimming();
    }

    private boolean wantsToSwim() {
        if (this.searchingForLand) {
            return true;
        }
        LivingEntity livingEntity = this.getTarget();
        return livingEntity != null && livingEntity.isInWater();
    }

    @Override
    public void travel(Vec3 vec3) {
        if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
            this.moveRelative(0.01f, vec3);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else {
            super.travel(vec3);
        }
    }

    @Override
    public void updateSwimming() {
        if (!this.level.isClientSide) {
            if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
            }
        }
    }

    protected boolean closeToNextPos() {
        double d;
        BlockPos blockPos;
        Path path = this.getNavigation().getPath();
        return path != null && (blockPos = path.getTarget()) != null && (d = this.distanceToSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ())) < 4.0;
    }

    @Override
    public void performRangedAttack(LivingEntity livingEntity, float f) {
        ThrownTrident thrownTrident = new ThrownTrident(this.level, this, new ItemStack(Items.TRIDENT));
        double d = livingEntity.getX() - this.getX();
        double d2 = livingEntity.getY(0.3333333333333333) - thrownTrident.getY();
        double d3 = livingEntity.getZ() - this.getZ();
        double d4 = Mth.sqrt(d * d + d3 * d3);
        thrownTrident.shoot(d, d2 + d4 * 0.20000000298023224, d3, 1.6f, 14 - this.level.getDifficulty().getId() * 4);
        this.playSound(SoundEvents.DROWNED_SHOOT, 1.0f, 1.0f / (this.getRandom().nextFloat() * 0.4f + 0.8f));
        this.level.addFreshEntity(thrownTrident);
    }

    public void setSearchingForLand(boolean bl) {
        this.searchingForLand = bl;
    }

    static class DrownedMoveControl
    extends MoveControl {
        private final Drowned drowned;

        public DrownedMoveControl(Drowned drowned) {
            super(drowned);
            this.drowned = drowned;
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = this.drowned.getTarget();
            if (this.drowned.wantsToSwim() && this.drowned.isInWater()) {
                if (livingEntity != null && livingEntity.getY() > this.drowned.getY() || this.drowned.searchingForLand) {
                    this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add(0.0, 0.002, 0.0));
                }
                if (this.operation != MoveControl.Operation.MOVE_TO || this.drowned.getNavigation().isDone()) {
                    this.drowned.setSpeed(0.0f);
                    return;
                }
                double d = this.wantedX - this.drowned.getX();
                double d2 = this.wantedY - this.drowned.getY();
                double d3 = this.wantedZ - this.drowned.getZ();
                double d4 = Mth.sqrt(d * d + d2 * d2 + d3 * d3);
                float f = (float)(Mth.atan2(d3, d) * 57.2957763671875) - 90.0f;
                this.drowned.yBodyRot = this.drowned.yRot = this.rotlerp(this.drowned.yRot, f, 90.0f);
                float f2 = (float)(this.speedModifier * this.drowned.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float f3 = Mth.lerp(0.125f, this.drowned.getSpeed(), f2);
                this.drowned.setSpeed(f3);
                this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add((double)f3 * d * 0.005, (double)f3 * (d2 /= d4) * 0.1, (double)f3 * d3 * 0.005));
            } else {
                if (!this.drowned.onGround) {
                    this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add(0.0, -0.008, 0.0));
                }
                super.tick();
            }
        }
    }

    static class DrownedAttackGoal
    extends ZombieAttackGoal {
        private final Drowned drowned;

        public DrownedAttackGoal(Drowned drowned, double d, boolean bl) {
            super(drowned, d, bl);
            this.drowned = drowned;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && this.drowned.okTarget(this.drowned.getTarget());
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.drowned.okTarget(this.drowned.getTarget());
        }
    }

    static class DrownedGoToWaterGoal
    extends Goal {
        private final PathfinderMob mob;
        private double wantedX;
        private double wantedY;
        private double wantedZ;
        private final double speedModifier;
        private final Level level;

        public DrownedGoToWaterGoal(PathfinderMob pathfinderMob, double d) {
            this.mob = pathfinderMob;
            this.speedModifier = d;
            this.level = pathfinderMob.level;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!this.level.isDay()) {
                return false;
            }
            if (this.mob.isInWater()) {
                return false;
            }
            Vec3 vec3 = this.getWaterPos();
            if (vec3 == null) {
                return false;
            }
            this.wantedX = vec3.x;
            this.wantedY = vec3.y;
            this.wantedZ = vec3.z;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.mob.getNavigation().isDone();
        }

        @Override
        public void start() {
            this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
        }

        @Nullable
        private Vec3 getWaterPos() {
            Random random = this.mob.getRandom();
            BlockPos blockPos = this.mob.blockPosition();
            for (int i = 0; i < 10; ++i) {
                BlockPos blockPos2 = blockPos.offset(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
                if (!this.level.getBlockState(blockPos2).is(Blocks.WATER)) continue;
                return Vec3.atBottomCenterOf(blockPos2);
            }
            return null;
        }
    }

    static class DrownedGoToBeachGoal
    extends MoveToBlockGoal {
        private final Drowned drowned;

        public DrownedGoToBeachGoal(Drowned drowned, double d) {
            super(drowned, d, 8, 2);
            this.drowned = drowned;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !this.drowned.level.isDay() && this.drowned.isInWater() && this.drowned.getY() >= (double)(this.drowned.level.getSeaLevel() - 3);
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse();
        }

        @Override
        protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
            BlockPos blockPos2 = blockPos.above();
            if (!levelReader.isEmptyBlock(blockPos2) || !levelReader.isEmptyBlock(blockPos2.above())) {
                return false;
            }
            return levelReader.getBlockState(blockPos).entityCanStandOn(levelReader, blockPos, this.drowned);
        }

        @Override
        public void start() {
            this.drowned.setSearchingForLand(false);
            this.drowned.navigation = this.drowned.groundNavigation;
            super.start();
        }

        @Override
        public void stop() {
            super.stop();
        }
    }

    static class DrownedSwimUpGoal
    extends Goal {
        private final Drowned drowned;
        private final double speedModifier;
        private final int seaLevel;
        private boolean stuck;

        public DrownedSwimUpGoal(Drowned drowned, double d, int n) {
            this.drowned = drowned;
            this.speedModifier = d;
            this.seaLevel = n;
        }

        @Override
        public boolean canUse() {
            return !this.drowned.level.isDay() && this.drowned.isInWater() && this.drowned.getY() < (double)(this.seaLevel - 2);
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse() && !this.stuck;
        }

        @Override
        public void tick() {
            if (this.drowned.getY() < (double)(this.seaLevel - 1) && (this.drowned.getNavigation().isDone() || this.drowned.closeToNextPos())) {
                Vec3 vec3 = RandomPos.getPosTowards(this.drowned, 4, 8, new Vec3(this.drowned.getX(), this.seaLevel - 1, this.drowned.getZ()));
                if (vec3 == null) {
                    this.stuck = true;
                    return;
                }
                this.drowned.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, this.speedModifier);
            }
        }

        @Override
        public void start() {
            this.drowned.setSearchingForLand(true);
            this.stuck = false;
        }

        @Override
        public void stop() {
            this.drowned.setSearchingForLand(false);
        }
    }

    static class DrownedTridentAttackGoal
    extends RangedAttackGoal {
        private final Drowned drowned;

        public DrownedTridentAttackGoal(RangedAttackMob rangedAttackMob, double d, int n, float f) {
            super(rangedAttackMob, d, n, f);
            this.drowned = (Drowned)rangedAttackMob;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && this.drowned.getMainHandItem().getItem() == Items.TRIDENT;
        }

        @Override
        public void start() {
            super.start();
            this.drowned.setAggressive(true);
            this.drowned.startUsingItem(InteractionHand.MAIN_HAND);
        }

        @Override
        public void stop() {
            super.stop();
            this.drowned.stopUsingItem();
            this.drowned.setAggressive(false);
        }
    }

}

