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
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class BlockPileFeature
extends Feature<BlockPileConfiguration> {
    public BlockPileFeature(Codec<BlockPileConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, BlockPileConfiguration blockPileConfiguration) {
        if (blockPos.getY() < 5) {
            return false;
        }
        int n = 2 + random.nextInt(2);
        int n2 = 2 + random.nextInt(2);
        for (BlockPos blockPos2 : BlockPos.betweenClosed(blockPos.offset(-n, 0, -n2), blockPos.offset(n, 1, n2))) {
            int n3;
            int n4 = blockPos.getX() - blockPos2.getX();
            if ((float)(n4 * n4 + (n3 = blockPos.getZ() - blockPos2.getZ()) * n3) <= random.nextFloat() * 10.0f - random.nextFloat() * 6.0f) {
                this.tryPlaceBlock(worldGenLevel, blockPos2, random, blockPileConfiguration);
                continue;
            }
            if (!((double)random.nextFloat() < 0.031)) continue;
            this.tryPlaceBlock(worldGenLevel, blockPos2, random, blockPileConfiguration);
        }
        return true;
    }

    private boolean mayPlaceOn(LevelAccessor levelAccessor, BlockPos blockPos, Random random) {
        BlockPos blockPos2 = blockPos.below();
        BlockState blockState = levelAccessor.getBlockState(blockPos2);
        if (blockState.is(Blocks.GRASS_PATH)) {
            return random.nextBoolean();
        }
        return blockState.isFaceSturdy(levelAccessor, blockPos2, Direction.UP);
    }

    private void tryPlaceBlock(LevelAccessor levelAccessor, BlockPos blockPos, Random random, BlockPileConfiguration blockPileConfiguration) {
        if (levelAccessor.isEmptyBlock(blockPos) && this.mayPlaceOn(levelAccessor, blockPos, random)) {
            levelAccessor.setBlock(blockPos, blockPileConfiguration.stateProvider.getState(random, blockPos), 4);
        }
    }
}

