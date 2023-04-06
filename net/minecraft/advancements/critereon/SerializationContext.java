/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 */
package net.minecraft.advancements.critereon;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SerializationContext {
    public static final SerializationContext INSTANCE = new SerializationContext();
    private final Gson predicateGson = Deserializers.createConditionSerializer().create();

    public final JsonElement serializeConditions(LootItemCondition[] arrlootItemCondition) {
        return this.predicateGson.toJsonTree((Object)arrlootItemCondition);
    }
}

