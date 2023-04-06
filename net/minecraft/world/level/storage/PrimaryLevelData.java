/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Decoder
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.OptionalDynamic
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.OptionalDynamic;
import java.util.AbstractList;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SerializableUUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.LevelVersion;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.timers.TimerCallbacks;
import net.minecraft.world.level.timers.TimerQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PrimaryLevelData
implements ServerLevelData,
WorldData {
    private static final Logger LOGGER = LogManager.getLogger();
    private LevelSettings settings;
    private final WorldGenSettings worldGenSettings;
    private final Lifecycle worldGenSettingsLifecycle;
    private int xSpawn;
    private int ySpawn;
    private int zSpawn;
    private float spawnAngle;
    private long gameTime;
    private long dayTime;
    @Nullable
    private final DataFixer fixerUpper;
    private final int playerDataVersion;
    private boolean upgradedPlayerTag;
    @Nullable
    private CompoundTag loadedPlayerTag;
    private final int version;
    private int clearWeatherTime;
    private boolean raining;
    private int rainTime;
    private boolean thundering;
    private int thunderTime;
    private boolean initialized;
    private boolean difficultyLocked;
    private WorldBorder.Settings worldBorder;
    private CompoundTag endDragonFightData;
    @Nullable
    private CompoundTag customBossEvents;
    private int wanderingTraderSpawnDelay;
    private int wanderingTraderSpawnChance;
    @Nullable
    private UUID wanderingTraderId;
    private final Set<String> knownServerBrands;
    private boolean wasModded;
    private final TimerQueue<MinecraftServer> scheduledEvents;

    private PrimaryLevelData(@Nullable DataFixer dataFixer, int n, @Nullable CompoundTag compoundTag, boolean bl, int n2, int n3, int n4, float f, long l, long l2, int n5, int n6, int n7, boolean bl2, int n8, boolean bl3, boolean bl4, boolean bl5, WorldBorder.Settings settings, int n9, int n10, @Nullable UUID uUID, LinkedHashSet<String> linkedHashSet, TimerQueue<MinecraftServer> timerQueue, @Nullable CompoundTag compoundTag2, CompoundTag compoundTag3, LevelSettings levelSettings, WorldGenSettings worldGenSettings, Lifecycle lifecycle) {
        this.fixerUpper = dataFixer;
        this.wasModded = bl;
        this.xSpawn = n2;
        this.ySpawn = n3;
        this.zSpawn = n4;
        this.spawnAngle = f;
        this.gameTime = l;
        this.dayTime = l2;
        this.version = n5;
        this.clearWeatherTime = n6;
        this.rainTime = n7;
        this.raining = bl2;
        this.thunderTime = n8;
        this.thundering = bl3;
        this.initialized = bl4;
        this.difficultyLocked = bl5;
        this.worldBorder = settings;
        this.wanderingTraderSpawnDelay = n9;
        this.wanderingTraderSpawnChance = n10;
        this.wanderingTraderId = uUID;
        this.knownServerBrands = linkedHashSet;
        this.loadedPlayerTag = compoundTag;
        this.playerDataVersion = n;
        this.scheduledEvents = timerQueue;
        this.customBossEvents = compoundTag2;
        this.endDragonFightData = compoundTag3;
        this.settings = levelSettings;
        this.worldGenSettings = worldGenSettings;
        this.worldGenSettingsLifecycle = lifecycle;
    }

    public PrimaryLevelData(LevelSettings levelSettings, WorldGenSettings worldGenSettings, Lifecycle lifecycle) {
        this(null, SharedConstants.getCurrentVersion().getWorldVersion(), null, false, 0, 0, 0, 0.0f, 0L, 0L, 19133, 0, 0, false, 0, false, false, false, WorldBorder.DEFAULT_SETTINGS, 0, 0, null, Sets.newLinkedHashSet(), new TimerQueue<MinecraftServer>(TimerCallbacks.SERVER_CALLBACKS), null, new CompoundTag(), levelSettings.copy(), worldGenSettings, lifecycle);
    }

    public static PrimaryLevelData parse(Dynamic<Tag> dynamic2, DataFixer dataFixer, int n, @Nullable CompoundTag compoundTag, LevelSettings levelSettings, LevelVersion levelVersion, WorldGenSettings worldGenSettings, Lifecycle lifecycle) {
        long l = dynamic2.get("Time").asLong(0L);
        CompoundTag compoundTag2 = (CompoundTag)dynamic2.get("DragonFight").result().map(Dynamic::getValue).orElseGet(() -> (Tag)dynamic2.get("DimensionData").get("1").get("DragonFight").orElseEmptyMap().getValue());
        return new PrimaryLevelData(dataFixer, n, compoundTag, dynamic2.get("WasModded").asBoolean(false), dynamic2.get("SpawnX").asInt(0), dynamic2.get("SpawnY").asInt(0), dynamic2.get("SpawnZ").asInt(0), dynamic2.get("SpawnAngle").asFloat(0.0f), l, dynamic2.get("DayTime").asLong(l), levelVersion.levelDataVersion(), dynamic2.get("clearWeatherTime").asInt(0), dynamic2.get("rainTime").asInt(0), dynamic2.get("raining").asBoolean(false), dynamic2.get("thunderTime").asInt(0), dynamic2.get("thundering").asBoolean(false), dynamic2.get("initialized").asBoolean(true), dynamic2.get("DifficultyLocked").asBoolean(false), WorldBorder.Settings.read(dynamic2, WorldBorder.DEFAULT_SETTINGS), dynamic2.get("WanderingTraderSpawnDelay").asInt(0), dynamic2.get("WanderingTraderSpawnChance").asInt(0), dynamic2.get("WanderingTraderId").read(SerializableUUID.CODEC).result().orElse(null), dynamic2.get("ServerBrands").asStream().flatMap(dynamic -> Util.toStream(dynamic.asString().result())).collect(Collectors.toCollection(Sets::newLinkedHashSet)), new TimerQueue<MinecraftServer>(TimerCallbacks.SERVER_CALLBACKS, dynamic2.get("ScheduledEvents").asStream()), (CompoundTag)dynamic2.get("CustomBossEvents").orElseEmptyMap().getValue(), compoundTag2, levelSettings, worldGenSettings, lifecycle);
    }

    @Override
    public CompoundTag createTag(RegistryAccess registryAccess, @Nullable CompoundTag compoundTag) {
        this.updatePlayerTag();
        if (compoundTag == null) {
            compoundTag = this.loadedPlayerTag;
        }
        CompoundTag compoundTag2 = new CompoundTag();
        this.setTagData(registryAccess, compoundTag2, compoundTag);
        return compoundTag2;
    }

    private void setTagData(RegistryAccess registryAccess, CompoundTag compoundTag, @Nullable CompoundTag compoundTag2) {
        ListTag listTag = new ListTag();
        this.knownServerBrands.stream().map(StringTag::valueOf).forEach(listTag::add);
        compoundTag.put("ServerBrands", listTag);
        compoundTag.putBoolean("WasModded", this.wasModded);
        CompoundTag compoundTag3 = new CompoundTag();
        compoundTag3.putString("Name", SharedConstants.getCurrentVersion().getName());
        compoundTag3.putInt("Id", SharedConstants.getCurrentVersion().getWorldVersion());
        compoundTag3.putBoolean("Snapshot", !SharedConstants.getCurrentVersion().isStable());
        compoundTag.put("Version", compoundTag3);
        compoundTag.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        RegistryWriteOps<Tag> registryWriteOps = RegistryWriteOps.create(NbtOps.INSTANCE, registryAccess);
        WorldGenSettings.CODEC.encodeStart(registryWriteOps, (Object)this.worldGenSettings).resultOrPartial(Util.prefix("WorldGenSettings: ", ((Logger)LOGGER)::error)).ifPresent(tag -> compoundTag.put("WorldGenSettings", (Tag)tag));
        compoundTag.putInt("GameType", this.settings.gameType().getId());
        compoundTag.putInt("SpawnX", this.xSpawn);
        compoundTag.putInt("SpawnY", this.ySpawn);
        compoundTag.putInt("SpawnZ", this.zSpawn);
        compoundTag.putFloat("SpawnAngle", this.spawnAngle);
        compoundTag.putLong("Time", this.gameTime);
        compoundTag.putLong("DayTime", this.dayTime);
        compoundTag.putLong("LastPlayed", Util.getEpochMillis());
        compoundTag.putString("LevelName", this.settings.levelName());
        compoundTag.putInt("version", 19133);
        compoundTag.putInt("clearWeatherTime", this.clearWeatherTime);
        compoundTag.putInt("rainTime", this.rainTime);
        compoundTag.putBoolean("raining", this.raining);
        compoundTag.putInt("thunderTime", this.thunderTime);
        compoundTag.putBoolean("thundering", this.thundering);
        compoundTag.putBoolean("hardcore", this.settings.hardcore());
        compoundTag.putBoolean("allowCommands", this.settings.allowCommands());
        compoundTag.putBoolean("initialized", this.initialized);
        this.worldBorder.write(compoundTag);
        compoundTag.putByte("Difficulty", (byte)this.settings.difficulty().getId());
        compoundTag.putBoolean("DifficultyLocked", this.difficultyLocked);
        compoundTag.put("GameRules", this.settings.gameRules().createTag());
        compoundTag.put("DragonFight", this.endDragonFightData);
        if (compoundTag2 != null) {
            compoundTag.put("Player", compoundTag2);
        }
        DataPackConfig.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.settings.getDataPackConfig()).result().ifPresent(tag -> compoundTag.put("DataPacks", (Tag)tag));
        if (this.customBossEvents != null) {
            compoundTag.put("CustomBossEvents", this.customBossEvents);
        }
        compoundTag.put("ScheduledEvents", this.scheduledEvents.store());
        compoundTag.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
        compoundTag.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
        if (this.wanderingTraderId != null) {
            compoundTag.putUUID("WanderingTraderId", this.wanderingTraderId);
        }
    }

    @Override
    public int getXSpawn() {
        return this.xSpawn;
    }

    @Override
    public int getYSpawn() {
        return this.ySpawn;
    }

    @Override
    public int getZSpawn() {
        return this.zSpawn;
    }

    @Override
    public float getSpawnAngle() {
        return this.spawnAngle;
    }

    @Override
    public long getGameTime() {
        return this.gameTime;
    }

    @Override
    public long getDayTime() {
        return this.dayTime;
    }

    private void updatePlayerTag() {
        if (this.upgradedPlayerTag || this.loadedPlayerTag == null) {
            return;
        }
        if (this.playerDataVersion < SharedConstants.getCurrentVersion().getWorldVersion()) {
            if (this.fixerUpper == null) {
                throw Util.pauseInIde(new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded."));
            }
            this.loadedPlayerTag = NbtUtils.update(this.fixerUpper, DataFixTypes.PLAYER, this.loadedPlayerTag, this.playerDataVersion);
        }
        this.upgradedPlayerTag = true;
    }

    @Override
    public CompoundTag getLoadedPlayerTag() {
        this.updatePlayerTag();
        return this.loadedPlayerTag;
    }

    @Override
    public void setXSpawn(int n) {
        this.xSpawn = n;
    }

    @Override
    public void setYSpawn(int n) {
        this.ySpawn = n;
    }

    @Override
    public void setZSpawn(int n) {
        this.zSpawn = n;
    }

    @Override
    public void setSpawnAngle(float f) {
        this.spawnAngle = f;
    }

    @Override
    public void setGameTime(long l) {
        this.gameTime = l;
    }

    @Override
    public void setDayTime(long l) {
        this.dayTime = l;
    }

    @Override
    public void setSpawn(BlockPos blockPos, float f) {
        this.xSpawn = blockPos.getX();
        this.ySpawn = blockPos.getY();
        this.zSpawn = blockPos.getZ();
        this.spawnAngle = f;
    }

    @Override
    public String getLevelName() {
        return this.settings.levelName();
    }

    @Override
    public int getVersion() {
        return this.version;
    }

    @Override
    public int getClearWeatherTime() {
        return this.clearWeatherTime;
    }

    @Override
    public void setClearWeatherTime(int n) {
        this.clearWeatherTime = n;
    }

    @Override
    public boolean isThundering() {
        return this.thundering;
    }

    @Override
    public void setThundering(boolean bl) {
        this.thundering = bl;
    }

    @Override
    public int getThunderTime() {
        return this.thunderTime;
    }

    @Override
    public void setThunderTime(int n) {
        this.thunderTime = n;
    }

    @Override
    public boolean isRaining() {
        return this.raining;
    }

    @Override
    public void setRaining(boolean bl) {
        this.raining = bl;
    }

    @Override
    public int getRainTime() {
        return this.rainTime;
    }

    @Override
    public void setRainTime(int n) {
        this.rainTime = n;
    }

    @Override
    public GameType getGameType() {
        return this.settings.gameType();
    }

    @Override
    public void setGameType(GameType gameType) {
        this.settings = this.settings.withGameType(gameType);
    }

    @Override
    public boolean isHardcore() {
        return this.settings.hardcore();
    }

    @Override
    public boolean getAllowCommands() {
        return this.settings.allowCommands();
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public void setInitialized(boolean bl) {
        this.initialized = bl;
    }

    @Override
    public GameRules getGameRules() {
        return this.settings.gameRules();
    }

    @Override
    public WorldBorder.Settings getWorldBorder() {
        return this.worldBorder;
    }

    @Override
    public void setWorldBorder(WorldBorder.Settings settings) {
        this.worldBorder = settings;
    }

    @Override
    public Difficulty getDifficulty() {
        return this.settings.difficulty();
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        this.settings = this.settings.withDifficulty(difficulty);
    }

    @Override
    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }

    @Override
    public void setDifficultyLocked(boolean bl) {
        this.difficultyLocked = bl;
    }

    @Override
    public TimerQueue<MinecraftServer> getScheduledEvents() {
        return this.scheduledEvents;
    }

    @Override
    public void fillCrashReportCategory(CrashReportCategory crashReportCategory) {
        ServerLevelData.super.fillCrashReportCategory(crashReportCategory);
        WorldData.super.fillCrashReportCategory(crashReportCategory);
    }

    @Override
    public WorldGenSettings worldGenSettings() {
        return this.worldGenSettings;
    }

    @Override
    public Lifecycle worldGenSettingsLifecycle() {
        return this.worldGenSettingsLifecycle;
    }

    @Override
    public CompoundTag endDragonFightData() {
        return this.endDragonFightData;
    }

    @Override
    public void setEndDragonFightData(CompoundTag compoundTag) {
        this.endDragonFightData = compoundTag;
    }

    @Override
    public DataPackConfig getDataPackConfig() {
        return this.settings.getDataPackConfig();
    }

    @Override
    public void setDataPackConfig(DataPackConfig dataPackConfig) {
        this.settings = this.settings.withDataPackConfig(dataPackConfig);
    }

    @Nullable
    @Override
    public CompoundTag getCustomBossEvents() {
        return this.customBossEvents;
    }

    @Override
    public void setCustomBossEvents(@Nullable CompoundTag compoundTag) {
        this.customBossEvents = compoundTag;
    }

    @Override
    public int getWanderingTraderSpawnDelay() {
        return this.wanderingTraderSpawnDelay;
    }

    @Override
    public void setWanderingTraderSpawnDelay(int n) {
        this.wanderingTraderSpawnDelay = n;
    }

    @Override
    public int getWanderingTraderSpawnChance() {
        return this.wanderingTraderSpawnChance;
    }

    @Override
    public void setWanderingTraderSpawnChance(int n) {
        this.wanderingTraderSpawnChance = n;
    }

    @Override
    public void setWanderingTraderId(UUID uUID) {
        this.wanderingTraderId = uUID;
    }

    @Override
    public void setModdedInfo(String string, boolean bl) {
        this.knownServerBrands.add(string);
        this.wasModded |= bl;
    }

    @Override
    public boolean wasModded() {
        return this.wasModded;
    }

    @Override
    public Set<String> getKnownServerBrands() {
        return ImmutableSet.copyOf(this.knownServerBrands);
    }

    @Override
    public ServerLevelData overworldData() {
        return this;
    }

    @Override
    public LevelSettings getLevelSettings() {
        return this.settings.copy();
    }
}

