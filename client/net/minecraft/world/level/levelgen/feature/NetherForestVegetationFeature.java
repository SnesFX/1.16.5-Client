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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class NetherForestVegetationFeature
extends Feature<BlockPileConfiguration> {
    public NetherForestVegetationFeature(Codec<BlockPileConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, BlockPileConfiguration blockPileConfiguration) {
        return NetherForestVegetationFeature.place(worldGenLevel, random, blockPos, blockPileConfiguration, 8, 4);
    }

    public static boolean place(LevelAccessor levelAccessor, Random random, BlockPos blockPos, BlockPileConfiguration blockPileConfiguration, int n, int n2) {
        Block block = levelAccessor.getBlockState(blockPos.below()).getBlock();
        if (!block.is(BlockTags.NYLIUM)) {
            return false;
        }
        int n3 = blockPos.getY();
        if (n3 < 1 || n3 + 1 >= 256) {
            return false;
        }
        int n4 = 0;
        for (int i = 0; i < n * n; ++i) {
            BlockPos blockPos2 = blockPos.offset(random.nextInt(n) - random.nextInt(n), random.nextInt(n2) - random.nextInt(n2), random.nextInt(n) - random.nextInt(n));
            BlockState blockState = blockPileConfiguration.stateProvider.getState(random, blockPos2);
            if (!levelAccessor.isEmptyBlock(blockPos2) || blockPos2.getY() <= 0 || !blockState.canSurvive(levelAccessor, blockPos2)) continue;
            levelAccessor.setBlock(blockPos2, blockState, 2);
            ++n4;
        }
        return n4 > 0;
    }
}

