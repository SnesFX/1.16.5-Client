/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractFurnaceBlock
extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    protected AbstractFurnaceBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(LIT, false));
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        this.openContainer(level, blockPos, player);
        return InteractionResult.CONSUME;
    }

    protected abstract void openContainer(Level var1, BlockPos var2, Player var3);

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return (BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
        BlockEntity blockEntity;
        if (itemStack.hasCustomHoverName() && (blockEntity = level.getBlockEntity(blockPos)) instanceof AbstractFurnaceBlockEntity) {
            ((AbstractFurnaceBlockEntity)blockEntity).setCustomName(itemStack.getHoverName());
        }
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState.is(blockState2.getBlock())) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof AbstractFurnaceBlockEntity) {
            Containers.dropContents(level, blockPos, (Container)((AbstractFurnaceBlockEntity)blockEntity));
            ((AbstractFurnaceBlockEntity)blockEntity).getRecipesToAwardAndPopExperience(level, Vec3.atCenterOf(blockPos));
            level.updateNeighbourForOutputSignal(blockPos, this);
        }
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(blockPos));
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return (BlockState)blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }
}

