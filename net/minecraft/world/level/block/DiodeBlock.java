/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerTickList;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickPriority;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class DiodeBlock
extends HorizontalDirectionalBlock {
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    protected DiodeBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return DiodeBlock.canSupportRigidBlock(levelReader, blockPos.below());
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        if (this.isLocked(serverLevel, blockPos, blockState)) {
            return;
        }
        boolean bl = blockState.getValue(POWERED);
        boolean bl2 = this.shouldTurnOn(serverLevel, blockPos, blockState);
        if (bl && !bl2) {
            serverLevel.setBlock(blockPos, (BlockState)blockState.setValue(POWERED, false), 2);
        } else if (!bl) {
            serverLevel.setBlock(blockPos, (BlockState)blockState.setValue(POWERED, true), 2);
            if (!bl2) {
                ((ServerTickList)serverLevel.getBlockTicks()).scheduleTick(blockPos, this, this.getDelay(blockState), TickPriority.VERY_HIGH);
            }
        }
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return blockState.getSignal(blockGetter, blockPos, direction);
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        if (!blockState.getValue(POWERED).booleanValue()) {
            return 0;
        }
        if (blockState.getValue(FACING) == direction) {
            return this.getOutputSignal(blockGetter, blockPos, blockState);
        }
        return 0;
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        if (blockState.canSurvive(level, blockPos)) {
            this.checkTickOnNeighbor(level, blockPos, blockState);
            return;
        }
        BlockEntity blockEntity = this.isEntityBlock() ? level.getBlockEntity(blockPos) : null;
        DiodeBlock.dropResources(blockState, level, blockPos, blockEntity);
        level.removeBlock(blockPos, false);
        for (Direction direction : Direction.values()) {
            level.updateNeighborsAt(blockPos.relative(direction), this);
        }
    }

    protected void checkTickOnNeighbor(Level level, BlockPos blockPos, BlockState blockState) {
        boolean bl;
        if (this.isLocked(level, blockPos, blockState)) {
            return;
        }
        boolean bl2 = blockState.getValue(POWERED);
        if (bl2 != (bl = this.shouldTurnOn(level, blockPos, blockState)) && !level.getBlockTicks().willTickThisTick(blockPos, this)) {
            TickPriority tickPriority = TickPriority.HIGH;
            if (this.shouldPrioritize(level, blockPos, blockState)) {
                tickPriority = TickPriority.EXTREMELY_HIGH;
            } else if (bl2) {
                tickPriority = TickPriority.VERY_HIGH;
            }
            level.getBlockTicks().scheduleTick(blockPos, this, this.getDelay(blockState), tickPriority);
        }
    }

    public boolean isLocked(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return false;
    }

    protected boolean shouldTurnOn(Level level, BlockPos blockPos, BlockState blockState) {
        return this.getInputSignal(level, blockPos, blockState) > 0;
    }

    protected int getInputSignal(Level level, BlockPos blockPos, BlockState blockState) {
        Direction direction = blockState.getValue(FACING);
        BlockPos blockPos2 = blockPos.relative(direction);
        int n = level.getSignal(blockPos2, direction);
        if (n >= 15) {
            return n;
        }
        BlockState blockState2 = level.getBlockState(blockPos2);
        return Math.max(n, blockState2.is(Blocks.REDSTONE_WIRE) ? blockState2.getValue(RedStoneWireBlock.POWER) : 0);
    }

    protected int getAlternateSignal(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        Direction direction = blockState.getValue(FACING);
        Direction direction2 = direction.getClockWise();
        Direction direction3 = direction.getCounterClockWise();
        return Math.max(this.getAlternateSignalAt(levelReader, blockPos.relative(direction2), direction2), this.getAlternateSignalAt(levelReader, blockPos.relative(direction3), direction3));
    }

    protected int getAlternateSignalAt(LevelReader levelReader, BlockPos blockPos, Direction direction) {
        BlockState blockState = levelReader.getBlockState(blockPos);
        if (this.isAlternateInput(blockState)) {
            if (blockState.is(Blocks.REDSTONE_BLOCK)) {
                return 15;
            }
            if (blockState.is(Blocks.REDSTONE_WIRE)) {
                return blockState.getValue(RedStoneWireBlock.POWER);
            }
            return levelReader.getDirectSignal(blockPos, direction);
        }
        return 0;
    }

    @Override
    public boolean isSignalSource(BlockState blockState) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return (BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
        if (this.shouldTurnOn(level, blockPos, blockState)) {
            level.getBlockTicks().scheduleTick(blockPos, this, 1);
        }
    }

    @Override
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        this.updateNeighborsInFront(level, blockPos, blockState);
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (bl || blockState.is(blockState2.getBlock())) {
            return;
        }
        super.onRemove(blockState, level, blockPos, blockState2, bl);
        this.updateNeighborsInFront(level, blockPos, blockState);
    }

    protected void updateNeighborsInFront(Level level, BlockPos blockPos, BlockState blockState) {
        Direction direction = blockState.getValue(FACING);
        BlockPos blockPos2 = blockPos.relative(direction.getOpposite());
        level.neighborChanged(blockPos2, this, blockPos);
        level.updateNeighborsAtExceptFromFacing(blockPos2, this, direction);
    }

    protected boolean isAlternateInput(BlockState blockState) {
        return blockState.isSignalSource();
    }

    protected int getOutputSignal(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        return 15;
    }

    public static boolean isDiode(BlockState blockState) {
        return blockState.getBlock() instanceof DiodeBlock;
    }

    public boolean shouldPrioritize(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        Direction direction = blockState.getValue(FACING).getOpposite();
        BlockState blockState2 = blockGetter.getBlockState(blockPos.relative(direction));
        return DiodeBlock.isDiode(blockState2) && blockState2.getValue(FACING) != direction;
    }

    protected abstract int getDelay(BlockState var1);
}

