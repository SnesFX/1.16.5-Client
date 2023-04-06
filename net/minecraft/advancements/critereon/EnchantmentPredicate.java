/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentPredicate {
    public static final EnchantmentPredicate ANY = new EnchantmentPredicate();
    public static final EnchantmentPredicate[] NONE = new EnchantmentPredicate[0];
    private final Enchantment enchantment;
    private final MinMaxBounds.Ints level;

    public EnchantmentPredicate() {
        this.enchantment = null;
        this.level = MinMaxBounds.Ints.ANY;
    }

    public EnchantmentPredicate(@Nullable Enchantment enchantment, MinMaxBounds.Ints ints) {
        this.enchantment = enchantment;
        this.level = ints;
    }

    public boolean containedIn(Map<Enchantment, Integer> map) {
        if (this.enchantment != null) {
            if (!map.containsKey(this.enchantment)) {
                return false;
            }
            int n = map.get(this.enchantment);
            if (this.level != null && !this.level.matches(n)) {
                return false;
            }
        } else if (this.level != null) {
            for (Integer n : map.values()) {
                if (!this.level.matches(n)) continue;
                return true;
            }
            return false;
        }
        return true;
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        if (this.enchantment != null) {
            jsonObject.addProperty("enchantment", Registry.ENCHANTMENT.getKey(this.enchantment).toString());
        }
        jsonObject.add("levels", this.level.serializeToJson());
        return jsonObject;
    }

    public static EnchantmentPredicate fromJson(@Nullable JsonElement jsonElement) {
        Object object;
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "enchantment");
        Enchantment enchantment = null;
        if (jsonObject.has("enchantment")) {
            object = new ResourceLocation(GsonHelper.getAsString(jsonObject, "enchantment"));
            enchantment = Registry.ENCHANTMENT.getOptional((ResourceLocation)object).orElseThrow(() -> EnchantmentPredicate.lambda$fromJson$0((ResourceLocation)object));
        }
        object = MinMaxBounds.Ints.fromJson(jsonObject.get("levels"));
        return new EnchantmentPredicate(enchantment, (MinMaxBounds.Ints)object);
    }

    public static EnchantmentPredicate[] fromJsonArray(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return NONE;
        }
        JsonArray jsonArray = GsonHelper.convertToJsonArray(jsonElement, "enchantments");
        EnchantmentPredicate[] arrenchantmentPredicate = new EnchantmentPredicate[jsonArray.size()];
        for (int i = 0; i < arrenchantmentPredicate.length; ++i) {
            arrenchantmentPredicate[i] = EnchantmentPredicate.fromJson(jsonArray.get(i));
        }
        return arrenchantmentPredicate;
    }

    private static /* synthetic */ JsonSyntaxException lambda$fromJson$0(ResourceLocation resourceLocation) {
        return new JsonSyntaxException("Unknown enchantment '" + resourceLocation + "'");
    }
}

