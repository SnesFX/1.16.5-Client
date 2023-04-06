/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 */
package net.minecraft.server.packs.metadata.pack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.GsonHelper;

public class PackMetadataSectionSerializer
implements MetadataSectionSerializer<PackMetadataSection> {
    @Override
    public PackMetadataSection fromJson(JsonObject jsonObject) {
        MutableComponent mutableComponent = Component.Serializer.fromJson(jsonObject.get("description"));
        if (mutableComponent == null) {
            throw new JsonParseException("Invalid/missing description!");
        }
        int n = GsonHelper.getAsInt(jsonObject, "pack_format");
        return new PackMetadataSection(mutableComponent, n);
    }

    @Override
    public String getMetadataSectionName() {
        return "pack";
    }

    @Override
    public /* synthetic */ Object fromJson(JsonObject jsonObject) {
        return this.fromJson(jsonObject);
    }
}

