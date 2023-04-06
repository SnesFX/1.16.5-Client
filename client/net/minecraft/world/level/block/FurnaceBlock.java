/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.OptionalInt;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class FurnaceBlock
extends AbstractFurnaceBlock {
    protected FurnaceBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter blockGetter) {
        return new FurnaceBlockEntity();
    }

    @Override
    protected void openContainer(Level level, BlockPos blockPos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity instanceof FurnaceBlockEntity) {
            player.openMenu((MenuProvider)((Object)blockEntity));
            player.awardStat(Stats.INTERACT_WITH_FURNACE);
        }
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
        if (!blockState.getValue(LIT).booleanValue()) {
            return;
        }
        double d = (double)blockPos.getX() + 0.5;
        double d2 = blockPos.getY();
        double d3 = (double)blockPos.getZ() + 0.5;
        if (random.nextDouble() < 0.1) {
            level.playLocalSound(d, d2, d3, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0f, 1.0f, false);
        }
        Direction direction = blockState.getValue(FACING);
        Direction.Axis axis = direction.getAxis();
        double d4 = 0.52;
        double d5 = random.nextDouble() * 0.6 - 0.3;
        double d6 = axis == Direction.Axis.X ? (double)direction.getStepX() * 0.52 : d5;
        double d7 = random.nextDouble() * 6.0 / 16.0;
        double d8 = axis == Direction.Axis.Z ? (double)direction.getStepZ() * 0.52 : d5;
        level.addParticle(ParticleTypes.SMOKE, d + d6, d2 + d7, d3 + d8, 0.0, 0.0, 0.0);
        level.addParticle(ParticleTypes.FLAME, d + d6, d2 + d7, d3 + d8, 0.0, 0.0, 0.0);
    }
}

