/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.util;

import net.minecraft.util.Mth;

public class SmoothDouble {
    private double targetValue;
    private double remainingValue;
    private double lastAmount;

    public double getNewDeltaValue(double d, double d2) {
        this.targetValue += d;
        double d3 = this.targetValue - this.remainingValue;
        double d4 = Mth.lerp(0.5, this.lastAmount, d3);
        double d5 = Math.signum(d3);
        if (d5 * d3 > d5 * this.lastAmount) {
            d3 = d4;
        }
        this.lastAmount = d4;
        this.remainingValue += d3 * d2;
        return d3 * d2;
    }

    public void reset() {
        this.targetValue = 0.0;
        this.remainingValue = 0.0;
        this.lastAmount = 0.0;
    }
}

