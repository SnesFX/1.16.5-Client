/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;

public class ChunkPos {
    public static final long INVALID_CHUNK_POS = ChunkPos.asLong(1875016, 1875016);
    public final int x;
    public final int z;

    public ChunkPos(int n, int n2) {
        this.x = n;
        this.z = n2;
    }

    public ChunkPos(BlockPos blockPos) {
        this.x = blockPos.getX() >> 4;
        this.z = blockPos.getZ() >> 4;
    }

    public ChunkPos(long l) {
        this.x = (int)l;
        this.z = (int)(l >> 32);
    }

    public long toLong() {
        return ChunkPos.asLong(this.x, this.z);
    }

    public static long asLong(int n, int n2) {
        return (long)n & 0xFFFFFFFFL | ((long)n2 & 0xFFFFFFFFL) << 32;
    }

    public static int getX(long l) {
        return (int)(l & 0xFFFFFFFFL);
    }

    public static int getZ(long l) {
        return (int)(l >>> 32 & 0xFFFFFFFFL);
    }

    public int hashCode() {
        int n = 1664525 * this.x + 1013904223;
        int n2 = 1664525 * (this.z ^ 0xDEADBEEF) + 1013904223;
        return n ^ n2;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof ChunkPos) {
            ChunkPos chunkPos = (ChunkPos)object;
            return this.x == chunkPos.x && this.z == chunkPos.z;
        }
        return false;
    }

    public int getMinBlockX() {
        return this.x << 4;
    }

    public int getMinBlockZ() {
        return this.z << 4;
    }

    public int getMaxBlockX() {
        return (this.x << 4) + 15;
    }

    public int getMaxBlockZ() {
        return (this.z << 4) + 15;
    }

    public int getRegionX() {
        return this.x >> 5;
    }

    public int getRegionZ() {
        return this.z >> 5;
    }

    public int getRegionLocalX() {
        return this.x & 0x1F;
    }

    public int getRegionLocalZ() {
        return this.z & 0x1F;
    }

    public String toString() {
        return "[" + this.x + ", " + this.z + "]";
    }

    public BlockPos getWorldPosition() {
        return new BlockPos(this.getMinBlockX(), 0, this.getMinBlockZ());
    }

    public int getChessboardDistance(ChunkPos chunkPos) {
        return Math.max(Math.abs(this.x - chunkPos.x), Math.abs(this.z - chunkPos.z));
    }

    public static Stream<ChunkPos> rangeClosed(ChunkPos chunkPos, int n) {
        return ChunkPos.rangeClosed(new ChunkPos(chunkPos.x - n, chunkPos.z - n), new ChunkPos(chunkPos.x + n, chunkPos.z + n));
    }

    public static Stream<ChunkPos> rangeClosed(final ChunkPos chunkPos, final ChunkPos chunkPos2) {
        int n = Math.abs(chunkPos.x - chunkPos2.x) + 1;
        int n2 = Math.abs(chunkPos.z - chunkPos2.z) + 1;
        final int n3 = chunkPos.x < chunkPos2.x ? 1 : -1;
        final int n4 = chunkPos.z < chunkPos2.z ? 1 : -1;
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<ChunkPos>((long)(n * n2), 64){
            @Nullable
            private ChunkPos pos;

            @Override
            public boolean tryAdvance(Consumer<? super ChunkPos> consumer) {
                if (this.pos == null) {
                    this.pos = chunkPos;
                } else {
                    int n = this.pos.x;
                    int n2 = this.pos.z;
                    if (n == chunkPos2.x) {
                        if (n2 == chunkPos2.z) {
                            return false;
                        }
                        this.pos = new ChunkPos(chunkPos.x, n2 + n4);
                    } else {
                        this.pos = new ChunkPos(n + n3, n2);
                    }
                }
                consumer.accept(this.pos);
                return true;
            }
        }, false);
    }

}

