/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SmeltItemFunction
extends LootItemConditionalFunction {
    private static final Logger LOGGER = LogManager.getLogger();

    private SmeltItemFunction(LootItemCondition[] arrlootItemCondition) {
        super(arrlootItemCondition);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.FURNACE_SMELT;
    }

    @Override
    public ItemStack run(ItemStack itemStack, LootContext lootContext) {
        ItemStack itemStack2;
        if (itemStack.isEmpty()) {
            return itemStack;
        }
        Optional<SmeltingRecipe> optional = lootContext.getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(itemStack), lootContext.getLevel());
        if (optional.isPresent() && !(itemStack2 = optional.get().getResultItem()).isEmpty()) {
            ItemStack itemStack3 = itemStack2.copy();
            itemStack3.setCount(itemStack.getCount());
            return itemStack3;
        }
        LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", (Object)itemStack);
        return itemStack;
    }

    public static LootItemConditionalFunction.Builder<?> smelted() {
        return SmeltItemFunction.simpleBuilder(SmeltItemFunction::new);
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<SmeltItemFunction> {
        @Override
        public SmeltItemFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            return new SmeltItemFunction(arrlootItemCondition);
        }

        @Override
        public /* synthetic */ LootItemConditionalFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            return this.deserialize(jsonObject, jsonDeserializationContext, arrlootItemCondition);
        }
    }

}

