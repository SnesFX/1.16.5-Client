/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetItemDamageFunction
extends LootItemConditionalFunction {
    private static final Logger LOGGER = LogManager.getLogger();
    private final RandomValueBounds damage;

    private SetItemDamageFunction(LootItemCondition[] arrlootItemCondition, RandomValueBounds randomValueBounds) {
        super(arrlootItemCondition);
        this.damage = randomValueBounds;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_DAMAGE;
    }

    @Override
    public ItemStack run(ItemStack itemStack, LootContext lootContext) {
        if (itemStack.isDamageableItem()) {
            float f = 1.0f - this.damage.getFloat(lootContext.getRandom());
            itemStack.setDamageValue(Mth.floor(f * (float)itemStack.getMaxDamage()));
        } else {
            LOGGER.warn("Couldn't set damage of loot item {}", (Object)itemStack);
        }
        return itemStack;
    }

    public static LootItemConditionalFunction.Builder<?> setDamage(RandomValueBounds randomValueBounds) {
        return SetItemDamageFunction.simpleBuilder(arrlootItemCondition -> new SetItemDamageFunction((LootItemCondition[])arrlootItemCondition, randomValueBounds));
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<SetItemDamageFunction> {
        @Override
        public void serialize(JsonObject jsonObject, SetItemDamageFunction setItemDamageFunction, JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, setItemDamageFunction, jsonSerializationContext);
            jsonObject.add("damage", jsonSerializationContext.serialize((Object)setItemDamageFunction.damage));
        }

        @Override
        public SetItemDamageFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            return new SetItemDamageFunction(arrlootItemCondition, GsonHelper.getAsObject(jsonObject, "damage", jsonDeserializationContext, RandomValueBounds.class));
        }

        @Override
        public /* synthetic */ LootItemConditionalFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            return this.deserialize(jsonObject, jsonDeserializationContext, arrlootItemCondition);
        }
    }

}

