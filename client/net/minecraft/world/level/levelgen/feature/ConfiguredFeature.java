/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Decoratable;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratedFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.ConfiguredDecorator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfiguredFeature<FC extends FeatureConfiguration, F extends Feature<FC>>
implements Decoratable<ConfiguredFeature<?, ?>> {
    public static final Codec<ConfiguredFeature<?, ?>> DIRECT_CODEC = Registry.FEATURE.dispatch(configuredFeature -> configuredFeature.feature, Feature::configuredCodec);
    public static final Codec<Supplier<ConfiguredFeature<?, ?>>> CODEC = RegistryFileCodec.create(Registry.CONFIGURED_FEATURE_REGISTRY, DIRECT_CODEC);
    public static final Codec<List<Supplier<ConfiguredFeature<?, ?>>>> LIST_CODEC = RegistryFileCodec.homogeneousList(Registry.CONFIGURED_FEATURE_REGISTRY, DIRECT_CODEC);
    public static final Logger LOGGER = LogManager.getLogger();
    public final F feature;
    public final FC config;

    public ConfiguredFeature(F f, FC FC) {
        this.feature = f;
        this.config = FC;
    }

    public F feature() {
        return this.feature;
    }

    public FC config() {
        return this.config;
    }

    @Override
    public ConfiguredFeature<?, ?> decorated(ConfiguredDecorator<?> configuredDecorator) {
        return Feature.DECORATED.configured(new DecoratedFeatureConfiguration(() -> this, configuredDecorator));
    }

    public WeightedConfiguredFeature weighted(float f) {
        return new WeightedConfiguredFeature(this, f);
    }

    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos) {
        return ((Feature)this.feature).place(worldGenLevel, chunkGenerator, random, blockPos, this.config);
    }

    public Stream<ConfiguredFeature<?, ?>> getFeatures() {
        return Stream.concat(Stream.of(this), this.config.getFeatures());
    }

    @Override
    public /* synthetic */ Object decorated(ConfiguredDecorator configuredDecorator) {
        return this.decorated(configuredDecorator);
    }
}

