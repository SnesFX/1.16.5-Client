/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TrappedChestBlockEntity
extends ChestBlockEntity {
    public TrappedChestBlockEntity() {
        super(BlockEntityType.TRAPPED_CHEST);
    }

    @Override
    protected void signalOpenCount() {
        super.signalOpenCount();
        this.level.updateNeighborsAt(this.worldPosition.below(), this.getBlockState().getBlock());
    }
}

