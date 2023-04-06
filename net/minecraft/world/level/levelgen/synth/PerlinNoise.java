/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.doubles.DoubleArrayList
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 *  it.unimi.dsi.fastutil.ints.IntBidirectionalIterator
 *  it.unimi.dsi.fastutil.ints.IntRBTreeSet
 *  it.unimi.dsi.fastutil.ints.IntSortedSet
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.synth;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.levelgen.synth.SurfaceNoise;

public class PerlinNoise
implements SurfaceNoise {
    private final ImprovedNoise[] noiseLevels;
    private final DoubleList amplitudes;
    private final double lowestFreqValueFactor;
    private final double lowestFreqInputFactor;

    public PerlinNoise(WorldgenRandom worldgenRandom, IntStream intStream) {
        this(worldgenRandom, (List)intStream.boxed().collect(ImmutableList.toImmutableList()));
    }

    public PerlinNoise(WorldgenRandom worldgenRandom, List<Integer> list) {
        this(worldgenRandom, (IntSortedSet)new IntRBTreeSet(list));
    }

    public static PerlinNoise create(WorldgenRandom worldgenRandom, int n, DoubleList doubleList) {
        return new PerlinNoise(worldgenRandom, (Pair<Integer, DoubleList>)Pair.of((Object)n, (Object)doubleList));
    }

    private static Pair<Integer, DoubleList> makeAmplitudes(IntSortedSet intSortedSet) {
        int n;
        if (intSortedSet.isEmpty()) {
            throw new IllegalArgumentException("Need some octaves!");
        }
        int n2 = -intSortedSet.firstInt();
        int n3 = n2 + (n = intSortedSet.lastInt()) + 1;
        if (n3 < 1) {
            throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
        }
        DoubleArrayList doubleArrayList = new DoubleArrayList(new double[n3]);
        IntBidirectionalIterator intBidirectionalIterator = intSortedSet.iterator();
        while (intBidirectionalIterator.hasNext()) {
            int n4 = intBidirectionalIterator.nextInt();
            doubleArrayList.set(n4 + n2, 1.0);
        }
        return Pair.of((Object)(-n2), (Object)doubleArrayList);
    }

    private PerlinNoise(WorldgenRandom worldgenRandom, IntSortedSet intSortedSet) {
        this(worldgenRandom, PerlinNoise.makeAmplitudes(intSortedSet));
    }

    private PerlinNoise(WorldgenRandom worldgenRandom, Pair<Integer, DoubleList> pair) {
        double d;
        int n = (Integer)pair.getFirst();
        this.amplitudes = (DoubleList)pair.getSecond();
        ImprovedNoise improvedNoise = new ImprovedNoise(worldgenRandom);
        int n2 = this.amplitudes.size();
        int n3 = -n;
        this.noiseLevels = new ImprovedNoise[n2];
        if (n3 >= 0 && n3 < n2 && (d = this.amplitudes.getDouble(n3)) != 0.0) {
            this.noiseLevels[n3] = improvedNoise;
        }
        for (int i = n3 - 1; i >= 0; --i) {
            if (i < n2) {
                double d2 = this.amplitudes.getDouble(i);
                if (d2 != 0.0) {
                    this.noiseLevels[i] = new ImprovedNoise(worldgenRandom);
                    continue;
                }
                worldgenRandom.consumeCount(262);
                continue;
            }
            worldgenRandom.consumeCount(262);
        }
        if (n3 < n2 - 1) {
            long l = (long)(improvedNoise.noise(0.0, 0.0, 0.0, 0.0, 0.0) * 9.223372036854776E18);
            WorldgenRandom worldgenRandom2 = new WorldgenRandom(l);
            for (int i = n3 + 1; i < n2; ++i) {
                if (i >= 0) {
                    double d3 = this.amplitudes.getDouble(i);
                    if (d3 != 0.0) {
                        this.noiseLevels[i] = new ImprovedNoise(worldgenRandom2);
                        continue;
                    }
                    worldgenRandom2.consumeCount(262);
                    continue;
                }
                worldgenRandom2.consumeCount(262);
            }
        }
        this.lowestFreqInputFactor = Math.pow(2.0, -n3);
        this.lowestFreqValueFactor = Math.pow(2.0, n2 - 1) / (Math.pow(2.0, n2) - 1.0);
    }

    public double getValue(double d, double d2, double d3) {
        return this.getValue(d, d2, d3, 0.0, 0.0, false);
    }

    public double getValue(double d, double d2, double d3, double d4, double d5, boolean bl) {
        double d6 = 0.0;
        double d7 = this.lowestFreqInputFactor;
        double d8 = this.lowestFreqValueFactor;
        for (int i = 0; i < this.noiseLevels.length; ++i) {
            ImprovedNoise improvedNoise = this.noiseLevels[i];
            if (improvedNoise != null) {
                d6 += this.amplitudes.getDouble(i) * improvedNoise.noise(PerlinNoise.wrap(d * d7), bl ? -improvedNoise.yo : PerlinNoise.wrap(d2 * d7), PerlinNoise.wrap(d3 * d7), d4 * d7, d5 * d7) * d8;
            }
            d7 *= 2.0;
            d8 /= 2.0;
        }
        return d6;
    }

    @Nullable
    public ImprovedNoise getOctaveNoise(int n) {
        return this.noiseLevels[this.noiseLevels.length - 1 - n];
    }

    public static double wrap(double d) {
        return d - (double)Mth.lfloor(d / 3.3554432E7 + 0.5) * 3.3554432E7;
    }

    @Override
    public double getSurfaceNoiseValue(double d, double d2, double d3, double d4) {
        return this.getValue(d, d2, 0.0, d3, d4, false);
    }
}

