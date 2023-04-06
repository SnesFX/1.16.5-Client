/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.font;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.RawGlyph;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.AllMissingGlyphProvider;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.providers.GlyphProviderBuilder;
import net.minecraft.client.gui.font.providers.GlyphProviderBuilderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FontManager
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ResourceLocation MISSING_FONT = new ResourceLocation("minecraft", "missing");
    private final FontSet missingFontSet;
    private final Map<ResourceLocation, FontSet> fontSets = Maps.newHashMap();
    private final TextureManager textureManager;
    private Map<ResourceLocation, ResourceLocation> renames = ImmutableMap.of();
    private final PreparableReloadListener reloadListener = new SimplePreparableReloadListener<Map<ResourceLocation, List<GlyphProvider>>>(){

        @Override
        protected Map<ResourceLocation, List<GlyphProvider>> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
            profilerFiller.startTick();
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            HashMap hashMap = Maps.newHashMap();
            for (ResourceLocation resourceLocation2 : resourceManager.listResources("font", string -> string.endsWith(".json"))) {
                String string2 = resourceLocation2.getPath();
                ResourceLocation resourceLocation3 = new ResourceLocation(resourceLocation2.getNamespace(), string2.substring("font/".length(), string2.length() - ".json".length()));
                List list = hashMap.computeIfAbsent(resourceLocation3, resourceLocation -> Lists.newArrayList((Object[])new GlyphProvider[]{new AllMissingGlyphProvider()}));
                profilerFiller.push(resourceLocation3::toString);
                try {
                    for (Resource resource : resourceManager.getResources(resourceLocation2)) {
                        profilerFiller.push(resource::getSourceName);
                        try {
                            try (Closeable closeable = resource.getInputStream();
                                 BufferedReader throwable2 = new BufferedReader(new InputStreamReader((InputStream)closeable, StandardCharsets.UTF_8));){
                                profilerFiller.push("reading");
                                JsonArray jsonArray = GsonHelper.getAsJsonArray(GsonHelper.fromJson(gson, (Reader)throwable2, JsonObject.class), "providers");
                                profilerFiller.popPush("parsing");
                                for (int i = jsonArray.size() - 1; i >= 0; --i) {
                                    JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonArray.get(i), "providers[" + i + "]");
                                    try {
                                        String string3 = GsonHelper.getAsString(jsonObject, "type");
                                        GlyphProviderBuilderType glyphProviderBuilderType = GlyphProviderBuilderType.byName(string3);
                                        profilerFiller.push(string3);
                                        GlyphProvider glyphProvider = glyphProviderBuilderType.create(jsonObject).create(resourceManager);
                                        if (glyphProvider != null) {
                                            list.add(glyphProvider);
                                        }
                                        profilerFiller.pop();
                                        continue;
                                    }
                                    catch (RuntimeException runtimeException) {
                                        LOGGER.warn("Unable to read definition '{}' in fonts.json in resourcepack: '{}': {}", (Object)resourceLocation3, (Object)resource.getSourceName(), (Object)runtimeException.getMessage());
                                    }
                                }
                                profilerFiller.pop();
                            }
                        }
                        catch (RuntimeException runtimeException) {
                            LOGGER.warn("Unable to load font '{}' in fonts.json in resourcepack: '{}': {}", (Object)resourceLocation3, (Object)resource.getSourceName(), (Object)runtimeException.getMessage());
                        }
                        profilerFiller.pop();
                    }
                }
                catch (IOException iOException) {
                    LOGGER.warn("Unable to load font '{}' in fonts.json: {}", (Object)resourceLocation3, (Object)iOException.getMessage());
                }
                profilerFiller.push("caching");
                IntOpenHashSet intOpenHashSet = new IntOpenHashSet();
                for (Closeable closeable : list) {
                    intOpenHashSet.addAll((IntCollection)closeable.getSupportedGlyphs());
                }
                intOpenHashSet.forEach(n -> {
                    GlyphProvider glyphProvider;
                    if (n == 32) {
                        return;
                    }
                    Iterator iterator = Lists.reverse((List)list).iterator();
                    while (iterator.hasNext() && (glyphProvider = (GlyphProvider)iterator.next()).getGlyph(n) == null) {
                    }
                });
                profilerFiller.pop();
                profilerFiller.pop();
            }
            profilerFiller.endTick();
            return hashMap;
        }

        @Override
        protected void apply(Map<ResourceLocation, List<GlyphProvider>> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
            profilerFiller.startTick();
            profilerFiller.push("closing");
            FontManager.this.fontSets.values().forEach(FontSet::close);
            FontManager.this.fontSets.clear();
            profilerFiller.popPush("reloading");
            map.forEach((resourceLocation, list) -> {
                FontSet fontSet = new FontSet(FontManager.this.textureManager, (ResourceLocation)resourceLocation);
                fontSet.reload(Lists.reverse((List)list));
                FontManager.this.fontSets.put(resourceLocation, fontSet);
            });
            profilerFiller.pop();
            profilerFiller.endTick();
        }

        @Override
        public String getName() {
            return "FontManager";
        }

        @Override
        protected /* synthetic */ Object prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
            return this.prepare(resourceManager, profilerFiller);
        }
    };

    public FontManager(TextureManager textureManager) {
        this.textureManager = textureManager;
        this.missingFontSet = Util.make(new FontSet(textureManager, MISSING_FONT), fontSet -> fontSet.reload(Lists.newArrayList((Object[])new GlyphProvider[]{new AllMissingGlyphProvider()})));
    }

    public void setRenames(Map<ResourceLocation, ResourceLocation> map) {
        this.renames = map;
    }

    public Font createFont() {
        return new Font(resourceLocation -> this.fontSets.getOrDefault(this.renames.getOrDefault(resourceLocation, (ResourceLocation)resourceLocation), this.missingFontSet));
    }

    public PreparableReloadListener getReloadListener() {
        return this.reloadListener;
    }

    @Override
    public void close() {
        this.fontSets.values().forEach(FontSet::close);
        this.missingFontSet.close();
    }

}

