/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.DeltaFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class DeltaFeature
extends Feature<DeltaFeatureConfiguration> {
    private static final ImmutableList<Block> CANNOT_REPLACE = ImmutableList.of((Object)Blocks.BEDROCK, (Object)Blocks.NETHER_BRICKS, (Object)Blocks.NETHER_BRICK_FENCE, (Object)Blocks.NETHER_BRICK_STAIRS, (Object)Blocks.NETHER_WART, (Object)Blocks.CHEST, (Object)Blocks.SPAWNER);
    private static final Direction[] DIRECTIONS = Direction.values();

    public DeltaFeature(Codec<DeltaFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DeltaFeatureConfiguration deltaFeatureConfiguration) {
        boolean bl = false;
        boolean bl2 = random.nextDouble() < 0.9;
        int n = bl2 ? deltaFeatureConfiguration.rimSize().sample(random) : 0;
        int n2 = bl2 ? deltaFeatureConfiguration.rimSize().sample(random) : 0;
        boolean bl3 = bl2 && n != 0 && n2 != 0;
        int n3 = deltaFeatureConfiguration.size().sample(random);
        int n4 = deltaFeatureConfiguration.size().sample(random);
        int n5 = Math.max(n3, n4);
        for (BlockPos blockPos2 : BlockPos.withinManhattan(blockPos, n3, 0, n4)) {
            BlockPos blockPos3;
            if (blockPos2.distManhattan(blockPos) > n5) break;
            if (!DeltaFeature.isClear(worldGenLevel, blockPos2, deltaFeatureConfiguration)) continue;
            if (bl3) {
                bl = true;
                this.setBlock(worldGenLevel, blockPos2, deltaFeatureConfiguration.rim());
            }
            if (!DeltaFeature.isClear(worldGenLevel, blockPos3 = blockPos2.offset(n, 0, n2), deltaFeatureConfiguration)) continue;
            bl = true;
            this.setBlock(worldGenLevel, blockPos3, deltaFeatureConfiguration.contents());
        }
        return bl;
    }

    private static boolean isClear(LevelAccessor levelAccessor, BlockPos blockPos, DeltaFeatureConfiguration deltaFeatureConfiguration) {
        BlockState blockState = levelAccessor.getBlockState(blockPos);
        if (blockState.is(deltaFeatureConfiguration.contents().getBlock())) {
            return false;
        }
        if (CANNOT_REPLACE.contains((Object)blockState.getBlock())) {
            return false;
        }
        for (Direction direction : DIRECTIONS) {
            boolean bl = levelAccessor.getBlockState(blockPos.relative(direction)).isAir();
            if ((!bl || direction == Direction.UP) && (bl || direction != Direction.UP)) continue;
            return false;
        }
        return true;
    }
}

