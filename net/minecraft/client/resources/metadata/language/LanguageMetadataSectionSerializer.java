/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 */
package net.minecraft.client.resources.metadata.language;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.metadata.language.LanguageMetadataSection;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;

public class LanguageMetadataSectionSerializer
implements MetadataSectionSerializer<LanguageMetadataSection> {
    @Override
    public LanguageMetadataSection fromJson(JsonObject jsonObject) {
        HashSet hashSet = Sets.newHashSet();
        for (Map.Entry entry : jsonObject.entrySet()) {
            String string = (String)entry.getKey();
            if (string.length() > 16) {
                throw new JsonParseException("Invalid language->'" + string + "': language code must not be more than " + 16 + " characters long");
            }
            JsonObject jsonObject2 = GsonHelper.convertToJsonObject((JsonElement)entry.getValue(), "language");
            String string2 = GsonHelper.getAsString(jsonObject2, "region");
            String string3 = GsonHelper.getAsString(jsonObject2, "name");
            boolean bl = GsonHelper.getAsBoolean(jsonObject2, "bidirectional", false);
            if (string2.isEmpty()) {
                throw new JsonParseException("Invalid language->'" + string + "'->region: empty value");
            }
            if (string3.isEmpty()) {
                throw new JsonParseException("Invalid language->'" + string + "'->name: empty value");
            }
            if (hashSet.add(new LanguageInfo(string, string2, string3, bl))) continue;
            throw new JsonParseException("Duplicate language->'" + string + "' defined");
        }
        return new LanguageMetadataSection(hashSet);
    }

    @Override
    public String getMetadataSectionName() {
        return "language";
    }

    @Override
    public /* synthetic */ Object fromJson(JsonObject jsonObject) {
        return this.fromJson(jsonObject);
    }
}

