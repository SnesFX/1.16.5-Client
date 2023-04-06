/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.resources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.DelegatingOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class RegistryWriteOps<T>
extends DelegatingOps<T> {
    private final RegistryAccess registryHolder;

    public static <T> RegistryWriteOps<T> create(DynamicOps<T> dynamicOps, RegistryAccess registryAccess) {
        return new RegistryWriteOps<T>(dynamicOps, registryAccess);
    }

    private RegistryWriteOps(DynamicOps<T> dynamicOps, RegistryAccess registryAccess) {
        super(dynamicOps);
        this.registryHolder = registryAccess;
    }

    protected <E> DataResult<T> encode(E e, T t, ResourceKey<? extends Registry<E>> resourceKey, Codec<E> codec) {
        WritableRegistry writableRegistry;
        Optional<ResourceKey<E>> optional;
        Optional optional2 = this.registryHolder.registry(resourceKey);
        if (optional2.isPresent() && (optional = (writableRegistry = optional2.get()).getResourceKey(e)).isPresent()) {
            ResourceKey<E> resourceKey2 = optional.get();
            return ResourceLocation.CODEC.encode((Object)resourceKey2.location(), this.delegate, t);
        }
        return codec.encode(e, (DynamicOps)this, t);
    }
}

