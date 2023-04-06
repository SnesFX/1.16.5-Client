/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class ViewArea {
    protected final LevelRenderer levelRenderer;
    protected final Level level;
    protected int chunkGridSizeY;
    protected int chunkGridSizeX;
    protected int chunkGridSizeZ;
    public ChunkRenderDispatcher.RenderChunk[] chunks;

    public ViewArea(ChunkRenderDispatcher chunkRenderDispatcher, Level level, int n, LevelRenderer levelRenderer) {
        this.levelRenderer = levelRenderer;
        this.level = level;
        this.setViewDistance(n);
        this.createChunks(chunkRenderDispatcher);
    }

    protected void createChunks(ChunkRenderDispatcher chunkRenderDispatcher) {
        int n = this.chunkGridSizeX * this.chunkGridSizeY * this.chunkGridSizeZ;
        this.chunks = new ChunkRenderDispatcher.RenderChunk[n];
        for (int i = 0; i < this.chunkGridSizeX; ++i) {
            for (int j = 0; j < this.chunkGridSizeY; ++j) {
                for (int k = 0; k < this.chunkGridSizeZ; ++k) {
                    int n2 = this.getChunkIndex(i, j, k);
                    this.chunks[n2] = new ChunkRenderDispatcher.RenderChunk(chunkRenderDispatcher);
                    this.chunks[n2].setOrigin(i * 16, j * 16, k * 16);
                }
            }
        }
    }

    public void releaseAllBuffers() {
        for (ChunkRenderDispatcher.RenderChunk renderChunk : this.chunks) {
            renderChunk.releaseBuffers();
        }
    }

    private int getChunkIndex(int n, int n2, int n3) {
        return (n3 * this.chunkGridSizeY + n2) * this.chunkGridSizeX + n;
    }

    protected void setViewDistance(int n) {
        int n2;
        this.chunkGridSizeX = n2 = n * 2 + 1;
        this.chunkGridSizeY = 16;
        this.chunkGridSizeZ = n2;
    }

    public void repositionCamera(double d, double d2) {
        int n = Mth.floor(d);
        int n2 = Mth.floor(d2);
        for (int i = 0; i < this.chunkGridSizeX; ++i) {
            int n3 = this.chunkGridSizeX * 16;
            int n4 = n - 8 - n3 / 2;
            int n5 = n4 + Math.floorMod(i * 16 - n4, n3);
            for (int j = 0; j < this.chunkGridSizeZ; ++j) {
                int n6 = this.chunkGridSizeZ * 16;
                int n7 = n2 - 8 - n6 / 2;
                int n8 = n7 + Math.floorMod(j * 16 - n7, n6);
                for (int k = 0; k < this.chunkGridSizeY; ++k) {
                    int n9 = k * 16;
                    ChunkRenderDispatcher.RenderChunk renderChunk = this.chunks[this.getChunkIndex(i, k, j)];
                    renderChunk.setOrigin(n5, n9, n8);
                }
            }
        }
    }

    public void setDirty(int n, int n2, int n3, boolean bl) {
        int n4 = Math.floorMod(n, this.chunkGridSizeX);
        int n5 = Math.floorMod(n2, this.chunkGridSizeY);
        int n6 = Math.floorMod(n3, this.chunkGridSizeZ);
        ChunkRenderDispatcher.RenderChunk renderChunk = this.chunks[this.getChunkIndex(n4, n5, n6)];
        renderChunk.setDirty(bl);
    }

    @Nullable
    protected ChunkRenderDispatcher.RenderChunk getRenderChunkAt(BlockPos blockPos) {
        int n = Mth.intFloorDiv(blockPos.getX(), 16);
        int n2 = Mth.intFloorDiv(blockPos.getY(), 16);
        int n3 = Mth.intFloorDiv(blockPos.getZ(), 16);
        if (n2 < 0 || n2 >= this.chunkGridSizeY) {
            return null;
        }
        n = Mth.positiveModulo(n, this.chunkGridSizeX);
        n3 = Mth.positiveModulo(n3, this.chunkGridSizeZ);
        return this.chunks[this.getChunkIndex(n, n2, n3)];
    }
}

