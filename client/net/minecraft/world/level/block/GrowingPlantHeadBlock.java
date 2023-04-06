/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class GrowingPlantHeadBlock
extends GrowingPlantBlock
implements BonemealableBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_25;
    private final double growPerTickProbability;

    protected GrowingPlantHeadBlock(BlockBehaviour.Properties properties, Direction direction, VoxelShape voxelShape, boolean bl, double d) {
        super(properties, direction, voxelShape, bl);
        this.growPerTickProbability = d;
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
    }

    @Override
    public BlockState getStateForPlacement(LevelAccessor levelAccessor) {
        return (BlockState)this.defaultBlockState().setValue(AGE, levelAccessor.getRandom().nextInt(25));
    }

    @Override
    public boolean isRandomlyTicking(BlockState blockState) {
        return blockState.getValue(AGE) < 25;
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        BlockPos blockPos2;
        if (blockState.getValue(AGE) < 25 && random.nextDouble() < this.growPerTickProbability && this.canGrowInto(serverLevel.getBlockState(blockPos2 = blockPos.relative(this.growthDirection)))) {
            serverLevel.setBlockAndUpdate(blockPos2, (BlockState)blockState.cycle(AGE));
        }
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (direction == this.growthDirection.getOpposite() && !blockState.canSurvive(levelAccessor, blockPos)) {
            levelAccessor.getBlockTicks().scheduleTick(blockPos, this, 1);
        }
        if (direction == this.growthDirection && (blockState2.is(this) || blockState2.is(this.getBodyBlock()))) {
            return this.getBodyBlock().defaultBlockState();
        }
        if (this.scheduleFluidTicks) {
            levelAccessor.getLiquidTicks().scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, boolean bl) {
        return this.canGrowInto(blockGetter.getBlockState(blockPos.relative(this.growthDirection)));
    }

    @Override
    public boolean isBonemealSuccess(Level level, Random random, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, Random random, BlockPos blockPos, BlockState blockState) {
        BlockPos blockPos2 = blockPos.relative(this.growthDirection);
        int n = Math.min(blockState.getValue(AGE) + 1, 25);
        int n2 = this.getBlocksToGrowWhenBonemealed(random);
        for (int i = 0; i < n2 && this.canGrowInto(serverLevel.getBlockState(blockPos2)); ++i) {
            serverLevel.setBlockAndUpdate(blockPos2, (BlockState)blockState.setValue(AGE, n));
            blockPos2 = blockPos2.relative(this.growthDirection);
            n = Math.min(n + 1, 25);
        }
    }

    protected abstract int getBlocksToGrowWhenBonemealed(Random var1);

    protected abstract boolean canGrowInto(BlockState var1);

    @Override
    protected GrowingPlantHeadBlock getHeadBlock() {
        return this;
    }
}

