/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.font.RawGlyph;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.Util;

public enum WhiteGlyph implements RawGlyph
{
    INSTANCE;
    
    private static final NativeImage IMAGE_DATA;

    @Override
    public int getPixelWidth() {
        return 5;
    }

    @Override
    public int getPixelHeight() {
        return 8;
    }

    @Override
    public float getAdvance() {
        return 6.0f;
    }

    @Override
    public float getOversample() {
        return 1.0f;
    }

    @Override
    public void upload(int n, int n2) {
        IMAGE_DATA.upload(0, n, n2, false);
    }

    @Override
    public boolean isColored() {
        return true;
    }

    static {
        IMAGE_DATA = Util.make(new NativeImage(NativeImage.Format.RGBA, 5, 8, false), nativeImage -> {
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 5; ++j) {
                    boolean bl = j == 0 || j + 1 == 5 || i == 0 || i + 1 == 8;
                    nativeImage.setPixelRGBA(j, i, -1);
                }
            }
            nativeImage.untrack();
        });
    }
}

