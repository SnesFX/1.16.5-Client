/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BonusChestFeature
extends Feature<NoneFeatureConfiguration> {
    public BonusChestFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        List list = IntStream.rangeClosed(chunkPos.getMinBlockX(), chunkPos.getMaxBlockX()).boxed().collect(Collectors.toList());
        Collections.shuffle(list, random);
        List list2 = IntStream.rangeClosed(chunkPos.getMinBlockZ(), chunkPos.getMaxBlockZ()).boxed().collect(Collectors.toList());
        Collections.shuffle(list2, random);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (Integer n : list) {
            for (Integer n2 : list2) {
                mutableBlockPos.set(n, 0, n2);
                BlockPos blockPos2 = worldGenLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mutableBlockPos);
                if (!worldGenLevel.isEmptyBlock(blockPos2) && !worldGenLevel.getBlockState(blockPos2).getCollisionShape(worldGenLevel, blockPos2).isEmpty()) continue;
                worldGenLevel.setBlock(blockPos2, Blocks.CHEST.defaultBlockState(), 2);
                RandomizableContainerBlockEntity.setLootTable(worldGenLevel, random, blockPos2, BuiltInLootTables.SPAWN_BONUS_CHEST);
                BlockState blockState = Blocks.TORCH.defaultBlockState();
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    BlockPos blockPos3 = blockPos2.relative(direction);
                    if (!blockState.canSurvive(worldGenLevel, blockPos3)) continue;
                    worldGenLevel.setBlock(blockPos3, blockState, 2);
                }
                return true;
            }
        }
        return false;
    }
}

