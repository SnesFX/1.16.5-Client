/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.renderer;

public class RunningTrimmedMean {
    private final long[] values;
    private int count;
    private int cursor;

    public RunningTrimmedMean(int n) {
        this.values = new long[n];
    }

    public long registerValueAndGetMean(long l) {
        if (this.count < this.values.length) {
            ++this.count;
        }
        this.values[this.cursor] = l;
        this.cursor = (this.cursor + 1) % this.values.length;
        long l2 = Long.MAX_VALUE;
        long l3 = Long.MIN_VALUE;
        long l4 = 0L;
        for (int i = 0; i < this.count; ++i) {
            long l5 = this.values[i];
            l4 += l5;
            l2 = Math.min(l2, l5);
            l3 = Math.max(l3, l5);
        }
        if (this.count > 2) {
            return (l4 -= l2 + l3) / (long)(this.count - 2);
        }
        if (l4 > 0L) {
            return (long)this.count / l4;
        }
        return 0L;
    }
}

