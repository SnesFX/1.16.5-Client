/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public class FlatLevelSource
extends ChunkGenerator {
    public static final Codec<FlatLevelSource> CODEC = FlatLevelGeneratorSettings.CODEC.fieldOf("settings").xmap(FlatLevelSource::new, FlatLevelSource::settings).codec();
    private final FlatLevelGeneratorSettings settings;

    public FlatLevelSource(FlatLevelGeneratorSettings flatLevelGeneratorSettings) {
        super(new FixedBiomeSource(flatLevelGeneratorSettings.getBiomeFromSettings()), new FixedBiomeSource(flatLevelGeneratorSettings.getBiome()), flatLevelGeneratorSettings.structureSettings(), 0L);
        this.settings = flatLevelGeneratorSettings;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long l) {
        return this;
    }

    public FlatLevelGeneratorSettings settings() {
        return this.settings;
    }

    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion worldGenRegion, ChunkAccess chunkAccess) {
    }

    @Override
    public int getSpawnHeight() {
        BlockState[] arrblockState = this.settings.getLayers();
        for (int i = 0; i < arrblockState.length; ++i) {
            BlockState blockState;
            BlockState blockState2 = blockState = arrblockState[i] == null ? Blocks.AIR.defaultBlockState() : arrblockState[i];
            if (Heightmap.Types.MOTION_BLOCKING.isOpaque().test(blockState)) continue;
            return i - 1;
        }
        return arrblockState.length;
    }

    @Override
    public void fillFromNoise(LevelAccessor levelAccessor, StructureFeatureManager structureFeatureManager, ChunkAccess chunkAccess) {
        BlockState[] arrblockState = this.settings.getLayers();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        Heightmap heightmap = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap heightmap2 = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        for (int i = 0; i < arrblockState.length; ++i) {
            BlockState blockState = arrblockState[i];
            if (blockState == null) continue;
            for (int j = 0; j < 16; ++j) {
                for (int k = 0; k < 16; ++k) {
                    chunkAccess.setBlockState(mutableBlockPos.set(j, i, k), blockState, false);
                    heightmap.update(j, i, k, blockState);
                    heightmap2.update(j, i, k, blockState);
                }
            }
        }
    }

    @Override
    public int getBaseHeight(int n, int n2, Heightmap.Types types) {
        BlockState[] arrblockState = this.settings.getLayers();
        for (int i = arrblockState.length - 1; i >= 0; --i) {
            BlockState blockState = arrblockState[i];
            if (blockState == null || !types.isOpaque().test(blockState)) continue;
            return i + 1;
        }
        return 0;
    }

    @Override
    public BlockGetter getBaseColumn(int n2, int n3) {
        return new NoiseColumn((BlockState[])Arrays.stream(this.settings.getLayers()).map(blockState -> blockState == null ? Blocks.AIR.defaultBlockState() : blockState).toArray(n -> new BlockState[n]));
    }
}

