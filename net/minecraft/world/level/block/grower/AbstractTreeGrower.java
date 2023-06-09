/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public abstract class AbstractTreeGrower {
    @Nullable
    protected abstract ConfiguredFeature<TreeConfiguration, ?> getConfiguredFeature(Random var1, boolean var2);

    public boolean growTree(ServerLevel serverLevel, ChunkGenerator chunkGenerator, BlockPos blockPos, BlockState blockState, Random random) {
        ConfiguredFeature<TreeConfiguration, ?> configuredFeature = this.getConfiguredFeature(random, this.hasFlowers(serverLevel, blockPos));
        if (configuredFeature == null) {
            return false;
        }
        serverLevel.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 4);
        ((TreeConfiguration)configuredFeature.config).setFromSapling();
        if (configuredFeature.place(serverLevel, chunkGenerator, random, blockPos)) {
            return true;
        }
        serverLevel.setBlock(blockPos, blockState, 4);
        return false;
    }

    private boolean hasFlowers(LevelAccessor levelAccessor, BlockPos blockPos) {
        for (BlockPos blockPos2 : BlockPos.MutableBlockPos.betweenClosed(blockPos.below().north(2).west(2), blockPos.above().south(2).east(2))) {
            if (!levelAccessor.getBlockState(blockPos2).is(BlockTags.FLOWERS)) continue;
            return true;
        }
        return false;
    }
}

