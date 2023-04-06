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
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import net.minecraft.world.level.storage.loot.RandomIntGenerators;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EnchantWithLevelsFunction
extends LootItemConditionalFunction {
    private final RandomIntGenerator levels;
    private final boolean treasure;

    private EnchantWithLevelsFunction(LootItemCondition[] arrlootItemCondition, RandomIntGenerator randomIntGenerator, boolean bl) {
        super(arrlootItemCondition);
        this.levels = randomIntGenerator;
        this.treasure = bl;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.ENCHANT_WITH_LEVELS;
    }

    @Override
    public ItemStack run(ItemStack itemStack, LootContext lootContext) {
        Random random = lootContext.getRandom();
        return EnchantmentHelper.enchantItem(random, itemStack, this.levels.getInt(random), this.treasure);
    }

    public static Builder enchantWithLevels(RandomIntGenerator randomIntGenerator) {
        return new Builder(randomIntGenerator);
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<EnchantWithLevelsFunction> {
        @Override
        public void serialize(JsonObject jsonObject, EnchantWithLevelsFunction enchantWithLevelsFunction, JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, enchantWithLevelsFunction, jsonSerializationContext);
            jsonObject.add("levels", RandomIntGenerators.serialize(enchantWithLevelsFunction.levels, jsonSerializationContext));
            jsonObject.addProperty("treasure", Boolean.valueOf(enchantWithLevelsFunction.treasure));
        }

        @Override
        public EnchantWithLevelsFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            RandomIntGenerator randomIntGenerator = RandomIntGenerators.deserialize(jsonObject.get("levels"), jsonDeserializationContext);
            boolean bl = GsonHelper.getAsBoolean(jsonObject, "treasure", false);
            return new EnchantWithLevelsFunction(arrlootItemCondition, randomIntGenerator, bl);
        }

        @Override
        public /* synthetic */ LootItemConditionalFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            return this.deserialize(jsonObject, jsonDeserializationContext, arrlootItemCondition);
        }
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final RandomIntGenerator levels;
        private boolean treasure;

        public Builder(RandomIntGenerator randomIntGenerator) {
            this.levels = randomIntGenerator;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder allowTreasure() {
            this.treasure = true;
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new EnchantWithLevelsFunction(this.getConditions(), this.levels, this.treasure);
        }

        @Override
        protected /* synthetic */ LootItemConditionalFunction.Builder getThis() {
            return this.getThis();
        }
    }

}

