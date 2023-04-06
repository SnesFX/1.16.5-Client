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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BambooBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;

public class BambooFeature
extends Feature<ProbabilityFeatureConfiguration> {
    private static final BlockState BAMBOO_TRUNK = (BlockState)((BlockState)((BlockState)Blocks.BAMBOO.defaultBlockState().setValue(BambooBlock.AGE, 1)).setValue(BambooBlock.LEAVES, BambooLeaves.NONE)).setValue(BambooBlock.STAGE, 0);
    private static final BlockState BAMBOO_FINAL_LARGE = (BlockState)((BlockState)BAMBOO_TRUNK.setValue(BambooBlock.LEAVES, BambooLeaves.LARGE)).setValue(BambooBlock.STAGE, 1);
    private static final BlockState BAMBOO_TOP_LARGE = (BlockState)BAMBOO_TRUNK.setValue(BambooBlock.LEAVES, BambooLeaves.LARGE);
    private static final BlockState BAMBOO_TOP_SMALL = (BlockState)BAMBOO_TRUNK.setValue(BambooBlock.LEAVES, BambooLeaves.SMALL);

    public BambooFeature(Codec<ProbabilityFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, ProbabilityFeatureConfiguration probabilityFeatureConfiguration) {
        int n = 0;
        BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();
        BlockPos.MutableBlockPos mutableBlockPos2 = blockPos.mutable();
        if (worldGenLevel.isEmptyBlock(mutableBlockPos)) {
            if (Blocks.BAMBOO.defaultBlockState().canSurvive(worldGenLevel, mutableBlockPos)) {
                int n2;
                int n3 = random.nextInt(12) + 5;
                if (random.nextFloat() < probabilityFeatureConfiguration.probability) {
                    n2 = random.nextInt(4) + 1;
                    for (int i = blockPos.getX() - n2; i <= blockPos.getX() + n2; ++i) {
                        for (int j = blockPos.getZ() - n2; j <= blockPos.getZ() + n2; ++j) {
                            int n4;
                            int n5 = i - blockPos.getX();
                            if (n5 * n5 + (n4 = j - blockPos.getZ()) * n4 > n2 * n2) continue;
                            mutableBlockPos2.set(i, worldGenLevel.getHeight(Heightmap.Types.WORLD_SURFACE, i, j) - 1, j);
                            if (!BambooFeature.isDirt(worldGenLevel.getBlockState(mutableBlockPos2).getBlock())) continue;
                            worldGenLevel.setBlock(mutableBlockPos2, Blocks.PODZOL.defaultBlockState(), 2);
                        }
                    }
                }
                for (n2 = 0; n2 < n3 && worldGenLevel.isEmptyBlock(mutableBlockPos); ++n2) {
                    worldGenLevel.setBlock(mutableBlockPos, BAMBOO_TRUNK, 2);
                    mutableBlockPos.move(Direction.UP, 1);
                }
                if (mutableBlockPos.getY() - blockPos.getY() >= 3) {
                    worldGenLevel.setBlock(mutableBlockPos, BAMBOO_FINAL_LARGE, 2);
                    worldGenLevel.setBlock(mutableBlockPos.move(Direction.DOWN, 1), BAMBOO_TOP_LARGE, 2);
                    worldGenLevel.setBlock(mutableBlockPos.move(Direction.DOWN, 1), BAMBOO_TOP_SMALL, 2);
                }
            }
            ++n;
        }
        return n > 0;
    }
}

