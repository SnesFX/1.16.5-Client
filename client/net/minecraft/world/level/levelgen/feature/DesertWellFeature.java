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
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class DesertWellFeature
extends Feature<NoneFeatureConfiguration> {
    private static final BlockStatePredicate IS_SAND = BlockStatePredicate.forBlock(Blocks.SAND);
    private final BlockState sandSlab = Blocks.SANDSTONE_SLAB.defaultBlockState();
    private final BlockState sandstone = Blocks.SANDSTONE.defaultBlockState();
    private final BlockState water = Blocks.WATER.defaultBlockState();

    public DesertWellFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
        int n;
        int n2;
        int n3;
        int n4;
        blockPos = blockPos.above();
        while (worldGenLevel.isEmptyBlock(blockPos) && blockPos.getY() > 2) {
            blockPos = blockPos.below();
        }
        if (!IS_SAND.test(worldGenLevel.getBlockState(blockPos))) {
            return false;
        }
        for (n = -2; n <= 2; ++n) {
            for (n2 = -2; n2 <= 2; ++n2) {
                if (!worldGenLevel.isEmptyBlock(blockPos.offset(n, -1, n2)) || !worldGenLevel.isEmptyBlock(blockPos.offset(n, -2, n2))) continue;
                return false;
            }
        }
        for (n = -1; n <= 0; ++n) {
            for (n2 = -2; n2 <= 2; ++n2) {
                for (int i = -2; i <= 2; ++i) {
                    worldGenLevel.setBlock(blockPos.offset(n2, n, i), this.sandstone, 2);
                }
            }
        }
        worldGenLevel.setBlock(blockPos, this.water, 2);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            worldGenLevel.setBlock(blockPos.relative(direction), this.water, 2);
        }
        for (n4 = -2; n4 <= 2; ++n4) {
            for (n3 = -2; n3 <= 2; ++n3) {
                if (n4 != -2 && n4 != 2 && n3 != -2 && n3 != 2) continue;
                worldGenLevel.setBlock(blockPos.offset(n4, 1, n3), this.sandstone, 2);
            }
        }
        worldGenLevel.setBlock(blockPos.offset(2, 1, 0), this.sandSlab, 2);
        worldGenLevel.setBlock(blockPos.offset(-2, 1, 0), this.sandSlab, 2);
        worldGenLevel.setBlock(blockPos.offset(0, 1, 2), this.sandSlab, 2);
        worldGenLevel.setBlock(blockPos.offset(0, 1, -2), this.sandSlab, 2);
        for (n4 = -1; n4 <= 1; ++n4) {
            for (n3 = -1; n3 <= 1; ++n3) {
                if (n4 == 0 && n3 == 0) {
                    worldGenLevel.setBlock(blockPos.offset(n4, 4, n3), this.sandstone, 2);
                    continue;
                }
                worldGenLevel.setBlock(blockPos.offset(n4, 4, n3), this.sandSlab, 2);
            }
        }
        for (n4 = 1; n4 <= 3; ++n4) {
            worldGenLevel.setBlock(blockPos.offset(-1, n4, -1), this.sandstone, 2);
            worldGenLevel.setBlock(blockPos.offset(-1, n4, 1), this.sandstone, 2);
            worldGenLevel.setBlock(blockPos.offset(1, n4, -1), this.sandstone, 2);
            worldGenLevel.setBlock(blockPos.offset(1, n4, 1), this.sandstone, 2);
        }
        return true;
    }
}

