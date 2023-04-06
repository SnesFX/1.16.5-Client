/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.data.models.blockstates;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Selector;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class MultiVariantGenerator
implements BlockStateGenerator {
    private final Block block;
    private final List<Variant> baseVariants;
    private final Set<Property<?>> seenProperties = Sets.newHashSet();
    private final List<PropertyDispatch> declaredPropertySets = Lists.newArrayList();

    private MultiVariantGenerator(Block block, List<Variant> list) {
        this.block = block;
        this.baseVariants = list;
    }

    public MultiVariantGenerator with(PropertyDispatch propertyDispatch) {
        propertyDispatch.getDefinedProperties().forEach(property -> {
            if (this.block.getStateDefinition().getProperty(property.getName()) != property) {
                throw new IllegalStateException("Property " + property + " is not defined for block " + this.block);
            }
            if (!this.seenProperties.add((Property<?>)property)) {
                throw new IllegalStateException("Values of property " + property + " already defined for block " + this.block);
            }
        });
        this.declaredPropertySets.add(propertyDispatch);
        return this;
    }

    @Override
    public JsonElement get() {
        PropertyDispatch propertyDispatch2;
        Stream<Object> stream = Stream.of(Pair.of((Object)Selector.empty(), this.baseVariants));
        for (PropertyDispatch propertyDispatch2 : this.declaredPropertySets) {
            Map<Selector, List<Variant>> map = propertyDispatch2.getEntries();
            stream = stream.flatMap(pair -> map.entrySet().stream().map(entry -> {
                Selector selector = ((Selector)pair.getFirst()).extend((Selector)entry.getKey());
                List<Variant> list = MultiVariantGenerator.mergeVariants((List)pair.getSecond(), (List)entry.getValue());
                return Pair.of((Object)selector, list);
            }));
        }
        TreeMap treeMap = new TreeMap();
        stream.forEach(pair -> treeMap.put(((Selector)pair.getFirst()).getKey(), Variant.convertList((List)pair.getSecond())));
        propertyDispatch2 = new JsonObject();
        propertyDispatch2.add("variants", (JsonElement)Util.make(new JsonObject(), jsonObject -> treeMap.forEach((arg_0, arg_1) -> ((JsonObject)jsonObject).add(arg_0, arg_1))));
        return propertyDispatch2;
    }

    private static List<Variant> mergeVariants(List<Variant> list, List<Variant> list2) {
        ImmutableList.Builder builder = ImmutableList.builder();
        list.forEach(variant -> list2.forEach(variant2 -> builder.add((Object)Variant.merge(variant, variant2))));
        return builder.build();
    }

    @Override
    public Block getBlock() {
        return this.block;
    }

    public static MultiVariantGenerator multiVariant(Block block) {
        return new MultiVariantGenerator(block, (List<Variant>)ImmutableList.of((Object)Variant.variant()));
    }

    public static MultiVariantGenerator multiVariant(Block block, Variant variant) {
        return new MultiVariantGenerator(block, (List<Variant>)ImmutableList.of((Object)variant));
    }

    public static MultiVariantGenerator multiVariant(Block block, Variant ... arrvariant) {
        return new MultiVariantGenerator(block, (List<Variant>)ImmutableList.copyOf((Object[])arrvariant));
    }

    @Override
    public /* synthetic */ Object get() {
        return this.get();
    }
}

