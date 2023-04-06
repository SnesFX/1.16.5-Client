/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.util;

import java.util.Random;

public class IntRange {
    private final int minInclusive;
    private final int maxInclusive;

    public IntRange(int n, int n2) {
        if (n2 < n) {
            throw new IllegalArgumentException("max must be >= minInclusive! Given minInclusive: " + n + ", Given max: " + n2);
        }
        this.minInclusive = n;
        this.maxInclusive = n2;
    }

    public static IntRange of(int n, int n2) {
        return new IntRange(n, n2);
    }

    public int randomValue(Random random) {
        if (this.minInclusive == this.maxInclusive) {
            return this.minInclusive;
        }
        return random.nextInt(this.maxInclusive - this.minInclusive + 1) + this.minInclusive;
    }

    public int getMinInclusive() {
        return this.minInclusive;
    }

    public int getMaxInclusive() {
        return this.maxInclusive;
    }

    public String toString() {
        return "IntRange[" + this.minInclusive + "-" + this.maxInclusive + "]";
    }
}

