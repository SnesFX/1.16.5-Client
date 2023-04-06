/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.level;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.advancements.critereon.EffectsChangedTrigger;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.LevitationTrigger;
import net.minecraft.advancements.critereon.LocationTrigger;
import net.minecraft.advancements.critereon.NetherTravelTrigger;
import net.minecraft.advancements.critereon.TickTrigger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundHorseScreenOpenPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundResourcePackPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ComplexItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ServerItemCooldowns;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.PortalForcer;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayer
extends Player
implements ContainerListener {
    private static final Logger LOGGER = LogManager.getLogger();
    public ServerGamePacketListenerImpl connection;
    public final MinecraftServer server;
    public final ServerPlayerGameMode gameMode;
    private final List<Integer> entitiesToRemove = Lists.newLinkedList();
    private final PlayerAdvancements advancements;
    private final ServerStatsCounter stats;
    private float lastRecordedHealthAndAbsorption = Float.MIN_VALUE;
    private int lastRecordedFoodLevel = Integer.MIN_VALUE;
    private int lastRecordedAirLevel = Integer.MIN_VALUE;
    private int lastRecordedArmor = Integer.MIN_VALUE;
    private int lastRecordedLevel = Integer.MIN_VALUE;
    private int lastRecordedExperience = Integer.MIN_VALUE;
    private float lastSentHealth = -1.0E8f;
    private int lastSentFood = -99999999;
    private boolean lastFoodSaturationZero = true;
    private int lastSentExp = -99999999;
    private int spawnInvulnerableTime = 60;
    private ChatVisiblity chatVisibility;
    private boolean canChatColor = true;
    private long lastActionTime = Util.getMillis();
    private Entity camera;
    private boolean isChangingDimension;
    private boolean seenCredits;
    private final ServerRecipeBook recipeBook = new ServerRecipeBook();
    private Vec3 levitationStartPos;
    private int levitationStartTime;
    private boolean disconnected;
    @Nullable
    private Vec3 enteredNetherPosition;
    private SectionPos lastSectionPos = SectionPos.of(0, 0, 0);
    private ResourceKey<Level> respawnDimension = Level.OVERWORLD;
    @Nullable
    private BlockPos respawnPosition;
    private boolean respawnForced;
    private float respawnAngle;
    @Nullable
    private final TextFilter textFilter;
    private int containerCounter;
    public boolean ignoreSlotUpdateHack;
    public int latency;
    public boolean wonGame;

    public ServerPlayer(MinecraftServer minecraftServer, ServerLevel serverLevel, GameProfile gameProfile, ServerPlayerGameMode serverPlayerGameMode) {
        super(serverLevel, serverLevel.getSharedSpawnPos(), serverLevel.getSharedSpawnAngle(), gameProfile);
        serverPlayerGameMode.player = this;
        this.gameMode = serverPlayerGameMode;
        this.server = minecraftServer;
        this.stats = minecraftServer.getPlayerList().getPlayerStats(this);
        this.advancements = minecraftServer.getPlayerList().getPlayerAdvancements(this);
        this.maxUpStep = 1.0f;
        this.fudgeSpawnLocation(serverLevel);
        this.textFilter = minecraftServer.createTextFilterForPlayer(this);
    }

    private void fudgeSpawnLocation(ServerLevel serverLevel) {
        BlockPos blockPos = serverLevel.getSharedSpawnPos();
        if (serverLevel.dimensionType().hasSkyLight() && serverLevel.getServer().getWorldData().getGameType() != GameType.ADVENTURE) {
            long l;
            long l2;
            int n = Math.max(0, this.server.getSpawnRadius(serverLevel));
            int n2 = Mth.floor(serverLevel.getWorldBorder().getDistanceToBorder(blockPos.getX(), blockPos.getZ()));
            if (n2 < n) {
                n = n2;
            }
            if (n2 <= 1) {
                n = 1;
            }
            int n3 = (l2 = (l = (long)(n * 2 + 1)) * l) > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)l2;
            int n4 = this.getCoprime(n3);
            int n5 = new Random().nextInt(n3);
            for (int i = 0; i < n3; ++i) {
                int n6 = (n5 + n4 * i) % n3;
                int n7 = n6 % (n * 2 + 1);
                int n8 = n6 / (n * 2 + 1);
                BlockPos blockPos2 = PlayerRespawnLogic.getOverworldRespawnPos(serverLevel, blockPos.getX() + n7 - n, blockPos.getZ() + n8 - n, false);
                if (blockPos2 == null) continue;
                this.moveTo(blockPos2, 0.0f, 0.0f);
                if (!serverLevel.noCollision(this)) {
                    continue;
                }
                break;
            }
        } else {
            this.moveTo(blockPos, 0.0f, 0.0f);
            while (!serverLevel.noCollision(this) && this.getY() < 255.0) {
                this.setPos(this.getX(), this.getY() + 1.0, this.getZ());
            }
        }
    }

    private int getCoprime(int n) {
        return n <= 16 ? n - 1 : 17;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("playerGameType", 99)) {
            if (this.getServer().getForceGameType()) {
                this.gameMode.setGameModeForPlayer(this.getServer().getDefaultGameType(), GameType.NOT_SET);
            } else {
                this.gameMode.setGameModeForPlayer(GameType.byId(compoundTag.getInt("playerGameType")), compoundTag.contains("previousPlayerGameType", 3) ? GameType.byId(compoundTag.getInt("previousPlayerGameType")) : GameType.NOT_SET);
            }
        }
        if (compoundTag.contains("enteredNetherPosition", 10)) {
            CompoundTag compoundTag2 = compoundTag.getCompound("enteredNetherPosition");
            this.enteredNetherPosition = new Vec3(compoundTag2.getDouble("x"), compoundTag2.getDouble("y"), compoundTag2.getDouble("z"));
        }
        this.seenCredits = compoundTag.getBoolean("seenCredits");
        if (compoundTag.contains("recipeBook", 10)) {
            this.recipeBook.fromNbt(compoundTag.getCompound("recipeBook"), this.server.getRecipeManager());
        }
        if (this.isSleeping()) {
            this.stopSleeping();
        }
        if (compoundTag.contains("SpawnX", 99) && compoundTag.contains("SpawnY", 99) && compoundTag.contains("SpawnZ", 99)) {
            this.respawnPosition = new BlockPos(compoundTag.getInt("SpawnX"), compoundTag.getInt("SpawnY"), compoundTag.getInt("SpawnZ"));
            this.respawnForced = compoundTag.getBoolean("SpawnForced");
            this.respawnAngle = compoundTag.getFloat("SpawnAngle");
            if (compoundTag.contains("SpawnDimension")) {
                this.respawnDimension = Level.RESOURCE_KEY_CODEC.parse((DynamicOps)NbtOps.INSTANCE, (Object)compoundTag.get("SpawnDimension")).resultOrPartial(((Logger)LOGGER)::error).orElse(Level.OVERWORLD);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        Object object;
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("playerGameType", this.gameMode.getGameModeForPlayer().getId());
        compoundTag.putInt("previousPlayerGameType", this.gameMode.getPreviousGameModeForPlayer().getId());
        compoundTag.putBoolean("seenCredits", this.seenCredits);
        if (this.enteredNetherPosition != null) {
            object = new CompoundTag();
            ((CompoundTag)object).putDouble("x", this.enteredNetherPosition.x);
            ((CompoundTag)object).putDouble("y", this.enteredNetherPosition.y);
            ((CompoundTag)object).putDouble("z", this.enteredNetherPosition.z);
            compoundTag.put("enteredNetherPosition", (Tag)object);
        }
        object = this.getRootVehicle();
        Entity entity = this.getVehicle();
        if (entity != null && object != this && ((Entity)object).hasOnePlayerPassenger()) {
            CompoundTag compoundTag2 = new CompoundTag();
            CompoundTag compoundTag3 = new CompoundTag();
            ((Entity)object).save(compoundTag3);
            compoundTag2.putUUID("Attach", entity.getUUID());
            compoundTag2.put("Entity", compoundTag3);
            compoundTag.put("RootVehicle", compoundTag2);
        }
        compoundTag.put("recipeBook", this.recipeBook.toNbt());
        compoundTag.putString("Dimension", this.level.dimension().location().toString());
        if (this.respawnPosition != null) {
            compoundTag.putInt("SpawnX", this.respawnPosition.getX());
            compoundTag.putInt("SpawnY", this.respawnPosition.getY());
            compoundTag.putInt("SpawnZ", this.respawnPosition.getZ());
            compoundTag.putBoolean("SpawnForced", this.respawnForced);
            compoundTag.putFloat("SpawnAngle", this.respawnAngle);
            ResourceLocation.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.respawnDimension.location()).resultOrPartial(((Logger)LOGGER)::error).ifPresent(tag -> compoundTag.put("SpawnDimension", (Tag)tag));
        }
    }

    public void setExperiencePoints(int n) {
        float f = this.getXpNeededForNextLevel();
        float f2 = (f - 1.0f) / f;
        this.experienceProgress = Mth.clamp((float)n / f, 0.0f, f2);
        this.lastSentExp = -1;
    }

    public void setExperienceLevels(int n) {
        this.experienceLevel = n;
        this.lastSentExp = -1;
    }

    @Override
    public void giveExperienceLevels(int n) {
        super.giveExperienceLevels(n);
        this.lastSentExp = -1;
    }

    @Override
    public void onEnchantmentPerformed(ItemStack itemStack, int n) {
        super.onEnchantmentPerformed(itemStack, n);
        this.lastSentExp = -1;
    }

    public void initMenu() {
        this.containerMenu.addSlotListener(this);
    }

    @Override
    public void onEnterCombat() {
        super.onEnterCombat();
        this.connection.send(new ClientboundPlayerCombatPacket(this.getCombatTracker(), ClientboundPlayerCombatPacket.Event.ENTER_COMBAT));
    }

    @Override
    public void onLeaveCombat() {
        super.onLeaveCombat();
        this.connection.send(new ClientboundPlayerCombatPacket(this.getCombatTracker(), ClientboundPlayerCombatPacket.Event.END_COMBAT));
    }

    @Override
    protected void onInsideBlock(BlockState blockState) {
        CriteriaTriggers.ENTER_BLOCK.trigger(this, blockState);
    }

    @Override
    protected ItemCooldowns createItemCooldowns() {
        return new ServerItemCooldowns(this);
    }

    @Override
    public void tick() {
        this.gameMode.tick();
        --this.spawnInvulnerableTime;
        if (this.invulnerableTime > 0) {
            --this.invulnerableTime;
        }
        this.containerMenu.broadcastChanges();
        if (!this.level.isClientSide && !this.containerMenu.stillValid(this)) {
            this.closeContainer();
            this.containerMenu = this.inventoryMenu;
        }
        while (!this.entitiesToRemove.isEmpty()) {
            int n = Math.min(this.entitiesToRemove.size(), Integer.MAX_VALUE);
            int[] arrn = new int[n];
            Iterator<Integer> iterator = this.entitiesToRemove.iterator();
            int n2 = 0;
            while (iterator.hasNext() && n2 < n) {
                arrn[n2++] = iterator.next();
                iterator.remove();
            }
            this.connection.send(new ClientboundRemoveEntitiesPacket(arrn));
        }
        Entity entity = this.getCamera();
        if (entity != this) {
            if (entity.isAlive()) {
                this.absMoveTo(entity.getX(), entity.getY(), entity.getZ(), entity.yRot, entity.xRot);
                this.getLevel().getChunkSource().move(this);
                if (this.wantsToStopRiding()) {
                    this.setCamera(this);
                }
            } else {
                this.setCamera(this);
            }
        }
        CriteriaTriggers.TICK.trigger(this);
        if (this.levitationStartPos != null) {
            CriteriaTriggers.LEVITATION.trigger(this, this.levitationStartPos, this.tickCount - this.levitationStartTime);
        }
        this.advancements.flushDirty(this);
    }

    public void doTick() {
        try {
            if (!this.isSpectator() || this.level.hasChunkAt(this.blockPosition())) {
                super.tick();
            }
            for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
                Packet<?> packet;
                ItemStack itemStack = this.inventory.getItem(i);
                if (!itemStack.getItem().isComplex() || (packet = ((ComplexItem)itemStack.getItem()).getUpdatePacket(itemStack, this.level, this)) == null) continue;
                this.connection.send(packet);
            }
            if (this.getHealth() != this.lastSentHealth || this.lastSentFood != this.foodData.getFoodLevel() || this.foodData.getSaturationLevel() == 0.0f != this.lastFoodSaturationZero) {
                this.connection.send(new ClientboundSetHealthPacket(this.getHealth(), this.foodData.getFoodLevel(), this.foodData.getSaturationLevel()));
                this.lastSentHealth = this.getHealth();
                this.lastSentFood = this.foodData.getFoodLevel();
                boolean bl = this.lastFoodSaturationZero = this.foodData.getSaturationLevel() == 0.0f;
            }
            if (this.getHealth() + this.getAbsorptionAmount() != this.lastRecordedHealthAndAbsorption) {
                this.lastRecordedHealthAndAbsorption = this.getHealth() + this.getAbsorptionAmount();
                this.updateScoreForCriteria(ObjectiveCriteria.HEALTH, Mth.ceil(this.lastRecordedHealthAndAbsorption));
            }
            if (this.foodData.getFoodLevel() != this.lastRecordedFoodLevel) {
                this.lastRecordedFoodLevel = this.foodData.getFoodLevel();
                this.updateScoreForCriteria(ObjectiveCriteria.FOOD, Mth.ceil(this.lastRecordedFoodLevel));
            }
            if (this.getAirSupply() != this.lastRecordedAirLevel) {
                this.lastRecordedAirLevel = this.getAirSupply();
                this.updateScoreForCriteria(ObjectiveCriteria.AIR, Mth.ceil(this.lastRecordedAirLevel));
            }
            if (this.getArmorValue() != this.lastRecordedArmor) {
                this.lastRecordedArmor = this.getArmorValue();
                this.updateScoreForCriteria(ObjectiveCriteria.ARMOR, Mth.ceil(this.lastRecordedArmor));
            }
            if (this.totalExperience != this.lastRecordedExperience) {
                this.lastRecordedExperience = this.totalExperience;
                this.updateScoreForCriteria(ObjectiveCriteria.EXPERIENCE, Mth.ceil(this.lastRecordedExperience));
            }
            if (this.experienceLevel != this.lastRecordedLevel) {
                this.lastRecordedLevel = this.experienceLevel;
                this.updateScoreForCriteria(ObjectiveCriteria.LEVEL, Mth.ceil(this.lastRecordedLevel));
            }
            if (this.totalExperience != this.lastSentExp) {
                this.lastSentExp = this.totalExperience;
                this.connection.send(new ClientboundSetExperiencePacket(this.experienceProgress, this.totalExperience, this.experienceLevel));
            }
            if (this.tickCount % 20 == 0) {
                CriteriaTriggers.LOCATION.trigger(this);
            }
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Ticking player");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Player being ticked");
            this.fillCrashReportCategory(crashReportCategory);
            throw new ReportedException(crashReport);
        }
    }

    private void updateScoreForCriteria(ObjectiveCriteria objectiveCriteria, int n) {
        this.getScoreboard().forAllObjectives(objectiveCriteria, this.getScoreboardName(), score -> score.setScore(n));
    }

    @Override
    public void die(DamageSource damageSource) {
        Object object;
        boolean bl = this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES);
        if (bl) {
            object = this.getCombatTracker().getDeathMessage();
            this.connection.send(new ClientboundPlayerCombatPacket(this.getCombatTracker(), ClientboundPlayerCombatPacket.Event.ENTITY_DIED, (Component)object), (GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener)arg_0 -> this.lambda$die$3((Component)object, arg_0)));
            Team team = this.getTeam();
            if (team == null || team.getDeathMessageVisibility() == Team.Visibility.ALWAYS) {
                this.server.getPlayerList().broadcastMessage((Component)object, ChatType.SYSTEM, Util.NIL_UUID);
            } else if (team.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OTHER_TEAMS) {
                this.server.getPlayerList().broadcastToTeam(this, (Component)object);
            } else if (team.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OWN_TEAM) {
                this.server.getPlayerList().broadcastToAllExceptTeam(this, (Component)object);
            }
        } else {
            this.connection.send(new ClientboundPlayerCombatPacket(this.getCombatTracker(), ClientboundPlayerCombatPacket.Event.ENTITY_DIED));
        }
        this.removeEntitiesOnShoulder();
        if (this.level.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
            this.tellNeutralMobsThatIDied();
        }
        if (!this.isSpectator()) {
            this.dropAllDeathLoot(damageSource);
        }
        this.getScoreboard().forAllObjectives(ObjectiveCriteria.DEATH_COUNT, this.getScoreboardName(), Score::increment);
        object = this.getKillCredit();
        if (object != null) {
            this.awardStat(Stats.ENTITY_KILLED_BY.get(((Entity)object).getType()));
            ((Entity)object).awardKillScore(this, this.deathScore, damageSource);
            this.createWitherRose((LivingEntity)object);
        }
        this.level.broadcastEntityEvent(this, (byte)3);
        this.awardStat(Stats.DEATHS);
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        this.clearFire();
        this.setSharedFlag(0, false);
        this.getCombatTracker().recheckStatus();
    }

    private void tellNeutralMobsThatIDied() {
        AABB aABB = new AABB(this.blockPosition()).inflate(32.0, 10.0, 32.0);
        this.level.getLoadedEntitiesOfClass(Mob.class, aABB).stream().filter(mob -> mob instanceof NeutralMob).forEach(mob -> ((NeutralMob)((Object)mob)).playerDied(this));
    }

    @Override
    public void awardKillScore(Entity entity, int n, DamageSource damageSource) {
        if (entity == this) {
            return;
        }
        super.awardKillScore(entity, n, damageSource);
        this.increaseScore(n);
        String string = this.getScoreboardName();
        String string2 = entity.getScoreboardName();
        this.getScoreboard().forAllObjectives(ObjectiveCriteria.KILL_COUNT_ALL, string, Score::increment);
        if (entity instanceof Player) {
            this.awardStat(Stats.PLAYER_KILLS);
            this.getScoreboard().forAllObjectives(ObjectiveCriteria.KILL_COUNT_PLAYERS, string, Score::increment);
        } else {
            this.awardStat(Stats.MOB_KILLS);
        }
        this.handleTeamKill(string, string2, ObjectiveCriteria.TEAM_KILL);
        this.handleTeamKill(string2, string, ObjectiveCriteria.KILLED_BY_TEAM);
        CriteriaTriggers.PLAYER_KILLED_ENTITY.trigger(this, entity, damageSource);
    }

    private void handleTeamKill(String string, String string2, ObjectiveCriteria[] arrobjectiveCriteria) {
        int n;
        PlayerTeam playerTeam = this.getScoreboard().getPlayersTeam(string2);
        if (playerTeam != null && (n = playerTeam.getColor().getId()) >= 0 && n < arrobjectiveCriteria.length) {
            this.getScoreboard().forAllObjectives(arrobjectiveCriteria[n], string, Score::increment);
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        boolean bl;
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        boolean bl2 = bl = this.server.isDedicatedServer() && this.isPvpAllowed() && "fall".equals(damageSource.msgId);
        if (!bl && this.spawnInvulnerableTime > 0 && damageSource != DamageSource.OUT_OF_WORLD) {
            return false;
        }
        if (damageSource instanceof EntityDamageSource) {
            AbstractArrow abstractArrow;
            Entity entity;
            Entity entity2 = damageSource.getEntity();
            if (entity2 instanceof Player && !this.canHarmPlayer((Player)entity2)) {
                return false;
            }
            if (entity2 instanceof AbstractArrow && (entity = (abstractArrow = (AbstractArrow)entity2).getOwner()) instanceof Player && !this.canHarmPlayer((Player)entity)) {
                return false;
            }
        }
        return super.hurt(damageSource, f);
    }

    @Override
    public boolean canHarmPlayer(Player player) {
        if (!this.isPvpAllowed()) {
            return false;
        }
        return super.canHarmPlayer(player);
    }

    private boolean isPvpAllowed() {
        return this.server.isPvpAllowed();
    }

    @Nullable
    @Override
    protected PortalInfo findDimensionEntryPoint(ServerLevel serverLevel) {
        PortalInfo portalInfo = super.findDimensionEntryPoint(serverLevel);
        if (portalInfo != null && this.level.dimension() == Level.OVERWORLD && serverLevel.dimension() == Level.END) {
            Vec3 vec3 = portalInfo.pos.add(0.0, -1.0, 0.0);
            return new PortalInfo(vec3, Vec3.ZERO, 90.0f, 0.0f);
        }
        return portalInfo;
    }

    @Nullable
    @Override
    public Entity changeDimension(ServerLevel serverLevel) {
        this.isChangingDimension = true;
        ServerLevel serverLevel2 = this.getLevel();
        ResourceKey<Level> resourceKey = serverLevel2.dimension();
        if (resourceKey == Level.END && serverLevel.dimension() == Level.OVERWORLD) {
            this.unRide();
            this.getLevel().removePlayerImmediately(this);
            if (!this.wonGame) {
                this.wonGame = true;
                this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, this.seenCredits ? 0.0f : 1.0f));
                this.seenCredits = true;
            }
            return this;
        }
        LevelData levelData = serverLevel.getLevelData();
        this.connection.send(new ClientboundRespawnPacket(serverLevel.dimensionType(), serverLevel.dimension(), BiomeManager.obfuscateSeed(serverLevel.getSeed()), this.gameMode.getGameModeForPlayer(), this.gameMode.getPreviousGameModeForPlayer(), serverLevel.isDebug(), serverLevel.isFlat(), true));
        this.connection.send(new ClientboundChangeDifficultyPacket(levelData.getDifficulty(), levelData.isDifficultyLocked()));
        PlayerList playerList = this.server.getPlayerList();
        playerList.sendPlayerPermissionLevel(this);
        serverLevel2.removePlayerImmediately(this);
        this.removed = false;
        PortalInfo portalInfo = this.findDimensionEntryPoint(serverLevel);
        if (portalInfo != null) {
            serverLevel2.getProfiler().push("moving");
            if (resourceKey == Level.OVERWORLD && serverLevel.dimension() == Level.NETHER) {
                this.enteredNetherPosition = this.position();
            } else if (serverLevel.dimension() == Level.END) {
                this.createEndPlatform(serverLevel, new BlockPos(portalInfo.pos));
            }
            serverLevel2.getProfiler().pop();
            serverLevel2.getProfiler().push("placing");
            this.setLevel(serverLevel);
            serverLevel.addDuringPortalTeleport(this);
            this.setRot(portalInfo.yRot, portalInfo.xRot);
            this.moveTo(portalInfo.pos.x, portalInfo.pos.y, portalInfo.pos.z);
            serverLevel2.getProfiler().pop();
            this.triggerDimensionChangeTriggers(serverLevel2);
            this.gameMode.setLevel(serverLevel);
            this.connection.send(new ClientboundPlayerAbilitiesPacket(this.abilities));
            playerList.sendLevelInfo(this, serverLevel);
            playerList.sendAllPlayerInfo(this);
            for (MobEffectInstance mobEffectInstance : this.getActiveEffects()) {
                this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), mobEffectInstance));
            }
            this.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
            this.lastSentExp = -1;
            this.lastSentHealth = -1.0f;
            this.lastSentFood = -1;
        }
        return this;
    }

    private void createEndPlatform(ServerLevel serverLevel, BlockPos blockPos) {
        BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                for (int k = -1; k < 3; ++k) {
                    BlockState blockState = k == -1 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState();
                    serverLevel.setBlockAndUpdate(mutableBlockPos.set(blockPos).move(j, k, i), blockState);
                }
            }
        }
    }

    @Override
    protected Optional<BlockUtil.FoundRectangle> getExitPortal(ServerLevel serverLevel, BlockPos blockPos, boolean bl) {
        Optional<BlockUtil.FoundRectangle> optional = super.getExitPortal(serverLevel, blockPos, bl);
        if (optional.isPresent()) {
            return optional;
        }
        Direction.Axis axis = this.level.getBlockState(this.portalEntrancePos).getOptionalValue(NetherPortalBlock.AXIS).orElse(Direction.Axis.X);
        Optional<BlockUtil.FoundRectangle> optional2 = serverLevel.getPortalForcer().createPortal(blockPos, axis);
        if (!optional2.isPresent()) {
            LOGGER.error("Unable to create a portal, likely target out of worldborder");
        }
        return optional2;
    }

    private void triggerDimensionChangeTriggers(ServerLevel serverLevel) {
        ResourceKey<Level> resourceKey = serverLevel.dimension();
        ResourceKey<Level> resourceKey2 = this.level.dimension();
        CriteriaTriggers.CHANGED_DIMENSION.trigger(this, resourceKey, resourceKey2);
        if (resourceKey == Level.NETHER && resourceKey2 == Level.OVERWORLD && this.enteredNetherPosition != null) {
            CriteriaTriggers.NETHER_TRAVEL.trigger(this, this.enteredNetherPosition);
        }
        if (resourceKey2 != Level.NETHER) {
            this.enteredNetherPosition = null;
        }
    }

    @Override
    public boolean broadcastToPlayer(ServerPlayer serverPlayer) {
        if (serverPlayer.isSpectator()) {
            return this.getCamera() == this;
        }
        if (this.isSpectator()) {
            return false;
        }
        return super.broadcastToPlayer(serverPlayer);
    }

    private void broadcast(BlockEntity blockEntity) {
        ClientboundBlockEntityDataPacket clientboundBlockEntityDataPacket;
        if (blockEntity != null && (clientboundBlockEntityDataPacket = blockEntity.getUpdatePacket()) != null) {
            this.connection.send(clientboundBlockEntityDataPacket);
        }
    }

    @Override
    public void take(Entity entity, int n) {
        super.take(entity, n);
        this.containerMenu.broadcastChanges();
    }

    @Override
    public Either<Player.BedSleepingProblem, Unit> startSleepInBed(BlockPos blockPos) {
        Direction direction = this.level.getBlockState(blockPos).getValue(HorizontalDirectionalBlock.FACING);
        if (this.isSleeping() || !this.isAlive()) {
            return Either.left((Object)((Object)Player.BedSleepingProblem.OTHER_PROBLEM));
        }
        if (!this.level.dimensionType().natural()) {
            return Either.left((Object)((Object)Player.BedSleepingProblem.NOT_POSSIBLE_HERE));
        }
        if (!this.bedInRange(blockPos, direction)) {
            return Either.left((Object)((Object)Player.BedSleepingProblem.TOO_FAR_AWAY));
        }
        if (this.bedBlocked(blockPos, direction)) {
            return Either.left((Object)((Object)Player.BedSleepingProblem.OBSTRUCTED));
        }
        this.setRespawnPosition(this.level.dimension(), blockPos, this.yRot, false, true);
        if (this.level.isDay()) {
            return Either.left((Object)((Object)Player.BedSleepingProblem.NOT_POSSIBLE_NOW));
        }
        if (!this.isCreative()) {
            double d = 8.0;
            double d2 = 5.0;
            Vec3 vec3 = Vec3.atBottomCenterOf(blockPos);
            List<Monster> list = this.level.getEntitiesOfClass(Monster.class, new AABB(vec3.x() - 8.0, vec3.y() - 5.0, vec3.z() - 8.0, vec3.x() + 8.0, vec3.y() + 5.0, vec3.z() + 8.0), monster -> monster.isPreventingPlayerRest(this));
            if (!list.isEmpty()) {
                return Either.left((Object)((Object)Player.BedSleepingProblem.NOT_SAFE));
            }
        }
        Either either = super.startSleepInBed(blockPos).ifRight(unit -> {
            this.awardStat(Stats.SLEEP_IN_BED);
            CriteriaTriggers.SLEPT_IN_BED.trigger(this);
        });
        ((ServerLevel)this.level).updateSleepingPlayerList();
        return either;
    }

    @Override
    public void startSleeping(BlockPos blockPos) {
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        super.startSleeping(blockPos);
    }

    private boolean bedInRange(BlockPos blockPos, Direction direction) {
        return this.isReachableBedBlock(blockPos) || this.isReachableBedBlock(blockPos.relative(direction.getOpposite()));
    }

    private boolean isReachableBedBlock(BlockPos blockPos) {
        Vec3 vec3 = Vec3.atBottomCenterOf(blockPos);
        return Math.abs(this.getX() - vec3.x()) <= 3.0 && Math.abs(this.getY() - vec3.y()) <= 2.0 && Math.abs(this.getZ() - vec3.z()) <= 3.0;
    }

    private boolean bedBlocked(BlockPos blockPos, Direction direction) {
        BlockPos blockPos2 = blockPos.above();
        return !this.freeAt(blockPos2) || !this.freeAt(blockPos2.relative(direction.getOpposite()));
    }

    @Override
    public void stopSleepInBed(boolean bl, boolean bl2) {
        if (this.isSleeping()) {
            this.getLevel().getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(this, 2));
        }
        super.stopSleepInBed(bl, bl2);
        if (this.connection != null) {
            this.connection.teleport(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
        }
    }

    @Override
    public boolean startRiding(Entity entity, boolean bl) {
        Entity entity2 = this.getVehicle();
        if (!super.startRiding(entity, bl)) {
            return false;
        }
        Entity entity3 = this.getVehicle();
        if (entity3 != entity2 && this.connection != null) {
            this.connection.teleport(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
        }
        return true;
    }

    @Override
    public void stopRiding() {
        Entity entity = this.getVehicle();
        super.stopRiding();
        Entity entity2 = this.getVehicle();
        if (entity2 != entity && this.connection != null) {
            this.connection.teleport(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || this.isChangingDimension() || this.abilities.invulnerable && damageSource == DamageSource.WITHER;
    }

    @Override
    protected void checkFallDamage(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
    }

    @Override
    protected void onChangedBlock(BlockPos blockPos) {
        if (!this.isSpectator()) {
            super.onChangedBlock(blockPos);
        }
    }

    public void doCheckFallDamage(double d, boolean bl) {
        BlockPos blockPos = this.getOnPos();
        if (!this.level.hasChunkAt(blockPos)) {
            return;
        }
        super.checkFallDamage(d, bl, this.level.getBlockState(blockPos), blockPos);
    }

    @Override
    public void openTextEdit(SignBlockEntity signBlockEntity) {
        signBlockEntity.setAllowedPlayerEditor(this);
        this.connection.send(new ClientboundOpenSignEditorPacket(signBlockEntity.getBlockPos()));
    }

    private void nextContainerCounter() {
        this.containerCounter = this.containerCounter % 100 + 1;
    }

    @Override
    public OptionalInt openMenu(@Nullable MenuProvider menuProvider) {
        if (menuProvider == null) {
            return OptionalInt.empty();
        }
        if (this.containerMenu != this.inventoryMenu) {
            this.closeContainer();
        }
        this.nextContainerCounter();
        AbstractContainerMenu abstractContainerMenu = menuProvider.createMenu(this.containerCounter, this.inventory, this);
        if (abstractContainerMenu == null) {
            if (this.isSpectator()) {
                this.displayClientMessage(new TranslatableComponent("container.spectatorCantOpen").withStyle(ChatFormatting.RED), true);
            }
            return OptionalInt.empty();
        }
        this.connection.send(new ClientboundOpenScreenPacket(abstractContainerMenu.containerId, abstractContainerMenu.getType(), menuProvider.getDisplayName()));
        abstractContainerMenu.addSlotListener(this);
        this.containerMenu = abstractContainerMenu;
        return OptionalInt.of(this.containerCounter);
    }

    @Override
    public void sendMerchantOffers(int n, MerchantOffers merchantOffers, int n2, int n3, boolean bl, boolean bl2) {
        this.connection.send(new ClientboundMerchantOffersPacket(n, merchantOffers, n2, n3, bl, bl2));
    }

    @Override
    public void openHorseInventory(AbstractHorse abstractHorse, Container container) {
        if (this.containerMenu != this.inventoryMenu) {
            this.closeContainer();
        }
        this.nextContainerCounter();
        this.connection.send(new ClientboundHorseScreenOpenPacket(this.containerCounter, container.getContainerSize(), abstractHorse.getId()));
        this.containerMenu = new HorseInventoryMenu(this.containerCounter, this.inventory, container, abstractHorse);
        this.containerMenu.addSlotListener(this);
    }

    @Override
    public void openItemGui(ItemStack itemStack, InteractionHand interactionHand) {
        Item item = itemStack.getItem();
        if (item == Items.WRITTEN_BOOK) {
            if (WrittenBookItem.resolveBookComponents(itemStack, this.createCommandSourceStack(), this)) {
                this.containerMenu.broadcastChanges();
            }
            this.connection.send(new ClientboundOpenBookPacket(interactionHand));
        }
    }

    @Override
    public void openCommandBlock(CommandBlockEntity commandBlockEntity) {
        commandBlockEntity.setSendToClient(true);
        this.broadcast(commandBlockEntity);
    }

    @Override
    public void slotChanged(AbstractContainerMenu abstractContainerMenu, int n, ItemStack itemStack) {
        if (abstractContainerMenu.getSlot(n) instanceof ResultSlot) {
            return;
        }
        if (abstractContainerMenu == this.inventoryMenu) {
            CriteriaTriggers.INVENTORY_CHANGED.trigger(this, this.inventory, itemStack);
        }
        if (this.ignoreSlotUpdateHack) {
            return;
        }
        this.connection.send(new ClientboundContainerSetSlotPacket(abstractContainerMenu.containerId, n, itemStack));
    }

    public void refreshContainer(AbstractContainerMenu abstractContainerMenu) {
        this.refreshContainer(abstractContainerMenu, abstractContainerMenu.getItems());
    }

    @Override
    public void refreshContainer(AbstractContainerMenu abstractContainerMenu, NonNullList<ItemStack> nonNullList) {
        this.connection.send(new ClientboundContainerSetContentPacket(abstractContainerMenu.containerId, nonNullList));
        this.connection.send(new ClientboundContainerSetSlotPacket(-1, -1, this.inventory.getCarried()));
    }

    @Override
    public void setContainerData(AbstractContainerMenu abstractContainerMenu, int n, int n2) {
        this.connection.send(new ClientboundContainerSetDataPacket(abstractContainerMenu.containerId, n, n2));
    }

    @Override
    public void closeContainer() {
        this.connection.send(new ClientboundContainerClosePacket(this.containerMenu.containerId));
        this.doCloseContainer();
    }

    public void broadcastCarriedItem() {
        if (this.ignoreSlotUpdateHack) {
            return;
        }
        this.connection.send(new ClientboundContainerSetSlotPacket(-1, -1, this.inventory.getCarried()));
    }

    public void doCloseContainer() {
        this.containerMenu.removed(this);
        this.containerMenu = this.inventoryMenu;
    }

    public void setPlayerInput(float f, float f2, boolean bl, boolean bl2) {
        if (this.isPassenger()) {
            if (f >= -1.0f && f <= 1.0f) {
                this.xxa = f;
            }
            if (f2 >= -1.0f && f2 <= 1.0f) {
                this.zza = f2;
            }
            this.jumping = bl;
            this.setShiftKeyDown(bl2);
        }
    }

    @Override
    public void awardStat(Stat<?> stat, int n) {
        this.stats.increment(this, stat, n);
        this.getScoreboard().forAllObjectives(stat, this.getScoreboardName(), score -> score.add(n));
    }

    @Override
    public void resetStat(Stat<?> stat) {
        this.stats.setValue(this, stat, 0);
        this.getScoreboard().forAllObjectives(stat, this.getScoreboardName(), Score::reset);
    }

    @Override
    public int awardRecipes(Collection<Recipe<?>> collection) {
        return this.recipeBook.addRecipes(collection, this);
    }

    @Override
    public void awardRecipesByKey(ResourceLocation[] arrresourceLocation) {
        ArrayList arrayList = Lists.newArrayList();
        for (ResourceLocation resourceLocation : arrresourceLocation) {
            this.server.getRecipeManager().byKey(resourceLocation).ifPresent(arrayList::add);
        }
        this.awardRecipes(arrayList);
    }

    @Override
    public int resetRecipes(Collection<Recipe<?>> collection) {
        return this.recipeBook.removeRecipes(collection, this);
    }

    @Override
    public void giveExperiencePoints(int n) {
        super.giveExperiencePoints(n);
        this.lastSentExp = -1;
    }

    public void disconnect() {
        this.disconnected = true;
        this.ejectPassengers();
        if (this.isSleeping()) {
            this.stopSleepInBed(true, false);
        }
    }

    public boolean hasDisconnected() {
        return this.disconnected;
    }

    public void resetSentInfo() {
        this.lastSentHealth = -1.0E8f;
    }

    @Override
    public void displayClientMessage(Component component, boolean bl) {
        this.connection.send(new ClientboundChatPacket(component, bl ? ChatType.GAME_INFO : ChatType.CHAT, Util.NIL_UUID));
    }

    @Override
    protected void completeUsingItem() {
        if (!this.useItem.isEmpty() && this.isUsingItem()) {
            this.connection.send(new ClientboundEntityEventPacket(this, 9));
            super.completeUsingItem();
        }
    }

    @Override
    public void lookAt(EntityAnchorArgument.Anchor anchor, Vec3 vec3) {
        super.lookAt(anchor, vec3);
        this.connection.send(new ClientboundPlayerLookAtPacket(anchor, vec3.x, vec3.y, vec3.z));
    }

    public void lookAt(EntityAnchorArgument.Anchor anchor, Entity entity, EntityAnchorArgument.Anchor anchor2) {
        Vec3 vec3 = anchor2.apply(entity);
        super.lookAt(anchor, vec3);
        this.connection.send(new ClientboundPlayerLookAtPacket(anchor, entity, anchor2));
    }

    public void restoreFrom(ServerPlayer serverPlayer, boolean bl) {
        if (bl) {
            this.inventory.replaceWith(serverPlayer.inventory);
            this.setHealth(serverPlayer.getHealth());
            this.foodData = serverPlayer.foodData;
            this.experienceLevel = serverPlayer.experienceLevel;
            this.totalExperience = serverPlayer.totalExperience;
            this.experienceProgress = serverPlayer.experienceProgress;
            this.setScore(serverPlayer.getScore());
            this.portalEntrancePos = serverPlayer.portalEntrancePos;
        } else if (this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || serverPlayer.isSpectator()) {
            this.inventory.replaceWith(serverPlayer.inventory);
            this.experienceLevel = serverPlayer.experienceLevel;
            this.totalExperience = serverPlayer.totalExperience;
            this.experienceProgress = serverPlayer.experienceProgress;
            this.setScore(serverPlayer.getScore());
        }
        this.enchantmentSeed = serverPlayer.enchantmentSeed;
        this.enderChestInventory = serverPlayer.enderChestInventory;
        this.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, serverPlayer.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION));
        this.lastSentExp = -1;
        this.lastSentHealth = -1.0f;
        this.lastSentFood = -1;
        this.recipeBook.copyOverData(serverPlayer.recipeBook);
        this.entitiesToRemove.addAll(serverPlayer.entitiesToRemove);
        this.seenCredits = serverPlayer.seenCredits;
        this.enteredNetherPosition = serverPlayer.enteredNetherPosition;
        this.setShoulderEntityLeft(serverPlayer.getShoulderEntityLeft());
        this.setShoulderEntityRight(serverPlayer.getShoulderEntityRight());
    }

    @Override
    protected void onEffectAdded(MobEffectInstance mobEffectInstance) {
        super.onEffectAdded(mobEffectInstance);
        this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), mobEffectInstance));
        if (mobEffectInstance.getEffect() == MobEffects.LEVITATION) {
            this.levitationStartTime = this.tickCount;
            this.levitationStartPos = this.position();
        }
        CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
    }

    @Override
    protected void onEffectUpdated(MobEffectInstance mobEffectInstance, boolean bl) {
        super.onEffectUpdated(mobEffectInstance, bl);
        this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), mobEffectInstance));
        CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
    }

    @Override
    protected void onEffectRemoved(MobEffectInstance mobEffectInstance) {
        super.onEffectRemoved(mobEffectInstance);
        this.connection.send(new ClientboundRemoveMobEffectPacket(this.getId(), mobEffectInstance.getEffect()));
        if (mobEffectInstance.getEffect() == MobEffects.LEVITATION) {
            this.levitationStartPos = null;
        }
        CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
    }

    @Override
    public void teleportTo(double d, double d2, double d3) {
        this.connection.teleport(d, d2, d3, this.yRot, this.xRot);
    }

    @Override
    public void moveTo(double d, double d2, double d3) {
        this.teleportTo(d, d2, d3);
        this.connection.resetPosition();
    }

    @Override
    public void crit(Entity entity) {
        this.getLevel().getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(entity, 4));
    }

    @Override
    public void magicCrit(Entity entity) {
        this.getLevel().getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(entity, 5));
    }

    @Override
    public void onUpdateAbilities() {
        if (this.connection == null) {
            return;
        }
        this.connection.send(new ClientboundPlayerAbilitiesPacket(this.abilities));
        this.updateInvisibilityStatus();
    }

    public ServerLevel getLevel() {
        return (ServerLevel)this.level;
    }

    @Override
    public void setGameMode(GameType gameType) {
        this.gameMode.setGameModeForPlayer(gameType);
        this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE, gameType.getId()));
        if (gameType == GameType.SPECTATOR) {
            this.removeEntitiesOnShoulder();
            this.stopRiding();
        } else {
            this.setCamera(this);
        }
        this.onUpdateAbilities();
        this.updateEffectVisibility();
    }

    @Override
    public boolean isSpectator() {
        return this.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
    }

    @Override
    public boolean isCreative() {
        return this.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
    }

    @Override
    public void sendMessage(Component component, UUID uUID) {
        this.sendMessage(component, ChatType.SYSTEM, uUID);
    }

    public void sendMessage(Component component, ChatType chatType, UUID uUID) {
        this.connection.send(new ClientboundChatPacket(component, chatType, uUID), (GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener)future -> {
            if (!(future.isSuccess() || chatType != ChatType.GAME_INFO && chatType != ChatType.SYSTEM)) {
                int n = 256;
                String string = component.getString(256);
                MutableComponent mutableComponent = new TextComponent(string).withStyle(ChatFormatting.YELLOW);
                this.connection.send(new ClientboundChatPacket(new TranslatableComponent("multiplayer.message_not_delivered", mutableComponent).withStyle(ChatFormatting.RED), ChatType.SYSTEM, uUID));
            }
        }));
    }

    public String getIpAddress() {
        String string = this.connection.connection.getRemoteAddress().toString();
        string = string.substring(string.indexOf("/") + 1);
        string = string.substring(0, string.indexOf(":"));
        return string;
    }

    public void updateOptions(ServerboundClientInformationPacket serverboundClientInformationPacket) {
        this.chatVisibility = serverboundClientInformationPacket.getChatVisibility();
        this.canChatColor = serverboundClientInformationPacket.getChatColors();
        this.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, (byte)serverboundClientInformationPacket.getModelCustomisation());
        this.getEntityData().set(DATA_PLAYER_MAIN_HAND, (byte)(serverboundClientInformationPacket.getMainHand() != HumanoidArm.LEFT ? 1 : 0));
    }

    public ChatVisiblity getChatVisibility() {
        return this.chatVisibility;
    }

    public void sendTexturePack(String string, String string2) {
        this.connection.send(new ClientboundResourcePackPacket(string, string2));
    }

    @Override
    protected int getPermissionLevel() {
        return this.server.getProfilePermissions(this.getGameProfile());
    }

    public void resetLastActionTime() {
        this.lastActionTime = Util.getMillis();
    }

    public ServerStatsCounter getStats() {
        return this.stats;
    }

    public ServerRecipeBook getRecipeBook() {
        return this.recipeBook;
    }

    public void sendRemoveEntity(Entity entity) {
        if (entity instanceof Player) {
            this.connection.send(new ClientboundRemoveEntitiesPacket(entity.getId()));
        } else {
            this.entitiesToRemove.add(entity.getId());
        }
    }

    public void cancelRemoveEntity(Entity entity) {
        this.entitiesToRemove.remove((Object)entity.getId());
    }

    @Override
    protected void updateInvisibilityStatus() {
        if (this.isSpectator()) {
            this.removeEffectParticles();
            this.setInvisible(true);
        } else {
            super.updateInvisibilityStatus();
        }
    }

    public Entity getCamera() {
        return this.camera == null ? this : this.camera;
    }

    public void setCamera(Entity entity) {
        Entity entity2 = this.getCamera();
        Entity entity3 = this.camera = entity == null ? this : entity;
        if (entity2 != this.camera) {
            this.connection.send(new ClientboundSetCameraPacket(this.camera));
            this.teleportTo(this.camera.getX(), this.camera.getY(), this.camera.getZ());
        }
    }

    @Override
    protected void processPortalCooldown() {
        if (!this.isChangingDimension) {
            super.processPortalCooldown();
        }
    }

    @Override
    public void attack(Entity entity) {
        if (this.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
            this.setCamera(entity);
        } else {
            super.attack(entity);
        }
    }

    public long getLastActionTime() {
        return this.lastActionTime;
    }

    @Nullable
    public Component getTabListDisplayName() {
        return null;
    }

    @Override
    public void swing(InteractionHand interactionHand) {
        super.swing(interactionHand);
        this.resetAttackStrengthTicker();
    }

    public boolean isChangingDimension() {
        return this.isChangingDimension;
    }

    public void hasChangedDimension() {
        this.isChangingDimension = false;
    }

    public PlayerAdvancements getAdvancements() {
        return this.advancements;
    }

    public void teleportTo(ServerLevel serverLevel, double d, double d2, double d3, float f, float f2) {
        this.setCamera(this);
        this.stopRiding();
        if (serverLevel == this.level) {
            this.connection.teleport(d, d2, d3, f, f2);
        } else {
            ServerLevel serverLevel2 = this.getLevel();
            LevelData levelData = serverLevel.getLevelData();
            this.connection.send(new ClientboundRespawnPacket(serverLevel.dimensionType(), serverLevel.dimension(), BiomeManager.obfuscateSeed(serverLevel.getSeed()), this.gameMode.getGameModeForPlayer(), this.gameMode.getPreviousGameModeForPlayer(), serverLevel.isDebug(), serverLevel.isFlat(), true));
            this.connection.send(new ClientboundChangeDifficultyPacket(levelData.getDifficulty(), levelData.isDifficultyLocked()));
            this.server.getPlayerList().sendPlayerPermissionLevel(this);
            serverLevel2.removePlayerImmediately(this);
            this.removed = false;
            this.moveTo(d, d2, d3, f, f2);
            this.setLevel(serverLevel);
            serverLevel.addDuringCommandTeleport(this);
            this.triggerDimensionChangeTriggers(serverLevel2);
            this.connection.teleport(d, d2, d3, f, f2);
            this.gameMode.setLevel(serverLevel);
            this.server.getPlayerList().sendLevelInfo(this, serverLevel);
            this.server.getPlayerList().sendAllPlayerInfo(this);
        }
    }

    @Nullable
    public BlockPos getRespawnPosition() {
        return this.respawnPosition;
    }

    public float getRespawnAngle() {
        return this.respawnAngle;
    }

    public ResourceKey<Level> getRespawnDimension() {
        return this.respawnDimension;
    }

    public boolean isRespawnForced() {
        return this.respawnForced;
    }

    public void setRespawnPosition(ResourceKey<Level> resourceKey, @Nullable BlockPos blockPos, float f, boolean bl, boolean bl2) {
        if (blockPos != null) {
            boolean bl3;
            boolean bl4 = bl3 = blockPos.equals(this.respawnPosition) && resourceKey.equals(this.respawnDimension);
            if (bl2 && !bl3) {
                this.sendMessage(new TranslatableComponent("block.minecraft.set_spawn"), Util.NIL_UUID);
            }
            this.respawnPosition = blockPos;
            this.respawnDimension = resourceKey;
            this.respawnAngle = f;
            this.respawnForced = bl;
        } else {
            this.respawnPosition = null;
            this.respawnDimension = Level.OVERWORLD;
            this.respawnAngle = 0.0f;
            this.respawnForced = false;
        }
    }

    public void trackChunk(ChunkPos chunkPos, Packet<?> packet, Packet<?> packet2) {
        this.connection.send(packet2);
        this.connection.send(packet);
    }

    public void untrackChunk(ChunkPos chunkPos) {
        if (this.isAlive()) {
            this.connection.send(new ClientboundForgetLevelChunkPacket(chunkPos.x, chunkPos.z));
        }
    }

    public SectionPos getLastSectionPos() {
        return this.lastSectionPos;
    }

    public void setLastSectionPos(SectionPos sectionPos) {
        this.lastSectionPos = sectionPos;
    }

    @Override
    public void playNotifySound(SoundEvent soundEvent, SoundSource soundSource, float f, float f2) {
        this.connection.send(new ClientboundSoundPacket(soundEvent, soundSource, this.getX(), this.getY(), this.getZ(), f, f2));
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddPlayerPacket(this);
    }

    @Override
    public ItemEntity drop(ItemStack itemStack, boolean bl, boolean bl2) {
        ItemEntity itemEntity = super.drop(itemStack, bl, bl2);
        if (itemEntity == null) {
            return null;
        }
        this.level.addFreshEntity(itemEntity);
        ItemStack itemStack2 = itemEntity.getItem();
        if (bl2) {
            if (!itemStack2.isEmpty()) {
                this.awardStat(Stats.ITEM_DROPPED.get(itemStack2.getItem()), itemStack.getCount());
            }
            this.awardStat(Stats.DROP);
        }
        return itemEntity;
    }

    @Nullable
    public TextFilter getTextFilter() {
        return this.textFilter;
    }

    private /* synthetic */ void lambda$die$3(Component component, Future future) throws Exception {
        if (!future.isSuccess()) {
            int n = 256;
            String string = component.getString(256);
            TranslatableComponent translatableComponent = new TranslatableComponent("death.attack.message_too_long", new TextComponent(string).withStyle(ChatFormatting.YELLOW));
            MutableComponent mutableComponent = new TranslatableComponent("death.attack.even_more_magic", this.getDisplayName()).withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, translatableComponent)));
            this.connection.send(new ClientboundPlayerCombatPacket(this.getCombatTracker(), ClientboundPlayerCombatPacket.Event.ENTITY_DIED, mutableComponent));
        }
    }
}

