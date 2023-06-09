/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class FallingBlock
extends Block {
    public FallingBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        level.getBlockTicks().scheduleTick(blockPos, this, this.getDelayAfterPlace());
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        levelAccessor.getBlockTicks().scheduleTick(blockPos, this, this.getDelayAfterPlace());
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random random) {
        if (!FallingBlock.isFree(serverLevel.getBlockState(blockPos.below())) || blockPos.getY() < 0) {
            return;
        }
        FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(serverLevel, (double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5, serverLevel.getBlockState(blockPos));
        this.falling(fallingBlockEntity);
        serverLevel.addFreshEntity(fallingBlockEntity);
    }

    protected void falling(FallingBlockEntity fallingBlockEntity) {
    }

    protected int getDelayAfterPlace() {
        return 2;
    }

    public static boolean isFree(BlockState blockState) {
        Material material = blockState.getMaterial();
        return blockState.isAir() || blockState.is(BlockTags.FIRE) || material.isLiquid() || material.isReplaceable();
    }

    public void onLand(Level level, BlockPos blockPos, BlockState blockState, BlockState blockState2, FallingBlockEntity fallingBlockEntity) {
    }

    public void onBroken(Level level, BlockPos blockPos, FallingBlockEntity fallingBlockEntity) {
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
        BlockPos blockPos2;
        if (random.nextInt(16) == 0 && FallingBlock.isFree(level.getBlockState(blockPos2 = blockPos.below()))) {
            double d = (double)blockPos.getX() + random.nextDouble();
            double d2 = (double)blockPos.getY() - 0.05;
            double d3 = (double)blockPos.getZ() + random.nextDouble();
            level.addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, blockState), d, d2, d3, 0.0, 0.0, 0.0);
        }
    }

    public int getDustColor(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return -16777216;
    }
}

