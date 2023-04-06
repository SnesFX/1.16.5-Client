/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.entity;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.EntityHurtPlayerTrigger;
import net.minecraft.advancements.critereon.ItemPickedUpByEntityTrigger;
import net.minecraft.advancements.critereon.PlayerHurtEntityTrigger;
import net.minecraft.advancements.critereon.UsedTotemTrigger;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddMobPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HoneyBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.apache.logging.log4j.Logger;

public abstract class LivingEntity
extends Entity {
    private static final UUID SPEED_MODIFIER_SPRINTING_UUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final UUID SPEED_MODIFIER_SOUL_SPEED_UUID = UUID.fromString("87f46a96-686f-4796-b035-22e16ee9e038");
    private static final AttributeModifier SPEED_MODIFIER_SPRINTING = new AttributeModifier(SPEED_MODIFIER_SPRINTING_UUID, "Sprinting speed boost", 0.30000001192092896, AttributeModifier.Operation.MULTIPLY_TOTAL);
    protected static final EntityDataAccessor<Byte> DATA_LIVING_ENTITY_FLAGS = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Float> DATA_HEALTH_ID = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_EFFECT_COLOR_ID = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_EFFECT_AMBIENCE_ID = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ARROW_COUNT_ID = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_STINGER_COUNT_ID = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<BlockPos>> SLEEPING_POS_ID = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    protected static final EntityDimensions SLEEPING_DIMENSIONS = EntityDimensions.fixed(0.2f, 0.2f);
    private final AttributeMap attributes;
    private final CombatTracker combatTracker = new CombatTracker(this);
    private final Map<MobEffect, MobEffectInstance> activeEffects = Maps.newHashMap();
    private final NonNullList<ItemStack> lastHandItemStacks = NonNullList.withSize(2, ItemStack.EMPTY);
    private final NonNullList<ItemStack> lastArmorItemStacks = NonNullList.withSize(4, ItemStack.EMPTY);
    public boolean swinging;
    public InteractionHand swingingArm;
    public int swingTime;
    public int removeArrowTime;
    public int removeStingerTime;
    public int hurtTime;
    public int hurtDuration;
    public float hurtDir;
    public int deathTime;
    public float oAttackAnim;
    public float attackAnim;
    protected int attackStrengthTicker;
    public float animationSpeedOld;
    public float animationSpeed;
    public float animationPosition;
    public final int invulnerableDuration = 20;
    public final float timeOffs;
    public final float rotA;
    public float yBodyRot;
    public float yBodyRotO;
    public float yHeadRot;
    public float yHeadRotO;
    public float flyingSpeed = 0.02f;
    @Nullable
    protected Player lastHurtByPlayer;
    protected int lastHurtByPlayerTime;
    protected boolean dead;
    protected int noActionTime;
    protected float oRun;
    protected float run;
    protected float animStep;
    protected float animStepO;
    protected float rotOffs;
    protected int deathScore;
    protected float lastHurt;
    protected boolean jumping;
    public float xxa;
    public float yya;
    public float zza;
    protected int lerpSteps;
    protected double lerpX;
    protected double lerpY;
    protected double lerpZ;
    protected double lerpYRot;
    protected double lerpXRot;
    protected double lyHeadRot;
    protected int lerpHeadSteps;
    private boolean effectsDirty = true;
    @Nullable
    private LivingEntity lastHurtByMob;
    private int lastHurtByMobTimestamp;
    private LivingEntity lastHurtMob;
    private int lastHurtMobTimestamp;
    private float speed;
    private int noJumpDelay;
    private float absorptionAmount;
    protected ItemStack useItem = ItemStack.EMPTY;
    protected int useItemRemaining;
    protected int fallFlyTicks;
    private BlockPos lastPos;
    private Optional<BlockPos> lastClimbablePos = Optional.empty();
    private DamageSource lastDamageSource;
    private long lastDamageStamp;
    protected int autoSpinAttackTicks;
    private float swimAmount;
    private float swimAmountO;
    protected Brain<?> brain;

    protected LivingEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        this.attributes = new AttributeMap(DefaultAttributes.getSupplier(entityType));
        this.setHealth(this.getMaxHealth());
        this.blocksBuilding = true;
        this.rotA = (float)((Math.random() + 1.0) * 0.009999999776482582);
        this.reapplyPosition();
        this.timeOffs = (float)Math.random() * 12398.0f;
        this.yHeadRot = this.yRot = (float)(Math.random() * 6.2831854820251465);
        this.maxUpStep = 0.6f;
        NbtOps nbtOps = NbtOps.INSTANCE;
        this.brain = this.makeBrain(new Dynamic((DynamicOps)nbtOps, nbtOps.createMap((Map)ImmutableMap.of((Object)nbtOps.createString("memories"), (Object)nbtOps.emptyMap()))));
    }

    public Brain<?> getBrain() {
        return this.brain;
    }

    protected Brain.Provider<?> brainProvider() {
        return Brain.provider(ImmutableList.of(), ImmutableList.of());
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return this.brainProvider().makeBrain(dynamic);
    }

    @Override
    public void kill() {
        this.hurt(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
    }

    public boolean canAttackType(EntityType<?> entityType) {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_LIVING_ENTITY_FLAGS, (byte)0);
        this.entityData.define(DATA_EFFECT_COLOR_ID, 0);
        this.entityData.define(DATA_EFFECT_AMBIENCE_ID, false);
        this.entityData.define(DATA_ARROW_COUNT_ID, 0);
        this.entityData.define(DATA_STINGER_COUNT_ID, 0);
        this.entityData.define(DATA_HEALTH_ID, Float.valueOf(1.0f));
        this.entityData.define(SLEEPING_POS_ID, Optional.empty());
    }

    public static AttributeSupplier.Builder createLivingAttributes() {
        return AttributeSupplier.builder().add(Attributes.MAX_HEALTH).add(Attributes.KNOCKBACK_RESISTANCE).add(Attributes.MOVEMENT_SPEED).add(Attributes.ARMOR).add(Attributes.ARMOR_TOUGHNESS);
    }

    @Override
    protected void checkFallDamage(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
        if (!this.isInWater()) {
            this.updateInWaterStateAndDoWaterCurrentPushing();
        }
        if (!this.level.isClientSide && bl && this.fallDistance > 0.0f) {
            this.removeSoulSpeed();
            this.tryAddSoulSpeed();
        }
        if (!this.level.isClientSide && this.fallDistance > 3.0f && bl) {
            float f = Mth.ceil(this.fallDistance - 3.0f);
            if (!blockState.isAir()) {
                double d2 = Math.min((double)(0.2f + f / 15.0f), 2.5);
                int n = (int)(150.0 * d2);
                ((ServerLevel)this.level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState), this.getX(), this.getY(), this.getZ(), n, 0.0, 0.0, 0.0, 0.15000000596046448);
            }
        }
        super.checkFallDamage(d, bl, blockState, blockPos);
    }

    public boolean canBreatheUnderwater() {
        return this.getMobType() == MobType.UNDEAD;
    }

    public float getSwimAmount(float f) {
        return Mth.lerp(f, this.swimAmountO, this.swimAmount);
    }

    @Override
    public void baseTick() {
        boolean bl;
        this.oAttackAnim = this.attackAnim;
        if (this.firstTick) {
            this.getSleepingPos().ifPresent(this::setPosToBed);
        }
        if (this.canSpawnSoulSpeedParticle()) {
            this.spawnSoulSpeedParticle();
        }
        super.baseTick();
        this.level.getProfiler().push("livingEntityBaseTick");
        boolean bl2 = this instanceof Player;
        if (this.isAlive()) {
            double d;
            double d2;
            if (this.isInWall()) {
                this.hurt(DamageSource.IN_WALL, 1.0f);
            } else if (bl2 && !this.level.getWorldBorder().isWithinBounds(this.getBoundingBox()) && (d = this.level.getWorldBorder().getDistanceToBorder(this) + this.level.getWorldBorder().getDamageSafeZone()) < 0.0 && (d2 = this.level.getWorldBorder().getDamagePerBlock()) > 0.0) {
                this.hurt(DamageSource.IN_WALL, Math.max(1, Mth.floor(-d * d2)));
            }
        }
        if (this.fireImmune() || this.level.isClientSide) {
            this.clearFire();
        }
        boolean bl3 = bl = bl2 && ((Player)this).abilities.invulnerable;
        if (this.isAlive()) {
            Object object;
            if (this.isEyeInFluid(FluidTags.WATER) && !this.level.getBlockState(new BlockPos(this.getX(), this.getEyeY(), this.getZ())).is(Blocks.BUBBLE_COLUMN)) {
                if (!(this.canBreatheUnderwater() || MobEffectUtil.hasWaterBreathing(this) || bl)) {
                    this.setAirSupply(this.decreaseAirSupply(this.getAirSupply()));
                    if (this.getAirSupply() == -20) {
                        this.setAirSupply(0);
                        object = this.getDeltaMovement();
                        for (int i = 0; i < 8; ++i) {
                            double d = this.random.nextDouble() - this.random.nextDouble();
                            double d3 = this.random.nextDouble() - this.random.nextDouble();
                            double d4 = this.random.nextDouble() - this.random.nextDouble();
                            this.level.addParticle(ParticleTypes.BUBBLE, this.getX() + d, this.getY() + d3, this.getZ() + d4, ((Vec3)object).x, ((Vec3)object).y, ((Vec3)object).z);
                        }
                        this.hurt(DamageSource.DROWN, 2.0f);
                    }
                }
                if (!this.level.isClientSide && this.isPassenger() && this.getVehicle() != null && !this.getVehicle().rideableUnderWater()) {
                    this.stopRiding();
                }
            } else if (this.getAirSupply() < this.getMaxAirSupply()) {
                this.setAirSupply(this.increaseAirSupply(this.getAirSupply()));
            }
            if (!this.level.isClientSide && !Objects.equal((Object)this.lastPos, (Object)(object = this.blockPosition()))) {
                this.lastPos = object;
                this.onChangedBlock((BlockPos)object);
            }
        }
        if (this.isAlive() && this.isInWaterRainOrBubble()) {
            this.clearFire();
        }
        if (this.hurtTime > 0) {
            --this.hurtTime;
        }
        if (this.invulnerableTime > 0 && !(this instanceof ServerPlayer)) {
            --this.invulnerableTime;
        }
        if (this.isDeadOrDying()) {
            this.tickDeath();
        }
        if (this.lastHurtByPlayerTime > 0) {
            --this.lastHurtByPlayerTime;
        } else {
            this.lastHurtByPlayer = null;
        }
        if (this.lastHurtMob != null && !this.lastHurtMob.isAlive()) {
            this.lastHurtMob = null;
        }
        if (this.lastHurtByMob != null) {
            if (!this.lastHurtByMob.isAlive()) {
                this.setLastHurtByMob(null);
            } else if (this.tickCount - this.lastHurtByMobTimestamp > 100) {
                this.setLastHurtByMob(null);
            }
        }
        this.tickEffects();
        this.animStepO = this.animStep;
        this.yBodyRotO = this.yBodyRot;
        this.yHeadRotO = this.yHeadRot;
        this.yRotO = this.yRot;
        this.xRotO = this.xRot;
        this.level.getProfiler().pop();
    }

    public boolean canSpawnSoulSpeedParticle() {
        return this.tickCount % 5 == 0 && this.getDeltaMovement().x != 0.0 && this.getDeltaMovement().z != 0.0 && !this.isSpectator() && EnchantmentHelper.hasSoulSpeed(this) && this.onSoulSpeedBlock();
    }

    protected void spawnSoulSpeedParticle() {
        Vec3 vec3 = this.getDeltaMovement();
        this.level.addParticle(ParticleTypes.SOUL, this.getX() + (this.random.nextDouble() - 0.5) * (double)this.getBbWidth(), this.getY() + 0.1, this.getZ() + (this.random.nextDouble() - 0.5) * (double)this.getBbWidth(), vec3.x * -0.2, 0.1, vec3.z * -0.2);
        float f = this.random.nextFloat() * 0.4f + this.random.nextFloat() > 0.9f ? 0.6f : 0.0f;
        this.playSound(SoundEvents.SOUL_ESCAPE, f, 0.6f + this.random.nextFloat() * 0.4f);
    }

    protected boolean onSoulSpeedBlock() {
        return this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).is(BlockTags.SOUL_SPEED_BLOCKS);
    }

    @Override
    protected float getBlockSpeedFactor() {
        if (this.onSoulSpeedBlock() && EnchantmentHelper.getEnchantmentLevel(Enchantments.SOUL_SPEED, this) > 0) {
            return 1.0f;
        }
        return super.getBlockSpeedFactor();
    }

    protected boolean shouldRemoveSoulSpeed(BlockState blockState) {
        return !blockState.isAir() || this.isFallFlying();
    }

    protected void removeSoulSpeed() {
        AttributeInstance attributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attributeInstance == null) {
            return;
        }
        if (attributeInstance.getModifier(SPEED_MODIFIER_SOUL_SPEED_UUID) != null) {
            attributeInstance.removeModifier(SPEED_MODIFIER_SOUL_SPEED_UUID);
        }
    }

    protected void tryAddSoulSpeed() {
        int n;
        if (!this.getBlockStateOn().isAir() && (n = EnchantmentHelper.getEnchantmentLevel(Enchantments.SOUL_SPEED, this)) > 0 && this.onSoulSpeedBlock()) {
            AttributeInstance attributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (attributeInstance == null) {
                return;
            }
            attributeInstance.addTransientModifier(new AttributeModifier(SPEED_MODIFIER_SOUL_SPEED_UUID, "Soul speed boost", (double)(0.03f * (1.0f + (float)n * 0.35f)), AttributeModifier.Operation.ADDITION));
            if (this.getRandom().nextFloat() < 0.04f) {
                ItemStack itemStack = this.getItemBySlot(EquipmentSlot.FEET);
                itemStack.hurtAndBreak(1, this, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.FEET));
            }
        }
    }

    protected void onChangedBlock(BlockPos blockPos) {
        int n = EnchantmentHelper.getEnchantmentLevel(Enchantments.FROST_WALKER, this);
        if (n > 0) {
            FrostWalkerEnchantment.onEntityMoved(this, this.level, blockPos, n);
        }
        if (this.shouldRemoveSoulSpeed(this.getBlockStateOn())) {
            this.removeSoulSpeed();
        }
        this.tryAddSoulSpeed();
    }

    public boolean isBaby() {
        return false;
    }

    public float getScale() {
        return this.isBaby() ? 0.5f : 1.0f;
    }

    protected boolean isAffectedByFluids() {
        return true;
    }

    @Override
    public boolean rideableUnderWater() {
        return false;
    }

    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove();
            for (int i = 0; i < 20; ++i) {
                double d = this.random.nextGaussian() * 0.02;
                double d2 = this.random.nextGaussian() * 0.02;
                double d3 = this.random.nextGaussian() * 0.02;
                this.level.addParticle(ParticleTypes.POOF, this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0), d, d2, d3);
            }
        }
    }

    protected boolean shouldDropExperience() {
        return !this.isBaby();
    }

    protected boolean shouldDropLoot() {
        return !this.isBaby();
    }

    protected int decreaseAirSupply(int n) {
        int n2 = EnchantmentHelper.getRespiration(this);
        if (n2 > 0 && this.random.nextInt(n2 + 1) > 0) {
            return n;
        }
        return n - 1;
    }

    protected int increaseAirSupply(int n) {
        return Math.min(n + 4, this.getMaxAirSupply());
    }

    protected int getExperienceReward(Player player) {
        return 0;
    }

    protected boolean isAlwaysExperienceDropper() {
        return false;
    }

    public Random getRandom() {
        return this.random;
    }

    @Nullable
    public LivingEntity getLastHurtByMob() {
        return this.lastHurtByMob;
    }

    public int getLastHurtByMobTimestamp() {
        return this.lastHurtByMobTimestamp;
    }

    public void setLastHurtByPlayer(@Nullable Player player) {
        this.lastHurtByPlayer = player;
        this.lastHurtByPlayerTime = this.tickCount;
    }

    public void setLastHurtByMob(@Nullable LivingEntity livingEntity) {
        this.lastHurtByMob = livingEntity;
        this.lastHurtByMobTimestamp = this.tickCount;
    }

    @Nullable
    public LivingEntity getLastHurtMob() {
        return this.lastHurtMob;
    }

    public int getLastHurtMobTimestamp() {
        return this.lastHurtMobTimestamp;
    }

    public void setLastHurtMob(Entity entity) {
        this.lastHurtMob = entity instanceof LivingEntity ? (LivingEntity)entity : null;
        this.lastHurtMobTimestamp = this.tickCount;
    }

    public int getNoActionTime() {
        return this.noActionTime;
    }

    public void setNoActionTime(int n) {
        this.noActionTime = n;
    }

    protected void playEquipSound(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return;
        }
        SoundEvent soundEvent = SoundEvents.ARMOR_EQUIP_GENERIC;
        Item item = itemStack.getItem();
        if (item instanceof ArmorItem) {
            soundEvent = ((ArmorItem)item).getMaterial().getEquipSound();
        } else if (item == Items.ELYTRA) {
            soundEvent = SoundEvents.ARMOR_EQUIP_ELYTRA;
        }
        this.playSound(soundEvent, 1.0f, 1.0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        DataResult<net.minecraft.nbt.Tag> dataResult;
        compoundTag.putFloat("Health", this.getHealth());
        compoundTag.putShort("HurtTime", (short)this.hurtTime);
        compoundTag.putInt("HurtByTimestamp", this.lastHurtByMobTimestamp);
        compoundTag.putShort("DeathTime", (short)this.deathTime);
        compoundTag.putFloat("AbsorptionAmount", this.getAbsorptionAmount());
        compoundTag.put("Attributes", this.getAttributes().save());
        if (!this.activeEffects.isEmpty()) {
            dataResult = new DataResult<net.minecraft.nbt.Tag>();
            for (MobEffectInstance mobEffectInstance : this.activeEffects.values()) {
                dataResult.add(mobEffectInstance.save(new CompoundTag()));
            }
            compoundTag.put("ActiveEffects", (net.minecraft.nbt.Tag)dataResult);
        }
        compoundTag.putBoolean("FallFlying", this.isFallFlying());
        this.getSleepingPos().ifPresent(blockPos -> {
            compoundTag.putInt("SleepingX", blockPos.getX());
            compoundTag.putInt("SleepingY", blockPos.getY());
            compoundTag.putInt("SleepingZ", blockPos.getZ());
        });
        dataResult = this.brain.serializeStart(NbtOps.INSTANCE);
        dataResult.resultOrPartial(((Logger)LOGGER)::error).ifPresent(tag -> compoundTag.put("Brain", (net.minecraft.nbt.Tag)tag));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        Object object;
        this.setAbsorptionAmount(compoundTag.getFloat("AbsorptionAmount"));
        if (compoundTag.contains("Attributes", 9) && this.level != null && !this.level.isClientSide) {
            this.getAttributes().load(compoundTag.getList("Attributes", 10));
        }
        if (compoundTag.contains("ActiveEffects", 9)) {
            object = compoundTag.getList("ActiveEffects", 10);
            for (int i = 0; i < ((ListTag)object).size(); ++i) {
                CompoundTag compoundTag2 = ((ListTag)object).getCompound(i);
                MobEffectInstance mobEffectInstance = MobEffectInstance.load(compoundTag2);
                if (mobEffectInstance == null) continue;
                this.activeEffects.put(mobEffectInstance.getEffect(), mobEffectInstance);
            }
        }
        if (compoundTag.contains("Health", 99)) {
            this.setHealth(compoundTag.getFloat("Health"));
        }
        this.hurtTime = compoundTag.getShort("HurtTime");
        this.deathTime = compoundTag.getShort("DeathTime");
        this.lastHurtByMobTimestamp = compoundTag.getInt("HurtByTimestamp");
        if (compoundTag.contains("Team", 8)) {
            boolean bl;
            object = compoundTag.getString("Team");
            PlayerTeam playerTeam = this.level.getScoreboard().getPlayerTeam((String)object);
            boolean bl2 = bl = playerTeam != null && this.level.getScoreboard().addPlayerToTeam(this.getStringUUID(), playerTeam);
            if (!bl) {
                LOGGER.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", object);
            }
        }
        if (compoundTag.getBoolean("FallFlying")) {
            this.setSharedFlag(7, true);
        }
        if (compoundTag.contains("SleepingX", 99) && compoundTag.contains("SleepingY", 99) && compoundTag.contains("SleepingZ", 99)) {
            object = new BlockPos(compoundTag.getInt("SleepingX"), compoundTag.getInt("SleepingY"), compoundTag.getInt("SleepingZ"));
            this.setSleepingPos((BlockPos)object);
            this.entityData.set(DATA_POSE, Pose.SLEEPING);
            if (!this.firstTick) {
                this.setPosToBed((BlockPos)object);
            }
        }
        if (compoundTag.contains("Brain", 10)) {
            this.brain = this.makeBrain(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)compoundTag.get("Brain")));
        }
    }

    protected void tickEffects() {
        Iterator<MobEffect> iterator = this.activeEffects.keySet().iterator();
        try {
            while (iterator.hasNext()) {
                MobEffect mobEffect = iterator.next();
                MobEffectInstance mobEffectInstance = this.activeEffects.get(mobEffect);
                if (!mobEffectInstance.tick(this, () -> this.onEffectUpdated(mobEffectInstance, true))) {
                    if (this.level.isClientSide) continue;
                    iterator.remove();
                    this.onEffectRemoved(mobEffectInstance);
                    continue;
                }
                if (mobEffectInstance.getDuration() % 600 != 0) continue;
                this.onEffectUpdated(mobEffectInstance, false);
            }
        }
        catch (ConcurrentModificationException concurrentModificationException) {
            // empty catch block
        }
        if (this.effectsDirty) {
            if (!this.level.isClientSide) {
                this.updateInvisibilityStatus();
            }
            this.effectsDirty = false;
        }
        int n = this.entityData.get(DATA_EFFECT_COLOR_ID);
        boolean bl = this.entityData.get(DATA_EFFECT_AMBIENCE_ID);
        if (n > 0) {
            boolean bl2 = this.isInvisible() ? this.random.nextInt(15) == 0 : this.random.nextBoolean();
            if (bl) {
                bl2 &= this.random.nextInt(5) == 0;
            }
            if (bl2 && n > 0) {
                double d = (double)(n >> 16 & 0xFF) / 255.0;
                double d2 = (double)(n >> 8 & 0xFF) / 255.0;
                double d3 = (double)(n >> 0 & 0xFF) / 255.0;
                this.level.addParticle(bl ? ParticleTypes.AMBIENT_ENTITY_EFFECT : ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), d, d2, d3);
            }
        }
    }

    protected void updateInvisibilityStatus() {
        if (this.activeEffects.isEmpty()) {
            this.removeEffectParticles();
            this.setInvisible(false);
        } else {
            Collection<MobEffectInstance> collection = this.activeEffects.values();
            this.entityData.set(DATA_EFFECT_AMBIENCE_ID, LivingEntity.areAllEffectsAmbient(collection));
            this.entityData.set(DATA_EFFECT_COLOR_ID, PotionUtils.getColor(collection));
            this.setInvisible(this.hasEffect(MobEffects.INVISIBILITY));
        }
    }

    public double getVisibilityPercent(@Nullable Entity entity) {
        double d = 1.0;
        if (this.isDiscrete()) {
            d *= 0.8;
        }
        if (this.isInvisible()) {
            float f = this.getArmorCoverPercentage();
            if (f < 0.1f) {
                f = 0.1f;
            }
            d *= 0.7 * (double)f;
        }
        if (entity != null) {
            ItemStack itemStack = this.getItemBySlot(EquipmentSlot.HEAD);
            Item item = itemStack.getItem();
            EntityType<?> entityType = entity.getType();
            if (entityType == EntityType.SKELETON && item == Items.SKELETON_SKULL || entityType == EntityType.ZOMBIE && item == Items.ZOMBIE_HEAD || entityType == EntityType.CREEPER && item == Items.CREEPER_HEAD) {
                d *= 0.5;
            }
        }
        return d;
    }

    public boolean canAttack(LivingEntity livingEntity) {
        return true;
    }

    public boolean canAttack(LivingEntity livingEntity, TargetingConditions targetingConditions) {
        return targetingConditions.test(this, livingEntity);
    }

    public static boolean areAllEffectsAmbient(Collection<MobEffectInstance> collection) {
        for (MobEffectInstance mobEffectInstance : collection) {
            if (mobEffectInstance.isAmbient()) continue;
            return false;
        }
        return true;
    }

    protected void removeEffectParticles() {
        this.entityData.set(DATA_EFFECT_AMBIENCE_ID, false);
        this.entityData.set(DATA_EFFECT_COLOR_ID, 0);
    }

    public boolean removeAllEffects() {
        if (this.level.isClientSide) {
            return false;
        }
        Iterator<MobEffectInstance> iterator = this.activeEffects.values().iterator();
        boolean bl = false;
        while (iterator.hasNext()) {
            this.onEffectRemoved(iterator.next());
            iterator.remove();
            bl = true;
        }
        return bl;
    }

    public Collection<MobEffectInstance> getActiveEffects() {
        return this.activeEffects.values();
    }

    public Map<MobEffect, MobEffectInstance> getActiveEffectsMap() {
        return this.activeEffects;
    }

    public boolean hasEffect(MobEffect mobEffect) {
        return this.activeEffects.containsKey(mobEffect);
    }

    @Nullable
    public MobEffectInstance getEffect(MobEffect mobEffect) {
        return this.activeEffects.get(mobEffect);
    }

    public boolean addEffect(MobEffectInstance mobEffectInstance) {
        if (!this.canBeAffected(mobEffectInstance)) {
            return false;
        }
        MobEffectInstance mobEffectInstance2 = this.activeEffects.get(mobEffectInstance.getEffect());
        if (mobEffectInstance2 == null) {
            this.activeEffects.put(mobEffectInstance.getEffect(), mobEffectInstance);
            this.onEffectAdded(mobEffectInstance);
            return true;
        }
        if (mobEffectInstance2.update(mobEffectInstance)) {
            this.onEffectUpdated(mobEffectInstance2, true);
            return true;
        }
        return false;
    }

    public boolean canBeAffected(MobEffectInstance mobEffectInstance) {
        MobEffect mobEffect;
        return this.getMobType() != MobType.UNDEAD || (mobEffect = mobEffectInstance.getEffect()) != MobEffects.REGENERATION && mobEffect != MobEffects.POISON;
    }

    public void forceAddEffect(MobEffectInstance mobEffectInstance) {
        if (!this.canBeAffected(mobEffectInstance)) {
            return;
        }
        MobEffectInstance mobEffectInstance2 = this.activeEffects.put(mobEffectInstance.getEffect(), mobEffectInstance);
        if (mobEffectInstance2 == null) {
            this.onEffectAdded(mobEffectInstance);
        } else {
            this.onEffectUpdated(mobEffectInstance, true);
        }
    }

    public boolean isInvertedHealAndHarm() {
        return this.getMobType() == MobType.UNDEAD;
    }

    @Nullable
    public MobEffectInstance removeEffectNoUpdate(@Nullable MobEffect mobEffect) {
        return this.activeEffects.remove(mobEffect);
    }

    public boolean removeEffect(MobEffect mobEffect) {
        MobEffectInstance mobEffectInstance = this.removeEffectNoUpdate(mobEffect);
        if (mobEffectInstance != null) {
            this.onEffectRemoved(mobEffectInstance);
            return true;
        }
        return false;
    }

    protected void onEffectAdded(MobEffectInstance mobEffectInstance) {
        this.effectsDirty = true;
        if (!this.level.isClientSide) {
            mobEffectInstance.getEffect().addAttributeModifiers(this, this.getAttributes(), mobEffectInstance.getAmplifier());
        }
    }

    protected void onEffectUpdated(MobEffectInstance mobEffectInstance, boolean bl) {
        this.effectsDirty = true;
        if (bl && !this.level.isClientSide) {
            MobEffect mobEffect = mobEffectInstance.getEffect();
            mobEffect.removeAttributeModifiers(this, this.getAttributes(), mobEffectInstance.getAmplifier());
            mobEffect.addAttributeModifiers(this, this.getAttributes(), mobEffectInstance.getAmplifier());
        }
    }

    protected void onEffectRemoved(MobEffectInstance mobEffectInstance) {
        this.effectsDirty = true;
        if (!this.level.isClientSide) {
            mobEffectInstance.getEffect().removeAttributeModifiers(this, this.getAttributes(), mobEffectInstance.getAmplifier());
        }
    }

    public void heal(float f) {
        float f2 = this.getHealth();
        if (f2 > 0.0f) {
            this.setHealth(f2 + f);
        }
    }

    public float getHealth() {
        return this.entityData.get(DATA_HEALTH_ID).floatValue();
    }

    public void setHealth(float f) {
        this.entityData.set(DATA_HEALTH_ID, Float.valueOf(Mth.clamp(f, 0.0f, this.getMaxHealth())));
    }

    public boolean isDeadOrDying() {
        return this.getHealth() <= 0.0f;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        Object object;
        boolean bl;
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        if (this.level.isClientSide) {
            return false;
        }
        if (this.isDeadOrDying()) {
            return false;
        }
        if (damageSource.isFire() && this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            return false;
        }
        if (this.isSleeping() && !this.level.isClientSide) {
            this.stopSleeping();
        }
        this.noActionTime = 0;
        float f2 = f;
        if (!(damageSource != DamageSource.ANVIL && damageSource != DamageSource.FALLING_BLOCK || this.getItemBySlot(EquipmentSlot.HEAD).isEmpty())) {
            this.getItemBySlot(EquipmentSlot.HEAD).hurtAndBreak((int)(f * 4.0f + this.random.nextFloat() * f * 2.0f), this, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.HEAD));
            f *= 0.75f;
        }
        boolean bl2 = false;
        float f3 = 0.0f;
        if (f > 0.0f && this.isDamageSourceBlocked(damageSource)) {
            Entity entity;
            this.hurtCurrentlyUsedShield(f);
            f3 = f;
            f = 0.0f;
            if (!damageSource.isProjectile() && (entity = damageSource.getDirectEntity()) instanceof LivingEntity) {
                this.blockUsingShield((LivingEntity)entity);
            }
            bl2 = true;
        }
        this.animationSpeed = 1.5f;
        boolean bl3 = true;
        if ((float)this.invulnerableTime > 10.0f) {
            if (f <= this.lastHurt) {
                return false;
            }
            this.actuallyHurt(damageSource, f - this.lastHurt);
            this.lastHurt = f;
            bl3 = false;
        } else {
            this.lastHurt = f;
            this.invulnerableTime = 20;
            this.actuallyHurt(damageSource, f);
            this.hurtTime = this.hurtDuration = 10;
        }
        this.hurtDir = 0.0f;
        Entity entity = damageSource.getEntity();
        if (entity != null) {
            if (entity instanceof LivingEntity) {
                this.setLastHurtByMob((LivingEntity)entity);
            }
            if (entity instanceof Player) {
                this.lastHurtByPlayerTime = 100;
                this.lastHurtByPlayer = (Player)entity;
            } else if (entity instanceof Wolf && ((TamableAnimal)(object = (Wolf)entity)).isTame()) {
                this.lastHurtByPlayerTime = 100;
                LivingEntity livingEntity2 = ((TamableAnimal)object).getOwner();
                this.lastHurtByPlayer = livingEntity2 != null && livingEntity2.getType() == EntityType.PLAYER ? (Player)livingEntity2 : null;
            }
        }
        if (bl3) {
            if (bl2) {
                this.level.broadcastEntityEvent(this, (byte)29);
            } else if (damageSource instanceof EntityDamageSource && ((EntityDamageSource)damageSource).isThorns()) {
                this.level.broadcastEntityEvent(this, (byte)33);
            } else {
                int n = damageSource == DamageSource.DROWN ? 36 : (damageSource.isFire() ? 37 : (damageSource == DamageSource.SWEET_BERRY_BUSH ? 44 : 2));
                this.level.broadcastEntityEvent(this, (byte)n);
            }
            if (damageSource != DamageSource.DROWN && (!bl2 || f > 0.0f)) {
                this.markHurt();
            }
            if (entity != null) {
                double d = entity.getX() - this.getX();
                double d2 = entity.getZ() - this.getZ();
                while (d * d + d2 * d2 < 1.0E-4) {
                    d = (Math.random() - Math.random()) * 0.01;
                    d2 = (Math.random() - Math.random()) * 0.01;
                }
                this.hurtDir = (float)(Mth.atan2(d2, d) * 57.2957763671875 - (double)this.yRot);
                this.knockback(0.4f, d, d2);
            } else {
                this.hurtDir = (int)(Math.random() * 2.0) * 180;
            }
        }
        if (this.isDeadOrDying()) {
            if (!this.checkTotemDeathProtection(damageSource)) {
                object = this.getDeathSound();
                if (bl3 && object != null) {
                    this.playSound((SoundEvent)object, this.getSoundVolume(), this.getVoicePitch());
                }
                this.die(damageSource);
            }
        } else if (bl3) {
            this.playHurtSound(damageSource);
        }
        boolean bl4 = bl = !bl2 || f > 0.0f;
        if (bl) {
            this.lastDamageSource = damageSource;
            this.lastDamageStamp = this.level.getGameTime();
        }
        if (this instanceof ServerPlayer) {
            CriteriaTriggers.ENTITY_HURT_PLAYER.trigger((ServerPlayer)this, damageSource, f2, f, bl2);
            if (f3 > 0.0f && f3 < 3.4028235E37f) {
                ((ServerPlayer)this).awardStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(f3 * 10.0f));
            }
        }
        if (entity instanceof ServerPlayer) {
            CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((ServerPlayer)entity, this, damageSource, f2, f, bl2);
        }
        return bl;
    }

    protected void blockUsingShield(LivingEntity livingEntity) {
        livingEntity.blockedByShield(this);
    }

    protected void blockedByShield(LivingEntity livingEntity) {
        livingEntity.knockback(0.5f, livingEntity.getX() - this.getX(), livingEntity.getZ() - this.getZ());
    }

    private boolean checkTotemDeathProtection(DamageSource damageSource) {
        if (damageSource.isBypassInvul()) {
            return false;
        }
        ItemStack itemStack = null;
        for (InteractionHand interactionHand : InteractionHand.values()) {
            ItemStack itemStack2 = this.getItemInHand(interactionHand);
            if (itemStack2.getItem() != Items.TOTEM_OF_UNDYING) continue;
            itemStack = itemStack2.copy();
            itemStack2.shrink(1);
            break;
        }
        if (itemStack != null) {
            if (this instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)this;
                serverPlayer.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING));
                CriteriaTriggers.USED_TOTEM.trigger(serverPlayer, itemStack);
            }
            this.setHealth(1.0f);
            this.removeAllEffects();
            this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
            this.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
            this.level.broadcastEntityEvent(this, (byte)35);
        }
        return itemStack != null;
    }

    @Nullable
    public DamageSource getLastDamageSource() {
        if (this.level.getGameTime() - this.lastDamageStamp > 40L) {
            this.lastDamageSource = null;
        }
        return this.lastDamageSource;
    }

    protected void playHurtSound(DamageSource damageSource) {
        SoundEvent soundEvent = this.getHurtSound(damageSource);
        if (soundEvent != null) {
            this.playSound(soundEvent, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    private boolean isDamageSourceBlocked(DamageSource damageSource) {
        Object object;
        Entity entity = damageSource.getDirectEntity();
        boolean bl = false;
        if (entity instanceof AbstractArrow && ((AbstractArrow)(object = (AbstractArrow)entity)).getPierceLevel() > 0) {
            bl = true;
        }
        if (!damageSource.isBypassArmor() && this.isBlocking() && !bl && (object = damageSource.getSourcePosition()) != null) {
            Vec3 vec3 = this.getViewVector(1.0f);
            Vec3 vec32 = ((Vec3)object).vectorTo(this.position()).normalize();
            vec32 = new Vec3(vec32.x, 0.0, vec32.z);
            if (vec32.dot(vec3) < 0.0) {
                return true;
            }
        }
        return false;
    }

    private void breakItem(ItemStack itemStack) {
        if (!itemStack.isEmpty()) {
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_BREAK, this.getSoundSource(), 0.8f, 0.8f + this.level.random.nextFloat() * 0.4f, false);
            }
            this.spawnItemParticles(itemStack, 5);
        }
    }

    public void die(DamageSource damageSource) {
        if (this.removed || this.dead) {
            return;
        }
        Entity entity = damageSource.getEntity();
        LivingEntity livingEntity = this.getKillCredit();
        if (this.deathScore >= 0 && livingEntity != null) {
            livingEntity.awardKillScore(this, this.deathScore, damageSource);
        }
        if (this.isSleeping()) {
            this.stopSleeping();
        }
        this.dead = true;
        this.getCombatTracker().recheckStatus();
        if (this.level instanceof ServerLevel) {
            if (entity != null) {
                entity.killed((ServerLevel)this.level, this);
            }
            this.dropAllDeathLoot(damageSource);
            this.createWitherRose(livingEntity);
        }
        this.level.broadcastEntityEvent(this, (byte)3);
        this.setPose(Pose.DYING);
    }

    protected void createWitherRose(@Nullable LivingEntity livingEntity) {
        if (this.level.isClientSide) {
            return;
        }
        boolean bl = false;
        if (livingEntity instanceof WitherBoss) {
            Object object;
            if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                object = this.blockPosition();
                BlockState blockState = Blocks.WITHER_ROSE.defaultBlockState();
                if (this.level.getBlockState((BlockPos)object).isAir() && blockState.canSurvive(this.level, (BlockPos)object)) {
                    this.level.setBlock((BlockPos)object, blockState, 3);
                    bl = true;
                }
            }
            if (!bl) {
                object = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), new ItemStack(Items.WITHER_ROSE));
                this.level.addFreshEntity((Entity)object);
            }
        }
    }

    protected void dropAllDeathLoot(DamageSource damageSource) {
        boolean bl;
        Entity entity = damageSource.getEntity();
        int n = entity instanceof Player ? EnchantmentHelper.getMobLooting((LivingEntity)entity) : 0;
        boolean bl2 = bl = this.lastHurtByPlayerTime > 0;
        if (this.shouldDropLoot() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.dropFromLootTable(damageSource, bl);
            this.dropCustomDeathLoot(damageSource, n, bl);
        }
        this.dropEquipment();
        this.dropExperience();
    }

    protected void dropEquipment() {
    }

    protected void dropExperience() {
        if (!this.level.isClientSide && (this.isAlwaysExperienceDropper() || this.lastHurtByPlayerTime > 0 && this.shouldDropExperience() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))) {
            int n;
            for (int i = this.getExperienceReward((Player)this.lastHurtByPlayer); i > 0; i -= n) {
                n = ExperienceOrb.getExperienceValue(i);
                this.level.addFreshEntity(new ExperienceOrb(this.level, this.getX(), this.getY(), this.getZ(), n));
            }
        }
    }

    protected void dropCustomDeathLoot(DamageSource damageSource, int n, boolean bl) {
    }

    public ResourceLocation getLootTable() {
        return this.getType().getDefaultLootTable();
    }

    protected void dropFromLootTable(DamageSource damageSource, boolean bl) {
        ResourceLocation resourceLocation = this.getLootTable();
        LootTable lootTable = this.level.getServer().getLootTables().get(resourceLocation);
        LootContext.Builder builder = this.createLootContext(bl, damageSource);
        lootTable.getRandomItems(builder.create(LootContextParamSets.ENTITY), this::spawnAtLocation);
    }

    protected LootContext.Builder createLootContext(boolean bl, DamageSource damageSource) {
        LootContext.Builder builder = new LootContext.Builder((ServerLevel)this.level).withRandom(this.random).withParameter(LootContextParams.THIS_ENTITY, this).withParameter(LootContextParams.ORIGIN, this.position()).withParameter(LootContextParams.DAMAGE_SOURCE, damageSource).withOptionalParameter(LootContextParams.KILLER_ENTITY, damageSource.getEntity()).withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, damageSource.getDirectEntity());
        if (bl && this.lastHurtByPlayer != null) {
            builder = builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, this.lastHurtByPlayer).withLuck(this.lastHurtByPlayer.getLuck());
        }
        return builder;
    }

    public void knockback(float f, double d, double d2) {
        if ((f = (float)((double)f * (1.0 - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)))) <= 0.0f) {
            return;
        }
        this.hasImpulse = true;
        Vec3 vec3 = this.getDeltaMovement();
        Vec3 vec32 = new Vec3(d, 0.0, d2).normalize().scale(f);
        this.setDeltaMovement(vec3.x / 2.0 - vec32.x, this.onGround ? Math.min(0.4, vec3.y / 2.0 + (double)f) : vec3.y, vec3.z / 2.0 - vec32.z);
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.GENERIC_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }

    protected SoundEvent getFallDamageSound(int n) {
        if (n > 4) {
            return SoundEvents.GENERIC_BIG_FALL;
        }
        return SoundEvents.GENERIC_SMALL_FALL;
    }

    protected SoundEvent getDrinkingSound(ItemStack itemStack) {
        return itemStack.getDrinkingSound();
    }

    public SoundEvent getEatingSound(ItemStack itemStack) {
        return itemStack.getEatingSound();
    }

    @Override
    public void setOnGround(boolean bl) {
        super.setOnGround(bl);
        if (bl) {
            this.lastClimbablePos = Optional.empty();
        }
    }

    public Optional<BlockPos> getLastClimbablePos() {
        return this.lastClimbablePos;
    }

    public boolean onClimbable() {
        if (this.isSpectator()) {
            return false;
        }
        BlockPos blockPos = this.blockPosition();
        BlockState blockState = this.getFeetBlockState();
        Block block = blockState.getBlock();
        if (block.is(BlockTags.CLIMBABLE)) {
            this.lastClimbablePos = Optional.of(blockPos);
            return true;
        }
        if (block instanceof TrapDoorBlock && this.trapdoorUsableAsLadder(blockPos, blockState)) {
            this.lastClimbablePos = Optional.of(blockPos);
            return true;
        }
        return false;
    }

    public BlockState getFeetBlockState() {
        return this.level.getBlockState(this.blockPosition());
    }

    private boolean trapdoorUsableAsLadder(BlockPos blockPos, BlockState blockState) {
        BlockState blockState2;
        return blockState.getValue(TrapDoorBlock.OPEN) != false && (blockState2 = this.level.getBlockState(blockPos.below())).is(Blocks.LADDER) && blockState2.getValue(LadderBlock.FACING) == blockState.getValue(TrapDoorBlock.FACING);
    }

    @Override
    public boolean isAlive() {
        return !this.removed && this.getHealth() > 0.0f;
    }

    @Override
    public boolean causeFallDamage(float f, float f2) {
        boolean bl = super.causeFallDamage(f, f2);
        int n = this.calculateFallDamage(f, f2);
        if (n > 0) {
            this.playSound(this.getFallDamageSound(n), 1.0f, 1.0f);
            this.playBlockFallSound();
            this.hurt(DamageSource.FALL, n);
            return true;
        }
        return bl;
    }

    protected int calculateFallDamage(float f, float f2) {
        MobEffectInstance mobEffectInstance = this.getEffect(MobEffects.JUMP);
        float f3 = mobEffectInstance == null ? 0.0f : (float)(mobEffectInstance.getAmplifier() + 1);
        return Mth.ceil((f - 3.0f - f3) * f2);
    }

    protected void playBlockFallSound() {
        int n;
        int n2;
        if (this.isSilent()) {
            return;
        }
        int n3 = Mth.floor(this.getX());
        BlockState blockState = this.level.getBlockState(new BlockPos(n3, n = Mth.floor(this.getY() - 0.20000000298023224), n2 = Mth.floor(this.getZ())));
        if (!blockState.isAir()) {
            SoundType soundType = blockState.getSoundType();
            this.playSound(soundType.getFallSound(), soundType.getVolume() * 0.5f, soundType.getPitch() * 0.75f);
        }
    }

    @Override
    public void animateHurt() {
        this.hurtTime = this.hurtDuration = 10;
        this.hurtDir = 0.0f;
    }

    public int getArmorValue() {
        return Mth.floor(this.getAttributeValue(Attributes.ARMOR));
    }

    protected void hurtArmor(DamageSource damageSource, float f) {
    }

    protected void hurtCurrentlyUsedShield(float f) {
    }

    protected float getDamageAfterArmorAbsorb(DamageSource damageSource, float f) {
        if (!damageSource.isBypassArmor()) {
            this.hurtArmor(damageSource, f);
            f = CombatRules.getDamageAfterAbsorb(f, this.getArmorValue(), (float)this.getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        }
        return f;
    }

    protected float getDamageAfterMagicAbsorb(DamageSource damageSource, float f) {
        int n;
        if (damageSource.isBypassMagic()) {
            return f;
        }
        if (this.hasEffect(MobEffects.DAMAGE_RESISTANCE) && damageSource != DamageSource.OUT_OF_WORLD) {
            n = (this.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier() + 1) * 5;
            int n2 = 25 - n;
            float f2 = f * (float)n2;
            float f3 = f;
            f = Math.max(f2 / 25.0f, 0.0f);
            float f4 = f3 - f;
            if (f4 > 0.0f && f4 < 3.4028235E37f) {
                if (this instanceof ServerPlayer) {
                    ((ServerPlayer)this).awardStat(Stats.DAMAGE_RESISTED, Math.round(f4 * 10.0f));
                } else if (damageSource.getEntity() instanceof ServerPlayer) {
                    ((ServerPlayer)damageSource.getEntity()).awardStat(Stats.DAMAGE_DEALT_RESISTED, Math.round(f4 * 10.0f));
                }
            }
        }
        if (f <= 0.0f) {
            return 0.0f;
        }
        n = EnchantmentHelper.getDamageProtection(this.getArmorSlots(), damageSource);
        if (n > 0) {
            f = CombatRules.getDamageAfterMagicAbsorb(f, n);
        }
        return f;
    }

    protected void actuallyHurt(DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return;
        }
        f = this.getDamageAfterArmorAbsorb(damageSource, f);
        float f2 = f = this.getDamageAfterMagicAbsorb(damageSource, f);
        f = Math.max(f - this.getAbsorptionAmount(), 0.0f);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - (f2 - f));
        float f3 = f2 - f;
        if (f3 > 0.0f && f3 < 3.4028235E37f && damageSource.getEntity() instanceof ServerPlayer) {
            ((ServerPlayer)damageSource.getEntity()).awardStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(f3 * 10.0f));
        }
        if (f == 0.0f) {
            return;
        }
        float f4 = this.getHealth();
        this.setHealth(f4 - f);
        this.getCombatTracker().recordDamage(damageSource, f4, f);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - f);
    }

    public CombatTracker getCombatTracker() {
        return this.combatTracker;
    }

    @Nullable
    public LivingEntity getKillCredit() {
        if (this.combatTracker.getKiller() != null) {
            return this.combatTracker.getKiller();
        }
        if (this.lastHurtByPlayer != null) {
            return this.lastHurtByPlayer;
        }
        if (this.lastHurtByMob != null) {
            return this.lastHurtByMob;
        }
        return null;
    }

    public final float getMaxHealth() {
        return (float)this.getAttributeValue(Attributes.MAX_HEALTH);
    }

    public final int getArrowCount() {
        return this.entityData.get(DATA_ARROW_COUNT_ID);
    }

    public final void setArrowCount(int n) {
        this.entityData.set(DATA_ARROW_COUNT_ID, n);
    }

    public final int getStingerCount() {
        return this.entityData.get(DATA_STINGER_COUNT_ID);
    }

    public final void setStingerCount(int n) {
        this.entityData.set(DATA_STINGER_COUNT_ID, n);
    }

    private int getCurrentSwingDuration() {
        if (MobEffectUtil.hasDigSpeed(this)) {
            return 6 - (1 + MobEffectUtil.getDigSpeedAmplification(this));
        }
        if (this.hasEffect(MobEffects.DIG_SLOWDOWN)) {
            return 6 + (1 + this.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) * 2;
        }
        return 6;
    }

    public void swing(InteractionHand interactionHand) {
        this.swing(interactionHand, false);
    }

    public void swing(InteractionHand interactionHand, boolean bl) {
        if (!this.swinging || this.swingTime >= this.getCurrentSwingDuration() / 2 || this.swingTime < 0) {
            this.swingTime = -1;
            this.swinging = true;
            this.swingingArm = interactionHand;
            if (this.level instanceof ServerLevel) {
                ClientboundAnimatePacket clientboundAnimatePacket = new ClientboundAnimatePacket(this, interactionHand == InteractionHand.MAIN_HAND ? 0 : 3);
                ServerChunkCache serverChunkCache = ((ServerLevel)this.level).getChunkSource();
                if (bl) {
                    serverChunkCache.broadcastAndSend(this, clientboundAnimatePacket);
                } else {
                    serverChunkCache.broadcast(this, clientboundAnimatePacket);
                }
            }
        }
    }

    @Override
    public void handleEntityEvent(byte by) {
        switch (by) {
            case 2: 
            case 33: 
            case 36: 
            case 37: 
            case 44: {
                SoundEvent soundEvent;
                DamageSource damageSource;
                boolean bl = by == 33;
                boolean bl2 = by == 36;
                boolean bl3 = by == 37;
                boolean bl4 = by == 44;
                this.animationSpeed = 1.5f;
                this.invulnerableTime = 20;
                this.hurtTime = this.hurtDuration = 10;
                this.hurtDir = 0.0f;
                if (bl) {
                    this.playSound(SoundEvents.THORNS_HIT, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                }
                if ((soundEvent = this.getHurtSound(damageSource = bl3 ? DamageSource.ON_FIRE : (bl2 ? DamageSource.DROWN : (bl4 ? DamageSource.SWEET_BERRY_BUSH : DamageSource.GENERIC)))) != null) {
                    this.playSound(soundEvent, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                }
                this.hurt(DamageSource.GENERIC, 0.0f);
                break;
            }
            case 3: {
                SoundEvent soundEvent = this.getDeathSound();
                if (soundEvent != null) {
                    this.playSound(soundEvent, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                }
                if (this instanceof Player) break;
                this.setHealth(0.0f);
                this.die(DamageSource.GENERIC);
                break;
            }
            case 30: {
                this.playSound(SoundEvents.SHIELD_BREAK, 0.8f, 0.8f + this.level.random.nextFloat() * 0.4f);
                break;
            }
            case 29: {
                this.playSound(SoundEvents.SHIELD_BLOCK, 1.0f, 0.8f + this.level.random.nextFloat() * 0.4f);
                break;
            }
            case 46: {
                int n = 128;
                for (int i = 0; i < 128; ++i) {
                    double d = (double)i / 127.0;
                    float f = (this.random.nextFloat() - 0.5f) * 0.2f;
                    float f2 = (this.random.nextFloat() - 0.5f) * 0.2f;
                    float f3 = (this.random.nextFloat() - 0.5f) * 0.2f;
                    double d2 = Mth.lerp(d, this.xo, this.getX()) + (this.random.nextDouble() - 0.5) * (double)this.getBbWidth() * 2.0;
                    double d3 = Mth.lerp(d, this.yo, this.getY()) + this.random.nextDouble() * (double)this.getBbHeight();
                    double d4 = Mth.lerp(d, this.zo, this.getZ()) + (this.random.nextDouble() - 0.5) * (double)this.getBbWidth() * 2.0;
                    this.level.addParticle(ParticleTypes.PORTAL, d2, d3, d4, f, f2, f3);
                }
                break;
            }
            case 47: {
                this.breakItem(this.getItemBySlot(EquipmentSlot.MAINHAND));
                break;
            }
            case 48: {
                this.breakItem(this.getItemBySlot(EquipmentSlot.OFFHAND));
                break;
            }
            case 49: {
                this.breakItem(this.getItemBySlot(EquipmentSlot.HEAD));
                break;
            }
            case 50: {
                this.breakItem(this.getItemBySlot(EquipmentSlot.CHEST));
                break;
            }
            case 51: {
                this.breakItem(this.getItemBySlot(EquipmentSlot.LEGS));
                break;
            }
            case 52: {
                this.breakItem(this.getItemBySlot(EquipmentSlot.FEET));
                break;
            }
            case 54: {
                HoneyBlock.showJumpParticles(this);
                break;
            }
            case 55: {
                this.swapHandItems();
                break;
            }
            default: {
                super.handleEntityEvent(by);
            }
        }
    }

    private void swapHandItems() {
        ItemStack itemStack = this.getItemBySlot(EquipmentSlot.OFFHAND);
        this.setItemSlot(EquipmentSlot.OFFHAND, this.getItemBySlot(EquipmentSlot.MAINHAND));
        this.setItemSlot(EquipmentSlot.MAINHAND, itemStack);
    }

    @Override
    protected void outOfWorld() {
        this.hurt(DamageSource.OUT_OF_WORLD, 4.0f);
    }

    protected void updateSwingTime() {
        int n = this.getCurrentSwingDuration();
        if (this.swinging) {
            ++this.swingTime;
            if (this.swingTime >= n) {
                this.swingTime = 0;
                this.swinging = false;
            }
        } else {
            this.swingTime = 0;
        }
        this.attackAnim = (float)this.swingTime / (float)n;
    }

    @Nullable
    public AttributeInstance getAttribute(Attribute attribute) {
        return this.getAttributes().getInstance(attribute);
    }

    public double getAttributeValue(Attribute attribute) {
        return this.getAttributes().getValue(attribute);
    }

    public double getAttributeBaseValue(Attribute attribute) {
        return this.getAttributes().getBaseValue(attribute);
    }

    public AttributeMap getAttributes() {
        return this.attributes;
    }

    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    public ItemStack getMainHandItem() {
        return this.getItemBySlot(EquipmentSlot.MAINHAND);
    }

    public ItemStack getOffhandItem() {
        return this.getItemBySlot(EquipmentSlot.OFFHAND);
    }

    public boolean isHolding(Item item) {
        return this.isHolding(item2 -> item2 == item);
    }

    public boolean isHolding(Predicate<Item> predicate) {
        return predicate.test(this.getMainHandItem().getItem()) || predicate.test(this.getOffhandItem().getItem());
    }

    public ItemStack getItemInHand(InteractionHand interactionHand) {
        if (interactionHand == InteractionHand.MAIN_HAND) {
            return this.getItemBySlot(EquipmentSlot.MAINHAND);
        }
        if (interactionHand == InteractionHand.OFF_HAND) {
            return this.getItemBySlot(EquipmentSlot.OFFHAND);
        }
        throw new IllegalArgumentException("Invalid hand " + (Object)((Object)interactionHand));
    }

    public void setItemInHand(InteractionHand interactionHand, ItemStack itemStack) {
        if (interactionHand == InteractionHand.MAIN_HAND) {
            this.setItemSlot(EquipmentSlot.MAINHAND, itemStack);
        } else if (interactionHand == InteractionHand.OFF_HAND) {
            this.setItemSlot(EquipmentSlot.OFFHAND, itemStack);
        } else {
            throw new IllegalArgumentException("Invalid hand " + (Object)((Object)interactionHand));
        }
    }

    public boolean hasItemInSlot(EquipmentSlot equipmentSlot) {
        return !this.getItemBySlot(equipmentSlot).isEmpty();
    }

    @Override
    public abstract Iterable<ItemStack> getArmorSlots();

    public abstract ItemStack getItemBySlot(EquipmentSlot var1);

    @Override
    public abstract void setItemSlot(EquipmentSlot var1, ItemStack var2);

    public float getArmorCoverPercentage() {
        Iterable<ItemStack> iterable = this.getArmorSlots();
        int n = 0;
        int n2 = 0;
        for (ItemStack itemStack : iterable) {
            if (!itemStack.isEmpty()) {
                ++n2;
            }
            ++n;
        }
        return n > 0 ? (float)n2 / (float)n : 0.0f;
    }

    @Override
    public void setSprinting(boolean bl) {
        super.setSprinting(bl);
        AttributeInstance attributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attributeInstance.getModifier(SPEED_MODIFIER_SPRINTING_UUID) != null) {
            attributeInstance.removeModifier(SPEED_MODIFIER_SPRINTING);
        }
        if (bl) {
            attributeInstance.addTransientModifier(SPEED_MODIFIER_SPRINTING);
        }
    }

    protected float getSoundVolume() {
        return 1.0f;
    }

    protected float getVoicePitch() {
        if (this.isBaby()) {
            return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.5f;
        }
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f;
    }

    protected boolean isImmobile() {
        return this.isDeadOrDying();
    }

    @Override
    public void push(Entity entity) {
        if (!this.isSleeping()) {
            super.push(entity);
        }
    }

    private void dismountVehicle(Entity entity) {
        Vec3 vec3 = entity.removed || this.level.getBlockState(entity.blockPosition()).getBlock().is(BlockTags.PORTALS) ? new Vec3(entity.getX(), entity.getY() + (double)entity.getBbHeight(), entity.getZ()) : entity.getDismountLocationForPassenger(this);
        this.teleportTo(vec3.x, vec3.y, vec3.z);
    }

    @Override
    public boolean shouldShowName() {
        return this.isCustomNameVisible();
    }

    protected float getJumpPower() {
        return 0.42f * this.getBlockJumpFactor();
    }

    protected void jumpFromGround() {
        float f = this.getJumpPower();
        if (this.hasEffect(MobEffects.JUMP)) {
            f += 0.1f * (float)(this.getEffect(MobEffects.JUMP).getAmplifier() + 1);
        }
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, f, vec3.z);
        if (this.isSprinting()) {
            float f2 = this.yRot * 0.017453292f;
            this.setDeltaMovement(this.getDeltaMovement().add(-Mth.sin(f2) * 0.2f, 0.0, Mth.cos(f2) * 0.2f));
        }
        this.hasImpulse = true;
    }

    protected void goDownInWater() {
        this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.03999999910593033, 0.0));
    }

    protected void jumpInLiquid(Tag<Fluid> tag) {
        this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.03999999910593033, 0.0));
    }

    protected float getWaterSlowDown() {
        return 0.8f;
    }

    public boolean canStandOnFluid(Fluid fluid) {
        return false;
    }

    public void travel(Vec3 vec3) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            boolean bl;
            double d = 0.08;
            boolean bl2 = bl = this.getDeltaMovement().y <= 0.0;
            if (bl && this.hasEffect(MobEffects.SLOW_FALLING)) {
                d = 0.01;
                this.fallDistance = 0.0f;
            }
            FluidState fluidState = this.level.getFluidState(this.blockPosition());
            if (this.isInWater() && this.isAffectedByFluids() && !this.canStandOnFluid(fluidState.getType())) {
                double d2 = this.getY();
                float f = this.isSprinting() ? 0.9f : this.getWaterSlowDown();
                float f2 = 0.02f;
                float f3 = EnchantmentHelper.getDepthStrider(this);
                if (f3 > 3.0f) {
                    f3 = 3.0f;
                }
                if (!this.onGround) {
                    f3 *= 0.5f;
                }
                if (f3 > 0.0f) {
                    f += (0.54600006f - f) * f3 / 3.0f;
                    f2 += (this.getSpeed() - f2) * f3 / 3.0f;
                }
                if (this.hasEffect(MobEffects.DOLPHINS_GRACE)) {
                    f = 0.96f;
                }
                this.moveRelative(f2, vec3);
                this.move(MoverType.SELF, this.getDeltaMovement());
                Vec3 vec32 = this.getDeltaMovement();
                if (this.horizontalCollision && this.onClimbable()) {
                    vec32 = new Vec3(vec32.x, 0.2, vec32.z);
                }
                this.setDeltaMovement(vec32.multiply(f, 0.800000011920929, f));
                Vec3 vec33 = this.getFluidFallingAdjustedMovement(d, bl, this.getDeltaMovement());
                this.setDeltaMovement(vec33);
                if (this.horizontalCollision && this.isFree(vec33.x, vec33.y + 0.6000000238418579 - this.getY() + d2, vec33.z)) {
                    this.setDeltaMovement(vec33.x, 0.30000001192092896, vec33.z);
                }
            } else if (this.isInLava() && this.isAffectedByFluids() && !this.canStandOnFluid(fluidState.getType())) {
                Vec3 vec34;
                double d3 = this.getY();
                this.moveRelative(0.02f, vec3);
                this.move(MoverType.SELF, this.getDeltaMovement());
                if (this.getFluidHeight(FluidTags.LAVA) <= this.getFluidJumpThreshold()) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 0.800000011920929, 0.5));
                    vec34 = this.getFluidFallingAdjustedMovement(d, bl, this.getDeltaMovement());
                    this.setDeltaMovement(vec34);
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
                }
                if (!this.isNoGravity()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0, -d / 4.0, 0.0));
                }
                vec34 = this.getDeltaMovement();
                if (this.horizontalCollision && this.isFree(vec34.x, vec34.y + 0.6000000238418579 - this.getY() + d3, vec34.z)) {
                    this.setDeltaMovement(vec34.x, 0.30000001192092896, vec34.z);
                }
            } else if (this.isFallFlying()) {
                double d4;
                float f;
                double d5;
                Vec3 vec35 = this.getDeltaMovement();
                if (vec35.y > -0.5) {
                    this.fallDistance = 1.0f;
                }
                Vec3 vec36 = this.getLookAngle();
                float f4 = this.xRot * 0.017453292f;
                double d6 = Math.sqrt(vec36.x * vec36.x + vec36.z * vec36.z);
                double d7 = Math.sqrt(LivingEntity.getHorizontalDistanceSqr(vec35));
                double d8 = vec36.length();
                float f5 = Mth.cos(f4);
                f5 = (float)((double)f5 * ((double)f5 * Math.min(1.0, d8 / 0.4)));
                vec35 = this.getDeltaMovement().add(0.0, d * (-1.0 + (double)f5 * 0.75), 0.0);
                if (vec35.y < 0.0 && d6 > 0.0) {
                    d4 = vec35.y * -0.1 * (double)f5;
                    vec35 = vec35.add(vec36.x * d4 / d6, d4, vec36.z * d4 / d6);
                }
                if (f4 < 0.0f && d6 > 0.0) {
                    d4 = d7 * (double)(-Mth.sin(f4)) * 0.04;
                    vec35 = vec35.add(-vec36.x * d4 / d6, d4 * 3.2, -vec36.z * d4 / d6);
                }
                if (d6 > 0.0) {
                    vec35 = vec35.add((vec36.x / d6 * d7 - vec35.x) * 0.1, 0.0, (vec36.z / d6 * d7 - vec35.z) * 0.1);
                }
                this.setDeltaMovement(vec35.multiply(0.9900000095367432, 0.9800000190734863, 0.9900000095367432));
                this.move(MoverType.SELF, this.getDeltaMovement());
                if (this.horizontalCollision && !this.level.isClientSide && (f = (float)((d5 = d7 - (d4 = Math.sqrt(LivingEntity.getHorizontalDistanceSqr(this.getDeltaMovement())))) * 10.0 - 3.0)) > 0.0f) {
                    this.playSound(this.getFallDamageSound((int)f), 1.0f, 1.0f);
                    this.hurt(DamageSource.FLY_INTO_WALL, f);
                }
                if (this.onGround && !this.level.isClientSide) {
                    this.setSharedFlag(7, false);
                }
            } else {
                BlockPos blockPos = this.getBlockPosBelowThatAffectsMyMovement();
                float f = this.level.getBlockState(blockPos).getBlock().getFriction();
                float f6 = this.onGround ? f * 0.91f : 0.91f;
                Vec3 vec37 = this.handleRelativeFrictionAndCalculateMovement(vec3, f);
                double d9 = vec37.y;
                if (this.hasEffect(MobEffects.LEVITATION)) {
                    d9 += (0.05 * (double)(this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1) - vec37.y) * 0.2;
                    this.fallDistance = 0.0f;
                } else if (!this.level.isClientSide || this.level.hasChunkAt(blockPos)) {
                    if (!this.isNoGravity()) {
                        d9 -= d;
                    }
                } else {
                    d9 = this.getY() > 0.0 ? -0.1 : 0.0;
                }
                this.setDeltaMovement(vec37.x * (double)f6, d9 * 0.9800000190734863, vec37.z * (double)f6);
            }
        }
        this.calculateEntityAnimation(this, this instanceof FlyingAnimal);
    }

    public void calculateEntityAnimation(LivingEntity livingEntity, boolean bl) {
        double d;
        double d2;
        livingEntity.animationSpeedOld = livingEntity.animationSpeed;
        double d3 = livingEntity.getX() - livingEntity.xo;
        float f = Mth.sqrt(d3 * d3 + (d = bl ? livingEntity.getY() - livingEntity.yo : 0.0) * d + (d2 = livingEntity.getZ() - livingEntity.zo) * d2) * 4.0f;
        if (f > 1.0f) {
            f = 1.0f;
        }
        livingEntity.animationSpeed += (f - livingEntity.animationSpeed) * 0.4f;
        livingEntity.animationPosition += livingEntity.animationSpeed;
    }

    public Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 vec3, float f) {
        this.moveRelative(this.getFrictionInfluencedSpeed(f), vec3);
        this.setDeltaMovement(this.handleOnClimbable(this.getDeltaMovement()));
        this.move(MoverType.SELF, this.getDeltaMovement());
        Vec3 vec32 = this.getDeltaMovement();
        if ((this.horizontalCollision || this.jumping) && this.onClimbable()) {
            vec32 = new Vec3(vec32.x, 0.2, vec32.z);
        }
        return vec32;
    }

    public Vec3 getFluidFallingAdjustedMovement(double d, boolean bl, Vec3 vec3) {
        if (!this.isNoGravity() && !this.isSprinting()) {
            double d2 = bl && Math.abs(vec3.y - 0.005) >= 0.003 && Math.abs(vec3.y - d / 16.0) < 0.003 ? -0.003 : vec3.y - d / 16.0;
            return new Vec3(vec3.x, d2, vec3.z);
        }
        return vec3;
    }

    private Vec3 handleOnClimbable(Vec3 vec3) {
        if (this.onClimbable()) {
            this.fallDistance = 0.0f;
            float f = 0.15f;
            double d = Mth.clamp(vec3.x, -0.15000000596046448, 0.15000000596046448);
            double d2 = Mth.clamp(vec3.z, -0.15000000596046448, 0.15000000596046448);
            double d3 = Math.max(vec3.y, -0.15000000596046448);
            if (d3 < 0.0 && !this.getFeetBlockState().is(Blocks.SCAFFOLDING) && this.isSuppressingSlidingDownLadder() && this instanceof Player) {
                d3 = 0.0;
            }
            vec3 = new Vec3(d, d3, d2);
        }
        return vec3;
    }

    private float getFrictionInfluencedSpeed(float f) {
        if (this.onGround) {
            return this.getSpeed() * (0.21600002f / (f * f * f));
        }
        return this.flyingSpeed;
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float f) {
        this.speed = f;
    }

    public boolean doHurtTarget(Entity entity) {
        this.setLastHurtMob(entity);
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        this.updatingUsingItem();
        this.updateSwimAmount();
        if (!this.level.isClientSide) {
            int n;
            int n2 = this.getArrowCount();
            if (n2 > 0) {
                if (this.removeArrowTime <= 0) {
                    this.removeArrowTime = 20 * (30 - n2);
                }
                --this.removeArrowTime;
                if (this.removeArrowTime <= 0) {
                    this.setArrowCount(n2 - 1);
                }
            }
            if ((n = this.getStingerCount()) > 0) {
                if (this.removeStingerTime <= 0) {
                    this.removeStingerTime = 20 * (30 - n);
                }
                --this.removeStingerTime;
                if (this.removeStingerTime <= 0) {
                    this.setStingerCount(n - 1);
                }
            }
            this.detectEquipmentUpdates();
            if (this.tickCount % 20 == 0) {
                this.getCombatTracker().recheckStatus();
            }
            if (!this.glowing) {
                boolean bl = this.hasEffect(MobEffects.GLOWING);
                if (this.getSharedFlag(6) != bl) {
                    this.setSharedFlag(6, bl);
                }
            }
            if (this.isSleeping() && !this.checkBedExists()) {
                this.stopSleeping();
            }
        }
        this.aiStep();
        double d = this.getX() - this.xo;
        double d2 = this.getZ() - this.zo;
        float f = (float)(d * d + d2 * d2);
        float f2 = this.yBodyRot;
        float f3 = 0.0f;
        this.oRun = this.run;
        float f4 = 0.0f;
        if (f > 0.0025000002f) {
            f4 = 1.0f;
            f3 = (float)Math.sqrt(f) * 3.0f;
            float f5 = (float)Mth.atan2(d2, d) * 57.295776f - 90.0f;
            float f6 = Mth.abs(Mth.wrapDegrees(this.yRot) - f5);
            f2 = 95.0f < f6 && f6 < 265.0f ? f5 - 180.0f : f5;
        }
        if (this.attackAnim > 0.0f) {
            f2 = this.yRot;
        }
        if (!this.onGround) {
            f4 = 0.0f;
        }
        this.run += (f4 - this.run) * 0.3f;
        this.level.getProfiler().push("headTurn");
        f3 = this.tickHeadTurn(f2, f3);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("rangeChecks");
        while (this.yRot - this.yRotO < -180.0f) {
            this.yRotO -= 360.0f;
        }
        while (this.yRot - this.yRotO >= 180.0f) {
            this.yRotO += 360.0f;
        }
        while (this.yBodyRot - this.yBodyRotO < -180.0f) {
            this.yBodyRotO -= 360.0f;
        }
        while (this.yBodyRot - this.yBodyRotO >= 180.0f) {
            this.yBodyRotO += 360.0f;
        }
        while (this.xRot - this.xRotO < -180.0f) {
            this.xRotO -= 360.0f;
        }
        while (this.xRot - this.xRotO >= 180.0f) {
            this.xRotO += 360.0f;
        }
        while (this.yHeadRot - this.yHeadRotO < -180.0f) {
            this.yHeadRotO -= 360.0f;
        }
        while (this.yHeadRot - this.yHeadRotO >= 180.0f) {
            this.yHeadRotO += 360.0f;
        }
        this.level.getProfiler().pop();
        this.animStep += f3;
        this.fallFlyTicks = this.isFallFlying() ? ++this.fallFlyTicks : 0;
        if (this.isSleeping()) {
            this.xRot = 0.0f;
        }
    }

    private void detectEquipmentUpdates() {
        Map<EquipmentSlot, ItemStack> map = this.collectEquipmentChanges();
        if (map != null) {
            this.handleHandSwap(map);
            if (!map.isEmpty()) {
                this.handleEquipmentChanges(map);
            }
        }
    }

    @Nullable
    private Map<EquipmentSlot, ItemStack> collectEquipmentChanges() {
        EnumMap enumMap = null;
        block4 : for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            ItemStack itemStack;
            switch (equipmentSlot.getType()) {
                case HAND: {
                    itemStack = this.getLastHandItem(equipmentSlot);
                    break;
                }
                case ARMOR: {
                    itemStack = this.getLastArmorItem(equipmentSlot);
                    break;
                }
                default: {
                    continue block4;
                }
            }
            ItemStack itemStack2 = this.getItemBySlot(equipmentSlot);
            if (ItemStack.matches(itemStack2, itemStack)) continue;
            if (enumMap == null) {
                enumMap = Maps.newEnumMap(EquipmentSlot.class);
            }
            enumMap.put(equipmentSlot, itemStack2);
            if (!itemStack.isEmpty()) {
                this.getAttributes().removeAttributeModifiers(itemStack.getAttributeModifiers(equipmentSlot));
            }
            if (itemStack2.isEmpty()) continue;
            this.getAttributes().addTransientAttributeModifiers(itemStack2.getAttributeModifiers(equipmentSlot));
        }
        return enumMap;
    }

    private void handleHandSwap(Map<EquipmentSlot, ItemStack> map) {
        ItemStack itemStack = map.get((Object)((Object)EquipmentSlot.MAINHAND));
        ItemStack itemStack2 = map.get((Object)((Object)EquipmentSlot.OFFHAND));
        if (itemStack != null && itemStack2 != null && ItemStack.matches(itemStack, this.getLastHandItem(EquipmentSlot.OFFHAND)) && ItemStack.matches(itemStack2, this.getLastHandItem(EquipmentSlot.MAINHAND))) {
            ((ServerLevel)this.level).getChunkSource().broadcast(this, new ClientboundEntityEventPacket(this, 55));
            map.remove((Object)((Object)EquipmentSlot.MAINHAND));
            map.remove((Object)((Object)EquipmentSlot.OFFHAND));
            this.setLastHandItem(EquipmentSlot.MAINHAND, itemStack.copy());
            this.setLastHandItem(EquipmentSlot.OFFHAND, itemStack2.copy());
        }
    }

    private void handleEquipmentChanges(Map<EquipmentSlot, ItemStack> map) {
        ArrayList arrayList = Lists.newArrayListWithCapacity((int)map.size());
        map.forEach((equipmentSlot, itemStack) -> {
            ItemStack itemStack2 = itemStack.copy();
            arrayList.add(Pair.of((Object)equipmentSlot, (Object)itemStack2));
            switch (equipmentSlot.getType()) {
                case HAND: {
                    this.setLastHandItem((EquipmentSlot)((Object)equipmentSlot), itemStack2);
                    break;
                }
                case ARMOR: {
                    this.setLastArmorItem((EquipmentSlot)((Object)equipmentSlot), itemStack2);
                }
            }
        });
        ((ServerLevel)this.level).getChunkSource().broadcast(this, new ClientboundSetEquipmentPacket(this.getId(), arrayList));
    }

    private ItemStack getLastArmorItem(EquipmentSlot equipmentSlot) {
        return this.lastArmorItemStacks.get(equipmentSlot.getIndex());
    }

    private void setLastArmorItem(EquipmentSlot equipmentSlot, ItemStack itemStack) {
        this.lastArmorItemStacks.set(equipmentSlot.getIndex(), itemStack);
    }

    private ItemStack getLastHandItem(EquipmentSlot equipmentSlot) {
        return this.lastHandItemStacks.get(equipmentSlot.getIndex());
    }

    private void setLastHandItem(EquipmentSlot equipmentSlot, ItemStack itemStack) {
        this.lastHandItemStacks.set(equipmentSlot.getIndex(), itemStack);
    }

    protected float tickHeadTurn(float f, float f2) {
        boolean bl;
        float f3 = Mth.wrapDegrees(f - this.yBodyRot);
        this.yBodyRot += f3 * 0.3f;
        float f4 = Mth.wrapDegrees(this.yRot - this.yBodyRot);
        boolean bl2 = bl = f4 < -90.0f || f4 >= 90.0f;
        if (f4 < -75.0f) {
            f4 = -75.0f;
        }
        if (f4 >= 75.0f) {
            f4 = 75.0f;
        }
        this.yBodyRot = this.yRot - f4;
        if (f4 * f4 > 2500.0f) {
            this.yBodyRot += f4 * 0.2f;
        }
        if (bl) {
            f2 *= -1.0f;
        }
        return f2;
    }

    public void aiStep() {
        if (this.noJumpDelay > 0) {
            --this.noJumpDelay;
        }
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.setPacketCoordinates(this.getX(), this.getY(), this.getZ());
        }
        if (this.lerpSteps > 0) {
            double d = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
            double d2 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
            double d3 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
            double d4 = Mth.wrapDegrees(this.lerpYRot - (double)this.yRot);
            this.yRot = (float)((double)this.yRot + d4 / (double)this.lerpSteps);
            this.xRot = (float)((double)this.xRot + (this.lerpXRot - (double)this.xRot) / (double)this.lerpSteps);
            --this.lerpSteps;
            this.setPos(d, d2, d3);
            this.setRot(this.yRot, this.xRot);
        } else if (!this.isEffectiveAi()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        }
        if (this.lerpHeadSteps > 0) {
            this.yHeadRot = (float)((double)this.yHeadRot + Mth.wrapDegrees(this.lyHeadRot - (double)this.yHeadRot) / (double)this.lerpHeadSteps);
            --this.lerpHeadSteps;
        }
        Vec3 vec3 = this.getDeltaMovement();
        double d = vec3.x;
        double d5 = vec3.y;
        double d6 = vec3.z;
        if (Math.abs(vec3.x) < 0.003) {
            d = 0.0;
        }
        if (Math.abs(vec3.y) < 0.003) {
            d5 = 0.0;
        }
        if (Math.abs(vec3.z) < 0.003) {
            d6 = 0.0;
        }
        this.setDeltaMovement(d, d5, d6);
        this.level.getProfiler().push("ai");
        if (this.isImmobile()) {
            this.jumping = false;
            this.xxa = 0.0f;
            this.zza = 0.0f;
        } else if (this.isEffectiveAi()) {
            this.level.getProfiler().push("newAi");
            this.serverAiStep();
            this.level.getProfiler().pop();
        }
        this.level.getProfiler().pop();
        this.level.getProfiler().push("jump");
        if (this.jumping && this.isAffectedByFluids()) {
            double d7 = this.isInLava() ? this.getFluidHeight(FluidTags.LAVA) : this.getFluidHeight(FluidTags.WATER);
            boolean bl = this.isInWater() && d7 > 0.0;
            double d8 = this.getFluidJumpThreshold();
            if (bl && (!this.onGround || d7 > d8)) {
                this.jumpInLiquid(FluidTags.WATER);
            } else if (this.isInLava() && (!this.onGround || d7 > d8)) {
                this.jumpInLiquid(FluidTags.LAVA);
            } else if ((this.onGround || bl && d7 <= d8) && this.noJumpDelay == 0) {
                this.jumpFromGround();
                this.noJumpDelay = 10;
            }
        } else {
            this.noJumpDelay = 0;
        }
        this.level.getProfiler().pop();
        this.level.getProfiler().push("travel");
        this.xxa *= 0.98f;
        this.zza *= 0.98f;
        this.updateFallFlying();
        AABB aABB = this.getBoundingBox();
        this.travel(new Vec3(this.xxa, this.yya, this.zza));
        this.level.getProfiler().pop();
        this.level.getProfiler().push("push");
        if (this.autoSpinAttackTicks > 0) {
            --this.autoSpinAttackTicks;
            this.checkAutoSpinAttack(aABB, this.getBoundingBox());
        }
        this.pushEntities();
        this.level.getProfiler().pop();
        if (!this.level.isClientSide && this.isSensitiveToWater() && this.isInWaterRainOrBubble()) {
            this.hurt(DamageSource.DROWN, 1.0f);
        }
    }

    public boolean isSensitiveToWater() {
        return false;
    }

    private void updateFallFlying() {
        boolean bl = this.getSharedFlag(7);
        if (bl && !this.onGround && !this.isPassenger() && !this.hasEffect(MobEffects.LEVITATION)) {
            ItemStack itemStack = this.getItemBySlot(EquipmentSlot.CHEST);
            if (itemStack.getItem() == Items.ELYTRA && ElytraItem.isFlyEnabled(itemStack)) {
                bl = true;
                if (!this.level.isClientSide && (this.fallFlyTicks + 1) % 20 == 0) {
                    itemStack.hurtAndBreak(1, this, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.CHEST));
                }
            } else {
                bl = false;
            }
        } else {
            bl = false;
        }
        if (!this.level.isClientSide) {
            this.setSharedFlag(7, bl);
        }
    }

    protected void serverAiStep() {
    }

    protected void pushEntities() {
        List<Entity> list = this.level.getEntities(this, this.getBoundingBox(), EntitySelector.pushableBy(this));
        if (!list.isEmpty()) {
            int n;
            int n2 = this.level.getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
            if (n2 > 0 && list.size() > n2 - 1 && this.random.nextInt(4) == 0) {
                n = 0;
                for (int i = 0; i < list.size(); ++i) {
                    if (list.get(i).isPassenger()) continue;
                    ++n;
                }
                if (n > n2 - 1) {
                    this.hurt(DamageSource.CRAMMING, 6.0f);
                }
            }
            for (n = 0; n < list.size(); ++n) {
                Entity entity = list.get(n);
                this.doPush(entity);
            }
        }
    }

    protected void checkAutoSpinAttack(AABB aABB, AABB aABB2) {
        AABB aABB3 = aABB.minmax(aABB2);
        List<Entity> list = this.level.getEntities(this, aABB3);
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); ++i) {
                Entity entity = list.get(i);
                if (!(entity instanceof LivingEntity)) continue;
                this.doAutoAttackOnTouch((LivingEntity)entity);
                this.autoSpinAttackTicks = 0;
                this.setDeltaMovement(this.getDeltaMovement().scale(-0.2));
                break;
            }
        } else if (this.horizontalCollision) {
            this.autoSpinAttackTicks = 0;
        }
        if (!this.level.isClientSide && this.autoSpinAttackTicks <= 0) {
            this.setLivingEntityFlag(4, false);
        }
    }

    protected void doPush(Entity entity) {
        entity.push(this);
    }

    protected void doAutoAttackOnTouch(LivingEntity livingEntity) {
    }

    public void startAutoSpinAttack(int n) {
        this.autoSpinAttackTicks = n;
        if (!this.level.isClientSide) {
            this.setLivingEntityFlag(4, true);
        }
    }

    public boolean isAutoSpinAttack() {
        return (this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 4) != 0;
    }

    @Override
    public void stopRiding() {
        Entity entity = this.getVehicle();
        super.stopRiding();
        if (entity != null && entity != this.getVehicle() && !this.level.isClientSide) {
            this.dismountVehicle(entity);
        }
    }

    @Override
    public void rideTick() {
        super.rideTick();
        this.oRun = this.run;
        this.run = 0.0f;
        this.fallDistance = 0.0f;
    }

    @Override
    public void lerpTo(double d, double d2, double d3, float f, float f2, int n, boolean bl) {
        this.lerpX = d;
        this.lerpY = d2;
        this.lerpZ = d3;
        this.lerpYRot = f;
        this.lerpXRot = f2;
        this.lerpSteps = n;
    }

    @Override
    public void lerpHeadTo(float f, int n) {
        this.lyHeadRot = f;
        this.lerpHeadSteps = n;
    }

    public void setJumping(boolean bl) {
        this.jumping = bl;
    }

    public void onItemPickup(ItemEntity itemEntity) {
        Player player;
        Player player2 = player = itemEntity.getThrower() != null ? this.level.getPlayerByUUID(itemEntity.getThrower()) : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.ITEM_PICKED_UP_BY_ENTITY.trigger((ServerPlayer)player, itemEntity.getItem(), this);
        }
    }

    public void take(Entity entity, int n) {
        if (!entity.removed && !this.level.isClientSide && (entity instanceof ItemEntity || entity instanceof AbstractArrow || entity instanceof ExperienceOrb)) {
            ((ServerLevel)this.level).getChunkSource().broadcast(entity, new ClientboundTakeItemEntityPacket(entity.getId(), this.getId(), n));
        }
    }

    public boolean canSee(Entity entity) {
        Vec3 vec3;
        Vec3 vec32 = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        return this.level.clip(new ClipContext(vec32, vec3 = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ()), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    @Override
    public float getViewYRot(float f) {
        if (f == 1.0f) {
            return this.yHeadRot;
        }
        return Mth.lerp(f, this.yHeadRotO, this.yHeadRot);
    }

    public float getAttackAnim(float f) {
        float f2 = this.attackAnim - this.oAttackAnim;
        if (f2 < 0.0f) {
            f2 += 1.0f;
        }
        return this.oAttackAnim + f2 * f;
    }

    public boolean isEffectiveAi() {
        return !this.level.isClientSide;
    }

    @Override
    public boolean isPickable() {
        return !this.removed;
    }

    @Override
    public boolean isPushable() {
        return this.isAlive() && !this.isSpectator() && !this.onClimbable();
    }

    @Override
    protected void markHurt() {
        this.hurtMarked = this.random.nextDouble() >= this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
    }

    @Override
    public float getYHeadRot() {
        return this.yHeadRot;
    }

    @Override
    public void setYHeadRot(float f) {
        this.yHeadRot = f;
    }

    @Override
    public void setYBodyRot(float f) {
        this.yBodyRot = f;
    }

    @Override
    protected Vec3 getRelativePortalPosition(Direction.Axis axis, BlockUtil.FoundRectangle foundRectangle) {
        return LivingEntity.resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition(axis, foundRectangle));
    }

    public static Vec3 resetForwardDirectionOfRelativePortalPosition(Vec3 vec3) {
        return new Vec3(vec3.x, vec3.y, 0.0);
    }

    public float getAbsorptionAmount() {
        return this.absorptionAmount;
    }

    public void setAbsorptionAmount(float f) {
        if (f < 0.0f) {
            f = 0.0f;
        }
        this.absorptionAmount = f;
    }

    public void onEnterCombat() {
    }

    public void onLeaveCombat() {
    }

    protected void updateEffectVisibility() {
        this.effectsDirty = true;
    }

    public abstract HumanoidArm getMainArm();

    public boolean isUsingItem() {
        return (this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 1) > 0;
    }

    public InteractionHand getUsedItemHand() {
        return (this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 2) > 0 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }

    private void updatingUsingItem() {
        if (this.isUsingItem()) {
            if (ItemStack.isSameIgnoreDurability(this.getItemInHand(this.getUsedItemHand()), this.useItem)) {
                this.useItem = this.getItemInHand(this.getUsedItemHand());
                this.useItem.onUseTick(this.level, this, this.getUseItemRemainingTicks());
                if (this.shouldTriggerItemUseEffects()) {
                    this.triggerItemUseEffects(this.useItem, 5);
                }
                if (--this.useItemRemaining == 0 && !this.level.isClientSide && !this.useItem.useOnRelease()) {
                    this.completeUsingItem();
                }
            } else {
                this.stopUsingItem();
            }
        }
    }

    private boolean shouldTriggerItemUseEffects() {
        int n = this.getUseItemRemainingTicks();
        FoodProperties foodProperties = this.useItem.getItem().getFoodProperties();
        boolean bl = foodProperties != null && foodProperties.isFastFood();
        return (bl |= n <= this.useItem.getUseDuration() - 7) && n % 4 == 0;
    }

    private void updateSwimAmount() {
        this.swimAmountO = this.swimAmount;
        this.swimAmount = this.isVisuallySwimming() ? Math.min(1.0f, this.swimAmount + 0.09f) : Math.max(0.0f, this.swimAmount - 0.09f);
    }

    protected void setLivingEntityFlag(int n, boolean bl) {
        int n2 = this.entityData.get(DATA_LIVING_ENTITY_FLAGS).byteValue();
        n2 = bl ? (n2 |= n) : (n2 &= ~n);
        this.entityData.set(DATA_LIVING_ENTITY_FLAGS, (byte)n2);
    }

    public void startUsingItem(InteractionHand interactionHand) {
        ItemStack itemStack = this.getItemInHand(interactionHand);
        if (itemStack.isEmpty() || this.isUsingItem()) {
            return;
        }
        this.useItem = itemStack;
        this.useItemRemaining = itemStack.getUseDuration();
        if (!this.level.isClientSide) {
            this.setLivingEntityFlag(1, true);
            this.setLivingEntityFlag(2, interactionHand == InteractionHand.OFF_HAND);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        super.onSyncedDataUpdated(entityDataAccessor);
        if (SLEEPING_POS_ID.equals(entityDataAccessor)) {
            if (this.level.isClientSide) {
                this.getSleepingPos().ifPresent(this::setPosToBed);
            }
        } else if (DATA_LIVING_ENTITY_FLAGS.equals(entityDataAccessor) && this.level.isClientSide) {
            if (this.isUsingItem() && this.useItem.isEmpty()) {
                this.useItem = this.getItemInHand(this.getUsedItemHand());
                if (!this.useItem.isEmpty()) {
                    this.useItemRemaining = this.useItem.getUseDuration();
                }
            } else if (!this.isUsingItem() && !this.useItem.isEmpty()) {
                this.useItem = ItemStack.EMPTY;
                this.useItemRemaining = 0;
            }
        }
    }

    @Override
    public void lookAt(EntityAnchorArgument.Anchor anchor, Vec3 vec3) {
        super.lookAt(anchor, vec3);
        this.yHeadRotO = this.yHeadRot;
        this.yBodyRotO = this.yBodyRot = this.yHeadRot;
    }

    protected void triggerItemUseEffects(ItemStack itemStack, int n) {
        if (itemStack.isEmpty() || !this.isUsingItem()) {
            return;
        }
        if (itemStack.getUseAnimation() == UseAnim.DRINK) {
            this.playSound(this.getDrinkingSound(itemStack), 0.5f, this.level.random.nextFloat() * 0.1f + 0.9f);
        }
        if (itemStack.getUseAnimation() == UseAnim.EAT) {
            this.spawnItemParticles(itemStack, n);
            this.playSound(this.getEatingSound(itemStack), 0.5f + 0.5f * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
        }
    }

    private void spawnItemParticles(ItemStack itemStack, int n) {
        for (int i = 0; i < n; ++i) {
            Vec3 vec3 = new Vec3(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            vec3 = vec3.xRot(-this.xRot * 0.017453292f);
            vec3 = vec3.yRot(-this.yRot * 0.017453292f);
            double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
            Vec3 vec32 = new Vec3(((double)this.random.nextFloat() - 0.5) * 0.3, d, 0.6);
            vec32 = vec32.xRot(-this.xRot * 0.017453292f);
            vec32 = vec32.yRot(-this.yRot * 0.017453292f);
            vec32 = vec32.add(this.getX(), this.getEyeY(), this.getZ());
            this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemStack), vec32.x, vec32.y, vec32.z, vec3.x, vec3.y + 0.05, vec3.z);
        }
    }

    protected void completeUsingItem() {
        InteractionHand interactionHand = this.getUsedItemHand();
        if (!this.useItem.equals(this.getItemInHand(interactionHand))) {
            this.releaseUsingItem();
            return;
        }
        if (!this.useItem.isEmpty() && this.isUsingItem()) {
            this.triggerItemUseEffects(this.useItem, 16);
            ItemStack itemStack = this.useItem.finishUsingItem(this.level, this);
            if (itemStack != this.useItem) {
                this.setItemInHand(interactionHand, itemStack);
            }
            this.stopUsingItem();
        }
    }

    public ItemStack getUseItem() {
        return this.useItem;
    }

    public int getUseItemRemainingTicks() {
        return this.useItemRemaining;
    }

    public int getTicksUsingItem() {
        if (this.isUsingItem()) {
            return this.useItem.getUseDuration() - this.getUseItemRemainingTicks();
        }
        return 0;
    }

    public void releaseUsingItem() {
        if (!this.useItem.isEmpty()) {
            this.useItem.releaseUsing(this.level, this, this.getUseItemRemainingTicks());
            if (this.useItem.useOnRelease()) {
                this.updatingUsingItem();
            }
        }
        this.stopUsingItem();
    }

    public void stopUsingItem() {
        if (!this.level.isClientSide) {
            this.setLivingEntityFlag(1, false);
        }
        this.useItem = ItemStack.EMPTY;
        this.useItemRemaining = 0;
    }

    public boolean isBlocking() {
        if (!this.isUsingItem() || this.useItem.isEmpty()) {
            return false;
        }
        Item item = this.useItem.getItem();
        if (item.getUseAnimation(this.useItem) != UseAnim.BLOCK) {
            return false;
        }
        return item.getUseDuration(this.useItem) - this.useItemRemaining >= 5;
    }

    public boolean isSuppressingSlidingDownLadder() {
        return this.isShiftKeyDown();
    }

    public boolean isFallFlying() {
        return this.getSharedFlag(7);
    }

    @Override
    public boolean isVisuallySwimming() {
        return super.isVisuallySwimming() || !this.isFallFlying() && this.getPose() == Pose.FALL_FLYING;
    }

    public int getFallFlyingTicks() {
        return this.fallFlyTicks;
    }

    public boolean randomTeleport(double d, double d2, double d3, boolean bl) {
        double d4 = this.getX();
        double d5 = this.getY();
        double d6 = this.getZ();
        double d7 = d2;
        boolean bl2 = false;
        Level level = this.level;
        BlockPos blockPos = new BlockPos(d, d7, d3);
        if (level.hasChunkAt(blockPos)) {
            boolean bl3 = false;
            while (!bl3 && blockPos.getY() > 0) {
                BlockPos blockPos2 = blockPos.below();
                BlockState blockState = level.getBlockState(blockPos2);
                if (blockState.getMaterial().blocksMotion()) {
                    bl3 = true;
                    continue;
                }
                d7 -= 1.0;
                blockPos = blockPos2;
            }
            if (bl3) {
                this.teleportTo(d, d7, d3);
                if (level.noCollision(this) && !level.containsAnyLiquid(this.getBoundingBox())) {
                    bl2 = true;
                }
            }
        }
        if (!bl2) {
            this.teleportTo(d4, d5, d6);
            return false;
        }
        if (bl) {
            level.broadcastEntityEvent(this, (byte)46);
        }
        if (this instanceof PathfinderMob) {
            ((PathfinderMob)this).getNavigation().stop();
        }
        return true;
    }

    public boolean isAffectedByPotions() {
        return true;
    }

    public boolean attackable() {
        return true;
    }

    public void setRecordPlayingNearby(BlockPos blockPos, boolean bl) {
    }

    public boolean canTakeItem(ItemStack itemStack) {
        return false;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddMobPacket(this);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return pose == Pose.SLEEPING ? SLEEPING_DIMENSIONS : super.getDimensions(pose).scale(this.getScale());
    }

    public ImmutableList<Pose> getDismountPoses() {
        return ImmutableList.of((Object)((Object)Pose.STANDING));
    }

    public AABB getLocalBoundsForPose(Pose pose) {
        EntityDimensions entityDimensions = this.getDimensions(pose);
        return new AABB(-entityDimensions.width / 2.0f, 0.0, -entityDimensions.width / 2.0f, entityDimensions.width / 2.0f, entityDimensions.height, entityDimensions.width / 2.0f);
    }

    public Optional<BlockPos> getSleepingPos() {
        return this.entityData.get(SLEEPING_POS_ID);
    }

    public void setSleepingPos(BlockPos blockPos) {
        this.entityData.set(SLEEPING_POS_ID, Optional.of(blockPos));
    }

    public void clearSleepingPos() {
        this.entityData.set(SLEEPING_POS_ID, Optional.empty());
    }

    public boolean isSleeping() {
        return this.getSleepingPos().isPresent();
    }

    public void startSleeping(BlockPos blockPos) {
        BlockState blockState;
        if (this.isPassenger()) {
            this.stopRiding();
        }
        if ((blockState = this.level.getBlockState(blockPos)).getBlock() instanceof BedBlock) {
            this.level.setBlock(blockPos, (BlockState)blockState.setValue(BedBlock.OCCUPIED, true), 3);
        }
        this.setPose(Pose.SLEEPING);
        this.setPosToBed(blockPos);
        this.setSleepingPos(blockPos);
        this.setDeltaMovement(Vec3.ZERO);
        this.hasImpulse = true;
    }

    private void setPosToBed(BlockPos blockPos) {
        this.setPos((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.6875, (double)blockPos.getZ() + 0.5);
    }

    private boolean checkBedExists() {
        return this.getSleepingPos().map(blockPos -> this.level.getBlockState((BlockPos)blockPos).getBlock() instanceof BedBlock).orElse(false);
    }

    public void stopSleeping() {
        this.getSleepingPos().filter(this.level::hasChunkAt).ifPresent(blockPos -> {
            BlockState blockState = this.level.getBlockState((BlockPos)blockPos);
            if (blockState.getBlock() instanceof BedBlock) {
                this.level.setBlock((BlockPos)blockPos, (BlockState)blockState.setValue(BedBlock.OCCUPIED, false), 3);
                Vec3 vec3 = BedBlock.findStandUpPosition(this.getType(), this.level, blockPos, this.yRot).orElseGet(() -> {
                    BlockPos blockPos2 = blockPos.above();
                    return new Vec3((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.1, (double)blockPos2.getZ() + 0.5);
                });
                Vec3 vec32 = Vec3.atBottomCenterOf(blockPos).subtract(vec3).normalize();
                float f = (float)Mth.wrapDegrees(Mth.atan2(vec32.z, vec32.x) * 57.2957763671875 - 90.0);
                this.setPos(vec3.x, vec3.y, vec3.z);
                this.yRot = f;
                this.xRot = 0.0f;
            }
        });
        Vec3 vec3 = this.position();
        this.setPose(Pose.STANDING);
        this.setPos(vec3.x, vec3.y, vec3.z);
        this.clearSleepingPos();
    }

    @Nullable
    public Direction getBedOrientation() {
        BlockPos blockPos = this.getSleepingPos().orElse(null);
        return blockPos != null ? BedBlock.getBedOrientation(this.level, blockPos) : null;
    }

    @Override
    public boolean isInWall() {
        return !this.isSleeping() && super.isInWall();
    }

    @Override
    protected final float getEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return pose == Pose.SLEEPING ? 0.2f : this.getStandingEyeHeight(pose, entityDimensions);
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return super.getEyeHeight(pose, entityDimensions);
    }

    public ItemStack getProjectile(ItemStack itemStack) {
        return ItemStack.EMPTY;
    }

    public ItemStack eat(Level level, ItemStack itemStack) {
        if (itemStack.isEdible()) {
            level.playSound(null, this.getX(), this.getY(), this.getZ(), this.getEatingSound(itemStack), SoundSource.NEUTRAL, 1.0f, 1.0f + (level.random.nextFloat() - level.random.nextFloat()) * 0.4f);
            this.addEatEffect(itemStack, level, this);
            if (!(this instanceof Player) || !((Player)this).abilities.instabuild) {
                itemStack.shrink(1);
            }
        }
        return itemStack;
    }

    private void addEatEffect(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        Item item = itemStack.getItem();
        if (item.isEdible()) {
            List<Pair<MobEffectInstance, Float>> list = item.getFoodProperties().getEffects();
            for (Pair<MobEffectInstance, Float> pair : list) {
                if (level.isClientSide || pair.getFirst() == null || !(level.random.nextFloat() < ((Float)pair.getSecond()).floatValue())) continue;
                livingEntity.addEffect(new MobEffectInstance((MobEffectInstance)pair.getFirst()));
            }
        }
    }

    private static byte entityEventForEquipmentBreak(EquipmentSlot equipmentSlot) {
        switch (equipmentSlot) {
            case MAINHAND: {
                return 47;
            }
            case OFFHAND: {
                return 48;
            }
            case HEAD: {
                return 49;
            }
            case CHEST: {
                return 50;
            }
            case FEET: {
                return 52;
            }
            case LEGS: {
                return 51;
            }
        }
        return 47;
    }

    public void broadcastBreakEvent(EquipmentSlot equipmentSlot) {
        this.level.broadcastEntityEvent(this, LivingEntity.entityEventForEquipmentBreak(equipmentSlot));
    }

    public void broadcastBreakEvent(InteractionHand interactionHand) {
        this.broadcastBreakEvent(interactionHand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        if (this.getItemBySlot(EquipmentSlot.HEAD).getItem() == Items.DRAGON_HEAD) {
            float f = 0.5f;
            return this.getBoundingBox().inflate(0.5, 0.5, 0.5);
        }
        return super.getBoundingBoxForCulling();
    }

}

