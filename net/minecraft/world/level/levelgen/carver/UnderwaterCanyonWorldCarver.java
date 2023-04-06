/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.serialization.Codec
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package net.minecraft.world.level.levelgen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.carver.CanyonWorldCarver;
import net.minecraft.world.level.levelgen.carver.UnderwaterCaveWorldCarver;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class UnderwaterCanyonWorldCarver
extends CanyonWorldCarver {
    public UnderwaterCanyonWorldCarver(Codec<ProbabilityFeatureConfiguration> codec) {
        super(codec);
        this.replaceableBlocks = ImmutableSet.of((Object)Blocks.STONE, (Object)Blocks.GRANITE, (Object)Blocks.DIORITE, (Object)Blocks.ANDESITE, (Object)Blocks.DIRT, (Object)Blocks.COARSE_DIRT, (Object[])new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.SAND, Blocks.GRAVEL, Blocks.WATER, Blocks.LAVA, Blocks.OBSIDIAN, Blocks.AIR, Blocks.CAVE_AIR});
    }

    @Override
    protected boolean hasWater(ChunkAccess chunkAccess, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        return false;
    }

    @Override
    protected boolean carveBlock(ChunkAccess chunkAccess, Function<BlockPos, Biome> function, BitSet bitSet, Random random, BlockPos.MutableBlockPos mutableBlockPos, BlockPos.MutableBlockPos mutableBlockPos2, BlockPos.MutableBlockPos mutableBlockPos3, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, MutableBoolean mutableBoolean) {
        return UnderwaterCaveWorldCarver.carveBlock(this, chunkAccess, bitSet, random, mutableBlockPos, n, n2, n3, n4, n5, n6, n7, n8);
    }
}

