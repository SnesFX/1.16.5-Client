/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.animal.horse;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.TameAnimalTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractHorse
extends Animal
implements ContainerListener,
PlayerRideableJumping,
Saddleable {
    private static final Predicate<LivingEntity> PARENT_HORSE_SELECTOR = livingEntity -> livingEntity instanceof AbstractHorse && ((AbstractHorse)livingEntity).isBred();
    private static final TargetingConditions MOMMY_TARGETING = new TargetingConditions().range(16.0).allowInvulnerable().allowSameTeam().allowUnseeable().selector(PARENT_HORSE_SELECTOR);
    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.WHEAT, Items.SUGAR, Blocks.HAY_BLOCK.asItem(), Items.APPLE, Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE);
    private static final EntityDataAccessor<Byte> DATA_ID_FLAGS = SynchedEntityData.defineId(AbstractHorse.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Optional<UUID>> DATA_ID_OWNER_UUID = SynchedEntityData.defineId(AbstractHorse.class, EntityDataSerializers.OPTIONAL_UUID);
    private int eatingCounter;
    private int mouthCounter;
    private int standCounter;
    public int tailCounter;
    public int sprintCounter;
    protected boolean isJumping;
    protected SimpleContainer inventory;
    protected int temper;
    protected float playerJumpPendingScale;
    private boolean allowStandSliding;
    private float eatAnim;
    private float eatAnimO;
    private float standAnim;
    private float standAnimO;
    private float mouthAnim;
    private float mouthAnimO;
    protected boolean canGallop = true;
    protected int gallopSoundCounter;

    protected AbstractHorse(EntityType<? extends AbstractHorse> entityType, Level level) {
        super(entityType, level);
        this.maxUpStep = 1.0f;
        this.createInventory();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.2));
        this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0, AbstractHorse.class));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.0));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.addBehaviourGoals();
    }

    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_FLAGS, (byte)0);
        this.entityData.define(DATA_ID_OWNER_UUID, Optional.empty());
    }

    protected boolean getFlag(int n) {
        return (this.entityData.get(DATA_ID_FLAGS) & n) != 0;
    }

    protected void setFlag(int n, boolean bl) {
        byte by = this.entityData.get(DATA_ID_FLAGS);
        if (bl) {
            this.entityData.set(DATA_ID_FLAGS, (byte)(by | n));
        } else {
            this.entityData.set(DATA_ID_FLAGS, (byte)(by & ~n));
        }
    }

    public boolean isTamed() {
        return this.getFlag(2);
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_ID_OWNER_UUID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID uUID) {
        this.entityData.set(DATA_ID_OWNER_UUID, Optional.ofNullable(uUID));
    }

    public boolean isJumping() {
        return this.isJumping;
    }

    public void setTamed(boolean bl) {
        this.setFlag(2, bl);
    }

    public void setIsJumping(boolean bl) {
        this.isJumping = bl;
    }

    @Override
    protected void onLeashDistance(float f) {
        if (f > 6.0f && this.isEating()) {
            this.setEating(false);
        }
    }

    public boolean isEating() {
        return this.getFlag(16);
    }

    public boolean isStanding() {
        return this.getFlag(32);
    }

    public boolean isBred() {
        return this.getFlag(8);
    }

    public void setBred(boolean bl) {
        this.setFlag(8, bl);
    }

    @Override
    public boolean isSaddleable() {
        return this.isAlive() && !this.isBaby() && this.isTamed();
    }

    @Override
    public void equipSaddle(@Nullable SoundSource soundSource) {
        this.inventory.setItem(0, new ItemStack(Items.SADDLE));
        if (soundSource != null) {
            this.level.playSound(null, this, SoundEvents.HORSE_SADDLE, soundSource, 0.5f, 1.0f);
        }
    }

    @Override
    public boolean isSaddled() {
        return this.getFlag(4);
    }

    public int getTemper() {
        return this.temper;
    }

    public void setTemper(int n) {
        this.temper = n;
    }

    public int modifyTemper(int n) {
        int n2 = Mth.clamp(this.getTemper() + n, 0, this.getMaxTemper());
        this.setTemper(n2);
        return n2;
    }

    @Override
    public boolean isPushable() {
        return !this.isVehicle();
    }

    private void eating() {
        SoundEvent soundEvent;
        this.openMouth();
        if (!this.isSilent() && (soundEvent = this.getEatingSound()) != null) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(), soundEvent, this.getSoundSource(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
    }

    @Override
    public boolean causeFallDamage(float f, float f2) {
        int n;
        if (f > 1.0f) {
            this.playSound(SoundEvents.HORSE_LAND, 0.4f, 1.0f);
        }
        if ((n = this.calculateFallDamage(f, f2)) <= 0) {
            return false;
        }
        this.hurt(DamageSource.FALL, n);
        if (this.isVehicle()) {
            for (Entity entity : this.getIndirectPassengers()) {
                entity.hurt(DamageSource.FALL, n);
            }
        }
        this.playBlockFallSound();
        return true;
    }

    @Override
    protected int calculateFallDamage(float f, float f2) {
        return Mth.ceil((f * 0.5f - 3.0f) * f2);
    }

    protected int getInventorySize() {
        return 2;
    }

    protected void createInventory() {
        SimpleContainer simpleContainer = this.inventory;
        this.inventory = new SimpleContainer(this.getInventorySize());
        if (simpleContainer != null) {
            simpleContainer.removeListener(this);
            int n = Math.min(simpleContainer.getContainerSize(), this.inventory.getContainerSize());
            for (int i = 0; i < n; ++i) {
                ItemStack itemStack = simpleContainer.getItem(i);
                if (itemStack.isEmpty()) continue;
                this.inventory.setItem(i, itemStack.copy());
            }
        }
        this.inventory.addListener(this);
        this.updateContainerEquipment();
    }

    protected void updateContainerEquipment() {
        if (this.level.isClientSide) {
            return;
        }
        this.setFlag(4, !this.inventory.getItem(0).isEmpty());
    }

    @Override
    public void containerChanged(Container container) {
        boolean bl = this.isSaddled();
        this.updateContainerEquipment();
        if (this.tickCount > 20 && !bl && this.isSaddled()) {
            this.playSound(SoundEvents.HORSE_SADDLE, 0.5f, 1.0f);
        }
    }

    public double getCustomJump() {
        return this.getAttributeValue(Attributes.JUMP_STRENGTH);
    }

    @Nullable
    protected SoundEvent getEatingSound() {
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        if (this.random.nextInt(3) == 0) {
            this.stand();
        }
        return null;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.random.nextInt(10) == 0 && !this.isImmobile()) {
            this.stand();
        }
        return null;
    }

    @Nullable
    protected SoundEvent getAngrySound() {
        this.stand();
        return null;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        if (blockState.getMaterial().isLiquid()) {
            return;
        }
        BlockState blockState2 = this.level.getBlockState(blockPos.above());
        SoundType soundType = blockState.getSoundType();
        if (blockState2.is(Blocks.SNOW)) {
            soundType = blockState2.getSoundType();
        }
        if (this.isVehicle() && this.canGallop) {
            ++this.gallopSoundCounter;
            if (this.gallopSoundCounter > 5 && this.gallopSoundCounter % 3 == 0) {
                this.playGallopSound(soundType);
            } else if (this.gallopSoundCounter <= 5) {
                this.playSound(SoundEvents.HORSE_STEP_WOOD, soundType.getVolume() * 0.15f, soundType.getPitch());
            }
        } else if (soundType == SoundType.WOOD) {
            this.playSound(SoundEvents.HORSE_STEP_WOOD, soundType.getVolume() * 0.15f, soundType.getPitch());
        } else {
            this.playSound(SoundEvents.HORSE_STEP, soundType.getVolume() * 0.15f, soundType.getPitch());
        }
    }

    protected void playGallopSound(SoundType soundType) {
        this.playSound(SoundEvents.HORSE_GALLOP, soundType.getVolume() * 0.15f, soundType.getPitch());
    }

    public static AttributeSupplier.Builder createBaseHorseAttributes() {
        return Mob.createMobAttributes().add(Attributes.JUMP_STRENGTH).add(Attributes.MAX_HEALTH, 53.0).add(Attributes.MOVEMENT_SPEED, 0.22499999403953552);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 6;
    }

    public int getMaxTemper() {
        return 100;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8f;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 400;
    }

    public void openInventory(Player player) {
        if (!this.level.isClientSide && (!this.isVehicle() || this.hasPassenger(player)) && this.isTamed()) {
            player.openHorseInventory(this, this.inventory);
        }
    }

    public InteractionResult fedFood(Player player, ItemStack itemStack) {
        boolean bl = this.handleEating(player, itemStack);
        if (!player.abilities.instabuild) {
            itemStack.shrink(1);
        }
        if (this.level.isClientSide) {
            return InteractionResult.CONSUME;
        }
        return bl ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    protected boolean handleEating(Player player, ItemStack itemStack) {
        boolean bl = false;
        float f = 0.0f;
        int n = 0;
        int n2 = 0;
        Item item = itemStack.getItem();
        if (item == Items.WHEAT) {
            f = 2.0f;
            n = 20;
            n2 = 3;
        } else if (item == Items.SUGAR) {
            f = 1.0f;
            n = 30;
            n2 = 3;
        } else if (item == Blocks.HAY_BLOCK.asItem()) {
            f = 20.0f;
            n = 180;
        } else if (item == Items.APPLE) {
            f = 3.0f;
            n = 60;
            n2 = 3;
        } else if (item == Items.GOLDEN_CARROT) {
            f = 4.0f;
            n = 60;
            n2 = 5;
            if (!this.level.isClientSide && this.isTamed() && this.getAge() == 0 && !this.isInLove()) {
                bl = true;
                this.setInLove(player);
            }
        } else if (item == Items.GOLDEN_APPLE || item == Items.ENCHANTED_GOLDEN_APPLE) {
            f = 10.0f;
            n = 240;
            n2 = 10;
            if (!this.level.isClientSide && this.isTamed() && this.getAge() == 0 && !this.isInLove()) {
                bl = true;
                this.setInLove(player);
            }
        }
        if (this.getHealth() < this.getMaxHealth() && f > 0.0f) {
            this.heal(f);
            bl = true;
        }
        if (this.isBaby() && n > 0) {
            this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
            if (!this.level.isClientSide) {
                this.ageUp(n);
            }
            bl = true;
        }
        if (n2 > 0 && (bl || !this.isTamed()) && this.getTemper() < this.getMaxTemper()) {
            bl = true;
            if (!this.level.isClientSide) {
                this.modifyTemper(n2);
            }
        }
        if (bl) {
            this.eating();
        }
        return bl;
    }

    protected void doPlayerRide(Player player) {
        this.setEating(false);
        this.setStanding(false);
        if (!this.level.isClientSide) {
            player.yRot = this.yRot;
            player.xRot = this.xRot;
            player.startRiding(this);
        }
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() && this.isVehicle() && this.isSaddled() || this.isEating() || this.isStanding();
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return FOOD_ITEMS.test(itemStack);
    }

    private void moveTail() {
        this.tailCounter = 1;
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.inventory == null) {
            return;
        }
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack itemStack = this.inventory.getItem(i);
            if (itemStack.isEmpty() || EnchantmentHelper.hasVanishingCurse(itemStack)) continue;
            this.spawnAtLocation(itemStack);
        }
    }

    @Override
    public void aiStep() {
        if (this.random.nextInt(200) == 0) {
            this.moveTail();
        }
        super.aiStep();
        if (this.level.isClientSide || !this.isAlive()) {
            return;
        }
        if (this.random.nextInt(900) == 0 && this.deathTime == 0) {
            this.heal(1.0f);
        }
        if (this.canEatGrass()) {
            if (!this.isEating() && !this.isVehicle() && this.random.nextInt(300) == 0 && this.level.getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK)) {
                this.setEating(true);
            }
            if (this.isEating() && ++this.eatingCounter > 50) {
                this.eatingCounter = 0;
                this.setEating(false);
            }
        }
        this.followMommy();
    }

    protected void followMommy() {
        AbstractHorse abstractHorse;
        if (this.isBred() && this.isBaby() && !this.isEating() && (abstractHorse = this.level.getNearestEntity(AbstractHorse.class, MOMMY_TARGETING, this, this.getX(), this.getY(), this.getZ(), this.getBoundingBox().inflate(16.0))) != null && this.distanceToSqr(abstractHorse) > 4.0) {
            this.navigation.createPath(abstractHorse, 0);
        }
    }

    public boolean canEatGrass() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.mouthCounter > 0 && ++this.mouthCounter > 30) {
            this.mouthCounter = 0;
            this.setFlag(64, false);
        }
        if ((this.isControlledByLocalInstance() || this.isEffectiveAi()) && this.standCounter > 0 && ++this.standCounter > 20) {
            this.standCounter = 0;
            this.setStanding(false);
        }
        if (this.tailCounter > 0 && ++this.tailCounter > 8) {
            this.tailCounter = 0;
        }
        if (this.sprintCounter > 0) {
            ++this.sprintCounter;
            if (this.sprintCounter > 300) {
                this.sprintCounter = 0;
            }
        }
        this.eatAnimO = this.eatAnim;
        if (this.isEating()) {
            this.eatAnim += (1.0f - this.eatAnim) * 0.4f + 0.05f;
            if (this.eatAnim > 1.0f) {
                this.eatAnim = 1.0f;
            }
        } else {
            this.eatAnim += (0.0f - this.eatAnim) * 0.4f - 0.05f;
            if (this.eatAnim < 0.0f) {
                this.eatAnim = 0.0f;
            }
        }
        this.standAnimO = this.standAnim;
        if (this.isStanding()) {
            this.eatAnimO = this.eatAnim = 0.0f;
            this.standAnim += (1.0f - this.standAnim) * 0.4f + 0.05f;
            if (this.standAnim > 1.0f) {
                this.standAnim = 1.0f;
            }
        } else {
            this.allowStandSliding = false;
            this.standAnim += (0.8f * this.standAnim * this.standAnim * this.standAnim - this.standAnim) * 0.6f - 0.05f;
            if (this.standAnim < 0.0f) {
                this.standAnim = 0.0f;
            }
        }
        this.mouthAnimO = this.mouthAnim;
        if (this.getFlag(64)) {
            this.mouthAnim += (1.0f - this.mouthAnim) * 0.7f + 0.05f;
            if (this.mouthAnim > 1.0f) {
                this.mouthAnim = 1.0f;
            }
        } else {
            this.mouthAnim += (0.0f - this.mouthAnim) * 0.7f - 0.05f;
            if (this.mouthAnim < 0.0f) {
                this.mouthAnim = 0.0f;
            }
        }
    }

    private void openMouth() {
        if (!this.level.isClientSide) {
            this.mouthCounter = 1;
            this.setFlag(64, true);
        }
    }

    public void setEating(boolean bl) {
        this.setFlag(16, bl);
    }

    public void setStanding(boolean bl) {
        if (bl) {
            this.setEating(false);
        }
        this.setFlag(32, bl);
    }

    private void stand() {
        if (this.isControlledByLocalInstance() || this.isEffectiveAi()) {
            this.standCounter = 1;
            this.setStanding(true);
        }
    }

    public void makeMad() {
        if (!this.isStanding()) {
            this.stand();
            SoundEvent soundEvent = this.getAngrySound();
            if (soundEvent != null) {
                this.playSound(soundEvent, this.getSoundVolume(), this.getVoicePitch());
            }
        }
    }

    public boolean tameWithName(Player player) {
        this.setOwnerUUID(player.getUUID());
        this.setTamed(true);
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)player, this);
        }
        this.level.broadcastEntityEvent(this, (byte)7);
        return true;
    }

    @Override
    public void travel(Vec3 vec3) {
        if (!this.isAlive()) {
            return;
        }
        if (!(this.isVehicle() && this.canBeControlledByRider() && this.isSaddled())) {
            this.flyingSpeed = 0.02f;
            super.travel(vec3);
            return;
        }
        LivingEntity livingEntity = (LivingEntity)this.getControllingPassenger();
        this.yRotO = this.yRot = livingEntity.yRot;
        this.xRot = livingEntity.xRot * 0.5f;
        this.setRot(this.yRot, this.xRot);
        this.yHeadRot = this.yBodyRot = this.yRot;
        float f = livingEntity.xxa * 0.5f;
        float f2 = livingEntity.zza;
        if (f2 <= 0.0f) {
            f2 *= 0.25f;
            this.gallopSoundCounter = 0;
        }
        if (this.onGround && this.playerJumpPendingScale == 0.0f && this.isStanding() && !this.allowStandSliding) {
            f = 0.0f;
            f2 = 0.0f;
        }
        if (this.playerJumpPendingScale > 0.0f && !this.isJumping() && this.onGround) {
            double d = this.getCustomJump() * (double)this.playerJumpPendingScale * (double)this.getBlockJumpFactor();
            double d2 = this.hasEffect(MobEffects.JUMP) ? d + (double)((float)(this.getEffect(MobEffects.JUMP).getAmplifier() + 1) * 0.1f) : d;
            Vec3 vec32 = this.getDeltaMovement();
            this.setDeltaMovement(vec32.x, d2, vec32.z);
            this.setIsJumping(true);
            this.hasImpulse = true;
            if (f2 > 0.0f) {
                float f3 = Mth.sin(this.yRot * 0.017453292f);
                float f4 = Mth.cos(this.yRot * 0.017453292f);
                this.setDeltaMovement(this.getDeltaMovement().add(-0.4f * f3 * this.playerJumpPendingScale, 0.0, 0.4f * f4 * this.playerJumpPendingScale));
            }
            this.playerJumpPendingScale = 0.0f;
        }
        this.flyingSpeed = this.getSpeed() * 0.1f;
        if (this.isControlledByLocalInstance()) {
            this.setSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
            super.travel(new Vec3(f, vec3.y, f2));
        } else if (livingEntity instanceof Player) {
            this.setDeltaMovement(Vec3.ZERO);
        }
        if (this.onGround) {
            this.playerJumpPendingScale = 0.0f;
            this.setIsJumping(false);
        }
        this.calculateEntityAnimation(this, false);
    }

    protected void playJumpSound() {
        this.playSound(SoundEvents.HORSE_JUMP, 0.4f, 1.0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("EatingHaystack", this.isEating());
        compoundTag.putBoolean("Bred", this.isBred());
        compoundTag.putInt("Temper", this.getTemper());
        compoundTag.putBoolean("Tame", this.isTamed());
        if (this.getOwnerUUID() != null) {
            compoundTag.putUUID("Owner", this.getOwnerUUID());
        }
        if (!this.inventory.getItem(0).isEmpty()) {
            compoundTag.put("SaddleItem", this.inventory.getItem(0).save(new CompoundTag()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        UUID uUID;
        Object object;
        super.readAdditionalSaveData(compoundTag);
        this.setEating(compoundTag.getBoolean("EatingHaystack"));
        this.setBred(compoundTag.getBoolean("Bred"));
        this.setTemper(compoundTag.getInt("Temper"));
        this.setTamed(compoundTag.getBoolean("Tame"));
        if (compoundTag.hasUUID("Owner")) {
            uUID = compoundTag.getUUID("Owner");
        } else {
            object = compoundTag.getString("Owner");
            uUID = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), (String)object);
        }
        if (uUID != null) {
            this.setOwnerUUID(uUID);
        }
        if (compoundTag.contains("SaddleItem", 10) && ((ItemStack)(object = ItemStack.of(compoundTag.getCompound("SaddleItem")))).getItem() == Items.SADDLE) {
            this.inventory.setItem(0, (ItemStack)object);
        }
        this.updateContainerEquipment();
    }

    @Override
    public boolean canMate(Animal animal) {
        return false;
    }

    protected boolean canParent() {
        return !this.isVehicle() && !this.isPassenger() && this.isTamed() && !this.isBaby() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
    }

    @Nullable
    @Override
    public AgableMob getBreedOffspring(ServerLevel serverLevel, AgableMob agableMob) {
        return null;
    }

    protected void setOffspringAttributes(AgableMob agableMob, AbstractHorse abstractHorse) {
        double d = this.getAttributeBaseValue(Attributes.MAX_HEALTH) + agableMob.getAttributeBaseValue(Attributes.MAX_HEALTH) + (double)this.generateRandomMaxHealth();
        abstractHorse.getAttribute(Attributes.MAX_HEALTH).setBaseValue(d / 3.0);
        double d2 = this.getAttributeBaseValue(Attributes.JUMP_STRENGTH) + agableMob.getAttributeBaseValue(Attributes.JUMP_STRENGTH) + this.generateRandomJumpStrength();
        abstractHorse.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(d2 / 3.0);
        double d3 = this.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) + agableMob.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) + this.generateRandomSpeed();
        abstractHorse.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(d3 / 3.0);
    }

    @Override
    public boolean canBeControlledByRider() {
        return this.getControllingPassenger() instanceof LivingEntity;
    }

    public float getEatAnim(float f) {
        return Mth.lerp(f, this.eatAnimO, this.eatAnim);
    }

    public float getStandAnim(float f) {
        return Mth.lerp(f, this.standAnimO, this.standAnim);
    }

    public float getMouthAnim(float f) {
        return Mth.lerp(f, this.mouthAnimO, this.mouthAnim);
    }

    @Override
    public void onPlayerJump(int n) {
        if (!this.isSaddled()) {
            return;
        }
        if (n < 0) {
            n = 0;
        } else {
            this.allowStandSliding = true;
            this.stand();
        }
        this.playerJumpPendingScale = n >= 90 ? 1.0f : 0.4f + 0.4f * (float)n / 90.0f;
    }

    @Override
    public boolean canJump() {
        return this.isSaddled();
    }

    @Override
    public void handleStartJump(int n) {
        this.allowStandSliding = true;
        this.stand();
        this.playJumpSound();
    }

    @Override
    public void handleStopJump() {
    }

    protected void spawnTamingParticles(boolean bl) {
        SimpleParticleType simpleParticleType = bl ? ParticleTypes.HEART : ParticleTypes.SMOKE;
        for (int i = 0; i < 7; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            double d3 = this.random.nextGaussian() * 0.02;
            this.level.addParticle(simpleParticleType, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d, d2, d3);
        }
    }

    @Override
    public void handleEntityEvent(byte by) {
        if (by == 7) {
            this.spawnTamingParticles(true);
        } else if (by == 6) {
            this.spawnTamingParticles(false);
        } else {
            super.handleEntityEvent(by);
        }
    }

    @Override
    public void positionRider(Entity entity) {
        super.positionRider(entity);
        if (entity instanceof Mob) {
            Mob mob = (Mob)entity;
            this.yBodyRot = mob.yBodyRot;
        }
        if (this.standAnimO > 0.0f) {
            float f = Mth.sin(this.yBodyRot * 0.017453292f);
            float f2 = Mth.cos(this.yBodyRot * 0.017453292f);
            float f3 = 0.7f * this.standAnimO;
            float f4 = 0.15f * this.standAnimO;
            entity.setPos(this.getX() + (double)(f3 * f), this.getY() + this.getPassengersRidingOffset() + entity.getMyRidingOffset() + (double)f4, this.getZ() - (double)(f3 * f2));
            if (entity instanceof LivingEntity) {
                ((LivingEntity)entity).yBodyRot = this.yBodyRot;
            }
        }
    }

    protected float generateRandomMaxHealth() {
        return 15.0f + (float)this.random.nextInt(8) + (float)this.random.nextInt(9);
    }

    protected double generateRandomJumpStrength() {
        return 0.4000000059604645 + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2;
    }

    protected double generateRandomSpeed() {
        return (0.44999998807907104 + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3) * 0.25;
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return entityDimensions.height * 0.95f;
    }

    public boolean canWearArmor() {
        return false;
    }

    public boolean isWearingArmor() {
        return !this.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
    }

    public boolean isArmor(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean setSlot(int n, ItemStack itemStack) {
        int n2 = n - 400;
        if (n2 >= 0 && n2 < 2 && n2 < this.inventory.getContainerSize()) {
            if (n2 == 0 && itemStack.getItem() != Items.SADDLE) {
                return false;
            }
            if (!(n2 != 1 || this.canWearArmor() && this.isArmor(itemStack))) {
                return false;
            }
            this.inventory.setItem(n2, itemStack);
            this.updateContainerEquipment();
            return true;
        }
        int n3 = n - 500 + 2;
        if (n3 >= 2 && n3 < this.inventory.getContainerSize()) {
            this.inventory.setItem(n3, itemStack);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Entity getControllingPassenger() {
        if (this.getPassengers().isEmpty()) {
            return null;
        }
        return this.getPassengers().get(0);
    }

    @Nullable
    private Vec3 getDismountLocationInDirection(Vec3 vec3, LivingEntity livingEntity) {
        double d = this.getX() + vec3.x;
        double d2 = this.getBoundingBox().minY;
        double d3 = this.getZ() + vec3.z;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        block0 : for (Pose pose : livingEntity.getDismountPoses()) {
            mutableBlockPos.set(d, d2, d3);
            double d4 = this.getBoundingBox().maxY + 0.75;
            do {
                AABB aABB;
                Vec3 vec32;
                double d5 = this.level.getBlockFloorHeight(mutableBlockPos);
                if ((double)mutableBlockPos.getY() + d5 > d4) continue block0;
                if (DismountHelper.isBlockFloorValid(d5) && DismountHelper.canDismountTo(this.level, livingEntity, (aABB = livingEntity.getLocalBoundsForPose(pose)).move(vec32 = new Vec3(d, (double)mutableBlockPos.getY() + d5, d3)))) {
                    livingEntity.setPose(pose);
                    return vec32;
                }
                mutableBlockPos.move(Direction.UP);
            } while ((double)mutableBlockPos.getY() < d4);
        }
        return null;
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
        Vec3 vec3 = AbstractHorse.getCollisionHorizontalEscapeVector(this.getBbWidth(), livingEntity.getBbWidth(), this.yRot + (livingEntity.getMainArm() == HumanoidArm.RIGHT ? 90.0f : -90.0f));
        Vec3 vec32 = this.getDismountLocationInDirection(vec3, livingEntity);
        if (vec32 != null) {
            return vec32;
        }
        Vec3 vec33 = AbstractHorse.getCollisionHorizontalEscapeVector(this.getBbWidth(), livingEntity.getBbWidth(), this.yRot + (livingEntity.getMainArm() == HumanoidArm.LEFT ? 90.0f : -90.0f));
        Vec3 vec34 = this.getDismountLocationInDirection(vec33, livingEntity);
        if (vec34 != null) {
            return vec34;
        }
        return this.position();
    }

    protected void randomizeAttributes() {
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        if (spawnGroupData == null) {
            spawnGroupData = new AgableMob.AgableMobGroupData(0.2f);
        }
        this.randomizeAttributes();
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }
}

