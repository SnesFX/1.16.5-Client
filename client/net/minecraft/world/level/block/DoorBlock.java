/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DoorBlock
extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape WEST_AABB = Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape EAST_AABB = Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);

    protected DoorBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(OPEN, false)).setValue(HINGE, DoorHingeSide.LEFT)).setValue(POWERED, false)).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        Direction direction = blockState.getValue(FACING);
        boolean bl = blockState.getValue(OPEN) == false;
        boolean bl2 = blockState.getValue(HINGE) == DoorHingeSide.RIGHT;
        switch (direction) {
            default: {
                return bl ? EAST_AABB : (bl2 ? NORTH_AABB : SOUTH_AABB);
            }
            case SOUTH: {
                return bl ? SOUTH_AABB : (bl2 ? EAST_AABB : WEST_AABB);
            }
            case WEST: {
                return bl ? WEST_AABB : (bl2 ? SOUTH_AABB : NORTH_AABB);
            }
            case NORTH: 
        }
        return bl ? NORTH_AABB : (bl2 ? WEST_AABB : EAST_AABB);
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        DoubleBlockHalf doubleBlockHalf = blockState.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            if (blockState2.is(this) && blockState2.getValue(HALF) != doubleBlockHalf) {
                return (BlockState)((BlockState)((BlockState)((BlockState)blockState.setValue(FACING, blockState2.getValue(FACING))).setValue(OPEN, blockState2.getValue(OPEN))).setValue(HINGE, blockState2.getValue(HINGE))).setValue(POWERED, blockState2.getValue(POWERED));
            }
            return Blocks.AIR.defaultBlockState();
        }
        if (doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !blockState.canSurvive(levelAccessor, blockPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        if (!level.isClientSide && player.isCreative()) {
            DoublePlantBlock.preventCreativeDropFromBottomPart(level, blockPos, blockState, player);
        }
        super.playerWillDestroy(level, blockPos, blockState, player);
    }

    @Override
    public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
        switch (pathComputationType) {
            case LAND: {
                return blockState.getValue(OPEN);
            }
            case WATER: {
                return false;
            }
            case AIR: {
                return blockState.getValue(OPEN);
            }
        }
        return false;
    }

    private int getCloseSound() {
        return this.material == Material.METAL ? 1011 : 1012;
    }

    private int getOpenSound() {
        return this.material == Material.METAL ? 1005 : 1006;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        BlockPos blockPos = blockPlaceContext.getClickedPos();
        if (blockPos.getY() < 255 && blockPlaceContext.getLevel().getBlockState(blockPos.above()).canBeReplaced(blockPlaceContext)) {
            Level level = blockPlaceContext.getLevel();
            boolean bl = level.hasNeighborSignal(blockPos) || level.hasNeighborSignal(blockPos.above());
            return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection())).setValue(HINGE, this.getHinge(blockPlaceContext))).setValue(POWERED, bl)).setValue(OPEN, bl)).setValue(HALF, DoubleBlockHalf.LOWER);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
        level.setBlock(blockPos.above(), (BlockState)blockState.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    private DoorHingeSide getHinge(BlockPlaceContext blockPlaceContext) {
        boolean bl;
        Level level = blockPlaceContext.getLevel();
        BlockPos blockPos = blockPlaceContext.getClickedPos();
        Direction direction = blockPlaceContext.getHorizontalDirection();
        BlockPos blockPos2 = blockPos.above();
        Direction direction2 = direction.getCounterClockWise();
        BlockPos blockPos3 = blockPos.relative(direction2);
        BlockState blockState = level.getBlockState(blockPos3);
        BlockPos blockPos4 = blockPos2.relative(direction2);
        BlockState blockState2 = level.getBlockState(blockPos4);
        Direction direction3 = direction.getClockWise();
        BlockPos blockPos5 = blockPos.relative(direction3);
        BlockState blockState3 = level.getBlockState(blockPos5);
        BlockPos blockPos6 = blockPos2.relative(direction3);
        BlockState blockState4 = level.getBlockState(blockPos6);
        int n = (blockState.isCollisionShapeFullBlock(level, blockPos3) ? -1 : 0) + (blockState2.isCollisionShapeFullBlock(level, blockPos4) ? -1 : 0) + (blockState3.isCollisionShapeFullBlock(level, blockPos5) ? 1 : 0) + (blockState4.isCollisionShapeFullBlock(level, blockPos6) ? 1 : 0);
        boolean bl2 = blockState.is(this) && blockState.getValue(HALF) == DoubleBlockHalf.LOWER;
        boolean bl3 = bl = blockState3.is(this) && blockState3.getValue(HALF) == DoubleBlockHalf.LOWER;
        if (bl2 && !bl || n > 0) {
            return DoorHingeSide.RIGHT;
        }
        if (bl && !bl2 || n < 0) {
            return DoorHingeSide.LEFT;
        }
        int n2 = direction.getStepX();
        int n3 = direction.getStepZ();
        Vec3 vec3 = blockPlaceContext.getClickLocation();
        double d = vec3.x - (double)blockPos.getX();
        double d2 = vec3.z - (double)blockPos.getZ();
        return n2 < 0 && d2 < 0.5 || n2 > 0 && d2 > 0.5 || n3 < 0 && d > 0.5 || n3 > 0 && d < 0.5 ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT;
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (this.material == Material.METAL) {
            return InteractionResult.PASS;
        }
        blockState = (BlockState)blockState.cycle(OPEN);
        level.setBlock(blockPos, blockState, 10);
        level.levelEvent(player, blockState.getValue(OPEN) != false ? this.getOpenSound() : this.getCloseSound(), blockPos, 0);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public boolean isOpen(BlockState blockState) {
        return blockState.getValue(OPEN);
    }

    public void setOpen(Level level, BlockState blockState, BlockPos blockPos, boolean bl) {
        if (!blockState.is(this) || blockState.getValue(OPEN) == bl) {
            return;
        }
        level.setBlock(blockPos, (BlockState)blockState.setValue(OPEN, bl), 10);
        this.playSound(level, blockPos, bl);
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        boolean bl2;
        boolean bl3 = level.hasNeighborSignal(blockPos) || level.hasNeighborSignal(blockPos.relative(blockState.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN)) ? true : (bl2 = false);
        if (block != this && bl2 != blockState.getValue(POWERED)) {
            if (bl2 != blockState.getValue(OPEN)) {
                this.playSound(level, blockPos, bl2);
            }
            level.setBlock(blockPos, (BlockState)((BlockState)blockState.setValue(POWERED, bl2)).setValue(OPEN, bl2), 2);
        }
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.below();
        BlockState blockState2 = levelReader.getBlockState(blockPos2);
        if (blockState.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return blockState2.isFaceSturdy(levelReader, blockPos2, Direction.UP);
        }
        return blockState2.is(this);
    }

    private void playSound(Level level, BlockPos blockPos, boolean bl) {
        level.levelEvent(null, bl ? this.getOpenSound() : this.getCloseSound(), blockPos, 0);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState blockState) {
        return PushReaction.DESTROY;
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return (BlockState)blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        if (mirror == Mirror.NONE) {
            return blockState;
        }
        return (BlockState)blockState.rotate(mirror.getRotation(blockState.getValue(FACING))).cycle(HINGE);
    }

    @Override
    public long getSeed(BlockState blockState, BlockPos blockPos) {
        return Mth.getSeed(blockPos.getX(), blockPos.below(blockState.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), blockPos.getZ());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING, OPEN, HINGE, POWERED);
    }

    public static boolean isWoodenDoor(Level level, BlockPos blockPos) {
        return DoorBlock.isWoodenDoor(level.getBlockState(blockPos));
    }

    public static boolean isWoodenDoor(BlockState blockState) {
        return blockState.getBlock() instanceof DoorBlock && (blockState.getMaterial() == Material.WOOD || blockState.getMaterial() == Material.NETHER_WOOD);
    }

}

