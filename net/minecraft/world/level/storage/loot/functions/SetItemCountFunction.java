/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import net.minecraft.world.level.storage.loot.RandomIntGenerators;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetItemCountFunction
extends LootItemConditionalFunction {
    private final RandomIntGenerator value;

    private SetItemCountFunction(LootItemCondition[] arrlootItemCondition, RandomIntGenerator randomIntGenerator) {
        super(arrlootItemCondition);
        this.value = randomIntGenerator;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_COUNT;
    }

    @Override
    public ItemStack run(ItemStack itemStack, LootContext lootContext) {
        itemStack.setCount(this.value.getInt(lootContext.getRandom()));
        return itemStack;
    }

    public static LootItemConditionalFunction.Builder<?> setCount(RandomIntGenerator randomIntGenerator) {
        return SetItemCountFunction.simpleBuilder(arrlootItemCondition -> new SetItemCountFunction((LootItemCondition[])arrlootItemCondition, randomIntGenerator));
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<SetItemCountFunction> {
        @Override
        public void serialize(JsonObject jsonObject, SetItemCountFunction setItemCountFunction, JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, setItemCountFunction, jsonSerializationContext);
            jsonObject.add("count", RandomIntGenerators.serialize(setItemCountFunction.value, jsonSerializationContext));
        }

        @Override
        public SetItemCountFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            RandomIntGenerator randomIntGenerator = RandomIntGenerators.deserialize(jsonObject.get("count"), jsonDeserializationContext);
            return new SetItemCountFunction(arrlootItemCondition, randomIntGenerator);
        }

        @Override
        public /* synthetic */ LootItemConditionalFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            return this.deserialize(jsonObject, jsonDeserializationContext, arrlootItemCondition);
        }
    }

}

