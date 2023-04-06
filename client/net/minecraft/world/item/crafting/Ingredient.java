/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSyntaxException
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntComparator
 *  it.unimi.dsi.fastutil.ints.IntComparators
 *  it.unimi.dsi.fastutil.ints.IntList
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public final class Ingredient
implements Predicate<ItemStack> {
    public static final Ingredient EMPTY = new Ingredient(Stream.empty());
    private final Value[] values;
    private ItemStack[] itemStacks;
    private IntList stackingIds;

    private Ingredient(Stream<? extends Value> stream) {
        this.values = (Value[])stream.toArray(n -> new Value[n]);
    }

    public ItemStack[] getItems() {
        this.dissolve();
        return this.itemStacks;
    }

    private void dissolve() {
        if (this.itemStacks == null) {
            this.itemStacks = (ItemStack[])Arrays.stream(this.values).flatMap(value -> value.getItems().stream()).distinct().toArray(n -> new ItemStack[n]);
        }
    }

    @Override
    public boolean test(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        this.dissolve();
        if (this.itemStacks.length == 0) {
            return itemStack.isEmpty();
        }
        for (ItemStack itemStack2 : this.itemStacks) {
            if (itemStack2.getItem() != itemStack.getItem()) continue;
            return true;
        }
        return false;
    }

    public IntList getStackingIds() {
        if (this.stackingIds == null) {
            this.dissolve();
            this.stackingIds = new IntArrayList(this.itemStacks.length);
            for (ItemStack itemStack : this.itemStacks) {
                this.stackingIds.add(StackedContents.getStackingIndex(itemStack));
            }
            this.stackingIds.sort((Comparator)IntComparators.NATURAL_COMPARATOR);
        }
        return this.stackingIds;
    }

    public void toNetwork(FriendlyByteBuf friendlyByteBuf) {
        this.dissolve();
        friendlyByteBuf.writeVarInt(this.itemStacks.length);
        for (int i = 0; i < this.itemStacks.length; ++i) {
            friendlyByteBuf.writeItem(this.itemStacks[i]);
        }
    }

    public JsonElement toJson() {
        if (this.values.length == 1) {
            return this.values[0].serialize();
        }
        JsonArray jsonArray = new JsonArray();
        for (Value value : this.values) {
            jsonArray.add((JsonElement)value.serialize());
        }
        return jsonArray;
    }

    public boolean isEmpty() {
        return !(this.values.length != 0 || this.itemStacks != null && this.itemStacks.length != 0 || this.stackingIds != null && !this.stackingIds.isEmpty());
    }

    private static Ingredient fromValues(Stream<? extends Value> stream) {
        Ingredient ingredient = new Ingredient(stream);
        return ingredient.values.length == 0 ? EMPTY : ingredient;
    }

    public static Ingredient of(ItemLike ... arritemLike) {
        return Ingredient.of(Arrays.stream(arritemLike).map(ItemStack::new));
    }

    public static Ingredient of(ItemStack ... arritemStack) {
        return Ingredient.of(Arrays.stream(arritemStack));
    }

    public static Ingredient of(Stream<ItemStack> stream) {
        return Ingredient.fromValues(stream.filter(itemStack -> !itemStack.isEmpty()).map(itemStack -> new ItemValue((ItemStack)itemStack)));
    }

    public static Ingredient of(Tag<Item> tag) {
        return Ingredient.fromValues(Stream.of(new TagValue(tag)));
    }

    public static Ingredient fromNetwork(FriendlyByteBuf friendlyByteBuf) {
        int n = friendlyByteBuf.readVarInt();
        return Ingredient.fromValues(Stream.generate(() -> new ItemValue(friendlyByteBuf.readItem())).limit(n));
    }

    public static Ingredient fromJson(@Nullable JsonElement jsonElement2) {
        if (jsonElement2 == null || jsonElement2.isJsonNull()) {
            throw new JsonSyntaxException("Item cannot be null");
        }
        if (jsonElement2.isJsonObject()) {
            return Ingredient.fromValues(Stream.of(Ingredient.valueFromJson(jsonElement2.getAsJsonObject())));
        }
        if (jsonElement2.isJsonArray()) {
            JsonArray jsonArray = jsonElement2.getAsJsonArray();
            if (jsonArray.size() == 0) {
                throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
            }
            return Ingredient.fromValues(StreamSupport.stream(jsonArray.spliterator(), false).map(jsonElement -> Ingredient.valueFromJson(GsonHelper.convertToJsonObject(jsonElement, "item"))));
        }
        throw new JsonSyntaxException("Expected item to be object or array of objects");
    }

    private static Value valueFromJson(JsonObject jsonObject) {
        if (jsonObject.has("item") && jsonObject.has("tag")) {
            throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
        }
        if (jsonObject.has("item")) {
            ResourceLocation resourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "item"));
            Item item = Registry.ITEM.getOptional(resourceLocation).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + resourceLocation + "'"));
            return new ItemValue(new ItemStack(item));
        }
        if (jsonObject.has("tag")) {
            ResourceLocation resourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "tag"));
            Tag<Item> tag = SerializationTags.getInstance().getItems().getTag(resourceLocation);
            if (tag == null) {
                throw new JsonSyntaxException("Unknown item tag '" + resourceLocation + "'");
            }
            return new TagValue(tag);
        }
        throw new JsonParseException("An ingredient entry needs either a tag or an item");
    }

    @Override
    public /* synthetic */ boolean test(@Nullable Object object) {
        return this.test((ItemStack)object);
    }

    static class TagValue
    implements Value {
        private final Tag<Item> tag;

        private TagValue(Tag<Item> tag) {
            this.tag = tag;
        }

        @Override
        public Collection<ItemStack> getItems() {
            ArrayList arrayList = Lists.newArrayList();
            for (Item item : this.tag.getValues()) {
                arrayList.add(new ItemStack(item));
            }
            return arrayList;
        }

        @Override
        public JsonObject serialize() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("tag", SerializationTags.getInstance().getItems().getIdOrThrow(this.tag).toString());
            return jsonObject;
        }
    }

    static class ItemValue
    implements Value {
        private final ItemStack item;

        private ItemValue(ItemStack itemStack) {
            this.item = itemStack;
        }

        @Override
        public Collection<ItemStack> getItems() {
            return Collections.singleton(this.item);
        }

        @Override
        public JsonObject serialize() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("item", Registry.ITEM.getKey(this.item.getItem()).toString());
            return jsonObject;
        }
    }

    static interface Value {
        public Collection<ItemStack> getItems();

        public JsonObject serialize();
    }

}

