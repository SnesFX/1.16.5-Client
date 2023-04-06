/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package net.minecraft.client.resources.metadata.animation;

import com.google.gson.JsonObject;
import net.minecraft.client.resources.metadata.animation.VillagerMetaDataSection;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;

public class VillagerMetadataSectionSerializer
implements MetadataSectionSerializer<VillagerMetaDataSection> {
    @Override
    public VillagerMetaDataSection fromJson(JsonObject jsonObject) {
        return new VillagerMetaDataSection(VillagerMetaDataSection.Hat.getByName(GsonHelper.getAsString(jsonObject, "hat", "none")));
    }

    @Override
    public String getMetadataSectionName() {
        return "villager";
    }

    @Override
    public /* synthetic */ Object fromJson(JsonObject jsonObject) {
        return this.fromJson(jsonObject);
    }
}

