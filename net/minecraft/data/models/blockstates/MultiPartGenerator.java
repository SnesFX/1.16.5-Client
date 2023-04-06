/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.data.models.blockstates;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class MultiPartGenerator
implements BlockStateGenerator {
    private final Block block;
    private final List<Entry> parts = Lists.newArrayList();

    private MultiPartGenerator(Block block) {
        this.block = block;
    }

    @Override
    public Block getBlock() {
        return this.block;
    }

    public static MultiPartGenerator multiPart(Block block) {
        return new MultiPartGenerator(block);
    }

    public MultiPartGenerator with(List<Variant> list) {
        this.parts.add(new Entry(list));
        return this;
    }

    public MultiPartGenerator with(Variant variant) {
        return this.with((List<Variant>)ImmutableList.of((Object)variant));
    }

    public MultiPartGenerator with(Condition condition, List<Variant> list) {
        this.parts.add(new ConditionalEntry(condition, list));
        return this;
    }

    public MultiPartGenerator with(Condition condition, Variant ... arrvariant) {
        return this.with(condition, (List<Variant>)ImmutableList.copyOf((Object[])arrvariant));
    }

    public MultiPartGenerator with(Condition condition, Variant variant) {
        return this.with(condition, (List<Variant>)ImmutableList.of((Object)variant));
    }

    @Override
    public JsonElement get() {
        StateDefinition<Block, BlockState> stateDefinition = this.block.getStateDefinition();
        this.parts.forEach(entry -> entry.validate(stateDefinition));
        JsonArray jsonArray = new JsonArray();
        this.parts.stream().map(Entry::get).forEach(((JsonArray)jsonArray)::add);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("multipart", (JsonElement)jsonArray);
        return jsonObject;
    }

    @Override
    public /* synthetic */ Object get() {
        return this.get();
    }

    static class ConditionalEntry
    extends Entry {
        private final Condition condition;

        private ConditionalEntry(Condition condition, List<Variant> list) {
            super(list);
            this.condition = condition;
        }

        @Override
        public void validate(StateDefinition<?, ?> stateDefinition) {
            this.condition.validate(stateDefinition);
        }

        @Override
        public void decorate(JsonObject jsonObject) {
            jsonObject.add("when", (JsonElement)this.condition.get());
        }
    }

    static class Entry
    implements Supplier<JsonElement> {
        private final List<Variant> variants;

        private Entry(List<Variant> list) {
            this.variants = list;
        }

        public void validate(StateDefinition<?, ?> stateDefinition) {
        }

        public void decorate(JsonObject jsonObject) {
        }

        @Override
        public JsonElement get() {
            JsonObject jsonObject = new JsonObject();
            this.decorate(jsonObject);
            jsonObject.add("apply", Variant.convertList(this.variants));
            return jsonObject;
        }

        @Override
        public /* synthetic */ Object get() {
            return this.get();
        }
    }

}

