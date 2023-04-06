/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

public class OreFeature
extends Feature<OreConfiguration> {
    public OreFeature(Codec<OreConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, OreConfiguration oreConfiguration) {
        float f = random.nextFloat() * 3.1415927f;
        float f2 = (float)oreConfiguration.size / 8.0f;
        int n = Mth.ceil(((float)oreConfiguration.size / 16.0f * 2.0f + 1.0f) / 2.0f);
        double d = (double)blockPos.getX() + Math.sin(f) * (double)f2;
        double d2 = (double)blockPos.getX() - Math.sin(f) * (double)f2;
        double d3 = (double)blockPos.getZ() + Math.cos(f) * (double)f2;
        double d4 = (double)blockPos.getZ() - Math.cos(f) * (double)f2;
        int n2 = 2;
        double d5 = blockPos.getY() + random.nextInt(3) - 2;
        double d6 = blockPos.getY() + random.nextInt(3) - 2;
        int n3 = blockPos.getX() - Mth.ceil(f2) - n;
        int n4 = blockPos.getY() - 2 - n;
        int n5 = blockPos.getZ() - Mth.ceil(f2) - n;
        int n6 = 2 * (Mth.ceil(f2) + n);
        int n7 = 2 * (2 + n);
        for (int i = n3; i <= n3 + n6; ++i) {
            for (int j = n5; j <= n5 + n6; ++j) {
                if (n4 > worldGenLevel.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, i, j)) continue;
                return this.doPlace(worldGenLevel, random, oreConfiguration, d, d2, d3, d4, d5, d6, n3, n4, n5, n6, n7);
            }
        }
        return false;
    }

    protected boolean doPlace(LevelAccessor levelAccessor, Random random, OreConfiguration oreConfiguration, double d, double d2, double d3, double d4, double d5, double d6, int n, int n2, int n3, int n4, int n5) {
        double d7;
        int n6;
        double d8;
        double d9;
        double d10;
        int n7 = 0;
        BitSet bitSet = new BitSet(n4 * n5 * n4);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        int n8 = oreConfiguration.size;
        double[] arrd = new double[n8 * 4];
        for (n6 = 0; n6 < n8; ++n6) {
            float f = (float)n6 / (float)n8;
            d7 = Mth.lerp((double)f, d, d2);
            d10 = Mth.lerp((double)f, d5, d6);
            d8 = Mth.lerp((double)f, d3, d4);
            d9 = random.nextDouble() * (double)n8 / 16.0;
            double d11 = ((double)(Mth.sin(3.1415927f * f) + 1.0f) * d9 + 1.0) / 2.0;
            arrd[n6 * 4 + 0] = d7;
            arrd[n6 * 4 + 1] = d10;
            arrd[n6 * 4 + 2] = d8;
            arrd[n6 * 4 + 3] = d11;
        }
        for (n6 = 0; n6 < n8 - 1; ++n6) {
            if (arrd[n6 * 4 + 3] <= 0.0) continue;
            for (int i = n6 + 1; i < n8; ++i) {
                if (arrd[i * 4 + 3] <= 0.0 || !((d9 = arrd[n6 * 4 + 3] - arrd[i * 4 + 3]) * d9 > (d7 = arrd[n6 * 4 + 0] - arrd[i * 4 + 0]) * d7 + (d10 = arrd[n6 * 4 + 1] - arrd[i * 4 + 1]) * d10 + (d8 = arrd[n6 * 4 + 2] - arrd[i * 4 + 2]) * d8)) continue;
                if (d9 > 0.0) {
                    arrd[i * 4 + 3] = -1.0;
                    continue;
                }
                arrd[n6 * 4 + 3] = -1.0;
            }
        }
        for (n6 = 0; n6 < n8; ++n6) {
            double d12 = arrd[n6 * 4 + 3];
            if (d12 < 0.0) continue;
            double d13 = arrd[n6 * 4 + 0];
            double d14 = arrd[n6 * 4 + 1];
            double d15 = arrd[n6 * 4 + 2];
            int n9 = Math.max(Mth.floor(d13 - d12), n);
            int n10 = Math.max(Mth.floor(d14 - d12), n2);
            int n11 = Math.max(Mth.floor(d15 - d12), n3);
            int n12 = Math.max(Mth.floor(d13 + d12), n9);
            int n13 = Math.max(Mth.floor(d14 + d12), n10);
            int n14 = Math.max(Mth.floor(d15 + d12), n11);
            for (int i = n9; i <= n12; ++i) {
                double d16 = ((double)i + 0.5 - d13) / d12;
                if (!(d16 * d16 < 1.0)) continue;
                for (int j = n10; j <= n13; ++j) {
                    double d17 = ((double)j + 0.5 - d14) / d12;
                    if (!(d16 * d16 + d17 * d17 < 1.0)) continue;
                    for (int k = n11; k <= n14; ++k) {
                        int n15;
                        double d18 = ((double)k + 0.5 - d15) / d12;
                        if (!(d16 * d16 + d17 * d17 + d18 * d18 < 1.0) || bitSet.get(n15 = i - n + (j - n2) * n4 + (k - n3) * n4 * n5)) continue;
                        bitSet.set(n15);
                        mutableBlockPos.set(i, j, k);
                        if (!oreConfiguration.target.test(levelAccessor.getBlockState(mutableBlockPos), random)) continue;
                        levelAccessor.setBlock(mutableBlockPos, oreConfiguration.state, 2);
                        ++n7;
                    }
                }
            }
        }
        return n7 > 0;
    }
}

