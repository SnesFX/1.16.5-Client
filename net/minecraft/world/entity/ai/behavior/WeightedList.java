/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Decoder
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.OptionalDynamic
 *  com.mojang.serialization.RecordBuilder
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import com.mojang.serialization.RecordBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class WeightedList<U> {
    protected final List<WeightedEntry<U>> entries;
    private final Random random = new Random();

    public WeightedList() {
        this(Lists.newArrayList());
    }

    private WeightedList(List<WeightedEntry<U>> list) {
        this.entries = Lists.newArrayList(list);
    }

    public static <U> Codec<WeightedList<U>> codec(Codec<U> codec) {
        return WeightedEntry.codec(codec).listOf().xmap(WeightedList::new, weightedList -> weightedList.entries);
    }

    public WeightedList<U> add(U u, int n) {
        this.entries.add(new WeightedEntry(u, n));
        return this;
    }

    public WeightedList<U> shuffle() {
        return this.shuffle(this.random);
    }

    public WeightedList<U> shuffle(Random random) {
        this.entries.forEach(weightedEntry -> weightedEntry.setRandom(random.nextFloat()));
        this.entries.sort(Comparator.comparingDouble(object -> ((WeightedEntry)object).getRandWeight()));
        return this;
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public Stream<U> stream() {
        return this.entries.stream().map(WeightedEntry::getData);
    }

    public U getOne(Random random) {
        return this.shuffle(random).stream().findFirst().orElseThrow(RuntimeException::new);
    }

    public String toString() {
        return "WeightedList[" + this.entries + "]";
    }

    public static class WeightedEntry<T> {
        private final T data;
        private final int weight;
        private double randWeight;

        private WeightedEntry(T t, int n) {
            this.weight = n;
            this.data = t;
        }

        private double getRandWeight() {
            return this.randWeight;
        }

        private void setRandom(float f) {
            this.randWeight = -Math.pow(f, 1.0f / (float)this.weight);
        }

        public T getData() {
            return this.data;
        }

        public String toString() {
            return "" + this.weight + ":" + this.data;
        }

        public static <E> Codec<WeightedEntry<E>> codec(final Codec<E> codec) {
            return new Codec<WeightedEntry<E>>(){

                public <T> DataResult<Pair<WeightedEntry<E>, T>> decode(DynamicOps<T> dynamicOps, T t) {
                    Dynamic dynamic = new Dynamic(dynamicOps, t);
                    return dynamic.get("data").flatMap(((Codec)codec)::parse).map(object -> new WeightedEntry(object, dynamic.get("weight").asInt(1))).map(weightedEntry -> Pair.of((Object)weightedEntry, (Object)dynamicOps.empty()));
                }

                public <T> DataResult<T> encode(WeightedEntry<E> weightedEntry, DynamicOps<T> dynamicOps, T t) {
                    return dynamicOps.mapBuilder().add("weight", dynamicOps.createInt(weightedEntry.weight)).add("data", codec.encodeStart(dynamicOps, weightedEntry.data)).build(t);
                }

                public /* synthetic */ DataResult encode(Object object, DynamicOps dynamicOps, Object object2) {
                    return this.encode((WeightedEntry)object, dynamicOps, object2);
                }
            };
        }

    }

}

