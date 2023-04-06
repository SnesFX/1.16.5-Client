/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class TagEntry
extends LootPoolSingletonContainer {
    private final Tag<Item> tag;
    private final boolean expand;

    private TagEntry(Tag<Item> tag, boolean bl, int n, int n2, LootItemCondition[] arrlootItemCondition, LootItemFunction[] arrlootItemFunction) {
        super(n, n2, arrlootItemCondition, arrlootItemFunction);
        this.tag = tag;
        this.expand = bl;
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.TAG;
    }

    @Override
    public void createItemStack(Consumer<ItemStack> consumer, LootContext lootContext) {
        this.tag.getValues().forEach(item -> consumer.accept(new ItemStack((ItemLike)item)));
    }

    private boolean expandTag(LootContext lootContext, Consumer<LootPoolEntry> consumer) {
        if (this.canRun(lootContext)) {
            for (final Item item : this.tag.getValues()) {
                consumer.accept(new LootPoolSingletonContainer.EntryBase(){

                    @Override
                    public void createItemStack(Consumer<ItemStack> consumer, LootContext lootContext) {
                        consumer.accept(new ItemStack(item));
                    }
                });
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean expand(LootContext lootContext, Consumer<LootPoolEntry> consumer) {
        if (this.expand) {
            return this.expandTag(lootContext, consumer);
        }
        return super.expand(lootContext, consumer);
    }

    public static LootPoolSingletonContainer.Builder<?> expandTag(Tag<Item> tag) {
        return TagEntry.simpleBuilder((n, n2, arrlootItemCondition, arrlootItemFunction) -> new TagEntry(tag, true, n, n2, arrlootItemCondition, arrlootItemFunction));
    }

    public static class Serializer
    extends LootPoolSingletonContainer.Serializer<TagEntry> {
        @Override
        public void serializeCustom(JsonObject jsonObject, TagEntry tagEntry, JsonSerializationContext jsonSerializationContext) {
            super.serializeCustom(jsonObject, tagEntry, jsonSerializationContext);
            jsonObject.addProperty("name", SerializationTags.getInstance().getItems().getIdOrThrow(tagEntry.tag).toString());
            jsonObject.addProperty("expand", Boolean.valueOf(tagEntry.expand));
        }

        @Override
        protected TagEntry deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int n, int n2, LootItemCondition[] arrlootItemCondition, LootItemFunction[] arrlootItemFunction) {
            ResourceLocation resourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "name"));
            Tag<Item> tag = SerializationTags.getInstance().getItems().getTag(resourceLocation);
            if (tag == null) {
                throw new JsonParseException("Can't find tag: " + resourceLocation);
            }
            boolean bl = GsonHelper.getAsBoolean(jsonObject, "expand");
            return new TagEntry(tag, bl, n, n2, arrlootItemCondition, arrlootItemFunction);
        }

        @Override
        protected /* synthetic */ LootPoolSingletonContainer deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int n, int n2, LootItemCondition[] arrlootItemCondition, LootItemFunction[] arrlootItemFunction) {
            return this.deserialize(jsonObject, jsonDeserializationContext, n, n2, arrlootItemCondition, arrlootItemFunction);
        }
    }

}

