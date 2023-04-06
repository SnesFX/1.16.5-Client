/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;

public class GiantTreeTaigaSurfaceBuilder
extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
    public GiantTreeTaigaSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> codec) {
        super(codec);
    }

    @Override
    public void apply(Random random, ChunkAccess chunkAccess, Biome biome, int n, int n2, int n3, double d, BlockState blockState, BlockState blockState2, int n4, long l, SurfaceBuilderBaseConfiguration surfaceBuilderBaseConfiguration) {
        if (d > 1.75) {
            SurfaceBuilder.DEFAULT.apply(random, chunkAccess, biome, n, n2, n3, d, blockState, blockState2, n4, l, SurfaceBuilder.CONFIG_COARSE_DIRT);
        } else if (d > -0.95) {
            SurfaceBuilder.DEFAULT.apply(random, chunkAccess, biome, n, n2, n3, d, blockState, blockState2, n4, l, SurfaceBuilder.CONFIG_PODZOL);
        } else {
            SurfaceBuilder.DEFAULT.apply(random, chunkAccess, biome, n, n2, n3, d, blockState, blockState2, n4, l, SurfaceBuilder.CONFIG_GRASS);
        }
    }
}

