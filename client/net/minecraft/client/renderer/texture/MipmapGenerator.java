/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.Util;

public class MipmapGenerator {
    private static final float[] POW22 = Util.make(new float[256], arrf -> {
        for (int i = 0; i < ((float[])arrf).length; ++i) {
            arrf[i] = (float)Math.pow((float)i / 255.0f, 2.2);
        }
    });

    public static NativeImage[] generateMipLevels(NativeImage nativeImage, int n) {
        NativeImage[] arrnativeImage = new NativeImage[n + 1];
        arrnativeImage[0] = nativeImage;
        if (n > 0) {
            int n2;
            boolean bl = false;
            block0 : for (n2 = 0; n2 < nativeImage.getWidth(); ++n2) {
                for (int i = 0; i < nativeImage.getHeight(); ++i) {
                    if (nativeImage.getPixelRGBA(n2, i) >> 24 != 0) continue;
                    bl = true;
                    break block0;
                }
            }
            for (n2 = 1; n2 <= n; ++n2) {
                NativeImage nativeImage2 = arrnativeImage[n2 - 1];
                NativeImage nativeImage3 = new NativeImage(nativeImage2.getWidth() >> 1, nativeImage2.getHeight() >> 1, false);
                int n3 = nativeImage3.getWidth();
                int n4 = nativeImage3.getHeight();
                for (int i = 0; i < n3; ++i) {
                    for (int j = 0; j < n4; ++j) {
                        nativeImage3.setPixelRGBA(i, j, MipmapGenerator.alphaBlend(nativeImage2.getPixelRGBA(i * 2 + 0, j * 2 + 0), nativeImage2.getPixelRGBA(i * 2 + 1, j * 2 + 0), nativeImage2.getPixelRGBA(i * 2 + 0, j * 2 + 1), nativeImage2.getPixelRGBA(i * 2 + 1, j * 2 + 1), bl));
                    }
                }
                arrnativeImage[n2] = nativeImage3;
            }
        }
        return arrnativeImage;
    }

    private static int alphaBlend(int n, int n2, int n3, int n4, boolean bl) {
        if (bl) {
            float f = 0.0f;
            float f2 = 0.0f;
            float f3 = 0.0f;
            float f4 = 0.0f;
            if (n >> 24 != 0) {
                f += MipmapGenerator.getPow22(n >> 24);
                f2 += MipmapGenerator.getPow22(n >> 16);
                f3 += MipmapGenerator.getPow22(n >> 8);
                f4 += MipmapGenerator.getPow22(n >> 0);
            }
            if (n2 >> 24 != 0) {
                f += MipmapGenerator.getPow22(n2 >> 24);
                f2 += MipmapGenerator.getPow22(n2 >> 16);
                f3 += MipmapGenerator.getPow22(n2 >> 8);
                f4 += MipmapGenerator.getPow22(n2 >> 0);
            }
            if (n3 >> 24 != 0) {
                f += MipmapGenerator.getPow22(n3 >> 24);
                f2 += MipmapGenerator.getPow22(n3 >> 16);
                f3 += MipmapGenerator.getPow22(n3 >> 8);
                f4 += MipmapGenerator.getPow22(n3 >> 0);
            }
            if (n4 >> 24 != 0) {
                f += MipmapGenerator.getPow22(n4 >> 24);
                f2 += MipmapGenerator.getPow22(n4 >> 16);
                f3 += MipmapGenerator.getPow22(n4 >> 8);
                f4 += MipmapGenerator.getPow22(n4 >> 0);
            }
            int n5 = (int)(Math.pow(f /= 4.0f, 0.45454545454545453) * 255.0);
            int n6 = (int)(Math.pow(f2 /= 4.0f, 0.45454545454545453) * 255.0);
            int n7 = (int)(Math.pow(f3 /= 4.0f, 0.45454545454545453) * 255.0);
            int n8 = (int)(Math.pow(f4 /= 4.0f, 0.45454545454545453) * 255.0);
            if (n5 < 96) {
                n5 = 0;
            }
            return n5 << 24 | n6 << 16 | n7 << 8 | n8;
        }
        int n9 = MipmapGenerator.gammaBlend(n, n2, n3, n4, 24);
        int n10 = MipmapGenerator.gammaBlend(n, n2, n3, n4, 16);
        int n11 = MipmapGenerator.gammaBlend(n, n2, n3, n4, 8);
        int n12 = MipmapGenerator.gammaBlend(n, n2, n3, n4, 0);
        return n9 << 24 | n10 << 16 | n11 << 8 | n12;
    }

    private static int gammaBlend(int n, int n2, int n3, int n4, int n5) {
        float f = MipmapGenerator.getPow22(n >> n5);
        float f2 = MipmapGenerator.getPow22(n2 >> n5);
        float f3 = MipmapGenerator.getPow22(n3 >> n5);
        float f4 = MipmapGenerator.getPow22(n4 >> n5);
        float f5 = (float)Math.pow((double)(f + f2 + f3 + f4) * 0.25, 0.45454545454545453);
        return (int)((double)f5 * 255.0);
    }

    private static float getPow22(int n) {
        return POW22[n & 0xFF];
    }
}

