/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.chunk;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;

public interface Palette<T> {
    public int idFor(T var1);

    public boolean maybeHas(Predicate<T> var1);

    @Nullable
    public T valueFor(int var1);

    public void read(FriendlyByteBuf var1);

    public void write(FriendlyByteBuf var1);

    public int getSerializedSize();

    public void read(ListTag var1);
}

