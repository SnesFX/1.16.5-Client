/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyBlockState
extends LootItemConditionalFunction {
    private final Block block;
    private final Set<Property<?>> properties;

    private CopyBlockState(LootItemCondition[] arrlootItemCondition, Block block, Set<Property<?>> set) {
        super(arrlootItemCondition);
        this.block = block;
        this.properties = set;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.COPY_STATE;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.BLOCK_STATE);
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext lootContext) {
        BlockState blockState = lootContext.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (blockState != null) {
            CompoundTag compoundTag;
            CompoundTag compoundTag2 = itemStack.getOrCreateTag();
            if (compoundTag2.contains("BlockStateTag", 10)) {
                compoundTag = compoundTag2.getCompound("BlockStateTag");
            } else {
                compoundTag = new CompoundTag();
                compoundTag2.put("BlockStateTag", compoundTag);
            }
            this.properties.stream().filter(blockState::hasProperty).forEach(property -> compoundTag.putString(property.getName(), CopyBlockState.serialize(blockState, property)));
        }
        return itemStack;
    }

    public static Builder copyState(Block block) {
        return new Builder(block);
    }

    private static <T extends Comparable<T>> String serialize(BlockState blockState, Property<T> property) {
        T t = blockState.getValue(property);
        return property.getName(t);
    }

    public static class Serializer
    extends LootItemConditionalFunction.Serializer<CopyBlockState> {
        @Override
        public void serialize(JsonObject jsonObject, CopyBlockState copyBlockState, JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, copyBlockState, jsonSerializationContext);
            jsonObject.addProperty("block", Registry.BLOCK.getKey(copyBlockState.block).toString());
            JsonArray jsonArray = new JsonArray();
            copyBlockState.properties.forEach(property -> jsonArray.add(property.getName()));
            jsonObject.add("properties", (JsonElement)jsonArray);
        }

        @Override
        public CopyBlockState deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            ResourceLocation resourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "block"));
            Block block = Registry.BLOCK.getOptional(resourceLocation).orElseThrow(() -> new IllegalArgumentException("Can't find block " + resourceLocation));
            StateDefinition<Block, BlockState> stateDefinition = block.getStateDefinition();
            HashSet hashSet = Sets.newHashSet();
            JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "properties", null);
            if (jsonArray != null) {
                jsonArray.forEach(jsonElement -> hashSet.add(stateDefinition.getProperty(GsonHelper.convertToString(jsonElement, "property"))));
            }
            return new CopyBlockState(arrlootItemCondition, block, hashSet);
        }

        @Override
        public /* synthetic */ LootItemConditionalFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] arrlootItemCondition) {
            return this.deserialize(jsonObject, jsonDeserializationContext, arrlootItemCondition);
        }
    }

    public static class Builder
    extends LootItemConditionalFunction.Builder<Builder> {
        private final Block block;
        private final Set<Property<?>> properties = Sets.newHashSet();

        private Builder(Block block) {
            this.block = block;
        }

        public Builder copy(Property<?> property) {
            if (!this.block.getStateDefinition().getProperties().contains(property)) {
                throw new IllegalStateException("Property " + property + " is not present on block " + this.block);
            }
            this.properties.add(property);
            return this;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new CopyBlockState(this.getConditions(), this.block, this.properties);
        }

        @Override
        protected /* synthetic */ LootItemConditionalFunction.Builder getThis() {
            return this.getThis();
        }
    }

}

