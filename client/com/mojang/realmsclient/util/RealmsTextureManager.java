/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  com.mojang.util.UUIDTypeAdapter
 *  javax.annotation.Nullable
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.SkinProcessor;
import com.mojang.util.UUIDTypeAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsTextureManager {
    private static final Map<String, RealmsTexture> TEXTURES = Maps.newHashMap();
    private static final Map<String, Boolean> SKIN_FETCH_STATUS = Maps.newHashMap();
    private static final Map<String, String> FETCHED_SKINS = Maps.newHashMap();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation TEMPLATE_ICON_LOCATION = new ResourceLocation("textures/gui/presets/isles.png");

    public static void bindWorldTemplate(String string, @Nullable String string2) {
        if (string2 == null) {
            Minecraft.getInstance().getTextureManager().bind(TEMPLATE_ICON_LOCATION);
            return;
        }
        int n = RealmsTextureManager.getTextureId(string, string2);
        RenderSystem.bindTexture(n);
    }

    public static void withBoundFace(String string, Runnable runnable) {
        RenderSystem.pushTextureAttributes();
        try {
            RealmsTextureManager.bindFace(string);
            runnable.run();
        }
        finally {
            RenderSystem.popAttributes();
        }
    }

    private static void bindDefaultFace(UUID uUID) {
        Minecraft.getInstance().getTextureManager().bind(DefaultPlayerSkin.getDefaultSkin(uUID));
    }

    private static void bindFace(final String string) {
        UUID uUID = UUIDTypeAdapter.fromString((String)string);
        if (TEXTURES.containsKey(string)) {
            RenderSystem.bindTexture(TEXTURES.get(string).textureId);
            return;
        }
        if (SKIN_FETCH_STATUS.containsKey(string)) {
            if (!SKIN_FETCH_STATUS.get(string).booleanValue()) {
                RealmsTextureManager.bindDefaultFace(uUID);
            } else if (FETCHED_SKINS.containsKey(string)) {
                int n = RealmsTextureManager.getTextureId(string, FETCHED_SKINS.get(string));
                RenderSystem.bindTexture(n);
            } else {
                RealmsTextureManager.bindDefaultFace(uUID);
            }
            return;
        }
        SKIN_FETCH_STATUS.put(string, false);
        RealmsTextureManager.bindDefaultFace(uUID);
        Thread thread = new Thread("Realms Texture Downloader"){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                block17 : {
                    block16 : {
                        ByteArrayOutputStream byteArrayOutputStream;
                        BufferedImage bufferedImage;
                        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = RealmsUtil.getTextures(string);
                        if (!map.containsKey((Object)MinecraftProfileTexture.Type.SKIN)) break block16;
                        MinecraftProfileTexture minecraftProfileTexture = map.get((Object)MinecraftProfileTexture.Type.SKIN);
                        String string2 = minecraftProfileTexture.getUrl();
                        HttpURLConnection httpURLConnection = null;
                        LOGGER.debug("Downloading http texture from {}", (Object)string2);
                        try {
                            httpURLConnection = (HttpURLConnection)new URL(string2).openConnection(Minecraft.getInstance().getProxy());
                            httpURLConnection.setDoInput(true);
                            httpURLConnection.setDoOutput(false);
                            httpURLConnection.connect();
                            if (httpURLConnection.getResponseCode() / 100 != 2) {
                                SKIN_FETCH_STATUS.remove(string);
                                return;
                            }
                            try {
                                bufferedImage = ImageIO.read(httpURLConnection.getInputStream());
                            }
                            catch (Exception exception) {
                                SKIN_FETCH_STATUS.remove(string);
                                if (httpURLConnection != null) {
                                    httpURLConnection.disconnect();
                                }
                                return;
                            }
                            finally {
                                IOUtils.closeQuietly((InputStream)httpURLConnection.getInputStream());
                            }
                            bufferedImage = new SkinProcessor().process(bufferedImage);
                            byteArrayOutputStream = new ByteArrayOutputStream();
                        }
                        catch (Exception exception) {
                            LOGGER.error("Couldn't download http texture", (Throwable)exception);
                            SKIN_FETCH_STATUS.remove(string);
                        }
                        finally {
                            if (httpURLConnection != null) {
                                httpURLConnection.disconnect();
                            }
                        }
                        ImageIO.write((RenderedImage)bufferedImage, "png", byteArrayOutputStream);
                        FETCHED_SKINS.put(string, new Base64().encodeToString(byteArrayOutputStream.toByteArray()));
                        SKIN_FETCH_STATUS.put(string, true);
                        break block17;
                    }
                    SKIN_FETCH_STATUS.put(string, true);
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static int getTextureId(String string, String string2) {
        int n;
        Object object;
        if (TEXTURES.containsKey(string)) {
            object = TEXTURES.get(string);
            if (((RealmsTexture)object).image.equals(string2)) {
                return ((RealmsTexture)object).textureId;
            }
            RenderSystem.deleteTexture(((RealmsTexture)object).textureId);
            n = ((RealmsTexture)object).textureId;
        } else {
            n = GlStateManager._genTexture();
        }
        object = null;
        int n2 = 0;
        int n3 = 0;
        try {
            BufferedImage bufferedImage;
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(new Base64().decode(string2));
            try {
                bufferedImage = ImageIO.read(byteArrayInputStream);
            }
            finally {
                IOUtils.closeQuietly((InputStream)byteArrayInputStream);
            }
            n2 = bufferedImage.getWidth();
            n3 = bufferedImage.getHeight();
            int[] arrn = new int[n2 * n3];
            bufferedImage.getRGB(0, 0, n2, n3, arrn, 0, n2);
            object = ByteBuffer.allocateDirect(4 * n2 * n3).order(ByteOrder.nativeOrder()).asIntBuffer();
            ((IntBuffer)object).put(arrn);
            ((Buffer)object).flip();
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
        RenderSystem.activeTexture(33984);
        RenderSystem.bindTexture(n);
        TextureUtil.initTexture((IntBuffer)object, n2, n3);
        TEXTURES.put(string, new RealmsTexture(string2, n));
        return n;
    }

    public static class RealmsTexture {
        private final String image;
        private final int textureId;

        public RealmsTexture(String string, int n) {
            this.image = string;
            this.textureId = n;
        }
    }

}

