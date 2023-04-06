/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 */
package net.minecraft.resources;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.ResourceKey;

public final class RegistryLookupCodec<E>
extends MapCodec<Registry<E>> {
    private final ResourceKey<? extends Registry<E>> registryKey;

    public static <E> RegistryLookupCodec<E> create(ResourceKey<? extends Registry<E>> resourceKey) {
        return new RegistryLookupCodec<E>(resourceKey);
    }

    private RegistryLookupCodec(ResourceKey<? extends Registry<E>> resourceKey) {
        this.registryKey = resourceKey;
    }

    public <T> RecordBuilder<T> encode(Registry<E> registry, DynamicOps<T> dynamicOps, RecordBuilder<T> recordBuilder) {
        return recordBuilder;
    }

    public <T> DataResult<Registry<E>> decode(DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
        if (dynamicOps instanceof RegistryReadOps) {
            return ((RegistryReadOps)dynamicOps).registry(this.registryKey);
        }
        return DataResult.error((String)"Not a registry ops");
    }

    public String toString() {
        return "RegistryLookupCodec[" + this.registryKey + "]";
    }

    public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
        return Stream.empty();
    }

    public /* synthetic */ RecordBuilder encode(Object object, DynamicOps dynamicOps, RecordBuilder recordBuilder) {
        return this.encode((Registry)object, dynamicOps, recordBuilder);
    }
}

