/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.StructureSettings;

public class DebugLevelSource
extends ChunkGenerator {
    public static final Codec<DebugLevelSource> CODEC = RegistryLookupCodec.create(Registry.BIOME_REGISTRY).xmap(DebugLevelSource::new, DebugLevelSource::biomes).stable().codec();
    private static final List<BlockState> ALL_BLOCKS = StreamSupport.stream(Registry.BLOCK.spliterator(), false).flatMap(block -> block.getStateDefinition().getPossibleStates().stream()).collect(Collectors.toList());
    private static final int GRID_WIDTH = Mth.ceil(Mth.sqrt(ALL_BLOCKS.size()));
    private static final int GRID_HEIGHT = Mth.ceil((float)ALL_BLOCKS.size() / (float)GRID_WIDTH);
    protected static final BlockState AIR = Blocks.AIR.defaultBlockState();
    protected static final BlockState BARRIER = Blocks.BARRIER.defaultBlockState();
    private final Registry<Biome> biomes;

    public DebugLevelSource(Registry<Biome> registry) {
        super(new FixedBiomeSource(registry.getOrThrow(Biomes.PLAINS)), new StructureSettings(false));
        this.biomes = registry;
    }

    public Registry<Biome> biomes() {
        return this.biomes;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long l) {
        return this;
    }

    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion worldGenRegion, ChunkAccess chunkAccess) {
    }

    @Override
    public void applyCarvers(long l, BiomeManager biomeManager, ChunkAccess chunkAccess, GenerationStep.Carving carving) {
    }

    @Override
    public void applyBiomeDecoration(WorldGenRegion worldGenRegion, StructureFeatureManager structureFeatureManager) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        int n = worldGenRegion.getCenterX();
        int n2 = worldGenRegion.getCenterZ();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                int n3 = (n << 4) + i;
                int n4 = (n2 << 4) + j;
                worldGenRegion.setBlock(mutableBlockPos.set(n3, 60, n4), BARRIER, 2);
                BlockState blockState = DebugLevelSource.getBlockStateFor(n3, n4);
                if (blockState == null) continue;
                worldGenRegion.setBlock(mutableBlockPos.set(n3, 70, n4), blockState, 2);
            }
        }
    }

    @Override
    public void fillFromNoise(LevelAccessor levelAccessor, StructureFeatureManager structureFeatureManager, ChunkAccess chunkAccess) {
    }

    @Override
    public int getBaseHeight(int n, int n2, Heightmap.Types types) {
        return 0;
    }

    @Override
    public BlockGetter getBaseColumn(int n, int n2) {
        return new NoiseColumn(new BlockState[0]);
    }

    public static BlockState getBlockStateFor(int n, int n2) {
        int n3;
        BlockState blockState = AIR;
        if (n > 0 && n2 > 0 && n % 2 != 0 && n2 % 2 != 0 && (n /= 2) <= GRID_WIDTH && (n2 /= 2) <= GRID_HEIGHT && (n3 = Mth.abs(n * GRID_WIDTH + n2)) < ALL_BLOCKS.size()) {
            blockState = ALL_BLOCKS.get(n3);
        }
        return blockState;
    }
}

