/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 */
package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootTableReference
extends LootPoolSingletonContainer {
    private final ResourceLocation name;

    private LootTableReference(ResourceLocation resourceLocation, int n, int n2, LootItemCondition[] arrlootItemCondition, LootItemFunction[] arrlootItemFunction) {
        super(n, n2, arrlootItemCondition, arrlootItemFunction);
        this.name = resourceLocation;
    }

    @Override
    public LootPoolEntryType getType() {
        return LootPoolEntries.REFERENCE;
    }

    @Override
    public void createItemStack(Consumer<ItemStack> consumer, LootContext lootContext) {
        LootTable lootTable = lootContext.getLootTable(this.name);
        lootTable.getRandomItemsRaw(lootContext, consumer);
    }

    @Override
    public void validate(ValidationContext validationContext) {
        if (validationContext.hasVisitedTable(this.name)) {
            validationContext.reportProblem("Table " + this.name + " is recursively called");
            return;
        }
        super.validate(validationContext);
        LootTable lootTable = validationContext.resolveLootTable(this.name);
        if (lootTable == null) {
            validationContext.reportProblem("Unknown loot table called " + this.name);
        } else {
            lootTable.validate(validationContext.enterTable("->{" + this.name + "}", this.name));
        }
    }

    public static LootPoolSingletonContainer.Builder<?> lootTableReference(ResourceLocation resourceLocation) {
        return LootTableReference.simpleBuilder((n, n2, arrlootItemCondition, arrlootItemFunction) -> new LootTableReference(resourceLocation, n, n2, arrlootItemCondition, arrlootItemFunction));
    }

    public static class Serializer
    extends LootPoolSingletonContainer.Serializer<LootTableReference> {
        @Override
        public void serializeCustom(JsonObject jsonObject, LootTableReference lootTableReference, JsonSerializationContext jsonSerializationContext) {
            super.serializeCustom(jsonObject, lootTableReference, jsonSerializationContext);
            jsonObject.addProperty("name", lootTableReference.name.toString());
        }

        @Override
        protected LootTableReference deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int n, int n2, LootItemCondition[] arrlootItemCondition, LootItemFunction[] arrlootItemFunction) {
            ResourceLocation resourceLocation = new ResourceLocation(GsonHelper.getAsString(jsonObject, "name"));
            return new LootTableReference(resourceLocation, n, n2, arrlootItemCondition, arrlootItemFunction);
        }

        @Override
        protected /* synthetic */ LootPoolSingletonContainer deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int n, int n2, LootItemCondition[] arrlootItemCondition, LootItemFunction[] arrlootItemFunction) {
            return this.deserialize(jsonObject, jsonDeserializationContext, n, n2, arrlootItemCondition, arrlootItemFunction);
        }
    }

}

