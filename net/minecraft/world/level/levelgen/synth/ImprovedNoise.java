/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.levelgen.synth;

import java.util.Random;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public final class ImprovedNoise {
    private final byte[] p;
    public final double xo;
    public final double yo;
    public final double zo;

    public ImprovedNoise(Random random) {
        int n;
        this.xo = random.nextDouble() * 256.0;
        this.yo = random.nextDouble() * 256.0;
        this.zo = random.nextDouble() * 256.0;
        this.p = new byte[256];
        for (n = 0; n < 256; ++n) {
            this.p[n] = (byte)n;
        }
        for (n = 0; n < 256; ++n) {
            int n2 = random.nextInt(256 - n);
            byte by = this.p[n];
            this.p[n] = this.p[n + n2];
            this.p[n + n2] = by;
        }
    }

    public double noise(double d, double d2, double d3, double d4, double d5) {
        double d6;
        double d7 = d + this.xo;
        double d8 = d2 + this.yo;
        double d9 = d3 + this.zo;
        int n = Mth.floor(d7);
        int n2 = Mth.floor(d8);
        int n3 = Mth.floor(d9);
        double d10 = d7 - (double)n;
        double d11 = d8 - (double)n2;
        double d12 = d9 - (double)n3;
        double d13 = Mth.smoothstep(d10);
        double d14 = Mth.smoothstep(d11);
        double d15 = Mth.smoothstep(d12);
        if (d4 != 0.0) {
            double d16 = Math.min(d5, d11);
            d6 = (double)Mth.floor(d16 / d4) * d4;
        } else {
            d6 = 0.0;
        }
        return this.sampleAndLerp(n, n2, n3, d10, d11 - d6, d12, d13, d14, d15);
    }

    private static double gradDot(int n, double d, double d2, double d3) {
        int n2 = n & 0xF;
        return SimplexNoise.dot(SimplexNoise.GRADIENT[n2], d, d2, d3);
    }

    private int p(int n) {
        return this.p[n & 0xFF] & 0xFF;
    }

    public double sampleAndLerp(int n, int n2, int n3, double d, double d2, double d3, double d4, double d5, double d6) {
        int n4 = this.p(n) + n2;
        int n5 = this.p(n4) + n3;
        int n6 = this.p(n4 + 1) + n3;
        int n7 = this.p(n + 1) + n2;
        int n8 = this.p(n7) + n3;
        int n9 = this.p(n7 + 1) + n3;
        double d7 = ImprovedNoise.gradDot(this.p(n5), d, d2, d3);
        double d8 = ImprovedNoise.gradDot(this.p(n8), d - 1.0, d2, d3);
        double d9 = ImprovedNoise.gradDot(this.p(n6), d, d2 - 1.0, d3);
        double d10 = ImprovedNoise.gradDot(this.p(n9), d - 1.0, d2 - 1.0, d3);
        double d11 = ImprovedNoise.gradDot(this.p(n5 + 1), d, d2, d3 - 1.0);
        double d12 = ImprovedNoise.gradDot(this.p(n8 + 1), d - 1.0, d2, d3 - 1.0);
        double d13 = ImprovedNoise.gradDot(this.p(n6 + 1), d, d2 - 1.0, d3 - 1.0);
        double d14 = ImprovedNoise.gradDot(this.p(n9 + 1), d - 1.0, d2 - 1.0, d3 - 1.0);
        return Mth.lerp3(d4, d5, d6, d7, d8, d9, d10, d11, d12, d13, d14);
    }
}

