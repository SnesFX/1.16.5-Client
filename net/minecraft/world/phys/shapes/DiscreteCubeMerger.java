/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.math.IntMath
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 */
package net.minecraft.world.phys.shapes;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.world.phys.shapes.CubePointRange;
import net.minecraft.world.phys.shapes.IndexMerger;
import net.minecraft.world.phys.shapes.Shapes;

public final class DiscreteCubeMerger
implements IndexMerger {
    private final CubePointRange result;
    private final int firstSize;
    private final int secondSize;
    private final int gcd;

    DiscreteCubeMerger(int n, int n2) {
        this.result = new CubePointRange((int)Shapes.lcm(n, n2));
        this.firstSize = n;
        this.secondSize = n2;
        this.gcd = IntMath.gcd((int)n, (int)n2);
    }

    @Override
    public boolean forMergedIndexes(IndexMerger.IndexConsumer indexConsumer) {
        int n = this.firstSize / this.gcd;
        int n2 = this.secondSize / this.gcd;
        for (int i = 0; i <= this.result.size(); ++i) {
            if (indexConsumer.merge(i / n2, i / n, i)) continue;
            return false;
        }
        return true;
    }

    @Override
    public DoubleList getList() {
        return this.result;
    }
}

