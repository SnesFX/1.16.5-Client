/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature.blockplacers;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.blockplacers.BlockPlacer;
import net.minecraft.world.level.levelgen.feature.blockplacers.BlockPlacerType;

public class DoublePlantPlacer
extends BlockPlacer {
    public static final Codec<DoublePlantPlacer> CODEC = Codec.unit(() -> INSTANCE);
    public static final DoublePlantPlacer INSTANCE = new DoublePlantPlacer();

    @Override
    protected BlockPlacerType<?> type() {
        return BlockPlacerType.DOUBLE_PLANT_PLACER;
    }

    @Override
    public void place(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Random random) {
        ((DoublePlantBlock)blockState.getBlock()).placeAt(levelAccessor, blockPos, 2);
    }
}

