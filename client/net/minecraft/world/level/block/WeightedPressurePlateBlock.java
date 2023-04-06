/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;

public class WeightedPressurePlateBlock
extends BasePressurePlateBlock {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    private final int maxWeight;

    protected WeightedPressurePlateBlock(int n, BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWER, 0));
        this.maxWeight = n;
    }

    @Override
    protected int getSignalStrength(Level level, BlockPos blockPos) {
        int n = Math.min(level.getEntitiesOfClass(Entity.class, TOUCH_AABB.move(blockPos)).size(), this.maxWeight);
        if (n > 0) {
            float f = (float)Math.min(this.maxWeight, n) / (float)this.maxWeight;
            return Mth.ceil(f * 15.0f);
        }
        return 0;
    }

    @Override
    protected void playOnSound(LevelAccessor levelAccessor, BlockPos blockPos) {
        levelAccessor.playSound(null, blockPos, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3f, 0.90000004f);
    }

    @Override
    protected void playOffSound(LevelAccessor levelAccessor, BlockPos blockPos) {
        levelAccessor.playSound(null, blockPos, SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3f, 0.75f);
    }

    @Override
    protected int getSignalForState(BlockState blockState) {
        return blockState.getValue(POWER);
    }

    @Override
    protected BlockState setSignalForState(BlockState blockState, int n) {
        return (BlockState)blockState.setValue(POWER, n);
    }

    @Override
    protected int getPressedTime() {
        return 10;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }
}

