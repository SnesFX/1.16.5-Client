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

public class CanyonWorldCarver
extends WorldCarver<ProbabilityFeatureConfiguration> {
    private final float[] rs = new float[1024];

    public CanyonWorldCarver(Codec<ProbabilityFeatureConfiguration> codec) {
        super(codec, 256);
    }

    @Override
    public boolean isStartChunk(Random random, int n, int n2, ProbabilityFeatureConfiguration probabilityFeatureConfiguration) {
        return random.nextFloat() <= probabilityFeatureConfiguration.probability;
    }

    @Override
    public boolean carve(ChunkAccess chunkAccess, Function<BlockPos, Biome> function, Random random, int n, int n2, int n3, int n4, int n5, BitSet bitSet, ProbabilityFeatureConfiguration probabilityFeatureConfiguration) {
        int n6 = (this.getRange() * 2 - 1) * 16;
        double d = n2 * 16 + random.nextInt(16);
        double d2 = random.nextInt(random.nextInt(40) + 8) + 20;
        double d3 = n3 * 16 + random.nextInt(16);
        float f = random.nextFloat() * 6.2831855f;
        float f2 = (random.nextFloat() - 0.5f) * 2.0f / 8.0f;
        double d4 = 3.0;
        float f3 = (random.nextFloat() * 2.0f + random.nextFloat()) * 2.0f;
        int n7 = n6 - random.nextInt(n6 / 4);
        boolean bl = false;
        this.genCanyon(chunkAccess, function, random.nextLong(), n, n4, n5, d, d2, d3, f3, f, f2, 0, n7, 3.0, bitSet);
        return true;
    }

    private void genCanyon(ChunkAccess chunkAccess, Function<BlockPos, Biome> function, long l, int n, int n2, int n3, double d, double d2, double d3, float f, float f2, float f3, int n4, int n5, double d4, BitSet bitSet) {
        Random random = new Random(l);
        float f4 = 1.0f;
        for (int i = 0; i < 256; ++i) {
            if (i == 0 || random.nextInt(3) == 0) {
                f4 = 1.0f + random.nextFloat() * random.nextFloat();
            }
            this.rs[i] = f4 * f4;
        }
        float f5 = 0.0f;
        float f6 = 0.0f;
        for (int i = n4; i < n5; ++i) {
            double d5 = 1.5 + (double)(Mth.sin((float)i * 3.1415927f / (float)n5) * f);
            double d6 = d5 * d4;
            d5 *= (double)random.nextFloat() * 0.25 + 0.75;
            d6 *= (double)random.nextFloat() * 0.25 + 0.75;
            float f7 = Mth.cos(f3);
            float f8 = Mth.sin(f3);
            d += (double)(Mth.cos(f2) * f7);
            d2 += (double)f8;
            d3 += (double)(Mth.sin(f2) * f7);
            f3 *= 0.7f;
            f3 += f6 * 0.05f;
            f2 += f5 * 0.05f;
            f6 *= 0.8f;
            f5 *= 0.5f;
            f6 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0f;
            f5 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0f;
            if (random.nextInt(4) == 0) continue;
            if (!this.canReach(n2, n3, d, d3, i, n5, f)) {
                return;
            }
            this.carveSphere(chunkAccess, function, l, n, n2, n3, d, d2, d3, d5, d6, bitSet);
        }
    }

    @Override
    protected boolean skip(double d, double d2, double d3, int n) {
        return (d * d + d3 * d3) * (double)this.rs[n - 1] + d2 * d2 / 6.0 >= 1.0;
    }
}

