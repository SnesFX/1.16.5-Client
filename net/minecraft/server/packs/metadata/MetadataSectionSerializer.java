/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package net.minecraft.server.packs.metadata;

import com.google.gson.JsonObject;

public interface MetadataSectionSerializer<T> {
    public String getMetadataSectionName();

    public T fromJson(JsonObject var1);
}

