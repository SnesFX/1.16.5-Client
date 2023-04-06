/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer1;

public enum ZoomLayer implements AreaTransformer1
{
    NORMAL,
    FUZZY{

        @Override
        protected int modeOrRandom(BigContext<?> bigContext, int n, int n2, int n3, int n4) {
            return bigContext.random(n, n2, n3, n4);
        }
    };
    

    @Override
    public int getParentX(int n) {
        return n >> 1;
    }

    @Override
    public int getParentY(int n) {
        return n >> 1;
    }

    @Override
    public int applyPixel(BigContext<?> bigContext, Area area, int n, int n2) {
        int n3 = area.get(this.getParentX(n), this.getParentY(n2));
        bigContext.initRandom(n >> 1 << 1, n2 >> 1 << 1);
        int n4 = n & 1;
        int n5 = n2 & 1;
        if (n4 == 0 && n5 == 0) {
            return n3;
        }
        int n6 = area.get(this.getParentX(n), this.getParentY(n2 + 1));
        int n7 = bigContext.random(n3, n6);
        if (n4 == 0 && n5 == 1) {
            return n7;
        }
        int n8 = area.get(this.getParentX(n + 1), this.getParentY(n2));
        int n9 = bigContext.random(n3, n8);
        if (n4 == 1 && n5 == 0) {
            return n9;
        }
        int n10 = area.get(this.getParentX(n + 1), this.getParentY(n2 + 1));
        return this.modeOrRandom(bigContext, n3, n8, n6, n10);
    }

    protected int modeOrRandom(BigContext<?> bigContext, int n, int n2, int n3, int n4) {
        if (n2 == n3 && n3 == n4) {
            return n2;
        }
        if (n == n2 && n == n3) {
            return n;
        }
        if (n == n2 && n == n4) {
            return n;
        }
        if (n == n3 && n == n4) {
            return n;
        }
        if (n == n2 && n3 != n4) {
            return n;
        }
        if (n == n3 && n2 != n4) {
            return n;
        }
        if (n == n4 && n2 != n3) {
            return n;
        }
        if (n2 == n3 && n != n4) {
            return n2;
        }
        if (n2 == n4 && n != n3) {
            return n2;
        }
        if (n3 == n4 && n != n2) {
            return n3;
        }
        return bigContext.random(n, n2, n3, n4);
    }

}

