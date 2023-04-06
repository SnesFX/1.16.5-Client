/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.function.Function;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.ResourceKey;

public final class RegistryDataPackCodec<E>
implements Codec<MappedRegistry<E>> {
    private final Codec<MappedRegistry<E>> directCodec;
    private final ResourceKey<? extends Registry<E>> registryKey;
    private final Codec<E> elementCodec;

    public static <E> RegistryDataPackCodec<E> create(ResourceKey<? extends Registry<E>> resourceKey, Lifecycle lifecycle, Codec<E> codec) {
        return new RegistryDataPackCodec<E>(resourceKey, lifecycle, codec);
    }

    private RegistryDataPackCodec(ResourceKey<? extends Registry<E>> resourceKey, Lifecycle lifecycle, Codec<E> codec) {
        this.directCodec = MappedRegistry.directCodec(resourceKey, lifecycle, codec);
        this.registryKey = resourceKey;
        this.elementCodec = codec;
    }

    public <T> DataResult<T> encode(MappedRegistry<E> mappedRegistry, DynamicOps<T> dynamicOps, T t) {
        return this.directCodec.encode(mappedRegistry, dynamicOps, t);
    }

    public <T> DataResult<Pair<MappedRegistry<E>, T>> decode(DynamicOps<T> dynamicOps, T t) {
        DataResult dataResult = this.directCodec.decode(dynamicOps, t);
        if (dynamicOps instanceof RegistryReadOps) {
            return dataResult.flatMap(pair -> ((RegistryReadOps)dynamicOps).decodeElements((MappedRegistry)pair.getFirst(), this.registryKey, this.elementCodec).map(mappedRegistry -> Pair.of((Object)mappedRegistry, (Object)pair.getSecond())));
        }
        return dataResult;
    }

    public String toString() {
        return "RegistryDataPackCodec[" + this.directCodec + " " + this.registryKey + " " + this.elementCodec + "]";
    }

    public /* synthetic */ DataResult encode(Object object, DynamicOps dynamicOps, Object object2) {
        return this.encode((MappedRegistry)object, dynamicOps, object2);
    }
}

