/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.util.datafix;

import net.minecraft.util.Mth;
import org.apache.commons.lang3.Validate;

public class PackedBitStorage {
    private final long[] data;
    private final int bits;
    private final long mask;
    private final int size;

    public PackedBitStorage(int n, int n2) {
        this(n, n2, new long[Mth.roundUp(n2 * n, 64) / 64]);
    }

    public PackedBitStorage(int n, int n2, long[] arrl) {
        Validate.inclusiveBetween((long)1L, (long)32L, (long)n);
        this.size = n2;
        this.bits = n;
        this.data = arrl;
        this.mask = (1L << n) - 1L;
        int n3 = Mth.roundUp(n2 * n, 64) / 64;
        if (arrl.length != n3) {
            throw new IllegalArgumentException("Invalid length given for storage, got: " + arrl.length + " but expected: " + n3);
        }
    }

    public void set(int n, int n2) {
        Validate.inclusiveBetween((long)0L, (long)(this.size - 1), (long)n);
        Validate.inclusiveBetween((long)0L, (long)this.mask, (long)n2);
        int n3 = n * this.bits;
        int n4 = n3 >> 6;
        int n5 = (n + 1) * this.bits - 1 >> 6;
        int n6 = n3 ^ n4 << 6;
        this.data[n4] = this.data[n4] & (this.mask << n6 ^ 0xFFFFFFFFFFFFFFFFL) | ((long)n2 & this.mask) << n6;
        if (n4 != n5) {
            int n7 = 64 - n6;
            int n8 = this.bits - n7;
            this.data[n5] = this.data[n5] >>> n8 << n8 | ((long)n2 & this.mask) >> n7;
        }
    }

    public int get(int n) {
        Validate.inclusiveBetween((long)0L, (long)(this.size - 1), (long)n);
        int n2 = n * this.bits;
        int n3 = n2 >> 6;
        int n4 = (n + 1) * this.bits - 1 >> 6;
        int n5 = n2 ^ n3 << 6;
        if (n3 == n4) {
            return (int)(this.data[n3] >>> n5 & this.mask);
        }
        int n6 = 64 - n5;
        return (int)((this.data[n3] >>> n5 | this.data[n4] << n6) & this.mask);
    }

    public long[] getRaw() {
        return this.data;
    }

    public int getBits() {
        return this.bits;
    }
}

