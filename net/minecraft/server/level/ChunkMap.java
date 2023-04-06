/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.util.Either
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteMap
 *  it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectSortedMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectSortedMap$FastSortedEntrySet
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.ObjectCollection
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.level;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectSortedMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkTaskPriorityQueue;
import net.minecraft.server.level.ChunkTaskPriorityQueueSorter;
import net.minecraft.server.level.PlayerMap;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkMap
extends ChunkStorage
implements ChunkHolder.PlayerProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final int MAX_CHUNK_DISTANCE = 33 + ChunkStatus.maxDistance();
    private final Long2ObjectLinkedOpenHashMap<ChunkHolder> updatingChunkMap = new Long2ObjectLinkedOpenHashMap();
    private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> visibleChunkMap = this.updatingChunkMap.clone();
    private final Long2ObjectLinkedOpenHashMap<ChunkHolder> pendingUnloads = new Long2ObjectLinkedOpenHashMap();
    private final LongSet entitiesInLevel = new LongOpenHashSet();
    private final ServerLevel level;
    private final ThreadedLevelLightEngine lightEngine;
    private final BlockableEventLoop<Runnable> mainThreadExecutor;
    private final ChunkGenerator generator;
    private final Supplier<DimensionDataStorage> overworldDataStorage;
    private final PoiManager poiManager;
    private final LongSet toDrop = new LongOpenHashSet();
    private boolean modified;
    private final ChunkTaskPriorityQueueSorter queueSorter;
    private final ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> worldgenMailbox;
    private final ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> mainThreadMailbox;
    private final ChunkProgressListener progressListener;
    private final DistanceManager distanceManager;
    private final AtomicInteger tickingGenerated = new AtomicInteger();
    private final StructureManager structureManager;
    private final File storageFolder;
    private final PlayerMap playerMap = new PlayerMap();
    private final Int2ObjectMap<TrackedEntity> entityMap = new Int2ObjectOpenHashMap();
    private final Long2ByteMap chunkTypeCache = new Long2ByteOpenHashMap();
    private final Queue<Runnable> unloadQueue = Queues.newConcurrentLinkedQueue();
    private int viewDistance;

    public ChunkMap(ServerLevel serverLevel, LevelStorageSource.LevelStorageAccess levelStorageAccess, DataFixer dataFixer, StructureManager structureManager, Executor executor, BlockableEventLoop<Runnable> blockableEventLoop, LightChunkGetter lightChunkGetter, ChunkGenerator chunkGenerator, ChunkProgressListener chunkProgressListener, Supplier<DimensionDataStorage> supplier, int n, boolean bl) {
        super(new File(levelStorageAccess.getDimensionPath(serverLevel.dimension()), "region"), dataFixer, bl);
        this.structureManager = structureManager;
        this.storageFolder = levelStorageAccess.getDimensionPath(serverLevel.dimension());
        this.level = serverLevel;
        this.generator = chunkGenerator;
        this.mainThreadExecutor = blockableEventLoop;
        ProcessorMailbox<Runnable> processorMailbox = ProcessorMailbox.create(executor, "worldgen");
        ProcessorHandle<Runnable> processorHandle = ProcessorHandle.of("main", blockableEventLoop::tell);
        this.progressListener = chunkProgressListener;
        ProcessorMailbox<Runnable> processorMailbox2 = ProcessorMailbox.create(executor, "light");
        this.queueSorter = new ChunkTaskPriorityQueueSorter((List<ProcessorHandle<?>>)ImmutableList.of(processorMailbox, processorHandle, processorMailbox2), executor, Integer.MAX_VALUE);
        this.worldgenMailbox = this.queueSorter.getProcessor(processorMailbox, false);
        this.mainThreadMailbox = this.queueSorter.getProcessor(processorHandle, false);
        this.lightEngine = new ThreadedLevelLightEngine(lightChunkGetter, this, this.level.dimensionType().hasSkyLight(), processorMailbox2, this.queueSorter.getProcessor(processorMailbox2, false));
        this.distanceManager = new DistanceManager(executor, blockableEventLoop);
        this.overworldDataStorage = supplier;
        this.poiManager = new PoiManager(new File(this.storageFolder, "poi"), dataFixer, bl);
        this.setViewDistance(n);
    }

    private static double euclideanDistanceSquared(ChunkPos chunkPos, Entity entity) {
        double d = chunkPos.x * 16 + 8;
        double d2 = chunkPos.z * 16 + 8;
        double d3 = d - entity.getX();
        double d4 = d2 - entity.getZ();
        return d3 * d3 + d4 * d4;
    }

    private static int checkerboardDistance(ChunkPos chunkPos, ServerPlayer serverPlayer, boolean bl) {
        int n;
        int n2;
        if (bl) {
            SectionPos sectionPos = serverPlayer.getLastSectionPos();
            n = sectionPos.x();
            n2 = sectionPos.z();
        } else {
            n = Mth.floor(serverPlayer.getX() / 16.0);
            n2 = Mth.floor(serverPlayer.getZ() / 16.0);
        }
        return ChunkMap.checkerboardDistance(chunkPos, n, n2);
    }

    private static int checkerboardDistance(ChunkPos chunkPos, int n, int n2) {
        int n3 = chunkPos.x - n;
        int n4 = chunkPos.z - n2;
        return Math.max(Math.abs(n3), Math.abs(n4));
    }

    protected ThreadedLevelLightEngine getLightEngine() {
        return this.lightEngine;
    }

    @Nullable
    protected ChunkHolder getUpdatingChunkIfPresent(long l) {
        return (ChunkHolder)this.updatingChunkMap.get(l);
    }

    @Nullable
    protected ChunkHolder getVisibleChunkIfPresent(long l) {
        return (ChunkHolder)this.visibleChunkMap.get(l);
    }

    protected IntSupplier getChunkQueueLevel(long l) {
        return () -> {
            ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(l);
            if (chunkHolder == null) {
                return ChunkTaskPriorityQueue.PRIORITY_LEVEL_COUNT - 1;
            }
            return Math.min(chunkHolder.getQueueLevel(), ChunkTaskPriorityQueue.PRIORITY_LEVEL_COUNT - 1);
        };
    }

    public String getChunkDebugData(ChunkPos chunkPos) {
        ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(chunkPos.toLong());
        if (chunkHolder == null) {
            return "null";
        }
        String string = chunkHolder.getTicketLevel() + "\n";
        ChunkStatus chunkStatus = chunkHolder.getLastAvailableStatus();
        ChunkAccess chunkAccess = chunkHolder.getLastAvailable();
        if (chunkStatus != null) {
            string = string + "St: \u00a7" + chunkStatus.getIndex() + chunkStatus + '\u00a7' + "r\n";
        }
        if (chunkAccess != null) {
            string = string + "Ch: \u00a7" + chunkAccess.getStatus().getIndex() + chunkAccess.getStatus() + '\u00a7' + "r\n";
        }
        ChunkHolder.FullChunkStatus fullChunkStatus = chunkHolder.getFullStatus();
        string = string + "\u00a7" + fullChunkStatus.ordinal() + (Object)((Object)fullChunkStatus);
        return string + '\u00a7' + "r";
    }

    private CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>> getChunkRangeFuture(ChunkPos chunkPos, final int n, IntFunction<ChunkStatus> intFunction) {
        ArrayList arrayList = Lists.newArrayList();
        final int n2 = chunkPos.x;
        final int n3 = chunkPos.z;
        for (int i = -n; i <= n; ++i) {
            for (int j = -n; j <= n; ++j) {
                int n4 = Math.max(Math.abs(j), Math.abs(i));
                final ChunkPos chunkPos2 = new ChunkPos(n2 + j, n3 + i);
                long l = chunkPos2.toLong();
                ChunkHolder chunkHolder = this.getUpdatingChunkIfPresent(l);
                if (chunkHolder == null) {
                    return CompletableFuture.completedFuture(Either.right((Object)new ChunkHolder.ChunkLoadingFailure(){

                        public String toString() {
                            return "Unloaded " + chunkPos2.toString();
                        }
                    }));
                }
                ChunkStatus chunkStatus = intFunction.apply(n4);
                CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completableFuture = chunkHolder.getOrScheduleFuture(chunkStatus, this);
                arrayList.add(completableFuture);
            }
        }
        CompletableFuture completableFuture = Util.sequence(arrayList);
        return completableFuture.thenApply(list -> {
            ArrayList arrayList = Lists.newArrayList();
            int n4 = 0;
            for (final Either either : list) {
                Optional optional = either.left();
                if (!optional.isPresent()) {
                    final int n5 = n4;
                    return Either.right((Object)new ChunkHolder.ChunkLoadingFailure(){

                        public String toString() {
                            return "Unloaded " + new ChunkPos(n2 + n5 % (n * 2 + 1), n3 + n5 / (n * 2 + 1)) + " " + ((ChunkHolder.ChunkLoadingFailure)either.right().get()).toString();
                        }
                    });
                }
                arrayList.add(optional.get());
                ++n4;
            }
            return Either.left((Object)arrayList);
        });
    }

    public CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> getEntityTickingRangeFuture(ChunkPos chunkPos) {
        return this.getChunkRangeFuture(chunkPos, 2, n -> ChunkStatus.FULL).thenApplyAsync(either -> either.mapLeft(list -> (LevelChunk)list.get(list.size() / 2)), (Executor)this.mainThreadExecutor);
    }

    @Nullable
    private ChunkHolder updateChunkScheduling(long l, int n, @Nullable ChunkHolder chunkHolder, int n2) {
        if (n2 > MAX_CHUNK_DISTANCE && n > MAX_CHUNK_DISTANCE) {
            return chunkHolder;
        }
        if (chunkHolder != null) {
            chunkHolder.setTicketLevel(n);
        }
        if (chunkHolder != null) {
            if (n > MAX_CHUNK_DISTANCE) {
                this.toDrop.add(l);
            } else {
                this.toDrop.remove(l);
            }
        }
        if (n <= MAX_CHUNK_DISTANCE && chunkHolder == null) {
            chunkHolder = (ChunkHolder)this.pendingUnloads.remove(l);
            if (chunkHolder != null) {
                chunkHolder.setTicketLevel(n);
            } else {
                chunkHolder = new ChunkHolder(new ChunkPos(l), n, this.lightEngine, this.queueSorter, this);
            }
            this.updatingChunkMap.put(l, (Object)chunkHolder);
            this.modified = true;
        }
        return chunkHolder;
    }

    @Override
    public void close() throws IOException {
        try {
            this.queueSorter.close();
            this.poiManager.close();
        }
        finally {
            super.close();
        }
    }

    protected void saveAllChunks(boolean bl) {
        if (bl) {
            List list = this.visibleChunkMap.values().stream().filter(ChunkHolder::wasAccessibleSinceLastSave).peek(ChunkHolder::refreshAccessibility).collect(Collectors.toList());
            MutableBoolean mutableBoolean = new MutableBoolean();
            do {
                mutableBoolean.setFalse();
                list.stream().map(chunkHolder -> {
                    CompletableFuture<ChunkAccess> completableFuture;
                    do {
                        completableFuture = chunkHolder.getChunkToSave();
                        this.mainThreadExecutor.managedBlock(completableFuture::isDone);
                    } while (completableFuture != chunkHolder.getChunkToSave());
                    return completableFuture.join();
                }).filter(chunkAccess -> chunkAccess instanceof ImposterProtoChunk || chunkAccess instanceof LevelChunk).filter(this::save).forEach(chunkAccess -> mutableBoolean.setTrue());
            } while (mutableBoolean.isTrue());
            this.processUnloads(() -> true);
            this.flushWorker();
            LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", (Object)this.storageFolder.getName());
        } else {
            this.visibleChunkMap.values().stream().filter(ChunkHolder::wasAccessibleSinceLastSave).forEach(chunkHolder -> {
                ChunkAccess chunkAccess = chunkHolder.getChunkToSave().getNow(null);
                if (chunkAccess instanceof ImposterProtoChunk || chunkAccess instanceof LevelChunk) {
                    this.save(chunkAccess);
                    chunkHolder.refreshAccessibility();
                }
            });
        }
    }

    protected void tick(BooleanSupplier booleanSupplier) {
        ProfilerFiller profilerFiller = this.level.getProfiler();
        profilerFiller.push("poi");
        this.poiManager.tick(booleanSupplier);
        profilerFiller.popPush("chunk_unload");
        if (!this.level.noSave()) {
            this.processUnloads(booleanSupplier);
        }
        profilerFiller.pop();
    }

    private void processUnloads(BooleanSupplier booleanSupplier) {
        Runnable runnable;
        LongIterator longIterator = this.toDrop.iterator();
        int n = 0;
        while (longIterator.hasNext() && (booleanSupplier.getAsBoolean() || n < 200 || this.toDrop.size() > 2000)) {
            long l = longIterator.nextLong();
            ChunkHolder chunkHolder = (ChunkHolder)this.updatingChunkMap.remove(l);
            if (chunkHolder != null) {
                this.pendingUnloads.put(l, (Object)chunkHolder);
                this.modified = true;
                ++n;
                this.scheduleUnload(l, chunkHolder);
            }
            longIterator.remove();
        }
        while ((booleanSupplier.getAsBoolean() || this.unloadQueue.size() > 2000) && (runnable = this.unloadQueue.poll()) != null) {
            runnable.run();
        }
    }

    private void scheduleUnload(long l, ChunkHolder chunkHolder) {
        CompletableFuture<ChunkAccess> completableFuture = chunkHolder.getChunkToSave();
        ((CompletableFuture)completableFuture.thenAcceptAsync(chunkAccess -> {
            CompletableFuture<ChunkAccess> completableFuture2 = chunkHolder.getChunkToSave();
            if (completableFuture2 != completableFuture) {
                this.scheduleUnload(l, chunkHolder);
                return;
            }
            if (this.pendingUnloads.remove(l, (Object)chunkHolder) && chunkAccess != null) {
                if (chunkAccess instanceof LevelChunk) {
                    ((LevelChunk)chunkAccess).setLoaded(false);
                }
                this.save((ChunkAccess)chunkAccess);
                if (this.entitiesInLevel.remove(l) && chunkAccess instanceof LevelChunk) {
                    LevelChunk levelChunk = (LevelChunk)chunkAccess;
                    this.level.unload(levelChunk);
                }
                this.lightEngine.updateChunkStatus(chunkAccess.getPos());
                this.lightEngine.tryScheduleUpdate();
                this.progressListener.onStatusChange(chunkAccess.getPos(), null);
            }
        }, this.unloadQueue::add)).whenComplete((void_, throwable) -> {
            if (throwable != null) {
                LOGGER.error("Failed to save chunk " + chunkHolder.getPos(), throwable);
            }
        });
    }

    protected boolean promoteChunkMap() {
        if (!this.modified) {
            return false;
        }
        this.visibleChunkMap = this.updatingChunkMap.clone();
        this.modified = false;
        return true;
    }

    public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> schedule(ChunkHolder chunkHolder, ChunkStatus chunkStatus) {
        ChunkPos chunkPos = chunkHolder.getPos();
        if (chunkStatus == ChunkStatus.EMPTY) {
            return this.scheduleChunkLoad(chunkPos);
        }
        CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completableFuture = chunkHolder.getOrScheduleFuture(chunkStatus.getParent(), this);
        return completableFuture.thenComposeAsync(either -> {
            ChunkAccess chunkAccess2;
            Optional optional = either.left();
            if (!optional.isPresent()) {
                return CompletableFuture.completedFuture(either);
            }
            if (chunkStatus == ChunkStatus.LIGHT) {
                this.distanceManager.addTicket(TicketType.LIGHT, chunkPos, 33 + ChunkStatus.getDistance(ChunkStatus.FEATURES), chunkPos);
            }
            if ((chunkAccess2 = (ChunkAccess)optional.get()).getStatus().isOrAfter(chunkStatus)) {
                CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completableFuture = chunkStatus == ChunkStatus.LIGHT ? this.scheduleChunkGeneration(chunkHolder, chunkStatus) : chunkStatus.load(this.level, this.structureManager, this.lightEngine, chunkAccess -> this.protoChunkToFullChunk(chunkHolder), chunkAccess2);
                this.progressListener.onStatusChange(chunkPos, chunkStatus);
                return completableFuture;
            }
            return this.scheduleChunkGeneration(chunkHolder, chunkStatus);
        }, (Executor)this.mainThreadExecutor);
    }

    private CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> scheduleChunkLoad(ChunkPos chunkPos) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                this.level.getProfiler().incrementCounter("chunkLoad");
                CompoundTag compoundTag = this.readChunk(chunkPos);
                if (compoundTag != null) {
                    boolean bl;
                    boolean bl2 = bl = compoundTag.contains("Level", 10) && compoundTag.getCompound("Level").contains("Status", 8);
                    if (bl) {
                        ProtoChunk protoChunk = ChunkSerializer.read(this.level, this.structureManager, this.poiManager, chunkPos, compoundTag);
                        protoChunk.setLastSaveTime(this.level.getGameTime());
                        this.markPosition(chunkPos, protoChunk.getStatus().getChunkType());
                        return Either.left((Object)protoChunk);
                    }
                    LOGGER.error("Chunk file at {} is missing level data, skipping", (Object)chunkPos);
                }
            }
            catch (ReportedException reportedException) {
                Throwable throwable = reportedException.getCause();
                if (throwable instanceof IOException) {
                    LOGGER.error("Couldn't load chunk {}", (Object)chunkPos, (Object)throwable);
                }
                this.markPositionReplaceable(chunkPos);
                throw reportedException;
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't load chunk {}", (Object)chunkPos, (Object)exception);
            }
            this.markPositionReplaceable(chunkPos);
            return Either.left((Object)new ProtoChunk(chunkPos, UpgradeData.EMPTY));
        }, this.mainThreadExecutor);
    }

    private void markPositionReplaceable(ChunkPos chunkPos) {
        this.chunkTypeCache.put(chunkPos.toLong(), (byte)-1);
    }

    private byte markPosition(ChunkPos chunkPos, ChunkStatus.ChunkType chunkType) {
        return this.chunkTypeCache.put(chunkPos.toLong(), chunkType == ChunkStatus.ChunkType.PROTOCHUNK ? (byte)-1 : 1);
    }

    private CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> scheduleChunkGeneration(ChunkHolder chunkHolder, ChunkStatus chunkStatus) {
        ChunkPos chunkPos = chunkHolder.getPos();
        CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>> completableFuture = this.getChunkRangeFuture(chunkPos, chunkStatus.getRange(), n -> this.getDependencyStatus(chunkStatus, n));
        this.level.getProfiler().incrementCounter(() -> "chunkGenerate " + chunkStatus.getName());
        return completableFuture.thenComposeAsync(either -> (CompletableFuture)either.map(list -> {
            try {
                CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completableFuture = chunkStatus.generate(this.level, this.generator, this.structureManager, this.lightEngine, chunkAccess -> this.protoChunkToFullChunk(chunkHolder), (List<ChunkAccess>)list);
                this.progressListener.onStatusChange(chunkPos, chunkStatus);
                return completableFuture;
            }
            catch (Exception exception) {
                CrashReport crashReport = CrashReport.forThrowable(exception, "Exception generating new chunk");
                CrashReportCategory crashReportCategory = crashReport.addCategory("Chunk to be generated");
                crashReportCategory.setDetail("Location", String.format("%d,%d", chunkPos.x, chunkPos.z));
                crashReportCategory.setDetail("Position hash", ChunkPos.asLong(chunkPos.x, chunkPos.z));
                crashReportCategory.setDetail("Generator", this.generator);
                throw new ReportedException(crashReport);
            }
        }, chunkLoadingFailure -> {
            this.releaseLightTicket(chunkPos);
            return CompletableFuture.completedFuture(Either.right((Object)chunkLoadingFailure));
        }), runnable -> this.worldgenMailbox.tell(ChunkTaskPriorityQueueSorter.message(chunkHolder, runnable)));
    }

    protected void releaseLightTicket(ChunkPos chunkPos) {
        this.mainThreadExecutor.tell(Util.name(() -> this.distanceManager.removeTicket(TicketType.LIGHT, chunkPos, 33 + ChunkStatus.getDistance(ChunkStatus.FEATURES), chunkPos), () -> "release light ticket " + chunkPos));
    }

    private ChunkStatus getDependencyStatus(ChunkStatus chunkStatus, int n) {
        ChunkStatus chunkStatus2 = n == 0 ? chunkStatus.getParent() : ChunkStatus.getStatus(ChunkStatus.getDistance(chunkStatus) + n);
        return chunkStatus2;
    }

    private CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> protoChunkToFullChunk(ChunkHolder chunkHolder) {
        CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completableFuture = chunkHolder.getFutureIfPresentUnchecked(ChunkStatus.FULL.getParent());
        return completableFuture.thenApplyAsync(either -> {
            ChunkStatus chunkStatus = ChunkHolder.getStatus(chunkHolder.getTicketLevel());
            if (!chunkStatus.isOrAfter(ChunkStatus.FULL)) {
                return ChunkHolder.UNLOADED_CHUNK;
            }
            return either.mapLeft(chunkAccess -> {
                LevelChunk levelChunk;
                ChunkPos chunkPos = chunkHolder.getPos();
                if (chunkAccess instanceof ImposterProtoChunk) {
                    levelChunk = ((ImposterProtoChunk)chunkAccess).getWrapped();
                } else {
                    levelChunk = new LevelChunk(this.level, (ProtoChunk)chunkAccess);
                    chunkHolder.replaceProtoChunk(new ImposterProtoChunk(levelChunk));
                }
                levelChunk.setFullStatus(() -> ChunkHolder.getFullChunkStatus(chunkHolder.getTicketLevel()));
                levelChunk.runPostLoad();
                if (this.entitiesInLevel.add(chunkPos.toLong())) {
                    levelChunk.setLoaded(true);
                    this.level.addAllPendingBlockEntities(levelChunk.getBlockEntities().values());
                    Iterable iterable = null;
                    for (ClassInstanceMultiMap<Entity> classInstanceMultiMap : levelChunk.getEntitySections()) {
                        for (Entity entity : classInstanceMultiMap) {
                            if (entity instanceof Player || this.level.loadFromChunk(entity)) continue;
                            if (iterable == null) {
                                iterable = Lists.newArrayList((Object[])new Entity[]{entity});
                                continue;
                            }
                            iterable.add(entity);
                        }
                    }
                    if (iterable != null) {
                        iterable.forEach(levelChunk::removeEntity);
                    }
                }
                return levelChunk;
            });
        }, runnable -> this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message(runnable, chunkHolder.getPos().toLong(), chunkHolder::getTicketLevel)));
    }

    public CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> postProcess(ChunkHolder chunkHolder) {
        ChunkPos chunkPos = chunkHolder.getPos();
        CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>> completableFuture = this.getChunkRangeFuture(chunkPos, 1, n -> ChunkStatus.FULL);
        CompletionStage completionStage = completableFuture.thenApplyAsync(either -> either.flatMap(list -> {
            LevelChunk levelChunk = (LevelChunk)list.get(list.size() / 2);
            levelChunk.postProcessGeneration();
            return Either.left((Object)levelChunk);
        }), runnable -> this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message(chunkHolder, runnable)));
        ((CompletableFuture)completionStage).thenAcceptAsync(either -> either.mapLeft(levelChunk -> {
            this.tickingGenerated.getAndIncrement();
            Packet[] arrpacket = new Packet[2];
            this.getPlayers(chunkPos, false).forEach(serverPlayer -> this.playerLoadedChunk((ServerPlayer)serverPlayer, arrpacket, (LevelChunk)levelChunk));
            return Either.left((Object)levelChunk);
        }), runnable -> this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message(chunkHolder, runnable)));
        return completionStage;
    }

    public CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> unpackTicks(ChunkHolder chunkHolder) {
        return chunkHolder.getOrScheduleFuture(ChunkStatus.FULL, this).thenApplyAsync(either -> either.mapLeft(chunkAccess -> {
            LevelChunk levelChunk = (LevelChunk)chunkAccess;
            levelChunk.unpackTicks();
            return levelChunk;
        }), runnable -> this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message(chunkHolder, runnable)));
    }

    public int getTickingGenerated() {
        return this.tickingGenerated.get();
    }

    private boolean save(ChunkAccess chunkAccess) {
        this.poiManager.flush(chunkAccess.getPos());
        if (!chunkAccess.isUnsaved()) {
            return false;
        }
        chunkAccess.setLastSaveTime(this.level.getGameTime());
        chunkAccess.setUnsaved(false);
        ChunkPos chunkPos = chunkAccess.getPos();
        try {
            ChunkStatus chunkStatus = chunkAccess.getStatus();
            if (chunkStatus.getChunkType() != ChunkStatus.ChunkType.LEVELCHUNK) {
                if (this.isExistingChunkFull(chunkPos)) {
                    return false;
                }
                if (chunkStatus == ChunkStatus.EMPTY && chunkAccess.getAllStarts().values().stream().noneMatch(StructureStart::isValid)) {
                    return false;
                }
            }
            this.level.getProfiler().incrementCounter("chunkSave");
            CompoundTag compoundTag = ChunkSerializer.write(this.level, chunkAccess);
            this.write(chunkPos, compoundTag);
            this.markPosition(chunkPos, chunkStatus.getChunkType());
            return true;
        }
        catch (Exception exception) {
            LOGGER.error("Failed to save chunk {},{}", (Object)chunkPos.x, (Object)chunkPos.z, (Object)exception);
            return false;
        }
    }

    private boolean isExistingChunkFull(ChunkPos chunkPos) {
        CompoundTag compoundTag;
        byte by = this.chunkTypeCache.get(chunkPos.toLong());
        if (by != 0) {
            return by == 1;
        }
        try {
            compoundTag = this.readChunk(chunkPos);
            if (compoundTag == null) {
                this.markPositionReplaceable(chunkPos);
                return false;
            }
        }
        catch (Exception exception) {
            LOGGER.error("Failed to read chunk {}", (Object)chunkPos, (Object)exception);
            this.markPositionReplaceable(chunkPos);
            return false;
        }
        ChunkStatus.ChunkType chunkType = ChunkSerializer.getChunkTypeFromTag(compoundTag);
        return this.markPosition(chunkPos, chunkType) == 1;
    }

    protected void setViewDistance(int n) {
        int n2 = Mth.clamp(n + 1, 3, 33);
        if (n2 != this.viewDistance) {
            int n3 = this.viewDistance;
            this.viewDistance = n2;
            this.distanceManager.updatePlayerTickets(this.viewDistance);
            for (ChunkHolder chunkHolder : this.updatingChunkMap.values()) {
                ChunkPos chunkPos = chunkHolder.getPos();
                Packet[] arrpacket = new Packet[2];
                this.getPlayers(chunkPos, false).forEach(serverPlayer -> {
                    int n2 = ChunkMap.checkerboardDistance(chunkPos, serverPlayer, true);
                    boolean bl = n2 <= n3;
                    boolean bl2 = n2 <= this.viewDistance;
                    this.updateChunkTracking((ServerPlayer)serverPlayer, chunkPos, arrpacket, bl, bl2);
                });
            }
        }
    }

    protected void updateChunkTracking(ServerPlayer serverPlayer, ChunkPos chunkPos, Packet<?>[] arrpacket, boolean bl, boolean bl2) {
        ChunkHolder chunkHolder;
        if (serverPlayer.level != this.level) {
            return;
        }
        if (bl2 && !bl && (chunkHolder = this.getVisibleChunkIfPresent(chunkPos.toLong())) != null) {
            LevelChunk levelChunk = chunkHolder.getTickingChunk();
            if (levelChunk != null) {
                this.playerLoadedChunk(serverPlayer, arrpacket, levelChunk);
            }
            DebugPackets.sendPoiPacketsForChunk(this.level, chunkPos);
        }
        if (!bl2 && bl) {
            serverPlayer.untrackChunk(chunkPos);
        }
    }

    public int size() {
        return this.visibleChunkMap.size();
    }

    protected DistanceManager getDistanceManager() {
        return this.distanceManager;
    }

    protected Iterable<ChunkHolder> getChunks() {
        return Iterables.unmodifiableIterable((Iterable)this.visibleChunkMap.values());
    }

    void dumpChunks(Writer writer) throws IOException {
        CsvOutput csvOutput = CsvOutput.builder().addColumn("x").addColumn("z").addColumn("level").addColumn("in_memory").addColumn("status").addColumn("full_status").addColumn("accessible_ready").addColumn("ticking_ready").addColumn("entity_ticking_ready").addColumn("ticket").addColumn("spawning").addColumn("entity_count").addColumn("block_entity_count").build(writer);
        for (Long2ObjectMap.Entry entry : this.visibleChunkMap.long2ObjectEntrySet()) {
            ChunkPos chunkPos = new ChunkPos(entry.getLongKey());
            ChunkHolder chunkHolder = (ChunkHolder)entry.getValue();
            Optional<ChunkAccess> optional = Optional.ofNullable(chunkHolder.getLastAvailable());
            Optional<Object> optional2 = optional.flatMap(chunkAccess -> chunkAccess instanceof LevelChunk ? Optional.of((LevelChunk)chunkAccess) : Optional.empty());
            csvOutput.writeRow(chunkPos.x, chunkPos.z, chunkHolder.getTicketLevel(), optional.isPresent(), optional.map(ChunkAccess::getStatus).orElse(null), optional2.map(LevelChunk::getFullStatus).orElse(null), ChunkMap.printFuture(chunkHolder.getFullChunkFuture()), ChunkMap.printFuture(chunkHolder.getTickingChunkFuture()), ChunkMap.printFuture(chunkHolder.getEntityTickingChunkFuture()), this.distanceManager.getTicketDebugString(entry.getLongKey()), !this.noPlayersCloseForSpawning(chunkPos), optional2.map(levelChunk -> Stream.of(levelChunk.getEntitySections()).mapToInt(ClassInstanceMultiMap::size).sum()).orElse(0), optional2.map(levelChunk -> levelChunk.getBlockEntities().size()).orElse(0));
        }
    }

    private static String printFuture(CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> completableFuture) {
        try {
            Either either = completableFuture.getNow(null);
            if (either != null) {
                return (String)either.map(levelChunk -> "done", chunkLoadingFailure -> "unloaded");
            }
            return "not completed";
        }
        catch (CompletionException completionException) {
            return "failed " + completionException.getCause().getMessage();
        }
        catch (CancellationException cancellationException) {
            return "cancelled";
        }
    }

    @Nullable
    private CompoundTag readChunk(ChunkPos chunkPos) throws IOException {
        CompoundTag compoundTag = this.read(chunkPos);
        if (compoundTag == null) {
            return null;
        }
        return this.upgradeChunkTag(this.level.dimension(), this.overworldDataStorage, compoundTag);
    }

    boolean noPlayersCloseForSpawning(ChunkPos chunkPos) {
        long l = chunkPos.toLong();
        if (!this.distanceManager.hasPlayersNearby(l)) {
            return true;
        }
        return this.playerMap.getPlayers(l).noneMatch(serverPlayer -> !serverPlayer.isSpectator() && ChunkMap.euclideanDistanceSquared(chunkPos, serverPlayer) < 16384.0);
    }

    private boolean skipPlayer(ServerPlayer serverPlayer) {
        return serverPlayer.isSpectator() && !this.level.getGameRules().getBoolean(GameRules.RULE_SPECTATORSGENERATECHUNKS);
    }

    void updatePlayerStatus(ServerPlayer serverPlayer, boolean bl) {
        boolean bl2 = this.skipPlayer(serverPlayer);
        boolean bl3 = this.playerMap.ignoredOrUnknown(serverPlayer);
        int n = Mth.floor(serverPlayer.getX()) >> 4;
        int n2 = Mth.floor(serverPlayer.getZ()) >> 4;
        if (bl) {
            this.playerMap.addPlayer(ChunkPos.asLong(n, n2), serverPlayer, bl2);
            this.updatePlayerPos(serverPlayer);
            if (!bl2) {
                this.distanceManager.addPlayer(SectionPos.of(serverPlayer), serverPlayer);
            }
        } else {
            SectionPos sectionPos = serverPlayer.getLastSectionPos();
            this.playerMap.removePlayer(sectionPos.chunk().toLong(), serverPlayer);
            if (!bl3) {
                this.distanceManager.removePlayer(sectionPos, serverPlayer);
            }
        }
        for (int i = n - this.viewDistance; i <= n + this.viewDistance; ++i) {
            for (int j = n2 - this.viewDistance; j <= n2 + this.viewDistance; ++j) {
                ChunkPos chunkPos = new ChunkPos(i, j);
                this.updateChunkTracking(serverPlayer, chunkPos, new Packet[2], !bl, bl);
            }
        }
    }

    private SectionPos updatePlayerPos(ServerPlayer serverPlayer) {
        SectionPos sectionPos = SectionPos.of(serverPlayer);
        serverPlayer.setLastSectionPos(sectionPos);
        serverPlayer.connection.send(new ClientboundSetChunkCacheCenterPacket(sectionPos.x(), sectionPos.z()));
        return sectionPos;
    }

    public void move(ServerPlayer serverPlayer) {
        boolean bl;
        for (TrackedEntity trackedEntity : this.entityMap.values()) {
            if (trackedEntity.entity == serverPlayer) {
                trackedEntity.updatePlayers(this.level.players());
                continue;
            }
            trackedEntity.updatePlayer(serverPlayer);
        }
        int n = Mth.floor(serverPlayer.getX()) >> 4;
        int n2 = Mth.floor(serverPlayer.getZ()) >> 4;
        SectionPos sectionPos = serverPlayer.getLastSectionPos();
        SectionPos sectionPos2 = SectionPos.of(serverPlayer);
        long l = sectionPos.chunk().toLong();
        long l2 = sectionPos2.chunk().toLong();
        boolean bl2 = this.playerMap.ignored(serverPlayer);
        boolean bl3 = this.skipPlayer(serverPlayer);
        boolean bl4 = bl = sectionPos.asLong() != sectionPos2.asLong();
        if (bl || bl2 != bl3) {
            this.updatePlayerPos(serverPlayer);
            if (!bl2) {
                this.distanceManager.removePlayer(sectionPos, serverPlayer);
            }
            if (!bl3) {
                this.distanceManager.addPlayer(sectionPos2, serverPlayer);
            }
            if (!bl2 && bl3) {
                this.playerMap.ignorePlayer(serverPlayer);
            }
            if (bl2 && !bl3) {
                this.playerMap.unIgnorePlayer(serverPlayer);
            }
            if (l != l2) {
                this.playerMap.updatePlayer(l, l2, serverPlayer);
            }
        }
        int n3 = sectionPos.x();
        int n4 = sectionPos.z();
        if (Math.abs(n3 - n) <= this.viewDistance * 2 && Math.abs(n4 - n2) <= this.viewDistance * 2) {
            int n5 = Math.min(n, n3) - this.viewDistance;
            int n6 = Math.min(n2, n4) - this.viewDistance;
            int n7 = Math.max(n, n3) + this.viewDistance;
            int n8 = Math.max(n2, n4) + this.viewDistance;
            for (int i = n5; i <= n7; ++i) {
                for (int j = n6; j <= n8; ++j) {
                    ChunkPos chunkPos = new ChunkPos(i, j);
                    boolean bl5 = ChunkMap.checkerboardDistance(chunkPos, n3, n4) <= this.viewDistance;
                    boolean bl6 = ChunkMap.checkerboardDistance(chunkPos, n, n2) <= this.viewDistance;
                    this.updateChunkTracking(serverPlayer, chunkPos, new Packet[2], bl5, bl6);
                }
            }
        } else {
            int n9;
            boolean bl7;
            ChunkPos chunkPos;
            int n10;
            boolean bl8;
            for (n9 = n3 - this.viewDistance; n9 <= n3 + this.viewDistance; ++n9) {
                for (n10 = n4 - this.viewDistance; n10 <= n4 + this.viewDistance; ++n10) {
                    chunkPos = new ChunkPos(n9, n10);
                    bl8 = true;
                    bl7 = false;
                    this.updateChunkTracking(serverPlayer, chunkPos, new Packet[2], true, false);
                }
            }
            for (n9 = n - this.viewDistance; n9 <= n + this.viewDistance; ++n9) {
                for (n10 = n2 - this.viewDistance; n10 <= n2 + this.viewDistance; ++n10) {
                    chunkPos = new ChunkPos(n9, n10);
                    bl8 = false;
                    bl7 = true;
                    this.updateChunkTracking(serverPlayer, chunkPos, new Packet[2], false, true);
                }
            }
        }
    }

    @Override
    public Stream<ServerPlayer> getPlayers(ChunkPos chunkPos, boolean bl) {
        return this.playerMap.getPlayers(chunkPos.toLong()).filter(serverPlayer -> {
            int n = ChunkMap.checkerboardDistance(chunkPos, serverPlayer, true);
            if (n > this.viewDistance) {
                return false;
            }
            return !bl || n == this.viewDistance;
        });
    }

    protected void addEntity(Entity entity) {
        if (entity instanceof EnderDragonPart) {
            return;
        }
        EntityType<?> entityType = entity.getType();
        int n = entityType.clientTrackingRange() * 16;
        int n2 = entityType.updateInterval();
        if (this.entityMap.containsKey(entity.getId())) {
            throw Util.pauseInIde(new IllegalStateException("Entity is already tracked!"));
        }
        TrackedEntity trackedEntity = new TrackedEntity(entity, n, n2, entityType.trackDeltas());
        this.entityMap.put(entity.getId(), (Object)trackedEntity);
        trackedEntity.updatePlayers(this.level.players());
        if (entity instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)entity;
            this.updatePlayerStatus(serverPlayer, true);
            for (TrackedEntity trackedEntity2 : this.entityMap.values()) {
                if (trackedEntity2.entity == serverPlayer) continue;
                trackedEntity2.updatePlayer(serverPlayer);
            }
        }
    }

    protected void removeEntity(Entity entity) {
        Object object;
        if (entity instanceof ServerPlayer) {
            object = (ServerPlayer)entity;
            this.updatePlayerStatus((ServerPlayer)object, false);
            for (TrackedEntity trackedEntity : this.entityMap.values()) {
                trackedEntity.removePlayer((ServerPlayer)object);
            }
        }
        if ((object = (TrackedEntity)this.entityMap.remove(entity.getId())) != null) {
            ((TrackedEntity)object).broadcastRemoved();
        }
    }

    protected void tick() {
        ArrayList arrayList = Lists.newArrayList();
        List<ServerPlayer> list = this.level.players();
        for (TrackedEntity trackedEntity : this.entityMap.values()) {
            SectionPos sectionPos;
            SectionPos sectionPos2 = trackedEntity.lastSectionPos;
            if (!Objects.equals(sectionPos2, sectionPos = SectionPos.of(trackedEntity.entity))) {
                trackedEntity.updatePlayers(list);
                Entity entity = trackedEntity.entity;
                if (entity instanceof ServerPlayer) {
                    arrayList.add((ServerPlayer)entity);
                }
                trackedEntity.lastSectionPos = sectionPos;
            }
            trackedEntity.serverEntity.sendChanges();
        }
        if (!arrayList.isEmpty()) {
            for (TrackedEntity trackedEntity : this.entityMap.values()) {
                trackedEntity.updatePlayers(arrayList);
            }
        }
    }

    protected void broadcast(Entity entity, Packet<?> packet) {
        TrackedEntity trackedEntity = (TrackedEntity)this.entityMap.get(entity.getId());
        if (trackedEntity != null) {
            trackedEntity.broadcast(packet);
        }
    }

    protected void broadcastAndSend(Entity entity, Packet<?> packet) {
        TrackedEntity trackedEntity = (TrackedEntity)this.entityMap.get(entity.getId());
        if (trackedEntity != null) {
            trackedEntity.broadcastAndSend(packet);
        }
    }

    private void playerLoadedChunk(ServerPlayer serverPlayer, Packet<?>[] arrpacket, LevelChunk levelChunk) {
        if (arrpacket[0] == null) {
            arrpacket[0] = new ClientboundLevelChunkPacket(levelChunk, 65535);
            arrpacket[1] = new ClientboundLightUpdatePacket(levelChunk.getPos(), this.lightEngine, true);
        }
        serverPlayer.trackChunk(levelChunk.getPos(), arrpacket[0], arrpacket[1]);
        DebugPackets.sendPoiPacketsForChunk(this.level, levelChunk.getPos());
        ArrayList arrayList = Lists.newArrayList();
        ArrayList arrayList2 = Lists.newArrayList();
        for (Object object : this.entityMap.values()) {
            Entity entity = ((TrackedEntity)object).entity;
            if (entity == serverPlayer || entity.xChunk != levelChunk.getPos().x || entity.zChunk != levelChunk.getPos().z) continue;
            ((TrackedEntity)object).updatePlayer(serverPlayer);
            if (entity instanceof Mob && ((Mob)entity).getLeashHolder() != null) {
                arrayList.add(entity);
            }
            if (entity.getPassengers().isEmpty()) continue;
            arrayList2.add(entity);
        }
        if (!arrayList.isEmpty()) {
            for (Object object : arrayList) {
                serverPlayer.connection.send(new ClientboundSetEntityLinkPacket((Entity)object, ((Mob)object).getLeashHolder()));
            }
        }
        if (!arrayList2.isEmpty()) {
            for (Object object : arrayList2) {
                serverPlayer.connection.send(new ClientboundSetPassengersPacket((Entity)object));
            }
        }
    }

    protected PoiManager getPoiManager() {
        return this.poiManager;
    }

    public CompletableFuture<Void> packTicks(LevelChunk levelChunk) {
        return this.mainThreadExecutor.submit(() -> levelChunk.packTicks(this.level));
    }

    class TrackedEntity {
        private final ServerEntity serverEntity;
        private final Entity entity;
        private final int range;
        private SectionPos lastSectionPos;
        private final Set<ServerPlayer> seenBy = Sets.newHashSet();

        public TrackedEntity(Entity entity, int n, int n2, boolean bl) {
            this.serverEntity = new ServerEntity(ChunkMap.this.level, entity, n2, bl, this::broadcast);
            this.entity = entity;
            this.range = n;
            this.lastSectionPos = SectionPos.of(entity);
        }

        public boolean equals(Object object) {
            if (object instanceof TrackedEntity) {
                return ((TrackedEntity)object).entity.getId() == this.entity.getId();
            }
            return false;
        }

        public int hashCode() {
            return this.entity.getId();
        }

        public void broadcast(Packet<?> packet) {
            for (ServerPlayer serverPlayer : this.seenBy) {
                serverPlayer.connection.send(packet);
            }
        }

        public void broadcastAndSend(Packet<?> packet) {
            this.broadcast(packet);
            if (this.entity instanceof ServerPlayer) {
                ((ServerPlayer)this.entity).connection.send(packet);
            }
        }

        public void broadcastRemoved() {
            for (ServerPlayer serverPlayer : this.seenBy) {
                this.serverEntity.removePairing(serverPlayer);
            }
        }

        public void removePlayer(ServerPlayer serverPlayer) {
            if (this.seenBy.remove(serverPlayer)) {
                this.serverEntity.removePairing(serverPlayer);
            }
        }

        public void updatePlayer(ServerPlayer serverPlayer) {
            boolean bl;
            if (serverPlayer == this.entity) {
                return;
            }
            Vec3 vec3 = serverPlayer.position().subtract(this.serverEntity.sentPos());
            int n = Math.min(this.getEffectiveRange(), (ChunkMap.this.viewDistance - 1) * 16);
            boolean bl2 = bl = vec3.x >= (double)(-n) && vec3.x <= (double)n && vec3.z >= (double)(-n) && vec3.z <= (double)n && this.entity.broadcastToPlayer(serverPlayer);
            if (bl) {
                ChunkPos chunkPos;
                ChunkHolder chunkHolder;
                boolean bl3 = this.entity.forcedLoading;
                if (!bl3 && (chunkHolder = ChunkMap.this.getVisibleChunkIfPresent((chunkPos = new ChunkPos(this.entity.xChunk, this.entity.zChunk)).toLong())) != null && chunkHolder.getTickingChunk() != null) {
                    boolean bl4 = bl3 = ChunkMap.checkerboardDistance(chunkPos, serverPlayer, false) <= ChunkMap.this.viewDistance;
                }
                if (bl3 && this.seenBy.add(serverPlayer)) {
                    this.serverEntity.addPairing(serverPlayer);
                }
            } else if (this.seenBy.remove(serverPlayer)) {
                this.serverEntity.removePairing(serverPlayer);
            }
        }

        private int scaledRange(int n) {
            return ChunkMap.this.level.getServer().getScaledTrackingDistance(n);
        }

        private int getEffectiveRange() {
            Collection<Entity> collection = this.entity.getIndirectPassengers();
            int n = this.range;
            for (Entity entity : collection) {
                int n2 = entity.getType().clientTrackingRange() * 16;
                if (n2 <= n) continue;
                n = n2;
            }
            return this.scaledRange(n);
        }

        public void updatePlayers(List<ServerPlayer> list) {
            for (ServerPlayer serverPlayer : list) {
                this.updatePlayer(serverPlayer);
            }
        }
    }

    class DistanceManager
    extends net.minecraft.server.level.DistanceManager {
        protected DistanceManager(Executor executor, Executor executor2) {
            super(executor, executor2);
        }

        @Override
        protected boolean isChunkToRemove(long l) {
            return ChunkMap.this.toDrop.contains(l);
        }

        @Nullable
        @Override
        protected ChunkHolder getChunk(long l) {
            return ChunkMap.this.getUpdatingChunkIfPresent(l);
        }

        @Nullable
        @Override
        protected ChunkHolder updateChunkScheduling(long l, int n, @Nullable ChunkHolder chunkHolder, int n2) {
            return ChunkMap.this.updateChunkScheduling(l, n, chunkHolder, n2);
        }
    }

}

