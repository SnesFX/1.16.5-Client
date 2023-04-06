/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class SpringFeature
extends Feature<SpringConfiguration> {
    public SpringFeature(Codec<SpringConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, SpringConfiguration springConfiguration) {
        if (!springConfiguration.validBlocks.contains(worldGenLevel.getBlockState(blockPos.above()).getBlock())) {
            return false;
        }
        if (springConfiguration.requiresBlockBelow && !springConfiguration.validBlocks.contains(worldGenLevel.getBlockState(blockPos.below()).getBlock())) {
            return false;
        }
        BlockState blockState = worldGenLevel.getBlockState(blockPos);
        if (!blockState.isAir() && !springConfiguration.validBlocks.contains(blockState.getBlock())) {
            return false;
        }
        int n = 0;
        int n2 = 0;
        if (springConfiguration.validBlocks.contains(worldGenLevel.getBlockState(blockPos.west()).getBlock())) {
            ++n2;
        }
        if (springConfiguration.validBlocks.contains(worldGenLevel.getBlockState(blockPos.east()).getBlock())) {
            ++n2;
        }
        if (springConfiguration.validBlocks.contains(worldGenLevel.getBlockState(blockPos.north()).getBlock())) {
            ++n2;
        }
        if (springConfiguration.validBlocks.contains(worldGenLevel.getBlockState(blockPos.south()).getBlock())) {
            ++n2;
        }
        if (springConfiguration.validBlocks.contains(worldGenLevel.getBlockState(blockPos.below()).getBlock())) {
            ++n2;
        }
        int n3 = 0;
        if (worldGenLevel.isEmptyBlock(blockPos.west())) {
            ++n3;
        }
        if (worldGenLevel.isEmptyBlock(blockPos.east())) {
            ++n3;
        }
        if (worldGenLevel.isEmptyBlock(blockPos.north())) {
            ++n3;
        }
        if (worldGenLevel.isEmptyBlock(blockPos.south())) {
            ++n3;
        }
        if (worldGenLevel.isEmptyBlock(blockPos.below())) {
            ++n3;
        }
        if (n2 == springConfiguration.rockCount && n3 == springConfiguration.holeCount) {
            worldGenLevel.setBlock(blockPos, springConfiguration.state.createLegacyBlock(), 2);
            worldGenLevel.getLiquidTicks().scheduleTick(blockPos, springConfiguration.state.getType(), 0);
            ++n;
        }
        return n > 0;
    }
}

