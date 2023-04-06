/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 */
package com.mojang.realmsclient.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class UploadTokenCache {
    private static final Long2ObjectMap<String> TOKEN_CACHE = new Long2ObjectOpenHashMap();

    public static String get(long l) {
        return (String)TOKEN_CACHE.get(l);
    }

    public static void invalidate(long l) {
        TOKEN_CACHE.remove(l);
    }

    public static void put(long l, String string) {
        TOKEN_CACHE.put(l, (Object)string);
    }
}

