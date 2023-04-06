/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class AbstractProjectileDispenseBehavior
extends DefaultDispenseItemBehavior {
    @Override
    public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
        ServerLevel serverLevel = blockSource.getLevel();
        Position position = DispenserBlock.getDispensePosition(blockSource);
        Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
        Projectile projectile = this.getProjectile(serverLevel, position, itemStack);
        projectile.shoot(direction.getStepX(), (float)direction.getStepY() + 0.1f, direction.getStepZ(), this.getPower(), this.getUncertainty());
        serverLevel.addFreshEntity(projectile);
        itemStack.shrink(1);
        return itemStack;
    }

    @Override
    protected void playSound(BlockSource blockSource) {
        blockSource.getLevel().levelEvent(1002, blockSource.getPos(), 0);
    }

    protected abstract Projectile getProjectile(Level var1, Position var2, ItemStack var3);

    protected float getUncertainty() {
        return 6.0f;
    }

    protected float getPower() {
        return 1.1f;
    }
}

