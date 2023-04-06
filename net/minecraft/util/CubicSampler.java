/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package net.minecraft.util;

import javax.annotation.Nonnull;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class CubicSampler {
    private static final double[] GAUSSIAN_SAMPLE_KERNEL = new double[]{0.0, 1.0, 4.0, 6.0, 4.0, 1.0, 0.0};

    @Nonnull
    public static Vec3 gaussianSampleVec3(Vec3 vec3, Vec3Fetcher vec3Fetcher) {
        int n = Mth.floor(vec3.x());
        int n2 = Mth.floor(vec3.y());
        int n3 = Mth.floor(vec3.z());
        double d = vec3.x() - (double)n;
        double d2 = vec3.y() - (double)n2;
        double d3 = vec3.z() - (double)n3;
        double d4 = 0.0;
        Vec3 vec32 = Vec3.ZERO;
        for (int i = 0; i < 6; ++i) {
            double d5 = Mth.lerp(d, GAUSSIAN_SAMPLE_KERNEL[i + 1], GAUSSIAN_SAMPLE_KERNEL[i]);
            int n4 = n - 2 + i;
            for (int j = 0; j < 6; ++j) {
                double d6 = Mth.lerp(d2, GAUSSIAN_SAMPLE_KERNEL[j + 1], GAUSSIAN_SAMPLE_KERNEL[j]);
                int n5 = n2 - 2 + j;
                for (int k = 0; k < 6; ++k) {
                    double d7 = Mth.lerp(d3, GAUSSIAN_SAMPLE_KERNEL[k + 1], GAUSSIAN_SAMPLE_KERNEL[k]);
                    int n6 = n3 - 2 + k;
                    double d8 = d5 * d6 * d7;
                    d4 += d8;
                    vec32 = vec32.add(vec3Fetcher.fetch(n4, n5, n6).scale(d8));
                }
            }
        }
        vec32 = vec32.scale(1.0 / d4);
        return vec32;
    }

    public static interface Vec3Fetcher {
        public Vec3 fetch(int var1, int var2, int var3);
    }

}

