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
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.material.Material;

public class FrozenOceanSurfaceBuilder
extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
    protected static final BlockState PACKED_ICE = Blocks.PACKED_ICE.defaultBlockState();
    protected static final BlockState SNOW_BLOCK = Blocks.SNOW_BLOCK.defaultBlockState();
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    private static final BlockState GRAVEL = Blocks.GRAVEL.defaultBlockState();
    private static final BlockState ICE = Blocks.ICE.defaultBlockState();
    private PerlinSimplexNoise icebergNoise;
    private PerlinSimplexNoise icebergRoofNoise;
    private long seed;

    public FrozenOceanSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> codec) {
        super(codec);
    }

    @Override
    public void apply(Random random, ChunkAccess chunkAccess, Biome biome, int n, int n2, int n3, double d, BlockState blockState, BlockState blockState2, int n4, long l, SurfaceBuilderBaseConfiguration surfaceBuilderBaseConfiguration) {
        double d2 = 0.0;
        double d3 = 0.0;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        float f = biome.getTemperature(mutableBlockPos.set(n, 63, n2));
        double d4 = Math.min(Math.abs(d), this.icebergNoise.getValue((double)n * 0.1, (double)n2 * 0.1, false) * 15.0);
        if (d4 > 1.8) {
            double d5 = 0.09765625;
            d2 = d4 * d4 * 1.2;
            double d6 = Math.abs(this.icebergRoofNoise.getValue((double)n * 0.09765625, (double)n2 * 0.09765625, false));
            double d7 = Math.ceil(d6 * 40.0) + 14.0;
            if (d2 > d7) {
                d2 = d7;
            }
            if (f > 0.1f) {
                d2 -= 2.0;
            }
            if (d2 > 2.0) {
                d3 = (double)n4 - d2 - 7.0;
                d2 += (double)n4;
            } else {
                d2 = 0.0;
            }
        }
        int n5 = n & 0xF;
        int n6 = n2 & 0xF;
        SurfaceBuilderConfiguration surfaceBuilderConfiguration = biome.getGenerationSettings().getSurfaceBuilderConfig();
        BlockState blockState3 = surfaceBuilderConfiguration.getUnderMaterial();
        BlockState blockState4 = surfaceBuilderConfiguration.getTopMaterial();
        BlockState blockState5 = blockState3;
        BlockState blockState6 = blockState4;
        int n7 = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        int n8 = -1;
        int n9 = 0;
        int n10 = 2 + random.nextInt(4);
        int n11 = n4 + 18 + random.nextInt(10);
        for (int i = Math.max((int)n3, (int)((int)d2 + 1)); i >= 0; --i) {
            mutableBlockPos.set(n5, i, n6);
            if (chunkAccess.getBlockState(mutableBlockPos).isAir() && i < (int)d2 && random.nextDouble() > 0.01) {
                chunkAccess.setBlockState(mutableBlockPos, PACKED_ICE, false);
            } else if (chunkAccess.getBlockState(mutableBlockPos).getMaterial() == Material.WATER && i > (int)d3 && i < n4 && d3 != 0.0 && random.nextDouble() > 0.15) {
                chunkAccess.setBlockState(mutableBlockPos, PACKED_ICE, false);
            }
            BlockState blockState7 = chunkAccess.getBlockState(mutableBlockPos);
            if (blockState7.isAir()) {
                n8 = -1;
                continue;
            }
            if (blockState7.is(blockState.getBlock())) {
                if (n8 == -1) {
                    if (n7 <= 0) {
                        blockState6 = AIR;
                        blockState5 = blockState;
                    } else if (i >= n4 - 4 && i <= n4 + 1) {
                        blockState6 = blockState4;
                        blockState5 = blockState3;
                    }
                    if (i < n4 && (blockState6 == null || blockState6.isAir())) {
                        blockState6 = biome.getTemperature(mutableBlockPos.set(n, i, n2)) < 0.15f ? ICE : blockState2;
                    }
                    n8 = n7;
                    if (i >= n4 - 1) {
                        chunkAccess.setBlockState(mutableBlockPos, blockState6, false);
                        continue;
                    }
                    if (i < n4 - 7 - n7) {
                        blockState6 = AIR;
                        blockState5 = blockState;
                        chunkAccess.setBlockState(mutableBlockPos, GRAVEL, false);
                        continue;
                    }
                    chunkAccess.setBlockState(mutableBlockPos, blockState5, false);
                    continue;
                }
                if (n8 <= 0) continue;
                chunkAccess.setBlockState(mutableBlockPos, blockState5, false);
                if (--n8 != 0 || !blockState5.is(Blocks.SAND) || n7 <= 1) continue;
                n8 = random.nextInt(4) + Math.max(0, i - 63);
                blockState5 = blockState5.is(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.defaultBlockState() : Blocks.SANDSTONE.defaultBlockState();
                continue;
            }
            if (!blockState7.is(Blocks.PACKED_ICE) || n9 > n10 || i <= n11) continue;
            chunkAccess.setBlockState(mutableBlockPos, SNOW_BLOCK, false);
            ++n9;
        }
    }

    @Override
    public void initNoise(long l) {
        if (this.seed != l || this.icebergNoise == null || this.icebergRoofNoise == null) {
            WorldgenRandom worldgenRandom = new WorldgenRandom(l);
            this.icebergNoise = new PerlinSimplexNoise(worldgenRandom, IntStream.rangeClosed(-3, 0));
            this.icebergRoofNoise = new PerlinSimplexNoise(worldgenRandom, (List<Integer>)ImmutableList.of((Object)0));
        }
        this.seed = l;
    }
}

