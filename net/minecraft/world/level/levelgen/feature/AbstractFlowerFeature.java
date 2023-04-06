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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public abstract class AbstractFlowerFeature<U extends FeatureConfiguration>
extends Feature<U> {
    public AbstractFlowerFeature(Codec<U> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, U u) {
        BlockState blockState = this.getRandomFlower(random, blockPos, u);
        int n = 0;
        for (int i = 0; i < this.getCount(u); ++i) {
            BlockPos blockPos2 = this.getPos(random, blockPos, u);
            if (!worldGenLevel.isEmptyBlock(blockPos2) || blockPos2.getY() >= 255 || !blockState.canSurvive(worldGenLevel, blockPos2) || !this.isValid(worldGenLevel, blockPos2, u)) continue;
            worldGenLevel.setBlock(blockPos2, blockState, 2);
            ++n;
        }
        return n > 0;
    }

    public abstract boolean isValid(LevelAccessor var1, BlockPos var2, U var3);

    public abstract int getCount(U var1);

    public abstract BlockPos getPos(Random var1, BlockPos var2, U var3);

    public abstract BlockState getRandomFlower(Random var1, BlockPos var2, U var3);
}

