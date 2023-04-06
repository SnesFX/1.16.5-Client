/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.syncher;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;

public interface EntityDataSerializer<T> {
    public void write(FriendlyByteBuf var1, T var2);

    public T read(FriendlyByteBuf var1);

    default public EntityDataAccessor<T> createAccessor(int n) {
        return new EntityDataAccessor<T>(n, this);
    }

    public T copy(T var1);
}

