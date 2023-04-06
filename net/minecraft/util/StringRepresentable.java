/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Keyable
 */
package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface StringRepresentable {
    public String getSerializedName();

    public static <E extends Enum<E>> Codec<E> fromEnum(Supplier<E[]> supplier, Function<? super String, ? extends E> function) {
        Enum[] arrenum = (Enum[])supplier.get();
        return StringRepresentable.fromStringResolver(Enum::ordinal, n -> arrenum[n], function);
    }

    public static <E extends StringRepresentable> Codec<E> fromStringResolver(final ToIntFunction<E> toIntFunction, final IntFunction<E> intFunction, final Function<? super String, ? extends E> function) {
        return new Codec<E>(){

            public <T> DataResult<T> encode(E e, DynamicOps<T> dynamicOps, T t) {
                if (dynamicOps.compressMaps()) {
                    return dynamicOps.mergeToPrimitive(t, dynamicOps.createInt(toIntFunction.applyAsInt(e)));
                }
                return dynamicOps.mergeToPrimitive(t, dynamicOps.createString(e.getSerializedName()));
            }

            public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> dynamicOps, T t) {
                if (dynamicOps.compressMaps()) {
                    return dynamicOps.getNumberValue(t).flatMap(number -> Optional.ofNullable(intFunction.apply(number.intValue())).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown element id: " + number)))).map(stringRepresentable -> Pair.of((Object)stringRepresentable, (Object)dynamicOps.empty()));
                }
                return dynamicOps.getStringValue(t).flatMap(string -> Optional.ofNullable(function.apply(string)).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unknown element name: " + string)))).map(stringRepresentable -> Pair.of((Object)stringRepresentable, (Object)dynamicOps.empty()));
            }

            public String toString() {
                return "StringRepresentable[" + toIntFunction + "]";
            }

            public /* synthetic */ DataResult encode(Object object, DynamicOps dynamicOps, Object object2) {
                return this.encode((E)((StringRepresentable)object), (DynamicOps<T>)dynamicOps, (T)object2);
            }
        };
    }

    public static Keyable keys(final StringRepresentable[] arrstringRepresentable) {
        return new Keyable(){

            public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
                if (dynamicOps.compressMaps()) {
                    return IntStream.range(0, arrstringRepresentable.length).mapToObj(dynamicOps::createInt);
                }
                return Arrays.stream(arrstringRepresentable).map(StringRepresentable::getSerializedName).map(dynamicOps::createString);
            }
        };
    }

}

