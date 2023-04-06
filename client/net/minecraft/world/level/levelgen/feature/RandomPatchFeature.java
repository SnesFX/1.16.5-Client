/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.blockplacers.BlockPlacer;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;

public class RandomPatchFeature
extends Feature<RandomPatchConfiguration> {
    public RandomPatchFeature(Codec<RandomPatchConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, RandomPatchConfiguration randomPatchConfiguration) {
        BlockState blockState = randomPatchConfiguration.stateProvider.getState(random, blockPos);
        BlockPos blockPos2 = randomPatchConfiguration.project ? worldGenLevel.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, blockPos) : blockPos;
        int n = 0;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < randomPatchConfiguration.tries; ++i) {
            mutableBlockPos.setWithOffset(blockPos2, random.nextInt(randomPatchConfiguration.xspread + 1) - random.nextInt(randomPatchConfiguration.xspread + 1), random.nextInt(randomPatchConfiguration.yspread + 1) - random.nextInt(randomPatchConfiguration.yspread + 1), random.nextInt(randomPatchConfiguration.zspread + 1) - random.nextInt(randomPatchConfiguration.zspread + 1));
            Vec3i vec3i = mutableBlockPos.below();
            BlockState blockState2 = worldGenLevel.getBlockState((BlockPos)vec3i);
            if (!worldGenLevel.isEmptyBlock(mutableBlockPos) && (!randomPatchConfiguration.canReplace || !worldGenLevel.getBlockState(mutableBlockPos).getMaterial().isReplaceable()) || !blockState.canSurvive(worldGenLevel, mutableBlockPos) || !randomPatchConfiguration.whitelist.isEmpty() && !randomPatchConfiguration.whitelist.contains(blockState2.getBlock()) || randomPatchConfiguration.blacklist.contains(blockState2) || randomPatchConfiguration.needWater && !worldGenLevel.getFluidState(((BlockPos)vec3i).west()).is(FluidTags.WATER) && !worldGenLevel.getFluidState(((BlockPos)vec3i).east()).is(FluidTags.WATER) && !worldGenLevel.getFluidState(((BlockPos)vec3i).north()).is(FluidTags.WATER) && !worldGenLevel.getFluidState(((BlockPos)vec3i).south()).is(FluidTags.WATER)) continue;
            randomPatchConfiguration.blockPlacer.place(worldGenLevel, mutableBlockPos, blockState, random);
            ++n;
        }
        return n > 0;
    }
}

