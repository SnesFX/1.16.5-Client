/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.doubles.DoubleArrayList
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 */
package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.world.phys.shapes.IndexMerger;

public final class IndirectMerger
implements IndexMerger {
    private final DoubleArrayList result;
    private final IntArrayList firstIndices;
    private final IntArrayList secondIndices;

    protected IndirectMerger(DoubleList doubleList, DoubleList doubleList2, boolean bl, boolean bl2) {
        int n = 0;
        int n2 = 0;
        double d = Double.NaN;
        int n3 = doubleList.size();
        int n4 = doubleList2.size();
        int n5 = n3 + n4;
        this.result = new DoubleArrayList(n5);
        this.firstIndices = new IntArrayList(n5);
        this.secondIndices = new IntArrayList(n5);
        do {
            double d2;
            boolean bl3;
            boolean bl4 = n < n3;
            boolean bl5 = bl3 = n2 < n4;
            if (!bl4 && !bl3) break;
            boolean bl6 = bl4 && (!bl3 || doubleList.getDouble(n) < doubleList2.getDouble(n2) + 1.0E-7);
            double d3 = d2 = bl6 ? doubleList.getDouble(n++) : doubleList2.getDouble(n2++);
            if ((n == 0 || !bl4) && !bl6 && !bl2 || (n2 == 0 || !bl3) && bl6 && !bl) continue;
            if (!(d >= d2 - 1.0E-7)) {
                this.firstIndices.add(n - 1);
                this.secondIndices.add(n2 - 1);
                this.result.add(d2);
                d = d2;
                continue;
            }
            if (this.result.isEmpty()) continue;
            this.firstIndices.set(this.firstIndices.size() - 1, n - 1);
            this.secondIndices.set(this.secondIndices.size() - 1, n2 - 1);
        } while (true);
        if (this.result.isEmpty()) {
            this.result.add(Math.min(doubleList.getDouble(n3 - 1), doubleList2.getDouble(n4 - 1)));
        }
    }

    @Override
    public boolean forMergedIndexes(IndexMerger.IndexConsumer indexConsumer) {
        for (int i = 0; i < this.result.size() - 1; ++i) {
            if (indexConsumer.merge(this.firstIndices.getInt(i), this.secondIndices.getInt(i), i)) continue;
            return false;
        }
        return true;
    }

    @Override
    public DoubleList getList() {
        return this.result;
    }
}

