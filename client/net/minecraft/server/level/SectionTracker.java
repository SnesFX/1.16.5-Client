/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.server.level;

import net.minecraft.core.SectionPos;
import net.minecraft.world.level.lighting.DynamicGraphMinFixedPoint;

public abstract class SectionTracker
extends DynamicGraphMinFixedPoint {
    protected SectionTracker(int n, int n2, int n3) {
        super(n, n2, n3);
    }

    @Override
    protected boolean isSource(long l) {
        return l == Long.MAX_VALUE;
    }

    @Override
    protected void checkNeighborsAfterUpdate(long l, int n, boolean bl) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    long l2 = SectionPos.offset(l, i, j, k);
                    if (l2 == l) continue;
                    this.checkNeighbor(l, l2, n, bl);
                }
            }
        }
    }

    @Override
    protected int getComputedLevel(long l, long l2, int n) {
        int n2 = n;
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    long l3 = SectionPos.offset(l, i, j, k);
                    if (l3 == l) {
                        l3 = Long.MAX_VALUE;
                    }
                    if (l3 == l2) continue;
                    int n3 = this.computeLevelFromNeighbor(l3, l, this.getLevel(l3));
                    if (n2 > n3) {
                        n2 = n3;
                    }
                    if (n2 != 0) continue;
                    return n2;
                }
            }
        }
        return n2;
    }

    @Override
    protected int computeLevelFromNeighbor(long l, long l2, int n) {
        if (l == Long.MAX_VALUE) {
            return this.getLevelFromSource(l2);
        }
        return n + 1;
    }

    protected abstract int getLevelFromSource(long var1);

    public void update(long l, int n, boolean bl) {
        this.checkEdge(Long.MAX_VALUE, l, n, bl);
    }
}

