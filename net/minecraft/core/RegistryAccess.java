/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$PartialResult
 *  com.mojang.serialization.Encoder
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.codecs.UnboundedMapCodec
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class RegistryAccess {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ResourceKey<? extends Registry<?>>, RegistryData<?>> REGISTRIES = (Map)Util.make(() -> {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        RegistryAccess.put(builder, Registry.DIMENSION_TYPE_REGISTRY, DimensionType.DIRECT_CODEC, DimensionType.DIRECT_CODEC);
        RegistryAccess.put(builder, Registry.BIOME_REGISTRY, Biome.DIRECT_CODEC, Biome.NETWORK_CODEC);
        RegistryAccess.put(builder, Registry.CONFIGURED_SURFACE_BUILDER_REGISTRY, ConfiguredSurfaceBuilder.DIRECT_CODEC);
        RegistryAccess.put(builder, Registry.CONFIGURED_CARVER_REGISTRY, ConfiguredWorldCarver.DIRECT_CODEC);
        RegistryAccess.put(builder, Registry.CONFIGURED_FEATURE_REGISTRY, ConfiguredFeature.DIRECT_CODEC);
        RegistryAccess.put(builder, Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, ConfiguredStructureFeature.DIRECT_CODEC);
        RegistryAccess.put(builder, Registry.PROCESSOR_LIST_REGISTRY, StructureProcessorType.DIRECT_CODEC);
        RegistryAccess.put(builder, Registry.TEMPLATE_POOL_REGISTRY, StructureTemplatePool.DIRECT_CODEC);
        RegistryAccess.put(builder, Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, NoiseGeneratorSettings.DIRECT_CODEC);
        return builder.build();
    });
    private static final RegistryHolder BUILTIN = Util.make(() -> {
        RegistryHolder registryHolder = new RegistryHolder();
        DimensionType.registerBuiltin(registryHolder);
        REGISTRIES.keySet().stream().filter(resourceKey -> !resourceKey.equals(Registry.DIMENSION_TYPE_REGISTRY)).forEach(resourceKey -> RegistryAccess.copyBuiltin(registryHolder, resourceKey));
        return registryHolder;
    });

    public abstract <E> Optional<WritableRegistry<E>> registry(ResourceKey<? extends Registry<E>> var1);

    public <E> WritableRegistry<E> registryOrThrow(ResourceKey<? extends Registry<E>> resourceKey) {
        return this.registry(resourceKey).orElseThrow(() -> new IllegalStateException("Missing registry: " + resourceKey));
    }

    public Registry<DimensionType> dimensionTypes() {
        return this.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
    }

    private static <E> void put(ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistryData<?>> builder, ResourceKey<? extends Registry<E>> resourceKey, Codec<E> codec) {
        builder.put(resourceKey, new RegistryData<E>(resourceKey, codec, null));
    }

    private static <E> void put(ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistryData<?>> builder, ResourceKey<? extends Registry<E>> resourceKey, Codec<E> codec, Codec<E> codec2) {
        builder.put(resourceKey, new RegistryData<E>(resourceKey, codec, codec2));
    }

    public static RegistryHolder builtin() {
        RegistryHolder registryHolder = new RegistryHolder();
        RegistryReadOps.ResourceAccess.MemoryMap memoryMap = new RegistryReadOps.ResourceAccess.MemoryMap();
        for (RegistryData<?> registryData : REGISTRIES.values()) {
            RegistryAccess.addBuiltinElements(registryHolder, memoryMap, registryData);
        }
        RegistryReadOps.create(JsonOps.INSTANCE, memoryMap, registryHolder);
        return registryHolder;
    }

    private static <E> void addBuiltinElements(RegistryHolder registryHolder, RegistryReadOps.ResourceAccess.MemoryMap memoryMap, RegistryData<E> registryData) {
        ResourceKey<Registry<E>> resourceKey = registryData.key();
        boolean bl = !resourceKey.equals(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY) && !resourceKey.equals(Registry.DIMENSION_TYPE_REGISTRY);
        WritableRegistry<E> writableRegistry = BUILTIN.registryOrThrow(resourceKey);
        WritableRegistry<E> writableRegistry2 = registryHolder.registryOrThrow(resourceKey);
        for (Map.Entry<ResourceKey<T>, T> entry : writableRegistry.entrySet()) {
            T t = entry.getValue();
            if (bl) {
                memoryMap.add(BUILTIN, entry.getKey(), registryData.codec(), writableRegistry.getId(t), t, writableRegistry.lifecycle(t));
                continue;
            }
            writableRegistry2.registerMapping(writableRegistry.getId(t), entry.getKey(), t, writableRegistry.lifecycle(t));
        }
    }

    private static <R extends Registry<?>> void copyBuiltin(RegistryHolder registryHolder, ResourceKey<R> resourceKey) {
        Registry<Registry<?>> registry = BuiltinRegistries.REGISTRY;
        Registry<?> registry2 = registry.get(resourceKey);
        if (registry2 == null) {
            throw new IllegalStateException("Missing builtin registry: " + resourceKey);
        }
        RegistryAccess.copy(registryHolder, registry2);
    }

    private static <E> void copy(RegistryHolder registryHolder, Registry<E> registry) {
        WritableRegistry<E> writableRegistry = registryHolder.registry(registry.key()).orElseThrow(() -> new IllegalStateException("Missing registry: " + registry.key()));
        for (Map.Entry<ResourceKey<E>, E> entry : registry.entrySet()) {
            E e = entry.getValue();
            writableRegistry.registerMapping(registry.getId(e), entry.getKey(), e, registry.lifecycle(e));
        }
    }

    public static void load(RegistryHolder registryHolder, RegistryReadOps<?> registryReadOps) {
        for (RegistryData<?> registryData : REGISTRIES.values()) {
            RegistryAccess.readRegistry(registryReadOps, registryHolder, registryData);
        }
    }

    private static <E> void readRegistry(RegistryReadOps<?> registryReadOps, RegistryHolder registryHolder, RegistryData<E> registryData) {
        ResourceKey<Registry<E>> resourceKey = registryData.key();
        MappedRegistry mappedRegistry2 = Optional.ofNullable(registryHolder.registries.get(resourceKey)).map(mappedRegistry -> mappedRegistry).orElseThrow(() -> new IllegalStateException("Missing registry: " + resourceKey));
        DataResult<MappedRegistry<E>> dataResult = registryReadOps.decodeElements(mappedRegistry2, registryData.key(), registryData.codec());
        dataResult.error().ifPresent(partialResult -> LOGGER.error("Error loading registry data: {}", (Object)partialResult.message()));
    }

    public static final class RegistryHolder
    extends RegistryAccess {
        public static final Codec<RegistryHolder> NETWORK_CODEC = RegistryHolder.makeNetworkCodec();
        private final Map<? extends ResourceKey<? extends Registry<?>>, ? extends MappedRegistry<?>> registries;

        private static <E> Codec<RegistryHolder> makeNetworkCodec() {
            Codec codec = ResourceLocation.CODEC.xmap(ResourceKey::createRegistryKey, ResourceKey::location);
            Codec codec2 = codec.partialDispatch("type", mappedRegistry -> DataResult.success(mappedRegistry.key()), resourceKey -> RegistryHolder.getNetworkCodec(resourceKey).map(codec -> MappedRegistry.networkCodec(resourceKey, Lifecycle.experimental(), codec)));
            UnboundedMapCodec unboundedMapCodec = Codec.unboundedMap((Codec)codec, (Codec)codec2);
            return RegistryHolder.captureMap(unboundedMapCodec);
        }

        private static <K extends ResourceKey<? extends Registry<?>>, V extends MappedRegistry<?>> Codec<RegistryHolder> captureMap(UnboundedMapCodec<K, V> unboundedMapCodec) {
            return unboundedMapCodec.xmap(RegistryHolder::new, registryHolder -> (ImmutableMap)registryHolder.registries.entrySet().stream().filter(entry -> ((RegistryData)REGISTRIES.get(entry.getKey())).sendToClient()).collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue)));
        }

        private static <E> DataResult<? extends Codec<E>> getNetworkCodec(ResourceKey<? extends Registry<E>> resourceKey) {
            return Optional.ofNullable(REGISTRIES.get(resourceKey)).map(registryData -> registryData.networkCodec()).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown or not serializable registry: " + resourceKey)));
        }

        public RegistryHolder() {
            this(REGISTRIES.keySet().stream().collect(Collectors.toMap(Function.identity(), RegistryHolder::createRegistry)));
        }

        private RegistryHolder(Map<? extends ResourceKey<? extends Registry<?>>, ? extends MappedRegistry<?>> map) {
            this.registries = map;
        }

        private static <E> MappedRegistry<?> createRegistry(ResourceKey<? extends Registry<?>> resourceKey) {
            return new MappedRegistry(resourceKey, Lifecycle.stable());
        }

        @Override
        public <E> Optional<WritableRegistry<E>> registry(ResourceKey<? extends Registry<E>> resourceKey) {
            return Optional.ofNullable(this.registries.get(resourceKey)).map(mappedRegistry -> mappedRegistry);
        }
    }

    static final class RegistryData<E> {
        private final ResourceKey<? extends Registry<E>> key;
        private final Codec<E> codec;
        @Nullable
        private final Codec<E> networkCodec;

        public RegistryData(ResourceKey<? extends Registry<E>> resourceKey, Codec<E> codec, @Nullable Codec<E> codec2) {
            this.key = resourceKey;
            this.codec = codec;
            this.networkCodec = codec2;
        }

        public ResourceKey<? extends Registry<E>> key() {
            return this.key;
        }

        public Codec<E> codec() {
            return this.codec;
        }

        @Nullable
        public Codec<E> networkCodec() {
            return this.networkCodec;
        }

        public boolean sendToClient() {
            return this.networkCodec != null;
        }
    }

}

