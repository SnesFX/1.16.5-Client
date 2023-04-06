/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.chunk;

public class OldDataLayer {
    public final byte[] data;
    private final int depthBits;
    private final int depthBitsPlusFour;

    public OldDataLayer(byte[] arrby, int n) {
        this.data = arrby;
        this.depthBits = n;
        this.depthBitsPlusFour = n + 4;
    }

    public int get(int n, int n2, int n3) {
        int n4 = n << this.depthBitsPlusFour | n3 << this.depthBits | n2;
        int n5 = n4 >> 1;
        int n6 = n4 & 1;
        if (n6 == 0) {
            return this.data[n5] & 0xF;
        }
        return this.data[n5] >> 4 & 0xF;
    }
}

