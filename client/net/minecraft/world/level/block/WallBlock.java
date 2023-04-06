/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallBlock
extends Block
implements SimpleWaterloggedBlock {
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final EnumProperty<WallSide> EAST_WALL = BlockStateProperties.EAST_WALL;
    public static final EnumProperty<WallSide> NORTH_WALL = BlockStateProperties.NORTH_WALL;
    public static final EnumProperty<WallSide> SOUTH_WALL = BlockStateProperties.SOUTH_WALL;
    public static final EnumProperty<WallSide> WEST_WALL = BlockStateProperties.WEST_WALL;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final Map<BlockState, VoxelShape> shapeByIndex;
    private final Map<BlockState, VoxelShape> collisionShapeByIndex;
    private static final VoxelShape POST_TEST = Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0);
    private static final VoxelShape NORTH_TEST = Block.box(7.0, 0.0, 0.0, 9.0, 16.0, 9.0);
    private static final VoxelShape SOUTH_TEST = Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 16.0);
    private static final VoxelShape WEST_TEST = Block.box(0.0, 0.0, 7.0, 9.0, 16.0, 9.0);
    private static final VoxelShape EAST_TEST = Block.box(7.0, 0.0, 7.0, 16.0, 16.0, 9.0);

    public WallBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(UP, true)).setValue(NORTH_WALL, WallSide.NONE)).setValue(EAST_WALL, WallSide.NONE)).setValue(SOUTH_WALL, WallSide.NONE)).setValue(WEST_WALL, WallSide.NONE)).setValue(WATERLOGGED, false));
        this.shapeByIndex = this.makeShapes(4.0f, 3.0f, 16.0f, 0.0f, 14.0f, 16.0f);
        this.collisionShapeByIndex = this.makeShapes(4.0f, 3.0f, 24.0f, 0.0f, 24.0f, 24.0f);
    }

    private static VoxelShape applyWallShape(VoxelShape voxelShape, WallSide wallSide, VoxelShape voxelShape2, VoxelShape voxelShape3) {
        if (wallSide == WallSide.TALL) {
            return Shapes.or(voxelShape, voxelShape3);
        }
        if (wallSide == WallSide.LOW) {
            return Shapes.or(voxelShape, voxelShape2);
        }
        return voxelShape;
    }

    private Map<BlockState, VoxelShape> makeShapes(float f, float f2, float f3, float f4, float f5, float f6) {
        float f7 = 8.0f - f;
        float f8 = 8.0f + f;
        float f9 = 8.0f - f2;
        float f10 = 8.0f + f2;
        VoxelShape voxelShape = Block.box(f7, 0.0, f7, f8, f3, f8);
        VoxelShape voxelShape2 = Block.box(f9, f4, 0.0, f10, f5, f10);
        VoxelShape voxelShape3 = Block.box(f9, f4, f9, f10, f5, 16.0);
        VoxelShape voxelShape4 = Block.box(0.0, f4, f9, f10, f5, f10);
        VoxelShape voxelShape5 = Block.box(f9, f4, f9, 16.0, f5, f10);
        VoxelShape voxelShape6 = Block.box(f9, f4, 0.0, f10, f6, f10);
        VoxelShape voxelShape7 = Block.box(f9, f4, f9, f10, f6, 16.0);
        VoxelShape voxelShape8 = Block.box(0.0, f4, f9, f10, f6, f10);
        VoxelShape voxelShape9 = Block.box(f9, f4, f9, 16.0, f6, f10);
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (Boolean bl : UP.getPossibleValues()) {
            for (WallSide wallSide : EAST_WALL.getPossibleValues()) {
                for (WallSide wallSide2 : NORTH_WALL.getPossibleValues()) {
                    for (WallSide wallSide3 : WEST_WALL.getPossibleValues()) {
                        for (WallSide wallSide4 : SOUTH_WALL.getPossibleValues()) {
                            VoxelShape voxelShape10 = Shapes.empty();
                            voxelShape10 = WallBlock.applyWallShape(voxelShape10, wallSide, voxelShape5, voxelShape9);
                            voxelShape10 = WallBlock.applyWallShape(voxelShape10, wallSide3, voxelShape4, voxelShape8);
                            voxelShape10 = WallBlock.applyWallShape(voxelShape10, wallSide2, voxelShape2, voxelShape6);
                            voxelShape10 = WallBlock.applyWallShape(voxelShape10, wallSide4, voxelShape3, voxelShape7);
                            if (bl.booleanValue()) {
                                voxelShape10 = Shapes.or(voxelShape10, voxelShape);
                            }
                            BlockState blockState = (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(UP, bl)).setValue(EAST_WALL, wallSide)).setValue(WEST_WALL, wallSide3)).setValue(NORTH_WALL, wallSide2)).setValue(SOUTH_WALL, wallSide4);
                            builder.put(blockState.setValue(WATERLOGGED, false), (Object)voxelShape10);
                            builder.put(blockState.setValue(WATERLOGGED, true), (Object)voxelShape10);
                        }
                    }
                }
            }
        }
        return builder.build();
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return this.shapeByIndex.get(blockState);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return this.collisionShapeByIndex.get(blockState);
    }

    @Override
    public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
        return false;
    }

    private boolean connectsTo(BlockState blockState, boolean bl, Direction direction) {
        Block block = blockState.getBlock();
        boolean bl2 = block instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(blockState, direction);
        return blockState.is(BlockTags.WALLS) || !WallBlock.isExceptionForConnection(block) && bl || block instanceof IronBarsBlock || bl2;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        Level level = blockPlaceContext.getLevel();
        BlockPos blockPos = blockPlaceContext.getClickedPos();
        FluidState fluidState = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());
        BlockPos blockPos2 = blockPos.north();
        BlockPos blockPos3 = blockPos.east();
        BlockPos blockPos4 = blockPos.south();
        BlockPos blockPos5 = blockPos.west();
        BlockPos blockPos6 = blockPos.above();
        BlockState blockState = level.getBlockState(blockPos2);
        BlockState blockState2 = level.getBlockState(blockPos3);
        BlockState blockState3 = level.getBlockState(blockPos4);
        BlockState blockState4 = level.getBlockState(blockPos5);
        BlockState blockState5 = level.getBlockState(blockPos6);
        boolean bl = this.connectsTo(blockState, blockState.isFaceSturdy(level, blockPos2, Direction.SOUTH), Direction.SOUTH);
        boolean bl2 = this.connectsTo(blockState2, blockState2.isFaceSturdy(level, blockPos3, Direction.WEST), Direction.WEST);
        boolean bl3 = this.connectsTo(blockState3, blockState3.isFaceSturdy(level, blockPos4, Direction.NORTH), Direction.NORTH);
        boolean bl4 = this.connectsTo(blockState4, blockState4.isFaceSturdy(level, blockPos5, Direction.EAST), Direction.EAST);
        BlockState blockState6 = (BlockState)this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        return this.updateShape(level, blockState6, blockPos6, blockState5, bl, bl2, bl3, bl4);
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (blockState.getValue(WATERLOGGED).booleanValue()) {
            levelAccessor.getLiquidTicks().scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }
        if (direction == Direction.DOWN) {
            return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
        }
        if (direction == Direction.UP) {
            return this.topUpdate(levelAccessor, blockState, blockPos2, blockState2);
        }
        return this.sideUpdate(levelAccessor, blockPos, blockState, blockPos2, blockState2, direction);
    }

    private static boolean isConnected(BlockState blockState, Property<WallSide> property) {
        return blockState.getValue(property) != WallSide.NONE;
    }

    private static boolean isCovered(VoxelShape voxelShape, VoxelShape voxelShape2) {
        return !Shapes.joinIsNotEmpty(voxelShape2, voxelShape, BooleanOp.ONLY_FIRST);
    }

    private BlockState topUpdate(LevelReader levelReader, BlockState blockState, BlockPos blockPos, BlockState blockState2) {
        boolean bl = WallBlock.isConnected(blockState, NORTH_WALL);
        boolean bl2 = WallBlock.isConnected(blockState, EAST_WALL);
        boolean bl3 = WallBlock.isConnected(blockState, SOUTH_WALL);
        boolean bl4 = WallBlock.isConnected(blockState, WEST_WALL);
        return this.updateShape(levelReader, blockState, blockPos, blockState2, bl, bl2, bl3, bl4);
    }

    private BlockState sideUpdate(LevelReader levelReader, BlockPos blockPos, BlockState blockState, BlockPos blockPos2, BlockState blockState2, Direction direction) {
        Direction direction2 = direction.getOpposite();
        boolean bl = direction == Direction.NORTH ? this.connectsTo(blockState2, blockState2.isFaceSturdy(levelReader, blockPos2, direction2), direction2) : WallBlock.isConnected(blockState, NORTH_WALL);
        boolean bl2 = direction == Direction.EAST ? this.connectsTo(blockState2, blockState2.isFaceSturdy(levelReader, blockPos2, direction2), direction2) : WallBlock.isConnected(blockState, EAST_WALL);
        boolean bl3 = direction == Direction.SOUTH ? this.connectsTo(blockState2, blockState2.isFaceSturdy(levelReader, blockPos2, direction2), direction2) : WallBlock.isConnected(blockState, SOUTH_WALL);
        boolean bl4 = direction == Direction.WEST ? this.connectsTo(blockState2, blockState2.isFaceSturdy(levelReader, blockPos2, direction2), direction2) : WallBlock.isConnected(blockState, WEST_WALL);
        BlockPos blockPos3 = blockPos.above();
        BlockState blockState3 = levelReader.getBlockState(blockPos3);
        return this.updateShape(levelReader, blockState, blockPos3, blockState3, bl, bl2, bl3, bl4);
    }

    private BlockState updateShape(LevelReader levelReader, BlockState blockState, BlockPos blockPos, BlockState blockState2, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        VoxelShape voxelShape = blockState2.getCollisionShape(levelReader, blockPos).getFaceShape(Direction.DOWN);
        BlockState blockState3 = this.updateSides(blockState, bl, bl2, bl3, bl4, voxelShape);
        return (BlockState)blockState3.setValue(UP, this.shouldRaisePost(blockState3, blockState2, voxelShape));
    }

    private boolean shouldRaisePost(BlockState blockState, BlockState blockState2, VoxelShape voxelShape) {
        boolean bl;
        boolean bl2;
        boolean bl3;
        boolean bl4 = bl2 = blockState2.getBlock() instanceof WallBlock && blockState2.getValue(UP) != false;
        if (bl2) {
            return true;
        }
        WallSide wallSide = blockState.getValue(NORTH_WALL);
        WallSide wallSide2 = blockState.getValue(SOUTH_WALL);
        WallSide wallSide3 = blockState.getValue(EAST_WALL);
        WallSide wallSide4 = blockState.getValue(WEST_WALL);
        boolean bl5 = wallSide2 == WallSide.NONE;
        boolean bl6 = wallSide4 == WallSide.NONE;
        boolean bl7 = wallSide3 == WallSide.NONE;
        boolean bl8 = wallSide == WallSide.NONE;
        boolean bl9 = bl3 = bl8 && bl5 && bl6 && bl7 || bl8 != bl5 || bl6 != bl7;
        if (bl3) {
            return true;
        }
        boolean bl10 = bl = wallSide == WallSide.TALL && wallSide2 == WallSide.TALL || wallSide3 == WallSide.TALL && wallSide4 == WallSide.TALL;
        if (bl) {
            return false;
        }
        return blockState2.getBlock().is(BlockTags.WALL_POST_OVERRIDE) || WallBlock.isCovered(voxelShape, POST_TEST);
    }

    private BlockState updateSides(BlockState blockState, boolean bl, boolean bl2, boolean bl3, boolean bl4, VoxelShape voxelShape) {
        return (BlockState)((BlockState)((BlockState)((BlockState)blockState.setValue(NORTH_WALL, this.makeWallState(bl, voxelShape, NORTH_TEST))).setValue(EAST_WALL, this.makeWallState(bl2, voxelShape, EAST_TEST))).setValue(SOUTH_WALL, this.makeWallState(bl3, voxelShape, SOUTH_TEST))).setValue(WEST_WALL, this.makeWallState(bl4, voxelShape, WEST_TEST));
    }

    private WallSide makeWallState(boolean bl, VoxelShape voxelShape, VoxelShape voxelShape2) {
        if (bl) {
            if (WallBlock.isCovered(voxelShape, voxelShape2)) {
                return WallSide.TALL;
            }
            return WallSide.LOW;
        }
        return WallSide.NONE;
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        if (blockState.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState(blockState);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return blockState.getValue(WATERLOGGED) == false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UP, NORTH_WALL, EAST_WALL, WEST_WALL, SOUTH_WALL, WATERLOGGED);
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)blockState.setValue(NORTH_WALL, blockState.getValue(SOUTH_WALL))).setValue(EAST_WALL, blockState.getValue(WEST_WALL))).setValue(SOUTH_WALL, blockState.getValue(NORTH_WALL))).setValue(WEST_WALL, blockState.getValue(EAST_WALL));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)blockState.setValue(NORTH_WALL, blockState.getValue(EAST_WALL))).setValue(EAST_WALL, blockState.getValue(SOUTH_WALL))).setValue(SOUTH_WALL, blockState.getValue(WEST_WALL))).setValue(WEST_WALL, blockState.getValue(NORTH_WALL));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)blockState.setValue(NORTH_WALL, blockState.getValue(WEST_WALL))).setValue(EAST_WALL, blockState.getValue(NORTH_WALL))).setValue(SOUTH_WALL, blockState.getValue(EAST_WALL))).setValue(WEST_WALL, blockState.getValue(SOUTH_WALL));
            }
        }
        return blockState;
    }

    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)blockState.setValue(NORTH_WALL, blockState.getValue(SOUTH_WALL))).setValue(SOUTH_WALL, blockState.getValue(NORTH_WALL));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)blockState.setValue(EAST_WALL, blockState.getValue(WEST_WALL))).setValue(WEST_WALL, blockState.getValue(EAST_WALL));
            }
        }
        return super.mirror(blockState, mirror);
    }

}

