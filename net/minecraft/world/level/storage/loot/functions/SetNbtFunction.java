/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetNbtFunction
extends LootItemConditionalFunction {
    private final CompoundTag tag;

    private SetNbtFunction(LootItemCondition[] arrlootItemCondition, CompoundTag compoundTag) {
        super(arrlootItemCondition);
        this.tag = compoundTag;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.SET_NBT;
    }

    @Override
    public ItemStack run(ItemStack itemStack, LootContext lootContext) {
        itemStack.getOrCreateTag().merge(this.tag);
        return itemStack;
    }

    public static LootItemConditionalFunction.Builder<?> setTag(CompoundTag compoundTag) {
        return SetNbtFunction.simpleBuilder(arrlootItemCondition -> new SetNbtFunction((LootItemCondition[])arrlootItemCondition, compoundTag));
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<SetNbtFunction> {
        @Override
        public void serialize(JsonObject jsonObject, SetNbtFunction setNbtFunction, JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, setNbtFunction, jsonSerializationContext);
            jsonObject.addProperty("tag", setNbtFunction.tag.toString());
        }

        @Override
        public SetNbtFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            try {
                CompoundTag compoundTag = TagParser.parseTag(GsonHelper.getAsString(jsonObject, "tag"));
                return new SetNbtFunction(arrlootItemCondition, compoundTag);
            }
            catch (CommandSyntaxException commandSyntaxException) {
                throw new JsonSyntaxException(commandSyntaxException.getMessage());
            }
        }

        @Override
        public /* synthetic */ LootItemConditionalFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            return this.deserialize(jsonObject, jsonDeserializationContext, arrlootItemCondition);
        }
    }

}

