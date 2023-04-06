/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.BredAnimalsTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.JumpGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.StrollThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Fox
extends Animal {
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(Fox.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Fox.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Optional<UUID>> DATA_TRUSTED_ID_0 = SynchedEntityData.defineId(Fox.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Optional<UUID>> DATA_TRUSTED_ID_1 = SynchedEntityData.defineId(Fox.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final Predicate<ItemEntity> ALLOWED_ITEMS = itemEntity -> !itemEntity.hasPickUpDelay() && itemEntity.isAlive();
    private static final Predicate<Entity> TRUSTED_TARGET_SELECTOR = entity -> {
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            return livingEntity.getLastHurtMob() != null && livingEntity.getLastHurtMobTimestamp() < livingEntity.tickCount + 600;
        }
        return false;
    };
    private static final Predicate<Entity> STALKABLE_PREY = entity -> entity instanceof Chicken || entity instanceof Rabbit;
    private static final Predicate<Entity> AVOID_PLAYERS = entity -> !entity.isDiscrete() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test((Entity)entity);
    private Goal landTargetGoal;
    private Goal turtleEggTargetGoal;
    private Goal fishTargetGoal;
    private float interestedAngle;
    private float interestedAngleO;
    private float crouchAmount;
    private float crouchAmountO;
    private int ticksSinceEaten;

    public Fox(EntityType<? extends Fox> entityType, Level level) {
        super(entityType, level);
        this.lookControl = new FoxLookControl();
        this.moveControl = new FoxMoveControl();
        this.setPathfindingMalus(BlockPathTypes.DANGER_OTHER, 0.0f);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_OTHER, 0.0f);
        this.setCanPickUpLoot(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TRUSTED_ID_0, Optional.empty());
        this.entityData.define(DATA_TRUSTED_ID_1, Optional.empty());
        this.entityData.define(DATA_TYPE_ID, 0);
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
    }

    @Override
    protected void registerGoals() {
        this.landTargetGoal = new NearestAttackableTargetGoal<Animal>(this, Animal.class, 10, false, false, livingEntity -> livingEntity instanceof Chicken || livingEntity instanceof Rabbit);
        this.turtleEggTargetGoal = new NearestAttackableTargetGoal<Turtle>(this, Turtle.class, 10, false, false, Turtle.BABY_ON_LAND_SELECTOR);
        this.fishTargetGoal = new NearestAttackableTargetGoal<AbstractFish>(this, AbstractFish.class, 20, false, false, livingEntity -> livingEntity instanceof AbstractSchoolingFish);
        this.goalSelector.addGoal(0, new FoxFloatGoal());
        this.goalSelector.addGoal(1, new FaceplantGoal());
        this.goalSelector.addGoal(2, new FoxPanicGoal(2.2));
        this.goalSelector.addGoal(3, new FoxBreedGoal(1.0));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<Player>(this, Player.class, 16.0f, 1.6, 1.4, livingEntity -> AVOID_PLAYERS.test((Entity)livingEntity) && !this.trusts(livingEntity.getUUID()) && !this.isDefending()));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<Wolf>(this, Wolf.class, 8.0f, 1.6, 1.4, livingEntity -> !((Wolf)livingEntity).isTame() && !this.isDefending()));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<PolarBear>(this, PolarBear.class, 8.0f, 1.6, 1.4, livingEntity -> !this.isDefending()));
        this.goalSelector.addGoal(5, new StalkPreyGoal());
        this.goalSelector.addGoal(6, new FoxPounceGoal());
        this.goalSelector.addGoal(6, new SeekShelterGoal(1.25));
        this.goalSelector.addGoal(7, new FoxMeleeAttackGoal(1.2000000476837158, true));
        this.goalSelector.addGoal(7, new SleepGoal());
        this.goalSelector.addGoal(8, new FoxFollowParentGoal(this, 1.25));
        this.goalSelector.addGoal(9, new FoxStrollThroughVillageGoal(32, 200));
        this.goalSelector.addGoal(10, new FoxEatBerriesGoal(1.2000000476837158, 12, 2));
        this.goalSelector.addGoal(10, new LeapAtTargetGoal(this, 0.4f));
        this.goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(11, new FoxSearchForItemsGoal());
        this.goalSelector.addGoal(12, new FoxLookAtPlayerGoal(this, Player.class, 24.0f));
        this.goalSelector.addGoal(13, new PerchAndSearchGoal());
        this.targetSelector.addGoal(3, new DefendTrustedTargetGoal(LivingEntity.class, false, false, livingEntity -> TRUSTED_TARGET_SELECTOR.test((Entity)livingEntity) && !this.trusts(livingEntity.getUUID())));
    }

    @Override
    public SoundEvent getEatingSound(ItemStack itemStack) {
        return SoundEvents.FOX_EAT;
    }

    @Override
    public void aiStep() {
        if (!this.level.isClientSide && this.isAlive() && this.isEffectiveAi()) {
            Object object;
            ++this.ticksSinceEaten;
            ItemStack itemStack = this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (this.canEat(itemStack)) {
                if (this.ticksSinceEaten > 600) {
                    object = itemStack.finishUsingItem(this.level, this);
                    if (!((ItemStack)object).isEmpty()) {
                        this.setItemSlot(EquipmentSlot.MAINHAND, (ItemStack)object);
                    }
                    this.ticksSinceEaten = 0;
                } else if (this.ticksSinceEaten > 560 && this.random.nextFloat() < 0.1f) {
                    this.playSound(this.getEatingSound(itemStack), 1.0f, 1.0f);
                    this.level.broadcastEntityEvent(this, (byte)45);
                }
            }
            if ((object = this.getTarget()) == null || !((LivingEntity)object).isAlive()) {
                this.setIsCrouching(false);
                this.setIsInterested(false);
            }
        }
        if (this.isSleeping() || this.isImmobile()) {
            this.jumping = false;
            this.xxa = 0.0f;
            this.zza = 0.0f;
        }
        super.aiStep();
        if (this.isDefending() && this.random.nextFloat() < 0.05f) {
            this.playSound(SoundEvents.FOX_AGGRO, 1.0f, 1.0f);
        }
    }

    @Override
    protected boolean isImmobile() {
        return this.isDeadOrDying();
    }

    private boolean canEat(ItemStack itemStack) {
        return itemStack.getItem().isEdible() && this.getTarget() == null && this.onGround && !this.isSleeping();
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficultyInstance) {
        if (this.random.nextFloat() < 0.2f) {
            float f = this.random.nextFloat();
            ItemStack itemStack = f < 0.05f ? new ItemStack(Items.EMERALD) : (f < 0.2f ? new ItemStack(Items.EGG) : (f < 0.4f ? (this.random.nextBoolean() ? new ItemStack(Items.RABBIT_FOOT) : new ItemStack(Items.RABBIT_HIDE)) : (f < 0.6f ? new ItemStack(Items.WHEAT) : (f < 0.8f ? new ItemStack(Items.LEATHER) : new ItemStack(Items.FEATHER)))));
            this.setItemSlot(EquipmentSlot.MAINHAND, itemStack);
        }
    }

    @Override
    public void handleEntityEvent(byte by) {
        if (by == 45) {
            ItemStack itemStack = this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (!itemStack.isEmpty()) {
                for (int i = 0; i < 8; ++i) {
                    Vec3 vec3 = new Vec3(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0).xRot(-this.xRot * 0.017453292f).yRot(-this.yRot * 0.017453292f);
                    this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemStack), this.getX() + this.getLookAngle().x / 2.0, this.getY(), this.getZ() + this.getLookAngle().z / 2.0, vec3.x, vec3.y + 0.05, vec3.z);
                }
            }
        } else {
            super.handleEntityEvent(by);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.30000001192092896).add(Attributes.MAX_HEALTH, 10.0).add(Attributes.FOLLOW_RANGE, 32.0).add(Attributes.ATTACK_DAMAGE, 2.0);
    }

    @Override
    public Fox getBreedOffspring(ServerLevel serverLevel, AgableMob agableMob) {
        Fox fox = EntityType.FOX.create(serverLevel);
        fox.setFoxType(this.random.nextBoolean() ? this.getFoxType() : ((Fox)agableMob).getFoxType());
        return fox;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        Optional<ResourceKey<Biome>> optional = serverLevelAccessor.getBiomeName(this.blockPosition());
        Type type = Type.byBiome(optional);
        boolean bl = false;
        if (spawnGroupData instanceof FoxGroupData) {
            type = ((FoxGroupData)spawnGroupData).type;
            if (((FoxGroupData)spawnGroupData).getGroupSize() >= 2) {
                bl = true;
            }
        } else {
            spawnGroupData = new FoxGroupData(type);
        }
        this.setFoxType(type);
        if (bl) {
            this.setAge(-24000);
        }
        if (serverLevelAccessor instanceof ServerLevel) {
            this.setTargetGoals();
        }
        this.populateDefaultEquipmentSlots(difficultyInstance);
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    private void setTargetGoals() {
        if (this.getFoxType() == Type.RED) {
            this.targetSelector.addGoal(4, this.landTargetGoal);
            this.targetSelector.addGoal(4, this.turtleEggTargetGoal);
            this.targetSelector.addGoal(6, this.fishTargetGoal);
        } else {
            this.targetSelector.addGoal(4, this.fishTargetGoal);
            this.targetSelector.addGoal(6, this.landTargetGoal);
            this.targetSelector.addGoal(6, this.turtleEggTargetGoal);
        }
    }

    @Override
    protected void usePlayerItem(Player player, ItemStack itemStack) {
        if (this.isFood(itemStack)) {
            this.playSound(this.getEatingSound(itemStack), 1.0f, 1.0f);
        }
        super.usePlayerItem(player, itemStack);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        if (this.isBaby()) {
            return entityDimensions.height * 0.85f;
        }
        return 0.4f;
    }

    public Type getFoxType() {
        return Type.byId(this.entityData.get(DATA_TYPE_ID));
    }

    private void setFoxType(Type type) {
        this.entityData.set(DATA_TYPE_ID, type.getId());
    }

    private List<UUID> getTrustedUUIDs() {
        ArrayList arrayList = Lists.newArrayList();
        arrayList.add(this.entityData.get(DATA_TRUSTED_ID_0).orElse(null));
        arrayList.add(this.entityData.get(DATA_TRUSTED_ID_1).orElse(null));
        return arrayList;
    }

    private void addTrustedUUID(@Nullable UUID uUID) {
        if (this.entityData.get(DATA_TRUSTED_ID_0).isPresent()) {
            this.entityData.set(DATA_TRUSTED_ID_1, Optional.ofNullable(uUID));
        } else {
            this.entityData.set(DATA_TRUSTED_ID_0, Optional.ofNullable(uUID));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        List<UUID> list = this.getTrustedUUIDs();
        ListTag listTag = new ListTag();
        for (UUID uUID : list) {
            if (uUID == null) continue;
            listTag.add(NbtUtils.createUUID(uUID));
        }
        compoundTag.put("Trusted", listTag);
        compoundTag.putBoolean("Sleeping", this.isSleeping());
        compoundTag.putString("Type", this.getFoxType().getName());
        compoundTag.putBoolean("Sitting", this.isSitting());
        compoundTag.putBoolean("Crouching", this.isCrouching());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        ListTag listTag = compoundTag.getList("Trusted", 11);
        for (int i = 0; i < listTag.size(); ++i) {
            this.addTrustedUUID(NbtUtils.loadUUID(listTag.get(i)));
        }
        this.setSleeping(compoundTag.getBoolean("Sleeping"));
        this.setFoxType(Type.byName(compoundTag.getString("Type")));
        this.setSitting(compoundTag.getBoolean("Sitting"));
        this.setIsCrouching(compoundTag.getBoolean("Crouching"));
        if (this.level instanceof ServerLevel) {
            this.setTargetGoals();
        }
    }

    public boolean isSitting() {
        return this.getFlag(1);
    }

    public void setSitting(boolean bl) {
        this.setFlag(1, bl);
    }

    public boolean isFaceplanted() {
        return this.getFlag(64);
    }

    private void setFaceplanted(boolean bl) {
        this.setFlag(64, bl);
    }

    private boolean isDefending() {
        return this.getFlag(128);
    }

    private void setDefending(boolean bl) {
        this.setFlag(128, bl);
    }

    @Override
    public boolean isSleeping() {
        return this.getFlag(32);
    }

    private void setSleeping(boolean bl) {
        this.setFlag(32, bl);
    }

    private void setFlag(int n, boolean bl) {
        if (bl) {
            this.entityData.set(DATA_FLAGS_ID, (byte)(this.entityData.get(DATA_FLAGS_ID) | n));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte)(this.entityData.get(DATA_FLAGS_ID) & ~n));
        }
    }

    private boolean getFlag(int n) {
        return (this.entityData.get(DATA_FLAGS_ID) & n) != 0;
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
    public boolean canHoldItem(ItemStack itemStack) {
        Item item = itemStack.getItem();
        ItemStack itemStack2 = this.getItemBySlot(EquipmentSlot.MAINHAND);
        return itemStack2.isEmpty() || this.ticksSinceEaten > 0 && item.isEdible() && !itemStack2.getItem().isEdible();
    }

    private void spitOutItem(ItemStack itemStack) {
        if (itemStack.isEmpty() || this.level.isClientSide) {
            return;
        }
        ItemEntity itemEntity = new ItemEntity(this.level, this.getX() + this.getLookAngle().x, this.getY() + 1.0, this.getZ() + this.getLookAngle().z, itemStack);
        itemEntity.setPickUpDelay(40);
        itemEntity.setThrower(this.getUUID());
        this.playSound(SoundEvents.FOX_SPIT, 1.0f, 1.0f);
        this.level.addFreshEntity(itemEntity);
    }

    private void dropItemStack(ItemStack itemStack) {
        ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), itemStack);
        this.level.addFreshEntity(itemEntity);
    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        ItemStack itemStack = itemEntity.getItem();
        if (this.canHoldItem(itemStack)) {
            int n = itemStack.getCount();
            if (n > 1) {
                this.dropItemStack(itemStack.split(n - 1));
            }
            this.spitOutItem(this.getItemBySlot(EquipmentSlot.MAINHAND));
            this.onItemPickup(itemEntity);
            this.setItemSlot(EquipmentSlot.MAINHAND, itemStack.split(1));
            this.handDropChances[EquipmentSlot.MAINHAND.getIndex()] = 2.0f;
            this.take(itemEntity, itemStack.getCount());
            itemEntity.remove();
            this.ticksSinceEaten = 0;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isEffectiveAi()) {
            boolean bl = this.isInWater();
            if (bl || this.getTarget() != null || this.level.isThundering()) {
                this.wakeUp();
            }
            if (bl || this.isSleeping()) {
                this.setSitting(false);
            }
            if (this.isFaceplanted() && this.level.random.nextFloat() < 0.2f) {
                BlockPos blockPos = this.blockPosition();
                BlockState blockState = this.level.getBlockState(blockPos);
                this.level.levelEvent(2001, blockPos, Block.getId(blockState));
            }
        }
        this.interestedAngleO = this.interestedAngle;
        this.interestedAngle = this.isInterested() ? (this.interestedAngle += (1.0f - this.interestedAngle) * 0.4f) : (this.interestedAngle += (0.0f - this.interestedAngle) * 0.4f);
        this.crouchAmountO = this.crouchAmount;
        if (this.isCrouching()) {
            this.crouchAmount += 0.2f;
            if (this.crouchAmount > 3.0f) {
                this.crouchAmount = 3.0f;
            }
        } else {
            this.crouchAmount = 0.0f;
        }
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.getItem() == Items.SWEET_BERRIES;
    }

    @Override
    protected void onOffspringSpawnedFromEgg(Player player, Mob mob) {
        ((Fox)mob).addTrustedUUID(player.getUUID());
    }

    public boolean isPouncing() {
        return this.getFlag(16);
    }

    public void setIsPouncing(boolean bl) {
        this.setFlag(16, bl);
    }

    public boolean isFullyCrouched() {
        return this.crouchAmount == 3.0f;
    }

    public void setIsCrouching(boolean bl) {
        this.setFlag(4, bl);
    }

    @Override
    public boolean isCrouching() {
        return this.getFlag(4);
    }

    public void setIsInterested(boolean bl) {
        this.setFlag(8, bl);
    }

    public boolean isInterested() {
        return this.getFlag(8);
    }

    public float getHeadRollAngle(float f) {
        return Mth.lerp(f, this.interestedAngleO, this.interestedAngle) * 0.11f * 3.1415927f;
    }

    public float getCrouchAmount(float f) {
        return Mth.lerp(f, this.crouchAmountO, this.crouchAmount);
    }

    @Override
    public void setTarget(@Nullable LivingEntity livingEntity) {
        if (this.isDefending() && livingEntity == null) {
            this.setDefending(false);
        }
        super.setTarget(livingEntity);
    }

    @Override
    protected int calculateFallDamage(float f, float f2) {
        return Mth.ceil((f - 5.0f) * f2);
    }

    private void wakeUp() {
        this.setSleeping(false);
    }

    private void clearStates() {
        this.setIsInterested(false);
        this.setIsCrouching(false);
        this.setSitting(false);
        this.setSleeping(false);
        this.setDefending(false);
        this.setFaceplanted(false);
    }

    private boolean canMove() {
        return !this.isSleeping() && !this.isSitting() && !this.isFaceplanted();
    }

    @Override
    public void playAmbientSound() {
        SoundEvent soundEvent = this.getAmbientSound();
        if (soundEvent == SoundEvents.FOX_SCREECH) {
            this.playSound(soundEvent, 2.0f, this.getVoicePitch());
        } else {
            super.playAmbientSound();
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        List<Entity> list;
        if (this.isSleeping()) {
            return SoundEvents.FOX_SLEEP;
        }
        if (!this.level.isDay() && this.random.nextFloat() < 0.1f && (list = this.level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(16.0, 16.0, 16.0), EntitySelector.NO_SPECTATORS)).isEmpty()) {
            return SoundEvents.FOX_SCREECH;
        }
        return SoundEvents.FOX_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.FOX_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.FOX_DEATH;
    }

    private boolean trusts(UUID uUID) {
        return this.getTrustedUUIDs().contains(uUID);
    }

    @Override
    protected void dropAllDeathLoot(DamageSource damageSource) {
        ItemStack itemStack = this.getItemBySlot(EquipmentSlot.MAINHAND);
        if (!itemStack.isEmpty()) {
            this.spawnAtLocation(itemStack);
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
        super.dropAllDeathLoot(damageSource);
    }

    public static boolean isPathClear(Fox fox, LivingEntity livingEntity) {
        double d = livingEntity.getZ() - fox.getZ();
        double d2 = livingEntity.getX() - fox.getX();
        double d3 = d / d2;
        int n = 6;
        for (int i = 0; i < 6; ++i) {
            double d4 = d3 == 0.0 ? 0.0 : d * (double)((float)i / 6.0f);
            double d5 = d3 == 0.0 ? d2 * (double)((float)i / 6.0f) : d4 / d3;
            for (int j = 1; j < 4; ++j) {
                if (fox.level.getBlockState(new BlockPos(fox.getX() + d5, fox.getY() + (double)j, fox.getZ() + d4)).getMaterial().isReplaceable()) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.55f * this.getEyeHeight(), this.getBbWidth() * 0.4f);
    }

    @Override
    public /* synthetic */ AgableMob getBreedOffspring(ServerLevel serverLevel, AgableMob agableMob) {
        return this.getBreedOffspring(serverLevel, agableMob);
    }

    class FoxLookAtPlayerGoal
    extends LookAtPlayerGoal {
        public FoxLookAtPlayerGoal(Mob mob, Class<? extends LivingEntity> class_, float f) {
            super(mob, class_, f);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !Fox.this.isFaceplanted() && !Fox.this.isInterested();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && !Fox.this.isFaceplanted() && !Fox.this.isInterested();
        }
    }

    class FoxFollowParentGoal
    extends FollowParentGoal {
        private final Fox fox;

        public FoxFollowParentGoal(Fox fox2, double d) {
            super(fox2, d);
            this.fox = fox2;
        }

        @Override
        public boolean canUse() {
            return !this.fox.isDefending() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !this.fox.isDefending() && super.canContinueToUse();
        }

        @Override
        public void start() {
            this.fox.clearStates();
            super.start();
        }
    }

    public class FoxLookControl
    extends LookControl {
        public FoxLookControl() {
            super(Fox.this);
        }

        @Override
        public void tick() {
            if (!Fox.this.isSleeping()) {
                super.tick();
            }
        }

        @Override
        protected boolean resetXRotOnTick() {
            return !Fox.this.isPouncing() && !Fox.this.isCrouching() && !Fox.this.isInterested() & !Fox.this.isFaceplanted();
        }
    }

    public class FoxPounceGoal
    extends JumpGoal {
        @Override
        public boolean canUse() {
            if (!Fox.this.isFullyCrouched()) {
                return false;
            }
            LivingEntity livingEntity = Fox.this.getTarget();
            if (livingEntity == null || !livingEntity.isAlive()) {
                return false;
            }
            if (livingEntity.getMotionDirection() != livingEntity.getDirection()) {
                return false;
            }
            boolean bl = Fox.isPathClear(Fox.this, livingEntity);
            if (!bl) {
                Fox.this.getNavigation().createPath(livingEntity, 0);
                Fox.this.setIsCrouching(false);
                Fox.this.setIsInterested(false);
            }
            return bl;
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity livingEntity = Fox.this.getTarget();
            if (livingEntity == null || !livingEntity.isAlive()) {
                return false;
            }
            double d = Fox.this.getDeltaMovement().y;
            return !(d * d < 0.05000000074505806 && Math.abs(Fox.this.xRot) < 15.0f && Fox.this.onGround || Fox.this.isFaceplanted());
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Override
        public void start() {
            Fox.this.setJumping(true);
            Fox.this.setIsPouncing(true);
            Fox.this.setIsInterested(false);
            LivingEntity livingEntity = Fox.this.getTarget();
            Fox.this.getLookControl().setLookAt(livingEntity, 60.0f, 30.0f);
            Vec3 vec3 = new Vec3(livingEntity.getX() - Fox.this.getX(), livingEntity.getY() - Fox.this.getY(), livingEntity.getZ() - Fox.this.getZ()).normalize();
            Fox.this.setDeltaMovement(Fox.this.getDeltaMovement().add(vec3.x * 0.8, 0.9, vec3.z * 0.8));
            Fox.this.getNavigation().stop();
        }

        @Override
        public void stop() {
            Fox.this.setIsCrouching(false);
            Fox.this.crouchAmount = 0.0f;
            Fox.this.crouchAmountO = 0.0f;
            Fox.this.setIsInterested(false);
            Fox.this.setIsPouncing(false);
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = Fox.this.getTarget();
            if (livingEntity != null) {
                Fox.this.getLookControl().setLookAt(livingEntity, 60.0f, 30.0f);
            }
            if (!Fox.this.isFaceplanted()) {
                Vec3 vec3 = Fox.this.getDeltaMovement();
                if (vec3.y * vec3.y < 0.029999999329447746 && Fox.this.xRot != 0.0f) {
                    Fox.this.xRot = Mth.rotlerp(Fox.this.xRot, 0.0f, 0.2f);
                } else {
                    double d = Math.sqrt(Entity.getHorizontalDistanceSqr(vec3));
                    double d2 = Math.signum(-vec3.y) * Math.acos(d / vec3.length()) * 57.2957763671875;
                    Fox.this.xRot = (float)d2;
                }
            }
            if (livingEntity != null && Fox.this.distanceTo(livingEntity) <= 2.0f) {
                Fox.this.doHurtTarget(livingEntity);
            } else if (Fox.this.xRot > 0.0f && Fox.this.onGround && (float)Fox.this.getDeltaMovement().y != 0.0f && Fox.this.level.getBlockState(Fox.this.blockPosition()).is(Blocks.SNOW)) {
                Fox.this.xRot = 60.0f;
                Fox.this.setTarget(null);
                Fox.this.setFaceplanted(true);
            }
        }
    }

    class FoxFloatGoal
    extends FloatGoal {
        public FoxFloatGoal() {
            super(Fox.this);
        }

        @Override
        public void start() {
            super.start();
            Fox.this.clearStates();
        }

        @Override
        public boolean canUse() {
            return Fox.this.isInWater() && Fox.this.getFluidHeight(FluidTags.WATER) > 0.25 || Fox.this.isInLava();
        }
    }

    class FoxStrollThroughVillageGoal
    extends StrollThroughVillageGoal {
        public FoxStrollThroughVillageGoal(int n, int n2) {
            super(Fox.this, n2);
        }

        @Override
        public void start() {
            Fox.this.clearStates();
            super.start();
        }

        @Override
        public boolean canUse() {
            return super.canUse() && this.canFoxMove();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.canFoxMove();
        }

        private boolean canFoxMove() {
            return !Fox.this.isSleeping() && !Fox.this.isSitting() && !Fox.this.isDefending() && Fox.this.getTarget() == null;
        }
    }

    class FoxPanicGoal
    extends PanicGoal {
        public FoxPanicGoal(double d) {
            super(Fox.this, d);
        }

        @Override
        public boolean canUse() {
            return !Fox.this.isDefending() && super.canUse();
        }
    }

    class FaceplantGoal
    extends Goal {
        int countdown;

        public FaceplantGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return Fox.this.isFaceplanted();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse() && this.countdown > 0;
        }

        @Override
        public void start() {
            this.countdown = 40;
        }

        @Override
        public void stop() {
            Fox.this.setFaceplanted(false);
        }

        @Override
        public void tick() {
            --this.countdown;
        }
    }

    public static class FoxGroupData
    extends AgableMob.AgableMobGroupData {
        public final Type type;

        public FoxGroupData(Type type) {
            super(false);
            this.type = type;
        }
    }

    public class FoxEatBerriesGoal
    extends MoveToBlockGoal {
        protected int ticksWaited;

        public FoxEatBerriesGoal(double d, int n, int n2) {
            super(Fox.this, d, n, n2);
        }

        @Override
        public double acceptedDistance() {
            return 2.0;
        }

        @Override
        public boolean shouldRecalculatePath() {
            return this.tryTicks % 100 == 0;
        }

        @Override
        protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
            BlockState blockState = levelReader.getBlockState(blockPos);
            return blockState.is(Blocks.SWEET_BERRY_BUSH) && blockState.getValue(SweetBerryBushBlock.AGE) >= 2;
        }

        @Override
        public void tick() {
            if (this.isReachedTarget()) {
                if (this.ticksWaited >= 40) {
                    this.onReachedTarget();
                } else {
                    ++this.ticksWaited;
                }
            } else if (!this.isReachedTarget() && Fox.this.random.nextFloat() < 0.05f) {
                Fox.this.playSound(SoundEvents.FOX_SNIFF, 1.0f, 1.0f);
            }
            super.tick();
        }

        protected void onReachedTarget() {
            if (!Fox.this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                return;
            }
            BlockState blockState = Fox.this.level.getBlockState(this.blockPos);
            if (!blockState.is(Blocks.SWEET_BERRY_BUSH)) {
                return;
            }
            int n = blockState.getValue(SweetBerryBushBlock.AGE);
            blockState.setValue(SweetBerryBushBlock.AGE, 1);
            int n2 = 1 + Fox.this.level.random.nextInt(2) + (n == 3 ? 1 : 0);
            ItemStack itemStack = Fox.this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (itemStack.isEmpty()) {
                Fox.this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.SWEET_BERRIES));
                --n2;
            }
            if (n2 > 0) {
                Block.popResource(Fox.this.level, this.blockPos, new ItemStack(Items.SWEET_BERRIES, n2));
            }
            Fox.this.playSound(SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, 1.0f, 1.0f);
            Fox.this.level.setBlock(this.blockPos, (BlockState)blockState.setValue(SweetBerryBushBlock.AGE, 1), 2);
        }

        @Override
        public boolean canUse() {
            return !Fox.this.isSleeping() && super.canUse();
        }

        @Override
        public void start() {
            this.ticksWaited = 0;
            Fox.this.setSitting(false);
            super.start();
        }
    }

    class PerchAndSearchGoal
    extends FoxBehaviorGoal {
        private double relX;
        private double relZ;
        private int lookTime;
        private int looksRemaining;

        public PerchAndSearchGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return Fox.this.getLastHurtByMob() == null && Fox.this.getRandom().nextFloat() < 0.02f && !Fox.this.isSleeping() && Fox.this.getTarget() == null && Fox.this.getNavigation().isDone() && !this.alertable() && !Fox.this.isPouncing() && !Fox.this.isCrouching();
        }

        @Override
        public boolean canContinueToUse() {
            return this.looksRemaining > 0;
        }

        @Override
        public void start() {
            this.resetLook();
            this.looksRemaining = 2 + Fox.this.getRandom().nextInt(3);
            Fox.this.setSitting(true);
            Fox.this.getNavigation().stop();
        }

        @Override
        public void stop() {
            Fox.this.setSitting(false);
        }

        @Override
        public void tick() {
            --this.lookTime;
            if (this.lookTime <= 0) {
                --this.looksRemaining;
                this.resetLook();
            }
            Fox.this.getLookControl().setLookAt(Fox.this.getX() + this.relX, Fox.this.getEyeY(), Fox.this.getZ() + this.relZ, Fox.this.getMaxHeadYRot(), Fox.this.getMaxHeadXRot());
        }

        private void resetLook() {
            double d = 6.283185307179586 * Fox.this.getRandom().nextDouble();
            this.relX = Math.cos(d);
            this.relZ = Math.sin(d);
            this.lookTime = 80 + Fox.this.getRandom().nextInt(20);
        }
    }

    class SleepGoal
    extends FoxBehaviorGoal {
        private int countdown;

        public SleepGoal() {
            this.countdown = Fox.this.random.nextInt(140);
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            if (Fox.this.xxa != 0.0f || Fox.this.yya != 0.0f || Fox.this.zza != 0.0f) {
                return false;
            }
            return this.canSleep() || Fox.this.isSleeping();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canSleep();
        }

        private boolean canSleep() {
            if (this.countdown > 0) {
                --this.countdown;
                return false;
            }
            return Fox.this.level.isDay() && this.hasShelter() && !this.alertable();
        }

        @Override
        public void stop() {
            this.countdown = Fox.this.random.nextInt(140);
            Fox.this.clearStates();
        }

        @Override
        public void start() {
            Fox.this.setSitting(false);
            Fox.this.setIsCrouching(false);
            Fox.this.setIsInterested(false);
            Fox.this.setJumping(false);
            Fox.this.setSleeping(true);
            Fox.this.getNavigation().stop();
            Fox.this.getMoveControl().setWantedPosition(Fox.this.getX(), Fox.this.getY(), Fox.this.getZ(), 0.0);
        }
    }

    abstract class FoxBehaviorGoal
    extends Goal {
        private final TargetingConditions alertableTargeting;

        private FoxBehaviorGoal() {
            this.alertableTargeting = new TargetingConditions().range(12.0).allowUnseeable().selector(new FoxAlertableEntitiesSelector());
        }

        protected boolean hasShelter() {
            BlockPos blockPos = new BlockPos(Fox.this.getX(), Fox.this.getBoundingBox().maxY, Fox.this.getZ());
            return !Fox.this.level.canSeeSky(blockPos) && Fox.this.getWalkTargetValue(blockPos) >= 0.0f;
        }

        protected boolean alertable() {
            return !Fox.this.level.getNearbyEntities(LivingEntity.class, this.alertableTargeting, Fox.this, Fox.this.getBoundingBox().inflate(12.0, 6.0, 12.0)).isEmpty();
        }
    }

    public class FoxAlertableEntitiesSelector
    implements Predicate<LivingEntity> {
        @Override
        public boolean test(LivingEntity livingEntity) {
            if (livingEntity instanceof Fox) {
                return false;
            }
            if (livingEntity instanceof Chicken || livingEntity instanceof Rabbit || livingEntity instanceof Monster) {
                return true;
            }
            if (livingEntity instanceof TamableAnimal) {
                return !((TamableAnimal)livingEntity).isTame();
            }
            if (livingEntity instanceof Player && (livingEntity.isSpectator() || ((Player)livingEntity).isCreative())) {
                return false;
            }
            if (Fox.this.trusts(livingEntity.getUUID())) {
                return false;
            }
            return !livingEntity.isSleeping() && !livingEntity.isDiscrete();
        }

        @Override
        public /* synthetic */ boolean test(Object object) {
            return this.test((LivingEntity)object);
        }
    }

    class SeekShelterGoal
    extends FleeSunGoal {
        private int interval;

        public SeekShelterGoal(double d) {
            super(Fox.this, d);
            this.interval = 100;
        }

        @Override
        public boolean canUse() {
            if (Fox.this.isSleeping() || this.mob.getTarget() != null) {
                return false;
            }
            if (Fox.this.level.isThundering()) {
                return true;
            }
            if (this.interval > 0) {
                --this.interval;
                return false;
            }
            this.interval = 100;
            BlockPos blockPos = this.mob.blockPosition();
            return Fox.this.level.isDay() && Fox.this.level.canSeeSky(blockPos) && !((ServerLevel)Fox.this.level).isVillage(blockPos) && this.setWantedPos();
        }

        @Override
        public void start() {
            Fox.this.clearStates();
            super.start();
        }
    }

    class DefendTrustedTargetGoal
    extends NearestAttackableTargetGoal<LivingEntity> {
        @Nullable
        private LivingEntity trustedLastHurtBy;
        private LivingEntity trustedLastHurt;
        private int timestamp;

        public DefendTrustedTargetGoal(Class<LivingEntity> class_, boolean bl, boolean bl2, @Nullable Predicate<LivingEntity> predicate) {
            super(Fox.this, class_, 10, bl, bl2, predicate);
        }

        @Override
        public boolean canUse() {
            if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
                return false;
            }
            for (UUID uUID : Fox.this.getTrustedUUIDs()) {
                Entity entity;
                LivingEntity livingEntity;
                if (uUID == null || !(Fox.this.level instanceof ServerLevel) || !((entity = ((ServerLevel)Fox.this.level).getEntity(uUID)) instanceof LivingEntity)) continue;
                this.trustedLastHurt = livingEntity = (LivingEntity)entity;
                this.trustedLastHurtBy = livingEntity.getLastHurtByMob();
                int n = livingEntity.getLastHurtByMobTimestamp();
                return n != this.timestamp && this.canAttack(this.trustedLastHurtBy, this.targetConditions);
            }
            return false;
        }

        @Override
        public void start() {
            this.setTarget(this.trustedLastHurtBy);
            this.target = this.trustedLastHurtBy;
            if (this.trustedLastHurt != null) {
                this.timestamp = this.trustedLastHurt.getLastHurtByMobTimestamp();
            }
            Fox.this.playSound(SoundEvents.FOX_AGGRO, 1.0f, 1.0f);
            Fox.this.setDefending(true);
            Fox.this.wakeUp();
            super.start();
        }
    }

    class FoxBreedGoal
    extends BreedGoal {
        public FoxBreedGoal(double d) {
            super(Fox.this, d);
        }

        @Override
        public void start() {
            ((Fox)this.animal).clearStates();
            ((Fox)this.partner).clearStates();
            super.start();
        }

        @Override
        protected void breed() {
            ServerLevel serverLevel = (ServerLevel)this.level;
            Fox fox = (Fox)this.animal.getBreedOffspring(serverLevel, this.partner);
            if (fox == null) {
                return;
            }
            ServerPlayer serverPlayer = this.animal.getLoveCause();
            ServerPlayer serverPlayer2 = this.partner.getLoveCause();
            ServerPlayer serverPlayer3 = serverPlayer;
            if (serverPlayer != null) {
                fox.addTrustedUUID(serverPlayer.getUUID());
            } else {
                serverPlayer3 = serverPlayer2;
            }
            if (serverPlayer2 != null && serverPlayer != serverPlayer2) {
                fox.addTrustedUUID(serverPlayer2.getUUID());
            }
            if (serverPlayer3 != null) {
                serverPlayer3.awardStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverPlayer3, this.animal, this.partner, fox);
            }
            this.animal.setAge(6000);
            this.partner.setAge(6000);
            this.animal.resetLove();
            this.partner.resetLove();
            fox.setAge(-24000);
            fox.moveTo(this.animal.getX(), this.animal.getY(), this.animal.getZ(), 0.0f, 0.0f);
            serverLevel.addFreshEntityWithPassengers(fox);
            this.level.broadcastEntityEvent(this.animal, (byte)18);
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), this.animal.getRandom().nextInt(7) + 1));
            }
        }
    }

    class FoxMeleeAttackGoal
    extends MeleeAttackGoal {
        public FoxMeleeAttackGoal(double d, boolean bl) {
            super(Fox.this, d, bl);
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity livingEntity, double d) {
            double d2 = this.getAttackReachSqr(livingEntity);
            if (d <= d2 && this.isTimeToAttack()) {
                this.resetAttackCooldown();
                this.mob.doHurtTarget(livingEntity);
                Fox.this.playSound(SoundEvents.FOX_BITE, 1.0f, 1.0f);
            }
        }

        @Override
        public void start() {
            Fox.this.setIsInterested(false);
            super.start();
        }

        @Override
        public boolean canUse() {
            return !Fox.this.isSitting() && !Fox.this.isSleeping() && !Fox.this.isCrouching() && !Fox.this.isFaceplanted() && super.canUse();
        }
    }

    class StalkPreyGoal
    extends Goal {
        public StalkPreyGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (Fox.this.isSleeping()) {
                return false;
            }
            LivingEntity livingEntity = Fox.this.getTarget();
            return livingEntity != null && livingEntity.isAlive() && STALKABLE_PREY.test(livingEntity) && Fox.this.distanceToSqr(livingEntity) > 36.0 && !Fox.this.isCrouching() && !Fox.this.isInterested() && !Fox.this.jumping;
        }

        @Override
        public void start() {
            Fox.this.setSitting(false);
            Fox.this.setFaceplanted(false);
        }

        @Override
        public void stop() {
            LivingEntity livingEntity = Fox.this.getTarget();
            if (livingEntity != null && Fox.isPathClear(Fox.this, livingEntity)) {
                Fox.this.setIsInterested(true);
                Fox.this.setIsCrouching(true);
                Fox.this.getNavigation().stop();
                Fox.this.getLookControl().setLookAt(livingEntity, Fox.this.getMaxHeadYRot(), Fox.this.getMaxHeadXRot());
            } else {
                Fox.this.setIsInterested(false);
                Fox.this.setIsCrouching(false);
            }
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = Fox.this.getTarget();
            Fox.this.getLookControl().setLookAt(livingEntity, Fox.this.getMaxHeadYRot(), Fox.this.getMaxHeadXRot());
            if (Fox.this.distanceToSqr(livingEntity) <= 36.0) {
                Fox.this.setIsInterested(true);
                Fox.this.setIsCrouching(true);
                Fox.this.getNavigation().stop();
            } else {
                Fox.this.getNavigation().moveTo(livingEntity, 1.5);
            }
        }
    }

    class FoxMoveControl
    extends MoveControl {
        public FoxMoveControl() {
            super(Fox.this);
        }

        @Override
        public void tick() {
            if (Fox.this.canMove()) {
                super.tick();
            }
        }
    }

    class FoxSearchForItemsGoal
    extends Goal {
        public FoxSearchForItemsGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!Fox.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
                return false;
            }
            if (Fox.this.getTarget() != null || Fox.this.getLastHurtByMob() != null) {
                return false;
            }
            if (!Fox.this.canMove()) {
                return false;
            }
            if (Fox.this.getRandom().nextInt(10) != 0) {
                return false;
            }
            List<ItemEntity> list = Fox.this.level.getEntitiesOfClass(ItemEntity.class, Fox.this.getBoundingBox().inflate(8.0, 8.0, 8.0), ALLOWED_ITEMS);
            return !list.isEmpty() && Fox.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
        }

        @Override
        public void tick() {
            List<ItemEntity> list = Fox.this.level.getEntitiesOfClass(ItemEntity.class, Fox.this.getBoundingBox().inflate(8.0, 8.0, 8.0), ALLOWED_ITEMS);
            ItemStack itemStack = Fox.this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (itemStack.isEmpty() && !list.isEmpty()) {
                Fox.this.getNavigation().moveTo(list.get(0), 1.2000000476837158);
            }
        }

        @Override
        public void start() {
            List<ItemEntity> list = Fox.this.level.getEntitiesOfClass(ItemEntity.class, Fox.this.getBoundingBox().inflate(8.0, 8.0, 8.0), ALLOWED_ITEMS);
            if (!list.isEmpty()) {
                Fox.this.getNavigation().moveTo(list.get(0), 1.2000000476837158);
            }
        }
    }

    public static enum Type {
        RED(0, "red", Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.TAIGA_MOUNTAINS, Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_SPRUCE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.GIANT_SPRUCE_TAIGA_HILLS),
        SNOW(1, "snow", Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS, Biomes.SNOWY_TAIGA_MOUNTAINS);
        
        private static final Type[] BY_ID;
        private static final Map<String, Type> BY_NAME;
        private final int id;
        private final String name;
        private final List<ResourceKey<Biome>> biomes;

        private Type(int n2, String string2, ResourceKey<Biome> ... arrresourceKey) {
            this.id = n2;
            this.name = string2;
            this.biomes = Arrays.asList(arrresourceKey);
        }

        public String getName() {
            return this.name;
        }

        public int getId() {
            return this.id;
        }

        public static Type byName(String string) {
            return BY_NAME.getOrDefault(string, RED);
        }

        public static Type byId(int n) {
            if (n < 0 || n > BY_ID.length) {
                n = 0;
            }
            return BY_ID[n];
        }

        public static Type byBiome(Optional<ResourceKey<Biome>> optional) {
            return optional.isPresent() && Type.SNOW.biomes.contains(optional.get()) ? SNOW : RED;
        }

        static {
            BY_ID = (Type[])Arrays.stream(Type.values()).sorted(Comparator.comparingInt(Type::getId)).toArray(n -> new Type[n]);
            BY_NAME = Arrays.stream(Type.values()).collect(Collectors.toMap(Type::getName, type -> type));
        }
    }

}

