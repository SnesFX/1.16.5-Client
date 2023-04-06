/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 */
package net.minecraft.world.level.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.PositionImpl;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class DispenserBlock
extends BaseEntityBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    private static final Map<Item, DispenseItemBehavior> DISPENSER_REGISTRY = (Map)Util.make(new Object2ObjectOpenHashMap(), object2ObjectOpenHashMap -> object2ObjectOpenHashMap.defaultReturnValue((Object)new DefaultDispenseItemBehavior()));

    public static void registerBehavior(ItemLike itemLike, DispenseItemBehavior dispenseItemBehavior) {
        DISPENSER_REGISTRY.put(itemLike.asItem(), dispenseItemBehavior);
    }

    protected DispenserBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TRIGGERED, false));
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof DispenserBlockEntity) {
            player.openMenu((DispenserBlockEntity)blockEntity);
            if (blockEntity instanceof DropperBlockEntity) {
                player.awardStat(Stats.INSPECT_DROPPER);
            } else {
                player.awardStat(Stats.INSPECT_DISPENSER);
            }
        }
        return InteractionResult.CONSUME;
    }

    protected void dispenseFrom(ServerLevel serverLevel, BlockPos blockPos) {
        BlockSourceImpl blockSourceImpl = new BlockSourceImpl(serverLevel, blockPos);
        DispenserBlockEntity dispenserBlockEntity = (DispenserBlockEntity)blockSourceImpl.getEntity();
        int n = dispenserBlockEntity.getRandomSlot();
        if (n < 0) {
            serverLevel.levelEvent(1001, blockPos, 0);
            return;
        }
        ItemStack itemStack = dispenserBlockEntity.getItem(n);
        DispenseItemBehavior dispenseItemBehavior = this.getDispenseMethod(itemStack);
        if (dispenseItemBehavior != DispenseItemBehavior.NOOP) {
            dispenserBlockEntity.setItem(n, dispenseItemBehavior.dispense(blockSourceImpl, itemStack));
        }
    }

    protected DispenseItemBehavior getDispenseMethod(ItemStack itemStack) {
        return DISPENSER_REGISTRY.get(itemStack.getItem());
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        boolean bl2 = level.hasNeighborSignal(blockPos) || level.hasNeighborSignal(blockPos.above());
        boolean bl3 = blockState.getValue(TRIGGERED);
        if (bl2 && !bl3) {
            level.getBlockTicks().scheduleTick(blockPos, this, 4);
            level.setBlock(blockPos, (BlockState)blockState.setValue(TRIGGERED, true), 4);
        } else if (!bl2 && bl3) {
            level.setBlock(blockPos, (BlockState)blockState.setValue(TRIGGERED, false), 4);
        }
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        this.dispenseFrom(serverLevel, blockPos);
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter blockGetter) {
        return new DispenserBlockEntity();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return (BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getNearestLookingDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
        BlockEntity blockEntity;
        if (itemStack.hasCustomHoverName() && (blockEntity = level.getBlockEntity(blockPos)) instanceof DispenserBlockEntity) {
            ((DispenserBlockEntity)blockEntity).setCustomName(itemStack.getHoverName());
        }
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState.is(blockState2.getBlock())) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof DispenserBlockEntity) {
            Containers.dropContents(level, blockPos, (Container)((DispenserBlockEntity)blockEntity));
            level.updateNeighbourForOutputSignal(blockPos, this);
        }
        super.onRemove(blockState, level, blockPos, blockState2, bl);
    }

    public static Position getDispensePosition(BlockSource blockSource) {
        Direction direction = blockSource.getBlockState().getValue(FACING);
        double d = blockSource.x() + 0.7 * (double)direction.getStepX();
        double d2 = blockSource.y() + 0.7 * (double)direction.getStepY();
        double d3 = blockSource.z() + 0.7 * (double)direction.getStepZ();
        return new PositionImpl(d, d2, d3);
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
        builder.add(FACING, TRIGGERED);
    }
}

