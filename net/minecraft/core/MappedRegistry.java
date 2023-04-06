/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P3
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function3
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  it.unimi.dsi.fastutil.Hash
 *  it.unimi.dsi.fastutil.Hash$Strategy
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.core;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryDataPackCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MappedRegistry<T>
extends WritableRegistry<T> {
    protected static final Logger LOGGER = LogManager.getLogger();
    private final ObjectList<T> byId = new ObjectArrayList(256);
    private final Object2IntMap<T> toId = new Object2IntOpenCustomHashMap(Util.identityStrategy());
    private final BiMap<ResourceLocation, T> storage;
    private final BiMap<ResourceKey<T>, T> keyStorage;
    private final Map<T, Lifecycle> lifecycles;
    private Lifecycle elementsLifecycle;
    protected Object[] randomCache;
    private int nextId;

    public MappedRegistry(ResourceKey<? extends Registry<T>> resourceKey, Lifecycle lifecycle) {
        super(resourceKey, lifecycle);
        this.toId.defaultReturnValue(-1);
        this.storage = HashBiMap.create();
        this.keyStorage = HashBiMap.create();
        this.lifecycles = Maps.newIdentityHashMap();
        this.elementsLifecycle = lifecycle;
    }

    public static <T> MapCodec<RegistryEntry<T>> withNameAndId(ResourceKey<? extends Registry<T>> resourceKey, MapCodec<T> mapCodec) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group((App)ResourceLocation.CODEC.xmap(ResourceKey.elementKey(resourceKey), ResourceKey::location).fieldOf("name").forGetter(registryEntry -> registryEntry.key), (App)Codec.INT.fieldOf("id").forGetter(registryEntry -> registryEntry.id), (App)mapCodec.forGetter(registryEntry -> registryEntry.value)).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> RegistryEntry.new(arg_0, arg_1, arg_2)));
    }

    @Override
    public <V extends T> V registerMapping(int n, ResourceKey<T> resourceKey, V v, Lifecycle lifecycle) {
        return this.registerMapping(n, resourceKey, v, lifecycle, true);
    }

    private <V extends T> V registerMapping(int n, ResourceKey<T> resourceKey, V v, Lifecycle lifecycle, boolean bl) {
        Validate.notNull(resourceKey);
        Validate.notNull(v);
        this.byId.size(Math.max(this.byId.size(), n + 1));
        this.byId.set(n, v);
        this.toId.put(v, n);
        this.randomCache = null;
        if (bl && this.keyStorage.containsKey(resourceKey)) {
            LOGGER.debug("Adding duplicate key '{}' to registry", resourceKey);
        }
        if (this.storage.containsValue(v)) {
            LOGGER.error("Adding duplicate value '{}' to registry", v);
        }
        this.storage.put((Object)resourceKey.location(), v);
        this.keyStorage.put(resourceKey, v);
        this.lifecycles.put(v, lifecycle);
        this.elementsLifecycle = this.elementsLifecycle.add(lifecycle);
        if (this.nextId <= n) {
            this.nextId = n + 1;
        }
        return v;
    }

    @Override
    public <V extends T> V register(ResourceKey<T> resourceKey, V v, Lifecycle lifecycle) {
        return this.registerMapping(this.nextId, resourceKey, v, lifecycle);
    }

    @Override
    public <V extends T> V registerOrOverride(OptionalInt optionalInt, ResourceKey<T> resourceKey, V v, Lifecycle lifecycle) {
        int n;
        Validate.notNull(resourceKey);
        Validate.notNull(v);
        Object object = this.keyStorage.get(resourceKey);
        if (object == null) {
            n = optionalInt.isPresent() ? optionalInt.getAsInt() : this.nextId;
        } else {
            n = this.toId.getInt(object);
            if (optionalInt.isPresent() && optionalInt.getAsInt() != n) {
                throw new IllegalStateException("ID mismatch");
            }
            this.toId.removeInt(object);
            this.lifecycles.remove(object);
        }
        return this.registerMapping(n, resourceKey, v, lifecycle, false);
    }

    @Nullable
    @Override
    public ResourceLocation getKey(T t) {
        return (ResourceLocation)this.storage.inverse().get(t);
    }

    @Override
    public Optional<ResourceKey<T>> getResourceKey(T t) {
        return Optional.ofNullable(this.keyStorage.inverse().get(t));
    }

    @Override
    public int getId(@Nullable T t) {
        return this.toId.getInt(t);
    }

    @Nullable
    @Override
    public T get(@Nullable ResourceKey<T> resourceKey) {
        return (T)this.keyStorage.get(resourceKey);
    }

    @Nullable
    @Override
    public T byId(int n) {
        if (n < 0 || n >= this.byId.size()) {
            return null;
        }
        return (T)this.byId.get(n);
    }

    @Override
    public Lifecycle lifecycle(T t) {
        return this.lifecycles.get(t);
    }

    @Override
    public Lifecycle elementsLifecycle() {
        return this.elementsLifecycle;
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.filter((Iterator)this.byId.iterator(), Objects::nonNull);
    }

    @Nullable
    @Override
    public T get(@Nullable ResourceLocation resourceLocation) {
        return (T)this.storage.get((Object)resourceLocation);
    }

    @Override
    public Set<ResourceLocation> keySet() {
        return Collections.unmodifiableSet(this.storage.keySet());
    }

    @Override
    public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
        return Collections.unmodifiableMap(this.keyStorage).entrySet();
    }

    @Nullable
    public T getRandom(Random random) {
        if (this.randomCache == null) {
            Set set = this.storage.values();
            if (set.isEmpty()) {
                return null;
            }
            this.randomCache = set.toArray(new Object[set.size()]);
        }
        return (T)Util.getRandom(this.randomCache, random);
    }

    @Override
    public boolean containsKey(ResourceLocation resourceLocation) {
        return this.storage.containsKey((Object)resourceLocation);
    }

    public static <T> Codec<MappedRegistry<T>> networkCodec(ResourceKey<? extends Registry<T>> resourceKey, Lifecycle lifecycle, Codec<T> codec) {
        return MappedRegistry.withNameAndId(resourceKey, codec.fieldOf("element")).codec().listOf().xmap(list -> {
            MappedRegistry mappedRegistry = new MappedRegistry(resourceKey, lifecycle);
            for (RegistryEntry registryEntry : list) {
                mappedRegistry.registerMapping(registryEntry.id, registryEntry.key, registryEntry.value, lifecycle);
            }
            return mappedRegistry;
        }, mappedRegistry -> {
            ImmutableList.Builder builder = ImmutableList.builder();
            for (T t : mappedRegistry) {
                builder.add(new RegistryEntry<T>(mappedRegistry.getResourceKey(t).get(), mappedRegistry.getId(t), t));
            }
            return builder.build();
        });
    }

    public static <T> Codec<MappedRegistry<T>> dataPackCodec(ResourceKey<? extends Registry<T>> resourceKey, Lifecycle lifecycle, Codec<T> codec) {
        return RegistryDataPackCodec.create(resourceKey, lifecycle, codec);
    }

    public static <T> Codec<MappedRegistry<T>> directCodec(ResourceKey<? extends Registry<T>> resourceKey, Lifecycle lifecycle, Codec<T> codec) {
        return Codec.unboundedMap((Codec)ResourceLocation.CODEC.xmap(ResourceKey.elementKey(resourceKey), ResourceKey::location), codec).xmap(map -> {
            MappedRegistry<T> mappedRegistry = new MappedRegistry<T>(resourceKey, lifecycle);
            map.forEach((resourceKey, object) -> mappedRegistry.register((ResourceKey<T>)resourceKey, (V)object, lifecycle));
            return mappedRegistry;
        }, mappedRegistry -> ImmutableMap.copyOf(mappedRegistry.keyStorage));
    }

    public static class RegistryEntry<T> {
        public final ResourceKey<T> key;
        public final int id;
        public final T value;

        public RegistryEntry(ResourceKey<T> resourceKey, int n, T t) {
            this.key = resourceKey;
            this.id = n;
            this.value = t;
        }
    }

}

