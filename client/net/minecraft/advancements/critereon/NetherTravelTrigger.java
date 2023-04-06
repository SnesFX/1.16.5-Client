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
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class NetherTravelTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID = new ResourceLocation("nether_travel");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite composite, DeserializationContext deserializationContext) {
        LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("entered"));
        LocationPredicate locationPredicate2 = LocationPredicate.fromJson(jsonObject.get("exited"));
        DistancePredicate distancePredicate = DistancePredicate.fromJson(jsonObject.get("distance"));
        return new TriggerInstance(composite, locationPredicate, locationPredicate2, distancePredicate);
    }

    public void trigger(ServerPlayer serverPlayer, Vec3 vec3) {
        this.trigger(serverPlayer, triggerInstance -> triggerInstance.matches(serverPlayer.getLevel(), vec3, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ()));
    }

    @Override
    public /* synthetic */ AbstractCriterionTriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite composite, DeserializationContext deserializationContext) {
        return this.createInstance(jsonObject, composite, deserializationContext);
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final LocationPredicate entered;
        private final LocationPredicate exited;
        private final DistancePredicate distance;

        public TriggerInstance(EntityPredicate.Composite composite, LocationPredicate locationPredicate, LocationPredicate locationPredicate2, DistancePredicate distancePredicate) {
            super(ID, composite);
            this.entered = locationPredicate;
            this.exited = locationPredicate2;
            this.distance = distancePredicate;
        }

        public static TriggerInstance travelledThroughNether(DistancePredicate distancePredicate) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, LocationPredicate.ANY, LocationPredicate.ANY, distancePredicate);
        }

        public boolean matches(ServerLevel serverLevel, Vec3 vec3, double d, double d2, double d3) {
            if (!this.entered.matches(serverLevel, vec3.x, vec3.y, vec3.z)) {
                return false;
            }
            if (!this.exited.matches(serverLevel, d, d2, d3)) {
                return false;
            }
            return this.distance.matches(vec3.x, vec3.y, vec3.z, d, d2, d3);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext serializationContext) {
            JsonObject jsonObject = super.serializeToJson(serializationContext);
            jsonObject.add("entered", this.entered.serializeToJson());
            jsonObject.add("exited", this.exited.serializeToJson());
            jsonObject.add("distance", this.distance.serializeToJson());
            return jsonObject;
        }
    }

}

