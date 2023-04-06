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
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class BasaltPillarFeature
extends Feature<NoneFeatureConfiguration> {
    public BasaltPillarFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
        if (!worldGenLevel.isEmptyBlock(blockPos) || worldGenLevel.isEmptyBlock(blockPos.above())) {
            return false;
        }
        BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();
        BlockPos.MutableBlockPos mutableBlockPos2 = blockPos.mutable();
        boolean bl = true;
        boolean bl2 = true;
        boolean bl3 = true;
        boolean bl4 = true;
        while (worldGenLevel.isEmptyBlock(mutableBlockPos)) {
            if (Level.isOutsideBuildHeight(mutableBlockPos)) {
                return true;
            }
            worldGenLevel.setBlock(mutableBlockPos, Blocks.BASALT.defaultBlockState(), 2);
            bl = bl && this.placeHangOff(worldGenLevel, random, mutableBlockPos2.setWithOffset(mutableBlockPos, Direction.NORTH));
            bl2 = bl2 && this.placeHangOff(worldGenLevel, random, mutableBlockPos2.setWithOffset(mutableBlockPos, Direction.SOUTH));
            bl3 = bl3 && this.placeHangOff(worldGenLevel, random, mutableBlockPos2.setWithOffset(mutableBlockPos, Direction.WEST));
            bl4 = bl4 && this.placeHangOff(worldGenLevel, random, mutableBlockPos2.setWithOffset(mutableBlockPos, Direction.EAST));
            mutableBlockPos.move(Direction.DOWN);
        }
        mutableBlockPos.move(Direction.UP);
        this.placeBaseHangOff(worldGenLevel, random, mutableBlockPos2.setWithOffset(mutableBlockPos, Direction.NORTH));
        this.placeBaseHangOff(worldGenLevel, random, mutableBlockPos2.setWithOffset(mutableBlockPos, Direction.SOUTH));
        this.placeBaseHangOff(worldGenLevel, random, mutableBlockPos2.setWithOffset(mutableBlockPos, Direction.WEST));
        this.placeBaseHangOff(worldGenLevel, random, mutableBlockPos2.setWithOffset(mutableBlockPos, Direction.EAST));
        mutableBlockPos.move(Direction.DOWN);
        BlockPos.MutableBlockPos mutableBlockPos3 = new BlockPos.MutableBlockPos();
        for (int i = -3; i < 4; ++i) {
            for (int j = -3; j < 4; ++j) {
                int n = Mth.abs(i) * Mth.abs(j);
                if (random.nextInt(10) >= 10 - n) continue;
                mutableBlockPos3.set(mutableBlockPos.offset(i, 0, j));
                int n2 = 3;
                while (worldGenLevel.isEmptyBlock(mutableBlockPos2.setWithOffset(mutableBlockPos3, Direction.DOWN))) {
                    mutableBlockPos3.move(Direction.DOWN);
                    if (--n2 > 0) continue;
                }
                if (worldGenLevel.isEmptyBlock(mutableBlockPos2.setWithOffset(mutableBlockPos3, Direction.DOWN))) continue;
                worldGenLevel.setBlock(mutableBlockPos3, Blocks.BASALT.defaultBlockState(), 2);
            }
        }
        return true;
    }

    private void placeBaseHangOff(LevelAccessor levelAccessor, Random random, BlockPos blockPos) {
        if (random.nextBoolean()) {
            levelAccessor.setBlock(blockPos, Blocks.BASALT.defaultBlockState(), 2);
        }
    }

    private boolean placeHangOff(LevelAccessor levelAccessor, Random random, BlockPos blockPos) {
        if (random.nextInt(10) != 0) {
            levelAccessor.setBlock(blockPos, Blocks.BASALT.defaultBlockState(), 2);
            return true;
        }
        return false;
    }
}

