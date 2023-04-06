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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.KelpBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class KelpFeature
extends Feature<NoneFeatureConfiguration> {
    public KelpFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
        int n = 0;
        int n2 = worldGenLevel.getHeight(Heightmap.Types.OCEAN_FLOOR, blockPos.getX(), blockPos.getZ());
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), n2, blockPos.getZ());
        if (worldGenLevel.getBlockState(blockPos2).is(Blocks.WATER)) {
            BlockState blockState = Blocks.KELP.defaultBlockState();
            BlockState blockState2 = Blocks.KELP_PLANT.defaultBlockState();
            int n3 = 1 + random.nextInt(10);
            for (int i = 0; i <= n3; ++i) {
                if (worldGenLevel.getBlockState(blockPos2).is(Blocks.WATER) && worldGenLevel.getBlockState(blockPos2.above()).is(Blocks.WATER) && blockState2.canSurvive(worldGenLevel, blockPos2)) {
                    if (i == n3) {
                        worldGenLevel.setBlock(blockPos2, (BlockState)blockState.setValue(KelpBlock.AGE, random.nextInt(4) + 20), 2);
                        ++n;
                    } else {
                        worldGenLevel.setBlock(blockPos2, blockState2, 2);
                    }
                } else if (i > 0) {
                    BlockPos blockPos3 = blockPos2.below();
                    if (!blockState.canSurvive(worldGenLevel, blockPos3) || worldGenLevel.getBlockState(blockPos3.below()).is(Blocks.KELP)) break;
                    worldGenLevel.setBlock(blockPos3, (BlockState)blockState.setValue(KelpBlock.AGE, random.nextInt(4) + 20), 2);
                    ++n;
                    break;
                }
                blockPos2 = blockPos2.above();
            }
        }
        return n > 0;
    }
}

