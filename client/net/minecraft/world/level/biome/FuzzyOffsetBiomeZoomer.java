/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.biome;

import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeZoomer;

public enum FuzzyOffsetBiomeZoomer implements BiomeZoomer
{
    INSTANCE;
    

    @Override
    public Biome getBiome(long l, int n, int n2, int n3, BiomeManager.NoiseBiomeSource noiseBiomeSource) {
        int n4;
        int n5;
        int n6;
        int n7;
        int n8 = n - 2;
        int n9 = n2 - 2;
        int n10 = n3 - 2;
        int n11 = n8 >> 2;
        int n12 = n9 >> 2;
        int n13 = n10 >> 2;
        double d = (double)(n8 & 3) / 4.0;
        double d2 = (double)(n9 & 3) / 4.0;
        double d3 = (double)(n10 & 3) / 4.0;
        double[] arrd = new double[8];
        for (n4 = 0; n4 < 8; ++n4) {
            boolean bl = (n4 & 4) == 0;
            boolean bl2 = (n4 & 2) == 0;
            n6 = (n4 & 1) == 0 ? 1 : 0;
            n7 = bl ? n11 : n11 + 1;
            n5 = bl2 ? n12 : n12 + 1;
            int n14 = n6 != 0 ? n13 : n13 + 1;
            double d4 = bl ? d : d - 1.0;
            double d5 = bl2 ? d2 : d2 - 1.0;
            double d6 = n6 != 0 ? d3 : d3 - 1.0;
            arrd[n4] = FuzzyOffsetBiomeZoomer.getFiddledDistance(l, n7, n5, n14, d4, d5, d6);
        }
        n4 = 0;
        double d7 = arrd[0];
        for (n6 = 1; n6 < 8; ++n6) {
            if (!(d7 > arrd[n6])) continue;
            n4 = n6;
            d7 = arrd[n6];
        }
        n6 = (n4 & 4) == 0 ? n11 : n11 + 1;
        n7 = (n4 & 2) == 0 ? n12 : n12 + 1;
        n5 = (n4 & 1) == 0 ? n13 : n13 + 1;
        return noiseBiomeSource.getNoiseBiome(n6, n7, n5);
    }

    private static double getFiddledDistance(long l, int n, int n2, int n3, double d, double d2, double d3) {
        long l2 = l;
        l2 = LinearCongruentialGenerator.next(l2, n);
        l2 = LinearCongruentialGenerator.next(l2, n2);
        l2 = LinearCongruentialGenerator.next(l2, n3);
        l2 = LinearCongruentialGenerator.next(l2, n);
        l2 = LinearCongruentialGenerator.next(l2, n2);
        l2 = LinearCongruentialGenerator.next(l2, n3);
        double d4 = FuzzyOffsetBiomeZoomer.getFiddle(l2);
        l2 = LinearCongruentialGenerator.next(l2, l);
        double d5 = FuzzyOffsetBiomeZoomer.getFiddle(l2);
        l2 = LinearCongruentialGenerator.next(l2, l);
        double d6 = FuzzyOffsetBiomeZoomer.getFiddle(l2);
        return FuzzyOffsetBiomeZoomer.sqr(d3 + d6) + FuzzyOffsetBiomeZoomer.sqr(d2 + d5) + FuzzyOffsetBiomeZoomer.sqr(d + d4);
    }

    private static double getFiddle(long l) {
        double d = (double)((int)Math.floorMod(l >> 24, 1024L)) / 1024.0;
        return (d - 0.5) * 0.9;
    }

    private static double sqr(double d) {
        return d * d;
    }
}

