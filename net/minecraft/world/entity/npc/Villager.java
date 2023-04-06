/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.entity.npc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.GolemSensor;
import net.minecraft.world.entity.ai.sensing.HurtBySensor;
import net.minecraft.world.entity.ai.sensing.NearestBedSensor;
import net.minecraft.world.entity.ai.sensing.NearestItemSensor;
import net.minecraft.world.entity.ai.sensing.NearestLivingEntitySensor;
import net.minecraft.world.entity.ai.sensing.PlayerSensor;
import net.minecraft.world.entity.ai.sensing.SecondaryPoiSensor;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.sensing.VillagerBabiesSensor;
import net.minecraft.world.entity.ai.sensing.VillagerHostilesSensor;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.Logger;

public class Villager
extends AbstractVillager
implements ReputationEventHandler,
VillagerDataHolder {
    private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA = SynchedEntityData.defineId(Villager.class, EntityDataSerializers.VILLAGER_DATA);
    public static final Map<Item, Integer> FOOD_POINTS = ImmutableMap.of((Object)Items.BREAD, (Object)4, (Object)Items.POTATO, (Object)1, (Object)Items.CARROT, (Object)1, (Object)Items.BEETROOT, (Object)1);
    private static final Set<Item> WANTED_ITEMS = ImmutableSet.of((Object)Items.BREAD, (Object)Items.POTATO, (Object)Items.CARROT, (Object)Items.WHEAT, (Object)Items.WHEAT_SEEDS, (Object)Items.BEETROOT, (Object[])new Item[]{Items.BEETROOT_SEEDS});
    private int updateMerchantTimer;
    private boolean increaseProfessionLevelOnUpdate;
    @Nullable
    private Player lastTradedPlayer;
    private byte foodLevel;
    private final GossipContainer gossips = new GossipContainer();
    private long lastGossipTime;
    private long lastGossipDecayTime;
    private int villagerXp;
    private long lastRestockGameTime;
    private int numberOfRestocksToday;
    private long lastRestockCheckDayTime;
    private boolean assignProfessionWhenSpawned;
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.WALK_TARGET, (Object[])new MemoryModuleType[]{MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WOKEN, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_DETECTED_RECENTLY});
    private static final ImmutableList<SensorType<? extends Sensor<? super Villager>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_BED, SensorType.HURT_BY, SensorType.VILLAGER_HOSTILES, SensorType.VILLAGER_BABIES, SensorType.SECONDARY_POIS, SensorType.GOLEM_DETECTED);
    public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<Villager, PoiType>> POI_MEMORIES = ImmutableMap.of(MemoryModuleType.HOME, (villager, poiType) -> poiType == PoiType.HOME, MemoryModuleType.JOB_SITE, (villager, poiType) -> villager.getVillagerData().getProfession().getJobPoiType() == poiType, MemoryModuleType.POTENTIAL_JOB_SITE, (villager, poiType) -> PoiType.ALL_JOBS.test((PoiType)poiType), MemoryModuleType.MEETING_POINT, (villager, poiType) -> poiType == PoiType.MEETING);

    public Villager(EntityType<? extends Villager> entityType, Level level) {
        this(entityType, level, VillagerType.PLAINS);
    }

    public Villager(EntityType<? extends Villager> entityType, Level level, VillagerType villagerType) {
        super(entityType, level);
        ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
        this.getNavigation().setCanFloat(true);
        this.setCanPickUpLoot(true);
        this.setVillagerData(this.getVillagerData().setType(villagerType).setProfession(VillagerProfession.NONE));
    }

    public Brain<Villager> getBrain() {
        return super.getBrain();
    }

    protected Brain.Provider<Villager> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        Brain<Villager> brain = this.brainProvider().makeBrain(dynamic);
        this.registerBrainGoals(brain);
        return brain;
    }

    public void refreshBrain(ServerLevel serverLevel) {
        Brain<Villager> brain = this.getBrain();
        brain.stopAll(serverLevel, this);
        this.brain = brain.copyWithoutBehaviors();
        this.registerBrainGoals(this.getBrain());
    }

    private void registerBrainGoals(Brain<Villager> brain) {
        VillagerProfession villagerProfession = this.getVillagerData().getProfession();
        if (this.isBaby()) {
            brain.setSchedule(Schedule.VILLAGER_BABY);
            brain.addActivity(Activity.PLAY, VillagerGoalPackages.getPlayPackage(0.5f));
        } else {
            brain.setSchedule(Schedule.VILLAGER_DEFAULT);
            brain.addActivityWithConditions(Activity.WORK, (ImmutableList<Pair<Integer, Behavior<Villager>>>)VillagerGoalPackages.getWorkPackage(villagerProfession, 0.5f), (Set<Pair<MemoryModuleType<?>, MemoryStatus>>)ImmutableSet.of((Object)Pair.of(MemoryModuleType.JOB_SITE, (Object)((Object)MemoryStatus.VALUE_PRESENT))));
        }
        brain.addActivity(Activity.CORE, VillagerGoalPackages.getCorePackage(villagerProfession, 0.5f));
        brain.addActivityWithConditions(Activity.MEET, (ImmutableList<Pair<Integer, Behavior<Villager>>>)VillagerGoalPackages.getMeetPackage(villagerProfession, 0.5f), (Set<Pair<MemoryModuleType<?>, MemoryStatus>>)ImmutableSet.of((Object)Pair.of(MemoryModuleType.MEETING_POINT, (Object)((Object)MemoryStatus.VALUE_PRESENT))));
        brain.addActivity(Activity.REST, VillagerGoalPackages.getRestPackage(villagerProfession, 0.5f));
        brain.addActivity(Activity.IDLE, VillagerGoalPackages.getIdlePackage(villagerProfession, 0.5f));
        brain.addActivity(Activity.PANIC, VillagerGoalPackages.getPanicPackage(villagerProfession, 0.5f));
        brain.addActivity(Activity.PRE_RAID, VillagerGoalPackages.getPreRaidPackage(villagerProfession, 0.5f));
        brain.addActivity(Activity.RAID, VillagerGoalPackages.getRaidPackage(villagerProfession, 0.5f));
        brain.addActivity(Activity.HIDE, VillagerGoalPackages.getHidePackage(villagerProfession, 0.5f));
        brain.setCoreActivities((Set<Activity>)ImmutableSet.of((Object)Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.setActiveActivityIfPossible(Activity.IDLE);
        brain.updateActivityFromSchedule(this.level.getDayTime(), this.level.getGameTime());
    }

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        if (this.level instanceof ServerLevel) {
            this.refreshBrain((ServerLevel)this.level);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.5).add(Attributes.FOLLOW_RANGE, 48.0);
    }

    public boolean assignProfessionWhenSpawned() {
        return this.assignProfessionWhenSpawned;
    }

    @Override
    protected void customServerAiStep() {
        Raid raid;
        this.level.getProfiler().push("villagerBrain");
        this.getBrain().tick((ServerLevel)this.level, this);
        this.level.getProfiler().pop();
        if (this.assignProfessionWhenSpawned) {
            this.assignProfessionWhenSpawned = false;
        }
        if (!this.isTrading() && this.updateMerchantTimer > 0) {
            --this.updateMerchantTimer;
            if (this.updateMerchantTimer <= 0) {
                if (this.increaseProfessionLevelOnUpdate) {
                    this.increaseMerchantCareer();
                    this.increaseProfessionLevelOnUpdate = false;
                }
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
            }
        }
        if (this.lastTradedPlayer != null && this.level instanceof ServerLevel) {
            ((ServerLevel)this.level).onReputationEvent(ReputationEventType.TRADE, this.lastTradedPlayer, this);
            this.level.broadcastEntityEvent(this, (byte)14);
            this.lastTradedPlayer = null;
        }
        if (!this.isNoAi() && this.random.nextInt(100) == 0 && (raid = ((ServerLevel)this.level).getRaidAt(this.blockPosition())) != null && raid.isActive() && !raid.isOver()) {
            this.level.broadcastEntityEvent(this, (byte)42);
        }
        if (this.getVillagerData().getProfession() == VillagerProfession.NONE && this.isTrading()) {
            this.stopTrading();
        }
        super.customServerAiStep();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getUnhappyCounter() > 0) {
            this.setUnhappyCounter(this.getUnhappyCounter() - 1);
        }
        this.maybeDecayGossip();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (itemStack.getItem() != Items.VILLAGER_SPAWN_EGG && this.isAlive() && !this.isTrading() && !this.isSleeping()) {
            if (this.isBaby()) {
                this.setUnhappy();
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
            boolean bl = this.getOffers().isEmpty();
            if (interactionHand == InteractionHand.MAIN_HAND) {
                if (bl && !this.level.isClientSide) {
                    this.setUnhappy();
                }
                player.awardStat(Stats.TALKED_TO_VILLAGER);
            }
            if (bl) {
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
            if (!this.level.isClientSide && !this.offers.isEmpty()) {
                this.startTrading(player);
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        return super.mobInteract(player, interactionHand);
    }

    private void setUnhappy() {
        this.setUnhappyCounter(40);
        if (!this.level.isClientSide()) {
            this.playSound(SoundEvents.VILLAGER_NO, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    private void startTrading(Player player) {
        this.updateSpecialPrices(player);
        this.setTradingPlayer(player);
        this.openTradingScreen(player, this.getDisplayName(), this.getVillagerData().getLevel());
    }

    @Override
    public void setTradingPlayer(@Nullable Player player) {
        boolean bl = this.getTradingPlayer() != null && player == null;
        super.setTradingPlayer(player);
        if (bl) {
            this.stopTrading();
        }
    }

    @Override
    protected void stopTrading() {
        super.stopTrading();
        this.resetSpecialPrices();
    }

    private void resetSpecialPrices() {
        for (MerchantOffer merchantOffer : this.getOffers()) {
            merchantOffer.resetSpecialPriceDiff();
        }
    }

    @Override
    public boolean canRestock() {
        return true;
    }

    public void restock() {
        this.updateDemand();
        for (MerchantOffer merchantOffer : this.getOffers()) {
            merchantOffer.resetUses();
        }
        this.lastRestockGameTime = this.level.getGameTime();
        ++this.numberOfRestocksToday;
    }

    private boolean needsToRestock() {
        for (MerchantOffer merchantOffer : this.getOffers()) {
            if (!merchantOffer.needsRestock()) continue;
            return true;
        }
        return false;
    }

    private boolean allowedToRestock() {
        return this.numberOfRestocksToday == 0 || this.numberOfRestocksToday < 2 && this.level.getGameTime() > this.lastRestockGameTime + 2400L;
    }

    public boolean shouldRestock() {
        long l = this.lastRestockGameTime + 12000L;
        long l2 = this.level.getGameTime();
        boolean bl = l2 > l;
        long l3 = this.level.getDayTime();
        if (this.lastRestockCheckDayTime > 0L) {
            long l4 = l3 / 24000L;
            long l5 = this.lastRestockCheckDayTime / 24000L;
            bl |= l4 > l5;
        }
        this.lastRestockCheckDayTime = l3;
        if (bl) {
            this.lastRestockGameTime = l2;
            this.resetNumberOfRestocks();
        }
        return this.allowedToRestock() && this.needsToRestock();
    }

    private void catchUpDemand() {
        int n = 2 - this.numberOfRestocksToday;
        if (n > 0) {
            for (MerchantOffer merchantOffer : this.getOffers()) {
                merchantOffer.resetUses();
            }
        }
        for (int i = 0; i < n; ++i) {
            this.updateDemand();
        }
    }

    private void updateDemand() {
        for (MerchantOffer merchantOffer : this.getOffers()) {
            merchantOffer.updateDemand();
        }
    }

    private void updateSpecialPrices(Player player) {
        int n = this.getPlayerReputation(player);
        if (n != 0) {
            for (MerchantOffer merchantOffer : this.getOffers()) {
                merchantOffer.addToSpecialPriceDiff(-Mth.floor((float)n * merchantOffer.getPriceMultiplier()));
            }
        }
        if (player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)) {
            MobEffectInstance mobEffectInstance = player.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
            int n2 = mobEffectInstance.getAmplifier();
            for (MerchantOffer merchantOffer : this.getOffers()) {
                double d = 0.3 + 0.0625 * (double)n2;
                int n3 = (int)Math.floor(d * (double)merchantOffer.getBaseCostA().getCount());
                merchantOffer.addToSpecialPriceDiff(-Math.max(n3, 1));
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        VillagerData.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.getVillagerData()).resultOrPartial(((Logger)LOGGER)::error).ifPresent(tag -> compoundTag.put("VillagerData", (Tag)tag));
        compoundTag.putByte("FoodLevel", this.foodLevel);
        compoundTag.put("Gossips", (Tag)this.gossips.store(NbtOps.INSTANCE).getValue());
        compoundTag.putInt("Xp", this.villagerXp);
        compoundTag.putLong("LastRestock", this.lastRestockGameTime);
        compoundTag.putLong("LastGossipDecay", this.lastGossipDecayTime);
        compoundTag.putInt("RestocksToday", this.numberOfRestocksToday);
        if (this.assignProfessionWhenSpawned) {
            compoundTag.putBoolean("AssignProfessionWhenSpawned", true);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        Object object;
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("VillagerData", 10)) {
            object = VillagerData.CODEC.parse(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)compoundTag.get("VillagerData")));
            object.resultOrPartial(((Logger)LOGGER)::error).ifPresent(this::setVillagerData);
        }
        if (compoundTag.contains("Offers", 10)) {
            this.offers = new MerchantOffers(compoundTag.getCompound("Offers"));
        }
        if (compoundTag.contains("FoodLevel", 1)) {
            this.foodLevel = compoundTag.getByte("FoodLevel");
        }
        object = compoundTag.getList("Gossips", 10);
        this.gossips.update(new Dynamic((DynamicOps)NbtOps.INSTANCE, object));
        if (compoundTag.contains("Xp", 3)) {
            this.villagerXp = compoundTag.getInt("Xp");
        }
        this.lastRestockGameTime = compoundTag.getLong("LastRestock");
        this.lastGossipDecayTime = compoundTag.getLong("LastGossipDecay");
        this.setCanPickUpLoot(true);
        if (this.level instanceof ServerLevel) {
            this.refreshBrain((ServerLevel)this.level);
        }
        this.numberOfRestocksToday = compoundTag.getInt("RestocksToday");
        if (compoundTag.contains("AssignProfessionWhenSpawned")) {
            this.assignProfessionWhenSpawned = compoundTag.getBoolean("AssignProfessionWhenSpawned");
        }
    }

    @Override
    public boolean removeWhenFarAway(double d) {
        return false;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
        }
        if (this.isTrading()) {
            return SoundEvents.VILLAGER_TRADE;
        }
        return SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    public void playWorkSound() {
        SoundEvent soundEvent = this.getVillagerData().getProfession().getWorkSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    public void setVillagerData(VillagerData villagerData) {
        VillagerData villagerData2 = this.getVillagerData();
        if (villagerData2.getProfession() != villagerData.getProfession()) {
            this.offers = null;
        }
        this.entityData.set(DATA_VILLAGER_DATA, villagerData);
    }

    @Override
    public VillagerData getVillagerData() {
        return this.entityData.get(DATA_VILLAGER_DATA);
    }

    @Override
    protected void rewardTradeXp(MerchantOffer merchantOffer) {
        int n = 3 + this.random.nextInt(4);
        this.villagerXp += merchantOffer.getXp();
        this.lastTradedPlayer = this.getTradingPlayer();
        if (this.shouldIncreaseLevel()) {
            this.updateMerchantTimer = 40;
            this.increaseProfessionLevelOnUpdate = true;
            n += 5;
        }
        if (merchantOffer.shouldRewardExp()) {
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.getX(), this.getY() + 0.5, this.getZ(), n));
        }
    }

    @Override
    public void setLastHurtByMob(@Nullable LivingEntity livingEntity) {
        if (livingEntity != null && this.level instanceof ServerLevel) {
            ((ServerLevel)this.level).onReputationEvent(ReputationEventType.VILLAGER_HURT, livingEntity, this);
            if (this.isAlive() && livingEntity instanceof Player) {
                this.level.broadcastEntityEvent(this, (byte)13);
            }
        }
        super.setLastHurtByMob(livingEntity);
    }

    @Override
    public void die(DamageSource damageSource) {
        LOGGER.info("Villager {} died, message: '{}'", (Object)this, (Object)damageSource.getLocalizedDeathMessage(this).getString());
        Entity entity = damageSource.getEntity();
        if (entity != null) {
            this.tellWitnessesThatIWasMurdered(entity);
        }
        this.releaseAllPois();
        super.die(damageSource);
    }

    private void releaseAllPois() {
        this.releasePoi(MemoryModuleType.HOME);
        this.releasePoi(MemoryModuleType.JOB_SITE);
        this.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE);
        this.releasePoi(MemoryModuleType.MEETING_POINT);
    }

    private void tellWitnessesThatIWasMurdered(Entity entity) {
        if (!(this.level instanceof ServerLevel)) {
            return;
        }
        Optional<List<LivingEntity>> optional = this.brain.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES);
        if (!optional.isPresent()) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)this.level;
        optional.get().stream().filter(livingEntity -> livingEntity instanceof ReputationEventHandler).forEach(livingEntity -> serverLevel.onReputationEvent(ReputationEventType.VILLAGER_KILLED, entity, (ReputationEventHandler)((Object)livingEntity)));
    }

    public void releasePoi(MemoryModuleType<GlobalPos> memoryModuleType) {
        if (!(this.level instanceof ServerLevel)) {
            return;
        }
        MinecraftServer minecraftServer = ((ServerLevel)this.level).getServer();
        this.brain.getMemory(memoryModuleType).ifPresent(globalPos -> {
            ServerLevel serverLevel = minecraftServer.getLevel(globalPos.dimension());
            if (serverLevel == null) {
                return;
            }
            PoiManager poiManager = serverLevel.getPoiManager();
            Optional<PoiType> optional = poiManager.getType(globalPos.pos());
            BiPredicate<Villager, PoiType> biPredicate = POI_MEMORIES.get(memoryModuleType);
            if (optional.isPresent() && biPredicate.test(this, optional.get())) {
                poiManager.release(globalPos.pos());
                DebugPackets.sendPoiTicketCountPacket(serverLevel, globalPos.pos());
            }
        });
    }

    @Override
    public boolean canBreed() {
        return this.foodLevel + this.countFoodPointsInInventory() >= 12 && this.getAge() == 0;
    }

    private boolean hungry() {
        return this.foodLevel < 12;
    }

    private void eatUntilFull() {
        if (!this.hungry() || this.countFoodPointsInInventory() == 0) {
            return;
        }
        for (int i = 0; i < this.getInventory().getContainerSize(); ++i) {
            Integer n;
            int n2;
            ItemStack itemStack = this.getInventory().getItem(i);
            if (itemStack.isEmpty() || (n = FOOD_POINTS.get(itemStack.getItem())) == null) continue;
            for (int j = n2 = itemStack.getCount(); j > 0; --j) {
                this.foodLevel = (byte)(this.foodLevel + n);
                this.getInventory().removeItem(i, 1);
                if (this.hungry()) continue;
                return;
            }
        }
    }

    public int getPlayerReputation(Player player) {
        return this.gossips.getReputation(player.getUUID(), gossipType -> true);
    }

    private void digestFood(int n) {
        this.foodLevel = (byte)(this.foodLevel - n);
    }

    public void eatAndDigestFood() {
        this.eatUntilFull();
        this.digestFood(12);
    }

    public void setOffers(MerchantOffers merchantOffers) {
        this.offers = merchantOffers;
    }

    private boolean shouldIncreaseLevel() {
        int n = this.getVillagerData().getLevel();
        return VillagerData.canLevelUp(n) && this.villagerXp >= VillagerData.getMaxXpPerLevel(n);
    }

    private void increaseMerchantCareer() {
        this.setVillagerData(this.getVillagerData().setLevel(this.getVillagerData().getLevel() + 1));
        this.updateTrades();
    }

    @Override
    protected Component getTypeName() {
        return new TranslatableComponent(this.getType().getDescriptionId() + '.' + Registry.VILLAGER_PROFESSION.getKey(this.getVillagerData().getProfession()).getPath());
    }

    @Override
    public void handleEntityEvent(byte by) {
        if (by == 12) {
            this.addParticlesAroundSelf(ParticleTypes.HEART);
        } else if (by == 13) {
            this.addParticlesAroundSelf(ParticleTypes.ANGRY_VILLAGER);
        } else if (by == 14) {
            this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
        } else if (by == 42) {
            this.addParticlesAroundSelf(ParticleTypes.SPLASH);
        } else {
            super.handleEntityEvent(by);
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        if (mobSpawnType == MobSpawnType.BREEDING) {
            this.setVillagerData(this.getVillagerData().setProfession(VillagerProfession.NONE));
        }
        if (mobSpawnType == MobSpawnType.COMMAND || mobSpawnType == MobSpawnType.SPAWN_EGG || mobSpawnType == MobSpawnType.SPAWNER || mobSpawnType == MobSpawnType.DISPENSER) {
            this.setVillagerData(this.getVillagerData().setType(VillagerType.byBiome(serverLevelAccessor.getBiomeName(this.blockPosition()))));
        }
        if (mobSpawnType == MobSpawnType.STRUCTURE) {
            this.assignProfessionWhenSpawned = true;
        }
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    @Override
    public Villager getBreedOffspring(ServerLevel serverLevel, AgableMob agableMob) {
        double d = this.random.nextDouble();
        VillagerType villagerType = d < 0.5 ? VillagerType.byBiome(serverLevel.getBiomeName(this.blockPosition())) : (d < 0.75 ? this.getVillagerData().getType() : ((Villager)agableMob).getVillagerData().getType());
        Villager villager = new Villager(EntityType.VILLAGER, serverLevel, villagerType);
        villager.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(villager.blockPosition()), MobSpawnType.BREEDING, null, null);
        return villager;
    }

    @Override
    public void thunderHit(ServerLevel serverLevel, LightningBolt lightningBolt) {
        if (serverLevel.getDifficulty() != Difficulty.PEACEFUL) {
            LOGGER.info("Villager {} was struck by lightning {}.", (Object)this, (Object)lightningBolt);
            Witch witch = EntityType.WITCH.create(serverLevel);
            witch.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
            witch.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(witch.blockPosition()), MobSpawnType.CONVERSION, null, null);
            witch.setNoAi(this.isNoAi());
            if (this.hasCustomName()) {
                witch.setCustomName(this.getCustomName());
                witch.setCustomNameVisible(this.isCustomNameVisible());
            }
            witch.setPersistenceRequired();
            serverLevel.addFreshEntityWithPassengers(witch);
            this.releaseAllPois();
            this.remove();
        } else {
            super.thunderHit(serverLevel, lightningBolt);
        }
    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        ItemStack itemStack = itemEntity.getItem();
        if (this.wantsToPickUp(itemStack)) {
            SimpleContainer simpleContainer = this.getInventory();
            boolean bl = simpleContainer.canAddItem(itemStack);
            if (!bl) {
                return;
            }
            this.onItemPickup(itemEntity);
            this.take(itemEntity, itemStack.getCount());
            ItemStack itemStack2 = simpleContainer.addItem(itemStack);
            if (itemStack2.isEmpty()) {
                itemEntity.remove();
            } else {
                itemStack.setCount(itemStack2.getCount());
            }
        }
    }

    @Override
    public boolean wantsToPickUp(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return (WANTED_ITEMS.contains(item) || this.getVillagerData().getProfession().getRequestedItems().contains((Object)item)) && this.getInventory().canAddItem(itemStack);
    }

    public boolean hasExcessFood() {
        return this.countFoodPointsInInventory() >= 24;
    }

    public boolean wantsMoreFood() {
        return this.countFoodPointsInInventory() < 12;
    }

    private int countFoodPointsInInventory() {
        SimpleContainer simpleContainer = this.getInventory();
        return FOOD_POINTS.entrySet().stream().mapToInt(entry -> simpleContainer.countItem((Item)entry.getKey()) * (Integer)entry.getValue()).sum();
    }

    public boolean hasFarmSeeds() {
        return this.getInventory().hasAnyOf((Set<Item>)ImmutableSet.of((Object)Items.WHEAT_SEEDS, (Object)Items.POTATO, (Object)Items.CARROT, (Object)Items.BEETROOT_SEEDS));
    }

    @Override
    protected void updateTrades() {
        VillagerData villagerData = this.getVillagerData();
        Int2ObjectMap<VillagerTrades.ItemListing[]> int2ObjectMap = VillagerTrades.TRADES.get(villagerData.getProfession());
        if (int2ObjectMap == null || int2ObjectMap.isEmpty()) {
            return;
        }
        VillagerTrades.ItemListing[] arritemListing = (VillagerTrades.ItemListing[])int2ObjectMap.get(villagerData.getLevel());
        if (arritemListing == null) {
            return;
        }
        MerchantOffers merchantOffers = this.getOffers();
        this.addOffersFromItemListings(merchantOffers, arritemListing, 2);
    }

    public void gossip(ServerLevel serverLevel, Villager villager, long l) {
        if (l >= this.lastGossipTime && l < this.lastGossipTime + 1200L || l >= villager.lastGossipTime && l < villager.lastGossipTime + 1200L) {
            return;
        }
        this.gossips.transferFrom(villager.gossips, this.random, 10);
        this.lastGossipTime = l;
        villager.lastGossipTime = l;
        this.spawnGolemIfNeeded(serverLevel, l, 5);
    }

    private void maybeDecayGossip() {
        long l = this.level.getGameTime();
        if (this.lastGossipDecayTime == 0L) {
            this.lastGossipDecayTime = l;
            return;
        }
        if (l < this.lastGossipDecayTime + 24000L) {
            return;
        }
        this.gossips.decay();
        this.lastGossipDecayTime = l;
    }

    public void spawnGolemIfNeeded(ServerLevel serverLevel, long l, int n) {
        if (!this.wantsToSpawnGolem(l)) {
            return;
        }
        AABB aABB = this.getBoundingBox().inflate(10.0, 10.0, 10.0);
        List<Villager> list = serverLevel.getEntitiesOfClass(Villager.class, aABB);
        List list2 = list.stream().filter(villager -> villager.wantsToSpawnGolem(l)).limit(5L).collect(Collectors.toList());
        if (list2.size() < n) {
            return;
        }
        IronGolem ironGolem = this.trySpawnGolem(serverLevel);
        if (ironGolem == null) {
            return;
        }
        list.forEach(GolemSensor::golemDetected);
    }

    public boolean wantsToSpawnGolem(long l) {
        if (!this.golemSpawnConditionsMet(this.level.getGameTime())) {
            return false;
        }
        return !this.brain.hasMemoryValue(MemoryModuleType.GOLEM_DETECTED_RECENTLY);
    }

    @Nullable
    private IronGolem trySpawnGolem(ServerLevel serverLevel) {
        BlockPos blockPos = this.blockPosition();
        for (int i = 0; i < 10; ++i) {
            double d;
            IronGolem ironGolem;
            double d2 = serverLevel.random.nextInt(16) - 8;
            BlockPos blockPos2 = this.findSpawnPositionForGolemInColumn(blockPos, d2, d = (double)(serverLevel.random.nextInt(16) - 8));
            if (blockPos2 == null || (ironGolem = EntityType.IRON_GOLEM.create(serverLevel, null, null, null, blockPos2, MobSpawnType.MOB_SUMMONED, false, false)) == null) continue;
            if (ironGolem.checkSpawnRules(serverLevel, MobSpawnType.MOB_SUMMONED) && ironGolem.checkSpawnObstruction(serverLevel)) {
                serverLevel.addFreshEntityWithPassengers(ironGolem);
                return ironGolem;
            }
            ironGolem.remove();
        }
        return null;
    }

    @Nullable
    private BlockPos findSpawnPositionForGolemInColumn(BlockPos blockPos, double d, double d2) {
        int n = 6;
        BlockPos blockPos2 = blockPos.offset(d, 6.0, d2);
        BlockState blockState = this.level.getBlockState(blockPos2);
        for (int i = 6; i >= -6; --i) {
            BlockPos blockPos3 = blockPos2;
            BlockState blockState2 = blockState;
            blockPos2 = blockPos3.below();
            blockState = this.level.getBlockState(blockPos2);
            if (!blockState2.isAir() && !blockState2.getMaterial().isLiquid() || !blockState.getMaterial().isSolidBlocking()) continue;
            return blockPos3;
        }
        return null;
    }

    @Override
    public void onReputationEventFrom(ReputationEventType reputationEventType, Entity entity) {
        if (reputationEventType == ReputationEventType.ZOMBIE_VILLAGER_CURED) {
            this.gossips.add(entity.getUUID(), GossipType.MAJOR_POSITIVE, 20);
            this.gossips.add(entity.getUUID(), GossipType.MINOR_POSITIVE, 25);
        } else if (reputationEventType == ReputationEventType.TRADE) {
            this.gossips.add(entity.getUUID(), GossipType.TRADING, 2);
        } else if (reputationEventType == ReputationEventType.VILLAGER_HURT) {
            this.gossips.add(entity.getUUID(), GossipType.MINOR_NEGATIVE, 25);
        } else if (reputationEventType == ReputationEventType.VILLAGER_KILLED) {
            this.gossips.add(entity.getUUID(), GossipType.MAJOR_NEGATIVE, 25);
        }
    }

    @Override
    public int getVillagerXp() {
        return this.villagerXp;
    }

    public void setVillagerXp(int n) {
        this.villagerXp = n;
    }

    private void resetNumberOfRestocks() {
        this.catchUpDemand();
        this.numberOfRestocksToday = 0;
    }

    public GossipContainer getGossips() {
        return this.gossips;
    }

    public void setGossips(Tag tag) {
        this.gossips.update(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)tag));
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public void startSleeping(BlockPos blockPos) {
        super.startSleeping(blockPos);
        this.brain.setMemory(MemoryModuleType.LAST_SLEPT, this.level.getGameTime());
        this.brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        this.brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    @Override
    public void stopSleeping() {
        super.stopSleeping();
        this.brain.setMemory(MemoryModuleType.LAST_WOKEN, this.level.getGameTime());
    }

    private boolean golemSpawnConditionsMet(long l) {
        Optional<Long> optional = this.brain.getMemory(MemoryModuleType.LAST_SLEPT);
        if (optional.isPresent()) {
            return l - optional.get() < 24000L;
        }
        return false;
    }

    @Override
    public /* synthetic */ AgableMob getBreedOffspring(ServerLevel serverLevel, AgableMob agableMob) {
        return this.getBreedOffspring(serverLevel, agableMob);
    }
}

