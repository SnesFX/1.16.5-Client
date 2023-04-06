/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.function.Predicate;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.ItemLike;

public class InventoryChangeTrigger
extends SimpleCriterionTrigger<TriggerInstance> {
    private static final ResourceLocation ID = new ResourceLocation("inventory_changed");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite composite, DeserializationContext deserializationContext) {
        JsonObject jsonObject2 = GsonHelper.getAsJsonObject(jsonObject, "slots", new JsonObject());
        MinMaxBounds.Ints ints = MinMaxBounds.Ints.fromJson(jsonObject2.get("occupied"));
        MinMaxBounds.Ints ints2 = MinMaxBounds.Ints.fromJson(jsonObject2.get("full"));
        MinMaxBounds.Ints ints3 = MinMaxBounds.Ints.fromJson(jsonObject2.get("empty"));
        ItemPredicate[] arritemPredicate = ItemPredicate.fromJsonArray(jsonObject.get("items"));
        return new TriggerInstance(composite, ints, ints2, ints3, arritemPredicate);
    }

    public void trigger(ServerPlayer serverPlayer, Inventory inventory, ItemStack itemStack) {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack itemStack2 = inventory.getItem(i);
            if (itemStack2.isEmpty()) {
                ++n2;
                continue;
            }
            ++n3;
            if (itemStack2.getCount() < itemStack2.getMaxStackSize()) continue;
            ++n;
        }
        this.trigger(serverPlayer, inventory, itemStack, n, n2, n3);
    }

    private void trigger(ServerPlayer serverPlayer, Inventory inventory, ItemStack itemStack, int n, int n2, int n3) {
        this.trigger(serverPlayer, triggerInstance -> triggerInstance.matches(inventory, itemStack, n, n2, n3));
    }

    @Override
    public /* synthetic */ AbstractCriterionTriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite composite, DeserializationContext deserializationContext) {
        return this.createInstance(jsonObject, composite, deserializationContext);
    }

    public static class TriggerInstance
    extends AbstractCriterionTriggerInstance {
        private final MinMaxBounds.Ints slotsOccupied;
        private final MinMaxBounds.Ints slotsFull;
        private final MinMaxBounds.Ints slotsEmpty;
        private final ItemPredicate[] predicates;

        public TriggerInstance(EntityPredicate.Composite composite, MinMaxBounds.Ints ints, MinMaxBounds.Ints ints2, MinMaxBounds.Ints ints3, ItemPredicate[] arritemPredicate) {
            super(ID, composite);
            this.slotsOccupied = ints;
            this.slotsFull = ints2;
            this.slotsEmpty = ints3;
            this.predicates = arritemPredicate;
        }

        public static TriggerInstance hasItems(ItemPredicate ... arritemPredicate) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, arritemPredicate);
        }

        public static TriggerInstance hasItems(ItemLike ... arritemLike) {
            ItemPredicate[] arritemPredicate = new ItemPredicate[arritemLike.length];
            for (int i = 0; i < arritemLike.length; ++i) {
                arritemPredicate[i] = new ItemPredicate(null, arritemLike[i].asItem(), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, null, NbtPredicate.ANY);
            }
            return TriggerInstance.hasItems(arritemPredicate);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext serializationContext) {
            JsonObject jsonObject;
            JsonObject jsonObject2 = super.serializeToJson(serializationContext);
            if (!(this.slotsOccupied.isAny() && this.slotsFull.isAny() && this.slotsEmpty.isAny())) {
                jsonObject = new JsonObject();
                jsonObject.add("occupied", this.slotsOccupied.serializeToJson());
                jsonObject.add("full", this.slotsFull.serializeToJson());
                jsonObject.add("empty", this.slotsEmpty.serializeToJson());
                jsonObject2.add("slots", (JsonElement)jsonObject);
            }
            if (this.predicates.length > 0) {
                jsonObject = new JsonArray();
                for (ItemPredicate itemPredicate : this.predicates) {
                    jsonObject.add(itemPredicate.serializeToJson());
                }
                jsonObject2.add("items", (JsonElement)jsonObject);
            }
            return jsonObject2;
        }

        public boolean matches(Inventory inventory, ItemStack itemStack, int n, int n2, int n3) {
            if (!this.slotsFull.matches(n)) {
                return false;
            }
            if (!this.slotsEmpty.matches(n2)) {
                return false;
            }
            if (!this.slotsOccupied.matches(n3)) {
                return false;
            }
            int n4 = this.predicates.length;
            if (n4 == 0) {
                return true;
            }
            if (n4 == 1) {
                return !itemStack.isEmpty() && this.predicates[0].matches(itemStack);
            }
            ObjectArrayList objectArrayList = new ObjectArrayList((Object[])this.predicates);
            int n5 = inventory.getContainerSize();
            for (int i = 0; i < n5; ++i) {
                if (objectArrayList.isEmpty()) {
                    return true;
                }
                ItemStack itemStack2 = inventory.getItem(i);
                if (itemStack2.isEmpty()) continue;
                objectArrayList.removeIf(itemPredicate -> itemPredicate.matches(itemStack2));
            }
            return objectArrayList.isEmpty();
        }
    }

}

