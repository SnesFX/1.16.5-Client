/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.util;

import java.util.function.IntConsumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import org.apache.commons.lang3.Validate;

public class BitStorage {
    private static final int[] MAGIC = new int[]{-1, -1, 0, Integer.MIN_VALUE, 0, 0, 1431655765, 1431655765, 0, Integer.MIN_VALUE, 0, 1, 858993459, 858993459, 0, 715827882, 715827882, 0, 613566756, 613566756, 0, Integer.MIN_VALUE, 0, 2, 477218588, 477218588, 0, 429496729, 429496729, 0, 390451572, 390451572, 0, 357913941, 357913941, 0, 330382099, 330382099, 0, 306783378, 306783378, 0, 286331153, 286331153, 0, Integer.MIN_VALUE, 0, 3, 252645135, 252645135, 0, 238609294, 238609294, 0, 226050910, 226050910, 0, 214748364, 214748364, 0, 204522252, 204522252, 0, 195225786, 195225786, 0, 186737708, 186737708, 0, 178956970, 178956970, 0, 171798691, 171798691, 0, 165191049, 165191049, 0, 159072862, 159072862, 0, 153391689, 153391689, 0, 148102320, 148102320, 0, 143165576, 143165576, 0, 138547332, 138547332, 0, Integer.MIN_VALUE, 0, 4, 130150524, 130150524, 0, 126322567, 126322567, 0, 122713351, 122713351, 0, 119304647, 119304647, 0, 116080197, 116080197, 0, 113025455, 113025455, 0, 110127366, 110127366, 0, 107374182, 107374182, 0, 104755299, 104755299, 0, 102261126, 102261126, 0, 99882960, 99882960, 0, 97612893, 97612893, 0, 95443717, 95443717, 0, 93368854, 93368854, 0, 91382282, 91382282, 0, 89478485, 89478485, 0, 87652393, 87652393, 0, 85899345, 85899345, 0, 84215045, 84215045, 0, 82595524, 82595524, 0, 81037118, 81037118, 0, 79536431, 79536431, 0, 78090314, 78090314, 0, 76695844, 76695844, 0, 75350303, 75350303, 0, 74051160, 74051160, 0, 72796055, 72796055, 0, 71582788, 71582788, 0, 70409299, 70409299, 0, 69273666, 69273666, 0, 68174084, 68174084, 0, Integer.MIN_VALUE, 0, 5};
    private final long[] data;
    private final int bits;
    private final long mask;
    private final int size;
    private final int valuesPerLong;
    private final int divideMul;
    private final int divideAdd;
    private final int divideShift;

    public BitStorage(int n, int n2) {
        this(n, n2, null);
    }

    public BitStorage(int n, int n2, @Nullable long[] arrl) {
        Validate.inclusiveBetween((long)1L, (long)32L, (long)n);
        this.size = n2;
        this.bits = n;
        this.mask = (1L << n) - 1L;
        this.valuesPerLong = (char)(64 / n);
        int n3 = 3 * (this.valuesPerLong - 1);
        this.divideMul = MAGIC[n3 + 0];
        this.divideAdd = MAGIC[n3 + 1];
        this.divideShift = MAGIC[n3 + 2];
        int n4 = (n2 + this.valuesPerLong - 1) / this.valuesPerLong;
        if (arrl != null) {
            if (arrl.length != n4) {
                throw Util.pauseInIde(new RuntimeException("Invalid length given for storage, got: " + arrl.length + " but expected: " + n4));
            }
            this.data = arrl;
        } else {
            this.data = new long[n4];
        }
    }

    private int cellIndex(int n) {
        long l = Integer.toUnsignedLong(this.divideMul);
        long l2 = Integer.toUnsignedLong(this.divideAdd);
        return (int)((long)n * l + l2 >> 32 >> this.divideShift);
    }

    public int getAndSet(int n, int n2) {
        Validate.inclusiveBetween((long)0L, (long)(this.size - 1), (long)n);
        Validate.inclusiveBetween((long)0L, (long)this.mask, (long)n2);
        int n3 = this.cellIndex(n);
        long l = this.data[n3];
        int n4 = (n - n3 * this.valuesPerLong) * this.bits;
        int n5 = (int)(l >> n4 & this.mask);
        this.data[n3] = l & (this.mask << n4 ^ 0xFFFFFFFFFFFFFFFFL) | ((long)n2 & this.mask) << n4;
        return n5;
    }

    public void set(int n, int n2) {
        Validate.inclusiveBetween((long)0L, (long)(this.size - 1), (long)n);
        Validate.inclusiveBetween((long)0L, (long)this.mask, (long)n2);
        int n3 = this.cellIndex(n);
        long l = this.data[n3];
        int n4 = (n - n3 * this.valuesPerLong) * this.bits;
        this.data[n3] = l & (this.mask << n4 ^ 0xFFFFFFFFFFFFFFFFL) | ((long)n2 & this.mask) << n4;
    }

    public int get(int n) {
        Validate.inclusiveBetween((long)0L, (long)(this.size - 1), (long)n);
        int n2 = this.cellIndex(n);
        long l = this.data[n2];
        int n3 = (n - n2 * this.valuesPerLong) * this.bits;
        return (int)(l >> n3 & this.mask);
    }

    public long[] getRaw() {
        return this.data;
    }

    public int getSize() {
        return this.size;
    }

    public void getAll(IntConsumer intConsumer) {
        int n = 0;
        for (long l : this.data) {
            for (int i = 0; i < this.valuesPerLong; ++i) {
                intConsumer.accept((int)(l & this.mask));
                l >>= this.bits;
                if (++n < this.size) continue;
                return;
            }
        }
    }
}

