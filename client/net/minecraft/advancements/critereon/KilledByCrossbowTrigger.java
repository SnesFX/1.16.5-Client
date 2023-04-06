/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootContext;

public class KilledByCrossbowTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID = new ResourceLocation("killed_by_crossbow");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite composite, DeserializationContext deserializationContext) {
        EntityPredicate.Composite[] arrcomposite = EntityPredicate.Composite.fromJsonArray(jsonObject, "victims", deserializationContext);
        MinMaxBounds.Ints ints = MinMaxBounds.Ints.fromJson(jsonObject.get("unique_entity_types"));
        return new TriggerInstance(composite, arrcomposite, ints);
    }

    @Override
    public void trigger(ServerPlayer serverPlayer, Collection<Entity> collection) {
        ArrayList arrayList = Lists.newArrayList();
        HashSet hashSet = Sets.newHashSet();
        for (Entity entity : collection) {
            hashSet.add(entity.getType());
            arrayList.add(EntityPredicate.createContext(serverPlayer, entity));
        }
        this.trigger(serverPlayer, triggerInstance -> triggerInstance.matches(arrayList, hashSet.size()));
    }

    @Override
    public /* synthetic */ AbstractCriterionTriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite composite, DeserializationContext deserializationContext) {
        return this.createInstance(jsonObject, composite, deserializationContext);
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final EntityPredicate.Composite[] victims;
        private final MinMaxBounds.Ints uniqueEntityTypes;

        public TriggerInstance(EntityPredicate.Composite composite, EntityPredicate.Composite[] arrcomposite, MinMaxBounds.Ints ints) {
            super(ID, composite);
            this.victims = arrcomposite;
            this.uniqueEntityTypes = ints;
        }

        public static TriggerInstance crossbowKilled(EntityPredicate.Builder ... arrbuilder) {
            EntityPredicate.Composite[] arrcomposite = new EntityPredicate.Composite[arrbuilder.length];
            for (int i = 0; i < arrbuilder.length; ++i) {
                EntityPredicate.Builder builder = arrbuilder[i];
                arrcomposite[i] = EntityPredicate.Composite.wrap(builder.build());
            }
            return new TriggerInstance(EntityPredicate.Composite.ANY, arrcomposite, MinMaxBounds.Ints.ANY);
        }

        public static TriggerInstance crossbowKilled(MinMaxBounds.Ints ints) {
            EntityPredicate.Composite[] arrcomposite = new EntityPredicate.Composite[]{};
            return new TriggerInstance(EntityPredicate.Composite.ANY, arrcomposite, ints);
        }

        public boolean matches(Collection<LootContext> collection, int n) {
            if (this.victims.length > 0) {
                ArrayList arrayList = Lists.newArrayList(collection);
                for (EntityPredicate.Composite composite : this.victims) {
                    boolean bl = false;
                    Iterator iterator = arrayList.iterator();
                    while (iterator.hasNext()) {
                        LootContext lootContext = (LootContext)iterator.next();
                        if (!composite.matches(lootContext)) continue;
                        iterator.remove();
                        bl = true;
                        break;
                    }
                    if (bl) continue;
                    return false;
                }
            }
            return this.uniqueEntityTypes.matches(n);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext serializationContext) {
            JsonObject jsonObject = super.serializeToJson(serializationContext);
            jsonObject.add("victims", EntityPredicate.Composite.toJson(this.victims, serializationContext));
            jsonObject.add("unique_entity_types", this.uniqueEntityTypes.serializeToJson());
            return jsonObject;
        }
    }

}

