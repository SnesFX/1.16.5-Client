/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class StatePropertiesPredicate {
    public static final StatePropertiesPredicate ANY = new StatePropertiesPredicate((List<PropertyMatcher>)ImmutableList.of());
    private final List<PropertyMatcher> properties;

    private static PropertyMatcher fromJson(String string, JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            String string2 = jsonElement.getAsString();
            return new ExactPropertyMatcher(string, string2);
        }
        JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "value");
        String string3 = jsonObject.has("min") ? StatePropertiesPredicate.getStringOrNull(jsonObject.get("min")) : null;
        String string4 = jsonObject.has("max") ? StatePropertiesPredicate.getStringOrNull(jsonObject.get("max")) : null;
        return string3 != null && string3.equals(string4) ? new ExactPropertyMatcher(string, string3) : new RangedPropertyMatcher(string, string3, string4);
    }

    @Nullable
    private static String getStringOrNull(JsonElement jsonElement) {
        if (jsonElement.isJsonNull()) {
            return null;
        }
        return jsonElement.getAsString();
    }

    private StatePropertiesPredicate(List<PropertyMatcher> list) {
        this.properties = ImmutableList.copyOf(list);
    }

    public <S extends StateHolder<?, S>> boolean matches(StateDefinition<?, S> stateDefinition, S s) {
        for (PropertyMatcher propertyMatcher : this.properties) {
            if (propertyMatcher.match(stateDefinition, s)) continue;
            return false;
        }
        return true;
    }

    public boolean matches(BlockState blockState) {
        return this.matches(blockState.getBlock().getStateDefinition(), blockState);
    }

    public boolean matches(FluidState fluidState) {
        return this.matches(fluidState.getType().getStateDefinition(), fluidState);
    }

    public void checkState(StateDefinition<?, ?> stateDefinition, Consumer<String> consumer) {
        this.properties.forEach(propertyMatcher -> propertyMatcher.checkState(stateDefinition, consumer));
    }

    public static StatePropertiesPredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "properties");
        ArrayList arrayList = Lists.newArrayList();
        for (Map.Entry entry : jsonObject.entrySet()) {
            arrayList.add(StatePropertiesPredicate.fromJson((String)entry.getKey(), (JsonElement)entry.getValue()));
        }
        return new StatePropertiesPredicate(arrayList);
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        if (!this.properties.isEmpty()) {
            this.properties.forEach(propertyMatcher -> jsonObject.add(propertyMatcher.getName(), propertyMatcher.toJson()));
        }
        return jsonObject;
    }

    public static class Builder {
        private final List<PropertyMatcher> matchers = Lists.newArrayList();

        private Builder() {
        }

        public static Builder properties() {
            return new Builder();
        }

        public Builder hasProperty(Property<?> property, String string) {
            this.matchers.add(new ExactPropertyMatcher(property.getName(), string));
            return this;
        }

        public Builder hasProperty(Property<Integer> property, int n) {
            return this.hasProperty((Property<T>)property, (T)Integer.toString(n));
        }

        public Builder hasProperty(Property<Boolean> property, boolean bl) {
            return this.hasProperty((Property<T>)property, (T)Boolean.toString(bl));
        }

        public <T extends Comparable<T> & StringRepresentable> Builder hasProperty(Property<T> property, T t) {
            return this.hasProperty(property, (T)((StringRepresentable)t).getSerializedName());
        }

        public StatePropertiesPredicate build() {
            return new StatePropertiesPredicate(this.matchers);
        }
    }

    static class RangedPropertyMatcher
    extends PropertyMatcher {
        @Nullable
        private final String minValue;
        @Nullable
        private final String maxValue;

        public RangedPropertyMatcher(String string, @Nullable String string2, @Nullable String string3) {
            super(string);
            this.minValue = string2;
            this.maxValue = string3;
        }

        @Override
        protected <T extends Comparable<T>> boolean match(StateHolder<?, ?> stateHolder, Property<T> property) {
            Optional<T> optional;
            T t = stateHolder.getValue(property);
            if (!(this.minValue == null || (optional = property.getValue(this.minValue)).isPresent() && t.compareTo(optional.get()) >= 0)) {
                return false;
            }
            return this.maxValue == null || (optional = property.getValue(this.maxValue)).isPresent() && t.compareTo(optional.get()) <= 0;
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            if (this.minValue != null) {
                jsonObject.addProperty("min", this.minValue);
            }
            if (this.maxValue != null) {
                jsonObject.addProperty("max", this.maxValue);
            }
            return jsonObject;
        }
    }

    static class ExactPropertyMatcher
    extends PropertyMatcher {
        private final String value;

        public ExactPropertyMatcher(String string, String string2) {
            super(string);
            this.value = string2;
        }

        @Override
        protected <T extends Comparable<T>> boolean match(StateHolder<?, ?> stateHolder, Property<T> property) {
            T t = stateHolder.getValue(property);
            Optional<T> optional = property.getValue(this.value);
            return optional.isPresent() && t.compareTo(optional.get()) == 0;
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(this.value);
        }
    }

    static abstract class PropertyMatcher {
        private final String name;

        public PropertyMatcher(String string) {
            this.name = string;
        }

        public <S extends StateHolder<?, S>> boolean match(StateDefinition<?, S> stateDefinition, S s) {
            Property<?> property = stateDefinition.getProperty(this.name);
            if (property == null) {
                return false;
            }
            return this.match(s, property);
        }

        protected abstract <T extends Comparable<T>> boolean match(StateHolder<?, ?> var1, Property<T> var2);

        public abstract JsonElement toJson();

        public String getName() {
            return this.name;
        }

        public void checkState(StateDefinition<?, ?> stateDefinition, Consumer<String> consumer) {
            Property<?> property = stateDefinition.getProperty(this.name);
            if (property == null) {
                consumer.accept(this.name);
            }
        }
    }

}

