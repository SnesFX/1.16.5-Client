/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.Int2IntMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 */
package net.minecraft.world.level.chunk;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.IdMapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.BitStorage;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.HashMapPalette;
import net.minecraft.world.level.chunk.LinearPalette;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PaletteResize;

public class PalettedContainer<T>
implements PaletteResize<T> {
    private final Palette<T> globalPalette;
    private final PaletteResize<T> dummyPaletteResize = (n, object) -> 0;
    private final IdMapper<T> registry;
    private final Function<CompoundTag, T> reader;
    private final Function<T, CompoundTag> writer;
    private final T defaultValue;
    protected BitStorage storage;
    private Palette<T> palette;
    private int bits;
    private final ReentrantLock lock = new ReentrantLock();

    public void acquire() {
        if (this.lock.isLocked() && !this.lock.isHeldByCurrentThread()) {
            String string = Thread.getAllStackTraces().keySet().stream().filter(Objects::nonNull).map(thread -> thread.getName() + ": \n\tat " + Arrays.stream(thread.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat "))).collect(Collectors.joining("\n"));
            CrashReport crashReport = new CrashReport("Writing into PalettedContainer from multiple threads", new IllegalStateException());
            CrashReportCategory crashReportCategory = crashReport.addCategory("Thread dumps");
            crashReportCategory.setDetail("Thread dumps", string);
            throw new ReportedException(crashReport);
        }
        this.lock.lock();
    }

    public void release() {
        this.lock.unlock();
    }

    public PalettedContainer(Palette<T> palette, IdMapper<T> idMapper, Function<CompoundTag, T> function, Function<T, CompoundTag> function2, T t) {
        this.globalPalette = palette;
        this.registry = idMapper;
        this.reader = function;
        this.writer = function2;
        this.defaultValue = t;
        this.setBits(4);
    }

    private static int getIndex(int n, int n2, int n3) {
        return n2 << 8 | n3 << 4 | n;
    }

    private void setBits(int n) {
        if (n == this.bits) {
            return;
        }
        this.bits = n;
        if (this.bits <= 4) {
            this.bits = 4;
            this.palette = new LinearPalette<T>(this.registry, this.bits, this, this.reader);
        } else if (this.bits < 9) {
            this.palette = new HashMapPalette<T>(this.registry, this.bits, this, this.reader, this.writer);
        } else {
            this.palette = this.globalPalette;
            this.bits = Mth.ceillog2(this.registry.size());
        }
        this.palette.idFor(this.defaultValue);
        this.storage = new BitStorage(this.bits, 4096);
    }

    @Override
    public int onResize(int n, T t) {
        int n2;
        this.acquire();
        BitStorage bitStorage = this.storage;
        Palette<T> palette = this.palette;
        this.setBits(n);
        for (n2 = 0; n2 < bitStorage.getSize(); ++n2) {
            T t2 = palette.valueFor(bitStorage.get(n2));
            if (t2 == null) continue;
            this.set(n2, t2);
        }
        n2 = this.palette.idFor(t);
        this.release();
        return n2;
    }

    public T getAndSet(int n, int n2, int n3, T t) {
        this.acquire();
        T t2 = this.getAndSet(PalettedContainer.getIndex(n, n2, n3), t);
        this.release();
        return t2;
    }

    public T getAndSetUnchecked(int n, int n2, int n3, T t) {
        return this.getAndSet(PalettedContainer.getIndex(n, n2, n3), t);
    }

    protected T getAndSet(int n, T t) {
        int n2 = this.palette.idFor(t);
        int n3 = this.storage.getAndSet(n, n2);
        T t2 = this.palette.valueFor(n3);
        return t2 == null ? this.defaultValue : t2;
    }

    protected void set(int n, T t) {
        int n2 = this.palette.idFor(t);
        this.storage.set(n, n2);
    }

    public T get(int n, int n2, int n3) {
        return this.get(PalettedContainer.getIndex(n, n2, n3));
    }

    protected T get(int n) {
        T t = this.palette.valueFor(this.storage.get(n));
        return t == null ? this.defaultValue : t;
    }

    public void read(FriendlyByteBuf friendlyByteBuf) {
        this.acquire();
        byte by = friendlyByteBuf.readByte();
        if (this.bits != by) {
            this.setBits(by);
        }
        this.palette.read(friendlyByteBuf);
        friendlyByteBuf.readLongArray(this.storage.getRaw());
        this.release();
    }

    public void write(FriendlyByteBuf friendlyByteBuf) {
        this.acquire();
        friendlyByteBuf.writeByte(this.bits);
        this.palette.write(friendlyByteBuf);
        friendlyByteBuf.writeLongArray(this.storage.getRaw());
        this.release();
    }

    public void read(ListTag listTag, long[] arrl) {
        this.acquire();
        int n = Math.max(4, Mth.ceillog2(listTag.size()));
        if (n != this.bits) {
            this.setBits(n);
        }
        this.palette.read(listTag);
        int n2 = arrl.length * 64 / 4096;
        if (this.palette == this.globalPalette) {
            HashMapPalette<T> hashMapPalette = new HashMapPalette<T>(this.registry, n, this.dummyPaletteResize, this.reader, this.writer);
            hashMapPalette.read(listTag);
            BitStorage bitStorage = new BitStorage(n, 4096, arrl);
            for (int i = 0; i < 4096; ++i) {
                this.storage.set(i, this.globalPalette.idFor(hashMapPalette.valueFor(bitStorage.get(i))));
            }
        } else if (n2 == this.bits) {
            System.arraycopy(arrl, 0, this.storage.getRaw(), 0, arrl.length);
        } else {
            BitStorage bitStorage = new BitStorage(n2, 4096, arrl);
            for (int i = 0; i < 4096; ++i) {
                this.storage.set(i, bitStorage.get(i));
            }
        }
        this.release();
    }

    public void write(CompoundTag compoundTag, String string, String string2) {
        this.acquire();
        HashMapPalette<T> hashMapPalette = new HashMapPalette<T>(this.registry, this.bits, this.dummyPaletteResize, this.reader, this.writer);
        T t = this.defaultValue;
        int n = hashMapPalette.idFor(this.defaultValue);
        int[] arrn = new int[4096];
        for (int i = 0; i < 4096; ++i) {
            T t2 = this.get(i);
            if (t2 != t) {
                t = t2;
                n = hashMapPalette.idFor(t2);
            }
            arrn[i] = n;
        }
        ListTag listTag = new ListTag();
        hashMapPalette.write(listTag);
        compoundTag.put(string, listTag);
        int n2 = Math.max(4, Mth.ceillog2(listTag.size()));
        BitStorage bitStorage = new BitStorage(n2, 4096);
        for (int i = 0; i < arrn.length; ++i) {
            bitStorage.set(i, arrn[i]);
        }
        compoundTag.putLongArray(string2, bitStorage.getRaw());
        this.release();
    }

    public int getSerializedSize() {
        return 1 + this.palette.getSerializedSize() + FriendlyByteBuf.getVarIntSize(this.storage.getSize()) + this.storage.getRaw().length * 8;
    }

    public boolean maybeHas(Predicate<T> predicate) {
        return this.palette.maybeHas(predicate);
    }

    public void count(CountConsumer<T> countConsumer) {
        Int2IntOpenHashMap int2IntOpenHashMap = new Int2IntOpenHashMap();
        this.storage.getAll(arg_0 -> PalettedContainer.lambda$count$2((Int2IntMap)int2IntOpenHashMap, arg_0));
        int2IntOpenHashMap.int2IntEntrySet().forEach(entry -> countConsumer.accept(this.palette.valueFor(entry.getIntKey()), entry.getIntValue()));
    }

    private static /* synthetic */ void lambda$count$2(Int2IntMap int2IntMap, int n) {
        int2IntMap.put(n, int2IntMap.get(n) + 1);
    }

    @FunctionalInterface
    public static interface CountConsumer<T> {
        public void accept(T var1, int var2);
    }

}

