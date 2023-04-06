/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 */
package net.minecraft.client.gui.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.RawGlyph;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.client.gui.font.FontTexture;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.gui.font.glyphs.MissingGlyph;
import net.minecraft.client.gui.font.glyphs.WhiteGlyph;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class FontSet
implements AutoCloseable {
    private static final EmptyGlyph SPACE_GLYPH = new EmptyGlyph();
    private static final GlyphInfo SPACE_INFO = () -> 4.0f;
    private static final Random RANDOM = new Random();
    private final TextureManager textureManager;
    private final ResourceLocation name;
    private BakedGlyph missingGlyph;
    private BakedGlyph whiteGlyph;
    private final List<GlyphProvider> providers = Lists.newArrayList();
    private final Int2ObjectMap<BakedGlyph> glyphs = new Int2ObjectOpenHashMap();
    private final Int2ObjectMap<GlyphInfo> glyphInfos = new Int2ObjectOpenHashMap();
    private final Int2ObjectMap<IntList> glyphsByWidth = new Int2ObjectOpenHashMap();
    private final List<FontTexture> textures = Lists.newArrayList();

    public FontSet(TextureManager textureManager, ResourceLocation resourceLocation) {
        this.textureManager = textureManager;
        this.name = resourceLocation;
    }

    public void reload(List<GlyphProvider> list) {
        this.closeProviders();
        this.closeTextures();
        this.glyphs.clear();
        this.glyphInfos.clear();
        this.glyphsByWidth.clear();
        this.missingGlyph = this.stitch(MissingGlyph.INSTANCE);
        this.whiteGlyph = this.stitch(WhiteGlyph.INSTANCE);
        IntOpenHashSet intOpenHashSet = new IntOpenHashSet();
        for (GlyphProvider glyphProvider : list) {
            intOpenHashSet.addAll((IntCollection)glyphProvider.getSupportedGlyphs());
        }
        HashSet hashSet = Sets.newHashSet();
        intOpenHashSet.forEach(n2 -> {
            for (GlyphProvider glyphProvider : list) {
                GlyphInfo glyphInfo = n2 == 32 ? SPACE_INFO : glyphProvider.getGlyph(n2);
                if (glyphInfo == null) continue;
                hashSet.add(glyphProvider);
                if (glyphInfo == MissingGlyph.INSTANCE) break;
                ((IntList)this.glyphsByWidth.computeIfAbsent(Mth.ceil(glyphInfo.getAdvance(false)), n -> new IntArrayList())).add(n2);
                break;
            }
        });
        list.stream().filter(hashSet::contains).forEach(this.providers::add);
    }

    @Override
    public void close() {
        this.closeProviders();
        this.closeTextures();
    }

    private void closeProviders() {
        for (GlyphProvider glyphProvider : this.providers) {
            glyphProvider.close();
        }
        this.providers.clear();
    }

    private void closeTextures() {
        for (FontTexture fontTexture : this.textures) {
            fontTexture.close();
        }
        this.textures.clear();
    }

    public GlyphInfo getGlyphInfo(int n2) {
        return (GlyphInfo)this.glyphInfos.computeIfAbsent(n2, n -> n == 32 ? SPACE_INFO : this.getRaw(n));
    }

    private RawGlyph getRaw(int n) {
        for (GlyphProvider glyphProvider : this.providers) {
            RawGlyph rawGlyph = glyphProvider.getGlyph(n);
            if (rawGlyph == null) continue;
            return rawGlyph;
        }
        return MissingGlyph.INSTANCE;
    }

    public BakedGlyph getGlyph(int n2) {
        return (BakedGlyph)this.glyphs.computeIfAbsent(n2, n -> n == 32 ? SPACE_GLYPH : this.stitch(this.getRaw(n)));
    }

    private BakedGlyph stitch(RawGlyph rawGlyph) {
        for (FontTexture object2 : this.textures) {
            BakedGlyph bakedGlyph = object2.add(rawGlyph);
            if (bakedGlyph == null) continue;
            return bakedGlyph;
        }
        FontTexture fontTexture = new FontTexture(new ResourceLocation(this.name.getNamespace(), this.name.getPath() + "/" + this.textures.size()), rawGlyph.isColored());
        this.textures.add(fontTexture);
        this.textureManager.register(fontTexture.getName(), fontTexture);
        BakedGlyph bakedGlyph = fontTexture.add(rawGlyph);
        return bakedGlyph == null ? this.missingGlyph : bakedGlyph;
    }

    public BakedGlyph getRandomGlyph(GlyphInfo glyphInfo) {
        IntList intList = (IntList)this.glyphsByWidth.get(Mth.ceil(glyphInfo.getAdvance(false)));
        if (intList != null && !intList.isEmpty()) {
            return this.getGlyph(intList.getInt(RANDOM.nextInt(intList.size())));
        }
        return this.missingGlyph;
    }

    public BakedGlyph whiteGlyph() {
        return this.whiteGlyph;
    }
}

