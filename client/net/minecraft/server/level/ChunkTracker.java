/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.server.level;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.lighting.DynamicGraphMinFixedPoint;

public abstract class ChunkTracker
extends DynamicGraphMinFixedPoint {
    protected ChunkTracker(int n, int n2, int n3) {
        super(n, n2, n3);
    }

    @Override
    protected boolean isSource(long l) {
        return l == ChunkPos.INVALID_CHUNK_POS;
    }

    @Override
    protected void checkNeighborsAfterUpdate(long l, int n, boolean bl) {
        ChunkPos chunkPos = new ChunkPos(l);
        int n2 = chunkPos.x;
        int n3 = chunkPos.z;
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                long l2 = ChunkPos.asLong(n2 + i, n3 + j);
                if (l2 == l) continue;
                this.checkNeighbor(l, l2, n, bl);
            }
        }
    }

    @Override
    protected int getComputedLevel(long l, long l2, int n) {
        int n2 = n;
        ChunkPos chunkPos = new ChunkPos(l);
        int n3 = chunkPos.x;
        int n4 = chunkPos.z;
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                long l3 = ChunkPos.asLong(n3 + i, n4 + j);
                if (l3 == l) {
                    l3 = ChunkPos.INVALID_CHUNK_POS;
                }
                if (l3 == l2) continue;
                int n5 = this.computeLevelFromNeighbor(l3, l, this.getLevel(l3));
                if (n2 > n5) {
                    n2 = n5;
                }
                if (n2 != 0) continue;
                return n2;
            }
        }
        return n2;
    }

    @Override
    protected int computeLevelFromNeighbor(long l, long l2, int n) {
        if (l == ChunkPos.INVALID_CHUNK_POS) {
            return this.getLevelFromSource(l2);
        }
        return n + 1;
    }

    protected abstract int getLevelFromSource(long var1);

    public void update(long l, int n, boolean bl) {
        this.checkEdge(ChunkPos.INVALID_CHUNK_POS, l, n, bl);
    }
}

