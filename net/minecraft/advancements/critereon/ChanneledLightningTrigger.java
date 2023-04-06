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
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class ChanneledLightningTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID = new ResourceLocation("channeled_lightning");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite composite, DeserializationContext deserializationContext) {
        EntityPredicate.Composite[] arrcomposite = EntityPredicate.Composite.fromJsonArray(jsonObject, "victims", deserializationContext);
        return new TriggerInstance(composite, arrcomposite);
    }

    @Override
    public void trigger(ServerPlayer serverPlayer, Collection<? extends Entity> collection) {
        List list = collection.stream().map(entity -> EntityPredicate.createContext(serverPlayer, entity)).collect(Collectors.toList());
        this.trigger(serverPlayer, triggerInstance -> triggerInstance.matches(list));
    }

    @Override
    public /* synthetic */ AbstractCriterionTriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite composite, DeserializationContext deserializationContext) {
        return this.createInstance(jsonObject, composite, deserializationContext);
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final EntityPredicate.Composite[] victims;

        public TriggerInstance(EntityPredicate.Composite composite, EntityPredicate.Composite[] arrcomposite) {
            super(ID, composite);
            this.victims = arrcomposite;
        }

        public static TriggerInstance channeledLightning(EntityPredicate ... arrentityPredicate) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, (EntityPredicate.Composite[])Stream.of(arrentityPredicate).map(EntityPredicate.Composite::wrap).toArray(n -> new EntityPredicate.Composite[n]));
        }

        public boolean matches(Collection<? extends LootContext> collection) {
            for (EntityPredicate.Composite composite : this.victims) {
                boolean bl = false;
                for (LootContext lootContext : collection) {
                    if (!composite.matches(lootContext)) continue;
                    bl = true;
                    break;
                }
                if (bl) continue;
                return false;
            }
            return true;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext serializationContext) {
            JsonObject jsonObject = super.serializeToJson(serializationContext);
            jsonObject.add("victims", EntityPredicate.Composite.toJson(this.victims, serializationContext));
            return jsonObject;
        }
    }

}

