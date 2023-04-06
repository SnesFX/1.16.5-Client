/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.blaze3d.font;

import com.mojang.blaze3d.font.GlyphInfo;

public interface RawGlyph
extends GlyphInfo {
    public int getPixelWidth();

    public int getPixelHeight();

    public void upload(int var1, int var2);

    public boolean isColored();

    public float getOversample();

    default public float getLeft() {
        return this.getBearingX();
    }

    default public float getRight() {
        return this.getLeft() + (float)this.getPixelWidth() / this.getOversample();
    }

    default public float getUp() {
        return this.getBearingY();
    }

    default public float getDown() {
        return this.getUp() + (float)this.getPixelHeight() / this.getOversample();
    }

    default public float getBearingY() {
        return 3.0f;
    }
}

