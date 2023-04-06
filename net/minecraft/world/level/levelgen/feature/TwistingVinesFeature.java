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
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class TwistingVinesFeature
extends Feature<NoneFeatureConfiguration> {
    public TwistingVinesFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
        return TwistingVinesFeature.place(worldGenLevel, random, blockPos, 8, 4, 8);
    }

    public static boolean place(LevelAccessor levelAccessor, Random random, BlockPos blockPos, int n, int n2, int n3) {
        if (TwistingVinesFeature.isInvalidPlacementLocation(levelAccessor, blockPos)) {
            return false;
        }
        TwistingVinesFeature.placeTwistingVines(levelAccessor, random, blockPos, n, n2, n3);
        return true;
    }

    private static void placeTwistingVines(LevelAccessor levelAccessor, Random random, BlockPos blockPos, int n, int n2, int n3) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < n * n; ++i) {
            mutableBlockPos.set(blockPos).move(Mth.nextInt(random, -n, n), Mth.nextInt(random, -n2, n2), Mth.nextInt(random, -n, n));
            if (!TwistingVinesFeature.findFirstAirBlockAboveGround(levelAccessor, mutableBlockPos) || TwistingVinesFeature.isInvalidPlacementLocation(levelAccessor, mutableBlockPos)) continue;
            int n4 = Mth.nextInt(random, 1, n3);
            if (random.nextInt(6) == 0) {
                n4 *= 2;
            }
            if (random.nextInt(5) == 0) {
                n4 = 1;
            }
            int n5 = 17;
            int n6 = 25;
            TwistingVinesFeature.placeWeepingVinesColumn(levelAccessor, random, mutableBlockPos, n4, 17, 25);
        }
    }

    private static boolean findFirstAirBlockAboveGround(LevelAccessor levelAccessor, BlockPos.MutableBlockPos mutableBlockPos) {
        do {
            mutableBlockPos.move(0, -1, 0);
            if (!Level.isOutsideBuildHeight(mutableBlockPos)) continue;
            return false;
        } while (levelAccessor.getBlockState(mutableBlockPos).isAir());
        mutableBlockPos.move(0, 1, 0);
        return true;
    }

    public static void placeWeepingVinesColumn(LevelAccessor levelAccessor, Random random, BlockPos.MutableBlockPos mutableBlockPos, int n, int n2, int n3) {
        for (int i = 1; i <= n; ++i) {
            if (levelAccessor.isEmptyBlock(mutableBlockPos)) {
                if (i == n || !levelAccessor.isEmptyBlock((BlockPos)mutableBlockPos.above())) {
                    levelAccessor.setBlock(mutableBlockPos, (BlockState)Blocks.TWISTING_VINES.defaultBlockState().setValue(GrowingPlantHeadBlock.AGE, Mth.nextInt(random, n2, n3)), 2);
                    break;
                }
                levelAccessor.setBlock(mutableBlockPos, Blocks.TWISTING_VINES_PLANT.defaultBlockState(), 2);
            }
            mutableBlockPos.move(Direction.UP);
        }
    }

    private static boolean isInvalidPlacementLocation(LevelAccessor levelAccessor, BlockPos blockPos) {
        if (!levelAccessor.isEmptyBlock(blockPos)) {
            return true;
        }
        BlockState blockState = levelAccessor.getBlockState(blockPos.below());
        return !blockState.is(Blocks.NETHERRACK) && !blockState.is(Blocks.WARPED_NYLIUM) && !blockState.is(Blocks.WARPED_WART_BLOCK);
    }
}

