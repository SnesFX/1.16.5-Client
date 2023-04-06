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
import net.minecraft.core.Direction;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class UnderwaterCaveWorldCarver
extends CaveWorldCarver {
    public UnderwaterCaveWorldCarver(Codec<ProbabilityFeatureConfiguration> codec) {
        super(codec, 256);
        this.replaceableBlocks = ImmutableSet.of((Object)Blocks.STONE, (Object)Blocks.GRANITE, (Object)Blocks.DIORITE, (Object)Blocks.ANDESITE, (Object)Blocks.DIRT, (Object)Blocks.COARSE_DIRT, (Object[])new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.SAND, Blocks.GRAVEL, Blocks.WATER, Blocks.LAVA, Blocks.OBSIDIAN, Blocks.AIR, Blocks.CAVE_AIR, Blocks.PACKED_ICE});
    }

    @Override
    protected boolean hasWater(ChunkAccess chunkAccess, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        return false;
    }

    @Override
    protected boolean carveBlock(ChunkAccess chunkAccess, Function<BlockPos, Biome> function, BitSet bitSet, Random random, BlockPos.MutableBlockPos mutableBlockPos, BlockPos.MutableBlockPos mutableBlockPos2, BlockPos.MutableBlockPos mutableBlockPos3, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, MutableBoolean mutableBoolean) {
        return UnderwaterCaveWorldCarver.carveBlock(this, chunkAccess, bitSet, random, mutableBlockPos, n, n2, n3, n4, n5, n6, n7, n8);
    }

    protected static boolean carveBlock(WorldCarver<?> worldCarver, ChunkAccess chunkAccess, BitSet bitSet, Random random, BlockPos.MutableBlockPos mutableBlockPos, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        if (n7 >= n) {
            return false;
        }
        int n9 = n6 | n8 << 4 | n7 << 8;
        if (bitSet.get(n9)) {
            return false;
        }
        bitSet.set(n9);
        mutableBlockPos.set(n4, n7, n5);
        BlockState blockState = chunkAccess.getBlockState(mutableBlockPos);
        if (!worldCarver.canReplaceBlock(blockState)) {
            return false;
        }
        if (n7 == 10) {
            float f = random.nextFloat();
            if ((double)f < 0.25) {
                chunkAccess.setBlockState(mutableBlockPos, Blocks.MAGMA_BLOCK.defaultBlockState(), false);
                chunkAccess.getBlockTicks().scheduleTick(mutableBlockPos, Blocks.MAGMA_BLOCK, 0);
            } else {
                chunkAccess.setBlockState(mutableBlockPos, Blocks.OBSIDIAN.defaultBlockState(), false);
            }
            return true;
        }
        if (n7 < 10) {
            chunkAccess.setBlockState(mutableBlockPos, Blocks.LAVA.defaultBlockState(), false);
            return false;
        }
        boolean bl = false;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            int n10 = n4 + direction.getStepX();
            int n11 = n5 + direction.getStepZ();
            if (n10 >> 4 == n2 && n11 >> 4 == n3 && !chunkAccess.getBlockState(mutableBlockPos.set(n10, n7, n11)).isAir()) continue;
            chunkAccess.setBlockState(mutableBlockPos, WATER.createLegacyBlock(), false);
            chunkAccess.getLiquidTicks().scheduleTick(mutableBlockPos, WATER.getType(), 0);
            bl = true;
            break;
        }
        mutableBlockPos.set(n4, n7, n5);
        if (!bl) {
            chunkAccess.setBlockState(mutableBlockPos, WATER.createLegacyBlock(), false);
            return true;
        }
        return true;
    }
}

