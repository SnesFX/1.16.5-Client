/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.chunk;

import java.util.function.Predicate;
import net.minecraft.core.IdMapper;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.chunk.Palette;

public class GlobalPalette<T>
implements Palette<T> {
    private final IdMapper<T> registry;
    private final T defaultValue;

    public GlobalPalette(IdMapper<T> idMapper, T t) {
        this.registry = idMapper;
        this.defaultValue = t;
    }

    @Override
    public int idFor(T t) {
        int n = this.registry.getId(t);
        return n == -1 ? 0 : n;
    }

    @Override
    public boolean maybeHas(Predicate<T> predicate) {
        return true;
    }

    @Override
    public T valueFor(int n) {
        T t = this.registry.byId(n);
        return t == null ? this.defaultValue : t;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public int getSerializedSize() {
        return FriendlyByteBuf.getVarIntSize(0);
    }

    @Override
    public void read(ListTag listTag) {
    }
}

