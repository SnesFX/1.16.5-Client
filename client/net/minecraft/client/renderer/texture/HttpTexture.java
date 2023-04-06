/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.io.FileUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpTexture
extends SimpleTexture {
    private static final Logger LOGGER = LogManager.getLogger();
    @Nullable
    private final File file;
    private final String urlString;
    private final boolean processLegacySkin;
    @Nullable
    private final Runnable onDownloaded;
    @Nullable
    private CompletableFuture<?> future;
    private boolean uploaded;

    public HttpTexture(@Nullable File file, String string, ResourceLocation resourceLocation, boolean bl, @Nullable Runnable runnable) {
        super(resourceLocation);
        this.file = file;
        this.urlString = string;
        this.processLegacySkin = bl;
        this.onDownloaded = runnable;
    }

    private void loadCallback(NativeImage nativeImage) {
        if (this.onDownloaded != null) {
            this.onDownloaded.run();
        }
        Minecraft.getInstance().execute(() -> {
            this.uploaded = true;
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall(() -> this.upload(nativeImage));
            } else {
                this.upload(nativeImage);
            }
        });
    }

    private void upload(NativeImage nativeImage) {
        TextureUtil.prepareImage(this.getId(), nativeImage.getWidth(), nativeImage.getHeight());
        nativeImage.upload(0, 0, 0, true);
    }

    @Override
    public void load(ResourceManager resourceManager) throws IOException {
        NativeImage nativeImage;
        Minecraft.getInstance().execute(() -> {
            if (!this.uploaded) {
                try {
                    super.load(resourceManager);
                }
                catch (IOException iOException) {
                    LOGGER.warn("Failed to load texture: {}", (Object)this.location, (Object)iOException);
                }
                this.uploaded = true;
            }
        });
        if (this.future != null) {
            return;
        }
        if (this.file != null && this.file.isFile()) {
            LOGGER.debug("Loading http texture from local cache ({})", (Object)this.file);
            FileInputStream fileInputStream = new FileInputStream(this.file);
            nativeImage = this.load(fileInputStream);
        } else {
            nativeImage = null;
        }
        if (nativeImage != null) {
            this.loadCallback(nativeImage);
            return;
        }
        this.future = CompletableFuture.runAsync(() -> {
            HttpURLConnection httpURLConnection = null;
            LOGGER.debug("Downloading http texture from {} to {}", (Object)this.urlString, (Object)this.file);
            try {
                InputStream inputStream;
                httpURLConnection = (HttpURLConnection)new URL(this.urlString).openConnection(Minecraft.getInstance().getProxy());
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(false);
                httpURLConnection.connect();
                if (httpURLConnection.getResponseCode() / 100 != 2) {
                    return;
                }
                if (this.file != null) {
                    FileUtils.copyInputStreamToFile((InputStream)httpURLConnection.getInputStream(), (File)this.file);
                    inputStream = new FileInputStream(this.file);
                } else {
                    inputStream = httpURLConnection.getInputStream();
                }
                Minecraft.getInstance().execute(() -> {
                    NativeImage nativeImage = this.load(inputStream);
                    if (nativeImage != null) {
                        this.loadCallback(nativeImage);
                    }
                });
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't download http texture", (Throwable)exception);
            }
            finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
        }, Util.backgroundExecutor());
    }

    @Nullable
    private NativeImage load(InputStream inputStream) {
        NativeImage nativeImage = null;
        try {
            nativeImage = NativeImage.read(inputStream);
            if (this.processLegacySkin) {
                nativeImage = HttpTexture.processLegacySkin(nativeImage);
            }
        }
        catch (IOException iOException) {
            LOGGER.warn("Error while loading the skin texture", (Throwable)iOException);
        }
        return nativeImage;
    }

    private static NativeImage processLegacySkin(NativeImage nativeImage) {
        boolean bl;
        boolean bl2 = bl = nativeImage.getHeight() == 32;
        if (bl) {
            NativeImage nativeImage2 = new NativeImage(64, 64, true);
            nativeImage2.copyFrom(nativeImage);
            nativeImage.close();
            nativeImage = nativeImage2;
            nativeImage.fillRect(0, 32, 64, 32, 0);
            nativeImage.copyRect(4, 16, 16, 32, 4, 4, true, false);
            nativeImage.copyRect(8, 16, 16, 32, 4, 4, true, false);
            nativeImage.copyRect(0, 20, 24, 32, 4, 12, true, false);
            nativeImage.copyRect(4, 20, 16, 32, 4, 12, true, false);
            nativeImage.copyRect(8, 20, 8, 32, 4, 12, true, false);
            nativeImage.copyRect(12, 20, 16, 32, 4, 12, true, false);
            nativeImage.copyRect(44, 16, -8, 32, 4, 4, true, false);
            nativeImage.copyRect(48, 16, -8, 32, 4, 4, true, false);
            nativeImage.copyRect(40, 20, 0, 32, 4, 12, true, false);
            nativeImage.copyRect(44, 20, -8, 32, 4, 12, true, false);
            nativeImage.copyRect(48, 20, -16, 32, 4, 12, true, false);
            nativeImage.copyRect(52, 20, -8, 32, 4, 12, true, false);
        }
        HttpTexture.setNoAlpha(nativeImage, 0, 0, 32, 16);
        if (bl) {
            HttpTexture.doNotchTransparencyHack(nativeImage, 32, 0, 64, 32);
        }
        HttpTexture.setNoAlpha(nativeImage, 0, 16, 64, 32);
        HttpTexture.setNoAlpha(nativeImage, 16, 48, 48, 64);
        return nativeImage;
    }

    private static void doNotchTransparencyHack(NativeImage nativeImage, int n, int n2, int n3, int n4) {
        int n5;
        int n6;
        for (n6 = n; n6 < n3; ++n6) {
            for (n5 = n2; n5 < n4; ++n5) {
                int n7 = nativeImage.getPixelRGBA(n6, n5);
                if ((n7 >> 24 & 0xFF) >= 128) continue;
                return;
            }
        }
        for (n6 = n; n6 < n3; ++n6) {
            for (n5 = n2; n5 < n4; ++n5) {
                nativeImage.setPixelRGBA(n6, n5, nativeImage.getPixelRGBA(n6, n5) & 0xFFFFFF);
            }
        }
    }

    private static void setNoAlpha(NativeImage nativeImage, int n, int n2, int n3, int n4) {
        for (int i = n; i < n3; ++i) {
            for (int j = n2; j < n4; ++j) {
                nativeImage.setPixelRGBA(i, j, nativeImage.getPixelRGBA(i, j) | 0xFF000000);
            }
        }
    }
}

