/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.AbstractDoubleList
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 */
package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class OffsetDoubleList
extends AbstractDoubleList {
    private final DoubleList delegate;
    private final double offset;

    public OffsetDoubleList(DoubleList doubleList, double d) {
        this.delegate = doubleList;
        this.offset = d;
    }

    public double getDouble(int n) {
        return this.delegate.getDouble(n) + this.offset;
    }

    public int size() {
        return this.delegate.size();
    }
}

