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
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public abstract class AbstractHugeMushroomFeature
extends Feature<HugeMushroomFeatureConfiguration> {
    public AbstractHugeMushroomFeature(Codec<HugeMushroomFeatureConfiguration> codec) {
        super(codec);
    }

    protected void placeTrunk(LevelAccessor levelAccessor, Random random, BlockPos blockPos, HugeMushroomFeatureConfiguration hugeMushroomFeatureConfiguration, int n, BlockPos.MutableBlockPos mutableBlockPos) {
        for (int i = 0; i < n; ++i) {
            mutableBlockPos.set(blockPos).move(Direction.UP, i);
            if (levelAccessor.getBlockState(mutableBlockPos).isSolidRender(levelAccessor, mutableBlockPos)) continue;
            this.setBlock(levelAccessor, mutableBlockPos, hugeMushroomFeatureConfiguration.stemProvider.getState(random, blockPos));
        }
    }

    protected int getTreeHeight(Random random) {
        int n = random.nextInt(3) + 4;
        if (random.nextInt(12) == 0) {
            n *= 2;
        }
        return n;
    }

    protected boolean isValidPosition(LevelAccessor levelAccessor, BlockPos blockPos, int n, BlockPos.MutableBlockPos mutableBlockPos, HugeMushroomFeatureConfiguration hugeMushroomFeatureConfiguration) {
        int n2 = blockPos.getY();
        if (n2 < 1 || n2 + n + 1 >= 256) {
            return false;
        }
        Block block = levelAccessor.getBlockState(blockPos.below()).getBlock();
        if (!AbstractHugeMushroomFeature.isDirt(block) && !block.is(BlockTags.MUSHROOM_GROW_BLOCK)) {
            return false;
        }
        for (int i = 0; i <= n; ++i) {
            int n3 = this.getTreeRadiusForHeight(-1, -1, hugeMushroomFeatureConfiguration.foliageRadius, i);
            for (int j = -n3; j <= n3; ++j) {
                for (int k = -n3; k <= n3; ++k) {
                    BlockState blockState = levelAccessor.getBlockState(mutableBlockPos.setWithOffset(blockPos, j, i, k));
                    if (blockState.isAir() || blockState.is(BlockTags.LEAVES)) continue;
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, HugeMushroomFeatureConfiguration hugeMushroomFeatureConfiguration) {
        BlockPos.MutableBlockPos mutableBlockPos;
        int n = this.getTreeHeight(random);
        if (!this.isValidPosition(worldGenLevel, blockPos, n, mutableBlockPos = new BlockPos.MutableBlockPos(), hugeMushroomFeatureConfiguration)) {
            return false;
        }
        this.makeCap(worldGenLevel, random, blockPos, n, mutableBlockPos, hugeMushroomFeatureConfiguration);
        this.placeTrunk(worldGenLevel, random, blockPos, hugeMushroomFeatureConfiguration, n, mutableBlockPos);
        return true;
    }

    protected abstract int getTreeRadiusForHeight(int var1, int var2, int var3, int var4);

    protected abstract void makeCap(LevelAccessor var1, Random var2, BlockPos var3, int var4, BlockPos.MutableBlockPos var5, HugeMushroomFeatureConfiguration var6);
}

