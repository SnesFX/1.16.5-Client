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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;

public class DefaultSurfaceBuilder
extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
    public DefaultSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> codec) {
        super(codec);
    }

    @Override
    public void apply(Random random, ChunkAccess chunkAccess, Biome biome, int n, int n2, int n3, double d, BlockState blockState, BlockState blockState2, int n4, long l, SurfaceBuilderBaseConfiguration surfaceBuilderBaseConfiguration) {
        this.apply(random, chunkAccess, biome, n, n2, n3, d, blockState, blockState2, surfaceBuilderBaseConfiguration.getTopMaterial(), surfaceBuilderBaseConfiguration.getUnderMaterial(), surfaceBuilderBaseConfiguration.getUnderwaterMaterial(), n4);
    }

    protected void apply(Random random, ChunkAccess chunkAccess, Biome biome, int n, int n2, int n3, double d, BlockState blockState, BlockState blockState2, BlockState blockState3, BlockState blockState4, BlockState blockState5, int n4) {
        BlockState blockState6 = blockState3;
        BlockState blockState7 = blockState4;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        int n5 = -1;
        int n6 = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        int n7 = n & 0xF;
        int n8 = n2 & 0xF;
        for (int i = n3; i >= 0; --i) {
            mutableBlockPos.set(n7, i, n8);
            BlockState blockState8 = chunkAccess.getBlockState(mutableBlockPos);
            if (blockState8.isAir()) {
                n5 = -1;
                continue;
            }
            if (!blockState8.is(blockState.getBlock())) continue;
            if (n5 == -1) {
                if (n6 <= 0) {
                    blockState6 = Blocks.AIR.defaultBlockState();
                    blockState7 = blockState;
                } else if (i >= n4 - 4 && i <= n4 + 1) {
                    blockState6 = blockState3;
                    blockState7 = blockState4;
                }
                if (i < n4 && (blockState6 == null || blockState6.isAir())) {
                    blockState6 = biome.getTemperature(mutableBlockPos.set(n, i, n2)) < 0.15f ? Blocks.ICE.defaultBlockState() : blockState2;
                    mutableBlockPos.set(n7, i, n8);
                }
                n5 = n6;
                if (i >= n4 - 1) {
                    chunkAccess.setBlockState(mutableBlockPos, blockState6, false);
                    continue;
                }
                if (i < n4 - 7 - n6) {
                    blockState6 = Blocks.AIR.defaultBlockState();
                    blockState7 = blockState;
                    chunkAccess.setBlockState(mutableBlockPos, blockState5, false);
                    continue;
                }
                chunkAccess.setBlockState(mutableBlockPos, blockState7, false);
                continue;
            }
            if (n5 <= 0) continue;
            chunkAccess.setBlockState(mutableBlockPos, blockState7, false);
            if (--n5 != 0 || !blockState7.is(Blocks.SAND) || n6 <= 1) continue;
            n5 = random.nextInt(4) + Math.max(0, i - 63);
            blockState7 = blockState7.is(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.defaultBlockState() : Blocks.SANDSTONE.defaultBlockState();
        }
    }
}

