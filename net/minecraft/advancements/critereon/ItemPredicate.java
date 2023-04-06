/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;

public class ItemPredicate {
    public static final ItemPredicate ANY = new ItemPredicate();
    @Nullable
    private final Tag<Item> tag;
    @Nullable
    private final Item item;
    private final MinMaxBounds.Ints count;
    private final MinMaxBounds.Ints durability;
    private final EnchantmentPredicate[] enchantments;
    private final EnchantmentPredicate[] storedEnchantments;
    @Nullable
    private final Potion potion;
    private final NbtPredicate nbt;

    public ItemPredicate() {
        this.tag = null;
        this.item = null;
        this.potion = null;
        this.count = MinMaxBounds.Ints.ANY;
        this.durability = MinMaxBounds.Ints.ANY;
        this.enchantments = EnchantmentPredicate.NONE;
        this.storedEnchantments = EnchantmentPredicate.NONE;
        this.nbt = NbtPredicate.ANY;
    }

    public ItemPredicate(@Nullable Tag<Item> tag, @Nullable Item item, MinMaxBounds.Ints ints, MinMaxBounds.Ints ints2, EnchantmentPredicate[] arrenchantmentPredicate, EnchantmentPredicate[] arrenchantmentPredicate2, @Nullable Potion potion, NbtPredicate nbtPredicate) {
        this.tag = tag;
        this.item = item;
        this.count = ints;
        this.durability = ints2;
        this.enchantments = arrenchantmentPredicate;
        this.storedEnchantments = arrenchantmentPredicate2;
        this.potion = potion;
        this.nbt = nbtPredicate;
    }

    public boolean matches(ItemStack itemStack) {
        Map<Enchantment, Integer> map;
        if (this == ANY) {
            return true;
        }
        if (this.tag != null && !this.tag.contains(itemStack.getItem())) {
            return false;
        }
        if (this.item != null && itemStack.getItem() != this.item) {
            return false;
        }
        if (!this.count.matches(itemStack.getCount())) {
            return false;
        }
        if (!this.durability.isAny() && !itemStack.isDamageableItem()) {
            return false;
        }
        if (!this.durability.matches(itemStack.getMaxDamage() - itemStack.getDamageValue())) {
            return false;
        }
        if (!this.nbt.matches(itemStack)) {
            return false;
        }
        if (this.enchantments.length > 0) {
            map = EnchantmentHelper.deserializeEnchantments(itemStack.getEnchantmentTags());
            for (EnchantmentPredicate enchantmentPredicate : this.enchantments) {
                if (enchantmentPredicate.containedIn(map)) continue;
                return false;
            }
        }
        if (this.storedEnchantments.length > 0) {
            map = EnchantmentHelper.deserializeEnchantments(EnchantedBookItem.getEnchantments(itemStack));
            for (EnchantmentPredicate enchantmentPredicate : this.storedEnchantments) {
                if (enchantmentPredicate.containedIn(map)) continue;
                return false;
            }
        }
        map = PotionUtils.getPotion(itemStack);
        return this.potion == null || this.potion == map;
    }

    public static ItemPredicate fromJson(@Nullable JsonElement jsonElement) {
        Tag<Item> tag;
        Object object;
        EnchantmentPredicate[] arrenchantmentPredicate;
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "item");
        MinMaxBounds.Ints ints = MinMaxBounds.Ints.fromJson(jsonObject.get("count"));
        MinMaxBounds.Ints ints2 = MinMaxBounds.Ints.fromJson(jsonObject.get("durability"));
        if (jsonObject.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        NbtPredicate nbtPredicate = NbtPredicate.fromJson(jsonObject.get("nbt"));
        Item item = null;
        if (jsonObject.has("item")) {
            tag = new ResourceLocation(GsonHelper.getAsString(jsonObject, "item"));
            item = Registry.ITEM.getOptional((ResourceLocation)((Object)tag)).orElseThrow(() -> ItemPredicate.lambda$fromJson$0((ResourceLocation)((Object)tag)));
        }
        tag = null;
        if (jsonObject.has("tag")) {
            object = new ResourceLocation(GsonHelper.getAsString(jsonObject, "tag"));
            tag = SerializationTags.getInstance().getItems().getTag((ResourceLocation)object);
            if (tag == null) {
                throw new JsonSyntaxException("Unknown item tag '" + object + "'");
            }
        }
        object = null;
        if (jsonObject.has("potion")) {
            arrenchantmentPredicate = new ResourceLocation(GsonHelper.getAsString(jsonObject, "potion"));
            object = Registry.POTION.getOptional((ResourceLocation)arrenchantmentPredicate).orElseThrow(() -> ItemPredicate.lambda$fromJson$1((ResourceLocation)arrenchantmentPredicate));
        }
        arrenchantmentPredicate = EnchantmentPredicate.fromJsonArray(jsonObject.get("enchantments"));
        EnchantmentPredicate[] arrenchantmentPredicate2 = EnchantmentPredicate.fromJsonArray(jsonObject.get("stored_enchantments"));
        return new ItemPredicate(tag, item, ints, ints2, arrenchantmentPredicate, arrenchantmentPredicate2, (Potion)object, nbtPredicate);
    }

    public JsonElement serializeToJson() {
        JsonArray jsonArray;
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        if (this.item != null) {
            jsonObject.addProperty("item", Registry.ITEM.getKey(this.item).toString());
        }
        if (this.tag != null) {
            jsonObject.addProperty("tag", SerializationTags.getInstance().getItems().getIdOrThrow(this.tag).toString());
        }
        jsonObject.add("count", this.count.serializeToJson());
        jsonObject.add("durability", this.durability.serializeToJson());
        jsonObject.add("nbt", this.nbt.serializeToJson());
        if (this.enchantments.length > 0) {
            jsonArray = new JsonArray();
            for (EnchantmentPredicate enchantmentPredicate : this.enchantments) {
                jsonArray.add(enchantmentPredicate.serializeToJson());
            }
            jsonObject.add("enchantments", (JsonElement)jsonArray);
        }
        if (this.storedEnchantments.length > 0) {
            jsonArray = new JsonArray();
            for (EnchantmentPredicate enchantmentPredicate : this.storedEnchantments) {
                jsonArray.add(enchantmentPredicate.serializeToJson());
            }
            jsonObject.add("stored_enchantments", (JsonElement)jsonArray);
        }
        if (this.potion != null) {
            jsonObject.addProperty("potion", Registry.POTION.getKey(this.potion).toString());
        }
        return jsonObject;
    }

    public static ItemPredicate[] fromJsonArray(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return new ItemPredicate[0];
        }
        JsonArray jsonArray = GsonHelper.convertToJsonArray(jsonElement, "items");
        ItemPredicate[] arritemPredicate = new ItemPredicate[jsonArray.size()];
        for (int i = 0; i < arritemPredicate.length; ++i) {
            arritemPredicate[i] = ItemPredicate.fromJson(jsonArray.get(i));
        }
        return arritemPredicate;
    }

    private static /* synthetic */ JsonSyntaxException lambda$fromJson$1(ResourceLocation resourceLocation) {
        return new JsonSyntaxException("Unknown potion '" + resourceLocation + "'");
    }

    private static /* synthetic */ JsonSyntaxException lambda$fromJson$0(ResourceLocation resourceLocation) {
        return new JsonSyntaxException("Unknown item id '" + resourceLocation + "'");
    }

    public static class Builder {
        private final List<EnchantmentPredicate> enchantments = Lists.newArrayList();
        private final List<EnchantmentPredicate> storedEnchantments = Lists.newArrayList();
        @Nullable
        private Item item;
        @Nullable
        private Tag<Item> tag;
        private MinMaxBounds.Ints count = MinMaxBounds.Ints.ANY;
        private MinMaxBounds.Ints durability = MinMaxBounds.Ints.ANY;
        @Nullable
        private Potion potion;
        private NbtPredicate nbt = NbtPredicate.ANY;

        private Builder() {
        }

        public static Builder item() {
            return new Builder();
        }

        public Builder of(ItemLike itemLike) {
            this.item = itemLike.asItem();
            return this;
        }

        public Builder of(Tag<Item> tag) {
            this.tag = tag;
            return this;
        }

        public Builder hasNbt(CompoundTag compoundTag) {
            this.nbt = new NbtPredicate(compoundTag);
            return this;
        }

        public Builder hasEnchantment(EnchantmentPredicate enchantmentPredicate) {
            this.enchantments.add(enchantmentPredicate);
            return this;
        }

        public ItemPredicate build() {
            return new ItemPredicate(this.tag, this.item, this.count, this.durability, this.enchantments.toArray(EnchantmentPredicate.NONE), this.storedEnchantments.toArray(EnchantmentPredicate.NONE), this.potion, this.nbt);
        }
    }

}

