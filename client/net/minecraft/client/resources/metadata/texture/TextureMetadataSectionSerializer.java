/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package net.minecraft.client.resources.metadata.texture;

import com.google.gson.JsonObject;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;

public class TextureMetadataSectionSerializer
implements MetadataSectionSerializer<TextureMetadataSection> {
    @Override
    public TextureMetadataSection fromJson(JsonObject jsonObject) {
        boolean bl = GsonHelper.getAsBoolean(jsonObject, "blur", false);
        boolean bl2 = GsonHelper.getAsBoolean(jsonObject, "clamp", false);
        return new TextureMetadataSection(bl, bl2);
    }

    @Override
    public String getMetadataSectionName() {
        return "texture";
    }

    @Override
    public /* synthetic */ Object fromJson(JsonObject jsonObject) {
        return this.fromJson(jsonObject);
    }
}

