/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.authlib.GameProfile;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class FillPlayerHead
extends LootItemConditionalFunction {
    private final LootContext.EntityTarget entityTarget;

    public FillPlayerHead(LootItemCondition[] arrlootItemCondition, LootContext.EntityTarget entityTarget) {
        super(arrlootItemCondition);
        this.entityTarget = entityTarget;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.FILL_PLAYER_HEAD;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(this.entityTarget.getParam());
    }

    @Override
    public ItemStack run(ItemStack itemStack, LootContext lootContext) {
        Entity entity;
        if (itemStack.getItem() == Items.PLAYER_HEAD && (entity = lootContext.getParamOrNull(this.entityTarget.getParam())) instanceof Player) {
            GameProfile gameProfile = ((Player)entity).getGameProfile();
            itemStack.getOrCreateTag().put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), gameProfile));
        }
        return itemStack;
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<FillPlayerHead> {
        @Override
        public void serialize(JsonObject jsonObject, FillPlayerHead fillPlayerHead, JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, fillPlayerHead, jsonSerializationContext);
            jsonObject.add("entity", jsonSerializationContext.serialize((Object)fillPlayerHead.entityTarget));
        }

        @Override
        public FillPlayerHead deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            LootContext.EntityTarget entityTarget = GsonHelper.getAsObject(jsonObject, "entity", jsonDeserializationContext, LootContext.EntityTarget.class);
            return new FillPlayerHead(arrlootItemCondition, entityTarget);
        }

        @Override
        public /* synthetic */ LootItemConditionalFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            return this.deserialize(jsonObject, jsonDeserializationContext, arrlootItemCondition);
        }
    }

}

