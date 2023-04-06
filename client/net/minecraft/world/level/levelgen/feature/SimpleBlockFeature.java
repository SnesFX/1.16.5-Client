/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;

public class SimpleBlockFeature
extends Feature<SimpleBlockConfiguration> {
    public SimpleBlockFeature(Codec<SimpleBlockConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, SimpleBlockConfiguration simpleBlockConfiguration) {
        if (simpleBlockConfiguration.placeOn.contains(worldGenLevel.getBlockState(blockPos.below())) && simpleBlockConfiguration.placeIn.contains(worldGenLevel.getBlockState(blockPos)) && simpleBlockConfiguration.placeUnder.contains(worldGenLevel.getBlockState(blockPos.above()))) {
            worldGenLevel.setBlock(blockPos, simpleBlockConfiguration.toPlace, 2);
            return true;
        }
        return false;
    }
}

