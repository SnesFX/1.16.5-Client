/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 *  it.unimi.dsi.fastutil.doubles.DoubleListIterator
 */
package net.minecraft.world.level.levelgen.synth;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public class NormalNoise {
    private final double valueFactor;
    private final PerlinNoise first;
    private final PerlinNoise second;

    public static NormalNoise create(WorldgenRandom worldgenRandom, int n, DoubleList doubleList) {
        return new NormalNoise(worldgenRandom, n, doubleList);
    }

    private NormalNoise(WorldgenRandom worldgenRandom, int n, DoubleList doubleList) {
        this.first = PerlinNoise.create(worldgenRandom, n, doubleList);
        this.second = PerlinNoise.create(worldgenRandom, n, doubleList);
        int n2 = Integer.MAX_VALUE;
        int n3 = Integer.MIN_VALUE;
        DoubleListIterator doubleListIterator = doubleList.iterator();
        while (doubleListIterator.hasNext()) {
            int n4 = doubleListIterator.nextIndex();
            double d = doubleListIterator.nextDouble();
            if (d == 0.0) continue;
            n2 = Math.min(n2, n4);
            n3 = Math.max(n3, n4);
        }
        this.valueFactor = 0.16666666666666666 / NormalNoise.expectedDeviation(n3 - n2);
    }

    private static double expectedDeviation(int n) {
        return 0.1 * (1.0 + 1.0 / (double)(n + 1));
    }

    public double getValue(double d, double d2, double d3) {
        double d4 = d * 1.0181268882175227;
        double d5 = d2 * 1.0181268882175227;
        double d6 = d3 * 1.0181268882175227;
        return (this.first.getValue(d, d2, d3) + this.second.getValue(d4, d5, d6)) * this.valueFactor;
    }
}

