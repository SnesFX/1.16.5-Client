/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P4
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function4
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Keyable
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.data.worldgen.SurfaceBuilders;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.AbstractFlowerFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeGenerationSettings {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final BiomeGenerationSettings EMPTY = new BiomeGenerationSettings(() -> SurfaceBuilders.NOPE, (Map<GenerationStep.Carving, List<Supplier<ConfiguredWorldCarver<?>>>>)ImmutableMap.of(), (List<List<Supplier<ConfiguredFeature<?, ?>>>>)ImmutableList.of(), (List<Supplier<ConfiguredStructureFeature<?, ?>>>)ImmutableList.of());
    public static final MapCodec<BiomeGenerationSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)ConfiguredSurfaceBuilder.CODEC.fieldOf("surface_builder").forGetter(biomeGenerationSettings -> biomeGenerationSettings.surfaceBuilder), (App)Codec.simpleMap(GenerationStep.Carving.CODEC, (Codec)ConfiguredWorldCarver.LIST_CODEC.promotePartial(Util.prefix("Carver: ", ((Logger)LOGGER)::error)), (Keyable)StringRepresentable.keys(GenerationStep.Carving.values())).fieldOf("carvers").forGetter(biomeGenerationSettings -> biomeGenerationSettings.carvers), (App)ConfiguredFeature.LIST_CODEC.promotePartial(Util.prefix("Feature: ", ((Logger)LOGGER)::error)).listOf().fieldOf("features").forGetter(biomeGenerationSettings -> biomeGenerationSettings.features), (App)ConfiguredStructureFeature.LIST_CODEC.promotePartial(Util.prefix("Structure start: ", ((Logger)LOGGER)::error)).fieldOf("starts").forGetter(biomeGenerationSettings -> biomeGenerationSettings.structureStarts)).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3) -> BiomeGenerationSettings.new(arg_0, arg_1, arg_2, arg_3)));
    private final Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder;
    private final Map<GenerationStep.Carving, List<Supplier<ConfiguredWorldCarver<?>>>> carvers;
    private final List<List<Supplier<ConfiguredFeature<?, ?>>>> features;
    private final List<Supplier<ConfiguredStructureFeature<?, ?>>> structureStarts;
    private final List<ConfiguredFeature<?, ?>> flowerFeatures;

    private BiomeGenerationSettings(Supplier<ConfiguredSurfaceBuilder<?>> supplier, Map<GenerationStep.Carving, List<Supplier<ConfiguredWorldCarver<?>>>> map, List<List<Supplier<ConfiguredFeature<?, ?>>>> list, List<Supplier<ConfiguredStructureFeature<?, ?>>> list2) {
        this.surfaceBuilder = supplier;
        this.carvers = map;
        this.features = list;
        this.structureStarts = list2;
        this.flowerFeatures = (List)list.stream().flatMap(Collection::stream).map(Supplier::get).flatMap(ConfiguredFeature::getFeatures).filter(configuredFeature -> configuredFeature.feature == Feature.FLOWER).collect(ImmutableList.toImmutableList());
    }

    public List<Supplier<ConfiguredWorldCarver<?>>> getCarvers(GenerationStep.Carving carving) {
        return (List)this.carvers.getOrDefault(carving, (List<Supplier<ConfiguredWorldCarver<?>>>)ImmutableList.of());
    }

    public boolean isValidStart(StructureFeature<?> structureFeature) {
        return this.structureStarts.stream().anyMatch(supplier -> ((ConfiguredStructureFeature)supplier.get()).feature == structureFeature);
    }

    public Collection<Supplier<ConfiguredStructureFeature<?, ?>>> structures() {
        return this.structureStarts;
    }

    public ConfiguredStructureFeature<?, ?> withBiomeConfig(ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
        return (ConfiguredStructureFeature)DataFixUtils.orElse(this.structureStarts.stream().map(Supplier::get).filter(configuredStructureFeature2 -> configuredStructureFeature2.feature == configuredStructureFeature.feature).findAny(), configuredStructureFeature);
    }

    public List<ConfiguredFeature<?, ?>> getFlowerFeatures() {
        return this.flowerFeatures;
    }

    public List<List<Supplier<ConfiguredFeature<?, ?>>>> features() {
        return this.features;
    }

    public Supplier<ConfiguredSurfaceBuilder<?>> getSurfaceBuilder() {
        return this.surfaceBuilder;
    }

    public SurfaceBuilderConfiguration getSurfaceBuilderConfig() {
        return this.surfaceBuilder.get().config();
    }

    public static class Builder {
        private Optional<Supplier<ConfiguredSurfaceBuilder<?>>> surfaceBuilder = Optional.empty();
        private final Map<GenerationStep.Carving, List<Supplier<ConfiguredWorldCarver<?>>>> carvers = Maps.newLinkedHashMap();
        private final List<List<Supplier<ConfiguredFeature<?, ?>>>> features = Lists.newArrayList();
        private final List<Supplier<ConfiguredStructureFeature<?, ?>>> structureStarts = Lists.newArrayList();

        public Builder surfaceBuilder(ConfiguredSurfaceBuilder<?> configuredSurfaceBuilder) {
            return this.surfaceBuilder(() -> configuredSurfaceBuilder);
        }

        public Builder surfaceBuilder(Supplier<ConfiguredSurfaceBuilder<?>> supplier) {
            this.surfaceBuilder = Optional.of(supplier);
            return this;
        }

        public Builder addFeature(GenerationStep.Decoration decoration, ConfiguredFeature<?, ?> configuredFeature) {
            return this.addFeature(decoration.ordinal(), () -> configuredFeature);
        }

        public Builder addFeature(int n, Supplier<ConfiguredFeature<?, ?>> supplier) {
            this.addFeatureStepsUpTo(n);
            this.features.get(n).add(supplier);
            return this;
        }

        public <C extends CarverConfiguration> Builder addCarver(GenerationStep.Carving carving2, ConfiguredWorldCarver<C> configuredWorldCarver) {
            this.carvers.computeIfAbsent(carving2, carving -> Lists.newArrayList()).add(() -> configuredWorldCarver);
            return this;
        }

        public Builder addStructureStart(ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
            this.structureStarts.add(() -> configuredStructureFeature);
            return this;
        }

        private void addFeatureStepsUpTo(int n) {
            while (this.features.size() <= n) {
                this.features.add(Lists.newArrayList());
            }
        }

        public BiomeGenerationSettings build() {
            return new BiomeGenerationSettings(this.surfaceBuilder.orElseThrow(() -> new IllegalStateException("Missing surface builder")), (Map)this.carvers.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ImmutableList.copyOf((Collection)((Collection)entry.getValue())))), (List)this.features.stream().map(ImmutableList::copyOf).collect(ImmutableList.toImmutableList()), (List)ImmutableList.copyOf(this.structureStarts));
        }
    }

}

