/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Queues
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.DataFixer
 *  it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.longs.LongSets
 *  it.unimi.dsi.fastutil.longs.LongSets$EmptySet
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$FastEntrySet
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectCollection
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagContainer;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.Mth;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockEventData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.ForcedChunksSavedData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.ServerTickList;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickNextTickData;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.portal.PortalForcer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.maps.MapIndex;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.level.timers.TimerQueue;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.Scoreboard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerLevel
extends Level
implements WorldGenLevel {
    public static final BlockPos END_SPAWN_POINT = new BlockPos(100, 50, 0);
    private static final Logger LOGGER = LogManager.getLogger();
    private final Int2ObjectMap<Entity> entitiesById = new Int2ObjectLinkedOpenHashMap();
    private final Map<UUID, Entity> entitiesByUuid = Maps.newHashMap();
    private final Queue<Entity> toAddAfterTick = Queues.newArrayDeque();
    private final List<ServerPlayer> players = Lists.newArrayList();
    private final ServerChunkCache chunkSource;
    boolean tickingEntities;
    private final MinecraftServer server;
    private final ServerLevelData serverLevelData;
    public boolean noSave;
    private boolean allPlayersSleeping;
    private int emptyTime;
    private final PortalForcer portalForcer;
    private final ServerTickList<Block> blockTicks = new ServerTickList<Block>(this, block -> block == null || block.defaultBlockState().isAir(), Registry.BLOCK::getKey, this::tickBlock);
    private final ServerTickList<Fluid> liquidTicks = new ServerTickList<Fluid>(this, fluid -> fluid == null || fluid == Fluids.EMPTY, Registry.FLUID::getKey, this::tickLiquid);
    private final Set<PathNavigation> navigations = Sets.newHashSet();
    protected final Raids raids;
    private final ObjectLinkedOpenHashSet<BlockEventData> blockEvents = new ObjectLinkedOpenHashSet();
    private boolean handlingTick;
    private final List<CustomSpawner> customSpawners;
    @Nullable
    private final EndDragonFight dragonFight;
    private final StructureFeatureManager structureFeatureManager;
    private final boolean tickTime;

    public ServerLevel(MinecraftServer minecraftServer, Executor executor, LevelStorageSource.LevelStorageAccess levelStorageAccess, ServerLevelData serverLevelData, ResourceKey<Level> resourceKey, DimensionType dimensionType, ChunkProgressListener chunkProgressListener, ChunkGenerator chunkGenerator, boolean bl, long l, List<CustomSpawner> list, boolean bl2) {
        super(serverLevelData, resourceKey, dimensionType, minecraftServer::getProfiler, false, bl, l);
        this.tickTime = bl2;
        this.server = minecraftServer;
        this.customSpawners = list;
        this.serverLevelData = serverLevelData;
        this.chunkSource = new ServerChunkCache(this, levelStorageAccess, minecraftServer.getFixerUpper(), minecraftServer.getStructureManager(), executor, chunkGenerator, minecraftServer.getPlayerList().getViewDistance(), minecraftServer.forceSynchronousWrites(), chunkProgressListener, () -> minecraftServer.overworld().getDataStorage());
        this.portalForcer = new PortalForcer(this);
        this.updateSkyBrightness();
        this.prepareWeather();
        this.getWorldBorder().setAbsoluteMaxSize(minecraftServer.getAbsoluteMaxWorldSize());
        this.raids = this.getDataStorage().computeIfAbsent(() -> new Raids(this), Raids.getFileId(this.dimensionType()));
        if (!minecraftServer.isSingleplayer()) {
            serverLevelData.setGameType(minecraftServer.getDefaultGameType());
        }
        this.structureFeatureManager = new StructureFeatureManager(this, minecraftServer.getWorldData().worldGenSettings());
        this.dragonFight = this.dimensionType().createDragonFight() ? new EndDragonFight(this, minecraftServer.getWorldData().worldGenSettings().seed(), minecraftServer.getWorldData().endDragonFightData()) : null;
    }

    public void setWeatherParameters(int n, int n2, boolean bl, boolean bl2) {
        this.serverLevelData.setClearWeatherTime(n);
        this.serverLevelData.setRainTime(n2);
        this.serverLevelData.setThunderTime(n2);
        this.serverLevelData.setRaining(bl);
        this.serverLevelData.setThundering(bl2);
    }

    @Override
    public Biome getUncachedNoiseBiome(int n, int n2, int n3) {
        return this.getChunkSource().getGenerator().getBiomeSource().getNoiseBiome(n, n2, n3);
    }

    public StructureFeatureManager structureFeatureManager() {
        return this.structureFeatureManager;
    }

    public void tick(BooleanSupplier booleanSupplier) {
        int n;
        ProfilerFiller profilerFiller = this.getProfiler();
        this.handlingTick = true;
        profilerFiller.push("world border");
        this.getWorldBorder().tick();
        profilerFiller.popPush("weather");
        boolean bl = this.isRaining();
        if (this.dimensionType().hasSkyLight()) {
            if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
                n = this.serverLevelData.getClearWeatherTime();
                int n2 = this.serverLevelData.getThunderTime();
                int n3 = this.serverLevelData.getRainTime();
                boolean bl2 = this.levelData.isThundering();
                boolean bl3 = this.levelData.isRaining();
                if (n > 0) {
                    --n;
                    n2 = bl2 ? 0 : 1;
                    n3 = bl3 ? 0 : 1;
                    bl2 = false;
                    bl3 = false;
                } else {
                    if (n2 > 0) {
                        if (--n2 == 0) {
                            bl2 = !bl2;
                        }
                    } else {
                        n2 = bl2 ? this.random.nextInt(12000) + 3600 : this.random.nextInt(168000) + 12000;
                    }
                    if (n3 > 0) {
                        if (--n3 == 0) {
                            bl3 = !bl3;
                        }
                    } else {
                        n3 = bl3 ? this.random.nextInt(12000) + 12000 : this.random.nextInt(168000) + 12000;
                    }
                }
                this.serverLevelData.setThunderTime(n2);
                this.serverLevelData.setRainTime(n3);
                this.serverLevelData.setClearWeatherTime(n);
                this.serverLevelData.setThundering(bl2);
                this.serverLevelData.setRaining(bl3);
            }
            this.oThunderLevel = this.thunderLevel;
            this.thunderLevel = this.levelData.isThundering() ? (float)((double)this.thunderLevel + 0.01) : (float)((double)this.thunderLevel - 0.01);
            this.thunderLevel = Mth.clamp(this.thunderLevel, 0.0f, 1.0f);
            this.oRainLevel = this.rainLevel;
            this.rainLevel = this.levelData.isRaining() ? (float)((double)this.rainLevel + 0.01) : (float)((double)this.rainLevel - 0.01);
            this.rainLevel = Mth.clamp(this.rainLevel, 0.0f, 1.0f);
        }
        if (this.oRainLevel != this.rainLevel) {
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, this.rainLevel), this.dimension());
        }
        if (this.oThunderLevel != this.thunderLevel) {
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, this.thunderLevel), this.dimension());
        }
        if (bl != this.isRaining()) {
            if (bl) {
                this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.STOP_RAINING, 0.0f));
            } else {
                this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.START_RAINING, 0.0f));
            }
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, this.rainLevel));
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, this.thunderLevel));
        }
        if (this.allPlayersSleeping && this.players.stream().noneMatch(serverPlayer -> !serverPlayer.isSpectator() && !serverPlayer.isSleepingLongEnough())) {
            this.allPlayersSleeping = false;
            if (this.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
                long l = this.levelData.getDayTime() + 24000L;
                this.setDayTime(l - l % 24000L);
            }
            this.wakeUpAllPlayers();
            if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
                this.stopWeather();
            }
        }
        this.updateSkyBrightness();
        this.tickTime();
        profilerFiller.popPush("chunkSource");
        this.getChunkSource().tick(booleanSupplier);
        profilerFiller.popPush("tickPending");
        if (!this.isDebug()) {
            this.blockTicks.tick();
            this.liquidTicks.tick();
        }
        profilerFiller.popPush("raid");
        this.raids.tick();
        profilerFiller.popPush("blockEvents");
        this.runBlockEvents();
        this.handlingTick = false;
        profilerFiller.popPush("entities");
        int n4 = n = !this.players.isEmpty() || !this.getForcedChunks().isEmpty() ? 1 : 0;
        if (n != 0) {
            this.resetEmptyTime();
        }
        if (n != 0 || this.emptyTime++ < 300) {
            Entity entity;
            if (this.dragonFight != null) {
                this.dragonFight.tick();
            }
            this.tickingEntities = true;
            ObjectIterator objectIterator = this.entitiesById.int2ObjectEntrySet().iterator();
            while (objectIterator.hasNext()) {
                Int2ObjectMap.Entry entry = (Int2ObjectMap.Entry)objectIterator.next();
                Entity entity2 = (Entity)entry.getValue();
                Entity entity3 = entity2.getVehicle();
                if (!this.server.isSpawningAnimals() && (entity2 instanceof Animal || entity2 instanceof WaterAnimal)) {
                    entity2.remove();
                }
                if (!this.server.areNpcsEnabled() && entity2 instanceof Npc) {
                    entity2.remove();
                }
                profilerFiller.push("checkDespawn");
                if (!entity2.removed) {
                    entity2.checkDespawn();
                }
                profilerFiller.pop();
                if (entity3 != null) {
                    if (!entity3.removed && entity3.hasPassenger(entity2)) continue;
                    entity2.stopRiding();
                }
                profilerFiller.push("tick");
                if (!entity2.removed && !(entity2 instanceof EnderDragonPart)) {
                    this.guardEntityTick(this::tickNonPassenger, entity2);
                }
                profilerFiller.pop();
                profilerFiller.push("remove");
                if (entity2.removed) {
                    this.removeFromChunk(entity2);
                    objectIterator.remove();
                    this.onEntityRemoved(entity2);
                }
                profilerFiller.pop();
            }
            this.tickingEntities = false;
            while ((entity = this.toAddAfterTick.poll()) != null) {
                this.add(entity);
            }
            this.tickBlockEntities();
        }
        profilerFiller.pop();
    }

    protected void tickTime() {
        if (!this.tickTime) {
            return;
        }
        long l = this.levelData.getGameTime() + 1L;
        this.serverLevelData.setGameTime(l);
        this.serverLevelData.getScheduledEvents().tick(this.server, l);
        if (this.levelData.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            this.setDayTime(this.levelData.getDayTime() + 1L);
        }
    }

    public void setDayTime(long l) {
        this.serverLevelData.setDayTime(l);
    }

    public void tickCustomSpawners(boolean bl, boolean bl2) {
        for (CustomSpawner customSpawner : this.customSpawners) {
            customSpawner.tick(this, bl, bl2);
        }
    }

    private void wakeUpAllPlayers() {
        this.players.stream().filter(LivingEntity::isSleeping).collect(Collectors.toList()).forEach(serverPlayer -> serverPlayer.stopSleepInBed(false, false));
    }

    public void tickChunk(LevelChunk levelChunk, int n) {
        Object object2;
        Object object;
        ChunkPos chunkPos = levelChunk.getPos();
        boolean bl = this.isRaining();
        int n2 = chunkPos.getMinBlockX();
        int n3 = chunkPos.getMinBlockZ();
        ProfilerFiller profilerFiller = this.getProfiler();
        profilerFiller.push("thunder");
        if (bl && this.isThundering() && this.random.nextInt(100000) == 0 && this.isRainingAt((BlockPos)(object2 = this.findLightingTargetAround(this.getBlockRandomPos(n2, 0, n3, 15))))) {
            int n4;
            object = this.getCurrentDifficultyAt((BlockPos)object2);
            int n5 = n4 = this.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && this.random.nextDouble() < (double)((DifficultyInstance)object).getEffectiveDifficulty() * 0.01 ? 1 : 0;
            if (n4) {
                SkeletonHorse object3 = EntityType.SKELETON_HORSE.create(this);
                object3.setTrap(true);
                object3.setAge(0);
                object3.setPos(((Vec3i)object2).getX(), ((Vec3i)object2).getY(), ((Vec3i)object2).getZ());
                this.addFreshEntity(object3);
            }
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(this);
            lightningBolt.moveTo(Vec3.atBottomCenterOf((Vec3i)object2));
            lightningBolt.setVisualOnly(n4 != 0);
            this.addFreshEntity(lightningBolt);
        }
        profilerFiller.popPush("iceandsnow");
        if (this.random.nextInt(16) == 0) {
            object2 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, this.getBlockRandomPos(n2, 0, n3, 15));
            object = ((BlockPos)object2).below();
            Biome biome = this.getBiome((BlockPos)object2);
            if (biome.shouldFreeze(this, (BlockPos)object)) {
                this.setBlockAndUpdate((BlockPos)object, Blocks.ICE.defaultBlockState());
            }
            if (bl && biome.shouldSnow(this, (BlockPos)object2)) {
                this.setBlockAndUpdate((BlockPos)object2, Blocks.SNOW.defaultBlockState());
            }
            if (bl && this.getBiome((BlockPos)object).getPrecipitation() == Biome.Precipitation.RAIN) {
                this.getBlockState((BlockPos)object).getBlock().handleRain(this, (BlockPos)object);
            }
        }
        profilerFiller.popPush("tickBlocks");
        if (n > 0) {
            for (LevelChunkSection levelChunkSection : levelChunk.getSections()) {
                if (levelChunkSection == LevelChunk.EMPTY_SECTION || !levelChunkSection.isRandomlyTicking()) continue;
                int n6 = levelChunkSection.bottomBlockY();
                for (int i = 0; i < n; ++i) {
                    FluidState fluidState;
                    BlockPos blockPos = this.getBlockRandomPos(n2, n6, n3, 15);
                    profilerFiller.push("randomTick");
                    BlockState blockState = levelChunkSection.getBlockState(blockPos.getX() - n2, blockPos.getY() - n6, blockPos.getZ() - n3);
                    if (blockState.isRandomlyTicking()) {
                        blockState.randomTick(this, blockPos, this.random);
                    }
                    if ((fluidState = blockState.getFluidState()).isRandomlyTicking()) {
                        fluidState.randomTick(this, blockPos, this.random);
                    }
                    profilerFiller.pop();
                }
            }
        }
        profilerFiller.pop();
    }

    protected BlockPos findLightingTargetAround(BlockPos blockPos) {
        BlockPos blockPos2 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos);
        AABB aABB = new AABB(blockPos2, new BlockPos(blockPos2.getX(), this.getMaxBuildHeight(), blockPos2.getZ())).inflate(3.0);
        List<LivingEntity> list = this.getEntitiesOfClass(LivingEntity.class, aABB, livingEntity -> livingEntity != null && livingEntity.isAlive() && this.canSeeSky(livingEntity.blockPosition()));
        if (!list.isEmpty()) {
            return list.get(this.random.nextInt(list.size())).blockPosition();
        }
        if (blockPos2.getY() == -1) {
            blockPos2 = blockPos2.above(2);
        }
        return blockPos2;
    }

    public boolean isHandlingTick() {
        return this.handlingTick;
    }

    public void updateSleepingPlayerList() {
        this.allPlayersSleeping = false;
        if (!this.players.isEmpty()) {
            int n = 0;
            int n2 = 0;
            for (ServerPlayer serverPlayer : this.players) {
                if (serverPlayer.isSpectator()) {
                    ++n;
                    continue;
                }
                if (!serverPlayer.isSleeping()) continue;
                ++n2;
            }
            this.allPlayersSleeping = n2 > 0 && n2 >= this.players.size() - n;
        }
    }

    @Override
    public ServerScoreboard getScoreboard() {
        return this.server.getScoreboard();
    }

    private void stopWeather() {
        this.serverLevelData.setRainTime(0);
        this.serverLevelData.setRaining(false);
        this.serverLevelData.setThunderTime(0);
        this.serverLevelData.setThundering(false);
    }

    public void resetEmptyTime() {
        this.emptyTime = 0;
    }

    private void tickLiquid(TickNextTickData<Fluid> tickNextTickData) {
        FluidState fluidState = this.getFluidState(tickNextTickData.pos);
        if (fluidState.getType() == tickNextTickData.getType()) {
            fluidState.tick(this, tickNextTickData.pos);
        }
    }

    private void tickBlock(TickNextTickData<Block> tickNextTickData) {
        BlockState blockState = this.getBlockState(tickNextTickData.pos);
        if (blockState.is(tickNextTickData.getType())) {
            blockState.tick(this, tickNextTickData.pos, this.random);
        }
    }

    public void tickNonPassenger(Entity entity) {
        if (!(entity instanceof Player) && !this.getChunkSource().isEntityTickingChunk(entity)) {
            this.updateChunkPos(entity);
            return;
        }
        entity.setPosAndOldPos(entity.getX(), entity.getY(), entity.getZ());
        entity.yRotO = entity.yRot;
        entity.xRotO = entity.xRot;
        if (entity.inChunk) {
            ++entity.tickCount;
            ProfilerFiller profilerFiller = this.getProfiler();
            profilerFiller.push(() -> Registry.ENTITY_TYPE.getKey(entity.getType()).toString());
            profilerFiller.incrementCounter("tickNonPassenger");
            entity.tick();
            profilerFiller.pop();
        }
        this.updateChunkPos(entity);
        if (entity.inChunk) {
            for (Entity entity2 : entity.getPassengers()) {
                this.tickPassenger(entity, entity2);
            }
        }
    }

    public void tickPassenger(Entity entity, Entity entity2) {
        if (entity2.removed || entity2.getVehicle() != entity) {
            entity2.stopRiding();
            return;
        }
        if (!(entity2 instanceof Player) && !this.getChunkSource().isEntityTickingChunk(entity2)) {
            return;
        }
        entity2.setPosAndOldPos(entity2.getX(), entity2.getY(), entity2.getZ());
        entity2.yRotO = entity2.yRot;
        entity2.xRotO = entity2.xRot;
        if (entity2.inChunk) {
            ++entity2.tickCount;
            ProfilerFiller profilerFiller = this.getProfiler();
            profilerFiller.push(() -> Registry.ENTITY_TYPE.getKey(entity2.getType()).toString());
            profilerFiller.incrementCounter("tickPassenger");
            entity2.rideTick();
            profilerFiller.pop();
        }
        this.updateChunkPos(entity2);
        if (entity2.inChunk) {
            for (Entity entity3 : entity2.getPassengers()) {
                this.tickPassenger(entity2, entity3);
            }
        }
    }

    public void updateChunkPos(Entity entity) {
        if (!entity.checkAndResetUpdateChunkPos()) {
            return;
        }
        this.getProfiler().push("chunkCheck");
        int n = Mth.floor(entity.getX() / 16.0);
        int n2 = Mth.floor(entity.getY() / 16.0);
        int n3 = Mth.floor(entity.getZ() / 16.0);
        if (!entity.inChunk || entity.xChunk != n || entity.yChunk != n2 || entity.zChunk != n3) {
            if (entity.inChunk && this.hasChunk(entity.xChunk, entity.zChunk)) {
                this.getChunk(entity.xChunk, entity.zChunk).removeEntity(entity, entity.yChunk);
            }
            if (entity.checkAndResetForcedChunkAdditionFlag() || this.hasChunk(n, n3)) {
                this.getChunk(n, n3).addEntity(entity);
            } else {
                if (entity.inChunk) {
                    LOGGER.warn("Entity {} left loaded chunk area", (Object)entity);
                }
                entity.inChunk = false;
            }
        }
        this.getProfiler().pop();
    }

    @Override
    public boolean mayInteract(Player player, BlockPos blockPos) {
        return !this.server.isUnderSpawnProtection(this, blockPos, player) && this.getWorldBorder().isWithinBounds(blockPos);
    }

    public void save(@Nullable ProgressListener progressListener, boolean bl, boolean bl2) {
        ServerChunkCache serverChunkCache = this.getChunkSource();
        if (bl2) {
            return;
        }
        if (progressListener != null) {
            progressListener.progressStartNoAbort(new TranslatableComponent("menu.savingLevel"));
        }
        this.saveLevelData();
        if (progressListener != null) {
            progressListener.progressStage(new TranslatableComponent("menu.savingChunks"));
        }
        serverChunkCache.save(bl);
    }

    private void saveLevelData() {
        if (this.dragonFight != null) {
            this.server.getWorldData().setEndDragonFightData(this.dragonFight.saveData());
        }
        this.getChunkSource().getDataStorage().save();
    }

    public List<Entity> getEntities(@Nullable EntityType<?> entityType, Predicate<? super Entity> predicate) {
        ArrayList arrayList = Lists.newArrayList();
        ServerChunkCache serverChunkCache = this.getChunkSource();
        for (Entity entity : this.entitiesById.values()) {
            if (entityType != null && entity.getType() != entityType || !serverChunkCache.hasChunk(Mth.floor(entity.getX()) >> 4, Mth.floor(entity.getZ()) >> 4) || !predicate.test(entity)) continue;
            arrayList.add(entity);
        }
        return arrayList;
    }

    public List<EnderDragon> getDragons() {
        ArrayList arrayList = Lists.newArrayList();
        for (Entity entity : this.entitiesById.values()) {
            if (!(entity instanceof EnderDragon) || !entity.isAlive()) continue;
            arrayList.add((EnderDragon)entity);
        }
        return arrayList;
    }

    public List<ServerPlayer> getPlayers(Predicate<? super ServerPlayer> predicate) {
        ArrayList arrayList = Lists.newArrayList();
        for (ServerPlayer serverPlayer : this.players) {
            if (!predicate.test(serverPlayer)) continue;
            arrayList.add(serverPlayer);
        }
        return arrayList;
    }

    @Nullable
    public ServerPlayer getRandomPlayer() {
        List<ServerPlayer> list = this.getPlayers(LivingEntity::isAlive);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(this.random.nextInt(list.size()));
    }

    @Override
    public boolean addFreshEntity(Entity entity) {
        return this.addEntity(entity);
    }

    public boolean addWithUUID(Entity entity) {
        return this.addEntity(entity);
    }

    public void addFromAnotherDimension(Entity entity) {
        boolean bl = entity.forcedLoading;
        entity.forcedLoading = true;
        this.addWithUUID(entity);
        entity.forcedLoading = bl;
        this.updateChunkPos(entity);
    }

    public void addDuringCommandTeleport(ServerPlayer serverPlayer) {
        this.addPlayer(serverPlayer);
        this.updateChunkPos(serverPlayer);
    }

    public void addDuringPortalTeleport(ServerPlayer serverPlayer) {
        this.addPlayer(serverPlayer);
        this.updateChunkPos(serverPlayer);
    }

    public void addNewPlayer(ServerPlayer serverPlayer) {
        this.addPlayer(serverPlayer);
    }

    public void addRespawnedPlayer(ServerPlayer serverPlayer) {
        this.addPlayer(serverPlayer);
    }

    private void addPlayer(ServerPlayer serverPlayer) {
        Entity entity = this.entitiesByUuid.get(serverPlayer.getUUID());
        if (entity != null) {
            LOGGER.warn("Force-added player with duplicate UUID {}", (Object)serverPlayer.getUUID().toString());
            entity.unRide();
            this.removePlayerImmediately((ServerPlayer)entity);
        }
        this.players.add(serverPlayer);
        this.updateSleepingPlayerList();
        ChunkAccess chunkAccess = this.getChunk(Mth.floor(serverPlayer.getX() / 16.0), Mth.floor(serverPlayer.getZ() / 16.0), ChunkStatus.FULL, true);
        if (chunkAccess instanceof LevelChunk) {
            chunkAccess.addEntity(serverPlayer);
        }
        this.add(serverPlayer);
    }

    private boolean addEntity(Entity entity) {
        if (entity.removed) {
            LOGGER.warn("Tried to add entity {} but it was marked as removed already", (Object)EntityType.getKey(entity.getType()));
            return false;
        }
        if (this.isUUIDUsed(entity)) {
            return false;
        }
        ChunkAccess chunkAccess = this.getChunk(Mth.floor(entity.getX() / 16.0), Mth.floor(entity.getZ() / 16.0), ChunkStatus.FULL, entity.forcedLoading);
        if (!(chunkAccess instanceof LevelChunk)) {
            return false;
        }
        chunkAccess.addEntity(entity);
        this.add(entity);
        return true;
    }

    public boolean loadFromChunk(Entity entity) {
        if (this.isUUIDUsed(entity)) {
            return false;
        }
        this.add(entity);
        return true;
    }

    private boolean isUUIDUsed(Entity entity) {
        UUID uUID = entity.getUUID();
        Entity entity2 = this.findAddedOrPendingEntity(uUID);
        if (entity2 == null) {
            return false;
        }
        LOGGER.warn("Trying to add entity with duplicated UUID {}. Existing {}#{}, new: {}#{}", (Object)uUID, (Object)EntityType.getKey(entity2.getType()), (Object)entity2.getId(), (Object)EntityType.getKey(entity.getType()), (Object)entity.getId());
        return true;
    }

    @Nullable
    private Entity findAddedOrPendingEntity(UUID uUID) {
        Entity entity = this.entitiesByUuid.get(uUID);
        if (entity != null) {
            return entity;
        }
        if (this.tickingEntities) {
            for (Entity entity2 : this.toAddAfterTick) {
                if (!entity2.getUUID().equals(uUID)) continue;
                return entity2;
            }
        }
        return null;
    }

    public boolean tryAddFreshEntityWithPassengers(Entity entity) {
        if (entity.getSelfAndPassengers().anyMatch(this::isUUIDUsed)) {
            return false;
        }
        this.addFreshEntityWithPassengers(entity);
        return true;
    }

    public void unload(LevelChunk levelChunk) {
        this.blockEntitiesToUnload.addAll(levelChunk.getBlockEntities().values());
        for (ClassInstanceMultiMap<Entity> classInstanceMultiMap : levelChunk.getEntitySections()) {
            for (Entity entity : classInstanceMultiMap) {
                if (entity instanceof ServerPlayer) continue;
                if (this.tickingEntities) {
                    throw Util.pauseInIde(new IllegalStateException("Removing entity while ticking!"));
                }
                this.entitiesById.remove(entity.getId());
                this.onEntityRemoved(entity);
            }
        }
    }

    public void onEntityRemoved(Entity entity) {
        if (entity instanceof EnderDragon) {
            for (EnderDragonPart enderDragonPart : ((EnderDragon)entity).getSubEntities()) {
                enderDragonPart.remove();
            }
        }
        this.entitiesByUuid.remove(entity.getUUID());
        this.getChunkSource().removeEntity(entity);
        if (entity instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)entity;
            this.players.remove(serverPlayer);
        }
        this.getScoreboard().entityRemoved(entity);
        if (entity instanceof Mob) {
            this.navigations.remove(((Mob)entity).getNavigation());
        }
    }

    private void add(Entity entity) {
        if (this.tickingEntities) {
            this.toAddAfterTick.add(entity);
        } else {
            this.entitiesById.put(entity.getId(), (Object)entity);
            if (entity instanceof EnderDragon) {
                for (EnderDragonPart enderDragonPart : ((EnderDragon)entity).getSubEntities()) {
                    this.entitiesById.put(enderDragonPart.getId(), (Object)enderDragonPart);
                }
            }
            this.entitiesByUuid.put(entity.getUUID(), entity);
            this.getChunkSource().addEntity(entity);
            if (entity instanceof Mob) {
                this.navigations.add(((Mob)entity).getNavigation());
            }
        }
    }

    public void despawn(Entity entity) {
        if (this.tickingEntities) {
            throw Util.pauseInIde(new IllegalStateException("Removing entity while ticking!"));
        }
        this.removeFromChunk(entity);
        this.entitiesById.remove(entity.getId());
        this.onEntityRemoved(entity);
    }

    private void removeFromChunk(Entity entity) {
        ChunkAccess chunkAccess = this.getChunk(entity.xChunk, entity.zChunk, ChunkStatus.FULL, false);
        if (chunkAccess instanceof LevelChunk) {
            ((LevelChunk)chunkAccess).removeEntity(entity);
        }
    }

    public void removePlayerImmediately(ServerPlayer serverPlayer) {
        serverPlayer.remove();
        this.despawn(serverPlayer);
        this.updateSleepingPlayerList();
    }

    @Override
    public void destroyBlockProgress(int n, BlockPos blockPos, int n2) {
        for (ServerPlayer serverPlayer : this.server.getPlayerList().getPlayers()) {
            double d;
            double d2;
            double d3;
            if (serverPlayer == null || serverPlayer.level != this || serverPlayer.getId() == n || !((d2 = (double)blockPos.getX() - serverPlayer.getX()) * d2 + (d = (double)blockPos.getY() - serverPlayer.getY()) * d + (d3 = (double)blockPos.getZ() - serverPlayer.getZ()) * d3 < 1024.0)) continue;
            serverPlayer.connection.send(new ClientboundBlockDestructionPacket(n, blockPos, n2));
        }
    }

    @Override
    public void playSound(@Nullable Player player, double d, double d2, double d3, SoundEvent soundEvent, SoundSource soundSource, float f, float f2) {
        this.server.getPlayerList().broadcast(player, d, d2, d3, f > 1.0f ? (double)(16.0f * f) : 16.0, this.dimension(), new ClientboundSoundPacket(soundEvent, soundSource, d, d2, d3, f, f2));
    }

    @Override
    public void playSound(@Nullable Player player, Entity entity, SoundEvent soundEvent, SoundSource soundSource, float f, float f2) {
        this.server.getPlayerList().broadcast(player, entity.getX(), entity.getY(), entity.getZ(), f > 1.0f ? (double)(16.0f * f) : 16.0, this.dimension(), new ClientboundSoundEntityPacket(soundEvent, soundSource, entity, f, f2));
    }

    @Override
    public void globalLevelEvent(int n, BlockPos blockPos, int n2) {
        this.server.getPlayerList().broadcastAll(new ClientboundLevelEventPacket(n, blockPos, n2, true));
    }

    @Override
    public void levelEvent(@Nullable Player player, int n, BlockPos blockPos, int n2) {
        this.server.getPlayerList().broadcast(player, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 64.0, this.dimension(), new ClientboundLevelEventPacket(n, blockPos, n2, false));
    }

    @Override
    public void sendBlockUpdated(BlockPos blockPos, BlockState blockState, BlockState blockState2, int n) {
        this.getChunkSource().blockChanged(blockPos);
        VoxelShape voxelShape = blockState.getCollisionShape(this, blockPos);
        VoxelShape voxelShape2 = blockState2.getCollisionShape(this, blockPos);
        if (!Shapes.joinIsNotEmpty(voxelShape, voxelShape2, BooleanOp.NOT_SAME)) {
            return;
        }
        for (PathNavigation pathNavigation : this.navigations) {
            if (pathNavigation.hasDelayedRecomputation()) continue;
            pathNavigation.recomputePath(blockPos);
        }
    }

    @Override
    public void broadcastEntityEvent(Entity entity, byte by) {
        this.getChunkSource().broadcastAndSend(entity, new ClientboundEntityEventPacket(entity, by));
    }

    @Override
    public ServerChunkCache getChunkSource() {
        return this.chunkSource;
    }

    @Override
    public Explosion explode(@Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator explosionDamageCalculator, double d, double d2, double d3, float f, boolean bl, Explosion.BlockInteraction blockInteraction) {
        Explosion explosion = new Explosion(this, entity, damageSource, explosionDamageCalculator, d, d2, d3, f, bl, blockInteraction);
        explosion.explode();
        explosion.finalizeExplosion(false);
        if (blockInteraction == Explosion.BlockInteraction.NONE) {
            explosion.clearToBlow();
        }
        for (ServerPlayer serverPlayer : this.players) {
            if (!(serverPlayer.distanceToSqr(d, d2, d3) < 4096.0)) continue;
            serverPlayer.connection.send(new ClientboundExplodePacket(d, d2, d3, f, explosion.getToBlow(), explosion.getHitPlayers().get(serverPlayer)));
        }
        return explosion;
    }

    @Override
    public void blockEvent(BlockPos blockPos, Block block, int n, int n2) {
        this.blockEvents.add((Object)new BlockEventData(blockPos, block, n, n2));
    }

    private void runBlockEvents() {
        while (!this.blockEvents.isEmpty()) {
            BlockEventData blockEventData = (BlockEventData)this.blockEvents.removeFirst();
            if (!this.doBlockEvent(blockEventData)) continue;
            this.server.getPlayerList().broadcast(null, blockEventData.getPos().getX(), blockEventData.getPos().getY(), blockEventData.getPos().getZ(), 64.0, this.dimension(), new ClientboundBlockEventPacket(blockEventData.getPos(), blockEventData.getBlock(), blockEventData.getParamA(), blockEventData.getParamB()));
        }
    }

    private boolean doBlockEvent(BlockEventData blockEventData) {
        BlockState blockState = this.getBlockState(blockEventData.getPos());
        if (blockState.is(blockEventData.getBlock())) {
            return blockState.triggerEvent(this, blockEventData.getPos(), blockEventData.getParamA(), blockEventData.getParamB());
        }
        return false;
    }

    public ServerTickList<Block> getBlockTicks() {
        return this.blockTicks;
    }

    public ServerTickList<Fluid> getLiquidTicks() {
        return this.liquidTicks;
    }

    @Nonnull
    @Override
    public MinecraftServer getServer() {
        return this.server;
    }

    public PortalForcer getPortalForcer() {
        return this.portalForcer;
    }

    public StructureManager getStructureManager() {
        return this.server.getStructureManager();
    }

    public <T extends ParticleOptions> int sendParticles(T t, double d, double d2, double d3, int n, double d4, double d5, double d6, double d7) {
        ClientboundLevelParticlesPacket clientboundLevelParticlesPacket = new ClientboundLevelParticlesPacket(t, false, d, d2, d3, (float)d4, (float)d5, (float)d6, (float)d7, n);
        int n2 = 0;
        for (int i = 0; i < this.players.size(); ++i) {
            ServerPlayer serverPlayer = this.players.get(i);
            if (!this.sendParticles(serverPlayer, false, d, d2, d3, clientboundLevelParticlesPacket)) continue;
            ++n2;
        }
        return n2;
    }

    public <T extends ParticleOptions> boolean sendParticles(ServerPlayer serverPlayer, T t, boolean bl, double d, double d2, double d3, int n, double d4, double d5, double d6, double d7) {
        ClientboundLevelParticlesPacket clientboundLevelParticlesPacket = new ClientboundLevelParticlesPacket(t, bl, d, d2, d3, (float)d4, (float)d5, (float)d6, (float)d7, n);
        return this.sendParticles(serverPlayer, bl, d, d2, d3, clientboundLevelParticlesPacket);
    }

    private boolean sendParticles(ServerPlayer serverPlayer, boolean bl, double d, double d2, double d3, Packet<?> packet) {
        if (serverPlayer.getLevel() != this) {
            return false;
        }
        BlockPos blockPos = serverPlayer.blockPosition();
        if (blockPos.closerThan(new Vec3(d, d2, d3), bl ? 512.0 : 32.0)) {
            serverPlayer.connection.send(packet);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Entity getEntity(int n) {
        return (Entity)this.entitiesById.get(n);
    }

    @Nullable
    public Entity getEntity(UUID uUID) {
        return this.entitiesByUuid.get(uUID);
    }

    @Nullable
    public BlockPos findNearestMapFeature(StructureFeature<?> structureFeature, BlockPos blockPos, int n, boolean bl) {
        if (!this.server.getWorldData().worldGenSettings().generateFeatures()) {
            return null;
        }
        return this.getChunkSource().getGenerator().findNearestMapFeature(this, structureFeature, blockPos, n, bl);
    }

    @Nullable
    public BlockPos findNearestBiome(Biome biome, BlockPos blockPos, int n, int n2) {
        return this.getChunkSource().getGenerator().getBiomeSource().findBiomeHorizontal(blockPos.getX(), blockPos.getY(), blockPos.getZ(), n, n2, biome2 -> biome2 == biome, this.random, true);
    }

    @Override
    public RecipeManager getRecipeManager() {
        return this.server.getRecipeManager();
    }

    @Override
    public TagContainer getTagManager() {
        return this.server.getTags();
    }

    @Override
    public boolean noSave() {
        return this.noSave;
    }

    @Override
    public RegistryAccess registryAccess() {
        return this.server.registryAccess();
    }

    public DimensionDataStorage getDataStorage() {
        return this.getChunkSource().getDataStorage();
    }

    @Nullable
    @Override
    public MapItemSavedData getMapData(String string) {
        return this.getServer().overworld().getDataStorage().get(() -> new MapItemSavedData(string), string);
    }

    @Override
    public void setMapData(MapItemSavedData mapItemSavedData) {
        this.getServer().overworld().getDataStorage().set(mapItemSavedData);
    }

    @Override
    public int getFreeMapId() {
        return this.getServer().overworld().getDataStorage().computeIfAbsent(MapIndex::new, "idcounts").getFreeAuxValueForMap();
    }

    public void setDefaultSpawnPos(BlockPos blockPos, float f) {
        ChunkPos chunkPos = new ChunkPos(new BlockPos(this.levelData.getXSpawn(), 0, this.levelData.getZSpawn()));
        this.levelData.setSpawn(blockPos, f);
        this.getChunkSource().removeRegionTicket(TicketType.START, chunkPos, 11, Unit.INSTANCE);
        this.getChunkSource().addRegionTicket(TicketType.START, new ChunkPos(blockPos), 11, Unit.INSTANCE);
        this.getServer().getPlayerList().broadcastAll(new ClientboundSetDefaultSpawnPositionPacket(blockPos, f));
    }

    public BlockPos getSharedSpawnPos() {
        BlockPos blockPos = new BlockPos(this.levelData.getXSpawn(), this.levelData.getYSpawn(), this.levelData.getZSpawn());
        if (!this.getWorldBorder().isWithinBounds(blockPos)) {
            blockPos = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
        }
        return blockPos;
    }

    public float getSharedSpawnAngle() {
        return this.levelData.getSpawnAngle();
    }

    public LongSet getForcedChunks() {
        ForcedChunksSavedData forcedChunksSavedData = this.getDataStorage().get(ForcedChunksSavedData::new, "chunks");
        return forcedChunksSavedData != null ? LongSets.unmodifiable((LongSet)forcedChunksSavedData.getChunks()) : LongSets.EMPTY_SET;
    }

    public boolean setChunkForced(int n, int n2, boolean bl) {
        boolean bl2;
        ForcedChunksSavedData forcedChunksSavedData = this.getDataStorage().computeIfAbsent(ForcedChunksSavedData::new, "chunks");
        ChunkPos chunkPos = new ChunkPos(n, n2);
        long l = chunkPos.toLong();
        if (bl) {
            bl2 = forcedChunksSavedData.getChunks().add(l);
            if (bl2) {
                this.getChunk(n, n2);
            }
        } else {
            bl2 = forcedChunksSavedData.getChunks().remove(l);
        }
        forcedChunksSavedData.setDirty(bl2);
        if (bl2) {
            this.getChunkSource().updateChunkForced(chunkPos, bl);
        }
        return bl2;
    }

    public List<ServerPlayer> players() {
        return this.players;
    }

    @Override
    public void onBlockStateChange(BlockPos blockPos, BlockState blockState, BlockState blockState2) {
        Optional<PoiType> optional;
        Optional<PoiType> optional2 = PoiType.forState(blockState);
        if (Objects.equals(optional2, optional = PoiType.forState(blockState2))) {
            return;
        }
        BlockPos blockPos2 = blockPos.immutable();
        optional2.ifPresent(poiType -> this.getServer().execute(() -> {
            this.getPoiManager().remove(blockPos2);
            DebugPackets.sendPoiRemovedPacket(this, blockPos2);
        }));
        optional.ifPresent(poiType -> this.getServer().execute(() -> {
            this.getPoiManager().add(blockPos2, (PoiType)poiType);
            DebugPackets.sendPoiAddedPacket(this, blockPos2);
        }));
    }

    public PoiManager getPoiManager() {
        return this.getChunkSource().getPoiManager();
    }

    public boolean isVillage(BlockPos blockPos) {
        return this.isCloseToVillage(blockPos, 1);
    }

    public boolean isVillage(SectionPos sectionPos) {
        return this.isVillage(sectionPos.center());
    }

    public boolean isCloseToVillage(BlockPos blockPos, int n) {
        if (n > 6) {
            return false;
        }
        return this.sectionsToVillage(SectionPos.of(blockPos)) <= n;
    }

    public int sectionsToVillage(SectionPos sectionPos) {
        return this.getPoiManager().sectionsToVillage(sectionPos);
    }

    public Raids getRaids() {
        return this.raids;
    }

    @Nullable
    public Raid getRaidAt(BlockPos blockPos) {
        return this.raids.getNearbyRaid(blockPos, 9216);
    }

    public boolean isRaided(BlockPos blockPos) {
        return this.getRaidAt(blockPos) != null;
    }

    public void onReputationEvent(ReputationEventType reputationEventType, Entity entity, ReputationEventHandler reputationEventHandler) {
        reputationEventHandler.onReputationEventFrom(reputationEventType, entity);
    }

    public void saveDebugReport(Path path) throws IOException {
        Object object5;
        Object object2;
        ChunkMap chunkMap = this.getChunkSource().chunkMap;
        Object object3 = Files.newBufferedWriter(path.resolve("stats.txt"), new OpenOption[0]);
        Object object4 = null;
        try {
            ((Writer)object3).write(String.format("spawning_chunks: %d\n", chunkMap.getDistanceManager().getNaturalSpawnChunkCount()));
            object2 = this.getChunkSource().getLastSpawnState();
            if (object2 != null) {
                for (Object object5 : ((NaturalSpawner.SpawnState)object2).getMobCategoryCounts().object2IntEntrySet()) {
                    ((Writer)object3).write(String.format("spawn_count.%s: %d\n", ((MobCategory)object5.getKey()).getName(), object5.getIntValue()));
                }
            }
            ((Writer)object3).write(String.format("entities: %d\n", this.entitiesById.size()));
            ((Writer)object3).write(String.format("block_entities: %d\n", this.blockEntityList.size()));
            ((Writer)object3).write(String.format("block_ticks: %d\n", ((ServerTickList)this.getBlockTicks()).size()));
            ((Writer)object3).write(String.format("fluid_ticks: %d\n", ((ServerTickList)this.getLiquidTicks()).size()));
            ((Writer)object3).write("distance_manager: " + chunkMap.getDistanceManager().getDebugStatus() + "\n");
            ((Writer)object3).write(String.format("pending_tasks: %d\n", this.getChunkSource().getPendingTasksCount()));
        }
        catch (Throwable throwable) {
            object4 = throwable;
            throw throwable;
        }
        finally {
            if (object3 != null) {
                if (object4 != null) {
                    try {
                        ((Writer)object3).close();
                    }
                    catch (Throwable throwable) {
                        ((Throwable)object4).addSuppressed(throwable);
                    }
                } else {
                    ((Writer)object3).close();
                }
            }
        }
        object3 = new CrashReport("Level dump", new Exception("dummy"));
        this.fillReportDetails((CrashReport)object3);
        object4 = Files.newBufferedWriter(path.resolve("example_crash.txt"), new OpenOption[0]);
        object2 = null;
        try {
            ((Writer)object4).write(((CrashReport)object3).getFriendlyReport());
        }
        catch (Throwable throwable) {
            object2 = throwable;
            throw throwable;
        }
        finally {
            if (object4 != null) {
                if (object2 != null) {
                    try {
                        ((Writer)object4).close();
                    }
                    catch (Throwable throwable) {
                        ((Throwable)object2).addSuppressed(throwable);
                    }
                } else {
                    ((Writer)object4).close();
                }
            }
        }
        object4 = path.resolve("chunks.csv");
        object2 = Files.newBufferedWriter((Path)object4, new OpenOption[0]);
        Object object6 = null;
        try {
            chunkMap.dumpChunks((Writer)object2);
        }
        catch (Throwable throwable) {
            object6 = throwable;
            throw throwable;
        }
        finally {
            if (object2 != null) {
                if (object6 != null) {
                    try {
                        ((Writer)object2).close();
                    }
                    catch (Throwable throwable) {
                        ((Throwable)object6).addSuppressed(throwable);
                    }
                } else {
                    ((Writer)object2).close();
                }
            }
        }
        object2 = path.resolve("entities.csv");
        object6 = Files.newBufferedWriter((Path)object2, new OpenOption[0]);
        object5 = null;
        try {
            ServerLevel.dumpEntities((Writer)object6, (Iterable<Entity>)this.entitiesById.values());
        }
        catch (Throwable throwable) {
            object5 = throwable;
            throw throwable;
        }
        finally {
            if (object6 != null) {
                if (object5 != null) {
                    try {
                        ((Writer)object6).close();
                    }
                    catch (Throwable throwable) {
                        ((Throwable)object5).addSuppressed(throwable);
                    }
                } else {
                    ((Writer)object6).close();
                }
            }
        }
        object6 = path.resolve("block_entities.csv");
        object5 = Files.newBufferedWriter((Path)object6, new OpenOption[0]);
        Throwable throwable = null;
        try {
            this.dumpBlockEntities((Writer)object5);
        }
        catch (Throwable throwable2) {
            throwable = throwable2;
            throw throwable2;
        }
        finally {
            if (object5 != null) {
                if (throwable != null) {
                    try {
                        ((Writer)object5).close();
                    }
                    catch (Throwable throwable3) {
                        throwable.addSuppressed(throwable3);
                    }
                } else {
                    ((Writer)object5).close();
                }
            }
        }
    }

    private static void dumpEntities(Writer writer, Iterable<Entity> iterable) throws IOException {
        CsvOutput csvOutput = CsvOutput.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("uuid").addColumn("type").addColumn("alive").addColumn("display_name").addColumn("custom_name").build(writer);
        for (Entity entity : iterable) {
            Component component = entity.getCustomName();
            Component component2 = entity.getDisplayName();
            csvOutput.writeRow(entity.getX(), entity.getY(), entity.getZ(), entity.getUUID(), Registry.ENTITY_TYPE.getKey(entity.getType()), entity.isAlive(), component2.getString(), component != null ? component.getString() : null);
        }
    }

    private void dumpBlockEntities(Writer writer) throws IOException {
        CsvOutput csvOutput = CsvOutput.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("type").build(writer);
        for (BlockEntity blockEntity : this.blockEntityList) {
            BlockPos blockPos = blockEntity.getBlockPos();
            csvOutput.writeRow(blockPos.getX(), blockPos.getY(), blockPos.getZ(), Registry.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()));
        }
    }

    @VisibleForTesting
    public void clearBlockEvents(BoundingBox boundingBox) {
        this.blockEvents.removeIf(blockEventData -> boundingBox.isInside(blockEventData.getPos()));
    }

    @Override
    public void blockUpdated(BlockPos blockPos, Block block) {
        if (!this.isDebug()) {
            this.updateNeighborsAt(blockPos, block);
        }
    }

    @Override
    public float getShade(Direction direction, boolean bl) {
        return 1.0f;
    }

    public Iterable<Entity> getAllEntities() {
        return Iterables.unmodifiableIterable((Iterable)this.entitiesById.values());
    }

    public String toString() {
        return "ServerLevel[" + this.serverLevelData.getLevelName() + "]";
    }

    public boolean isFlat() {
        return this.server.getWorldData().worldGenSettings().isFlatWorld();
    }

    @Override
    public long getSeed() {
        return this.server.getWorldData().worldGenSettings().seed();
    }

    @Nullable
    public EndDragonFight dragonFight() {
        return this.dragonFight;
    }

    @Override
    public Stream<? extends StructureStart<?>> startsForFeature(SectionPos sectionPos, StructureFeature<?> structureFeature) {
        return this.structureFeatureManager().startsForFeature(sectionPos, structureFeature);
    }

    @Override
    public ServerLevel getLevel() {
        return this;
    }

    @VisibleForTesting
    public String getWatchdogStats() {
        return String.format("players: %s, entities: %d [%s], block_entities: %d [%s], block_ticks: %d, fluid_ticks: %d, chunk_source: %s", this.players.size(), this.entitiesById.size(), ServerLevel.getTypeCount(this.entitiesById.values(), entity -> Registry.ENTITY_TYPE.getKey(entity.getType())), this.tickableBlockEntities.size(), ServerLevel.getTypeCount(this.tickableBlockEntities, blockEntity -> Registry.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType())), ((ServerTickList)this.getBlockTicks()).size(), ((ServerTickList)this.getLiquidTicks()).size(), this.gatherChunkSourceStats());
    }

    private static <T> String getTypeCount(Collection<T> collection, Function<T, ResourceLocation> function) {
        try {
            Object2IntOpenHashMap object2IntOpenHashMap = new Object2IntOpenHashMap();
            for (T t : collection) {
                ResourceLocation resourceLocation = function.apply(t);
                object2IntOpenHashMap.addTo((Object)resourceLocation, 1);
            }
            return object2IntOpenHashMap.object2IntEntrySet().stream().sorted(Comparator.comparing(Object2IntMap.Entry::getIntValue).reversed()).limit(5L).map(entry -> entry.getKey() + ":" + entry.getIntValue()).collect(Collectors.joining(","));
        }
        catch (Exception exception) {
            return "";
        }
    }

    public static void makeObsidianPlatform(ServerLevel serverLevel) {
        BlockPos blockPos2 = END_SPAWN_POINT;
        int n = blockPos2.getX();
        int n2 = blockPos2.getY() - 2;
        int n3 = blockPos2.getZ();
        BlockPos.betweenClosed(n - 2, n2 + 1, n3 - 2, n + 2, n2 + 3, n3 + 2).forEach(blockPos -> serverLevel.setBlockAndUpdate((BlockPos)blockPos, Blocks.AIR.defaultBlockState()));
        BlockPos.betweenClosed(n - 2, n2, n3 - 2, n + 2, n2, n3 + 2).forEach(blockPos -> serverLevel.setBlockAndUpdate((BlockPos)blockPos, Blocks.OBSIDIAN.defaultBlockState()));
    }

    @Override
    public /* synthetic */ Scoreboard getScoreboard() {
        return this.getScoreboard();
    }

    @Override
    public /* synthetic */ ChunkSource getChunkSource() {
        return this.getChunkSource();
    }

    public /* synthetic */ TickList getLiquidTicks() {
        return this.getLiquidTicks();
    }

    public /* synthetic */ TickList getBlockTicks() {
        return this.getBlockTicks();
    }
}

