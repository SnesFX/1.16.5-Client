/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.hash.HashCode
 *  com.google.common.hash.Hashing
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.InsecureTextureException
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.properties.Property
 *  com.mojang.authlib.properties.PropertyMap
 *  javax.annotation.Nullable
 */
package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;

public class SkinManager {
    private final TextureManager textureManager;
    private final File skinsDirectory;
    private final MinecraftSessionService sessionService;
    private final LoadingCache<String, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> insecureSkinCache;

    public SkinManager(TextureManager textureManager, File file, final MinecraftSessionService minecraftSessionService) {
        this.textureManager = textureManager;
        this.skinsDirectory = file;
        this.sessionService = minecraftSessionService;
        this.insecureSkinCache = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build((CacheLoader)new CacheLoader<String, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>(){

            public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> load(String string) {
                GameProfile gameProfile = new GameProfile(null, "dummy_mcdummyface");
                gameProfile.getProperties().put((Object)"textures", (Object)new Property("textures", string, ""));
                try {
                    return minecraftSessionService.getTextures(gameProfile, false);
                }
                catch (Throwable throwable) {
                    return ImmutableMap.of();
                }
            }

            public /* synthetic */ Object load(Object object) throws Exception {
                return this.load((String)object);
            }
        });
    }

    public ResourceLocation registerTexture(MinecraftProfileTexture minecraftProfileTexture, MinecraftProfileTexture.Type type) {
        return this.registerTexture(minecraftProfileTexture, type, null);
    }

    private ResourceLocation registerTexture(MinecraftProfileTexture minecraftProfileTexture, MinecraftProfileTexture.Type type, @Nullable SkinTextureCallback skinTextureCallback) {
        String string = Hashing.sha1().hashUnencodedChars((CharSequence)minecraftProfileTexture.getHash()).toString();
        ResourceLocation resourceLocation = new ResourceLocation("skins/" + string);
        AbstractTexture abstractTexture = this.textureManager.getTexture(resourceLocation);
        if (abstractTexture != null) {
            if (skinTextureCallback != null) {
                skinTextureCallback.onSkinTextureAvailable(type, resourceLocation, minecraftProfileTexture);
            }
        } else {
            File file = new File(this.skinsDirectory, string.length() > 2 ? string.substring(0, 2) : "xx");
            File file2 = new File(file, string);
            HttpTexture httpTexture = new HttpTexture(file2, minecraftProfileTexture.getUrl(), DefaultPlayerSkin.getDefaultSkin(), type == MinecraftProfileTexture.Type.SKIN, () -> {
                if (skinTextureCallback != null) {
                    skinTextureCallback.onSkinTextureAvailable(type, resourceLocation, minecraftProfileTexture);
                }
            });
            this.textureManager.register(resourceLocation, httpTexture);
        }
        return resourceLocation;
    }

    public void registerSkins(GameProfile gameProfile, SkinTextureCallback skinTextureCallback, boolean bl) {
        Runnable runnable = () -> {
            HashMap hashMap = Maps.newHashMap();
            try {
                hashMap.putAll(this.sessionService.getTextures(gameProfile, bl));
            }
            catch (InsecureTextureException insecureTextureException) {
                // empty catch block
            }
            if (hashMap.isEmpty()) {
                gameProfile.getProperties().clear();
                if (gameProfile.getId().equals(Minecraft.getInstance().getUser().getGameProfile().getId())) {
                    gameProfile.getProperties().putAll((Multimap)Minecraft.getInstance().getProfileProperties());
                    hashMap.putAll(this.sessionService.getTextures(gameProfile, false));
                } else {
                    this.sessionService.fillProfileProperties(gameProfile, bl);
                    try {
                        hashMap.putAll(this.sessionService.getTextures(gameProfile, bl));
                    }
                    catch (InsecureTextureException insecureTextureException) {
                        // empty catch block
                    }
                }
            }
            Minecraft.getInstance().execute(() -> RenderSystem.recordRenderCall(() -> ImmutableList.of((Object)MinecraftProfileTexture.Type.SKIN, (Object)MinecraftProfileTexture.Type.CAPE).forEach(type -> {
                if (hashMap.containsKey(type)) {
                    this.registerTexture((MinecraftProfileTexture)hashMap.get(type), (MinecraftProfileTexture.Type)type, skinTextureCallback);
                }
            })));
        };
        Util.backgroundExecutor().execute(runnable);
    }

    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getInsecureSkinInformation(GameProfile gameProfile) {
        Property property = (Property)Iterables.getFirst((Iterable)gameProfile.getProperties().get((Object)"textures"), null);
        if (property == null) {
            return ImmutableMap.of();
        }
        return (Map)this.insecureSkinCache.getUnchecked((Object)property.getValue());
    }

    public static interface SkinTextureCallback {
        public void onSkinTextureAvailable(MinecraftProfileTexture.Type var1, ResourceLocation var2, MinecraftProfileTexture var3);
    }

}

