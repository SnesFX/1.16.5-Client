/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.stb.STBTTFontinfo
 *  org.lwjgl.stb.STBTruetype
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.gui.font.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.TrueTypeGlyphProvider;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.providers.GlyphProviderBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

public class TrueTypeGlyphProviderBuilder
implements GlyphProviderBuilder {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ResourceLocation location;
    private final float size;
    private final float oversample;
    private final float shiftX;
    private final float shiftY;
    private final String skip;

    public TrueTypeGlyphProviderBuilder(ResourceLocation resourceLocation, float f, float f2, float f3, float f4, String string) {
        this.location = resourceLocation;
        this.size = f;
        this.oversample = f2;
        this.shiftX = f3;
        this.shiftY = f4;
        this.skip = string;
    }

    public static GlyphProviderBuilder fromJson(JsonObject jsonObject) {
        Object object;
        float f = 0.0f;
        float f2 = 0.0f;
        if (jsonObject.has("shift")) {
            object = jsonObject.getAsJsonArray("shift");
            if (object.size() != 2) {
                throw new JsonParseException("Expected 2 elements in 'shift', found " + object.size());
            }
            f = GsonHelper.convertToFloat(object.get(0), "shift[0]");
            f2 = GsonHelper.convertToFloat(object.get(1), "shift[1]");
        }
        object = new StringBuilder();
        if (jsonObject.has("skip")) {
            JsonElement jsonElement = jsonObject.get("skip");
            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = GsonHelper.convertToJsonArray(jsonElement, "skip");
                for (int i = 0; i < jsonArray.size(); ++i) {
                    ((StringBuilder)object).append(GsonHelper.convertToString(jsonArray.get(i), "skip[" + i + "]"));
                }
            } else {
                ((StringBuilder)object).append(GsonHelper.convertToString(jsonElement, "skip"));
            }
        }
        return new TrueTypeGlyphProviderBuilder(new ResourceLocation(GsonHelper.getAsString(jsonObject, "file")), GsonHelper.getAsFloat(jsonObject, "size", 11.0f), GsonHelper.getAsFloat(jsonObject, "oversample", 1.0f), f, f2, ((StringBuilder)object).toString());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    @Override
    public GlyphProvider create(ResourceManager resourceManager) {
        STBTTFontinfo sTBTTFontinfo = null;
        ByteBuffer byteBuffer = null;
        try {
            try (Resource resource = resourceManager.getResource(new ResourceLocation(this.location.getNamespace(), "font/" + this.location.getPath()));){
                LOGGER.debug("Loading font {}", (Object)this.location);
                sTBTTFontinfo = STBTTFontinfo.malloc();
                byteBuffer = TextureUtil.readResource(resource.getInputStream());
                byteBuffer.flip();
                LOGGER.debug("Reading font {}", (Object)this.location);
                if (!STBTruetype.stbtt_InitFont((STBTTFontinfo)sTBTTFontinfo, (ByteBuffer)byteBuffer)) {
                    throw new IOException("Invalid ttf");
                }
                TrueTypeGlyphProvider trueTypeGlyphProvider = new TrueTypeGlyphProvider(byteBuffer, sTBTTFontinfo, this.size, this.oversample, this.shiftX, this.shiftY, this.skip);
                return trueTypeGlyphProvider;
            }
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't load truetype font {}", (Object)this.location, (Object)exception);
            if (sTBTTFontinfo != null) {
                sTBTTFontinfo.free();
            }
            MemoryUtil.memFree(byteBuffer);
            return null;
        }
    }
}

