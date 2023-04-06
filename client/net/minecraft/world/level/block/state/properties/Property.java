/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.MoreObjects$ToStringHelper
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.PrimitiveCodec
 */
package net.minecraft.world.level.block.state.properties;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.PrimitiveCodec;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.level.block.state.StateHolder;

public abstract class Property<T extends Comparable<T>> {
    private final Class<T> clazz;
    private final String name;
    private Integer hashCode;
    private final Codec<T> codec = Codec.STRING.comapFlatMap(string -> this.getValue((String)string).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unable to read property: " + this + " with value: " + string))), this::getName);
    private final Codec<Value<T>> valueCodec = this.codec.xmap(this::value, Value::value);

    protected Property(String string2, Class<T> class_) {
        this.clazz = class_;
        this.name = string2;
    }

    public Value<T> value(T t) {
        return new Value(this, (Comparable)t, null);
    }

    public Value<T> value(StateHolder<?, ?> stateHolder) {
        return new Value(this, (Comparable)stateHolder.getValue(this), null);
    }

    public Stream<Value<T>> getAllValues() {
        return this.getPossibleValues().stream().map(this::value);
    }

    public Codec<Value<T>> valueCodec() {
        return this.valueCodec;
    }

    public String getName() {
        return this.name;
    }

    public Class<T> getValueClass() {
        return this.clazz;
    }

    public abstract Collection<T> getPossibleValues();

    public abstract String getName(T var1);

    public abstract Optional<T> getValue(String var1);

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("name", (Object)this.name).add("clazz", this.clazz).add("values", this.getPossibleValues()).toString();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Property) {
            Property property = (Property)object;
            return this.clazz.equals(property.clazz) && this.name.equals(property.name);
        }
        return false;
    }

    public final int hashCode() {
        if (this.hashCode == null) {
            this.hashCode = this.generateHashCode();
        }
        return this.hashCode;
    }

    public int generateHashCode() {
        return 31 * this.clazz.hashCode() + this.name.hashCode();
    }

    public static final class Value<T extends Comparable<T>> {
        private final Property<T> property;
        private final T value;

        private Value(Property<T> property, T t) {
            if (!property.getPossibleValues().contains(t)) {
                throw new IllegalArgumentException("Value " + t + " does not belong to property " + property);
            }
            this.property = property;
            this.value = t;
        }

        public Property<T> getProperty() {
            return this.property;
        }

        public T value() {
            return this.value;
        }

        public String toString() {
            return this.property.getName() + "=" + this.property.getName(this.value);
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof Value)) {
                return false;
            }
            Value value = (Value)object;
            return this.property == value.property && this.value.equals(value.value);
        }

        public int hashCode() {
            int n = this.property.hashCode();
            n = 31 * n + this.value.hashCode();
            return n;
        }

        /* synthetic */ Value(Property property, Comparable comparable, 1 var3_3) {
            this(property, comparable);
        }
    }

}

