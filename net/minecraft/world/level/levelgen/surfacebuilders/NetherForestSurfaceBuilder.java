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
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public class NetherForestSurfaceBuilder
extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
    private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();
    protected long seed;
    private PerlinNoise decorationNoise;

    public NetherForestSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> codec) {
        super(codec);
    }

    @Override
    public void apply(Random random, ChunkAccess chunkAccess, Biome biome, int n, int n2, int n3, double d, BlockState blockState, BlockState blockState2, int n4, long l, SurfaceBuilderBaseConfiguration surfaceBuilderBaseConfiguration) {
        int n5 = n4;
        int n6 = n & 0xF;
        int n7 = n2 & 0xF;
        double d2 = this.decorationNoise.getValue((double)n * 0.1, n4, (double)n2 * 0.1);
        boolean bl = d2 > 0.15 + random.nextDouble() * 0.35;
        double d3 = this.decorationNoise.getValue((double)n * 0.1, 109.0, (double)n2 * 0.1);
        boolean bl2 = d3 > 0.25 + random.nextDouble() * 0.9;
        int n8 = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        int n9 = -1;
        BlockState blockState3 = surfaceBuilderBaseConfiguration.getUnderMaterial();
        for (int i = 127; i >= 0; --i) {
            mutableBlockPos.set(n6, i, n7);
            BlockState blockState4 = surfaceBuilderBaseConfiguration.getTopMaterial();
            BlockState blockState5 = chunkAccess.getBlockState(mutableBlockPos);
            if (blockState5.isAir()) {
                n9 = -1;
                continue;
            }
            if (!blockState5.is(blockState.getBlock())) continue;
            if (n9 == -1) {
                boolean bl3 = false;
                if (n8 <= 0) {
                    bl3 = true;
                    blockState3 = surfaceBuilderBaseConfiguration.getUnderMaterial();
                }
                if (bl) {
                    blockState4 = surfaceBuilderBaseConfiguration.getUnderMaterial();
                } else if (bl2) {
                    blockState4 = surfaceBuilderBaseConfiguration.getUnderwaterMaterial();
                }
                if (i < n5 && bl3) {
                    blockState4 = blockState2;
                }
                n9 = n8;
                if (i >= n5 - 1) {
                    chunkAccess.setBlockState(mutableBlockPos, blockState4, false);
                    continue;
                }
                chunkAccess.setBlockState(mutableBlockPos, blockState3, false);
                continue;
            }
            if (n9 <= 0) continue;
            --n9;
            chunkAccess.setBlockState(mutableBlockPos, blockState3, false);
        }
    }

    @Override
    public void initNoise(long l) {
        if (this.seed != l || this.decorationNoise == null) {
            this.decorationNoise = new PerlinNoise(new WorldgenRandom(l), (List<Integer>)ImmutableList.of((Object)0));
        }
        this.seed = l;
    }
}

