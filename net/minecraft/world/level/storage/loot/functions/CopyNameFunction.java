/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyNameFunction
extends LootItemConditionalFunction {
    private final NameSource source;

    private CopyNameFunction(LootItemCondition[] arrlootItemCondition, NameSource nameSource) {
        super(arrlootItemCondition);
        this.source = nameSource;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.COPY_NAME;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(this.source.param);
    }

    @Override
    public ItemStack run(ItemStack itemStack, LootContext lootContext) {
        Nameable nameable;
        Object obj = lootContext.getParamOrNull(this.source.param);
        if (obj instanceof Nameable && (nameable = (Nameable)obj).hasCustomName()) {
            itemStack.setHoverName(nameable.getDisplayName());
        }
        return itemStack;
    }

    public static LootItemConditionalFunction.Builder<?> copyName(NameSource nameSource) {
        return CopyNameFunction.simpleBuilder(arrlootItemCondition -> new CopyNameFunction((LootItemCondition[])arrlootItemCondition, nameSource));
    }

    static /* synthetic */ NameSource access$000(CopyNameFunction copyNameFunction) {
        return copyNameFunction.source;
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<CopyNameFunction> {
        @Override
        public void serialize(JsonObject jsonObject, CopyNameFunction copyNameFunction, JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, copyNameFunction, jsonSerializationContext);
            jsonObject.addProperty("source", CopyNameFunction.access$000((CopyNameFunction)copyNameFunction).name);
        }

        @Override
        public CopyNameFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            NameSource nameSource = NameSource.getByName(GsonHelper.getAsString(jsonObject, "source"));
            return new CopyNameFunction(arrlootItemCondition, nameSource);
        }

        @Override
        public /* synthetic */ LootItemConditionalFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            return this.deserialize(jsonObject, jsonDeserializationContext, arrlootItemCondition);
        }
    }

    public static enum NameSource {
        THIS("this", LootContextParams.THIS_ENTITY),
        KILLER("killer", LootContextParams.KILLER_ENTITY),
        KILLER_PLAYER("killer_player", LootContextParams.LAST_DAMAGE_PLAYER),
        BLOCK_ENTITY("block_entity", LootContextParams.BLOCK_ENTITY);
        
        public final String name;
        public final LootContextParam<?> param;

        private NameSource(String string2, LootContextParam<?> lootContextParam) {
            this.name = string2;
            this.param = lootContextParam;
        }

        public static NameSource getByName(String string) {
            for (NameSource nameSource : NameSource.values()) {
                if (!nameSource.name.equals(string)) continue;
                return nameSource;
            }
            throw new IllegalArgumentException("Invalid name source " + string);
        }
    }

}

