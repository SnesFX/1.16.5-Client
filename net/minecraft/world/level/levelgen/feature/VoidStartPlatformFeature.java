/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class VoidStartPlatformFeature
extends Feature<NoneFeatureConfiguration> {
    private static final BlockPos PLATFORM_ORIGIN = new BlockPos(8, 3, 8);
    private static final ChunkPos PLATFORM_ORIGIN_CHUNK = new ChunkPos(PLATFORM_ORIGIN);

    public VoidStartPlatformFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    private static int checkerboardDistance(int n, int n2, int n3, int n4) {
        return Math.max(Math.abs(n - n3), Math.abs(n2 - n4));
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        if (VoidStartPlatformFeature.checkerboardDistance(chunkPos.x, chunkPos.z, VoidStartPlatformFeature.PLATFORM_ORIGIN_CHUNK.x, VoidStartPlatformFeature.PLATFORM_ORIGIN_CHUNK.z) > 1) {
            return true;
        }
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = chunkPos.getMinBlockZ(); i <= chunkPos.getMaxBlockZ(); ++i) {
            for (int j = chunkPos.getMinBlockX(); j <= chunkPos.getMaxBlockX(); ++j) {
                if (VoidStartPlatformFeature.checkerboardDistance(PLATFORM_ORIGIN.getX(), PLATFORM_ORIGIN.getZ(), j, i) > 16) continue;
                mutableBlockPos.set(j, PLATFORM_ORIGIN.getY(), i);
                if (mutableBlockPos.equals(PLATFORM_ORIGIN)) {
                    worldGenLevel.setBlock(mutableBlockPos, Blocks.COBBLESTONE.defaultBlockState(), 2);
                    continue;
                }
                worldGenLevel.setBlock(mutableBlockPos, Blocks.STONE.defaultBlockState(), 2);
            }
        }
        return true;
    }
}

