/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.WeightedConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class RandomFeatureConfiguration
implements FeatureConfiguration {
    public static final Codec<RandomFeatureConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.apply2((arg_0, arg_1) -> RandomFeatureConfiguration.new(arg_0, arg_1), (App)WeightedConfiguredFeature.CODEC.listOf().fieldOf("features").forGetter(randomFeatureConfiguration -> randomFeatureConfiguration.features), (App)ConfiguredFeature.CODEC.fieldOf("default").forGetter(randomFeatureConfiguration -> randomFeatureConfiguration.defaultFeature)));
    public final List<WeightedConfiguredFeature> features;
    public final Supplier<ConfiguredFeature<?, ?>> defaultFeature;

    public RandomFeatureConfiguration(List<WeightedConfiguredFeature> list, ConfiguredFeature<?, ?> configuredFeature) {
        this(list, () -> configuredFeature);
    }

    private RandomFeatureConfiguration(List<WeightedConfiguredFeature> list, Supplier<ConfiguredFeature<?, ?>> supplier) {
        this.features = list;
        this.defaultFeature = supplier;
    }

    @Override
    public Stream<ConfiguredFeature<?, ?>> getFeatures() {
        return Stream.concat(this.features.stream().flatMap(weightedConfiguredFeature -> weightedConfiguredFeature.feature.get().getFeatures()), this.defaultFeature.get().getFeatures());
    }
}

