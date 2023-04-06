/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.util.Either
 *  javax.annotation.Nullable
 */
package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorageSource;

public class ServerChunkCache
extends ChunkSource {
    private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.getStatusList();
    private final DistanceManager distanceManager;
    private final ChunkGenerator generator;
    private final ServerLevel level;
    private final Thread mainThread;
    private final ThreadedLevelLightEngine lightEngine;
    private final MainThreadExecutor mainThreadProcessor;
    public final ChunkMap chunkMap;
    private final DimensionDataStorage dataStorage;
    private long lastInhabitedUpdate;
    private boolean spawnEnemies = true;
    private boolean spawnFriendlies = true;
    private final long[] lastChunkPos = new long[4];
    private final ChunkStatus[] lastChunkStatus = new ChunkStatus[4];
    private final ChunkAccess[] lastChunk = new ChunkAccess[4];
    @Nullable
    private NaturalSpawner.SpawnState lastSpawnState;

    public ServerChunkCache(ServerLevel serverLevel, LevelStorageSource.LevelStorageAccess levelStorageAccess, DataFixer dataFixer, StructureManager structureManager, Executor executor, ChunkGenerator chunkGenerator, int n, boolean bl, ChunkProgressListener chunkProgressListener, Supplier<DimensionDataStorage> supplier) {
        this.level = serverLevel;
        this.mainThreadProcessor = new MainThreadExecutor(serverLevel);
        this.generator = chunkGenerator;
        this.mainThread = Thread.currentThread();
        File file = levelStorageAccess.getDimensionPath(serverLevel.dimension());
        File file2 = new File(file, "data");
        file2.mkdirs();
        this.dataStorage = new DimensionDataStorage(file2, dataFixer);
        this.chunkMap = new ChunkMap(serverLevel, levelStorageAccess, dataFixer, structureManager, executor, this.mainThreadProcessor, this, this.getGenerator(), chunkProgressListener, supplier, n, bl);
        this.lightEngine = this.chunkMap.getLightEngine();
        this.distanceManager = this.chunkMap.getDistanceManager();
        this.clearCache();
    }

    @Override
    public ThreadedLevelLightEngine getLightEngine() {
        return this.lightEngine;
    }

    @Nullable
    private ChunkHolder getVisibleChunkIfPresent(long l) {
        return this.chunkMap.getVisibleChunkIfPresent(l);
    }

    public int getTickingGenerated() {
        return this.chunkMap.getTickingGenerated();
    }

    private void storeInCache(long l, ChunkAccess chunkAccess, ChunkStatus chunkStatus) {
        for (int i = 3; i > 0; --i) {
            this.lastChunkPos[i] = this.lastChunkPos[i - 1];
            this.lastChunkStatus[i] = this.lastChunkStatus[i - 1];
            this.lastChunk[i] = this.lastChunk[i - 1];
        }
        this.lastChunkPos[0] = l;
        this.lastChunkStatus[0] = chunkStatus;
        this.lastChunk[0] = chunkAccess;
    }

    @Nullable
    @Override
    public ChunkAccess getChunk(int n, int n2, ChunkStatus chunkStatus, boolean bl) {
        ChunkAccess chunkAccess2;
        if (Thread.currentThread() != this.mainThread) {
            return CompletableFuture.supplyAsync(() -> this.getChunk(n, n2, chunkStatus, bl), this.mainThreadProcessor).join();
        }
        ProfilerFiller profilerFiller = this.level.getProfiler();
        profilerFiller.incrementCounter("getChunk");
        long l = ChunkPos.asLong(n, n2);
        for (int i = 0; i < 4; ++i) {
            if (l != this.lastChunkPos[i] || chunkStatus != this.lastChunkStatus[i] || (chunkAccess2 = this.lastChunk[i]) == null && bl) continue;
            return chunkAccess2;
        }
        profilerFiller.incrementCounter("getChunkCacheMiss");
        CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completableFuture = this.getChunkFutureMainThread(n, n2, chunkStatus, bl);
        this.mainThreadProcessor.managedBlock(completableFuture::isDone);
        chunkAccess2 = (ChunkAccess)completableFuture.join().map(chunkAccess -> chunkAccess, chunkLoadingFailure -> {
            if (bl) {
                throw Util.pauseInIde(new IllegalStateException("Chunk not there when requested: " + chunkLoadingFailure));
            }
            return null;
        });
        this.storeInCache(l, chunkAccess2, chunkStatus);
        return chunkAccess2;
    }

    @Nullable
    @Override
    public LevelChunk getChunkNow(int n, int n2) {
        if (Thread.currentThread() != this.mainThread) {
            return null;
        }
        this.level.getProfiler().incrementCounter("getChunkNow");
        long l = ChunkPos.asLong(n, n2);
        for (int i = 0; i < 4; ++i) {
            if (l != this.lastChunkPos[i] || this.lastChunkStatus[i] != ChunkStatus.FULL) continue;
            ChunkAccess chunkAccess = this.lastChunk[i];
            return chunkAccess instanceof LevelChunk ? (LevelChunk)chunkAccess : null;
        }
        ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(l);
        if (chunkHolder == null) {
            return null;
        }
        Either either = chunkHolder.getFutureIfPresent(ChunkStatus.FULL).getNow(null);
        if (either == null) {
            return null;
        }
        ChunkAccess chunkAccess = either.left().orElse(null);
        if (chunkAccess != null) {
            this.storeInCache(l, chunkAccess, ChunkStatus.FULL);
            if (chunkAccess instanceof LevelChunk) {
                return (LevelChunk)chunkAccess;
            }
        }
        return null;
    }

    private void clearCache() {
        Arrays.fill(this.lastChunkPos, ChunkPos.INVALID_CHUNK_POS);
        Arrays.fill(this.lastChunkStatus, null);
        Arrays.fill(this.lastChunk, null);
    }

    public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getChunkFuture(int n, int n2, ChunkStatus chunkStatus, boolean bl) {
        CompletionStage<Object> completionStage;
        boolean bl2;
        boolean bl3 = bl2 = Thread.currentThread() == this.mainThread;
        if (bl2) {
            completionStage = this.getChunkFutureMainThread(n, n2, chunkStatus, bl);
            this.mainThreadProcessor.managedBlock(completionStage::isDone);
        } else {
            completionStage = CompletableFuture.supplyAsync(() -> this.getChunkFutureMainThread(n, n2, chunkStatus, bl), this.mainThreadProcessor).thenCompose(completableFuture -> completableFuture);
        }
        return completionStage;
    }

    private CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getChunkFutureMainThread(int n, int n2, ChunkStatus chunkStatus, boolean bl) {
        ChunkPos chunkPos = new ChunkPos(n, n2);
        long l = chunkPos.toLong();
        int n3 = 33 + ChunkStatus.getDistance(chunkStatus);
        ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(l);
        if (bl) {
            this.distanceManager.addTicket(TicketType.UNKNOWN, chunkPos, n3, chunkPos);
            if (this.chunkAbsent(chunkHolder, n3)) {
                ProfilerFiller profilerFiller = this.level.getProfiler();
                profilerFiller.push("chunkLoad");
                this.runDistanceManagerUpdates();
                chunkHolder = this.getVisibleChunkIfPresent(l);
                profilerFiller.pop();
                if (this.chunkAbsent(chunkHolder, n3)) {
                    throw Util.pauseInIde(new IllegalStateException("No chunk holder after ticket has been added"));
                }
            }
        }
        if (this.chunkAbsent(chunkHolder, n3)) {
            return ChunkHolder.UNLOADED_CHUNK_FUTURE;
        }
        return chunkHolder.getOrScheduleFuture(chunkStatus, this.chunkMap);
    }

    private boolean chunkAbsent(@Nullable ChunkHolder chunkHolder, int n) {
        return chunkHolder == null || chunkHolder.getTicketLevel() > n;
    }

    @Override
    public boolean hasChunk(int n, int n2) {
        int n3;
        ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(new ChunkPos(n, n2).toLong());
        return !this.chunkAbsent(chunkHolder, n3 = 33 + ChunkStatus.getDistance(ChunkStatus.FULL));
    }

    @Override
    public BlockGetter getChunkForLighting(int n, int n2) {
        long l = ChunkPos.asLong(n, n2);
        ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(l);
        if (chunkHolder == null) {
            return null;
        }
        int n3 = CHUNK_STATUSES.size() - 1;
        do {
            ChunkStatus chunkStatus;
            Optional optional;
            if ((optional = chunkHolder.getFutureIfPresentUnchecked(chunkStatus = CHUNK_STATUSES.get(n3)).getNow(ChunkHolder.UNLOADED_CHUNK).left()).isPresent()) {
                return (BlockGetter)optional.get();
            }
            if (chunkStatus == ChunkStatus.LIGHT.getParent()) break;
            --n3;
        } while (true);
        return null;
    }

    @Override
    public Level getLevel() {
        return this.level;
    }

    public boolean pollTask() {
        return this.mainThreadProcessor.pollTask();
    }

    private boolean runDistanceManagerUpdates() {
        boolean bl = this.distanceManager.runAllUpdates(this.chunkMap);
        boolean bl2 = this.chunkMap.promoteChunkMap();
        if (bl || bl2) {
            this.clearCache();
            return true;
        }
        return false;
    }

    @Override
    public boolean isEntityTickingChunk(Entity entity) {
        long l = ChunkPos.asLong(Mth.floor(entity.getX()) >> 4, Mth.floor(entity.getZ()) >> 4);
        return this.checkChunkFuture(l, ChunkHolder::getEntityTickingChunkFuture);
    }

    @Override
    public boolean isEntityTickingChunk(ChunkPos chunkPos) {
        return this.checkChunkFuture(chunkPos.toLong(), ChunkHolder::getEntityTickingChunkFuture);
    }

    @Override
    public boolean isTickingChunk(BlockPos blockPos) {
        long l = ChunkPos.asLong(blockPos.getX() >> 4, blockPos.getZ() >> 4);
        return this.checkChunkFuture(l, ChunkHolder::getTickingChunkFuture);
    }

    private boolean checkChunkFuture(long l, Function<ChunkHolder, CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>>> function) {
        ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(l);
        if (chunkHolder == null) {
            return false;
        }
        Either<LevelChunk, ChunkHolder.ChunkLoadingFailure> either = function.apply(chunkHolder).getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK);
        return either.left().isPresent();
    }

    public void save(boolean bl) {
        this.runDistanceManagerUpdates();
        this.chunkMap.saveAllChunks(bl);
    }

    @Override
    public void close() throws IOException {
        this.save(true);
        this.lightEngine.close();
        this.chunkMap.close();
    }

    public void tick(BooleanSupplier booleanSupplier) {
        this.level.getProfiler().push("purge");
        this.distanceManager.purgeStaleTickets();
        this.runDistanceManagerUpdates();
        this.level.getProfiler().popPush("chunks");
        this.tickChunks();
        this.level.getProfiler().popPush("unload");
        this.chunkMap.tick(booleanSupplier);
        this.level.getProfiler().pop();
        this.clearCache();
    }

    private void tickChunks() {
        long l = this.level.getGameTime();
        long l2 = l - this.lastInhabitedUpdate;
        this.lastInhabitedUpdate = l;
        LevelData levelData = this.level.getLevelData();
        boolean bl = this.level.isDebug();
        boolean bl2 = this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
        if (!bl) {
            NaturalSpawner.SpawnState spawnState;
            this.level.getProfiler().push("pollingChunks");
            int n = this.level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
            boolean bl3 = levelData.getGameTime() % 400L == 0L;
            this.level.getProfiler().push("naturalSpawnCount");
            int n2 = this.distanceManager.getNaturalSpawnChunkCount();
            this.lastSpawnState = spawnState = NaturalSpawner.createState(n2, this.level.getAllEntities(), (arg_0, arg_1) -> this.getFullChunk(arg_0, arg_1));
            this.level.getProfiler().pop();
            ArrayList arrayList = Lists.newArrayList(this.chunkMap.getChunks());
            Collections.shuffle(arrayList);
            arrayList.forEach(chunkHolder -> {
                Optional optional = chunkHolder.getTickingChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).left();
                if (!optional.isPresent()) {
                    return;
                }
                this.level.getProfiler().push("broadcast");
                chunkHolder.broadcastChanges((LevelChunk)optional.get());
                this.level.getProfiler().pop();
                Optional optional2 = chunkHolder.getEntityTickingChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).left();
                if (!optional2.isPresent()) {
                    return;
                }
                LevelChunk levelChunk = (LevelChunk)optional2.get();
                ChunkPos chunkPos = chunkHolder.getPos();
                if (this.chunkMap.noPlayersCloseForSpawning(chunkPos)) {
                    return;
                }
                levelChunk.setInhabitedTime(levelChunk.getInhabitedTime() + l2);
                if (bl2 && (this.spawnEnemies || this.spawnFriendlies) && this.level.getWorldBorder().isWithinBounds(levelChunk.getPos())) {
                    NaturalSpawner.spawnForChunk(this.level, levelChunk, spawnState, this.spawnFriendlies, this.spawnEnemies, bl3);
                }
                this.level.tickChunk(levelChunk, n);
            });
            this.level.getProfiler().push("customSpawners");
            if (bl2) {
                this.level.tickCustomSpawners(this.spawnEnemies, this.spawnFriendlies);
            }
            this.level.getProfiler().pop();
            this.level.getProfiler().pop();
        }
        this.chunkMap.tick();
    }

    private void getFullChunk(long l, Consumer<LevelChunk> consumer) {
        ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(l);
        if (chunkHolder != null) {
            chunkHolder.getFullChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).left().ifPresent(consumer);
        }
    }

    @Override
    public String gatherStats() {
        return "ServerChunkCache: " + this.getLoadedChunksCount();
    }

    @VisibleForTesting
    public int getPendingTasksCount() {
        return this.mainThreadProcessor.getPendingTasksCount();
    }

    public ChunkGenerator getGenerator() {
        return this.generator;
    }

    public int getLoadedChunksCount() {
        return this.chunkMap.size();
    }

    public void blockChanged(BlockPos blockPos) {
        int n;
        int n2 = blockPos.getX() >> 4;
        ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(ChunkPos.asLong(n2, n = blockPos.getZ() >> 4));
        if (chunkHolder != null) {
            chunkHolder.blockChanged(blockPos);
        }
    }

    @Override
    public void onLightUpdate(LightLayer lightLayer, SectionPos sectionPos) {
        this.mainThreadProcessor.execute(() -> {
            ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(sectionPos.chunk().toLong());
            if (chunkHolder != null) {
                chunkHolder.sectionLightChanged(lightLayer, sectionPos.y());
            }
        });
    }

    public <T> void addRegionTicket(TicketType<T> ticketType, ChunkPos chunkPos, int n, T t) {
        this.distanceManager.addRegionTicket(ticketType, chunkPos, n, t);
    }

    public <T> void removeRegionTicket(TicketType<T> ticketType, ChunkPos chunkPos, int n, T t) {
        this.distanceManager.removeRegionTicket(ticketType, chunkPos, n, t);
    }

    @Override
    public void updateChunkForced(ChunkPos chunkPos, boolean bl) {
        this.distanceManager.updateChunkForced(chunkPos, bl);
    }

    public void move(ServerPlayer serverPlayer) {
        this.chunkMap.move(serverPlayer);
    }

    public void removeEntity(Entity entity) {
        this.chunkMap.removeEntity(entity);
    }

    public void addEntity(Entity entity) {
        this.chunkMap.addEntity(entity);
    }

    public void broadcastAndSend(Entity entity, Packet<?> packet) {
        this.chunkMap.broadcastAndSend(entity, packet);
    }

    public void broadcast(Entity entity, Packet<?> packet) {
        this.chunkMap.broadcast(entity, packet);
    }

    public void setViewDistance(int n) {
        this.chunkMap.setViewDistance(n);
    }

    @Override
    public void setSpawnSettings(boolean bl, boolean bl2) {
        this.spawnEnemies = bl;
        this.spawnFriendlies = bl2;
    }

    public String getChunkDebugData(ChunkPos chunkPos) {
        return this.chunkMap.getChunkDebugData(chunkPos);
    }

    public DimensionDataStorage getDataStorage() {
        return this.dataStorage;
    }

    public PoiManager getPoiManager() {
        return this.chunkMap.getPoiManager();
    }

    @Nullable
    public NaturalSpawner.SpawnState getLastSpawnState() {
        return this.lastSpawnState;
    }

    @Override
    public /* synthetic */ LevelLightEngine getLightEngine() {
        return this.getLightEngine();
    }

    @Override
    public /* synthetic */ BlockGetter getLevel() {
        return this.getLevel();
    }

    final class MainThreadExecutor
    extends BlockableEventLoop<Runnable> {
        private MainThreadExecutor(Level level) {
            super("Chunk source main thread executor for " + level.dimension().location());
        }

        @Override
        protected Runnable wrapRunnable(Runnable runnable) {
            return runnable;
        }

        @Override
        protected boolean shouldRun(Runnable runnable) {
            return true;
        }

        @Override
        protected boolean scheduleExecutables() {
            return true;
        }

        @Override
        protected Thread getRunningThread() {
            return ServerChunkCache.this.mainThread;
        }

        @Override
        protected void doRunTask(Runnable runnable) {
            ServerChunkCache.this.level.getProfiler().incrementCounter("runTask");
            super.doRunTask(runnable);
        }

        @Override
        protected boolean pollTask() {
            if (ServerChunkCache.this.runDistanceManagerUpdates()) {
                return true;
            }
            ServerChunkCache.this.lightEngine.tryScheduleUpdate();
            return super.pollTask();
        }
    }

}

