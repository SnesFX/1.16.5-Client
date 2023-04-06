/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 */
package com.mojang.realmsclient.dto;

import com.google.gson.Gson;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;

public class GuardedSerializer {
    private final Gson gson = new Gson();

    public String toJson(ReflectionBasedSerialization reflectionBasedSerialization) {
        return this.gson.toJson((Object)reflectionBasedSerialization);
    }

    public <T extends ReflectionBasedSerialization> T fromJson(String string, Class<T> class_) {
        return (T)((ReflectionBasedSerialization)this.gson.fromJson(string, class_));
    }
}

