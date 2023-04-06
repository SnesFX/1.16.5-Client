/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.Lifecycle
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.data;

import com.google.common.collect.Maps;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.worldgen.Carvers;
import net.minecraft.data.worldgen.Features;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.data.worldgen.SurfaceBuilders;
import net.minecraft.data.worldgen.biome.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BuiltinRegistries {
    protected static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ResourceLocation, Supplier<?>> LOADERS = Maps.newLinkedHashMap();
    private static final WritableRegistry<WritableRegistry<?>> WRITABLE_REGISTRY = new MappedRegistry(ResourceKey.createRegistryKey(new ResourceLocation("root")), Lifecycle.experimental());
    public static final Registry<? extends Registry<?>> REGISTRY = WRITABLE_REGISTRY;
    public static final Registry<ConfiguredSurfaceBuilder<?>> CONFIGURED_SURFACE_BUILDER = BuiltinRegistries.registerSimple(Registry.CONFIGURED_SURFACE_BUILDER_REGISTRY, () -> SurfaceBuilders.NOPE);
    public static final Registry<ConfiguredWorldCarver<?>> CONFIGURED_CARVER = BuiltinRegistries.registerSimple(Registry.CONFIGURED_CARVER_REGISTRY, () -> Carvers.CAVE);
    public static final Registry<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE = BuiltinRegistries.registerSimple(Registry.CONFIGURED_FEATURE_REGISTRY, () -> Features.OAK);
    public static final Registry<ConfiguredStructureFeature<?, ?>> CONFIGURED_STRUCTURE_FEATURE = BuiltinRegistries.registerSimple(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, () -> StructureFeatures.MINESHAFT);
    public static final Registry<StructureProcessorList> PROCESSOR_LIST = BuiltinRegistries.registerSimple(Registry.PROCESSOR_LIST_REGISTRY, () -> ProcessorLists.ZOMBIE_PLAINS);
    public static final Registry<StructureTemplatePool> TEMPLATE_POOL = BuiltinRegistries.registerSimple(Registry.TEMPLATE_POOL_REGISTRY, Pools::bootstrap);
    public static final Registry<Biome> BIOME = BuiltinRegistries.registerSimple(Registry.BIOME_REGISTRY, () -> Biomes.PLAINS);
    public static final Registry<NoiseGeneratorSettings> NOISE_GENERATOR_SETTINGS = BuiltinRegistries.registerSimple(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, NoiseGeneratorSettings::bootstrap);

    private static <T> Registry<T> registerSimple(ResourceKey<? extends Registry<T>> resourceKey, Supplier<T> supplier) {
        return BuiltinRegistries.registerSimple(resourceKey, Lifecycle.stable(), supplier);
    }

    private static <T> Registry<T> registerSimple(ResourceKey<? extends Registry<T>> resourceKey, Lifecycle lifecycle, Supplier<T> supplier) {
        return BuiltinRegistries.internalRegister(resourceKey, new MappedRegistry(resourceKey, lifecycle), supplier, lifecycle);
    }

    private static <T, R extends WritableRegistry<T>> R internalRegister(ResourceKey<? extends Registry<T>> resourceKey, R r, Supplier<T> supplier, Lifecycle lifecycle) {
        ResourceLocation resourceLocation = resourceKey.location();
        LOADERS.put(resourceLocation, supplier);
        WritableRegistry<WritableRegistry<?>> writableRegistry = WRITABLE_REGISTRY;
        return writableRegistry.register(resourceKey, r, lifecycle);
    }

    public static <T> T register(Registry<? super T> registry, String string, T t) {
        return BuiltinRegistries.register(registry, new ResourceLocation(string), t);
    }

    public static <V, T extends V> T register(Registry<V> registry, ResourceLocation resourceLocation, T t) {
        return ((WritableRegistry)registry).register(ResourceKey.create(registry.key(), resourceLocation), t, Lifecycle.stable());
    }

    public static <V, T extends V> T registerMapping(Registry<V> registry, int n, ResourceKey<V> resourceKey, T t) {
        return ((WritableRegistry)registry).registerMapping(n, resourceKey, t, Lifecycle.stable());
    }

    public static void bootstrap() {
    }

    static {
        LOADERS.forEach((resourceLocation, supplier) -> {
            if (supplier.get() == null) {
                LOGGER.error("Unable to bootstrap registry '{}'", resourceLocation);
            }
        });
        Registry.checkRegistry(WRITABLE_REGISTRY);
    }
}

