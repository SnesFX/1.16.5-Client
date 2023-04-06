/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceSphereConfiguration;

public class ReplaceBlobsFeature
extends Feature<ReplaceSphereConfiguration> {
    public ReplaceBlobsFeature(Codec<ReplaceSphereConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, ReplaceSphereConfiguration replaceSphereConfiguration) {
        Block block = replaceSphereConfiguration.targetState.getBlock();
        BlockPos blockPos2 = ReplaceBlobsFeature.findTarget(worldGenLevel, blockPos.mutable().clamp(Direction.Axis.Y, 1, worldGenLevel.getMaxBuildHeight() - 1), block);
        if (blockPos2 == null) {
            return false;
        }
        int n = replaceSphereConfiguration.radius().sample(random);
        boolean bl = false;
        for (BlockPos blockPos3 : BlockPos.withinManhattan(blockPos2, n, n, n)) {
            if (blockPos3.distManhattan(blockPos2) > n) break;
            BlockState blockState = worldGenLevel.getBlockState(blockPos3);
            if (!blockState.is(block)) continue;
            this.setBlock(worldGenLevel, blockPos3, replaceSphereConfiguration.replaceState);
            bl = true;
        }
        return bl;
    }

    @Nullable
    private static BlockPos findTarget(LevelAccessor levelAccessor, BlockPos.MutableBlockPos mutableBlockPos, Block block) {
        while (mutableBlockPos.getY() > 1) {
            BlockState blockState = levelAccessor.getBlockState(mutableBlockPos);
            if (blockState.is(block)) {
                return mutableBlockPos;
            }
            mutableBlockPos.move(Direction.DOWN);
        }
        return null;
    }
}

