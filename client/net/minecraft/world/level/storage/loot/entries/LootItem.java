/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItem
extends LootPoolSingletonContainer {
    private final Item item;

    private LootItem(Item item, int n, int n2, LootItemCondition[] arrlootItemCondition, LootItemFunction[] arrlootItemFunction) {
        super(n, n2, arrlootItemCondition, arrlootItemFunction);
        this.item = item;
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.ITEM;
    }

    @Override
    public void createItemStack(Consumer<ItemStack> consumer, LootContext lootContext) {
        consumer.accept(new ItemStack(this.item));
    }

    public static LootPoolSingletonContainer.Builder<?> lootTableItem(ItemLike itemLike) {
        return LootItem.simpleBuilder((n, n2, arrlootItemCondition, arrlootItemFunction) -> new LootItem(itemLike.asItem(), n, n2, arrlootItemCondition, arrlootItemFunction));
    }

    public static class Serializer
    extends LootPoolSingletonContainer.Serializer<LootItem> {
        @Override
        public void serializeCustom(JsonObject jsonObject, LootItem lootItem, JsonSerializationContext jsonSerializationContext) {
            super.serializeCustom(jsonObject, lootItem, jsonSerializationContext);
            ResourceLocation resourceLocation = Registry.ITEM.getKey(lootItem.item);
            if (resourceLocation == null) {
                throw new IllegalArgumentException("Can't serialize unknown item " + lootItem.item);
            }
            jsonObject.addProperty("name", resourceLocation.toString());
        }

        @Override
        protected LootItem deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int n, int n2, LootItemCondition[] arrlootItemCondition, LootItemFunction[] arrlootItemFunction) {
            Item item = GsonHelper.getAsItem(jsonObject, "name");
            return new LootItem(item, n, n2, arrlootItemCondition, arrlootItemFunction);
        }

        @Override
        protected /* synthetic */ LootPoolSingletonContainer deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int n, int n2, LootItemCondition[] arrlootItemCondition, LootItemFunction[] arrlootItemFunction) {
            return this.deserialize(jsonObject, jsonDeserializationContext, n, n2, arrlootItemCondition, arrlootItemFunction);
        }
    }

}

