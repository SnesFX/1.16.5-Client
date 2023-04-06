/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class BaseDiskFeature
extends Feature<DiskConfiguration> {
    public BaseDiskFeature(Codec<DiskConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DiskConfiguration diskConfiguration) {
        boolean bl = false;
        int n = diskConfiguration.radius.sample(random);
        for (int i = blockPos.getX() - n; i <= blockPos.getX() + n; ++i) {
            for (int j = blockPos.getZ() - n; j <= blockPos.getZ() + n; ++j) {
                int n2;
                int n3 = i - blockPos.getX();
                if (n3 * n3 + (n2 = j - blockPos.getZ()) * n2 > n * n) continue;
                block2 : for (int k = blockPos.getY() - diskConfiguration.halfHeight; k <= blockPos.getY() + diskConfiguration.halfHeight; ++k) {
                    BlockPos blockPos2 = new BlockPos(i, k, j);
                    Block block = worldGenLevel.getBlockState(blockPos2).getBlock();
                    for (BlockState blockState : diskConfiguration.targets) {
                        if (!blockState.is(block)) continue;
                        worldGenLevel.setBlock(blockPos2, diskConfiguration.state, 2);
                        bl = true;
                        continue block2;
                    }
                }
            }
        }
        return bl;
    }
}

