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
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.surfacebuilders.BadlandsSurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

public class ErodedBadlandsSurfaceBuilder
extends BadlandsSurfaceBuilder {
    private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.defaultBlockState();
    private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.defaultBlockState();
    private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.defaultBlockState();

    public ErodedBadlandsSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> codec) {
        super(codec);
    }

    @Override
    public void apply(Random random, ChunkAccess chunkAccess, Biome biome, int n, int n2, int n3, double d, BlockState blockState, BlockState blockState2, int n4, long l, SurfaceBuilderBaseConfiguration surfaceBuilderBaseConfiguration) {
        double d2 = 0.0;
        double d3 = Math.min(Math.abs(d), this.pillarNoise.getValue((double)n * 0.25, (double)n2 * 0.25, false) * 15.0);
        if (d3 > 0.0) {
            double d4 = 0.001953125;
            d2 = d3 * d3 * 2.5;
            double d5 = Math.abs(this.pillarRoofNoise.getValue((double)n * 0.001953125, (double)n2 * 0.001953125, false));
            double d6 = Math.ceil(d5 * 50.0) + 14.0;
            if (d2 > d6) {
                d2 = d6;
            }
            d2 += 64.0;
        }
        int n5 = n & 0xF;
        int n6 = n2 & 0xF;
        BlockState blockState3 = WHITE_TERRACOTTA;
        SurfaceBuilderConfiguration surfaceBuilderConfiguration = biome.getGenerationSettings().getSurfaceBuilderConfig();
        BlockState blockState4 = surfaceBuilderConfiguration.getUnderMaterial();
        BlockState blockState5 = surfaceBuilderConfiguration.getTopMaterial();
        BlockState blockState6 = blockState4;
        int n7 = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        boolean bl = Math.cos(d / 3.0 * 3.141592653589793) > 0.0;
        int n8 = -1;
        boolean bl2 = false;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = Math.max((int)n3, (int)((int)d2 + 1)); i >= 0; --i) {
            BlockState blockState7;
            mutableBlockPos.set(n5, i, n6);
            if (chunkAccess.getBlockState(mutableBlockPos).isAir() && i < (int)d2) {
                chunkAccess.setBlockState(mutableBlockPos, blockState, false);
            }
            if ((blockState7 = chunkAccess.getBlockState(mutableBlockPos)).isAir()) {
                n8 = -1;
                continue;
            }
            if (!blockState7.is(blockState.getBlock())) continue;
            if (n8 == -1) {
                Object object;
                bl2 = false;
                if (n7 <= 0) {
                    blockState3 = Blocks.AIR.defaultBlockState();
                    blockState6 = blockState;
                } else if (i >= n4 - 4 && i <= n4 + 1) {
                    blockState3 = WHITE_TERRACOTTA;
                    blockState6 = blockState4;
                }
                if (i < n4 && (blockState3 == null || blockState3.isAir())) {
                    blockState3 = blockState2;
                }
                n8 = n7 + Math.max(0, i - n4);
                if (i >= n4 - 1) {
                    if (i > n4 + 3 + n7) {
                        object = i < 64 || i > 127 ? ORANGE_TERRACOTTA : (bl ? TERRACOTTA : this.getBand(n, i, n2));
                        chunkAccess.setBlockState(mutableBlockPos, (BlockState)object, false);
                        continue;
                    }
                    chunkAccess.setBlockState(mutableBlockPos, blockState5, false);
                    bl2 = true;
                    continue;
                }
                chunkAccess.setBlockState(mutableBlockPos, blockState6, false);
                object = blockState6.getBlock();
                if (object != Blocks.WHITE_TERRACOTTA && object != Blocks.ORANGE_TERRACOTTA && object != Blocks.MAGENTA_TERRACOTTA && object != Blocks.LIGHT_BLUE_TERRACOTTA && object != Blocks.YELLOW_TERRACOTTA && object != Blocks.LIME_TERRACOTTA && object != Blocks.PINK_TERRACOTTA && object != Blocks.GRAY_TERRACOTTA && object != Blocks.LIGHT_GRAY_TERRACOTTA && object != Blocks.CYAN_TERRACOTTA && object != Blocks.PURPLE_TERRACOTTA && object != Blocks.BLUE_TERRACOTTA && object != Blocks.BROWN_TERRACOTTA && object != Blocks.GREEN_TERRACOTTA && object != Blocks.RED_TERRACOTTA && object != Blocks.BLACK_TERRACOTTA) continue;
                chunkAccess.setBlockState(mutableBlockPos, ORANGE_TERRACOTTA, false);
                continue;
            }
            if (n8 <= 0) continue;
            --n8;
            if (bl2) {
                chunkAccess.setBlockState(mutableBlockPos, ORANGE_TERRACOTTA, false);
                continue;
            }
            chunkAccess.setBlockState(mutableBlockPos, this.getBand(n, i, n2), false);
        }
    }
}

