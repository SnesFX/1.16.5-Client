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

public class WoodedBadlandsSurfaceBuilder
extends BadlandsSurfaceBuilder {
    private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.defaultBlockState();
    private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.defaultBlockState();
    private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.defaultBlockState();

    public WoodedBadlandsSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> codec) {
        super(codec);
    }

    @Override
    public void apply(Random random, ChunkAccess chunkAccess, Biome biome, int n, int n2, int n3, double d, BlockState blockState, BlockState blockState2, int n4, long l, SurfaceBuilderBaseConfiguration surfaceBuilderBaseConfiguration) {
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
        int n9 = 0;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = n3; i >= 0; --i) {
            if (n9 >= 15) continue;
            mutableBlockPos.set(n5, i, n6);
            BlockState blockState7 = chunkAccess.getBlockState(mutableBlockPos);
            if (blockState7.isAir()) {
                n8 = -1;
                continue;
            }
            if (!blockState7.is(blockState.getBlock())) continue;
            if (n8 == -1) {
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
                    if (i > 86 + n7 * 2) {
                        if (bl) {
                            chunkAccess.setBlockState(mutableBlockPos, Blocks.COARSE_DIRT.defaultBlockState(), false);
                        } else {
                            chunkAccess.setBlockState(mutableBlockPos, Blocks.GRASS_BLOCK.defaultBlockState(), false);
                        }
                    } else if (i > n4 + 3 + n7) {
                        BlockState blockState8 = i < 64 || i > 127 ? ORANGE_TERRACOTTA : (bl ? TERRACOTTA : this.getBand(n, i, n2));
                        chunkAccess.setBlockState(mutableBlockPos, blockState8, false);
                    } else {
                        chunkAccess.setBlockState(mutableBlockPos, blockState5, false);
                        bl2 = true;
                    }
                } else {
                    chunkAccess.setBlockState(mutableBlockPos, blockState6, false);
                    if (blockState6 == WHITE_TERRACOTTA) {
                        chunkAccess.setBlockState(mutableBlockPos, ORANGE_TERRACOTTA, false);
                    }
                }
            } else if (n8 > 0) {
                --n8;
                if (bl2) {
                    chunkAccess.setBlockState(mutableBlockPos, ORANGE_TERRACOTTA, false);
                } else {
                    chunkAccess.setBlockState(mutableBlockPos, this.getBand(n, i, n2), false);
                }
            }
            ++n9;
        }
    }
}

