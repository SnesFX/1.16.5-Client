/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RailState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BaseRailBlock
extends Block {
    protected static final VoxelShape FLAT_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    protected static final VoxelShape HALF_BLOCK_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    private final boolean isStraight;

    public static boolean isRail(Level level, BlockPos blockPos) {
        return BaseRailBlock.isRail(level.getBlockState(blockPos));
    }

    public static boolean isRail(BlockState blockState) {
        return blockState.is(BlockTags.RAILS) && blockState.getBlock() instanceof BaseRailBlock;
    }

    protected BaseRailBlock(boolean bl, BlockBehaviour.Properties properties) {
        super(properties);
        this.isStraight = bl;
    }

    public boolean isStraight() {
        return this.isStraight;
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        RailShape railShape;
        RailShape railShape2 = railShape = blockState.is(this) ? blockState.getValue(this.getShapeProperty()) : null;
        if (railShape != null && railShape.isAscending()) {
            return HALF_BLOCK_AABB;
        }
        return FLAT_AABB;
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return BaseRailBlock.canSupportRigidBlock(levelReader, blockPos.below());
    }

    @Override
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState2.is(blockState.getBlock())) {
            return;
        }
        this.updateState(blockState, level, blockPos, bl);
    }

    protected BlockState updateState(BlockState blockState, Level level, BlockPos blockPos, boolean bl) {
        blockState = this.updateDir(level, blockPos, blockState, true);
        if (this.isStraight) {
            blockState.neighborChanged(level, blockPos, this, blockPos, bl);
        }
        return blockState;
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        if (level.isClientSide || !level.getBlockState(blockPos).is(this)) {
            return;
        }
        RailShape railShape = blockState.getValue(this.getShapeProperty());
        if (BaseRailBlock.shouldBeRemoved(blockPos, level, railShape)) {
            BaseRailBlock.dropResources(blockState, level, blockPos);
            level.removeBlock(blockPos, bl);
        } else {
            this.updateState(blockState, level, blockPos, block);
        }
    }

    private static boolean shouldBeRemoved(BlockPos blockPos, Level level, RailShape railShape) {
        if (!BaseRailBlock.canSupportRigidBlock(level, blockPos.below())) {
            return true;
        }
        switch (railShape) {
            case ASCENDING_EAST: {
                return !BaseRailBlock.canSupportRigidBlock(level, blockPos.east());
            }
            case ASCENDING_WEST: {
                return !BaseRailBlock.canSupportRigidBlock(level, blockPos.west());
            }
            case ASCENDING_NORTH: {
                return !BaseRailBlock.canSupportRigidBlock(level, blockPos.north());
            }
            case ASCENDING_SOUTH: {
                return !BaseRailBlock.canSupportRigidBlock(level, blockPos.south());
            }
        }
        return false;
    }

    protected void updateState(BlockState blockState, Level level, BlockPos blockPos, Block block) {
    }

    protected BlockState updateDir(Level level, BlockPos blockPos, BlockState blockState, boolean bl) {
        if (level.isClientSide) {
            return blockState;
        }
        RailShape railShape = blockState.getValue(this.getShapeProperty());
        return new RailState(level, blockPos, blockState).place(level.hasNeighborSignal(blockPos), bl, railShape).getState();
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState blockState) {
        return PushReaction.NORMAL;
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (bl) {
            return;
        }
        super.onRemove(blockState, level, blockPos, blockState2, bl);
        if (blockState.getValue(this.getShapeProperty()).isAscending()) {
            level.updateNeighborsAt(blockPos.above(), this);
        }
        if (this.isStraight) {
            level.updateNeighborsAt(blockPos, this);
            level.updateNeighborsAt(blockPos.below(), this);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        BlockState blockState = super.defaultBlockState();
        Direction direction = blockPlaceContext.getHorizontalDirection();
        boolean bl = direction == Direction.EAST || direction == Direction.WEST;
        return (BlockState)blockState.setValue(this.getShapeProperty(), bl ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
    }

    public abstract Property<RailShape> getShapeProperty();

}

