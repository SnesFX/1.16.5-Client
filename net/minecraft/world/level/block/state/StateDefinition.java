/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.MoreObjects$ToStringHelper
 *  com.google.common.collect.ImmutableCollection
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSortedMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.UnmodifiableIterator
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Decoder
 *  com.mojang.serialization.Encoder
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.MapDecoder
 *  com.mojang.serialization.MapEncoder
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

public class StateDefinition<O, S extends StateHolder<O, S>> {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-z0-9_]+$");
    private final O owner;
    private final ImmutableSortedMap<String, Property<?>> propertiesByName;
    private final ImmutableList<S> states;

    protected StateDefinition(Function<O, S> function, O o, Factory<O, S> factory, Map<String, Property<?>> map) {
        Object object2;
        this.owner = o;
        this.propertiesByName = ImmutableSortedMap.copyOf(map);
        Supplier<StateHolder> supplier = () -> (StateHolder)function.apply(o);
        MapCodec<StateHolder> mapCodec = MapCodec.of((MapEncoder)Encoder.empty(), (MapDecoder)Decoder.unit(supplier));
        for (Object object2 : this.propertiesByName.entrySet()) {
            mapCodec = StateDefinition.appendPropertyCodec(mapCodec, supplier, (String)object2.getKey(), (Property)object2.getValue());
        }
        UnmodifiableIterator unmodifiableIterator = mapCodec;
        object2 = Maps.newLinkedHashMap();
        ArrayList arrayList = Lists.newArrayList();
        Stream<List<List<Object>>> stream = Stream.of(Collections.emptyList());
        for (Object object3 : this.propertiesByName.values()) {
            stream = stream.flatMap(arg_0 -> StateDefinition.lambda$new$2((Property)object3, arg_0));
        }
        stream.forEach(arg_0 -> StateDefinition.lambda$new$3(factory, o, (MapCodec)unmodifiableIterator, (Map)object2, arrayList, arg_0));
        for (Object object3 : arrayList) {
            ((StateHolder)object3).populateNeighbours(object2);
        }
        this.states = ImmutableList.copyOf((Collection)arrayList);
    }

    private static <S extends StateHolder<?, S>, T extends Comparable<T>> MapCodec<S> appendPropertyCodec(MapCodec<S> mapCodec, Supplier<S> supplier, String string, Property<T> property) {
        return Codec.mapPair(mapCodec, (MapCodec)property.valueCodec().fieldOf(string).setPartial(() -> property.value((StateHolder)supplier.get()))).xmap(pair -> (StateHolder)((StateHolder)pair.getFirst()).setValue(property, ((Property.Value)pair.getSecond()).value()), stateHolder -> Pair.of((Object)stateHolder, property.value((StateHolder<?, ?>)stateHolder)));
    }

    public ImmutableList<S> getPossibleStates() {
        return this.states;
    }

    public S any() {
        return (S)((StateHolder)this.states.get(0));
    }

    public O getOwner() {
        return this.owner;
    }

    public Collection<Property<?>> getProperties() {
        return this.propertiesByName.values();
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("block", this.owner).add("properties", this.propertiesByName.values().stream().map(Property::getName).collect(Collectors.toList())).toString();
    }

    @Nullable
    public Property<?> getProperty(String string) {
        return (Property)this.propertiesByName.get((Object)string);
    }

    private static /* synthetic */ void lambda$new$3(Factory factory, Object object, MapCodec mapCodec, Map map, List list, List list2) {
        ImmutableMap immutableMap = (ImmutableMap)list2.stream().collect(ImmutableMap.toImmutableMap(Pair::getFirst, Pair::getSecond));
        StateHolder stateHolder = (StateHolder)factory.create(object, immutableMap, mapCodec);
        map.put(immutableMap, stateHolder);
        list.add(stateHolder);
    }

    private static /* synthetic */ Stream lambda$new$2(Property property, List list) {
        return property.getPossibleValues().stream().map(comparable -> {
            ArrayList arrayList = Lists.newArrayList((Iterable)list);
            arrayList.add(Pair.of((Object)property, (Object)comparable));
            return arrayList;
        });
    }

    public static class Builder<O, S extends StateHolder<O, S>> {
        private final O owner;
        private final Map<String, Property<?>> properties = Maps.newHashMap();

        public Builder(O o) {
            this.owner = o;
        }

        public Builder<O, S> add(Property<?> ... arrproperty) {
            for (Property<?> property : arrproperty) {
                this.validateProperty(property);
                this.properties.put(property.getName(), property);
            }
            return this;
        }

        private <T extends Comparable<T>> void validateProperty(Property<T> property) {
            String string = property.getName();
            if (!NAME_PATTERN.matcher(string).matches()) {
                throw new IllegalArgumentException(this.owner + " has invalidly named property: " + string);
            }
            Collection<T> collection = property.getPossibleValues();
            if (collection.size() <= 1) {
                throw new IllegalArgumentException(this.owner + " attempted use property " + string + " with <= 1 possible values");
            }
            for (Comparable comparable : collection) {
                String string2 = property.getName(comparable);
                if (NAME_PATTERN.matcher(string2).matches()) continue;
                throw new IllegalArgumentException(this.owner + " has property: " + string + " with invalidly named value: " + string2);
            }
            if (this.properties.containsKey(string)) {
                throw new IllegalArgumentException(this.owner + " has duplicate property: " + string);
            }
        }

        public StateDefinition<O, S> create(Function<O, S> function, Factory<O, S> factory) {
            return new StateDefinition<O, S>(function, this.owner, factory, this.properties);
        }
    }

    public static interface Factory<O, S> {
        public S create(O var1, ImmutableMap<Property<?>, Comparable<?>> var2, MapCodec<S> var3);
    }

}

