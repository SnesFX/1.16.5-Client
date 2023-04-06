/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public class EndGatewayBlock
extends BaseEntityBlock {
    protected EndGatewayBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter blockGetter) {
        return new TheEndGatewayBlockEntity();
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (!(blockEntity instanceof TheEndGatewayBlockEntity)) {
            return;
        }
        int n = ((TheEndGatewayBlockEntity)blockEntity).getParticleAmount();
        for (int i = 0; i < n; ++i) {
            double d = (double)blockPos.getX() + random.nextDouble();
            double d2 = (double)blockPos.getY() + random.nextDouble();
            double d3 = (double)blockPos.getZ() + random.nextDouble();
            double d4 = (random.nextDouble() - 0.5) * 0.5;
            double d5 = (random.nextDouble() - 0.5) * 0.5;
            double d6 = (random.nextDouble() - 0.5) * 0.5;
            int n2 = random.nextInt(2) * 2 - 1;
            if (random.nextBoolean()) {
                d3 = (double)blockPos.getZ() + 0.5 + 0.25 * (double)n2;
                d6 = random.nextFloat() * 2.0f * (float)n2;
            } else {
                d = (double)blockPos.getX() + 0.5 + 0.25 * (double)n2;
                d4 = random.nextFloat() * 2.0f * (float)n2;
            }
            level.addParticle(ParticleTypes.PORTAL, d, d2, d3, d4, d5, d6);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canBeReplaced(BlockState blockState, Fluid fluid) {
        return false;
    }
}

