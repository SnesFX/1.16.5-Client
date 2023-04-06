/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.levelgen.synth;

import java.util.Random;
import net.minecraft.util.Mth;

public class SimplexNoise {
    protected static final int[][] GRADIENT = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}, {1, 1, 0}, {0, -1, 1}, {-1, 1, 0}, {0, -1, -1}};
    private static final double SQRT_3 = Math.sqrt(3.0);
    private static final double F2 = 0.5 * (SQRT_3 - 1.0);
    private static final double G2 = (3.0 - SQRT_3) / 6.0;
    private final int[] p = new int[512];
    public final double xo;
    public final double yo;
    public final double zo;

    public SimplexNoise(Random random) {
        int n;
        this.xo = random.nextDouble() * 256.0;
        this.yo = random.nextDouble() * 256.0;
        this.zo = random.nextDouble() * 256.0;
        for (n = 0; n < 256; ++n) {
            this.p[n] = n;
        }
        for (n = 0; n < 256; ++n) {
            int n2 = random.nextInt(256 - n);
            int n3 = this.p[n];
            this.p[n] = this.p[n2 + n];
            this.p[n2 + n] = n3;
        }
    }

    private int p(int n) {
        return this.p[n & 0xFF];
    }

    protected static double dot(int[] arrn, double d, double d2, double d3) {
        return (double)arrn[0] * d + (double)arrn[1] * d2 + (double)arrn[2] * d3;
    }

    private double getCornerNoise3D(int n, double d, double d2, double d3, double d4) {
        double d5;
        double d6 = d4 - d * d - d2 * d2 - d3 * d3;
        if (d6 < 0.0) {
            d5 = 0.0;
        } else {
            d6 *= d6;
            d5 = d6 * d6 * SimplexNoise.dot(GRADIENT[n], d, d2, d3);
        }
        return d5;
    }

    public double getValue(double d, double d2) {
        double d3;
        int n;
        double d4;
        double d5;
        int n2;
        int n3;
        double d6 = (d + d2) * F2;
        int n4 = Mth.floor(d + d6);
        double d7 = (double)n4 - (d4 = (double)(n4 + (n3 = Mth.floor(d2 + d6))) * G2);
        double d8 = d - d7;
        if (d8 > (d3 = d2 - (d5 = (double)n3 - d4))) {
            n = 1;
            n2 = 0;
        } else {
            n = 0;
            n2 = 1;
        }
        double d9 = d8 - (double)n + G2;
        double d10 = d3 - (double)n2 + G2;
        double d11 = d8 - 1.0 + 2.0 * G2;
        double d12 = d3 - 1.0 + 2.0 * G2;
        int n5 = n4 & 0xFF;
        int n6 = n3 & 0xFF;
        int n7 = this.p(n5 + this.p(n6)) % 12;
        int n8 = this.p(n5 + n + this.p(n6 + n2)) % 12;
        int n9 = this.p(n5 + 1 + this.p(n6 + 1)) % 12;
        double d13 = this.getCornerNoise3D(n7, d8, d3, 0.0, 0.5);
        double d14 = this.getCornerNoise3D(n8, d9, d10, 0.0, 0.5);
        double d15 = this.getCornerNoise3D(n9, d11, d12, 0.0, 0.5);
        return 70.0 * (d13 + d14 + d15);
    }

    public double getValue(double d, double d2, double d3) {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        int n6;
        double d4 = 0.3333333333333333;
        double d5 = (d + d2 + d3) * 0.3333333333333333;
        int n7 = Mth.floor(d + d5);
        int n8 = Mth.floor(d2 + d5);
        int n9 = Mth.floor(d3 + d5);
        double d6 = 0.16666666666666666;
        double d7 = (double)(n7 + n8 + n9) * 0.16666666666666666;
        double d8 = (double)n7 - d7;
        double d9 = (double)n8 - d7;
        double d10 = (double)n9 - d7;
        double d11 = d - d8;
        double d12 = d2 - d9;
        double d13 = d3 - d10;
        if (d11 >= d12) {
            if (d12 >= d13) {
                n6 = 1;
                n = 0;
                n4 = 0;
                n3 = 1;
                n2 = 1;
                n5 = 0;
            } else if (d11 >= d13) {
                n6 = 1;
                n = 0;
                n4 = 0;
                n3 = 1;
                n2 = 0;
                n5 = 1;
            } else {
                n6 = 0;
                n = 0;
                n4 = 1;
                n3 = 1;
                n2 = 0;
                n5 = 1;
            }
        } else if (d12 < d13) {
            n6 = 0;
            n = 0;
            n4 = 1;
            n3 = 0;
            n2 = 1;
            n5 = 1;
        } else if (d11 < d13) {
            n6 = 0;
            n = 1;
            n4 = 0;
            n3 = 0;
            n2 = 1;
            n5 = 1;
        } else {
            n6 = 0;
            n = 1;
            n4 = 0;
            n3 = 1;
            n2 = 1;
            n5 = 0;
        }
        double d14 = d11 - (double)n6 + 0.16666666666666666;
        double d15 = d12 - (double)n + 0.16666666666666666;
        double d16 = d13 - (double)n4 + 0.16666666666666666;
        double d17 = d11 - (double)n3 + 0.3333333333333333;
        double d18 = d12 - (double)n2 + 0.3333333333333333;
        double d19 = d13 - (double)n5 + 0.3333333333333333;
        double d20 = d11 - 1.0 + 0.5;
        double d21 = d12 - 1.0 + 0.5;
        double d22 = d13 - 1.0 + 0.5;
        int n10 = n7 & 0xFF;
        int n11 = n8 & 0xFF;
        int n12 = n9 & 0xFF;
        int n13 = this.p(n10 + this.p(n11 + this.p(n12))) % 12;
        int n14 = this.p(n10 + n6 + this.p(n11 + n + this.p(n12 + n4))) % 12;
        int n15 = this.p(n10 + n3 + this.p(n11 + n2 + this.p(n12 + n5))) % 12;
        int n16 = this.p(n10 + 1 + this.p(n11 + 1 + this.p(n12 + 1))) % 12;
        double d23 = this.getCornerNoise3D(n13, d11, d12, d13, 0.6);
        double d24 = this.getCornerNoise3D(n14, d14, d15, d16, 0.6);
        double d25 = this.getCornerNoise3D(n15, d17, d18, d19, 0.6);
        double d26 = this.getCornerNoise3D(n16, d20, d21, d22, 0.6);
        return 32.0 * (d23 + d24 + d25 + d26);
    }
}

