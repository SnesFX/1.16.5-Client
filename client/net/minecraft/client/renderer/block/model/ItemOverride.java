/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemOverride {
    private final ResourceLocation model;
    private final Map<ResourceLocation, Float> predicates;

    public ItemOverride(ResourceLocation resourceLocation, Map<ResourceLocation, Float> map) {
        this.model = resourceLocation;
        this.predicates = map;
    }

    public ResourceLocation getModel() {
        return this.model;
    }

    boolean test(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
        Item item = itemStack.getItem();
        for (Map.Entry<ResourceLocation, Float> entry : this.predicates.entrySet()) {
            ItemPropertyFunction itemPropertyFunction = ItemProperties.getProperty(item, entry.getKey());
            if (itemPropertyFunction != null && !(itemPropertyFunction.call(itemStack, clientLevel, livingEntity) < entry.getValue().floatValue())) continue;
            return false;
        }
        return true;
    }

    public static class Deserializer
    implements JsonDeserializer<ItemOverride> {
        protected Deserializer() {
        }

        public ItemOverride deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            ResourceLocation resourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "model"));
            Map<ResourceLocation, Float> map = this.getPredicates(jsonObject);
            return new ItemOverride(resourceLocation, map);
        }

        protected Map<ResourceLocation, Float> getPredicates(JsonObject jsonObject) {
            LinkedHashMap linkedHashMap = Maps.newLinkedHashMap();
            JsonObject jsonObject2 = GsonHelper.getAsJsonObject(jsonObject, "predicate");
            for (Map.Entry entry : jsonObject2.entrySet()) {
                linkedHashMap.put(new ResourceLocation((String)entry.getKey()), Float.valueOf(GsonHelper.convertToFloat((JsonElement)entry.getValue(), (String)entry.getKey())));
            }
            return linkedHashMap;
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

}

