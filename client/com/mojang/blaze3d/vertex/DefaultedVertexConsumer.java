/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.vertex.VertexConsumer;

public abstract class DefaultedVertexConsumer
implements VertexConsumer {
    protected boolean defaultColorSet = false;
    protected int defaultR = 255;
    protected int defaultG = 255;
    protected int defaultB = 255;
    protected int defaultA = 255;

    public void defaultColor(int n, int n2, int n3, int n4) {
        this.defaultR = n;
        this.defaultG = n2;
        this.defaultB = n3;
        this.defaultA = n4;
        this.defaultColorSet = true;
    }
}

