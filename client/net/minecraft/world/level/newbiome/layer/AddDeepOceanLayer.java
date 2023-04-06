/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum AddDeepOceanLayer implements CastleTransformer
{
    INSTANCE;
    

    @Override
    public int apply(Context context, int n, int n2, int n3, int n4, int n5) {
        if (Layers.isShallowOcean(n5)) {
            int n6 = 0;
            if (Layers.isShallowOcean(n)) {
                ++n6;
            }
            if (Layers.isShallowOcean(n2)) {
                ++n6;
            }
            if (Layers.isShallowOcean(n4)) {
                ++n6;
            }
            if (Layers.isShallowOcean(n3)) {
                ++n6;
            }
            if (n6 > 3) {
                if (n5 == 44) {
                    return 47;
                }
                if (n5 == 45) {
                    return 48;
                }
                if (n5 == 0) {
                    return 24;
                }
                if (n5 == 46) {
                    return 49;
                }
                if (n5 == 10) {
                    return 50;
                }
                return 24;
            }
        }
        return n5;
    }
}

