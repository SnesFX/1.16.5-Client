/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.newbiome.layer.traits.C1Transformer;

public enum AddSnowLayer implements C1Transformer
{
    INSTANCE;
    

    @Override
    public int apply(Context context, int n) {
        if (Layers.isShallowOcean(n)) {
            return n;
        }
        int n2 = context.nextRandom(6);
        if (n2 == 0) {
            return 4;
        }
        if (n2 == 1) {
            return 3;
        }
        return 1;
    }
}

