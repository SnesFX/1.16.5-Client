/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 */
package net.minecraft.data.models.blockstates;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.data.models.blockstates.Selector;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class PropertyDispatch {
    private final Map<Selector, List<Variant>> values = Maps.newHashMap();

    protected void putValue(Selector selector, List<Variant> list) {
        List<Variant> list2 = this.values.put(selector, list);
        if (list2 != null) {
            throw new IllegalStateException("Value " + selector + " is already defined");
        }
    }

    Map<Selector, List<Variant>> getEntries() {
        this.verifyComplete();
        return ImmutableMap.copyOf(this.values);
    }

    private void verifyComplete() {
        List<Property<?>> list = this.getDefinedProperties();
        Stream<Selector> stream = Stream.of(Selector.empty());
        for (Property<?> property : list) {
            stream = stream.flatMap(selector -> property.getAllValues().map(selector::extend));
        }
        List list2 = stream.filter(selector -> !this.values.containsKey(selector)).collect(Collectors.toList());
        if (!list2.isEmpty()) {
            throw new IllegalStateException("Missing definition for properties: " + list2);
        }
    }

    abstract List<Property<?>> getDefinedProperties();

    public static <T1 extends Comparable<T1>> C1<T1> property(Property<T1> property) {
        return new C1<T1>(property);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>> C2<T1, T2> properties(Property<T1> property, Property<T2> property2) {
        return new C2<T1, T2>(property, property2);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>> C3<T1, T2, T3> properties(Property<T1> property, Property<T2> property2, Property<T3> property3) {
        return new C3<T1, T2, T3>(property, property2, property3);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>> C4<T1, T2, T3, T4> properties(Property<T1> property, Property<T2> property2, Property<T3> property3, Property<T4> property4) {
        return new C4<T1, T2, T3, T4>(property, property2, property3, property4);
    }

    public static <T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>> C5<T1, T2, T3, T4, T5> properties(Property<T1> property, Property<T2> property2, Property<T3> property3, Property<T4> property4, Property<T5> property5) {
        return new C5<T1, T2, T3, T4, T5>(property, property2, property3, property4, property5);
    }

    @FunctionalInterface
    public static interface TriFunction<P1, P2, P3, R> {
        public R apply(P1 var1, P2 var2, P3 var3);
    }

    public static class C5<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>, T5 extends Comparable<T5>>
    extends PropertyDispatch {
        private final Property<T1> property1;
        private final Property<T2> property2;
        private final Property<T3> property3;
        private final Property<T4> property4;
        private final Property<T5> property5;

        private C5(Property<T1> property, Property<T2> property2, Property<T3> property3, Property<T4> property4, Property<T5> property5) {
            this.property1 = property;
            this.property2 = property2;
            this.property3 = property3;
            this.property4 = property4;
            this.property5 = property5;
        }

        @Override
        public List<Property<?>> getDefinedProperties() {
            return ImmutableList.of(this.property1, this.property2, this.property3, this.property4, this.property5);
        }

        public C5<T1, T2, T3, T4, T5> select(T1 T1, T2 T2, T3 T3, T4 T4, T5 T5, List<Variant> list) {
            Selector selector = Selector.of(this.property1.value(T1), this.property2.value(T2), this.property3.value(T3), this.property4.value(T4), this.property5.value(T5));
            this.putValue(selector, list);
            return this;
        }

        public C5<T1, T2, T3, T4, T5> select(T1 T1, T2 T2, T3 T3, T4 T4, T5 T5, Variant variant) {
            return this.select(T1, T2, T3, T4, T5, Collections.singletonList(variant));
        }
    }

    public static class C4<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>, T4 extends Comparable<T4>>
    extends PropertyDispatch {
        private final Property<T1> property1;
        private final Property<T2> property2;
        private final Property<T3> property3;
        private final Property<T4> property4;

        private C4(Property<T1> property, Property<T2> property2, Property<T3> property3, Property<T4> property4) {
            this.property1 = property;
            this.property2 = property2;
            this.property3 = property3;
            this.property4 = property4;
        }

        @Override
        public List<Property<?>> getDefinedProperties() {
            return ImmutableList.of(this.property1, this.property2, this.property3, this.property4);
        }

        public C4<T1, T2, T3, T4> select(T1 T1, T2 T2, T3 T3, T4 T4, List<Variant> list) {
            Selector selector = Selector.of(this.property1.value(T1), this.property2.value(T2), this.property3.value(T3), this.property4.value(T4));
            this.putValue(selector, list);
            return this;
        }

        public C4<T1, T2, T3, T4> select(T1 T1, T2 T2, T3 T3, T4 T4, Variant variant) {
            return this.select(T1, T2, T3, T4, Collections.singletonList(variant));
        }
    }

    public static class C3<T1 extends Comparable<T1>, T2 extends Comparable<T2>, T3 extends Comparable<T3>>
    extends PropertyDispatch {
        private final Property<T1> property1;
        private final Property<T2> property2;
        private final Property<T3> property3;

        private C3(Property<T1> property, Property<T2> property2, Property<T3> property3) {
            this.property1 = property;
            this.property2 = property2;
            this.property3 = property3;
        }

        @Override
        public List<Property<?>> getDefinedProperties() {
            return ImmutableList.of(this.property1, this.property2, this.property3);
        }

        public C3<T1, T2, T3> select(T1 T1, T2 T2, T3 T3, List<Variant> list) {
            Selector selector = Selector.of(this.property1.value(T1), this.property2.value(T2), this.property3.value(T3));
            this.putValue(selector, list);
            return this;
        }

        public C3<T1, T2, T3> select(T1 T1, T2 T2, T3 T3, Variant variant) {
            return this.select(T1, T2, T3, Collections.singletonList(variant));
        }

        public PropertyDispatch generate(TriFunction<T1, T2, T3, Variant> triFunction) {
            this.property1.getPossibleValues().forEach(comparable -> this.property2.getPossibleValues().forEach(comparable2 -> this.property3.getPossibleValues().forEach(comparable3 -> this.select(comparable, comparable2, comparable3, (Variant)triFunction.apply(comparable, comparable2, comparable3)))));
            return this;
        }
    }

    public static class C2<T1 extends Comparable<T1>, T2 extends Comparable<T2>>
    extends PropertyDispatch {
        private final Property<T1> property1;
        private final Property<T2> property2;

        private C2(Property<T1> property, Property<T2> property2) {
            this.property1 = property;
            this.property2 = property2;
        }

        @Override
        public List<Property<?>> getDefinedProperties() {
            return ImmutableList.of(this.property1, this.property2);
        }

        public C2<T1, T2> select(T1 T1, T2 T2, List<Variant> list) {
            Selector selector = Selector.of(this.property1.value(T1), this.property2.value(T2));
            this.putValue(selector, list);
            return this;
        }

        public C2<T1, T2> select(T1 T1, T2 T2, Variant variant) {
            return this.select(T1, T2, Collections.singletonList(variant));
        }

        public PropertyDispatch generate(BiFunction<T1, T2, Variant> biFunction) {
            this.property1.getPossibleValues().forEach(comparable -> this.property2.getPossibleValues().forEach(comparable2 -> this.select(comparable, comparable2, (Variant)biFunction.apply(comparable, comparable2))));
            return this;
        }

        public PropertyDispatch generateList(BiFunction<T1, T2, List<Variant>> biFunction) {
            this.property1.getPossibleValues().forEach(comparable -> this.property2.getPossibleValues().forEach(comparable2 -> this.select(comparable, comparable2, (List)biFunction.apply(comparable, comparable2))));
            return this;
        }
    }

    public static class C1<T1 extends Comparable<T1>>
    extends PropertyDispatch {
        private final Property<T1> property1;

        private C1(Property<T1> property) {
            this.property1 = property;
        }

        @Override
        public List<Property<?>> getDefinedProperties() {
            return ImmutableList.of(this.property1);
        }

        public C1<T1> select(T1 T1, List<Variant> list) {
            Selector selector = Selector.of(this.property1.value(T1));
            this.putValue(selector, list);
            return this;
        }

        public C1<T1> select(T1 T1, Variant variant) {
            return this.select(T1, Collections.singletonList(variant));
        }

        public PropertyDispatch generate(Function<T1, Variant> function) {
            this.property1.getPossibleValues().forEach(comparable -> this.select(comparable, (Variant)function.apply(comparable)));
            return this;
        }
    }

}

