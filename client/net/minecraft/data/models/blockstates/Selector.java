/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package net.minecraft.data.models.blockstates;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.world.level.block.state.properties.Property;

public final class Selector {
    private static final Selector EMPTY = new Selector((List<Property.Value<?>>)ImmutableList.of());
    private static final Comparator<Property.Value<?>> COMPARE_BY_NAME = Comparator.comparing(value -> value.getProperty().getName());
    private final List<Property.Value<?>> values;

    public Selector extend(Property.Value<?> value) {
        return new Selector((List<Property.Value<?>>)ImmutableList.builder().addAll(this.values).add(value).build());
    }

    public Selector extend(Selector selector) {
        return new Selector((List<Property.Value<?>>)ImmutableList.builder().addAll(this.values).addAll(selector.values).build());
    }

    private Selector(List<Property.Value<?>> list) {
        this.values = list;
    }

    public static Selector empty() {
        return EMPTY;
    }

    public static Selector of(Property.Value<?> ... arrvalue) {
        return new Selector((List<Property.Value<?>>)ImmutableList.copyOf((Object[])arrvalue));
    }

    public boolean equals(Object object) {
        return this == object || object instanceof Selector && this.values.equals(((Selector)object).values);
    }

    public int hashCode() {
        return this.values.hashCode();
    }

    public String getKey() {
        return this.values.stream().sorted(COMPARE_BY_NAME).map(Property.Value::toString).collect(Collectors.joining(","));
    }

    public String toString() {
        return this.getKey();
    }
}

