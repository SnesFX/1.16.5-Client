/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.AbstractDoubleList
 */
package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class CubePointRange
extends AbstractDoubleList {
    private final int parts;

    CubePointRange(int n) {
        this.parts = n;
    }

    public double getDouble(int n) {
        return (double)n / (double)this.parts;
    }

    public int size() {
        return this.parts + 1;
    }
}

