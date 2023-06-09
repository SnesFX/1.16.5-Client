/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class ButtonBlock
extends FaceAttachedHorizontalDirectionalBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    protected static final VoxelShape CEILING_AABB_X = Block.box(6.0, 14.0, 5.0, 10.0, 16.0, 11.0);
    protected static final VoxelShape CEILING_AABB_Z = Block.box(5.0, 14.0, 6.0, 11.0, 16.0, 10.0);
    protected static final VoxelShape FLOOR_AABB_X = Block.box(6.0, 0.0, 5.0, 10.0, 2.0, 11.0);
    protected static final VoxelShape FLOOR_AABB_Z = Block.box(5.0, 0.0, 6.0, 11.0, 2.0, 10.0);
    protected static final VoxelShape NORTH_AABB = Block.box(5.0, 6.0, 14.0, 11.0, 10.0, 16.0);
    protected static final VoxelShape SOUTH_AABB = Block.box(5.0, 6.0, 0.0, 11.0, 10.0, 2.0);
    protected static final VoxelShape WEST_AABB = Block.box(14.0, 6.0, 5.0, 16.0, 10.0, 11.0);
    protected static final VoxelShape EAST_AABB = Block.box(0.0, 6.0, 5.0, 2.0, 10.0, 11.0);
    protected static final VoxelShape PRESSED_CEILING_AABB_X = Block.box(6.0, 15.0, 5.0, 10.0, 16.0, 11.0);
    protected static final VoxelShape PRESSED_CEILING_AABB_Z = Block.box(5.0, 15.0, 6.0, 11.0, 16.0, 10.0);
    protected static final VoxelShape PRESSED_FLOOR_AABB_X = Block.box(6.0, 0.0, 5.0, 10.0, 1.0, 11.0);
    protected static final VoxelShape PRESSED_FLOOR_AABB_Z = Block.box(5.0, 0.0, 6.0, 11.0, 1.0, 10.0);
    protected static final VoxelShape PRESSED_NORTH_AABB = Block.box(5.0, 6.0, 15.0, 11.0, 10.0, 16.0);
    protected static final VoxelShape PRESSED_SOUTH_AABB = Block.box(5.0, 6.0, 0.0, 11.0, 10.0, 1.0);
    protected static final VoxelShape PRESSED_WEST_AABB = Block.box(15.0, 6.0, 5.0, 16.0, 10.0, 11.0);
    protected static final VoxelShape PRESSED_EAST_AABB = Block.box(0.0, 6.0, 5.0, 1.0, 10.0, 11.0);
    private final boolean sensitive;

    protected ButtonBlock(boolean bl, BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(FACE, AttachFace.WALL));
        this.sensitive = bl;
    }

    private int getPressDuration() {
        return this.sensitive ? 30 : 20;
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        Direction direction = blockState.getValue(FACING);
        boolean bl = blockState.getValue(POWERED);
        switch ((AttachFace)blockState.getValue(FACE)) {
            case FLOOR: {
                if (direction.getAxis() == Direction.Axis.X) {
                    return bl ? PRESSED_FLOOR_AABB_X : FLOOR_AABB_X;
                }
                return bl ? PRESSED_FLOOR_AABB_Z : FLOOR_AABB_Z;
            }
            case WALL: {
                switch (direction) {
                    case EAST: {
                        return bl ? PRESSED_EAST_AABB : EAST_AABB;
                    }
                    case WEST: {
                        return bl ? PRESSED_WEST_AABB : WEST_AABB;
                    }
                    case SOUTH: {
                        return bl ? PRESSED_SOUTH_AABB : SOUTH_AABB;
                    }
                }
                return bl ? PRESSED_NORTH_AABB : NORTH_AABB;
            }
        }
        if (direction.getAxis() == Direction.Axis.X) {
            return bl ? PRESSED_CEILING_AABB_X : CEILING_AABB_X;
        }
        return bl ? PRESSED_CEILING_AABB_Z : CEILING_AABB_Z;
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (blockState.getValue(POWERED).booleanValue()) {
            return InteractionResult.CONSUME;
        }
        this.press(blockState, level, blockPos);
        this.playSound(player, level, blockPos, true);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public void press(BlockState blockState, Level level, BlockPos blockPos) {
        level.setBlock(blockPos, (BlockState)blockState.setValue(POWERED, true), 3);
        this.updateNeighbours(blockState, level, blockPos);
        level.getBlockTicks().scheduleTick(blockPos, this, this.getPressDuration());
    }

    protected void playSound(@Nullable Player player, LevelAccessor levelAccessor, BlockPos blockPos, boolean bl) {
        levelAccessor.playSound(bl ? player : null, blockPos, this.getSound(bl), SoundSource.BLOCKS, 0.3f, bl ? 0.6f : 0.5f);
    }

    protected abstract SoundEvent getSound(boolean var1);

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (bl || blockState.is(blockState2.getBlock())) {
            return;
        }
        if (blockState.getValue(POWERED).booleanValue()) {
            this.updateNeighbours(blockState, level, blockPos);
        }
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return blockState.getValue(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        if (blockState.getValue(POWERED).booleanValue() && ButtonBlock.getConnectedDirection(blockState) == direction) {
            return 15;
        }
        return 0;
    }

    @Override
    public boolean isSignalSource(BlockState blockState) {
        return true;
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        if (!blockState.getValue(POWERED).booleanValue()) {
            return;
        }
        if (this.sensitive) {
            this.checkPressed(blockState, serverLevel, blockPos);
        } else {
            serverLevel.setBlock(blockPos, (BlockState)blockState.setValue(POWERED, false), 3);
            this.updateNeighbours(blockState, serverLevel, blockPos);
            this.playSound(null, serverLevel, blockPos, false);
        }
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if (level.isClientSide || !this.sensitive || blockState.getValue(POWERED).booleanValue()) {
            return;
        }
        this.checkPressed(blockState, level, blockPos);
    }

    private void checkPressed(BlockState blockState, Level level, BlockPos blockPos) {
        boolean bl;
        List<AbstractArrow> list = level.getEntitiesOfClass(AbstractArrow.class, blockState.getShape(level, blockPos).bounds().move(blockPos));
        boolean bl2 = !list.isEmpty();
        if (bl2 != (bl = blockState.getValue(POWERED).booleanValue())) {
            level.setBlock(blockPos, (BlockState)blockState.setValue(POWERED, bl2), 3);
            this.updateNeighbours(blockState, level, blockPos);
            this.playSound(null, level, blockPos, bl2);
        }
        if (bl2) {
            level.getBlockTicks().scheduleTick(new BlockPos(blockPos), this, this.getPressDuration());
        }
    }

    private void updateNeighbours(BlockState blockState, Level level, BlockPos blockPos) {
        level.updateNeighborsAt(blockPos, this);
        level.updateNeighborsAt(blockPos.relative(ButtonBlock.getConnectedDirection(blockState).getOpposite()), this);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, FACE);
    }

}

