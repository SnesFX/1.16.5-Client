/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.ListBuilder
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 */
package net.minecraft.resources;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public abstract class DelegatingOps<T>
implements DynamicOps<T> {
    protected final DynamicOps<T> delegate;

    protected DelegatingOps(DynamicOps<T> dynamicOps) {
        this.delegate = dynamicOps;
    }

    public T empty() {
        return (T)this.delegate.empty();
    }

    public <U> U convertTo(DynamicOps<U> dynamicOps, T t) {
        return (U)this.delegate.convertTo(dynamicOps, t);
    }

    public DataResult<Number> getNumberValue(T t) {
        return this.delegate.getNumberValue(t);
    }

    public T createNumeric(Number number) {
        return (T)this.delegate.createNumeric(number);
    }

    public T createByte(byte by) {
        return (T)this.delegate.createByte(by);
    }

    public T createShort(short s) {
        return (T)this.delegate.createShort(s);
    }

    public T createInt(int n) {
        return (T)this.delegate.createInt(n);
    }

    public T createLong(long l) {
        return (T)this.delegate.createLong(l);
    }

    public T createFloat(float f) {
        return (T)this.delegate.createFloat(f);
    }

    public T createDouble(double d) {
        return (T)this.delegate.createDouble(d);
    }

    public DataResult<Boolean> getBooleanValue(T t) {
        return this.delegate.getBooleanValue(t);
    }

    public T createBoolean(boolean bl) {
        return (T)this.delegate.createBoolean(bl);
    }

    public DataResult<String> getStringValue(T t) {
        return this.delegate.getStringValue(t);
    }

    public T createString(String string) {
        return (T)this.delegate.createString(string);
    }

    public DataResult<T> mergeToList(T t, T t2) {
        return this.delegate.mergeToList(t, t2);
    }

    public DataResult<T> mergeToList(T t, List<T> list) {
        return this.delegate.mergeToList(t, list);
    }

    public DataResult<T> mergeToMap(T t, T t2, T t3) {
        return this.delegate.mergeToMap(t, t2, t3);
    }

    public DataResult<T> mergeToMap(T t, MapLike<T> mapLike) {
        return this.delegate.mergeToMap(t, mapLike);
    }

    public DataResult<Stream<Pair<T, T>>> getMapValues(T t) {
        return this.delegate.getMapValues(t);
    }

    public DataResult<Consumer<BiConsumer<T, T>>> getMapEntries(T t) {
        return this.delegate.getMapEntries(t);
    }

    public T createMap(Stream<Pair<T, T>> stream) {
        return (T)this.delegate.createMap(stream);
    }

    public DataResult<MapLike<T>> getMap(T t) {
        return this.delegate.getMap(t);
    }

    public DataResult<Stream<T>> getStream(T t) {
        return this.delegate.getStream(t);
    }

    public DataResult<Consumer<Consumer<T>>> getList(T t) {
        return this.delegate.getList(t);
    }

    public T createList(Stream<T> stream) {
        return (T)this.delegate.createList(stream);
    }

    public DataResult<ByteBuffer> getByteBuffer(T t) {
        return this.delegate.getByteBuffer(t);
    }

    public T createByteList(ByteBuffer byteBuffer) {
        return (T)this.delegate.createByteList(byteBuffer);
    }

    public DataResult<IntStream> getIntStream(T t) {
        return this.delegate.getIntStream(t);
    }

    public T createIntList(IntStream intStream) {
        return (T)this.delegate.createIntList(intStream);
    }

    public DataResult<LongStream> getLongStream(T t) {
        return this.delegate.getLongStream(t);
    }

    public T createLongList(LongStream longStream) {
        return (T)this.delegate.createLongList(longStream);
    }

    public T remove(T t, String string) {
        return (T)this.delegate.remove(t, string);
    }

    public boolean compressMaps() {
        return this.delegate.compressMaps();
    }

    public ListBuilder<T> listBuilder() {
        return this.delegate.listBuilder();
    }

    public RecordBuilder<T> mapBuilder() {
        return this.delegate.mapBuilder();
    }
}

