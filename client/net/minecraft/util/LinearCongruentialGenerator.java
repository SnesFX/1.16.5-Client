/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.util;

public class LinearCongruentialGenerator {
    public static long next(long l, long l2) {
        l *= l * 6364136223846793005L + 1442695040888963407L;
        return l += l2;
    }
}

