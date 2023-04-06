/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.Predicate;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class UsedEnderEyeTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID = new ResourceLocation("used_ender_eye");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite composite, DeserializationContext deserializationContext) {
        MinMaxBounds.Floats floats = MinMaxBounds.Floats.fromJson(jsonObject.get("distance"));
        return new TriggerInstance(composite, floats);
    }

    public void trigger(ServerPlayer serverPlayer, BlockPos blockPos) {
        double d = serverPlayer.getX() - (double)blockPos.getX();
        double d2 = serverPlayer.getZ() - (double)blockPos.getZ();
        double d3 = d * d + d2 * d2;
        this.trigger(serverPlayer, triggerInstance -> triggerInstance.matches(d3));
    }

    @Override
    public /* synthetic */ AbstractCriterionTriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite composite, DeserializationContext deserializationContext) {
        return this.createInstance(jsonObject, composite, deserializationContext);
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final MinMaxBounds.Floats level;

        public TriggerInstance(EntityPredicate.Composite composite, MinMaxBounds.Floats floats) {
            super(ID, composite);
            this.level = floats;
        }

        public boolean matches(double d) {
            return this.level.matchesSqr(d);
        }
    }

}

