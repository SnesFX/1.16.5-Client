/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class DefaultedRegistry<T>
extends MappedRegistry<T> {
    private final ResourceLocation defaultKey;
    private T defaultValue;

    public DefaultedRegistry(String string, ResourceKey<? extends Registry<T>> resourceKey, Lifecycle lifecycle) {
        super(resourceKey, lifecycle);
        this.defaultKey = new ResourceLocation(string);
    }

    @Override
    public <V extends T> V registerMapping(int n, ResourceKey<T> resourceKey, V v, Lifecycle lifecycle) {
        if (this.defaultKey.equals(resourceKey.location())) {
            this.defaultValue = v;
        }
        return super.registerMapping(n, resourceKey, v, lifecycle);
    }

    @Override
    public int getId(@Nullable T t) {
        int n = super.getId(t);
        return n == -1 ? super.getId(this.defaultValue) : n;
    }

    @Nonnull
    @Override
    public ResourceLocation getKey(T t) {
        ResourceLocation resourceLocation = super.getKey(t);
        return resourceLocation == null ? this.defaultKey : resourceLocation;
    }

    @Nonnull
    @Override
    public T get(@Nullable ResourceLocation resourceLocation) {
        Object t = super.get(resourceLocation);
        return t == null ? this.defaultValue : t;
    }

    @Override
    public Optional<T> getOptional(@Nullable ResourceLocation resourceLocation) {
        return Optional.ofNullable(super.get(resourceLocation));
    }

    @Nonnull
    @Override
    public T byId(int n) {
        Object t = super.byId(n);
        return t == null ? this.defaultValue : t;
    }

    @Nonnull
    @Override
    public T getRandom(Random random) {
        Object t = super.getRandom(random);
        return t == null ? this.defaultValue : t;
    }

    public ResourceLocation getDefaultKey() {
        return this.defaultKey;
    }
}

