/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Codec
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.ColumnFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class BasaltColumnsFeature
extends Feature<ColumnFeatureConfiguration> {
    private static final ImmutableList<Block> CANNOT_PLACE_ON = ImmutableList.of((Object)Blocks.LAVA, (Object)Blocks.BEDROCK, (Object)Blocks.MAGMA_BLOCK, (Object)Blocks.SOUL_SAND, (Object)Blocks.NETHER_BRICKS, (Object)Blocks.NETHER_BRICK_FENCE, (Object)Blocks.NETHER_BRICK_STAIRS, (Object)Blocks.NETHER_WART, (Object)Blocks.CHEST, (Object)Blocks.SPAWNER);

    public BasaltColumnsFeature(Codec<ColumnFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, ColumnFeatureConfiguration columnFeatureConfiguration) {
        int n = chunkGenerator.getSeaLevel();
        if (!BasaltColumnsFeature.canPlaceAt(worldGenLevel, n, blockPos.mutable())) {
            return false;
        }
        int n2 = columnFeatureConfiguration.height().sample(random);
        boolean bl = random.nextFloat() < 0.9f;
        int n3 = Math.min(n2, bl ? 5 : 8);
        int n4 = bl ? 50 : 15;
        boolean bl2 = false;
        for (BlockPos blockPos2 : BlockPos.randomBetweenClosed(random, n4, blockPos.getX() - n3, blockPos.getY(), blockPos.getZ() - n3, blockPos.getX() + n3, blockPos.getY(), blockPos.getZ() + n3)) {
            int n5 = n2 - blockPos2.distManhattan(blockPos);
            if (n5 < 0) continue;
            bl2 |= this.placeColumn(worldGenLevel, n, blockPos2, n5, columnFeatureConfiguration.reach().sample(random));
        }
        return bl2;
    }

    private boolean placeColumn(LevelAccessor levelAccessor, int n, BlockPos blockPos, int n2, int n3) {
        boolean bl = false;
        block0 : for (BlockPos blockPos2 : BlockPos.betweenClosed(blockPos.getX() - n3, blockPos.getY(), blockPos.getZ() - n3, blockPos.getX() + n3, blockPos.getY(), blockPos.getZ() + n3)) {
            BlockPos blockPos3;
            int n4 = blockPos2.distManhattan(blockPos);
            BlockPos blockPos4 = blockPos3 = BasaltColumnsFeature.isAirOrLavaOcean(levelAccessor, n, blockPos2) ? BasaltColumnsFeature.findSurface(levelAccessor, n, blockPos2.mutable(), n4) : BasaltColumnsFeature.findAir(levelAccessor, blockPos2.mutable(), n4);
            if (blockPos3 == null) continue;
            BlockPos.MutableBlockPos mutableBlockPos = blockPos3.mutable();
            for (int i = n2 - n4 / 2; i >= 0; --i) {
                if (BasaltColumnsFeature.isAirOrLavaOcean(levelAccessor, n, mutableBlockPos)) {
                    this.setBlock(levelAccessor, mutableBlockPos, Blocks.BASALT.defaultBlockState());
                    mutableBlockPos.move(Direction.UP);
                    bl = true;
                    continue;
                }
                if (!levelAccessor.getBlockState(mutableBlockPos).is(Blocks.BASALT)) continue block0;
                mutableBlockPos.move(Direction.UP);
            }
        }
        return bl;
    }

    @Nullable
    private static BlockPos findSurface(LevelAccessor levelAccessor, int n, BlockPos.MutableBlockPos mutableBlockPos, int n2) {
        while (mutableBlockPos.getY() > 1 && n2 > 0) {
            --n2;
            if (BasaltColumnsFeature.canPlaceAt(levelAccessor, n, mutableBlockPos)) {
                return mutableBlockPos;
            }
            mutableBlockPos.move(Direction.DOWN);
        }
        return null;
    }

    private static boolean canPlaceAt(LevelAccessor levelAccessor, int n, BlockPos.MutableBlockPos mutableBlockPos) {
        if (BasaltColumnsFeature.isAirOrLavaOcean(levelAccessor, n, mutableBlockPos)) {
            BlockState blockState = levelAccessor.getBlockState(mutableBlockPos.move(Direction.DOWN));
            mutableBlockPos.move(Direction.UP);
            return !blockState.isAir() && !CANNOT_PLACE_ON.contains((Object)blockState.getBlock());
        }
        return false;
    }

    @Nullable
    private static BlockPos findAir(LevelAccessor levelAccessor, BlockPos.MutableBlockPos mutableBlockPos, int n) {
        while (mutableBlockPos.getY() < levelAccessor.getMaxBuildHeight() && n > 0) {
            --n;
            BlockState blockState = levelAccessor.getBlockState(mutableBlockPos);
            if (CANNOT_PLACE_ON.contains((Object)blockState.getBlock())) {
                return null;
            }
            if (blockState.isAir()) {
                return mutableBlockPos;
            }
            mutableBlockPos.move(Direction.UP);
        }
        return null;
    }

    private static boolean isAirOrLavaOcean(LevelAccessor levelAccessor, int n, BlockPos blockPos) {
        BlockState blockState = levelAccessor.getBlockState(blockPos);
        return blockState.isAir() || blockState.is(Blocks.LAVA) && blockPos.getY() <= n;
    }
}

