/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.surfacebuilders.NetherCappedSurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;

public class BasaltDeltasSurfaceBuilder
extends NetherCappedSurfaceBuilder {
    private static final BlockState BASALT = Blocks.BASALT.defaultBlockState();
    private static final BlockState BLACKSTONE = Blocks.BLACKSTONE.defaultBlockState();
    private static final BlockState GRAVEL = Blocks.GRAVEL.defaultBlockState();
    private static final ImmutableList<BlockState> FLOOR_BLOCK_STATES = ImmutableList.of((Object)BASALT, (Object)BLACKSTONE);
    private static final ImmutableList<BlockState> CEILING_BLOCK_STATES = ImmutableList.of((Object)BASALT);

    public BasaltDeltasSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> codec) {
        super(codec);
    }

    @Override
    protected ImmutableList<BlockState> getFloorBlockStates() {
        return FLOOR_BLOCK_STATES;
    }

    @Override
    protected ImmutableList<BlockState> getCeilingBlockStates() {
        return CEILING_BLOCK_STATES;
    }

    @Override
    protected BlockState getPatchBlockState() {
        return GRAVEL;
    }
}

