/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;

public class ConfiguredSurfaceBuilder<SC extends SurfaceBuilderConfiguration> {
    public static final Codec<ConfiguredSurfaceBuilder<?>> DIRECT_CODEC = Registry.SURFACE_BUILDER.dispatch(configuredSurfaceBuilder -> configuredSurfaceBuilder.surfaceBuilder, SurfaceBuilder::configuredCodec);
    public static final Codec<Supplier<ConfiguredSurfaceBuilder<?>>> CODEC = RegistryFileCodec.create(Registry.CONFIGURED_SURFACE_BUILDER_REGISTRY, DIRECT_CODEC);
    public final SurfaceBuilder<SC> surfaceBuilder;
    public final SC config;

    public ConfiguredSurfaceBuilder(SurfaceBuilder<SC> surfaceBuilder, SC SC) {
        this.surfaceBuilder = surfaceBuilder;
        this.config = SC;
    }

    public void apply(Random random, ChunkAccess chunkAccess, Biome biome, int n, int n2, int n3, double d, BlockState blockState, BlockState blockState2, int n4, long l) {
        this.surfaceBuilder.apply(random, chunkAccess, biome, n, n2, n3, d, blockState, blockState2, n4, l, this.config);
    }

    public void initNoise(long l) {
        this.surfaceBuilder.initNoise(l);
    }

    public SC config() {
        return this.config;
    }
}

