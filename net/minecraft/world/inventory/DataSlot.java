/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.inventory;

import net.minecraft.world.inventory.ContainerData;

public abstract class DataSlot {
    private int prevValue;

    public static DataSlot forContainer(final ContainerData containerData, final int n) {
        return new DataSlot(){

            @Override
            public int get() {
                return containerData.get(n);
            }

            @Override
            public void set(int n2) {
                containerData.set(n, n2);
            }
        };
    }

    public static DataSlot shared(final int[] arrn, final int n) {
        return new DataSlot(){

            @Override
            public int get() {
                return arrn[n];
            }

            @Override
            public void set(int n2) {
                arrn[n] = n2;
            }
        };
    }

    public static DataSlot standalone() {
        return new DataSlot(){
            private int value;

            @Override
            public int get() {
                return this.value;
            }

            @Override
            public void set(int n) {
                this.value = n;
            }
        };
    }

    public abstract int get();

    public abstract void set(int var1);

    public boolean checkAndClearUpdateFlag() {
        int n = this.get();
        boolean bl = n != this.prevValue;
        this.prevValue = n;
        return bl;
    }

}

