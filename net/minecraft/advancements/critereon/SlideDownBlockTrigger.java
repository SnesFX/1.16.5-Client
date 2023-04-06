/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class SlideDownBlockTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID = new ResourceLocation("slide_down_block");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite composite, DeserializationContext deserializationContext) {
        Block block = SlideDownBlockTrigger.deserializeBlock(jsonObject);
        StatePropertiesPredicate statePropertiesPredicate = StatePropertiesPredicate.fromJson(jsonObject.get("state"));
        if (block != null) {
            statePropertiesPredicate.checkState(block.getStateDefinition(), string -> {
                throw new JsonSyntaxException("Block " + block + " has no property " + string);
            });
        }
        return new TriggerInstance(composite, block, statePropertiesPredicate);
    }

    @Nullable
    private static Block deserializeBlock(JsonObject jsonObject) {
        if (jsonObject.has("block")) {
            ResourceLocation resourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "block"));
            return Registry.BLOCK.getOptional(resourceLocation).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + resourceLocation + "'"));
        }
        return null;
    }

    public void trigger(ServerPlayer serverPlayer, BlockState blockState) {
        this.trigger(serverPlayer, triggerInstance -> triggerInstance.matches(blockState));
    }

    @Override
    public /* synthetic */ AbstractCriterionTriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite composite, DeserializationContext deserializationContext) {
        return this.createInstance(jsonObject, composite, deserializationContext);
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final Block block;
        private final StatePropertiesPredicate state;

        public TriggerInstance(EntityPredicate.Composite composite, @Nullable Block block, StatePropertiesPredicate statePropertiesPredicate) {
            super(ID, composite);
            this.block = block;
            this.state = statePropertiesPredicate;
        }

        public static TriggerInstance slidesDownBlock(Block block) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, block, StatePropertiesPredicate.ANY);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext serializationContext) {
            JsonObject jsonObject = super.serializeToJson(serializationContext);
            if (this.block != null) {
                jsonObject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
            }
            jsonObject.add("state", this.state.serializeToJson());
            return jsonObject;
        }

        public boolean matches(BlockState blockState) {
            if (this.block != null && !blockState.is(this.block)) {
                return false;
            }
            return this.state.matches(blockState);
        }
    }

}

