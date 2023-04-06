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
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PaletteResize;

public class HashMapPalette<T>
implements Palette<T> {
    private final IdMapper<T> registry;
    private final CrudeIncrementalIntIdentityHashBiMap<T> values;
    private final PaletteResize<T> resizeHandler;
    private final Function<CompoundTag, T> reader;
    private final Function<T, CompoundTag> writer;
    private final int bits;

    public HashMapPalette(IdMapper<T> idMapper, int n, PaletteResize<T> paletteResize, Function<CompoundTag, T> function, Function<T, CompoundTag> function2) {
        this.registry = idMapper;
        this.bits = n;
        this.resizeHandler = paletteResize;
        this.reader = function;
        this.writer = function2;
        this.values = new CrudeIncrementalIntIdentityHashBiMap(1 << n);
    }

    @Override
    public int idFor(T t) {
        int n = this.values.getId(t);
        if (n == -1 && (n = this.values.add(t)) >= 1 << this.bits) {
            n = this.resizeHandler.onResize(this.bits + 1, t);
        }
        return n;
    }

    @Override
    public boolean maybeHas(Predicate<T> predicate) {
        for (int i = 0; i < this.getSize(); ++i) {
            if (!predicate.test(this.values.byId(i))) continue;
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public T valueFor(int n) {
        return this.values.byId(n);
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) {
        this.values.clear();
        int n = friendlyByteBuf.readVarInt();
        for (int i = 0; i < n; ++i) {
            this.values.add(this.registry.byId(friendlyByteBuf.readVarInt()));
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        int n = this.getSize();
        friendlyByteBuf.writeVarInt(n);
        for (int i = 0; i < n; ++i) {
            friendlyByteBuf.writeVarInt(this.registry.getId(this.values.byId(i)));
        }
    }

    @Override
    public int getSerializedSize() {
        int n = FriendlyByteBuf.getVarIntSize(this.getSize());
        for (int i = 0; i < this.getSize(); ++i) {
            n += FriendlyByteBuf.getVarIntSize(this.registry.getId(this.values.byId(i)));
        }
        return n;
    }

    public int getSize() {
        return this.values.size();
    }

    @Override
    public void read(ListTag listTag) {
        this.values.clear();
        for (int i = 0; i < listTag.size(); ++i) {
            this.values.add(this.reader.apply(listTag.getCompound(i)));
        }
    }

    public void write(ListTag listTag) {
        for (int i = 0; i < this.getSize(); ++i) {
            listTag.add(this.writer.apply(this.values.byId(i)));
        }
    }
}

