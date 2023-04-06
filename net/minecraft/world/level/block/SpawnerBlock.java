/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnerBlock
extends BaseEntityBlock {
    protected SpawnerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter blockGetter) {
        return new SpawnerBlockEntity();
    }

    @Override
    public void spawnAfterBreak(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack) {
        super.spawnAfterBreak(blockState, serverLevel, blockPos, itemStack);
        int n = 15 + serverLevel.random.nextInt(15) + serverLevel.random.nextInt(15);
        this.popExperience(serverLevel, blockPos, n);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        return ItemStack.EMPTY;
    }
}

