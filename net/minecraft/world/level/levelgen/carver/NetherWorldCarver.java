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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class NetherWorldCarver
extends CaveWorldCarver {
    public NetherWorldCarver(Codec<ProbabilityFeatureConfiguration> codec) {
        super(codec, 128);
        this.replaceableBlocks = ImmutableSet.of((Object)Blocks.STONE, (Object)Blocks.GRANITE, (Object)Blocks.DIORITE, (Object)Blocks.ANDESITE, (Object)Blocks.DIRT, (Object)Blocks.COARSE_DIRT, (Object[])new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.NETHER_WART_BLOCK, Blocks.WARPED_WART_BLOCK, Blocks.BASALT, Blocks.BLACKSTONE});
        this.liquids = ImmutableSet.of((Object)Fluids.LAVA, (Object)Fluids.WATER);
    }

    @Override
    protected int getCaveBound() {
        return 10;
    }

    @Override
    protected float getThickness(Random random) {
        return (random.nextFloat() * 2.0f + random.nextFloat()) * 2.0f;
    }

    @Override
    protected double getYScale() {
        return 5.0;
    }

    @Override
    protected int getCaveY(Random random) {
        return random.nextInt(this.genHeight);
    }

    @Override
    protected boolean carveBlock(ChunkAccess chunkAccess, Function<BlockPos, Biome> function, BitSet bitSet, Random random, BlockPos.MutableBlockPos mutableBlockPos, BlockPos.MutableBlockPos mutableBlockPos2, BlockPos.MutableBlockPos mutableBlockPos3, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, MutableBoolean mutableBoolean) {
        int n9 = n6 | n8 << 4 | n7 << 8;
        if (bitSet.get(n9)) {
            return false;
        }
        bitSet.set(n9);
        mutableBlockPos.set(n4, n7, n5);
        if (this.canReplaceBlock(chunkAccess.getBlockState(mutableBlockPos))) {
            BlockState blockState = n7 <= 31 ? LAVA.createLegacyBlock() : CAVE_AIR;
            chunkAccess.setBlockState(mutableBlockPos, blockState, false);
            return true;
        }
        return false;
    }
}

