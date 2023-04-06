/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum RiverLayer implements CastleTransformer
{
    INSTANCE;
    

    @Override
    public int apply(Context context, int n, int n2, int n3, int n4, int n5) {
        int n6 = RiverLayer.riverFilter(n5);
        if (n6 == RiverLayer.riverFilter(n4) && n6 == RiverLayer.riverFilter(n) && n6 == RiverLayer.riverFilter(n2) && n6 == RiverLayer.riverFilter(n3)) {
            return -1;
        }
        return 7;
    }

    private static int riverFilter(int n) {
        if (n >= 2) {
            return 2 + (n & 1);
        }
        return n;
    }
}

