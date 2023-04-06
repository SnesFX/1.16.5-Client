/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;

public class MinecartItem
extends Item {
    private static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior(){
        private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

        @Override
        public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
            RailShape railShape;
            double d;
            Object object;
            Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
            ServerLevel serverLevel = blockSource.getLevel();
            double d2 = blockSource.x() + (double)direction.getStepX() * 1.125;
            double d3 = Math.floor(blockSource.y()) + (double)direction.getStepY();
            double d4 = blockSource.z() + (double)direction.getStepZ() * 1.125;
            BlockPos blockPos = blockSource.getPos().relative(direction);
            BlockState blockState = serverLevel.getBlockState(blockPos);
            RailShape railShape2 = railShape = blockState.getBlock() instanceof BaseRailBlock ? blockState.getValue(((BaseRailBlock)blockState.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            if (blockState.is(BlockTags.RAILS)) {
                d = railShape.isAscending() ? 0.6 : 0.1;
            } else if (blockState.isAir() && serverLevel.getBlockState(blockPos.below()).is(BlockTags.RAILS)) {
                RailShape railShape3;
                object = serverLevel.getBlockState(blockPos.below());
                RailShape railShape4 = railShape3 = ((BlockBehaviour.BlockStateBase)object).getBlock() instanceof BaseRailBlock ? ((StateHolder)object).getValue(((BaseRailBlock)((BlockBehaviour.BlockStateBase)object).getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                d = direction == Direction.DOWN || !railShape3.isAscending() ? -0.9 : -0.4;
            } else {
                return this.defaultDispenseItemBehavior.dispense(blockSource, itemStack);
            }
            object = AbstractMinecart.createMinecart(serverLevel, d2, d3 + d, d4, ((MinecartItem)itemStack.getItem()).type);
            if (itemStack.hasCustomHoverName()) {
                ((Entity)object).setCustomName(itemStack.getHoverName());
            }
            serverLevel.addFreshEntity((Entity)object);
            itemStack.shrink(1);
            return itemStack;
        }

        @Override
        protected void playSound(BlockSource blockSource) {
            blockSource.getLevel().levelEvent(1000, blockSource.getPos(), 0);
        }
    };
    private final AbstractMinecart.Type type;

    public MinecartItem(AbstractMinecart.Type type, Item.Properties properties) {
        super(properties);
        this.type = type;
        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        BlockPos blockPos;
        Level level = useOnContext.getLevel();
        BlockState blockState = level.getBlockState(blockPos = useOnContext.getClickedPos());
        if (!blockState.is(BlockTags.RAILS)) {
            return InteractionResult.FAIL;
        }
        ItemStack itemStack = useOnContext.getItemInHand();
        if (!level.isClientSide) {
            RailShape railShape = blockState.getBlock() instanceof BaseRailBlock ? blockState.getValue(((BaseRailBlock)blockState.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            double d = 0.0;
            if (railShape.isAscending()) {
                d = 0.5;
            }
            AbstractMinecart abstractMinecart = AbstractMinecart.createMinecart(level, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.0625 + d, (double)blockPos.getZ() + 0.5, this.type);
            if (itemStack.hasCustomHoverName()) {
                abstractMinecart.setCustomName(itemStack.getHoverName());
            }
            level.addFreshEntity(abstractMinecart);
        }
        itemStack.shrink(1);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

}

