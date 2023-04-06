/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import net.minecraft.world.inventory.ContainerData;

public class SimpleContainerData
implements ContainerData {
    private final int[] ints;

    public SimpleContainerData(int n) {
        this.ints = new int[n];
    }

    @Override
    public int get(int n) {
        return this.ints[n];
    }

    @Override
    public void set(int n, int n2) {
        this.ints[n] = n2;
    }

    @Override
    public int getCount() {
        return this.ints.length;
    }
}

