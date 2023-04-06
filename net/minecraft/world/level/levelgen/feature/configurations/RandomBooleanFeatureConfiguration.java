/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P2
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class RandomBooleanFeatureConfiguration
implements FeatureConfiguration {
    public static final Codec<RandomBooleanFeatureConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ConfiguredFeature.CODEC.fieldOf("feature_true").forGetter(randomBooleanFeatureConfiguration -> randomBooleanFeatureConfiguration.featureTrue), (App)ConfiguredFeature.CODEC.fieldOf("feature_false").forGetter(randomBooleanFeatureConfiguration -> randomBooleanFeatureConfiguration.featureFalse)).apply((Applicative)instance, (arg_0, arg_1) -> RandomBooleanFeatureConfiguration.new(arg_0, arg_1)));
    public final Supplier<ConfiguredFeature<?, ?>> featureTrue;
    public final Supplier<ConfiguredFeature<?, ?>> featureFalse;

    public RandomBooleanFeatureConfiguration(Supplier<ConfiguredFeature<?, ?>> supplier, Supplier<ConfiguredFeature<?, ?>> supplier2) {
        this.featureTrue = supplier;
        this.featureFalse = supplier2;
    }

    @Override
    public Stream<ConfiguredFeature<?, ?>> getFeatures() {
        return Stream.concat(this.featureTrue.get().getFeatures(), this.featureFalse.get().getFeatures());
    }
}

