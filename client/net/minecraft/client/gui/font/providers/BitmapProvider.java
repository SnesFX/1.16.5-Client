/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.ints.IntSets
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.font.providers;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.RawGlyph;
import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.providers.GlyphProviderBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BitmapProvider
implements GlyphProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private final NativeImage image;
    private final Int2ObjectMap<Glyph> glyphs;

    private BitmapProvider(NativeImage nativeImage, Int2ObjectMap<Glyph> int2ObjectMap) {
        this.image = nativeImage;
        this.glyphs = int2ObjectMap;
    }

    @Override
    public void close() {
        this.image.close();
    }

    @Nullable
    @Override
    public RawGlyph getGlyph(int n) {
        return (RawGlyph)this.glyphs.get(n);
    }

    @Override
    public IntSet getSupportedGlyphs() {
        return IntSets.unmodifiable((IntSet)this.glyphs.keySet());
    }

    static final class Glyph
    implements RawGlyph {
        private final float scale;
        private final NativeImage image;
        private final int offsetX;
        private final int offsetY;
        private final int width;
        private final int height;
        private final int advance;
        private final int ascent;

        private Glyph(float f, NativeImage nativeImage, int n, int n2, int n3, int n4, int n5, int n6) {
            this.scale = f;
            this.image = nativeImage;
            this.offsetX = n;
            this.offsetY = n2;
            this.width = n3;
            this.height = n4;
            this.advance = n5;
            this.ascent = n6;
        }

        @Override
        public float getOversample() {
            return 1.0f / this.scale;
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
            return this.advance;
        }

        @Override
        public float getBearingY() {
            return RawGlyph.super.getBearingY() + 7.0f - (float)this.ascent;
        }

        @Override
        public void upload(int n, int n2) {
            this.image.upload(0, n, n2, this.offsetX, this.offsetY, this.width, this.height, false, false);
        }

        @Override
        public boolean isColored() {
            return this.image.format().components() > 1;
        }
    }

    public static class Builder
    implements GlyphProviderBuilder {
        private final ResourceLocation texture;
        private final List<int[]> chars;
        private final int height;
        private final int ascent;

        public Builder(ResourceLocation resourceLocation, int n, int n2, List<int[]> list) {
            this.texture = new ResourceLocation(resourceLocation.getNamespace(), "textures/" + resourceLocation.getPath());
            this.chars = list;
            this.height = n;
            this.ascent = n2;
        }

        public static Builder fromJson(JsonObject jsonObject) {
            int n = GsonHelper.getAsInt(jsonObject, "height", 8);
            int n2 = GsonHelper.getAsInt(jsonObject, "ascent");
            if (n2 > n) {
                throw new JsonParseException("Ascent " + n2 + " higher than height " + n);
            }
            ArrayList arrayList = Lists.newArrayList();
            JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "chars");
            for (int i = 0; i < jsonArray.size(); ++i) {
                int n3;
                String string = GsonHelper.convertToString(jsonArray.get(i), "chars[" + i + "]");
                int[] arrn = string.codePoints().toArray();
                if (i > 0 && arrn.length != (n3 = ((int[])arrayList.get(0)).length)) {
                    throw new JsonParseException("Elements of chars have to be the same length (found: " + arrn.length + ", expected: " + n3 + "), pad with space or \\u0000");
                }
                arrayList.add(arrn);
            }
            if (arrayList.isEmpty() || ((int[])arrayList.get(0)).length == 0) {
                throw new JsonParseException("Expected to find data in chars, found none.");
            }
            return new Builder(new ResourceLocation(GsonHelper.getAsString(jsonObject, "file")), n, n2, arrayList);
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
                try (Resource resource = resourceManager.getResource(this.texture);){
                    NativeImage nativeImage = NativeImage.read(NativeImage.Format.RGBA, resource.getInputStream());
                    int n = nativeImage.getWidth();
                    int n2 = nativeImage.getHeight();
                    int n3 = n / this.chars.get(0).length;
                    int n4 = n2 / this.chars.size();
                    float f = (float)this.height / (float)n4;
                    Int2ObjectOpenHashMap int2ObjectOpenHashMap = new Int2ObjectOpenHashMap();
                    int i = 0;
                    do {
                        int[] arrn;
                        int n5;
                        int n52;
                        if (i < this.chars.size()) {
                            n52 = 0;
                            arrn = this.chars.get(i);
                            n5 = arrn.length;
                        } else {
                            BitmapProvider bitmapProvider = new BitmapProvider(nativeImage, (Int2ObjectMap)int2ObjectOpenHashMap);
                            return bitmapProvider;
                        }
                        for (int j = 0; j < n5; ++j) {
                            Glyph glyph;
                            int n7;
                            int n6 = arrn[j];
                            int n8 = n52++;
                            if (n6 == 0 || n6 == 32 || (glyph = (Glyph)int2ObjectOpenHashMap.put(n6, (Object)new Glyph(f, nativeImage, n8 * n3, i * n4, n3, n4, (int)(0.5 + (double)((float)(n7 = this.getActualGlyphWidth(nativeImage, n3, n4, n8, i)) * f)) + 1, this.ascent))) == null) continue;
                            LOGGER.warn("Codepoint '{}' declared multiple times in {}", (Object)Integer.toHexString(n6), (Object)this.texture);
                        }
                        ++i;
                    } while (true);
                }
            }
            catch (IOException iOException) {
                throw new RuntimeException(iOException.getMessage());
            }
        }

        private int getActualGlyphWidth(NativeImage nativeImage, int n, int n2, int n3, int n4) {
            int n5;
            for (n5 = n - 1; n5 >= 0; --n5) {
                int n6 = n3 * n + n5;
                for (int i = 0; i < n2; ++i) {
                    int n7 = n4 * n2 + i;
                    if (nativeImage.getLuminanceOrAlpha(n6, n7) == 0) continue;
                    return n5 + 1;
                }
            }
            return n5 + 1;
        }
    }

}

