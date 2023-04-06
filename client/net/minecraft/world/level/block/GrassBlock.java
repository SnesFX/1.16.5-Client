/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractFlowerFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class GrassBlock
extends SpreadingSnowyDirtBlock
implements BonemealableBlock {
    public GrassBlock(BlockBehaviour.Properties properties) {
        super(properties);
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
        BlockPos blockPos2 = blockPos.above();
        BlockState blockState2 = Blocks.GRASS.defaultBlockState();
        block0 : for (int i = 0; i < 128; ++i) {
            BlockState blockState3;
            BlockPos blockPos3 = blockPos2;
            for (int j = 0; j < i / 16; ++j) {
                if (!serverLevel.getBlockState((blockPos3 = blockPos3.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1)).below()).is(this) || serverLevel.getBlockState(blockPos3).isCollisionShapeFullBlock(serverLevel, blockPos3)) continue block0;
            }
            BlockState blockState4 = serverLevel.getBlockState(blockPos3);
            if (blockState4.is(blockState2.getBlock()) && random.nextInt(10) == 0) {
                ((BonemealableBlock)((Object)blockState2.getBlock())).performBonemeal(serverLevel, random, blockPos3, blockState4);
            }
            if (!blockState4.isAir()) continue;
            if (random.nextInt(8) == 0) {
                List<ConfiguredFeature<?, ?>> list = serverLevel.getBiome(blockPos3).getGenerationSettings().getFlowerFeatures();
                if (list.isEmpty()) continue;
                ConfiguredFeature<?, ?> configuredFeature = list.get(0);
                AbstractFlowerFeature abstractFlowerFeature = (AbstractFlowerFeature)configuredFeature.feature;
                blockState3 = abstractFlowerFeature.getRandomFlower(random, blockPos3, configuredFeature.config());
            } else {
                blockState3 = blockState2;
            }
            if (!blockState3.canSurvive(serverLevel, blockPos3)) continue;
            serverLevel.setBlock(blockPos3, blockState3, 3);
        }
    }
}

