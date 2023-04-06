/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.chunk;

import javax.annotation.Nullable;
import net.minecraft.Util;

public class DataLayer {
    @Nullable
    protected byte[] data;

    public DataLayer() {
    }

    public DataLayer(byte[] arrby) {
        this.data = arrby;
        if (arrby.length != 2048) {
            throw Util.pauseInIde(new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + arrby.length));
        }
    }

    protected DataLayer(int n) {
        this.data = new byte[n];
    }

    public int get(int n, int n2, int n3) {
        return this.get(this.getIndex(n, n2, n3));
    }

    public void set(int n, int n2, int n3, int n4) {
        this.set(this.getIndex(n, n2, n3), n4);
    }

    protected int getIndex(int n, int n2, int n3) {
        return n2 << 8 | n3 << 4 | n;
    }

    private int get(int n) {
        if (this.data == null) {
            return 0;
        }
        int n2 = this.getPosition(n);
        if (this.isFirst(n)) {
            return this.data[n2] & 0xF;
        }
        return this.data[n2] >> 4 & 0xF;
    }

    private void set(int n, int n2) {
        if (this.data == null) {
            this.data = new byte[2048];
        }
        int n3 = this.getPosition(n);
        this.data[n3] = this.isFirst(n) ? (byte)(this.data[n3] & 0xF0 | n2 & 0xF) : (byte)(this.data[n3] & 0xF | (n2 & 0xF) << 4);
    }

    private boolean isFirst(int n) {
        return (n & 1) == 0;
    }

    private int getPosition(int n) {
        return n >> 1;
    }

    public byte[] getData() {
        if (this.data == null) {
            this.data = new byte[2048];
        }
        return this.data;
    }

    public DataLayer copy() {
        if (this.data == null) {
            return new DataLayer();
        }
        return new DataLayer((byte[])this.data.clone());
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 4096; ++i) {
            stringBuilder.append(Integer.toHexString(this.get(i)));
            if ((i & 0xF) == 15) {
                stringBuilder.append("\n");
            }
            if ((i & 0xFF) != 255) continue;
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public boolean isEmpty() {
        return this.data == null;
    }
}

