/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap
 */
package net.minecraft.world.level.newbiome.area;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.layer.traits.PixelTransformer;

public final class LazyArea
implements Area {
    private final PixelTransformer transformer;
    private final Long2IntLinkedOpenHashMap cache;
    private final int maxCache;

    public LazyArea(Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap, int n, PixelTransformer pixelTransformer) {
        this.cache = long2IntLinkedOpenHashMap;
        this.maxCache = n;
        this.transformer = pixelTransformer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int get(int n, int n2) {
        long l = ChunkPos.asLong(n, n2);
        Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap = this.cache;
        synchronized (long2IntLinkedOpenHashMap) {
            int n3 = this.cache.get(l);
            if (n3 != Integer.MIN_VALUE) {
                return n3;
            }
            int n4 = this.transformer.apply(n, n2);
            this.cache.put(l, n4);
            if (this.cache.size() > this.maxCache) {
                for (int i = 0; i < this.maxCache / 16; ++i) {
                    this.cache.removeFirstInt();
                }
            }
            return n4;
        }
    }

    public int getMaxCache() {
        return this.maxCache;
    }
}

