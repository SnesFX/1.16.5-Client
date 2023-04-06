/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  it.unimi.dsi.fastutil.ints.IntRBTreeSet
 *  it.unimi.dsi.fastutil.ints.IntSortedSet
 */
package net.minecraft.world.level.levelgen.synth;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import net.minecraft.world.level.levelgen.synth.SurfaceNoise;

public class PerlinSimplexNoise
implements SurfaceNoise {
    private final SimplexNoise[] noiseLevels;
    private final double highestFreqValueFactor;
    private final double highestFreqInputFactor;

    public PerlinSimplexNoise(WorldgenRandom worldgenRandom, IntStream intStream) {
        this(worldgenRandom, (List)intStream.boxed().collect(ImmutableList.toImmutableList()));
    }

    public PerlinSimplexNoise(WorldgenRandom worldgenRandom, List<Integer> list) {
        this(worldgenRandom, (IntSortedSet)new IntRBTreeSet(list));
    }

    private PerlinSimplexNoise(WorldgenRandom worldgenRandom, IntSortedSet intSortedSet) {
        int n;
        if (intSortedSet.isEmpty()) {
            throw new IllegalArgumentException("Need some octaves!");
        }
        int n2 = -intSortedSet.firstInt();
        int n3 = n2 + (n = intSortedSet.lastInt()) + 1;
        if (n3 < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
        }
        SimplexNoise simplexNoise = new SimplexNoise(worldgenRandom);
        int n4 = n;
        this.noiseLevels = new SimplexNoise[n3];
        if (n4 >= 0 && n4 < n3 && intSortedSet.contains(0)) {
            this.noiseLevels[n4] = simplexNoise;
        }
        for (int i = n4 + 1; i < n3; ++i) {
            if (i >= 0 && intSortedSet.contains(n4 - i)) {
                this.noiseLevels[i] = new SimplexNoise(worldgenRandom);
                continue;
            }
            worldgenRandom.consumeCount(262);
        }
        if (n > 0) {
            long l = (long)(simplexNoise.getValue(simplexNoise.xo, simplexNoise.yo, simplexNoise.zo) * 9.223372036854776E18);
            WorldgenRandom worldgenRandom2 = new WorldgenRandom(l);
            for (int i = n4 - 1; i >= 0; --i) {
                if (i < n3 && intSortedSet.contains(n4 - i)) {
                    this.noiseLevels[i] = new SimplexNoise(worldgenRandom2);
                    continue;
                }
                worldgenRandom2.consumeCount(262);
            }
        }
        this.highestFreqInputFactor = Math.pow(2.0, n);
        this.highestFreqValueFactor = 1.0 / (Math.pow(2.0, n3) - 1.0);
    }

    public double getValue(double d, double d2, boolean bl) {
        double d3 = 0.0;
        double d4 = this.highestFreqInputFactor;
        double d5 = this.highestFreqValueFactor;
        for (SimplexNoise simplexNoise : this.noiseLevels) {
            if (simplexNoise != null) {
                d3 += simplexNoise.getValue(d * d4 + (bl ? simplexNoise.xo : 0.0), d2 * d4 + (bl ? simplexNoise.yo : 0.0)) * d5;
            }
            d4 /= 2.0;
            d5 *= 2.0;
        }
        return d3;
    }

    @Override
    public double getSurfaceNoiseValue(double d, double d2, double d3, double d4) {
        return this.getValue(d, d2, true) * 0.55;
    }
}

