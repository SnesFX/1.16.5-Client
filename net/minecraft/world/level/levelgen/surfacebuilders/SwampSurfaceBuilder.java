/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

public class SwampSurfaceBuilder
extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
    public SwampSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> codec) {
        super(codec);
    }

    @Override
    public void apply(Random random, ChunkAccess chunkAccess, Biome biome, int n, int n2, int n3, double d, BlockState blockState, BlockState blockState2, int n4, long l, SurfaceBuilderBaseConfiguration surfaceBuilderBaseConfiguration) {
        double d2 = Biome.BIOME_INFO_NOISE.getValue((double)n * 0.25, (double)n2 * 0.25, false);
        if (d2 > 0.0) {
            int n5 = n & 0xF;
            int n6 = n2 & 0xF;
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            for (int i = n3; i >= 0; --i) {
                mutableBlockPos.set(n5, i, n6);
                if (chunkAccess.getBlockState(mutableBlockPos).isAir()) continue;
                if (i != 62 || chunkAccess.getBlockState(mutableBlockPos).is(blockState2.getBlock())) break;
                chunkAccess.setBlockState(mutableBlockPos, blockState2, false);
                break;
            }
        }
        SurfaceBuilder.DEFAULT.apply(random, chunkAccess, biome, n, n2, n3, d, blockState, blockState2, n4, l, surfaceBuilderBaseConfiguration);
    }
}

