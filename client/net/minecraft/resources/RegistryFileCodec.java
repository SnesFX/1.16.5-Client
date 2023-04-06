/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.resources;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.resources.ResourceKey;

public final class RegistryFileCodec<E>
implements Codec<Supplier<E>> {
    private final ResourceKey<? extends Registry<E>> registryKey;
    private final Codec<E> elementCodec;
    private final boolean allowInline;

    public static <E> RegistryFileCodec<E> create(ResourceKey<? extends Registry<E>> resourceKey, Codec<E> codec) {
        return RegistryFileCodec.create(resourceKey, codec, true);
    }

    public static <E> Codec<List<Supplier<E>>> homogeneousList(ResourceKey<? extends Registry<E>> resourceKey, Codec<E> codec) {
        return Codec.either((Codec)RegistryFileCodec.create(resourceKey, codec, false).listOf(), (Codec)codec.xmap(object -> () -> object, Supplier::get).listOf()).xmap(either -> (List)either.map(list -> list, list -> list), Either::left);
    }

    private static <E> RegistryFileCodec<E> create(ResourceKey<? extends Registry<E>> resourceKey, Codec<E> codec, boolean bl) {
        return new RegistryFileCodec<E>(resourceKey, codec, bl);
    }

    private RegistryFileCodec(ResourceKey<? extends Registry<E>> resourceKey, Codec<E> codec, boolean bl) {
        this.registryKey = resourceKey;
        this.elementCodec = codec;
        this.allowInline = bl;
    }

    public <T> DataResult<T> encode(Supplier<E> supplier, DynamicOps<T> dynamicOps, T t) {
        if (dynamicOps instanceof RegistryWriteOps) {
            return ((RegistryWriteOps)dynamicOps).encode(supplier.get(), t, this.registryKey, this.elementCodec);
        }
        return this.elementCodec.encode(supplier.get(), dynamicOps, t);
    }

    public <T> DataResult<Pair<Supplier<E>, T>> decode(DynamicOps<T> dynamicOps, T t) {
        if (dynamicOps instanceof RegistryReadOps) {
            return ((RegistryReadOps)dynamicOps).decodeElement(t, this.registryKey, this.elementCodec, this.allowInline);
        }
        return this.elementCodec.decode(dynamicOps, t).map(pair -> pair.mapFirst(object -> () -> object));
    }

    public String toString() {
        return "RegistryFileCodec[" + this.registryKey + " " + this.elementCodec + "]";
    }

    public /* synthetic */ DataResult encode(Object object, DynamicOps dynamicOps, Object object2) {
        return this.encode((Supplier)object, dynamicOps, object2);
    }
}

