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
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class IceSpikeFeature
extends Feature<NoneFeatureConfiguration> {
    public IceSpikeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
        int n;
        float f;
        int n2;
        Object object;
        while (worldGenLevel.isEmptyBlock(blockPos) && blockPos.getY() > 2) {
            blockPos = blockPos.below();
        }
        if (!worldGenLevel.getBlockState(blockPos).is(Blocks.SNOW_BLOCK)) {
            return false;
        }
        blockPos = blockPos.above(random.nextInt(4));
        int n3 = random.nextInt(4) + 7;
        int n4 = n3 / 4 + random.nextInt(2);
        if (n4 > 1 && random.nextInt(60) == 0) {
            blockPos = blockPos.above(10 + random.nextInt(30));
        }
        for (n2 = 0; n2 < n3; ++n2) {
            float f2 = (1.0f - (float)n2 / (float)n3) * (float)n4;
            n = Mth.ceil(f2);
            for (int i = -n; i <= n; ++i) {
                f = (float)Mth.abs(i) - 0.25f;
                for (int j = -n; j <= n; ++j) {
                    object = (float)Mth.abs(j) - 0.25f;
                    if ((i != 0 || j != 0) && f * f + object * object > f2 * f2 || (i == -n || i == n || j == -n || j == n) && random.nextFloat() > 0.75f) continue;
                    BlockState blockState = worldGenLevel.getBlockState(blockPos.offset(i, n2, j));
                    Block block = blockState.getBlock();
                    if (blockState.isAir() || IceSpikeFeature.isDirt(block) || block == Blocks.SNOW_BLOCK || block == Blocks.ICE) {
                        this.setBlock(worldGenLevel, blockPos.offset(i, n2, j), Blocks.PACKED_ICE.defaultBlockState());
                    }
                    if (n2 == 0 || n <= 1) continue;
                    blockState = worldGenLevel.getBlockState(blockPos.offset(i, -n2, j));
                    block = blockState.getBlock();
                    if (!blockState.isAir() && !IceSpikeFeature.isDirt(block) && block != Blocks.SNOW_BLOCK && block != Blocks.ICE) continue;
                    this.setBlock(worldGenLevel, blockPos.offset(i, -n2, j), Blocks.PACKED_ICE.defaultBlockState());
                }
            }
        }
        n2 = n4 - 1;
        if (n2 < 0) {
            n2 = 0;
        } else if (n2 > 1) {
            n2 = 1;
        }
        for (int i = -n2; i <= n2; ++i) {
            block5 : for (n = -n2; n <= n2; ++n) {
                BlockPos blockPos2 = blockPos.offset(i, -1, n);
                f = 50;
                if (Math.abs(i) == 1 && Math.abs(n) == 1) {
                    f = random.nextInt(5);
                }
                while (blockPos2.getY() > 50) {
                    BlockState blockState = worldGenLevel.getBlockState(blockPos2);
                    object = blockState.getBlock();
                    if (!blockState.isAir() && !IceSpikeFeature.isDirt((Block)object) && object != Blocks.SNOW_BLOCK && object != Blocks.ICE && object != Blocks.PACKED_ICE) continue block5;
                    this.setBlock(worldGenLevel, blockPos2, Blocks.PACKED_ICE.defaultBlockState());
                    blockPos2 = blockPos2.below();
                    if (--f > 0) continue;
                    blockPos2 = blockPos2.below(random.nextInt(5) + 1);
                    f = random.nextInt(5);
                }
            }
        }
        return true;
    }
}

