/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.chunk;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LevelLightEngine;

public abstract class ChunkSource
implements LightChunkGetter,
AutoCloseable {
    @Nullable
    public LevelChunk getChunk(int n, int n2, boolean bl) {
        return (LevelChunk)this.getChunk(n, n2, ChunkStatus.FULL, bl);
    }

    @Nullable
    public LevelChunk getChunkNow(int n, int n2) {
        return this.getChunk(n, n2, false);
    }

    @Nullable
    @Override
    public BlockGetter getChunkForLighting(int n, int n2) {
        return this.getChunk(n, n2, ChunkStatus.EMPTY, false);
    }

    public boolean hasChunk(int n, int n2) {
        return this.getChunk(n, n2, ChunkStatus.FULL, false) != null;
    }

    @Nullable
    public abstract ChunkAccess getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

    public abstract String gatherStats();

    @Override
    public void close() throws IOException {
    }

    public abstract LevelLightEngine getLightEngine();

    public void setSpawnSettings(boolean bl, boolean bl2) {
    }

    public void updateChunkForced(ChunkPos chunkPos, boolean bl) {
    }

    public boolean isEntityTickingChunk(Entity entity) {
        return true;
    }

    public boolean isEntityTickingChunk(ChunkPos chunkPos) {
        return true;
    }

    public boolean isTickingChunk(BlockPos blockPos) {
        return true;
    }
}

