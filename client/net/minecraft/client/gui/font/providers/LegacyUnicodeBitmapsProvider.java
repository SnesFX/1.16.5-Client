/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonObject
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.font.providers;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.RawGlyph;
import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.providers.GlyphProviderBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LegacyUnicodeBitmapsProvider
implements GlyphProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ResourceManager resourceManager;
    private final byte[] sizes;
    private final String texturePattern;
    private final Map<ResourceLocation, NativeImage> textures = Maps.newHashMap();

    public LegacyUnicodeBitmapsProvider(ResourceManager resourceManager, byte[] arrby, String string) {
        this.resourceManager = resourceManager;
        this.sizes = arrby;
        this.texturePattern = string;
        for (int i = 0; i < 256; ++i) {
            int n = i * 256;
            ResourceLocation resourceLocation = this.getSheetLocation(n);
            try {
                try (Resource resource = this.resourceManager.getResource(resourceLocation);
                     NativeImage nativeImage = NativeImage.read(NativeImage.Format.RGBA, resource.getInputStream());){
                    if (nativeImage.getWidth() == 256 && nativeImage.getHeight() == 256) {
                        for (int j = 0; j < 256; ++j) {
                            byte by = arrby[n + j];
                            if (by == 0 || LegacyUnicodeBitmapsProvider.getLeft(by) <= LegacyUnicodeBitmapsProvider.getRight(by)) continue;
                            arrby[n + j] = 0;
                        }
                        continue;
                    }
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            Arrays.fill(arrby, n, n + 256, (byte)0);
        }
    }

    @Override
    public void close() {
        this.textures.values().forEach(NativeImage::close);
    }

    private ResourceLocation getSheetLocation(int n) {
        ResourceLocation resourceLocation = new ResourceLocation(String.format(this.texturePattern, String.format("%02x", n / 256)));
        return new ResourceLocation(resourceLocation.getNamespace(), "textures/" + resourceLocation.getPath());
    }

    @Nullable
    @Override
    public RawGlyph getGlyph(int n) {
        NativeImage nativeImage;
        if (n < 0 || n > 65535) {
            return null;
        }
        byte by = this.sizes[n];
        if (by != 0 && (nativeImage = this.textures.computeIfAbsent(this.getSheetLocation(n), this::loadTexture)) != null) {
            int n2 = LegacyUnicodeBitmapsProvider.getLeft(by);
            return new Glyph(n % 16 * 16 + n2, (n & 0xFF) / 16 * 16, LegacyUnicodeBitmapsProvider.getRight(by) - n2, 16, nativeImage);
        }
        return null;
    }

    @Override
    public IntSet getSupportedGlyphs() {
        IntOpenHashSet intOpenHashSet = new IntOpenHashSet();
        for (int i = 0; i < 65535; ++i) {
            if (this.sizes[i] == 0) continue;
            intOpenHashSet.add(i);
        }
        return intOpenHashSet;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private NativeImage loadTexture(ResourceLocation resourceLocation) {
        try {
            try (Resource resource = this.resourceManager.getResource(resourceLocation);){
                NativeImage nativeImage = NativeImage.read(NativeImage.Format.RGBA, resource.getInputStream());
                return nativeImage;
            }
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't load texture {}", (Object)resourceLocation, (Object)iOException);
            return null;
        }
    }

    private static int getLeft(byte by) {
        return by >> 4 & 0xF;
    }

    private static int getRight(byte by) {
        return (by & 0xF) + 1;
    }

    static class Glyph
    implements RawGlyph {
        private final int width;
        private final int height;
        private final int sourceX;
        private final int sourceY;
        private final NativeImage source;

        private Glyph(int n, int n2, int n3, int n4, NativeImage nativeImage) {
            this.width = n3;
            this.height = n4;
            this.sourceX = n;
            this.sourceY = n2;
            this.source = nativeImage;
        }

        @Override
        public float getOversample() {
            return 2.0f;
        }

        @Override
        public int getPixelWidth() {
            return this.width;
        }

        @Override
        public int getPixelHeight() {
            return this.height;
        }

        @Override
        public float getAdvance() {
            return this.width / 2 + 1;
        }

        @Override
        public void upload(int n, int n2) {
            this.source.upload(0, n, n2, this.sourceX, this.sourceY, this.width, this.height, false, false);
        }

        @Override
        public boolean isColored() {
            return this.source.format().components() > 1;
        }

        @Override
        public float getShadowOffset() {
            return 0.5f;
        }

        @Override
        public float getBoldOffset() {
            return 0.5f;
        }
    }

    public static class Builder
    implements GlyphProviderBuilder {
        private final ResourceLocation metadata;
        private final String texturePattern;

        public Builder(ResourceLocation resourceLocation, String string) {
            this.metadata = resourceLocation;
            this.texturePattern = string;
        }

        public static GlyphProviderBuilder fromJson(JsonObject jsonObject) {
            return new Builder(new ResourceLocation(GsonHelper.getAsString(jsonObject, "sizes")), GsonHelper.getAsString(jsonObject, "template"));
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Nullable
        @Override
        public GlyphProvider create(ResourceManager resourceManager) {
            try {
                try (Resource resource = Minecraft.getInstance().getResourceManager().getResource(this.metadata);){
                    byte[] arrby = new byte[65536];
                    resource.getInputStream().read(arrby);
                    LegacyUnicodeBitmapsProvider legacyUnicodeBitmapsProvider = new LegacyUnicodeBitmapsProvider(resourceManager, arrby, this.texturePattern);
                    return legacyUnicodeBitmapsProvider;
                }
            }
            catch (IOException iOException) {
                LOGGER.error("Cannot load {}, unicode glyphs will not render correctly", (Object)this.metadata);
                return null;
            }
        }
    }

}

