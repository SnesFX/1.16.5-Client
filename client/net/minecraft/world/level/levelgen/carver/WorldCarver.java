/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package net.minecraft.world.level.levelgen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.carver.CanyonWorldCarver;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.NetherWorldCarver;
import net.minecraft.world.level.levelgen.carver.UnderwaterCanyonWorldCarver;
import net.minecraft.world.level.levelgen.carver.UnderwaterCaveWorldCarver;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class WorldCarver<C extends CarverConfiguration> {
    public static final WorldCarver<ProbabilityFeatureConfiguration> CAVE = WorldCarver.register("cave", new CaveWorldCarver(ProbabilityFeatureConfiguration.CODEC, 256));
    public static final WorldCarver<ProbabilityFeatureConfiguration> NETHER_CAVE = WorldCarver.register("nether_cave", new NetherWorldCarver(ProbabilityFeatureConfiguration.CODEC));
    public static final WorldCarver<ProbabilityFeatureConfiguration> CANYON = WorldCarver.register("canyon", new CanyonWorldCarver(ProbabilityFeatureConfiguration.CODEC));
    public static final WorldCarver<ProbabilityFeatureConfiguration> UNDERWATER_CANYON = WorldCarver.register("underwater_canyon", new UnderwaterCanyonWorldCarver(ProbabilityFeatureConfiguration.CODEC));
    public static final WorldCarver<ProbabilityFeatureConfiguration> UNDERWATER_CAVE = WorldCarver.register("underwater_cave", new UnderwaterCaveWorldCarver(ProbabilityFeatureConfiguration.CODEC));
    protected static final BlockState AIR = Blocks.AIR.defaultBlockState();
    protected static final BlockState CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();
    protected static final FluidState WATER = Fluids.WATER.defaultFluidState();
    protected static final FluidState LAVA = Fluids.LAVA.defaultFluidState();
    protected Set<Block> replaceableBlocks = ImmutableSet.of((Object)Blocks.STONE, (Object)Blocks.GRANITE, (Object)Blocks.DIORITE, (Object)Blocks.ANDESITE, (Object)Blocks.DIRT, (Object)Blocks.COARSE_DIRT, (Object[])new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE});
    protected Set<Fluid> liquids = ImmutableSet.of((Object)Fluids.WATER);
    private final Codec<ConfiguredWorldCarver<C>> configuredCodec;
    protected final int genHeight;

    private static <C extends CarverConfiguration, F extends WorldCarver<C>> F register(String string, F f) {
        return (F)Registry.register(Registry.CARVER, string, f);
    }

    public WorldCarver(Codec<C> codec, int n) {
        this.genHeight = n;
        this.configuredCodec = codec.fieldOf("config").xmap(this::configured, ConfiguredWorldCarver::config).codec();
    }

    public ConfiguredWorldCarver<C> configured(C c) {
        return new ConfiguredWorldCarver<C>(this, c);
    }

    public Codec<ConfiguredWorldCarver<C>> configuredCodec() {
        return this.configuredCodec;
    }

    public int getRange() {
        return 4;
    }

    protected boolean carveSphere(ChunkAccess chunkAccess, Function<BlockPos, Biome> function, long l, int n, int n2, int n3, double d, double d2, double d3, double d4, double d5, BitSet bitSet) {
        int n4;
        int n5;
        int n6;
        int n7;
        int n8;
        Random random = new Random(l + (long)n2 + (long)n3);
        double d6 = n2 * 16 + 8;
        double d7 = n3 * 16 + 8;
        if (d < d6 - 16.0 - d4 * 2.0 || d3 < d7 - 16.0 - d4 * 2.0 || d > d6 + 16.0 + d4 * 2.0 || d3 > d7 + 16.0 + d4 * 2.0) {
            return false;
        }
        int n9 = Math.max(Mth.floor(d - d4) - n2 * 16 - 1, 0);
        if (this.hasWater(chunkAccess, n2, n3, n9, n7 = Math.min(Mth.floor(d + d4) - n2 * 16 + 1, 16), n8 = Math.max(Mth.floor(d2 - d5) - 1, 1), n4 = Math.min(Mth.floor(d2 + d5) + 1, this.genHeight - 8), n6 = Math.max(Mth.floor(d3 - d4) - n3 * 16 - 1, 0), n5 = Math.min(Mth.floor(d3 + d4) - n3 * 16 + 1, 16))) {
            return false;
        }
        boolean bl = false;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos mutableBlockPos2 = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos mutableBlockPos3 = new BlockPos.MutableBlockPos();
        for (int i = n9; i < n7; ++i) {
            int n10 = i + n2 * 16;
            double d8 = ((double)n10 + 0.5 - d) / d4;
            for (int j = n6; j < n5; ++j) {
                int n11 = j + n3 * 16;
                double d9 = ((double)n11 + 0.5 - d3) / d4;
                if (d8 * d8 + d9 * d9 >= 1.0) continue;
                MutableBoolean mutableBoolean = new MutableBoolean(false);
                for (int k = n4; k > n8; --k) {
                    double d10 = ((double)k - 0.5 - d2) / d5;
                    if (this.skip(d8, d10, d9, k)) continue;
                    bl |= this.carveBlock(chunkAccess, function, bitSet, random, mutableBlockPos, mutableBlockPos2, mutableBlockPos3, n, n2, n3, n10, n11, i, k, j, mutableBoolean);
                }
            }
        }
        return bl;
    }

    protected boolean carveBlock(ChunkAccess chunkAccess, Function<BlockPos, Biome> function, BitSet bitSet, Random random, BlockPos.MutableBlockPos mutableBlockPos, BlockPos.MutableBlockPos mutableBlockPos2, BlockPos.MutableBlockPos mutableBlockPos3, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, MutableBoolean mutableBoolean) {
        int n9 = n6 | n8 << 4 | n7 << 8;
        if (bitSet.get(n9)) {
            return false;
        }
        bitSet.set(n9);
        mutableBlockPos.set(n4, n7, n5);
        BlockState blockState = chunkAccess.getBlockState(mutableBlockPos);
        BlockState blockState2 = chunkAccess.getBlockState(mutableBlockPos2.setWithOffset(mutableBlockPos, Direction.UP));
        if (blockState.is(Blocks.GRASS_BLOCK) || blockState.is(Blocks.MYCELIUM)) {
            mutableBoolean.setTrue();
        }
        if (!this.canReplaceBlock(blockState, blockState2)) {
            return false;
        }
        if (n7 < 11) {
            chunkAccess.setBlockState(mutableBlockPos, LAVA.createLegacyBlock(), false);
        } else {
            chunkAccess.setBlockState(mutableBlockPos, CAVE_AIR, false);
            if (mutableBoolean.isTrue()) {
                mutableBlockPos3.setWithOffset(mutableBlockPos, Direction.DOWN);
                if (chunkAccess.getBlockState(mutableBlockPos3).is(Blocks.DIRT)) {
                    chunkAccess.setBlockState(mutableBlockPos3, function.apply(mutableBlockPos).getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial(), false);
                }
            }
        }
        return true;
    }

    public abstract boolean carve(ChunkAccess var1, Function<BlockPos, Biome> var2, Random var3, int var4, int var5, int var6, int var7, int var8, BitSet var9, C var10);

    public abstract boolean isStartChunk(Random var1, int var2, int var3, C var4);

    protected boolean canReplaceBlock(BlockState blockState) {
        return this.replaceableBlocks.contains(blockState.getBlock());
    }

    protected boolean canReplaceBlock(BlockState blockState, BlockState blockState2) {
        return this.canReplaceBlock(blockState) || (blockState.is(Blocks.SAND) || blockState.is(Blocks.GRAVEL)) && !blockState2.getFluidState().is(FluidTags.WATER);
    }

    protected boolean hasWater(ChunkAccess chunkAccess, int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = n3; i < n4; ++i) {
            for (int j = n7; j < n8; ++j) {
                for (int k = n5 - 1; k <= n6 + 1; ++k) {
                    if (this.liquids.contains(chunkAccess.getFluidState(mutableBlockPos.set(i + n * 16, k, j + n2 * 16)).getType())) {
                        return true;
                    }
                    if (k == n6 + 1 || this.isEdge(n3, n4, n7, n8, i, j)) continue;
                    k = n6;
                }
            }
        }
        return false;
    }

    private boolean isEdge(int n, int n2, int n3, int n4, int n5, int n6) {
        return n5 == n || n5 == n2 - 1 || n6 == n3 || n6 == n4 - 1;
    }

    protected boolean canReach(int n, int n2, double d, double d2, int n3, int n4, float f) {
        double d3 = n * 16 + 8;
        double d4 = d - d3;
        double d5 = n2 * 16 + 8;
        double d6 = d2 - d5;
        double d7 = n4 - n3;
        double d8 = f + 2.0f + 16.0f;
        return d4 * d4 + d6 * d6 - d7 * d7 <= d8 * d8;
    }

    protected abstract boolean skip(double var1, double var3, double var5, int var7);
}

