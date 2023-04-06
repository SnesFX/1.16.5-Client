/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SugarCaneBlock
extends Block {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
    protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    protected SugarCaneBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        if (!blockState.canSurvive(serverLevel, blockPos)) {
            serverLevel.destroyBlock(blockPos, true);
        }
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        if (serverLevel.isEmptyBlock(blockPos.above())) {
            int n = 1;
            while (serverLevel.getBlockState(blockPos.below(n)).is(this)) {
                ++n;
            }
            if (n < 3) {
                int n2 = blockState.getValue(AGE);
                if (n2 == 15) {
                    serverLevel.setBlockAndUpdate(blockPos.above(), this.defaultBlockState());
                    serverLevel.setBlock(blockPos, (BlockState)blockState.setValue(AGE, 0), 4);
                } else {
                    serverLevel.setBlock(blockPos, (BlockState)blockState.setValue(AGE, n2 + 1), 4);
                }
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (!blockState.canSurvive(levelAccessor, blockPos)) {
            levelAccessor.getBlockTicks().scheduleTick(blockPos, this, 1);
        }
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        BlockState blockState2 = levelReader.getBlockState(blockPos.below());
        if (blockState2.getBlock() == this) {
            return true;
        }
        if (blockState2.is(Blocks.GRASS_BLOCK) || blockState2.is(Blocks.DIRT) || blockState2.is(Blocks.COARSE_DIRT) || blockState2.is(Blocks.PODZOL) || blockState2.is(Blocks.SAND) || blockState2.is(Blocks.RED_SAND)) {
            BlockPos blockPos2 = blockPos.below();
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockState blockState3 = levelReader.getBlockState(blockPos2.relative(direction));
                FluidState fluidState = levelReader.getFluidState(blockPos2.relative(direction));
                if (!fluidState.is(FluidTags.WATER) && !blockState3.is(Blocks.FROSTED_ICE)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}

