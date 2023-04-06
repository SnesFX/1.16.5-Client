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

public class WeepingVinesFeature
extends Feature<NoneFeatureConfiguration> {
    private static final Direction[] DIRECTIONS = Direction.values();

    public WeepingVinesFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
        if (!worldGenLevel.isEmptyBlock(blockPos)) {
            return false;
        }
        BlockState blockState = worldGenLevel.getBlockState(blockPos.above());
        if (!blockState.is(Blocks.NETHERRACK) && !blockState.is(Blocks.NETHER_WART_BLOCK)) {
            return false;
        }
        this.placeRoofNetherWart(worldGenLevel, random, blockPos);
        this.placeRoofWeepingVines(worldGenLevel, random, blockPos);
        return true;
    }

    private void placeRoofNetherWart(LevelAccessor levelAccessor, Random random, BlockPos blockPos) {
        levelAccessor.setBlock(blockPos, Blocks.NETHER_WART_BLOCK.defaultBlockState(), 2);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos mutableBlockPos2 = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 200; ++i) {
            mutableBlockPos.setWithOffset(blockPos, random.nextInt(6) - random.nextInt(6), random.nextInt(2) - random.nextInt(5), random.nextInt(6) - random.nextInt(6));
            if (!levelAccessor.isEmptyBlock(mutableBlockPos)) continue;
            int n = 0;
            for (Direction direction : DIRECTIONS) {
                BlockState blockState = levelAccessor.getBlockState(mutableBlockPos2.setWithOffset(mutableBlockPos, direction));
                if (blockState.is(Blocks.NETHERRACK) || blockState.is(Blocks.NETHER_WART_BLOCK)) {
                    ++n;
                }
                if (n > 1) break;
            }
            if (n != true) continue;
            levelAccessor.setBlock(mutableBlockPos, Blocks.NETHER_WART_BLOCK.defaultBlockState(), 2);
        }
    }

    private void placeRoofWeepingVines(LevelAccessor levelAccessor, Random random, BlockPos blockPos) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 100; ++i) {
            BlockState blockState;
            mutableBlockPos.setWithOffset(blockPos, random.nextInt(8) - random.nextInt(8), random.nextInt(2) - random.nextInt(7), random.nextInt(8) - random.nextInt(8));
            if (!levelAccessor.isEmptyBlock(mutableBlockPos) || !(blockState = levelAccessor.getBlockState((BlockPos)mutableBlockPos.above())).is(Blocks.NETHERRACK) && !blockState.is(Blocks.NETHER_WART_BLOCK)) continue;
            int n = Mth.nextInt(random, 1, 8);
            if (random.nextInt(6) == 0) {
                n *= 2;
            }
            if (random.nextInt(5) == 0) {
                n = 1;
            }
            int n2 = 17;
            int n3 = 25;
            WeepingVinesFeature.placeWeepingVinesColumn(levelAccessor, random, mutableBlockPos, n, 17, 25);
        }
    }

    public static void placeWeepingVinesColumn(LevelAccessor levelAccessor, Random random, BlockPos.MutableBlockPos mutableBlockPos, int n, int n2, int n3) {
        for (int i = 0; i <= n; ++i) {
            if (levelAccessor.isEmptyBlock(mutableBlockPos)) {
                if (i == n || !levelAccessor.isEmptyBlock((BlockPos)mutableBlockPos.below())) {
                    levelAccessor.setBlock(mutableBlockPos, (BlockState)Blocks.WEEPING_VINES.defaultBlockState().setValue(GrowingPlantHeadBlock.AGE, Mth.nextInt(random, n2, n3)), 2);
                    break;
                }
                levelAccessor.setBlock(mutableBlockPos, Blocks.WEEPING_VINES_PLANT.defaultBlockState(), 2);
            }
            mutableBlockPos.move(Direction.DOWN);
        }
    }
}

