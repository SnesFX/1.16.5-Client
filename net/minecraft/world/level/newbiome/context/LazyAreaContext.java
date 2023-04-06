/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap
 */
package net.minecraft.world.level.newbiome.context;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.Random;
import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.area.LazyArea;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.layer.traits.PixelTransformer;

public class LazyAreaContext
implements BigContext<LazyArea> {
    private final Long2IntLinkedOpenHashMap cache;
    private final int maxCache;
    private final ImprovedNoise biomeNoise;
    private final long seed;
    private long rval;

    public LazyAreaContext(int n, long l, long l2) {
        this.seed = LazyAreaContext.mixSeed(l, l2);
        this.biomeNoise = new ImprovedNoise(new Random(l));
        this.cache = new Long2IntLinkedOpenHashMap(16, 0.25f);
        this.cache.defaultReturnValue(Integer.MIN_VALUE);
        this.maxCache = n;
    }

    @Override
    public LazyArea createResult(PixelTransformer pixelTransformer) {
        return new LazyArea(this.cache, this.maxCache, pixelTransformer);
    }

    @Override
    public LazyArea createResult(PixelTransformer pixelTransformer, LazyArea lazyArea) {
        return new LazyArea(this.cache, Math.min(1024, lazyArea.getMaxCache() * 4), pixelTransformer);
    }

    @Override
    public LazyArea createResult(PixelTransformer pixelTransformer, LazyArea lazyArea, LazyArea lazyArea2) {
        return new LazyArea(this.cache, Math.min(1024, Math.max(lazyArea.getMaxCache(), lazyArea2.getMaxCache()) * 4), pixelTransformer);
    }

    @Override
    public void initRandom(long l, long l2) {
        long l3 = this.seed;
        l3 = LinearCongruentialGenerator.next(l3, l);
        l3 = LinearCongruentialGenerator.next(l3, l2);
        l3 = LinearCongruentialGenerator.next(l3, l);
        this.rval = l3 = LinearCongruentialGenerator.next(l3, l2);
    }

    @Override
    public int nextRandom(int n) {
        int n2 = (int)Math.floorMod(this.rval >> 24, (long)n);
        this.rval = LinearCongruentialGenerator.next(this.rval, this.seed);
        return n2;
    }

    @Override
    public ImprovedNoise getBiomeNoise() {
        return this.biomeNoise;
    }

    private static long mixSeed(long l, long l2) {
        long l3 = l2;
        l3 = LinearCongruentialGenerator.next(l3, l2);
        l3 = LinearCongruentialGenerator.next(l3, l2);
        l3 = LinearCongruentialGenerator.next(l3, l2);
        long l4 = l;
        l4 = LinearCongruentialGenerator.next(l4, l3);
        l4 = LinearCongruentialGenerator.next(l4, l3);
        l4 = LinearCongruentialGenerator.next(l4, l3);
        return l4;
    }

    @Override
    public /* synthetic */ Area createResult(PixelTransformer pixelTransformer) {
        return this.createResult(pixelTransformer);
    }
}

