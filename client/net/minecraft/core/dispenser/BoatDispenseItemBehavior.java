/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class BoatDispenseItemBehavior
extends DefaultDispenseItemBehavior {
    private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
    private final Boat.Type type;

    public BoatDispenseItemBehavior(Boat.Type type) {
        this.type = type;
    }

    @Override
    public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
        double d;
        Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
        ServerLevel serverLevel = blockSource.getLevel();
        double d2 = blockSource.x() + (double)((float)direction.getStepX() * 1.125f);
        double d3 = blockSource.y() + (double)((float)direction.getStepY() * 1.125f);
        double d4 = blockSource.z() + (double)((float)direction.getStepZ() * 1.125f);
        BlockPos blockPos = blockSource.getPos().relative(direction);
        if (serverLevel.getFluidState(blockPos).is(FluidTags.WATER)) {
            d = 1.0;
        } else if (serverLevel.getBlockState(blockPos).isAir() && serverLevel.getFluidState(blockPos.below()).is(FluidTags.WATER)) {
            d = 0.0;
        } else {
            return this.defaultDispenseItemBehavior.dispense(blockSource, itemStack);
        }
        Boat boat = new Boat(serverLevel, d2, d3 + d, d4);
        boat.setType(this.type);
        boat.yRot = direction.toYRot();
        serverLevel.addFreshEntity(boat);
        itemStack.shrink(1);
        return itemStack;
    }

    @Override
    protected void playSound(BlockSource blockSource) {
        blockSource.getLevel().levelEvent(1000, blockSource.getPos(), 0);
    }
}

