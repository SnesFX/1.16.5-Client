/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  it.unimi.dsi.fastutil.shorts.ShortArraySet
 *  it.unimi.dsi.fastutil.shorts.ShortIterator
 *  it.unimi.dsi.fastutil.shorts.ShortSet
 *  javax.annotation.Nullable
 */
package net.minecraft.server.level;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.shorts.ShortArraySet;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ChunkHolder {
    public static final Either<ChunkAccess, ChunkLoadingFailure> UNLOADED_CHUNK = Either.right((Object)ChunkLoadingFailure.UNLOADED);
    public static final CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_CHUNK);
    public static final Either<LevelChunk, ChunkLoadingFailure> UNLOADED_LEVEL_CHUNK = Either.right((Object)ChunkLoadingFailure.UNLOADED);
    private static final CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> UNLOADED_LEVEL_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_LEVEL_CHUNK);
    private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.getStatusList();
    private static final FullChunkStatus[] FULL_CHUNK_STATUSES = FullChunkStatus.values();
    private final AtomicReferenceArray<CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>>> futures = new AtomicReferenceArray(CHUNK_STATUSES.size());
    private volatile CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> fullChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
    private volatile CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> tickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
    private volatile CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> entityTickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
    private CompletableFuture<ChunkAccess> chunkToSave = CompletableFuture.completedFuture(null);
    private int oldTicketLevel;
    private int ticketLevel;
    private int queueLevel;
    private final ChunkPos pos;
    private boolean hasChangedSections;
    private final ShortSet[] changedBlocksPerSection = new ShortSet[16];
    private int blockChangedLightSectionFilter;
    private int skyChangedLightSectionFilter;
    private final LevelLightEngine lightEngine;
    private final LevelChangeListener onLevelChange;
    private final PlayerProvider playerProvider;
    private boolean wasAccessibleSinceLastSave;
    private boolean resendLight;

    public ChunkHolder(ChunkPos chunkPos, int n, LevelLightEngine levelLightEngine, LevelChangeListener levelChangeListener, PlayerProvider playerProvider) {
        this.pos = chunkPos;
        this.lightEngine = levelLightEngine;
        this.onLevelChange = levelChangeListener;
        this.playerProvider = playerProvider;
        this.ticketLevel = this.oldTicketLevel = ChunkMap.MAX_CHUNK_DISTANCE + 1;
        this.queueLevel = this.oldTicketLevel;
        this.setTicketLevel(n);
    }

    public CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> getFutureIfPresentUnchecked(ChunkStatus chunkStatus) {
        CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> completableFuture = this.futures.get(chunkStatus.getIndex());
        return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
    }

    public CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> getFutureIfPresent(ChunkStatus chunkStatus) {
        if (ChunkHolder.getStatus(this.ticketLevel).isOrAfter(chunkStatus)) {
            return this.getFutureIfPresentUnchecked(chunkStatus);
        }
        return UNLOADED_CHUNK_FUTURE;
    }

    public CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> getTickingChunkFuture() {
        return this.tickingChunkFuture;
    }

    public CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> getEntityTickingChunkFuture() {
        return this.entityTickingChunkFuture;
    }

    public CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> getFullChunkFuture() {
        return this.fullChunkFuture;
    }

    @Nullable
    public LevelChunk getTickingChunk() {
        CompletableFuture<Either<LevelChunk, ChunkLoadingFailure>> completableFuture = this.getTickingChunkFuture();
        Either either = completableFuture.getNow(null);
        if (either == null) {
            return null;
        }
        return either.left().orElse(null);
    }

    @Nullable
    public ChunkStatus getLastAvailableStatus() {
        for (int i = ChunkHolder.CHUNK_STATUSES.size() - 1; i >= 0; --i) {
            ChunkStatus chunkStatus = CHUNK_STATUSES.get(i);
            CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> completableFuture = this.getFutureIfPresentUnchecked(chunkStatus);
            if (!completableFuture.getNow(UNLOADED_CHUNK).left().isPresent()) continue;
            return chunkStatus;
        }
        return null;
    }

    @Nullable
    public ChunkAccess getLastAvailable() {
        for (int i = ChunkHolder.CHUNK_STATUSES.size() - 1; i >= 0; --i) {
            Optional optional;
            ChunkStatus chunkStatus = CHUNK_STATUSES.get(i);
            CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> completableFuture = this.getFutureIfPresentUnchecked(chunkStatus);
            if (completableFuture.isCompletedExceptionally() || !(optional = completableFuture.getNow(UNLOADED_CHUNK).left()).isPresent()) continue;
            return (ChunkAccess)optional.get();
        }
        return null;
    }

    public CompletableFuture<ChunkAccess> getChunkToSave() {
        return this.chunkToSave;
    }

    public void blockChanged(BlockPos blockPos) {
        LevelChunk levelChunk = this.getTickingChunk();
        if (levelChunk == null) {
            return;
        }
        byte by = (byte)SectionPos.blockToSectionCoord(blockPos.getY());
        if (this.changedBlocksPerSection[by] == null) {
            this.hasChangedSections = true;
            this.changedBlocksPerSection[by] = new ShortArraySet();
        }
        this.changedBlocksPerSection[by].add(SectionPos.sectionRelativePos(blockPos));
    }

    public void sectionLightChanged(LightLayer lightLayer, int n) {
        LevelChunk levelChunk = this.getTickingChunk();
        if (levelChunk == null) {
            return;
        }
        levelChunk.setUnsaved(true);
        if (lightLayer == LightLayer.SKY) {
            this.skyChangedLightSectionFilter |= 1 << n - -1;
        } else {
            this.blockChangedLightSectionFilter |= 1 << n - -1;
        }
    }

    public void broadcastChanges(LevelChunk levelChunk) {
        int n;
        if (!this.hasChangedSections && this.skyChangedLightSectionFilter == 0 && this.blockChangedLightSectionFilter == 0) {
            return;
        }
        Level level = levelChunk.getLevel();
        int n2 = 0;
        for (n = 0; n < this.changedBlocksPerSection.length; ++n) {
            n2 += this.changedBlocksPerSection[n] != null ? this.changedBlocksPerSection[n].size() : 0;
        }
        this.resendLight |= n2 >= 64;
        if (this.skyChangedLightSectionFilter != 0 || this.blockChangedLightSectionFilter != 0) {
            this.broadcast(new ClientboundLightUpdatePacket(levelChunk.getPos(), this.lightEngine, this.skyChangedLightSectionFilter, this.blockChangedLightSectionFilter, true), !this.resendLight);
            this.skyChangedLightSectionFilter = 0;
            this.blockChangedLightSectionFilter = 0;
        }
        for (n = 0; n < this.changedBlocksPerSection.length; ++n) {
            Object object;
            Object object2;
            ShortSet shortSet = this.changedBlocksPerSection[n];
            if (shortSet == null) continue;
            SectionPos sectionPos = SectionPos.of(levelChunk.getPos(), n);
            if (shortSet.size() == 1) {
                object = sectionPos.relativeToBlockPos(shortSet.iterator().nextShort());
                object2 = level.getBlockState((BlockPos)object);
                this.broadcast(new ClientboundBlockUpdatePacket((BlockPos)object, (BlockState)object2), false);
                this.broadcastBlockEntityIfNeeded(level, (BlockPos)object, (BlockState)object2);
            } else {
                object = levelChunk.getSections()[sectionPos.getY()];
                object2 = new ClientboundSectionBlocksUpdatePacket(sectionPos, shortSet, (LevelChunkSection)object, this.resendLight);
                this.broadcast((Packet<?>)object2, false);
                ((ClientboundSectionBlocksUpdatePacket)object2).runUpdates((blockPos, blockState) -> this.broadcastBlockEntityIfNeeded(level, (BlockPos)blockPos, (BlockState)blockState));
            }
            this.changedBlocksPerSection[n] = null;
        }
        this.hasChangedSections = false;
    }

    private void broadcastBlockEntityIfNeeded(Level level, BlockPos blockPos, BlockState blockState) {
        if (blockState.getBlock().isEntityBlock()) {
            this.broadcastBlockEntity(level, blockPos);
        }
    }

    private void broadcastBlockEntity(Level level, BlockPos blockPos) {
        ClientboundBlockEntityDataPacket clientboundBlockEntityDataPacket;
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity != null && (clientboundBlockEntityDataPacket = blockEntity.getUpdatePacket()) != null) {
            this.broadcast(clientboundBlockEntityDataPacket, false);
        }
    }

    private void broadcast(Packet<?> packet, boolean bl) {
        this.playerProvider.getPlayers(this.pos, bl).forEach(serverPlayer -> serverPlayer.connection.send(packet));
    }

    public CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> getOrScheduleFuture(ChunkStatus chunkStatus, ChunkMap chunkMap) {
        Object object;
        int n = chunkStatus.getIndex();
        CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> completableFuture = this.futures.get(n);
        if (completableFuture != null && ((object = (Either)completableFuture.getNow(null)) == null || object.left().isPresent())) {
            return completableFuture;
        }
        if (ChunkHolder.getStatus(this.ticketLevel).isOrAfter(chunkStatus)) {
            object = chunkMap.schedule(this, chunkStatus);
            this.updateChunkToSave((CompletableFuture<? extends Either<? extends ChunkAccess, ChunkLoadingFailure>>)object);
            this.futures.set(n, (CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>>)object);
            return object;
        }
        return completableFuture == null ? UNLOADED_CHUNK_FUTURE : completableFuture;
    }

    private void updateChunkToSave(CompletableFuture<? extends Either<? extends ChunkAccess, ChunkLoadingFailure>> completableFuture) {
        this.chunkToSave = this.chunkToSave.thenCombine(completableFuture, (chunkAccess2, either) -> (ChunkAccess)either.map(chunkAccess -> chunkAccess, chunkLoadingFailure -> chunkAccess2));
    }

    public FullChunkStatus getFullStatus() {
        return ChunkHolder.getFullChunkStatus(this.ticketLevel);
    }

    public ChunkPos getPos() {
        return this.pos;
    }

    public int getTicketLevel() {
        return this.ticketLevel;
    }

    public int getQueueLevel() {
        return this.queueLevel;
    }

    private void setQueueLevel(int n) {
        this.queueLevel = n;
    }

    public void setTicketLevel(int n) {
        this.ticketLevel = n;
    }

    protected void updateFutures(ChunkMap chunkMap) {
        CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> completableFuture;
        int n;
        ChunkStatus chunkStatus = ChunkHolder.getStatus(this.oldTicketLevel);
        ChunkStatus chunkStatus2 = ChunkHolder.getStatus(this.ticketLevel);
        boolean bl = this.oldTicketLevel <= ChunkMap.MAX_CHUNK_DISTANCE;
        boolean bl2 = this.ticketLevel <= ChunkMap.MAX_CHUNK_DISTANCE;
        FullChunkStatus fullChunkStatus = ChunkHolder.getFullChunkStatus(this.oldTicketLevel);
        FullChunkStatus fullChunkStatus2 = ChunkHolder.getFullChunkStatus(this.ticketLevel);
        if (bl) {
            Either either2 = Either.right((Object)new ChunkLoadingFailure(){

                public String toString() {
                    return "Unloaded ticket level " + ChunkHolder.this.pos.toString();
                }
            });
            int n2 = n = bl2 ? chunkStatus2.getIndex() + 1 : 0;
            while (n <= chunkStatus.getIndex()) {
                completableFuture = this.futures.get(n);
                if (completableFuture != null) {
                    completableFuture.complete((Either<ChunkAccess, ChunkLoadingFailure>)either2);
                } else {
                    this.futures.set(n, CompletableFuture.completedFuture(either2));
                }
                ++n;
            }
        }
        boolean bl3 = fullChunkStatus.isOrAfter(FullChunkStatus.BORDER);
        n = fullChunkStatus2.isOrAfter(FullChunkStatus.BORDER);
        this.wasAccessibleSinceLastSave |= n;
        if (!bl3 && n != 0) {
            this.fullChunkFuture = chunkMap.unpackTicks(this);
            this.updateChunkToSave(this.fullChunkFuture);
        }
        if (bl3 && n == 0) {
            completableFuture = this.fullChunkFuture;
            this.fullChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
            this.updateChunkToSave((CompletableFuture<? extends Either<? extends ChunkAccess, ChunkLoadingFailure>>)completableFuture.thenApply(either -> either.ifLeft(chunkMap::packTicks)));
        }
        boolean bl4 = fullChunkStatus.isOrAfter(FullChunkStatus.TICKING);
        boolean bl5 = fullChunkStatus2.isOrAfter(FullChunkStatus.TICKING);
        if (!bl4 && bl5) {
            this.tickingChunkFuture = chunkMap.postProcess(this);
            this.updateChunkToSave(this.tickingChunkFuture);
        }
        if (bl4 && !bl5) {
            this.tickingChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
            this.tickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
        }
        boolean bl6 = fullChunkStatus.isOrAfter(FullChunkStatus.ENTITY_TICKING);
        boolean bl7 = fullChunkStatus2.isOrAfter(FullChunkStatus.ENTITY_TICKING);
        if (!bl6 && bl7) {
            if (this.entityTickingChunkFuture != UNLOADED_LEVEL_CHUNK_FUTURE) {
                throw Util.pauseInIde(new IllegalStateException());
            }
            this.entityTickingChunkFuture = chunkMap.getEntityTickingRangeFuture(this.pos);
            this.updateChunkToSave(this.entityTickingChunkFuture);
        }
        if (bl6 && !bl7) {
            this.entityTickingChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
            this.entityTickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
        }
        this.onLevelChange.onLevelChange(this.pos, this::getQueueLevel, this.ticketLevel, this::setQueueLevel);
        this.oldTicketLevel = this.ticketLevel;
    }

    public static ChunkStatus getStatus(int n) {
        if (n < 33) {
            return ChunkStatus.FULL;
        }
        return ChunkStatus.getStatus(n - 33);
    }

    public static FullChunkStatus getFullChunkStatus(int n) {
        return FULL_CHUNK_STATUSES[Mth.clamp(33 - n + 1, 0, FULL_CHUNK_STATUSES.length - 1)];
    }

    public boolean wasAccessibleSinceLastSave() {
        return this.wasAccessibleSinceLastSave;
    }

    public void refreshAccessibility() {
        this.wasAccessibleSinceLastSave = ChunkHolder.getFullChunkStatus(this.ticketLevel).isOrAfter(FullChunkStatus.BORDER);
    }

    public void replaceProtoChunk(ImposterProtoChunk imposterProtoChunk) {
        for (int i = 0; i < this.futures.length(); ++i) {
            Optional optional;
            CompletableFuture<Either<ChunkAccess, ChunkLoadingFailure>> completableFuture = this.futures.get(i);
            if (completableFuture == null || !(optional = completableFuture.getNow(UNLOADED_CHUNK).left()).isPresent() || !(optional.get() instanceof ProtoChunk)) continue;
            this.futures.set(i, CompletableFuture.completedFuture(Either.left((Object)imposterProtoChunk)));
        }
        this.updateChunkToSave(CompletableFuture.completedFuture(Either.left((Object)imposterProtoChunk.getWrapped())));
    }

    public static interface PlayerProvider {
        public Stream<ServerPlayer> getPlayers(ChunkPos var1, boolean var2);
    }

    public static interface LevelChangeListener {
        public void onLevelChange(ChunkPos var1, IntSupplier var2, int var3, IntConsumer var4);
    }

    public static interface ChunkLoadingFailure {
        public static final ChunkLoadingFailure UNLOADED = new ChunkLoadingFailure(){

            public String toString() {
                return "UNLOADED";
            }
        };

    }

    public static enum FullChunkStatus {
        INACCESSIBLE,
        BORDER,
        TICKING,
        ENTITY_TICKING;
        

        public boolean isOrAfter(FullChunkStatus fullChunkStatus) {
            return this.ordinal() >= fullChunkStatus.ordinal();
        }
    }

}

