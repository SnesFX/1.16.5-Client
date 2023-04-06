/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.core;

import net.minecraft.core.Position;

public class PositionImpl
implements Position {
    protected final double x;
    protected final double y;
    protected final double z;

    public PositionImpl(double d, double d2, double d3) {
        this.x = d;
        this.y = d2;
        this.z = d3;
    }

    @Override
    public double x() {
        return this.x;
    }

    @Override
    public double y() {
        return this.y;
    }

    @Override
    public double z() {
        return this.z;
    }
}

