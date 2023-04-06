/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package net.minecraft.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;

public interface CriterionTriggerInstance {
    public ResourceLocation getCriterion();

    public JsonObject serializeToJson(SerializationContext var1);
}

