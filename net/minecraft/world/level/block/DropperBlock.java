/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class DropperBlock
extends DispenserBlock {
    private static final DispenseItemBehavior DISPENSE_BEHAVIOUR = new DefaultDispenseItemBehavior();

    public DropperBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected DispenseItemBehavior getDispenseMethod(ItemStack itemStack) {
        return DISPENSE_BEHAVIOUR;
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter blockGetter) {
        return new DropperBlockEntity();
    }

    @Override
    protected void dispenseFrom(ServerLevel serverLevel, BlockPos blockPos) {
        ItemStack itemStack;
        BlockSourceImpl blockSourceImpl = new BlockSourceImpl(serverLevel, blockPos);
        DispenserBlockEntity dispenserBlockEntity = (DispenserBlockEntity)blockSourceImpl.getEntity();
        int n = dispenserBlockEntity.getRandomSlot();
        if (n < 0) {
            serverLevel.levelEvent(1001, blockPos, 0);
            return;
        }
        ItemStack itemStack2 = dispenserBlockEntity.getItem(n);
        if (itemStack2.isEmpty()) {
            return;
        }
        Direction direction = serverLevel.getBlockState(blockPos).getValue(FACING);
        Container container = HopperBlockEntity.getContainerAt(serverLevel, blockPos.relative(direction));
        if (container == null) {
            itemStack = DISPENSE_BEHAVIOUR.dispense(blockSourceImpl, itemStack2);
        } else {
            itemStack = HopperBlockEntity.addItem(dispenserBlockEntity, container, itemStack2.copy().split(1), direction.getOpposite());
            if (itemStack.isEmpty()) {
                itemStack = itemStack2.copy();
                itemStack.shrink(1);
            } else {
                itemStack = itemStack2.copy();
            }
        }
        dispenserBlockEntity.setItem(n, itemStack);
    }
}

