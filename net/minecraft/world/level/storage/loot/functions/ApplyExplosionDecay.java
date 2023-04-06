/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Random;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ApplyExplosionDecay
extends LootItemConditionalFunction {
    private ApplyExplosionDecay(LootItemCondition[] arrlootItemCondition) {
        super(arrlootItemCondition);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.EXPLOSION_DECAY;
    }

    @Override
    public ItemStack run(ItemStack itemStack, LootContext lootContext) {
        Float f = lootContext.getParamOrNull(LootContextParams.EXPLOSION_RADIUS);
        if (f != null) {
            Random random = lootContext.getRandom();
            float f2 = 1.0f / f.floatValue();
            int n = itemStack.getCount();
            int n2 = 0;
            for (int i = 0; i < n; ++i) {
                if (!(random.nextFloat() <= f2)) continue;
                ++n2;
            }
            itemStack.setCount(n2);
        }
        return itemStack;
    }

    public static LootItemConditionalFunction.Builder<?> explosionDecay() {
        return ApplyExplosionDecay.simpleBuilder(ApplyExplosionDecay::new);
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<ApplyExplosionDecay> {
        @Override
        public ApplyExplosionDecay deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            return new ApplyExplosionDecay(arrlootItemCondition);
        }

        @Override
        public /* synthetic */ LootItemConditionalFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            return this.deserialize(jsonObject, jsonDeserializationContext, arrlootItemCondition);
        }
    }

}

