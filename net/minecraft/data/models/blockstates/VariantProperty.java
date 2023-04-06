/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.data.models.blockstates;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.Function;

public class VariantProperty<T> {
    private final String key;
    private final Function<T, JsonElement> serializer;

    public VariantProperty(String string, Function<T, JsonElement> function) {
        this.key = string;
        this.serializer = function;
    }

    public VariantProperty<T> withValue(T t) {
        return new Value(t);
    }

    public String toString() {
        return this.key;
    }

    public class Value {
        private final T value;

        public Value(T t) {
            this.value = t;
        }

        public void addToVariant(JsonObject jsonObject) {
            jsonObject.add(VariantProperty.this.key, (JsonElement)VariantProperty.this.serializer.apply(this.value));
        }

        public String toString() {
            return VariantProperty.this.key + "=" + this.value;
        }
    }

}

