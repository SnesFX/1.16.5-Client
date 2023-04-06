/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.util;

public class FrameTimer {
    private final long[] loggedTimes = new long[240];
    private int logStart;
    private int logLength;
    private int logEnd;

    public void logFrameDuration(long l) {
        this.loggedTimes[this.logEnd] = l;
        ++this.logEnd;
        if (this.logEnd == 240) {
            this.logEnd = 0;
        }
        if (this.logLength < 240) {
            this.logStart = 0;
            ++this.logLength;
        } else {
            this.logStart = this.wrapIndex(this.logEnd + 1);
        }
    }

    public int scaleSampleTo(long l, int n, int n2) {
        double d = (double)l / (double)(1000000000L / (long)n2);
        return (int)(d * (double)n);
    }

    public int getLogStart() {
        return this.logStart;
    }

    public int getLogEnd() {
        return this.logEnd;
    }

    public int wrapIndex(int n) {
        return n % 240;
    }

    public long[] getLog() {
        return this.loggedTimes;
    }
}

