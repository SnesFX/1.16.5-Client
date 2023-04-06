/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  javax.annotation.Nullable
 */
package com.mojang.blaze3d.font;

import com.mojang.blaze3d.font.RawGlyph;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.Closeable;
import javax.annotation.Nullable;

public interface GlyphProvider
extends Closeable {
    @Override
    default public void close() {
    }

    @Nullable
    default public RawGlyph getGlyph(int n) {
        return null;
    }

    public IntSet getSupportedGlyphs();
}

