/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;

public class PoweredRailBlock
extends BaseRailBlock {
    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    protected PoweredRailBlock(BlockBehaviour.Properties properties) {
        super(true, properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(SHAPE, RailShape.NORTH_SOUTH)).setValue(POWERED, false));
    }

    protected boolean findPoweredRailSignal(Level level, BlockPos blockPos, BlockState blockState, boolean bl, int n) {
        if (n >= 8) {
            return false;
        }
        int n2 = blockPos.getX();
        int n3 = blockPos.getY();
        int n4 = blockPos.getZ();
        boolean bl2 = true;
        RailShape railShape = blockState.getValue(SHAPE);
        switch (railShape) {
            case NORTH_SOUTH: {
                if (bl) {
                    ++n4;
                    break;
                }
                --n4;
                break;
            }
            case EAST_WEST: {
                if (bl) {
                    --n2;
                    break;
                }
                ++n2;
                break;
            }
            case ASCENDING_EAST: {
                if (bl) {
                    --n2;
                } else {
                    ++n2;
                    ++n3;
                    bl2 = false;
                }
                railShape = RailShape.EAST_WEST;
                break;
            }
            case ASCENDING_WEST: {
                if (bl) {
                    --n2;
                    ++n3;
                    bl2 = false;
                } else {
                    ++n2;
                }
                railShape = RailShape.EAST_WEST;
                break;
            }
            case ASCENDING_NORTH: {
                if (bl) {
                    ++n4;
                } else {
                    --n4;
                    ++n3;
                    bl2 = false;
                }
                railShape = RailShape.NORTH_SOUTH;
                break;
            }
            case ASCENDING_SOUTH: {
                if (bl) {
                    ++n4;
                    ++n3;
                    bl2 = false;
                } else {
                    --n4;
                }
                railShape = RailShape.NORTH_SOUTH;
            }
        }
        if (this.isSameRailWithPower(level, new BlockPos(n2, n3, n4), bl, n, railShape)) {
            return true;
        }
        return bl2 && this.isSameRailWithPower(level, new BlockPos(n2, n3 - 1, n4), bl, n, railShape);
    }

    protected boolean isSameRailWithPower(Level level, BlockPos blockPos, boolean bl, int n, RailShape railShape) {
        BlockState blockState = level.getBlockState(blockPos);
        if (!blockState.is(this)) {
            return false;
        }
        RailShape railShape2 = blockState.getValue(SHAPE);
        if (railShape == RailShape.EAST_WEST && (railShape2 == RailShape.NORTH_SOUTH || railShape2 == RailShape.ASCENDING_NORTH || railShape2 == RailShape.ASCENDING_SOUTH)) {
            return false;
        }
        if (railShape == RailShape.NORTH_SOUTH && (railShape2 == RailShape.EAST_WEST || railShape2 == RailShape.ASCENDING_EAST || railShape2 == RailShape.ASCENDING_WEST)) {
            return false;
        }
        if (blockState.getValue(POWERED).booleanValue()) {
            if (level.hasNeighborSignal(blockPos)) {
                return true;
            }
            return this.findPoweredRailSignal(level, blockPos, blockState, bl, n + 1);
        }
        return false;
    }

    @Override
    protected void updateState(BlockState blockState, Level level, BlockPos blockPos, Block block) {
        boolean bl;
        boolean bl2 = blockState.getValue(POWERED);
        boolean bl3 = bl = level.hasNeighborSignal(blockPos) || this.findPoweredRailSignal(level, blockPos, blockState, true, 0) || this.findPoweredRailSignal(level, blockPos, blockState, false, 0);
        if (bl != bl2) {
            level.setBlock(blockPos, (BlockState)blockState.setValue(POWERED, bl), 3);
            level.updateNeighborsAt(blockPos.below(), this);
            if (blockState.getValue(SHAPE).isAscending()) {
                level.updateNeighborsAt(blockPos.above(), this);
            }
        }
    }

    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180: {
                switch (blockState.getValue(SHAPE)) {
                    case ASCENDING_EAST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case ASCENDING_NORTH: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_WEST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_EAST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.SOUTH_EAST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.SOUTH_WEST);
                    }
                }
            }
            case COUNTERCLOCKWISE_90: {
                switch (blockState.getValue(SHAPE)) {
                    case NORTH_SOUTH: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.EAST_WEST);
                    }
                    case EAST_WEST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_SOUTH);
                    }
                    case ASCENDING_EAST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_NORTH: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_EAST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.SOUTH_EAST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_WEST);
                    }
                }
            }
            case CLOCKWISE_90: {
                switch (blockState.getValue(SHAPE)) {
                    case NORTH_SOUTH: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.EAST_WEST);
                    }
                    case EAST_WEST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_SOUTH);
                    }
                    case ASCENDING_EAST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case ASCENDING_NORTH: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_WEST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_EAST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.SOUTH_EAST);
                    }
                }
            }
        }
        return blockState;
    }

    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        RailShape railShape = blockState.getValue(SHAPE);
        switch (mirror) {
            case LEFT_RIGHT: {
                switch (railShape) {
                    case ASCENDING_NORTH: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
                    }
                    case ASCENDING_SOUTH: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_NORTH);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_EAST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_WEST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.SOUTH_EAST);
                    }
                }
                break;
            }
            case FRONT_BACK: {
                switch (railShape) {
                    case ASCENDING_EAST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_WEST);
                    }
                    case ASCENDING_WEST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.ASCENDING_EAST);
                    }
                    case SOUTH_EAST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.SOUTH_WEST);
                    }
                    case SOUTH_WEST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.SOUTH_EAST);
                    }
                    case NORTH_WEST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_EAST);
                    }
                    case NORTH_EAST: {
                        return (BlockState)blockState.setValue(SHAPE, RailShape.NORTH_WEST);
                    }
                }
                break;
            }
        }
        return super.mirror(blockState, mirror);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, POWERED);
    }

}

