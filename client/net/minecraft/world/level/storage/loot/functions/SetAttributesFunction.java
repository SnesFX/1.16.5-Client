/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetAttributesFunction
extends LootItemConditionalFunction {
    private final List<Modifier> modifiers;

    private SetAttributesFunction(LootItemCondition[] arrlootItemCondition, List<Modifier> list) {
        super(arrlootItemCondition);
        this.modifiers = ImmutableList.copyOf(list);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_ATTRIBUTES;
    }

    @Override
    public ItemStack run(ItemStack itemStack, LootContext lootContext) {
        Random random = lootContext.getRandom();
        for (Modifier modifier : this.modifiers) {
            UUID uUID = modifier.id;
            if (uUID == null) {
                uUID = UUID.randomUUID();
            }
            EquipmentSlot equipmentSlot = Util.getRandom(modifier.slots, random);
            itemStack.addAttributeModifier(modifier.attribute, new AttributeModifier(uUID, modifier.name, (double)modifier.amount.getFloat(random), modifier.operation), equipmentSlot);
        }
        return itemStack;
    }

    static class Modifier {
        private final String name;
        private final Attribute attribute;
        private final AttributeModifier.Operation operation;
        private final RandomValueBounds amount;
        @Nullable
        private final UUID id;
        private final EquipmentSlot[] slots;

        private Modifier(String string, Attribute attribute, AttributeModifier.Operation operation, RandomValueBounds randomValueBounds, EquipmentSlot[] arrequipmentSlot, @Nullable UUID uUID) {
            this.name = string;
            this.attribute = attribute;
            this.operation = operation;
            this.amount = randomValueBounds;
            this.id = uUID;
            this.slots = arrequipmentSlot;
        }

        public JsonObject serialize(JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", this.name);
            jsonObject.addProperty("attribute", Registry.ATTRIBUTE.getKey(this.attribute).toString());
            jsonObject.addProperty("operation", Modifier.operationToString(this.operation));
            jsonObject.add("amount", jsonSerializationContext.serialize((Object)this.amount));
            if (this.id != null) {
                jsonObject.addProperty("id", this.id.toString());
            }
            if (this.slots.length == 1) {
                jsonObject.addProperty("slot", this.slots[0].getName());
            } else {
                JsonArray jsonArray = new JsonArray();
                for (EquipmentSlot equipmentSlot : this.slots) {
                    jsonArray.add((JsonElement)new JsonPrimitive(equipmentSlot.getName()));
                }
                jsonObject.add("slot", (JsonElement)jsonArray);
            }
            return jsonObject;
        }

        public static Modifier deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            Object object;
            EquipmentSlot[] arrequipmentSlot;
            String string = GsonHelper.getAsString(jsonObject, "name");
            ResourceLocation resourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "attribute"));
            Attribute attribute = Registry.ATTRIBUTE.get(resourceLocation);
            if (attribute == null) {
                throw new JsonSyntaxException("Unknown attribute: " + resourceLocation);
            }
            AttributeModifier.Operation operation = Modifier.operationFromString(GsonHelper.getAsString(jsonObject, "operation"));
            RandomValueBounds randomValueBounds = GsonHelper.getAsObject(jsonObject, "amount", jsonDeserializationContext, RandomValueBounds.class);
            UUID uUID = null;
            if (GsonHelper.isStringValue(jsonObject, "slot")) {
                arrequipmentSlot = new EquipmentSlot[]{EquipmentSlot.byName(GsonHelper.getAsString(jsonObject, "slot"))};
            } else if (GsonHelper.isArrayNode(jsonObject, "slot")) {
                object = GsonHelper.getAsJsonArray(jsonObject, "slot");
                arrequipmentSlot = new EquipmentSlot[object.size()];
                int n = 0;
                for (JsonElement jsonElement : object) {
                    arrequipmentSlot[n++] = EquipmentSlot.byName(GsonHelper.convertToString(jsonElement, "slot"));
                }
                if (arrequipmentSlot.length == 0) {
                    throw new JsonSyntaxException("Invalid attribute modifier slot; must contain at least one entry.");
                }
            } else {
                throw new JsonSyntaxException("Invalid or missing attribute modifier slot; must be either string or array of strings.");
            }
            if (jsonObject.has("id")) {
                object = GsonHelper.getAsString(jsonObject, "id");
                try {
                    uUID = UUID.fromString((String)object);
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    throw new JsonSyntaxException("Invalid attribute modifier id '" + (String)object + "' (must be UUID format, with dashes)");
                }
            }
            return new Modifier(string, attribute, operation, randomValueBounds, arrequipmentSlot, uUID);
        }

        private static String operationToString(AttributeModifier.Operation operation) {
            switch (operation) {
                case ADDITION: {
                    return "addition";
                }
                case MULTIPLY_BASE: {
                    return "multiply_base";
                }
                case MULTIPLY_TOTAL: {
                    return "multiply_total";
                }
            }
            throw new IllegalArgumentException("Unknown operation " + (Object)((Object)operation));
        }

        private static AttributeModifier.Operation operationFromString(String string) {
            switch (string) {
                case "addition": {
                    return AttributeModifier.Operation.ADDITION;
                }
                case "multiply_base": {
                    return AttributeModifier.Operation.MULTIPLY_BASE;
                }
                case "multiply_total": {
                    return AttributeModifier.Operation.MULTIPLY_TOTAL;
                }
            }
            throw new JsonSyntaxException("Unknown attribute modifier operation " + string);
        }
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<SetAttributesFunction> {
        @Override
        public void serialize(JsonObject jsonObject, SetAttributesFunction setAttributesFunction, JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, setAttributesFunction, jsonSerializationContext);
            JsonArray jsonArray = new JsonArray();
            for (Modifier modifier : setAttributesFunction.modifiers) {
                jsonArray.add((JsonElement)modifier.serialize(jsonSerializationContext));
            }
            jsonObject.add("modifiers", (JsonElement)jsonArray);
        }

        @Override
        public SetAttributesFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "modifiers");
            ArrayList arrayList = Lists.newArrayListWithExpectedSize((int)jsonArray.size());
            for (JsonElement jsonElement : jsonArray) {
                arrayList.add(Modifier.deserialize(GsonHelper.convertToJsonObject(jsonElement, "modifier"), jsonDeserializationContext));
            }
            if (arrayList.isEmpty()) {
                throw new JsonSyntaxException("Invalid attribute modifiers array; cannot be empty");
            }
            return new SetAttributesFunction(arrlootItemCondition, arrayList);
        }

        @Override
        public /* synthetic */ LootItemConditionalFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            return this.deserialize(jsonObject, jsonDeserializationContext, arrlootItemCondition);
        }
    }

}

