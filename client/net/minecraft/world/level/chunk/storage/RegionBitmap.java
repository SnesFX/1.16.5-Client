/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.chunk.storage;

import java.util.BitSet;

public class RegionBitmap {
    private final BitSet used = new BitSet();

    public void force(int n, int n2) {
        this.used.set(n, n + n2);
    }

    public void free(int n, int n2) {
        this.used.clear(n, n + n2);
    }

    public int allocate(int n) {
        int n2 = 0;
        do {
            int n3;
            int n4;
            if ((n4 = this.used.nextSetBit(n3 = this.used.nextClearBit(n2))) == -1 || n4 - n3 >= n) {
                this.force(n3, n);
                return n3;
            }
            n2 = n4;
        } while (true);
    }
}

