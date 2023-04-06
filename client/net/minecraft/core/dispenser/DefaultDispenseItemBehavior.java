/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.core.dispenser;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class DefaultDispenseItemBehavior
implements DispenseItemBehavior {
    @Override
    public final ItemStack dispense(BlockSource blockSource, ItemStack itemStack) {
        ItemStack itemStack2 = this.execute(blockSource, itemStack);
        this.playSound(blockSource);
        this.playAnimation(blockSource, blockSource.getBlockState().getValue(DispenserBlock.FACING));
        return itemStack2;
    }

    protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
        Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
        Position position = DispenserBlock.getDispensePosition(blockSource);
        ItemStack itemStack2 = itemStack.split(1);
        DefaultDispenseItemBehavior.spawnItem(blockSource.getLevel(), itemStack2, 6, direction, position);
        return itemStack;
    }

    public static void spawnItem(Level level, ItemStack itemStack, int n, Direction direction, Position position) {
        double d = position.x();
        double d2 = position.y();
        double d3 = position.z();
        d2 = direction.getAxis() == Direction.Axis.Y ? (d2 -= 0.125) : (d2 -= 0.15625);
        ItemEntity itemEntity = new ItemEntity(level, d, d2, d3, itemStack);
        double d4 = level.random.nextDouble() * 0.1 + 0.2;
        itemEntity.setDeltaMovement(level.random.nextGaussian() * 0.007499999832361937 * (double)n + (double)direction.getStepX() * d4, level.random.nextGaussian() * 0.007499999832361937 * (double)n + 0.20000000298023224, level.random.nextGaussian() * 0.007499999832361937 * (double)n + (double)direction.getStepZ() * d4);
        level.addFreshEntity(itemEntity);
    }

    protected void playSound(BlockSource blockSource) {
        blockSource.getLevel().levelEvent(1000, blockSource.getPos(), 0);
    }

    protected void playAnimation(BlockSource blockSource, Direction direction) {
        blockSource.getLevel().levelEvent(2000, blockSource.getPos(), direction.get3DDataValue());
    }
}

