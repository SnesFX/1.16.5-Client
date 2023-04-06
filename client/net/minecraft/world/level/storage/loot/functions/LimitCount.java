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
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.IntLimiter;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LimitCount
extends LootItemConditionalFunction {
    private final IntLimiter limiter;

    private LimitCount(LootItemCondition[] arrlootItemCondition, IntLimiter intLimiter) {
        super(arrlootItemCondition);
        this.limiter = intLimiter;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.LIMIT_COUNT;
    }

    @Override
    public ItemStack run(ItemStack itemStack, LootContext lootContext) {
        int n = this.limiter.applyAsInt(itemStack.getCount());
        itemStack.setCount(n);
        return itemStack;
    }

    public static LootItemConditionalFunction.Builder<?> limitCount(IntLimiter intLimiter) {
        return LimitCount.simpleBuilder(arrlootItemCondition -> new LimitCount((LootItemCondition[])arrlootItemCondition, intLimiter));
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<LimitCount> {
        @Override
        public void serialize(JsonObject jsonObject, LimitCount limitCount, JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, limitCount, jsonSerializationContext);
            jsonObject.add("limit", jsonSerializationContext.serialize((Object)limitCount.limiter));
        }

        @Override
        public LimitCount deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            IntLimiter intLimiter = GsonHelper.getAsObject(jsonObject, "limit", jsonDeserializationContext, IntLimiter.class);
            return new LimitCount(arrlootItemCondition, intLimiter);
        }

        @Override
        public /* synthetic */ LootItemConditionalFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            return this.deserialize(jsonObject, jsonDeserializationContext, arrlootItemCondition);
        }
    }

}

