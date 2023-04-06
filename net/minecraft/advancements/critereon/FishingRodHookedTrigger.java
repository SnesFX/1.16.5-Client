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
import java.util.function.Predicate;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class FishingRodHookedTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID = new ResourceLocation("fishing_rod_hooked");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite composite, DeserializationContext deserializationContext) {
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("rod"));
        EntityPredicate.Composite composite2 = EntityPredicate.Composite.fromJson(jsonObject, "entity", deserializationContext);
        ItemPredicate itemPredicate2 = ItemPredicate.fromJson(jsonObject.get("item"));
        return new TriggerInstance(composite, itemPredicate, composite2, itemPredicate2);
    }

    public void trigger(ServerPlayer serverPlayer, ItemStack itemStack, FishingHook fishingHook, Collection<ItemStack> collection) {
        LootContext lootContext = EntityPredicate.createContext(serverPlayer, fishingHook.getHookedIn() != null ? fishingHook.getHookedIn() : fishingHook);
        this.trigger(serverPlayer, triggerInstance -> triggerInstance.matches(itemStack, lootContext, collection));
    }

    @Override
    public /* synthetic */ AbstractCriterionTriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite composite, DeserializationContext deserializationContext) {
        return this.createInstance(jsonObject, composite, deserializationContext);
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final ItemPredicate rod;
        private final EntityPredicate.Composite entity;
        private final ItemPredicate item;

        public TriggerInstance(EntityPredicate.Composite composite, ItemPredicate itemPredicate, EntityPredicate.Composite composite2, ItemPredicate itemPredicate2) {
            super(ID, composite);
            this.rod = itemPredicate;
            this.entity = composite2;
            this.item = itemPredicate2;
        }

        public static TriggerInstance fishedItem(ItemPredicate itemPredicate, EntityPredicate entityPredicate, ItemPredicate itemPredicate2) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, itemPredicate, EntityPredicate.Composite.wrap(entityPredicate), itemPredicate2);
        }

        public boolean matches(ItemStack itemStack, LootContext lootContext, Collection<ItemStack> collection) {
            if (!this.rod.matches(itemStack)) {
                return false;
            }
            if (!this.entity.matches(lootContext)) {
                return false;
            }
            if (this.item != ItemPredicate.ANY) {
                Object object;
                boolean bl = false;
                Entity entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
                if (entity instanceof ItemEntity && this.item.matches(((ItemEntity)(object = (ItemEntity)entity)).getItem())) {
                    bl = true;
                }
                for (ItemStack itemStack2 : collection) {
                    if (!this.item.matches(itemStack2)) continue;
                    bl = true;
                    break;
                }
                if (!bl) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext serializationContext) {
            JsonObject jsonObject = super.serializeToJson(serializationContext);
            jsonObject.add("rod", this.rod.serializeToJson());
            jsonObject.add("entity", this.entity.toJson(serializationContext));
            jsonObject.add("item", this.item.serializeToJson());
            return jsonObject;
        }
    }

}

