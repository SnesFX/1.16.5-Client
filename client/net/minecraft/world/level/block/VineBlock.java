/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VineBlock
extends Block {
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter(entry -> entry.getKey() != Direction.DOWN).collect(Util.toMap());
    private static final VoxelShape UP_AABB = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape WEST_AABB = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    private static final VoxelShape EAST_AABB = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    private static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
    private final Map<BlockState, VoxelShape> shapesCache;

    public VineBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(UP, false)).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false));
        this.shapesCache = ImmutableMap.copyOf(this.stateDefinition.getPossibleStates().stream().collect(Collectors.toMap(Function.identity(), VineBlock::calculateShape)));
    }

    private static VoxelShape calculateShape(BlockState blockState) {
        VoxelShape voxelShape = Shapes.empty();
        if (blockState.getValue(UP).booleanValue()) {
            voxelShape = UP_AABB;
        }
        if (blockState.getValue(NORTH).booleanValue()) {
            voxelShape = Shapes.or(voxelShape, NORTH_AABB);
        }
        if (blockState.getValue(SOUTH).booleanValue()) {
            voxelShape = Shapes.or(voxelShape, SOUTH_AABB);
        }
        if (blockState.getValue(EAST).booleanValue()) {
            voxelShape = Shapes.or(voxelShape, EAST_AABB);
        }
        if (blockState.getValue(WEST).booleanValue()) {
            voxelShape = Shapes.or(voxelShape, WEST_AABB);
        }
        return voxelShape;
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return this.shapesCache.get(blockState);
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return this.hasFaces(this.getUpdatedState(blockState, levelReader, blockPos));
    }

    private boolean hasFaces(BlockState blockState) {
        return this.countFaces(blockState) > 0;
    }

    private int countFaces(BlockState blockState) {
        int n = 0;
        for (BooleanProperty booleanProperty : PROPERTY_BY_DIRECTION.values()) {
            if (!blockState.getValue(booleanProperty).booleanValue()) continue;
            ++n;
        }
        return n;
    }

    private boolean canSupportAtFace(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        if (direction == Direction.DOWN) {
            return false;
        }
        BlockPos blockPos2 = blockPos.relative(direction);
        if (VineBlock.isAcceptableNeighbour(blockGetter, blockPos2, direction)) {
            return true;
        }
        if (direction.getAxis() != Direction.Axis.Y) {
            BooleanProperty booleanProperty = PROPERTY_BY_DIRECTION.get(direction);
            BlockState blockState = blockGetter.getBlockState(blockPos.above());
            return blockState.is(this) && blockState.getValue(booleanProperty) != false;
        }
        return false;
    }

    public static boolean isAcceptableNeighbour(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        BlockState blockState = blockGetter.getBlockState(blockPos);
        return Block.isFaceFull(blockState.getCollisionShape(blockGetter, blockPos), direction.getOpposite());
    }

    private BlockState getUpdatedState(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.above();
        if (blockState.getValue(UP).booleanValue()) {
            blockState = (BlockState)blockState.setValue(UP, VineBlock.isAcceptableNeighbour(blockGetter, blockPos2, Direction.DOWN));
        }
        BlockBehaviour.BlockStateBase blockStateBase = null;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BooleanProperty booleanProperty = VineBlock.getPropertyForFace(direction);
            if (!blockState.getValue(booleanProperty).booleanValue()) continue;
            boolean bl = this.canSupportAtFace(blockGetter, blockPos, direction);
            if (!bl) {
                if (blockStateBase == null) {
                    blockStateBase = blockGetter.getBlockState(blockPos2);
                }
                bl = blockStateBase.is(this) && blockStateBase.getValue(booleanProperty) != false;
            }
            blockState = (BlockState)blockState.setValue(booleanProperty, bl);
        }
        return blockState;
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (direction == Direction.DOWN) {
            return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
        }
        BlockState blockState3 = this.getUpdatedState(blockState, levelAccessor, blockPos);
        if (!this.hasFaces(blockState3)) {
            return Blocks.AIR.defaultBlockState();
        }
        return blockState3;
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        BlockPos blockPos2;
        BlockState blockState2;
        BlockState blockState3;
        BlockState blockState4;
        if (serverLevel.random.nextInt(4) != 0) {
            return;
        }
        Direction direction = Direction.getRandom(random);
        BlockPos blockPos3 = blockPos.above();
        if (direction.getAxis().isHorizontal() && !blockState.getValue(VineBlock.getPropertyForFace(direction)).booleanValue()) {
            if (!this.canSpread(serverLevel, blockPos)) {
                return;
            }
            BlockPos blockPos4 = blockPos.relative(direction);
            BlockState blockState5 = serverLevel.getBlockState(blockPos4);
            if (blockState5.isAir()) {
                Direction direction2 = direction.getClockWise();
                Direction direction3 = direction.getCounterClockWise();
                boolean bl = blockState.getValue(VineBlock.getPropertyForFace(direction2));
                boolean bl2 = blockState.getValue(VineBlock.getPropertyForFace(direction3));
                BlockPos blockPos5 = blockPos4.relative(direction2);
                BlockPos blockPos6 = blockPos4.relative(direction3);
                if (bl && VineBlock.isAcceptableNeighbour(serverLevel, blockPos5, direction2)) {
                    serverLevel.setBlock(blockPos4, (BlockState)this.defaultBlockState().setValue(VineBlock.getPropertyForFace(direction2), true), 2);
                } else if (bl2 && VineBlock.isAcceptableNeighbour(serverLevel, blockPos6, direction3)) {
                    serverLevel.setBlock(blockPos4, (BlockState)this.defaultBlockState().setValue(VineBlock.getPropertyForFace(direction3), true), 2);
                } else {
                    Direction direction4 = direction.getOpposite();
                    if (bl && serverLevel.isEmptyBlock(blockPos5) && VineBlock.isAcceptableNeighbour(serverLevel, blockPos.relative(direction2), direction4)) {
                        serverLevel.setBlock(blockPos5, (BlockState)this.defaultBlockState().setValue(VineBlock.getPropertyForFace(direction4), true), 2);
                    } else if (bl2 && serverLevel.isEmptyBlock(blockPos6) && VineBlock.isAcceptableNeighbour(serverLevel, blockPos.relative(direction3), direction4)) {
                        serverLevel.setBlock(blockPos6, (BlockState)this.defaultBlockState().setValue(VineBlock.getPropertyForFace(direction4), true), 2);
                    } else if ((double)serverLevel.random.nextFloat() < 0.05 && VineBlock.isAcceptableNeighbour(serverLevel, blockPos4.above(), Direction.UP)) {
                        serverLevel.setBlock(blockPos4, (BlockState)this.defaultBlockState().setValue(UP, true), 2);
                    }
                }
            } else if (VineBlock.isAcceptableNeighbour(serverLevel, blockPos4, direction)) {
                serverLevel.setBlock(blockPos, (BlockState)blockState.setValue(VineBlock.getPropertyForFace(direction), true), 2);
            }
            return;
        }
        if (direction == Direction.UP && blockPos.getY() < 255) {
            if (this.canSupportAtFace(serverLevel, blockPos, direction)) {
                serverLevel.setBlock(blockPos, (BlockState)blockState.setValue(UP, true), 2);
                return;
            }
            if (serverLevel.isEmptyBlock(blockPos3)) {
                if (!this.canSpread(serverLevel, blockPos)) {
                    return;
                }
                BlockState blockState6 = blockState;
                for (Direction direction5 : Direction.Plane.HORIZONTAL) {
                    if (!random.nextBoolean() && VineBlock.isAcceptableNeighbour(serverLevel, blockPos3.relative(direction5), Direction.UP)) continue;
                    blockState6 = (BlockState)blockState6.setValue(VineBlock.getPropertyForFace(direction5), false);
                }
                if (this.hasHorizontalConnection(blockState6)) {
                    serverLevel.setBlock(blockPos3, blockState6, 2);
                }
                return;
            }
        }
        if (blockPos.getY() > 0 && ((blockState3 = serverLevel.getBlockState(blockPos2 = blockPos.below())).isAir() || blockState3.is(this)) && (blockState4 = blockState3.isAir() ? this.defaultBlockState() : blockState3) != (blockState2 = this.copyRandomFaces(blockState, blockState4, random)) && this.hasHorizontalConnection(blockState2)) {
            serverLevel.setBlock(blockPos2, blockState2, 2);
        }
    }

    private BlockState copyRandomFaces(BlockState blockState, BlockState blockState2, Random random) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BooleanProperty booleanProperty;
            if (!random.nextBoolean() || !blockState.getValue(booleanProperty = VineBlock.getPropertyForFace(direction)).booleanValue()) continue;
            blockState2 = (BlockState)blockState2.setValue(booleanProperty, true);
        }
        return blockState2;
    }

    private boolean hasHorizontalConnection(BlockState blockState) {
        return blockState.getValue(NORTH) != false || blockState.getValue(EAST) != false || blockState.getValue(SOUTH) != false || blockState.getValue(WEST) != false;
    }

    private boolean canSpread(BlockGetter blockGetter, BlockPos blockPos) {
        int n = 4;
        Iterable<BlockPos> iterable = BlockPos.betweenClosed(blockPos.getX() - 4, blockPos.getY() - 1, blockPos.getZ() - 4, blockPos.getX() + 4, blockPos.getY() + 1, blockPos.getZ() + 4);
        int n2 = 5;
        for (BlockPos blockPos2 : iterable) {
            if (!blockGetter.getBlockState(blockPos2).is(this) || --n2 > 0) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        BlockState blockState2 = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos());
        if (blockState2.is(this)) {
            return this.countFaces(blockState2) < PROPERTY_BY_DIRECTION.size();
        }
        return super.canBeReplaced(blockState, blockPlaceContext);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        BlockState blockState = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos());
        boolean bl = blockState.is(this);
        BlockState blockState2 = bl ? blockState : this.defaultBlockState();
        for (Direction direction : blockPlaceContext.getNearestLookingDirections()) {
            boolean bl2;
            if (direction == Direction.DOWN) continue;
            BooleanProperty booleanProperty = VineBlock.getPropertyForFace(direction);
            boolean bl3 = bl2 = bl && blockState.getValue(booleanProperty) != false;
            if (bl2 || !this.canSupportAtFace(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos(), direction)) continue;
            return (BlockState)blockState2.setValue(booleanProperty, true);
        }
        return bl ? blockState2 : null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UP, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180: {
                return (BlockState)((BlockState)((BlockState)((BlockState)blockState.setValue(NORTH, blockState.getValue(SOUTH))).setValue(EAST, blockState.getValue(WEST))).setValue(SOUTH, blockState.getValue(NORTH))).setValue(WEST, blockState.getValue(EAST));
            }
            case COUNTERCLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)blockState.setValue(NORTH, blockState.getValue(EAST))).setValue(EAST, blockState.getValue(SOUTH))).setValue(SOUTH, blockState.getValue(WEST))).setValue(WEST, blockState.getValue(NORTH));
            }
            case CLOCKWISE_90: {
                return (BlockState)((BlockState)((BlockState)((BlockState)blockState.setValue(NORTH, blockState.getValue(WEST))).setValue(EAST, blockState.getValue(NORTH))).setValue(SOUTH, blockState.getValue(EAST))).setValue(WEST, blockState.getValue(SOUTH));
            }
        }
        return blockState;
    }

    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT: {
                return (BlockState)((BlockState)blockState.setValue(NORTH, blockState.getValue(SOUTH))).setValue(SOUTH, blockState.getValue(NORTH));
            }
            case FRONT_BACK: {
                return (BlockState)((BlockState)blockState.setValue(EAST, blockState.getValue(WEST))).setValue(WEST, blockState.getValue(EAST));
            }
        }
        return super.mirror(blockState, mirror);
    }

    public static BooleanProperty getPropertyForFace(Direction direction) {
        return PROPERTY_BY_DIRECTION.get(direction);
    }

}

