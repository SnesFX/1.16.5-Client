/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ApplyBonusCount
extends LootItemConditionalFunction {
    private static final Map<ResourceLocation, FormulaDeserializer> FORMULAS = Maps.newHashMap();
    private final Enchantment enchantment;
    private final Formula formula;

    private ApplyBonusCount(LootItemCondition[] arrlootItemCondition, Enchantment enchantment, Formula formula) {
        super(arrlootItemCondition);
        this.enchantment = enchantment;
        this.formula = formula;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.APPLY_BONUS;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.TOOL);
    }

    @Override
    public ItemStack run(ItemStack itemStack, LootContext lootContext) {
        ItemStack itemStack2 = lootContext.getParamOrNull(LootContextParams.TOOL);
        if (itemStack2 != null) {
            int n = EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, itemStack2);
            int n2 = this.formula.calculateNewCount(lootContext.getRandom(), itemStack.getCount(), n);
            itemStack.setCount(n2);
        }
        return itemStack;
    }

    public static LootItemConditionalFunction.Builder<?> addBonusBinomialDistributionCount(Enchantment enchantment, float f, int n) {
        return ApplyBonusCount.simpleBuilder(arrlootItemCondition -> new ApplyBonusCount((LootItemCondition[])arrlootItemCondition, enchantment, new BinomialWithBonusCount(n, f)));
    }

    public static LootItemConditionalFunction.Builder<?> addOreBonusCount(Enchantment enchantment) {
        return ApplyBonusCount.simpleBuilder(arrlootItemCondition -> new ApplyBonusCount((LootItemCondition[])arrlootItemCondition, enchantment, new OreDrops()));
    }

    public static LootItemConditionalFunction.Builder<?> addUniformBonusCount(Enchantment enchantment) {
        return ApplyBonusCount.simpleBuilder(arrlootItemCondition -> new ApplyBonusCount((LootItemCondition[])arrlootItemCondition, enchantment, new UniformBonusCount(1)));
    }

    public static LootItemConditionalFunction.Builder<?> addUniformBonusCount(Enchantment enchantment, int n) {
        return ApplyBonusCount.simpleBuilder(arrlootItemCondition -> new ApplyBonusCount((LootItemCondition[])arrlootItemCondition, enchantment, new UniformBonusCount(n)));
    }

    static {
        FORMULAS.put(BinomialWithBonusCount.TYPE, (arg_0, arg_1) -> BinomialWithBonusCount.deserialize(arg_0, arg_1));
        FORMULAS.put(OreDrops.TYPE, (arg_0, arg_1) -> OreDrops.deserialize(arg_0, arg_1));
        FORMULAS.put(UniformBonusCount.TYPE, (arg_0, arg_1) -> UniformBonusCount.deserialize(arg_0, arg_1));
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<ApplyBonusCount> {
        @Override
        public void serialize(JsonObject jsonObject, ApplyBonusCount applyBonusCount, JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, applyBonusCount, jsonSerializationContext);
            jsonObject.addProperty("enchantment", Registry.ENCHANTMENT.getKey(applyBonusCount.enchantment).toString());
            jsonObject.addProperty("formula", applyBonusCount.formula.getType().toString());
            JsonObject jsonObject2 = new JsonObject();
            applyBonusCount.formula.serializeParams(jsonObject2, jsonSerializationContext);
            if (jsonObject2.size() > 0) {
                jsonObject.add("parameters", (JsonElement)jsonObject2);
            }
        }

        @Override
        public ApplyBonusCount deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            ResourceLocation resourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "enchantment"));
            Enchantment enchantment = Registry.ENCHANTMENT.getOptional(resourceLocation).orElseThrow(() -> new JsonParseException("Invalid enchantment id: " + resourceLocation));
            ResourceLocation resourceLocation2 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "formula"));
            FormulaDeserializer formulaDeserializer = (FormulaDeserializer)FORMULAS.get(resourceLocation2);
            if (formulaDeserializer == null) {
                throw new JsonParseException("Invalid formula id: " + resourceLocation2);
            }
            Formula formula = jsonObject.has("parameters") ? formulaDeserializer.deserialize(GsonHelper.getAsJsonObject(jsonObject, "parameters"), jsonDeserializationContext) : formulaDeserializer.deserialize(new JsonObject(), jsonDeserializationContext);
            return new ApplyBonusCount(arrlootItemCondition, enchantment, formula);
        }

        @Override
        public /* synthetic */ LootItemConditionalFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            return this.deserialize(jsonObject, jsonDeserializationContext, arrlootItemCondition);
        }
    }

    static final class OreDrops
    implements Formula {
        public static final ResourceLocation TYPE = new ResourceLocation("ore_drops");

        private OreDrops() {
        }

        @Override
        public int calculateNewCount(Random random, int n, int n2) {
            if (n2 > 0) {
                int n3 = random.nextInt(n2 + 2) - 1;
                if (n3 < 0) {
                    n3 = 0;
                }
                return n * (n3 + 1);
            }
            return n;
        }

        @Override
        public void serializeParams(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
        }

        public static Formula deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return new OreDrops();
        }

        @Override
        public ResourceLocation getType() {
            return TYPE;
        }
    }

    static final class UniformBonusCount
    implements Formula {
        public static final ResourceLocation TYPE = new ResourceLocation("uniform_bonus_count");
        private final int bonusMultiplier;

        public UniformBonusCount(int n) {
            this.bonusMultiplier = n;
        }

        @Override
        public int calculateNewCount(Random random, int n, int n2) {
            return n + random.nextInt(this.bonusMultiplier * n2 + 1);
        }

        @Override
        public void serializeParams(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("bonusMultiplier", (Number)this.bonusMultiplier);
        }

        public static Formula deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            int n = GsonHelper.getAsInt(jsonObject, "bonusMultiplier");
            return new UniformBonusCount(n);
        }

        @Override
        public ResourceLocation getType() {
            return TYPE;
        }
    }

    static final class BinomialWithBonusCount
    implements Formula {
        public static final ResourceLocation TYPE = new ResourceLocation("binomial_with_bonus_count");
        private final int extraRounds;
        private final float probability;

        public BinomialWithBonusCount(int n, float f) {
            this.extraRounds = n;
            this.probability = f;
        }

        @Override
        public int calculateNewCount(Random random, int n, int n2) {
            for (int i = 0; i < n2 + this.extraRounds; ++i) {
                if (!(random.nextFloat() < this.probability)) continue;
                ++n;
            }
            return n;
        }

        @Override
        public void serializeParams(JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("extra", (Number)this.extraRounds);
            jsonObject.addProperty("probability", (Number)Float.valueOf(this.probability));
        }

        public static Formula deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            int n = GsonHelper.getAsInt(jsonObject, "extra");
            float f = GsonHelper.getAsFloat(jsonObject, "probability");
            return new BinomialWithBonusCount(n, f);
        }

        @Override
        public ResourceLocation getType() {
            return TYPE;
        }
    }

    static interface FormulaDeserializer {
        public Formula deserialize(JsonObject var1, JsonDeserializationContext var2);
    }

    static interface Formula {
        public int calculateNewCount(Random var1, int var2, int var3);

        public void serializeParams(JsonObject var1, JsonSerializationContext var2);

        public ResourceLocation getType();
    }

}

