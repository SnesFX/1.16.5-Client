/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class EndIslandFeature
extends Feature<NoneFeatureConfiguration> {
    public EndIslandFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
        float f = random.nextInt(3) + 4;
        int n = 0;
        while (f > 0.5f) {
            for (int i = Mth.floor((float)(-f)); i <= Mth.ceil(f); ++i) {
                for (int j = Mth.floor((float)(-f)); j <= Mth.ceil(f); ++j) {
                    if (!((float)(i * i + j * j) <= (f + 1.0f) * (f + 1.0f))) continue;
                    this.setBlock(worldGenLevel, blockPos.offset(i, n, j), Blocks.END_STONE.defaultBlockState());
                }
            }
            f = (float)((double)f - ((double)random.nextInt(2) + 0.5));
            --n;
        }
        return true;
    }
}

