/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetContainerContents
extends LootItemConditionalFunction {
    private final List<LootPoolEntryContainer> entries;

    private SetContainerContents(LootItemCondition[] arrlootItemCondition, List<LootPoolEntryContainer> list) {
        super(arrlootItemCondition);
        this.entries = ImmutableList.copyOf(list);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_CONTENTS;
    }

    @Override
    public ItemStack run(ItemStack itemStack, LootContext lootContext) {
        if (itemStack.isEmpty()) {
            return itemStack;
        }
        NonNullList<ItemStack> nonNullList = NonNullList.create();
        this.entries.forEach(lootPoolEntryContainer -> lootPoolEntryContainer.expand(lootContext, lootPoolEntry -> lootPoolEntry.createItemStack(LootTable.createStackSplitter(nonNullList::add), lootContext)));
        CompoundTag compoundTag = new CompoundTag();
        ContainerHelper.saveAllItems(compoundTag, nonNullList);
        CompoundTag compoundTag2 = itemStack.getOrCreateTag();
        compoundTag2.put("BlockEntityTag", compoundTag.merge(compoundTag2.getCompound("BlockEntityTag")));
        return itemStack;
    }

    @Override
    public void validate(ValidationContext validationContext) {
        super.validate(validationContext);
        for (int i = 0; i < this.entries.size(); ++i) {
            this.entries.get(i).validate(validationContext.forChild(".entry[" + i + "]"));
        }
    }

    public static Builder setContents() {
        return new Builder();
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<SetContainerContents> {
        @Override
        public void serialize(JsonObject jsonObject, SetContainerContents setContainerContents, JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, setContainerContents, jsonSerializationContext);
            jsonObject.add("entries", jsonSerializationContext.serialize((Object)setContainerContents.entries));
        }

        @Override
        public SetContainerContents deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            LootPoolEntryContainer[] arrlootPoolEntryContainer = GsonHelper.getAsObject(jsonObject, "entries", jsonDeserializationContext, LootPoolEntryContainer[].class);
            return new SetContainerContents(arrlootItemCondition, Arrays.asList(arrlootPoolEntryContainer));
        }

        @Override
        public /* synthetic */ LootItemConditionalFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            return this.deserialize(jsonObject, jsonDeserializationContext, arrlootItemCondition);
        }
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final List<LootPoolEntryContainer> entries = Lists.newArrayList();

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder withEntry(LootPoolEntryContainer.Builder<?> builder) {
            this.entries.add(builder.build());
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new SetContainerContents(this.getConditions(), this.entries);
        }

        @Override
        protected /* synthetic */ LootItemConditionalFunction.Builder getThis() {
            return this.getThis();
        }
    }

}

