/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.Features;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MushroomBlock
extends BushBlock
implements BonemealableBlock {
    protected static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);

    public MushroomBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos object, Random random) {
        if (random.nextInt(25) == 0) {
            int n = 5;
            int n2 = 4;
            for (BlockPos blockPos : BlockPos.betweenClosed(((BlockPos)object).offset(-4, -1, -4), ((BlockPos)object).offset(4, 1, 4))) {
                if (!serverLevel.getBlockState(blockPos).is(this) || --n > 0) continue;
                return;
            }
            Object object2 = ((BlockPos)object).offset(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            for (int i = 0; i < 4; ++i) {
                if (serverLevel.isEmptyBlock((BlockPos)object2) && blockState.canSurvive(serverLevel, (BlockPos)object2)) {
                    object = object2;
                }
                object2 = ((BlockPos)object).offset(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            }
            if (serverLevel.isEmptyBlock((BlockPos)object2) && blockState.canSurvive(serverLevel, (BlockPos)object2)) {
                serverLevel.setBlock((BlockPos)object2, blockState, 2);
            }
        }
    }

    @Override
    protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return blockState.isSolidRender(blockGetter, blockPos);
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.below();
        BlockState blockState2 = levelReader.getBlockState(blockPos2);
        if (blockState2.is(BlockTags.MUSHROOM_GROW_BLOCK)) {
            return true;
        }
        return levelReader.getRawBrightness(blockPos, 0) < 13 && this.mayPlaceOn(blockState2, levelReader, blockPos2);
    }

    public boolean growMushroom(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState, Random random) {
        ConfiguredFeature<?, ?> configuredFeature;
        serverLevel.removeBlock(blockPos, false);
        if (this == Blocks.BROWN_MUSHROOM) {
            configuredFeature = Features.HUGE_BROWN_MUSHROOM;
        } else if (this == Blocks.RED_MUSHROOM) {
            configuredFeature = Features.HUGE_RED_MUSHROOM;
        } else {
            serverLevel.setBlock(blockPos, blockState, 3);
            return false;
        }
        if (configuredFeature.place(serverLevel, serverLevel.getChunkSource().getGenerator(), random, blockPos)) {
            return true;
        }
        serverLevel.setBlock(blockPos, blockState, 3);
        return false;
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, boolean bl) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, Random random, BlockPos blockPos, BlockState blockState) {
        return (double)random.nextFloat() < 0.4;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, Random random, BlockPos blockPos, BlockState blockState) {
        this.growMushroom(serverLevel, blockPos, blockState, random);
    }
}

