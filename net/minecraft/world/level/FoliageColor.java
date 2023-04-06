/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level;

public class FoliageColor {
    private static int[] pixels = new int[65536];

    public static void init(int[] arrn) {
        pixels = arrn;
    }

    public static int get(double d, double d2) {
        int n = (int)((1.0 - d) * 255.0);
        int n2 = (int)((1.0 - (d2 *= d)) * 255.0);
        return pixels[n2 << 8 | n];
    }

    public static int getEvergreenColor() {
        return 6396257;
    }

    public static int getBirchColor() {
        return 8431445;
    }

    public static int getDefaultColor() {
        return 4764952;
    }
}

