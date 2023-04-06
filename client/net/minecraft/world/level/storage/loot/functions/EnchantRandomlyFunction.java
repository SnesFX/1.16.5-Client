/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnchantRandomlyFunction
extends LootItemConditionalFunction {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<Enchantment> enchantments;

    private EnchantRandomlyFunction(LootItemCondition[] arrlootItemCondition, Collection<Enchantment> collection) {
        super(arrlootItemCondition);
        this.enchantments = ImmutableList.copyOf(collection);
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.ENCHANT_RANDOMLY;
    }

    @Override
    public ItemStack run(ItemStack itemStack, LootContext lootContext) {
        Enchantment enchantment2;
        Random random = lootContext.getRandom();
        if (this.enchantments.isEmpty()) {
            boolean bl = itemStack.getItem() == Items.BOOK;
            List list = Registry.ENCHANTMENT.stream().filter(Enchantment::isDiscoverable).filter(enchantment -> bl || enchantment.canEnchant(itemStack)).collect(Collectors.toList());
            if (list.isEmpty()) {
                LOGGER.warn("Couldn't find a compatible enchantment for {}", (Object)itemStack);
                return itemStack;
            }
            enchantment2 = (Enchantment)list.get(random.nextInt(list.size()));
        } else {
            enchantment2 = this.enchantments.get(random.nextInt(this.enchantments.size()));
        }
        return EnchantRandomlyFunction.enchantItem(itemStack, enchantment2, random);
    }

    private static ItemStack enchantItem(ItemStack itemStack, Enchantment enchantment, Random random) {
        int n = Mth.nextInt(random, enchantment.getMinLevel(), enchantment.getMaxLevel());
        if (itemStack.getItem() == Items.BOOK) {
            itemStack = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantedBookItem.addEnchantment(itemStack, new EnchantmentInstance(enchantment, n));
        } else {
            itemStack.enchant(enchantment, n);
        }
        return itemStack;
    }

    public static LootItemConditionalFunction.Builder<?> randomApplicableEnchantment() {
        return EnchantRandomlyFunction.simpleBuilder(arrlootItemCondition -> new EnchantRandomlyFunction((LootItemCondition[])arrlootItemCondition, (Collection<Enchantment>)ImmutableList.of()));
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<EnchantRandomlyFunction> {
        @Override
        public void serialize(JsonObject jsonObject, EnchantRandomlyFunction enchantRandomlyFunction, JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, enchantRandomlyFunction, jsonSerializationContext);
            if (!enchantRandomlyFunction.enchantments.isEmpty()) {
                JsonArray jsonArray = new JsonArray();
                for (Enchantment enchantment : enchantRandomlyFunction.enchantments) {
                    ResourceLocation resourceLocation = Registry.ENCHANTMENT.getKey(enchantment);
                    if (resourceLocation == null) {
                        throw new IllegalArgumentException("Don't know how to serialize enchantment " + enchantment);
                    }
                    jsonArray.add((JsonElement)new JsonPrimitive(resourceLocation.toString()));
                }
                jsonObject.add("enchantments", (JsonElement)jsonArray);
            }
        }

        @Override
        public EnchantRandomlyFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            ArrayList arrayList = Lists.newArrayList();
            if (jsonObject.has("enchantments")) {
                JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "enchantments");
                for (JsonElement jsonElement : jsonArray) {
                    String string = GsonHelper.convertToString(jsonElement, "enchantment");
                    Enchantment enchantment = Registry.ENCHANTMENT.getOptional(new ResourceLocation(string)).orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + string + "'"));
                    arrayList.add(enchantment);
                }
            }
            return new EnchantRandomlyFunction(arrlootItemCondition, arrayList);
        }

        @Override
        public /* synthetic */ LootItemConditionalFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            return this.deserialize(jsonObject, jsonDeserializationContext, arrlootItemCondition);
        }
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final Set<Enchantment> enchantments = Sets.newHashSet();

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder withEnchantment(Enchantment enchantment) {
            this.enchantments.add(enchantment);
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new EnchantRandomlyFunction(this.getConditions(), this.enchantments);
        }

        @Override
        protected /* synthetic */ LootItemConditionalFunction.Builder getThis() {
            return this.getThis();
        }
    }

}

