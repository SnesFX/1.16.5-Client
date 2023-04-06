/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.lighting;

import net.minecraft.world.level.chunk.DataLayer;

public class FlatDataLayer
extends DataLayer {
    public FlatDataLayer() {
        super(128);
    }

    public FlatDataLayer(DataLayer dataLayer, int n) {
        super(128);
        System.arraycopy(dataLayer.getData(), n * 128, this.data, 0, 128);
    }

    @Override
    protected int getIndex(int n, int n2, int n3) {
        return n3 << 4 | n;
    }

    @Override
    public byte[] getData() {
        byte[] arrby = new byte[2048];
        for (int i = 0; i < 16; ++i) {
            System.arraycopy(this.data, 0, arrby, i * 128, 128);
        }
        return arrby;
    }
}

