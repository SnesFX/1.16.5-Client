/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.OptionalInt;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public abstract class WritableRegistry<T>
extends Registry<T> {
    public WritableRegistry(ResourceKey<? extends Registry<T>> resourceKey, Lifecycle lifecycle) {
        super(resourceKey, lifecycle);
    }

    public abstract <V extends T> V registerMapping(int var1, ResourceKey<T> var2, V var3, Lifecycle var4);

    public abstract <V extends T> V register(ResourceKey<T> var1, V var2, Lifecycle var3);

    public abstract <V extends T> V registerOrOverride(OptionalInt var1, ResourceKey<T> var2, V var3, Lifecycle var4);
}

