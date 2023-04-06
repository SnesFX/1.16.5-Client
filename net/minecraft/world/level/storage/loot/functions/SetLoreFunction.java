/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Streams
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.AbstractList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetLoreFunction
extends LootItemConditionalFunction {
    private final boolean replace;
    private final List<Component> lore;
    @Nullable
    private final LootContext.EntityTarget resolutionContext;

    public SetLoreFunction(LootItemCondition[] arrlootItemCondition, boolean bl, List<Component> list, @Nullable LootContext.EntityTarget entityTarget) {
        super(arrlootItemCondition);
        this.replace = bl;
        this.lore = ImmutableList.copyOf(list);
        this.resolutionContext = entityTarget;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_LORE;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.resolutionContext != null ? ImmutableSet.of(this.resolutionContext.getParam()) : ImmutableSet.of();
    }

    @Override
    public ItemStack run(ItemStack itemStack, LootContext lootContext) {
        ListTag listTag = this.getLoreTag(itemStack, !this.lore.isEmpty());
        if (listTag != null) {
            if (this.replace) {
                listTag.clear();
            }
            UnaryOperator<Component> unaryOperator = SetNameFunction.createResolver(lootContext, this.resolutionContext);
            this.lore.stream().map(unaryOperator).map(Component.Serializer::toJson).map(StringTag::valueOf).forEach(listTag::add);
        }
        return itemStack;
    }

    @Nullable
    private ListTag getLoreTag(ItemStack itemStack, boolean bl) {
        CompoundTag compoundTag;
        CompoundTag compoundTag2;
        if (itemStack.hasTag()) {
            compoundTag2 = itemStack.getTag();
        } else if (bl) {
            compoundTag2 = new CompoundTag();
            itemStack.setTag(compoundTag2);
        } else {
            return null;
        }
        if (compoundTag2.contains("display", 10)) {
            compoundTag = compoundTag2.getCompound("display");
        } else if (bl) {
            compoundTag = new CompoundTag();
            compoundTag2.put("display", compoundTag);
        } else {
            return null;
        }
        if (compoundTag.contains("Lore", 9)) {
            return compoundTag.getList("Lore", 8);
        }
        if (bl) {
            ListTag listTag = new ListTag();
            compoundTag.put("Lore", listTag);
            return listTag;
        }
        return null;
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<SetLoreFunction> {
        @Override
        public void serialize(JsonObject jsonObject, SetLoreFunction setLoreFunction, JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, setLoreFunction, jsonSerializationContext);
            jsonObject.addProperty("replace", Boolean.valueOf(setLoreFunction.replace));
            JsonArray jsonArray = new JsonArray();
            for (Component component : setLoreFunction.lore) {
                jsonArray.add(Component.Serializer.toJsonTree(component));
            }
            jsonObject.add("lore", (JsonElement)jsonArray);
            if (setLoreFunction.resolutionContext != null) {
                jsonObject.add("entity", jsonSerializationContext.serialize((Object)setLoreFunction.resolutionContext));
            }
        }

        @Override
        public SetLoreFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            boolean bl = GsonHelper.getAsBoolean(jsonObject, "replace", false);
            List list = (List)Streams.stream((Iterable)GsonHelper.getAsJsonArray(jsonObject, "lore")).map(Component.Serializer::fromJson).collect(ImmutableList.toImmutableList());
            LootContext.EntityTarget entityTarget = GsonHelper.getAsObject(jsonObject, "entity", null, jsonDeserializationContext, LootContext.EntityTarget.class);
            return new SetLoreFunction(arrlootItemCondition, bl, list, entityTarget);
        }

        @Override
        public /* synthetic */ LootItemConditionalFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            return this.deserialize(jsonObject, jsonDeserializationContext, arrlootItemCondition);
        }
    }

}

