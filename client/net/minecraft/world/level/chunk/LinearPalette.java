/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.chunk;

import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.IdMapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PaletteResize;

public class LinearPalette<T>
implements Palette<T> {
    private final IdMapper<T> registry;
    private final T[] values;
    private final PaletteResize<T> resizeHandler;
    private final Function<CompoundTag, T> reader;
    private final int bits;
    private int size;

    public LinearPalette(IdMapper<T> idMapper, int n, PaletteResize<T> paletteResize, Function<CompoundTag, T> function) {
        this.registry = idMapper;
        this.values = new Object[1 << n];
        this.bits = n;
        this.resizeHandler = paletteResize;
        this.reader = function;
    }

    @Override
    public int idFor(T t) {
        int n;
        for (n = 0; n < this.size; ++n) {
            if (this.values[n] != t) continue;
            return n;
        }
        if ((n = this.size++) < this.values.length) {
            this.values[n] = t;
            return n;
        }
        return this.resizeHandler.onResize(this.bits + 1, t);
    }

    @Override
    public boolean maybeHas(Predicate<T> predicate) {
        for (int i = 0; i < this.size; ++i) {
            if (!predicate.test(this.values[i])) continue;
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public T valueFor(int n) {
        if (n >= 0 && n < this.size) {
            return this.values[n];
        }
        return null;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        this.size = friendlyByteBuf.readVarInt();
        for (int i = 0; i < this.size; ++i) {
            this.values[i] = this.registry.byId(friendlyByteBuf.readVarInt());
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(this.size);
        for (int i = 0; i < this.size; ++i) {
            friendlyByteBuf.writeVarInt(this.registry.getId(this.values[i]));
        }
    }

    @Override
    public int getSerializedSize() {
        int n = FriendlyByteBuf.getVarIntSize(this.getSize());
        for (int i = 0; i < this.getSize(); ++i) {
            n += FriendlyByteBuf.getVarIntSize(this.registry.getId(this.values[i]));
        }
        return n;
    }

    public int getSize() {
        return this.size;
    }

    @Override
    public void read(ListTag listTag) {
        for (int i = 0; i < listTag.size(); ++i) {
            this.values[i] = this.reader.apply(listTag.getCompound(i));
        }
        this.size = listTag.size();
    }
}

