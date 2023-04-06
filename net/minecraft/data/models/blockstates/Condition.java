/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.data.models.blockstates;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public interface Condition
extends Supplier<JsonElement> {
    public void validate(StateDefinition<?, ?> var1);

    public static TerminalCondition condition() {
        return new TerminalCondition();
    }

    public static Condition or(Condition ... arrcondition) {
        return new CompositeCondition(Operation.OR, Arrays.asList(arrcondition));
    }

    public static class TerminalCondition
    implements Condition {
        private final Map<Property<?>, String> terms = Maps.newHashMap();

        private static <T extends Comparable<T>> String joinValues(Property<T> property, Stream<T> stream) {
            return stream.map(property::getName).collect(Collectors.joining("|"));
        }

        private static <T extends Comparable<T>> String getTerm(Property<T> property, T t, T[] arrT) {
            return TerminalCondition.joinValues(property, Stream.concat(Stream.of(t), Stream.of(arrT)));
        }

        private <T extends Comparable<T>> void putValue(Property<T> property, String string) {
            String string2 = this.terms.put(property, string);
            if (string2 != null) {
                throw new IllegalStateException("Tried to replace " + property + " value from " + string2 + " to " + string);
            }
        }

        public final <T extends Comparable<T>> TerminalCondition term(Property<T> property, T t) {
            this.putValue(property, property.getName(t));
            return this;
        }

        @SafeVarargs
        public final <T extends Comparable<T>> TerminalCondition term(Property<T> property, T t, T ... arrT) {
            this.putValue(property, TerminalCondition.getTerm(property, t, arrT));
            return this;
        }

        @Override
        public JsonElement get() {
            JsonObject jsonObject = new JsonObject();
            this.terms.forEach((property, string) -> jsonObject.addProperty(property.getName(), string));
            return jsonObject;
        }

        @Override
        public void validate(StateDefinition<?, ?> stateDefinition) {
            List list = this.terms.keySet().stream().filter(property -> stateDefinition.getProperty(property.getName()) != property).collect(Collectors.toList());
            if (!list.isEmpty()) {
                throw new IllegalStateException("Properties " + list + " are missing from " + stateDefinition);
            }
        }

        @Override
        public /* synthetic */ Object get() {
            return this.get();
        }
    }

    public static class CompositeCondition
    implements Condition {
        private final Operation operation;
        private final List<Condition> subconditions;

        private CompositeCondition(Operation operation, List<Condition> list) {
            this.operation = operation;
            this.subconditions = list;
        }

        @Override
        public void validate(StateDefinition<?, ?> stateDefinition) {
            this.subconditions.forEach(condition -> condition.validate(stateDefinition));
        }

        @Override
        public JsonElement get() {
            JsonArray jsonArray = new JsonArray();
            this.subconditions.stream().map(Supplier::get).forEach(((JsonArray)jsonArray)::add);
            JsonObject jsonObject = new JsonObject();
            jsonObject.add(this.operation.id, (JsonElement)jsonArray);
            return jsonObject;
        }

        @Override
        public /* synthetic */ Object get() {
            return this.get();
        }
    }

    public static enum Operation {
        AND("AND"),
        OR("OR");
        
        private final String id;

        private Operation(String string2) {
            this.id = string2;
        }
    }

}

