/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 */
package net.minecraft.world.level.block.state.properties;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.Property;

public class EnumProperty<T extends Enum<T>>
extends Property<T> {
    private final ImmutableSet<T> values;
    private final Map<String, T> names = Maps.newHashMap();

    protected EnumProperty(String string, Class<T> class_, Collection<T> collection) {
        super(string, class_);
        this.values = ImmutableSet.copyOf(collection);
        for (Enum enum_ : collection) {
            String string2 = ((StringRepresentable)((Object)enum_)).getSerializedName();
            if (this.names.containsKey(string2)) {
                throw new IllegalArgumentException("Multiple values have the same name '" + string2 + "'");
            }
            this.names.put(string2, enum_);
        }
    }

    @Override
    public Collection<T> getPossibleValues() {
        return this.values;
    }

    @Override
    public Optional<T> getValue(String string) {
        return Optional.ofNullable(this.names.get(string));
    }

    @Override
    public String getName(T t) {
        return ((StringRepresentable)t).getSerializedName();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof EnumProperty && super.equals(object)) {
            EnumProperty enumProperty = (EnumProperty)object;
            return this.values.equals(enumProperty.values) && this.names.equals(enumProperty.names);
        }
        return false;
    }

    @Override
    public int generateHashCode() {
        int n = super.generateHashCode();
        n = 31 * n + this.values.hashCode();
        n = 31 * n + this.names.hashCode();
        return n;
    }

    public static <T extends Enum<T>> EnumProperty<T> create(String string, Class<T> class_) {
        return EnumProperty.create(string, class_, Predicates.alwaysTrue());
    }

    public static <T extends Enum<T>> EnumProperty<T> create(String string, Class<T> class_, Predicate<T> predicate) {
        return EnumProperty.create(string, class_, Arrays.stream(class_.getEnumConstants()).filter(predicate).collect(Collectors.toList()));
    }

    public static <T extends Enum<T>> EnumProperty<T> create(String string, Class<T> class_, T ... arrT) {
        return EnumProperty.create(string, class_, Lists.newArrayList((Object[])arrT));
    }

    public static <T extends Enum<T>> EnumProperty<T> create(String string, Class<T> class_, Collection<T> collection) {
        return new EnumProperty<T>(string, class_, collection);
    }
}

