/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.DolphinLookControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.DolphinJumpGoal;
import net.minecraft.world.entity.ai.goal.FollowBoatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ShipwreckConfiguration;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Dolphin
extends WaterAnimal {
    private static final EntityDataAccessor<BlockPos> TREASURE_POS = SynchedEntityData.defineId(Dolphin.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Boolean> GOT_FISH = SynchedEntityData.defineId(Dolphin.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> MOISTNESS_LEVEL = SynchedEntityData.defineId(Dolphin.class, EntityDataSerializers.INT);
    private static final TargetingConditions SWIM_WITH_PLAYER_TARGETING = new TargetingConditions().range(10.0).allowSameTeam().allowInvulnerable().allowUnseeable();
    public static final Predicate<ItemEntity> ALLOWED_ITEMS = itemEntity -> !itemEntity.hasPickUpDelay() && itemEntity.isAlive() && itemEntity.isInWater();

    public Dolphin(EntityType<? extends Dolphin> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new DolphinMoveControl(this);
        this.lookControl = new DolphinLookControl(this, 10);
        this.setCanPickUpLoot(true);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        this.setAirSupply(this.getMaxAirSupply());
        this.xRot = 0.0f;
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return false;
    }

    @Override
    protected void handleAirSupply(int n) {
    }

    public void setTreasurePos(BlockPos blockPos) {
        this.entityData.set(TREASURE_POS, blockPos);
    }

    public BlockPos getTreasurePos() {
        return this.entityData.get(TREASURE_POS);
    }

    public boolean gotFish() {
        return this.entityData.get(GOT_FISH);
    }

    public void setGotFish(boolean bl) {
        this.entityData.set(GOT_FISH, bl);
    }

    public int getMoistnessLevel() {
        return this.entityData.get(MOISTNESS_LEVEL);
    }

    public void setMoisntessLevel(int n) {
        this.entityData.set(MOISTNESS_LEVEL, n);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TREASURE_POS, BlockPos.ZERO);
        this.entityData.define(GOT_FISH, false);
        this.entityData.define(MOISTNESS_LEVEL, 2400);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("TreasurePosX", this.getTreasurePos().getX());
        compoundTag.putInt("TreasurePosY", this.getTreasurePos().getY());
        compoundTag.putInt("TreasurePosZ", this.getTreasurePos().getZ());
        compoundTag.putBoolean("GotFish", this.gotFish());
        compoundTag.putInt("Moistness", this.getMoistnessLevel());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        int n = compoundTag.getInt("TreasurePosX");
        int n2 = compoundTag.getInt("TreasurePosY");
        int n3 = compoundTag.getInt("TreasurePosZ");
        this.setTreasurePos(new BlockPos(n, n2, n3));
        super.readAdditionalSaveData(compoundTag);
        this.setGotFish(compoundTag.getBoolean("GotFish"));
        this.setMoisntessLevel(compoundTag.getInt("Moistness"));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BreathAirGoal(this));
        this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(1, new DolphinSwimToTreasureGoal(this));
        this.goalSelector.addGoal(2, new DolphinSwimWithPlayerGoal(this, 4.0));
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0, 10));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(5, new DolphinJumpGoal(this, 10));
        this.goalSelector.addGoal(6, new MeleeAttackGoal(this, 1.2000000476837158, true));
        this.goalSelector.addGoal(8, new PlayWithItemsGoal());
        this.goalSelector.addGoal(8, new FollowBoatGoal(this));
        this.goalSelector.addGoal(9, new AvoidEntityGoal<Guardian>(this, Guardian.class, 8.0f, 1.0, 1.0));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Guardian.class).setAlertOthers(new Class[0]));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 1.2000000476837158).add(Attributes.ATTACK_DAMAGE, 3.0);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean bl = entity.hurt(DamageSource.mobAttack(this), (int)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
        if (bl) {
            this.doEnchantDamageEffects(this, entity);
            this.playSound(SoundEvents.DOLPHIN_ATTACK, 1.0f, 1.0f);
        }
        return bl;
    }

    @Override
    public int getMaxAirSupply() {
        return 4800;
    }

    @Override
    protected int increaseAirSupply(int n) {
        return this.getMaxAirSupply();
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return 0.3f;
    }

    @Override
    public int getMaxHeadXRot() {
        return 1;
    }

    @Override
    public int getMaxHeadYRot() {
        return 1;
    }

    @Override
    protected boolean canRide(Entity entity) {
        return true;
    }

    @Override
    public boolean canTakeItem(ItemStack itemStack) {
        EquipmentSlot equipmentSlot = Mob.getEquipmentSlotForItem(itemStack);
        if (!this.getItemBySlot(equipmentSlot).isEmpty()) {
            return false;
        }
        return equipmentSlot == EquipmentSlot.MAINHAND && super.canTakeItem(itemStack);
    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        ItemStack itemStack;
        if (this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() && this.canHoldItem(itemStack = itemEntity.getItem())) {
            this.onItemPickup(itemEntity);
            this.setItemSlot(EquipmentSlot.MAINHAND, itemStack);
            this.handDropChances[EquipmentSlot.MAINHAND.getIndex()] = 2.0f;
            this.take(itemEntity, itemStack.getCount());
            itemEntity.remove();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isNoAi()) {
            this.setAirSupply(this.getMaxAirSupply());
            return;
        }
        if (this.isInWaterRainOrBubble()) {
            this.setMoisntessLevel(2400);
        } else {
            this.setMoisntessLevel(this.getMoistnessLevel() - 1);
            if (this.getMoistnessLevel() <= 0) {
                this.hurt(DamageSource.DRY_OUT, 1.0f);
            }
            if (this.onGround) {
                this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0f - 1.0f) * 0.2f, 0.5, (this.random.nextFloat() * 2.0f - 1.0f) * 0.2f));
                this.yRot = this.random.nextFloat() * 360.0f;
                this.onGround = false;
                this.hasImpulse = true;
            }
        }
        if (this.level.isClientSide && this.isInWater() && this.getDeltaMovement().lengthSqr() > 0.03) {
            Vec3 vec3 = this.getViewVector(0.0f);
            float f = Mth.cos(this.yRot * 0.017453292f) * 0.3f;
            float f2 = Mth.sin(this.yRot * 0.017453292f) * 0.3f;
            float f3 = 1.2f - this.random.nextFloat() * 0.7f;
            for (int i = 0; i < 2; ++i) {
                this.level.addParticle(ParticleTypes.DOLPHIN, this.getX() - vec3.x * (double)f3 + (double)f, this.getY() - vec3.y, this.getZ() - vec3.z * (double)f3 + (double)f2, 0.0, 0.0, 0.0);
                this.level.addParticle(ParticleTypes.DOLPHIN, this.getX() - vec3.x * (double)f3 - (double)f, this.getY() - vec3.y, this.getZ() - vec3.z * (double)f3 - (double)f2, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public void handleEntityEvent(byte by) {
        if (by == 38) {
            this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
        } else {
            super.handleEntityEvent(by);
        }
    }

    private void addParticlesAroundSelf(ParticleOptions particleOptions) {
        for (int i = 0; i < 7; ++i) {
            double d = this.random.nextGaussian() * 0.01;
            double d2 = this.random.nextGaussian() * 0.01;
            double d3 = this.random.nextGaussian() * 0.01;
            this.level.addParticle(particleOptions, this.getRandomX(1.0), this.getRandomY() + 0.2, this.getRandomZ(1.0), d, d2, d3);
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (!itemStack.isEmpty() && itemStack.getItem().is(ItemTags.FISHES)) {
            if (!this.level.isClientSide) {
                this.playSound(SoundEvents.DOLPHIN_EAT, 1.0f, 1.0f);
            }
            this.setGotFish(true);
            if (!player.abilities.instabuild) {
                itemStack.shrink(1);
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        return super.mobInteract(player, interactionHand);
    }

    public static boolean checkDolphinSpawnRules(EntityType<Dolphin> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
        if (blockPos.getY() <= 45 || blockPos.getY() >= levelAccessor.getSeaLevel()) {
            return false;
        }
        Optional<ResourceKey<Biome>> optional = levelAccessor.getBiomeName(blockPos);
        return (!Objects.equals(optional, Optional.of(Biomes.OCEAN)) || !Objects.equals(optional, Optional.of(Biomes.DEEP_OCEAN))) && levelAccessor.getFluidState(blockPos).is(FluidTags.WATER);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.DOLPHIN_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.DOLPHIN_DEATH;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isInWater() ? SoundEvents.DOLPHIN_AMBIENT_WATER : SoundEvents.DOLPHIN_AMBIENT;
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.DOLPHIN_SPLASH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.DOLPHIN_SWIM;
    }

    protected boolean closeToNextPos() {
        BlockPos blockPos = this.getNavigation().getTargetPos();
        if (blockPos != null) {
            return blockPos.closerThan(this.position(), 12.0);
        }
        return false;
    }

    @Override
    public void travel(Vec3 vec3) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), vec3);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
            }
        } else {
            super.travel(vec3);
        }
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return true;
    }

    static class DolphinSwimToTreasureGoal
    extends Goal {
        private final Dolphin dolphin;
        private boolean stuck;

        DolphinSwimToTreasureGoal(Dolphin dolphin) {
            this.dolphin = dolphin;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Override
        public boolean canUse() {
            return this.dolphin.gotFish() && this.dolphin.getAirSupply() >= 100;
        }

        @Override
        public boolean canContinueToUse() {
            BlockPos blockPos = this.dolphin.getTreasurePos();
            return !new BlockPos((double)blockPos.getX(), this.dolphin.getY(), (double)blockPos.getZ()).closerThan(this.dolphin.position(), 4.0) && !this.stuck && this.dolphin.getAirSupply() >= 100;
        }

        /*
         * Enabled aggressive block sorting
         */
        @Override
        public void start() {
            if (!(this.dolphin.level instanceof ServerLevel)) {
                return;
            }
            ServerLevel serverLevel = (ServerLevel)this.dolphin.level;
            this.stuck = false;
            this.dolphin.getNavigation().stop();
            BlockPos blockPos = this.dolphin.blockPosition();
            StructureFeature<FeatureConfiguration> structureFeature = (double)serverLevel.random.nextFloat() >= 0.5 ? StructureFeature.OCEAN_RUIN : StructureFeature.SHIPWRECK;
            BlockPos blockPos2 = serverLevel.findNearestMapFeature(structureFeature, blockPos, 50, false);
            if (blockPos2 == null) {
                StructureFeature<FeatureConfiguration> structureFeature2 = structureFeature.equals(StructureFeature.OCEAN_RUIN) ? StructureFeature.SHIPWRECK : StructureFeature.OCEAN_RUIN;
                BlockPos blockPos3 = serverLevel.findNearestMapFeature(structureFeature2, blockPos, 50, false);
                if (blockPos3 == null) {
                    this.stuck = true;
                    return;
                }
                this.dolphin.setTreasurePos(blockPos3);
            } else {
                this.dolphin.setTreasurePos(blockPos2);
            }
            serverLevel.broadcastEntityEvent(this.dolphin, (byte)38);
        }

        @Override
        public void stop() {
            BlockPos blockPos = this.dolphin.getTreasurePos();
            if (new BlockPos((double)blockPos.getX(), this.dolphin.getY(), (double)blockPos.getZ()).closerThan(this.dolphin.position(), 4.0) || this.stuck) {
                this.dolphin.setGotFish(false);
            }
        }

        @Override
        public void tick() {
            Level level = this.dolphin.level;
            if (this.dolphin.closeToNextPos() || this.dolphin.getNavigation().isDone()) {
                BlockPos blockPos;
                Vec3 vec3 = Vec3.atCenterOf(this.dolphin.getTreasurePos());
                Vec3 vec32 = RandomPos.getPosTowards(this.dolphin, 16, 1, vec3, 0.39269909262657166);
                if (vec32 == null) {
                    vec32 = RandomPos.getPosTowards(this.dolphin, 8, 4, vec3);
                }
                if (!(vec32 == null || level.getFluidState(blockPos = new BlockPos(vec32)).is(FluidTags.WATER) && level.getBlockState(blockPos).isPathfindable(level, blockPos, PathComputationType.WATER))) {
                    vec32 = RandomPos.getPosTowards(this.dolphin, 8, 5, vec3);
                }
                if (vec32 == null) {
                    this.stuck = true;
                    return;
                }
                this.dolphin.getLookControl().setLookAt(vec32.x, vec32.y, vec32.z, this.dolphin.getMaxHeadYRot() + 20, this.dolphin.getMaxHeadXRot());
                this.dolphin.getNavigation().moveTo(vec32.x, vec32.y, vec32.z, 1.3);
                if (level.random.nextInt(80) == 0) {
                    level.broadcastEntityEvent(this.dolphin, (byte)38);
                }
            }
        }
    }

    static class DolphinSwimWithPlayerGoal
    extends Goal {
        private final Dolphin dolphin;
        private final double speedModifier;
        private Player player;

        DolphinSwimWithPlayerGoal(Dolphin dolphin, double d) {
            this.dolphin = dolphin;
            this.speedModifier = d;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            this.player = this.dolphin.level.getNearestPlayer(SWIM_WITH_PLAYER_TARGETING, this.dolphin);
            if (this.player == null) {
                return false;
            }
            return this.player.isSwimming() && this.dolphin.getTarget() != this.player;
        }

        @Override
        public boolean canContinueToUse() {
            return this.player != null && this.player.isSwimming() && this.dolphin.distanceToSqr(this.player) < 256.0;
        }

        @Override
        public void start() {
            this.player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 100));
        }

        @Override
        public void stop() {
            this.player = null;
            this.dolphin.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.dolphin.getLookControl().setLookAt(this.player, this.dolphin.getMaxHeadYRot() + 20, this.dolphin.getMaxHeadXRot());
            if (this.dolphin.distanceToSqr(this.player) < 6.25) {
                this.dolphin.getNavigation().stop();
            } else {
                this.dolphin.getNavigation().moveTo(this.player, this.speedModifier);
            }
            if (this.player.isSwimming() && this.player.level.random.nextInt(6) == 0) {
                this.player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 100));
            }
        }
    }

    class PlayWithItemsGoal
    extends Goal {
        private int cooldown;

        private PlayWithItemsGoal() {
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > Dolphin.this.tickCount) {
                return false;
            }
            List<ItemEntity> list = Dolphin.this.level.getEntitiesOfClass(ItemEntity.class, Dolphin.this.getBoundingBox().inflate(8.0, 8.0, 8.0), ALLOWED_ITEMS);
            return !list.isEmpty() || !Dolphin.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
        }

        @Override
        public void start() {
            List<ItemEntity> list = Dolphin.this.level.getEntitiesOfClass(ItemEntity.class, Dolphin.this.getBoundingBox().inflate(8.0, 8.0, 8.0), ALLOWED_ITEMS);
            if (!list.isEmpty()) {
                Dolphin.this.getNavigation().moveTo(list.get(0), 1.2000000476837158);
                Dolphin.this.playSound(SoundEvents.DOLPHIN_PLAY, 1.0f, 1.0f);
            }
            this.cooldown = 0;
        }

        @Override
        public void stop() {
            ItemStack itemStack = Dolphin.this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (!itemStack.isEmpty()) {
                this.drop(itemStack);
                Dolphin.this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                this.cooldown = Dolphin.this.tickCount + Dolphin.this.random.nextInt(100);
            }
        }

        @Override
        public void tick() {
            List<ItemEntity> list = Dolphin.this.level.getEntitiesOfClass(ItemEntity.class, Dolphin.this.getBoundingBox().inflate(8.0, 8.0, 8.0), ALLOWED_ITEMS);
            ItemStack itemStack = Dolphin.this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (!itemStack.isEmpty()) {
                this.drop(itemStack);
                Dolphin.this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            } else if (!list.isEmpty()) {
                Dolphin.this.getNavigation().moveTo(list.get(0), 1.2000000476837158);
            }
        }

        private void drop(ItemStack itemStack) {
            if (itemStack.isEmpty()) {
                return;
            }
            double d = Dolphin.this.getEyeY() - 0.30000001192092896;
            ItemEntity itemEntity = new ItemEntity(Dolphin.this.level, Dolphin.this.getX(), d, Dolphin.this.getZ(), itemStack);
            itemEntity.setPickUpDelay(40);
            itemEntity.setThrower(Dolphin.this.getUUID());
            float f = 0.3f;
            float f2 = Dolphin.this.random.nextFloat() * 6.2831855f;
            float f3 = 0.02f * Dolphin.this.random.nextFloat();
            itemEntity.setDeltaMovement(0.3f * -Mth.sin(Dolphin.this.yRot * 0.017453292f) * Mth.cos(Dolphin.this.xRot * 0.017453292f) + Mth.cos(f2) * f3, 0.3f * Mth.sin(Dolphin.this.xRot * 0.017453292f) * 1.5f, 0.3f * Mth.cos(Dolphin.this.yRot * 0.017453292f) * Mth.cos(Dolphin.this.xRot * 0.017453292f) + Mth.sin(f2) * f3);
            Dolphin.this.level.addFreshEntity(itemEntity);
        }
    }

    static class DolphinMoveControl
    extends MoveControl {
        private final Dolphin dolphin;

        public DolphinMoveControl(Dolphin dolphin) {
            super(dolphin);
            this.dolphin = dolphin;
        }

        @Override
        public void tick() {
            double d;
            double d2;
            if (this.dolphin.isInWater()) {
                this.dolphin.setDeltaMovement(this.dolphin.getDeltaMovement().add(0.0, 0.005, 0.0));
            }
            if (this.operation != MoveControl.Operation.MOVE_TO || this.dolphin.getNavigation().isDone()) {
                this.dolphin.setSpeed(0.0f);
                this.dolphin.setXxa(0.0f);
                this.dolphin.setYya(0.0f);
                this.dolphin.setZza(0.0f);
                return;
            }
            double d3 = this.wantedX - this.dolphin.getX();
            double d4 = d3 * d3 + (d = this.wantedY - this.dolphin.getY()) * d + (d2 = this.wantedZ - this.dolphin.getZ()) * d2;
            if (d4 < 2.500000277905201E-7) {
                this.mob.setZza(0.0f);
                return;
            }
            float f = (float)(Mth.atan2(d2, d3) * 57.2957763671875) - 90.0f;
            this.dolphin.yBodyRot = this.dolphin.yRot = this.rotlerp(this.dolphin.yRot, f, 10.0f);
            this.dolphin.yHeadRot = this.dolphin.yRot;
            float f2 = (float)(this.speedModifier * this.dolphin.getAttributeValue(Attributes.MOVEMENT_SPEED));
            if (this.dolphin.isInWater()) {
                this.dolphin.setSpeed(f2 * 0.02f);
                float f3 = -((float)(Mth.atan2(d, Mth.sqrt(d3 * d3 + d2 * d2)) * 57.2957763671875));
                f3 = Mth.clamp(Mth.wrapDegrees(f3), -85.0f, 85.0f);
                this.dolphin.xRot = this.rotlerp(this.dolphin.xRot, f3, 5.0f);
                float f4 = Mth.cos(this.dolphin.xRot * 0.017453292f);
                float f5 = Mth.sin(this.dolphin.xRot * 0.017453292f);
                this.dolphin.zza = f4 * f2;
                this.dolphin.yya = -f5 * f2;
            } else {
                this.dolphin.setSpeed(f2 * 0.1f);
            }
        }
    }

}

