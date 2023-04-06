/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.Maps
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.util.UUIDTypeAdapter
 */
package com.mojang.realmsclient.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;
import java.net.Proxy;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;

public class RealmsUtil {
    private static final YggdrasilAuthenticationService AUTHENTICATION_SERVICE = new YggdrasilAuthenticationService(Minecraft.getInstance().getProxy());
    private static final MinecraftSessionService SESSION_SERVICE = AUTHENTICATION_SERVICE.createMinecraftSessionService();
    public static LoadingCache<String, GameProfile> gameProfileCache = CacheBuilder.newBuilder().expireAfterWrite(60L, TimeUnit.MINUTES).build((CacheLoader)new CacheLoader<String, GameProfile>(){

        public GameProfile load(String string) throws Exception {
            GameProfile gameProfile = SESSION_SERVICE.fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString((String)string), null), false);
            if (gameProfile == null) {
                throw new Exception("Couldn't get profile");
            }
            return gameProfile;
        }

        public /* synthetic */ Object load(Object object) throws Exception {
            return this.load((String)object);
        }
    });

    public static String uuidToName(String string) throws Exception {
        GameProfile gameProfile = (GameProfile)gameProfileCache.get((Object)string);
        return gameProfile.getName();
    }

    public static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(String string) {
        try {
            GameProfile gameProfile = (GameProfile)gameProfileCache.get((Object)string);
            return SESSION_SERVICE.getTextures(gameProfile, false);
        }
        catch (Exception exception) {
            return Maps.newHashMap();
        }
    }

    public static String convertToAgePresentation(long l) {
        if (l < 0L) {
            return "right now";
        }
        long l2 = l / 1000L;
        if (l2 < 60L) {
            return (l2 == 1L ? "1 second" : l2 + " seconds") + " ago";
        }
        if (l2 < 3600L) {
            long l3 = l2 / 60L;
            return (l3 == 1L ? "1 minute" : l3 + " minutes") + " ago";
        }
        if (l2 < 86400L) {
            long l4 = l2 / 3600L;
            return (l4 == 1L ? "1 hour" : l4 + " hours") + " ago";
        }
        long l5 = l2 / 86400L;
        return (l5 == 1L ? "1 day" : l5 + " days") + " ago";
    }

    public static String convertToAgePresentationFromInstant(Date date) {
        return RealmsUtil.convertToAgePresentation(System.currentTimeMillis() - date.getTime());
    }

}

