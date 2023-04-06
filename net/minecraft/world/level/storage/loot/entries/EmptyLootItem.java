/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 */
package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.function.Consumer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EmptyLootItem
extends LootPoolSingletonContainer {
    private EmptyLootItem(int n, int n2, LootItemCondition[] arrlootItemCondition, LootItemFunction[] arrlootItemFunction) {
        super(n, n2, arrlootItemCondition, arrlootItemFunction);
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.EMPTY;
    }

    @Override
    public void createItemStack(Consumer<ItemStack> consumer, LootContext lootContext) {
    }

    public static LootPoolSingletonContainer.Builder<?> emptyItem() {
        return EmptyLootItem.simpleBuilder((arg_0, arg_1, arg_2, arg_3) -> EmptyLootItem.new(arg_0, arg_1, arg_2, arg_3));
    }

    public static class Serializer
    extends LootPoolSingletonContainer.Serializer<EmptyLootItem> {
        @Override
        public EmptyLootItem deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int n, int n2, LootItemCondition[] arrlootItemCondition, LootItemFunction[] arrlootItemFunction) {
            return new EmptyLootItem(n, n2, arrlootItemCondition, arrlootItemFunction);
        }

        @Override
        public /* synthetic */ LootPoolSingletonContainer deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int n, int n2, LootItemCondition[] arrlootItemCondition, LootItemFunction[] arrlootItemFunction) {
            return this.deserialize(jsonObject, jsonDeserializationContext, n, n2, arrlootItemCondition, arrlootItemFunction);
        }
    }

}

