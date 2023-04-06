/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;

public class CaveWorldCarver
extends WorldCarver<ProbabilityFeatureConfiguration> {
    public CaveWorldCarver(Codec<ProbabilityFeatureConfiguration> codec, int n) {
        super(codec, n);
    }

    @Override
    public boolean isStartChunk(Random random, int n, int n2, ProbabilityFeatureConfiguration probabilityFeatureConfiguration) {
        return random.nextFloat() <= probabilityFeatureConfiguration.probability;
    }

    @Override
    public boolean carve(ChunkAccess chunkAccess, Function<BlockPos, Biome> function, Random random, int n, int n2, int n3, int n4, int n5, BitSet bitSet, ProbabilityFeatureConfiguration probabilityFeatureConfiguration) {
        int n6 = (this.getRange() * 2 - 1) * 16;
        int n7 = random.nextInt(random.nextInt(random.nextInt(this.getCaveBound()) + 1) + 1);
        for (int i = 0; i < n7; ++i) {
            float f;
            double d = n2 * 16 + random.nextInt(16);
            double d2 = this.getCaveY(random);
            double d3 = n3 * 16 + random.nextInt(16);
            int n8 = 1;
            if (random.nextInt(4) == 0) {
                double d4 = 0.5;
                f = 1.0f + random.nextFloat() * 6.0f;
                this.genRoom(chunkAccess, function, random.nextLong(), n, n4, n5, d, d2, d3, f, 0.5, bitSet);
                n8 += random.nextInt(4);
            }
            for (int j = 0; j < n8; ++j) {
                float f2 = random.nextFloat() * 6.2831855f;
                f = (random.nextFloat() - 0.5f) / 4.0f;
                float f3 = this.getThickness(random);
                int n9 = n6 - random.nextInt(n6 / 4);
                boolean bl = false;
                this.genTunnel(chunkAccess, function, random.nextLong(), n, n4, n5, d, d2, d3, f3, f2, f, 0, n9, this.getYScale(), bitSet);
            }
        }
        return true;
    }

    protected int getCaveBound() {
        return 15;
    }

    protected float getThickness(Random random) {
        float f = random.nextFloat() * 2.0f + random.nextFloat();
        if (random.nextInt(10) == 0) {
            f *= random.nextFloat() * random.nextFloat() * 3.0f + 1.0f;
        }
        return f;
    }

    protected double getYScale() {
        return 1.0;
    }

    protected int getCaveY(Random random) {
        return random.nextInt(random.nextInt(120) + 8);
    }

    protected void genRoom(ChunkAccess chunkAccess, Function<BlockPos, Biome> function, long l, int n, int n2, int n3, double d, double d2, double d3, float f, double d4, BitSet bitSet) {
        double d5 = 1.5 + (double)(Mth.sin(1.5707964f) * f);
        double d6 = d5 * d4;
        this.carveSphere(chunkAccess, function, l, n, n2, n3, d + 1.0, d2, d3, d5, d6, bitSet);
    }

    protected void genTunnel(ChunkAccess chunkAccess, Function<BlockPos, Biome> function, long l, int n, int n2, int n3, double d, double d2, double d3, float f, float f2, float f3, int n4, int n5, double d4, BitSet bitSet) {
        Random random = new Random(l);
        int n6 = random.nextInt(n5 / 2) + n5 / 4;
        boolean bl = random.nextInt(6) == 0;
        float f4 = 0.0f;
        float f5 = 0.0f;
        for (int i = n4; i < n5; ++i) {
            double d5 = 1.5 + (double)(Mth.sin(3.1415927f * (float)i / (float)n5) * f);
            double d6 = d5 * d4;
            float f6 = Mth.cos(f3);
            d += (double)(Mth.cos(f2) * f6);
            d2 += (double)Mth.sin(f3);
            d3 += (double)(Mth.sin(f2) * f6);
            f3 *= bl ? 0.92f : 0.7f;
            f3 += f5 * 0.1f;
            f2 += f4 * 0.1f;
            f5 *= 0.9f;
            f4 *= 0.75f;
            f5 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f;
            f4 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f;
            if (i == n6 && f > 1.0f) {
                this.genTunnel(chunkAccess, function, random.nextLong(), n, n2, n3, d, d2, d3, random.nextFloat() * 0.5f + 0.5f, f2 - 1.5707964f, f3 / 3.0f, i, n5, 1.0, bitSet);
                this.genTunnel(chunkAccess, function, random.nextLong(), n, n2, n3, d, d2, d3, random.nextFloat() * 0.5f + 0.5f, f2 + 1.5707964f, f3 / 3.0f, i, n5, 1.0, bitSet);
                return;
            }
            if (random.nextInt(4) == 0) continue;
            if (!this.canReach(n2, n3, d, d3, i, n5, f)) {
                return;
            }
            this.carveSphere(chunkAccess, function, l, n, n2, n3, d, d2, d3, d5, d6, bitSet);
        }
    }

    @Override
    protected boolean skip(double d, double d2, double d3, int n) {
        return d2 <= -0.7 || d * d + d2 * d2 + d3 * d3 >= 1.0;
    }
}

