/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.multiplayer;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientChunkCache
extends ChunkSource {
    private static final Logger LOGGER = LogManager.getLogger();
    private final LevelChunk emptyChunk;
    private final LevelLightEngine lightEngine;
    private volatile Storage storage;
    private final ClientLevel level;

    public ClientChunkCache(ClientLevel clientLevel, int n) {
        this.level = clientLevel;
        this.emptyChunk = new EmptyLevelChunk(clientLevel, new ChunkPos(0, 0));
        this.lightEngine = new LevelLightEngine(this, true, clientLevel.dimensionType().hasSkyLight());
        this.storage = new Storage(ClientChunkCache.calculateStorageRange(n));
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return this.lightEngine;
    }

    private static boolean isValidChunk(@Nullable LevelChunk levelChunk, int n, int n2) {
        if (levelChunk == null) {
            return false;
        }
        ChunkPos chunkPos = levelChunk.getPos();
        return chunkPos.x == n && chunkPos.z == n2;
    }

    public void drop(int n, int n2) {
        if (!this.storage.inRange(n, n2)) {
            return;
        }
        int n3 = this.storage.getIndex(n, n2);
        LevelChunk levelChunk = this.storage.getChunk(n3);
        if (ClientChunkCache.isValidChunk(levelChunk, n, n2)) {
            this.storage.replace(n3, levelChunk, null);
        }
    }

    @Nullable
    @Override
    public LevelChunk getChunk(int n, int n2, ChunkStatus chunkStatus, boolean bl) {
        LevelChunk levelChunk;
        if (this.storage.inRange(n, n2) && ClientChunkCache.isValidChunk(levelChunk = this.storage.getChunk(this.storage.getIndex(n, n2)), n, n2)) {
            return levelChunk;
        }
        if (bl) {
            return this.emptyChunk;
        }
        return null;
    }

    @Override
    public BlockGetter getLevel() {
        return this.level;
    }

    @Nullable
    public LevelChunk replaceWithPacketData(int n, int n2, @Nullable ChunkBiomeContainer chunkBiomeContainer, FriendlyByteBuf friendlyByteBuf, CompoundTag compoundTag, int n3, boolean bl) {
        if (!this.storage.inRange(n, n2)) {
            LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", (Object)n, (Object)n2);
            return null;
        }
        int n4 = this.storage.getIndex(n, n2);
        LevelChunk levelChunk = (LevelChunk)this.storage.chunks.get(n4);
        if (bl || !ClientChunkCache.isValidChunk(levelChunk, n, n2)) {
            if (chunkBiomeContainer == null) {
                LOGGER.warn("Ignoring chunk since we don't have complete data: {}, {}", (Object)n, (Object)n2);
                return null;
            }
            levelChunk = new LevelChunk(this.level, new ChunkPos(n, n2), chunkBiomeContainer);
            levelChunk.replaceWithPacketData(chunkBiomeContainer, friendlyByteBuf, compoundTag, n3);
            this.storage.replace(n4, levelChunk);
        } else {
            levelChunk.replaceWithPacketData(chunkBiomeContainer, friendlyByteBuf, compoundTag, n3);
        }
        LevelChunkSection[] arrlevelChunkSection = levelChunk.getSections();
        LevelLightEngine levelLightEngine = this.getLightEngine();
        levelLightEngine.enableLightSources(new ChunkPos(n, n2), true);
        for (int i = 0; i < arrlevelChunkSection.length; ++i) {
            LevelChunkSection levelChunkSection = arrlevelChunkSection[i];
            levelLightEngine.updateSectionStatus(SectionPos.of(n, i, n2), LevelChunkSection.isEmpty(levelChunkSection));
        }
        this.level.onChunkLoaded(n, n2);
        return levelChunk;
    }

    public void tick(BooleanSupplier booleanSupplier) {
    }

    public void updateViewCenter(int n, int n2) {
        this.storage.viewCenterX = n;
        this.storage.viewCenterZ = n2;
    }

    public void updateViewRadius(int n) {
        int n2;
        int n3 = this.storage.chunkRadius;
        if (n3 != (n2 = ClientChunkCache.calculateStorageRange(n))) {
            Storage storage = new Storage(n2);
            storage.viewCenterX = this.storage.viewCenterX;
            storage.viewCenterZ = this.storage.viewCenterZ;
            for (int i = 0; i < this.storage.chunks.length(); ++i) {
                LevelChunk levelChunk = (LevelChunk)this.storage.chunks.get(i);
                if (levelChunk == null) continue;
                ChunkPos chunkPos = levelChunk.getPos();
                if (!storage.inRange(chunkPos.x, chunkPos.z)) continue;
                storage.replace(storage.getIndex(chunkPos.x, chunkPos.z), levelChunk);
            }
            this.storage = storage;
        }
    }

    private static int calculateStorageRange(int n) {
        return Math.max(2, n) + 3;
    }

    @Override
    public String gatherStats() {
        return "Client Chunk Cache: " + this.storage.chunks.length() + ", " + this.getLoadedChunksCount();
    }

    public int getLoadedChunksCount() {
        return this.storage.chunkCount;
    }

    @Override
    public void onLightUpdate(LightLayer lightLayer, SectionPos sectionPos) {
        Minecraft.getInstance().levelRenderer.setSectionDirty(sectionPos.x(), sectionPos.y(), sectionPos.z());
    }

    @Override
    public boolean isTickingChunk(BlockPos blockPos) {
        return this.hasChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
    }

    @Override
    public boolean isEntityTickingChunk(ChunkPos chunkPos) {
        return this.hasChunk(chunkPos.x, chunkPos.z);
    }

    @Override
    public boolean isEntityTickingChunk(Entity entity) {
        return this.hasChunk(Mth.floor(entity.getX()) >> 4, Mth.floor(entity.getZ()) >> 4);
    }

    @Nullable
    @Override
    public /* synthetic */ ChunkAccess getChunk(int n, int n2, ChunkStatus chunkStatus, boolean bl) {
        return this.getChunk(n, n2, chunkStatus, bl);
    }

    final class Storage {
        private final AtomicReferenceArray<LevelChunk> chunks;
        private final int chunkRadius;
        private final int viewRange;
        private volatile int viewCenterX;
        private volatile int viewCenterZ;
        private int chunkCount;

        private Storage(int n) {
            this.chunkRadius = n;
            this.viewRange = n * 2 + 1;
            this.chunks = new AtomicReferenceArray(this.viewRange * this.viewRange);
        }

        private int getIndex(int n, int n2) {
            return Math.floorMod(n2, this.viewRange) * this.viewRange + Math.floorMod(n, this.viewRange);
        }

        protected void replace(int n, @Nullable LevelChunk levelChunk) {
            LevelChunk levelChunk2 = this.chunks.getAndSet(n, levelChunk);
            if (levelChunk2 != null) {
                --this.chunkCount;
                ClientChunkCache.this.level.unload(levelChunk2);
            }
            if (levelChunk != null) {
                ++this.chunkCount;
            }
        }

        protected LevelChunk replace(int n, LevelChunk levelChunk, @Nullable LevelChunk levelChunk2) {
            if (this.chunks.compareAndSet(n, levelChunk, levelChunk2) && levelChunk2 == null) {
                --this.chunkCount;
            }
            ClientChunkCache.this.level.unload(levelChunk);
            return levelChunk;
        }

        private boolean inRange(int n, int n2) {
            return Math.abs(n - this.viewCenterX) <= this.chunkRadius && Math.abs(n2 - this.viewCenterZ) <= this.chunkRadius;
        }

        @Nullable
        protected LevelChunk getChunk(int n) {
            return this.chunks.get(n);
        }
    }

}

