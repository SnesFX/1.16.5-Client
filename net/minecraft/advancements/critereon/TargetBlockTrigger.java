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
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;

public class TargetBlockTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID = new ResourceLocation("target_hit");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite composite, DeserializationContext deserializationContext) {
        MinMaxBounds.Ints ints = MinMaxBounds.Ints.fromJson(jsonObject.get("signal_strength"));
        EntityPredicate.Composite composite2 = EntityPredicate.Composite.fromJson(jsonObject, "projectile", deserializationContext);
        return new TriggerInstance(composite, ints, composite2);
    }

    public void trigger(ServerPlayer serverPlayer, Entity entity, Vec3 vec3, int n) {
        LootContext lootContext = EntityPredicate.createContext(serverPlayer, entity);
        this.trigger(serverPlayer, triggerInstance -> triggerInstance.matches(lootContext, vec3, n));
    }

    @Override
    public /* synthetic */ AbstractCriterionTriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite composite, DeserializationContext deserializationContext) {
        return this.createInstance(jsonObject, composite, deserializationContext);
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final MinMaxBounds.Ints signalStrength;
        private final EntityPredicate.Composite projectile;

        public TriggerInstance(EntityPredicate.Composite composite, MinMaxBounds.Ints ints, EntityPredicate.Composite composite2) {
            super(ID, composite);
            this.signalStrength = ints;
            this.projectile = composite2;
        }

        public static TriggerInstance targetHit(MinMaxBounds.Ints ints, EntityPredicate.Composite composite) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, ints, composite);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext serializationContext) {
            JsonObject jsonObject = super.serializeToJson(serializationContext);
            jsonObject.add("signal_strength", this.signalStrength.serializeToJson());
            jsonObject.add("projectile", this.projectile.toJson(serializationContext));
            return jsonObject;
        }

        public boolean matches(LootContext lootContext, Vec3 vec3, int n) {
            if (!this.signalStrength.matches(n)) {
                return false;
            }
            return this.projectile.matches(lootContext);
        }
    }

}

