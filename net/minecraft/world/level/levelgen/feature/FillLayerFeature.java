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
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.LayerConfiguration;

public class FillLayerFeature
extends Feature<LayerConfiguration> {
    public FillLayerFeature(Codec<LayerConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, LayerConfiguration layerConfiguration) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                int n = blockPos.getX() + i;
                int n2 = blockPos.getZ() + j;
                int n3 = layerConfiguration.height;
                mutableBlockPos.set(n, n3, n2);
                if (!worldGenLevel.getBlockState(mutableBlockPos).isAir()) continue;
                worldGenLevel.setBlock(mutableBlockPos, layerConfiguration.state, 2);
            }
        }
        return true;
    }
}

