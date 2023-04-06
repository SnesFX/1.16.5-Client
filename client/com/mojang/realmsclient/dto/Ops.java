/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 */
package com.mojang.realmsclient.dto;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.Set;

public class Ops
extends ValueObject {
    public Set<String> ops = Sets.newHashSet();

    public static Ops parse(String string) {
        Ops ops = new Ops();
        JsonParser jsonParser = new JsonParser();
        try {
            JsonElement jsonElement = jsonParser.parse(string);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonElement jsonElement2 = jsonObject.get("ops");
            if (jsonElement2.isJsonArray()) {
                for (JsonElement jsonElement3 : jsonElement2.getAsJsonArray()) {
                    ops.ops.add(jsonElement3.getAsString());
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return ops;
    }
}

