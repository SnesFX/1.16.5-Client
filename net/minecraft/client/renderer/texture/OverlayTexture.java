/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;

public class OverlayTexture
implements AutoCloseable {
    public static final int NO_OVERLAY = OverlayTexture.pack(0, 10);
    private final DynamicTexture texture = new DynamicTexture(16, 16, false);

    public OverlayTexture() {
        NativeImage nativeImage = this.texture.getPixels();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                if (i < 8) {
                    nativeImage.setPixelRGBA(j, i, -1308622593);
                    continue;
                }
                int n = (int)((1.0f - (float)j / 15.0f * 0.75f) * 255.0f);
                nativeImage.setPixelRGBA(j, i, n << 24 | 0xFFFFFF);
            }
        }
        RenderSystem.activeTexture(33985);
        this.texture.bind();
        RenderSystem.matrixMode(5890);
        RenderSystem.loadIdentity();
        float f = 0.06666667f;
        RenderSystem.scalef(0.06666667f, 0.06666667f, 0.06666667f);
        RenderSystem.matrixMode(5888);
        this.texture.bind();
        nativeImage.upload(0, 0, 0, 0, 0, nativeImage.getWidth(), nativeImage.getHeight(), false, true, false, false);
        RenderSystem.activeTexture(33984);
    }

    @Override
    public void close() {
        this.texture.close();
    }

    public void setupOverlayColor() {
        RenderSystem.setupOverlayColor(this.texture::getId, 16);
    }

    public static int u(float f) {
        return (int)(f * 15.0f);
    }

    public static int v(boolean bl) {
        return bl ? 3 : 10;
    }

    public static int pack(int n, int n2) {
        return n | n2 << 16;
    }

    public static int pack(float f, boolean bl) {
        return OverlayTexture.pack(OverlayTexture.u(f), OverlayTexture.v(bl));
    }

    public void teardownOverlayColor() {
        RenderSystem.teardownOverlayColor();
    }
}

