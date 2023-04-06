/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client;

public class Timer {
    public float partialTick;
    public float tickDelta;
    private long lastMs;
    private final float msPerTick;

    public Timer(float f, long l) {
        this.msPerTick = 1000.0f / f;
        this.lastMs = l;
    }

    public int advanceTime(long l) {
        this.tickDelta = (float)(l - this.lastMs) / this.msPerTick;
        this.lastMs = l;
        this.partialTick += this.tickDelta;
        int n = (int)this.partialTick;
        this.partialTick -= (float)n;
        return n;
    }
}

