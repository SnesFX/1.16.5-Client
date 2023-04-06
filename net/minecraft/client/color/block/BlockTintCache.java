/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 */
package net.minecraft.client.color.block;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.IntSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class BlockTintCache {
    private final ThreadLocal<LatestCacheInfo> latestChunkOnThread = ThreadLocal.withInitial(() -> new LatestCacheInfo());
    private final Long2ObjectLinkedOpenHashMap<int[]> cache = new Long2ObjectLinkedOpenHashMap(256, 0.25f);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public int getColor(BlockPos blockPos, IntSupplier intSupplier) {
        int n;
        int n2 = blockPos.getX() >> 4;
        int n3 = blockPos.getZ() >> 4;
        LatestCacheInfo latestCacheInfo = this.latestChunkOnThread.get();
        if (latestCacheInfo.x != n2 || latestCacheInfo.z != n3) {
            latestCacheInfo.x = n2;
            latestCacheInfo.z = n3;
            latestCacheInfo.cache = this.findOrCreateChunkCache(n2, n3);
        }
        int n4 = blockPos.getX() & 0xF;
        int n5 = blockPos.getZ() & 0xF;
        int n6 = n5 << 4 | n4;
        int n7 = latestCacheInfo.cache[n6];
        if (n7 != -1) {
            return n7;
        }
        latestCacheInfo.cache[n6] = n = intSupplier.getAsInt();
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void invalidateForChunk(int n, int n2) {
        try {
            this.lock.writeLock().lock();
            for (int i = -1; i <= 1; ++i) {
                for (int j = -1; j <= 1; ++j) {
                    long l = ChunkPos.asLong(n + i, n2 + j);
                    this.cache.remove(l);
                }
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    public void invalidateAll() {
        try {
            this.lock.writeLock().lock();
            this.cache.clear();
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int[] findOrCreateChunkCache(int n, int n2) {
        int[] arrn;
        long l = ChunkPos.asLong(n, n2);
        this.lock.readLock().lock();
        try {
            arrn = (int[])this.cache.get(l);
        }
        finally {
            this.lock.readLock().unlock();
        }
        if (arrn != null) {
            return arrn;
        }
        int[] arrn2 = new int[256];
        Arrays.fill(arrn2, -1);
        try {
            this.lock.writeLock().lock();
            if (this.cache.size() >= 256) {
                this.cache.removeFirst();
            }
            this.cache.put(l, (Object)arrn2);
        }
        finally {
            this.lock.writeLock().unlock();
        }
        return arrn2;
    }

    static class LatestCacheInfo {
        public int x = Integer.MIN_VALUE;
        public int z = Integer.MIN_VALUE;
        public int[] cache;

        private LatestCacheInfo() {
        }
    }

}

