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
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class SeaPickleFeature
extends Feature<CountConfiguration> {
    public SeaPickleFeature(Codec<CountConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, CountConfiguration countConfiguration) {
        int n = 0;
        int n2 = countConfiguration.count().sample(random);
        for (int i = 0; i < n2; ++i) {
            int n3 = random.nextInt(8) - random.nextInt(8);
            int n4 = random.nextInt(8) - random.nextInt(8);
            int n5 = worldGenLevel.getHeight(Heightmap.Types.OCEAN_FLOOR, blockPos.getX() + n3, blockPos.getZ() + n4);
            BlockPos blockPos2 = new BlockPos(blockPos.getX() + n3, n5, blockPos.getZ() + n4);
            BlockState blockState = (BlockState)Blocks.SEA_PICKLE.defaultBlockState().setValue(SeaPickleBlock.PICKLES, random.nextInt(4) + 1);
            if (!worldGenLevel.getBlockState(blockPos2).is(Blocks.WATER) || !blockState.canSurvive(worldGenLevel, blockPos2)) continue;
            worldGenLevel.setBlock(blockPos2, blockState, 2);
            ++n;
        }
        return n > 0;
    }
}

