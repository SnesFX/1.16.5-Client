/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.surfacebuilders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

public abstract class NetherCappedSurfaceBuilder
extends SurfaceBuilder<SurfaceBuilderBaseConfiguration> {
    private long seed;
    private ImmutableMap<BlockState, PerlinNoise> floorNoises = ImmutableMap.of();
    private ImmutableMap<BlockState, PerlinNoise> ceilingNoises = ImmutableMap.of();
    private PerlinNoise patchNoise;

    public NetherCappedSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> codec) {
        super(codec);
    }

    @Override
    public void apply(Random random, ChunkAccess chunkAccess, Biome biome, int n, int n2, int n3, double d, BlockState blockState, BlockState blockState2, int n4, long l, SurfaceBuilderBaseConfiguration surfaceBuilderBaseConfiguration) {
        int n5 = n4 + 1;
        int n6 = n & 0xF;
        int n7 = n2 & 0xF;
        int n8 = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        int n9 = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
        double d2 = 0.03125;
        boolean bl = this.patchNoise.getValue((double)n * 0.03125, 109.0, (double)n2 * 0.03125) * 75.0 + random.nextDouble() > 0.0;
        BlockState blockState3 = (BlockState)this.ceilingNoises.entrySet().stream().max(Comparator.comparing(entry -> ((PerlinNoise)entry.getValue()).getValue(n, n4, n2))).get().getKey();
        BlockState blockState4 = (BlockState)this.floorNoises.entrySet().stream().max(Comparator.comparing(entry -> ((PerlinNoise)entry.getValue()).getValue(n, n4, n2))).get().getKey();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        BlockState blockState5 = chunkAccess.getBlockState(mutableBlockPos.set(n6, 128, n7));
        for (int i = 127; i >= 0; --i) {
            int n10;
            mutableBlockPos.set(n6, i, n7);
            BlockState blockState6 = chunkAccess.getBlockState(mutableBlockPos);
            if (blockState5.is(blockState.getBlock()) && (blockState6.isAir() || blockState6 == blockState2)) {
                for (n10 = 0; n10 < n8; ++n10) {
                    mutableBlockPos.move(Direction.UP);
                    if (!chunkAccess.getBlockState(mutableBlockPos).is(blockState.getBlock())) break;
                    chunkAccess.setBlockState(mutableBlockPos, blockState3, false);
                }
                mutableBlockPos.set(n6, i, n7);
            }
            if ((blockState5.isAir() || blockState5 == blockState2) && blockState6.is(blockState.getBlock())) {
                for (n10 = 0; n10 < n9 && chunkAccess.getBlockState(mutableBlockPos).is(blockState.getBlock()); ++n10) {
                    if (bl && i >= n5 - 4 && i <= n5 + 1) {
                        chunkAccess.setBlockState(mutableBlockPos, this.getPatchBlockState(), false);
                    } else {
                        chunkAccess.setBlockState(mutableBlockPos, blockState4, false);
                    }
                    mutableBlockPos.move(Direction.DOWN);
                }
            }
            blockState5 = blockState6;
        }
    }

    @Override
    public void initNoise(long l) {
        if (this.seed != l || this.patchNoise == null || this.floorNoises.isEmpty() || this.ceilingNoises.isEmpty()) {
            this.floorNoises = NetherCappedSurfaceBuilder.initPerlinNoises(this.getFloorBlockStates(), l);
            this.ceilingNoises = NetherCappedSurfaceBuilder.initPerlinNoises(this.getCeilingBlockStates(), l + (long)this.floorNoises.size());
            this.patchNoise = new PerlinNoise(new WorldgenRandom(l + (long)this.floorNoises.size() + (long)this.ceilingNoises.size()), (List<Integer>)ImmutableList.of((Object)0));
        }
        this.seed = l;
    }

    private static ImmutableMap<BlockState, PerlinNoise> initPerlinNoises(ImmutableList<BlockState> immutableList, long l) {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        for (BlockState blockState : immutableList) {
            builder.put((Object)blockState, (Object)new PerlinNoise(new WorldgenRandom(l), (List<Integer>)ImmutableList.of((Object)-4)));
            ++l;
        }
        return builder.build();
    }

    protected abstract ImmutableList<BlockState> getFloorBlockStates();

    protected abstract ImmutableList<BlockState> getCeilingBlockStates();

    protected abstract BlockState getPatchBlockState();
}

