/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.Features;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.NetherForestVegetationFeature;
import net.minecraft.world.level.levelgen.feature.TwistingVinesFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;
import net.minecraft.world.level.lighting.LayerLightEngine;

public class NyliumBlock
extends Block
implements BonemealableBlock {
    protected NyliumBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    private static boolean canBeNylium(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.above();
        BlockState blockState2 = levelReader.getBlockState(blockPos2);
        int n = LayerLightEngine.getLightBlockInto(levelReader, blockState, blockPos, blockState2, blockPos2, Direction.UP, blockState2.getLightBlock(levelReader, blockPos2));
        return n < levelReader.getMaxLightLevel();
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        if (!NyliumBlock.canBeNylium(blockState, serverLevel, blockPos)) {
            serverLevel.setBlockAndUpdate(blockPos, Blocks.NETHERRACK.defaultBlockState());
        }
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, boolean bl) {
        return blockGetter.getBlockState(blockPos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, Random random, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, Random random, BlockPos blockPos, BlockState blockState) {
        BlockState blockState2 = serverLevel.getBlockState(blockPos);
        BlockPos blockPos2 = blockPos.above();
        if (blockState2.is(Blocks.CRIMSON_NYLIUM)) {
            NetherForestVegetationFeature.place(serverLevel, random, blockPos2, Features.Configs.CRIMSON_FOREST_CONFIG, 3, 1);
        } else if (blockState2.is(Blocks.WARPED_NYLIUM)) {
            NetherForestVegetationFeature.place(serverLevel, random, blockPos2, Features.Configs.WARPED_FOREST_CONFIG, 3, 1);
            NetherForestVegetationFeature.place(serverLevel, random, blockPos2, Features.Configs.NETHER_SPROUTS_CONFIG, 3, 1);
            if (random.nextInt(8) == 0) {
                TwistingVinesFeature.place(serverLevel, random, blockPos2, 3, 1, 2);
            }
        }
    }
}

