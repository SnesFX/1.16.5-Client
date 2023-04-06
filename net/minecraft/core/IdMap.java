/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.core;

import javax.annotation.Nullable;

public interface IdMap<T>
extends Iterable<T> {
    public int getId(T var1);

    @Nullable
    public T byId(int var1);
}

