/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum SmoothLayer implements CastleTransformer
{
    INSTANCE;
    

    @Override
    public int apply(Context context, int n, int n2, int n3, int n4, int n5) {
        boolean bl;
        boolean bl2 = n2 == n4;
        boolean bl3 = bl = n == n3;
        if (bl2 == bl) {
            if (bl2) {
                return context.nextRandom(2) == 0 ? n4 : n;
            }
            return n5;
        }
        return bl2 ? n4 : n;
    }
}

