/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.model;

public class ModelUtils {
    public static float rotlerpRad(float f, float f2, float f3) {
        float f4;
        for (f4 = f2 - f; f4 < -3.1415927f; f4 += 6.2831855f) {
        }
        while (f4 >= 3.1415927f) {
            f4 -= 6.2831855f;
        }
        return f + f3 * f4;
    }
}

