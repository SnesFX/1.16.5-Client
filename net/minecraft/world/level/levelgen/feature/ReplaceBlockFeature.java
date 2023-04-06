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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

public class ReplaceBlockFeature
extends Feature<ReplaceBlockConfiguration> {
    public ReplaceBlockFeature(Codec<ReplaceBlockConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, ReplaceBlockConfiguration replaceBlockConfiguration) {
        if (worldGenLevel.getBlockState(blockPos).is(replaceBlockConfiguration.target.getBlock())) {
            worldGenLevel.setBlock(blockPos, replaceBlockConfiguration.state, 2);
        }
        return true;
    }
}

