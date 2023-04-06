/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum RemoveTooMuchOceanLayer implements CastleTransformer
{
    INSTANCE;
    

    @Override
    public int apply(Context context, int n, int n2, int n3, int n4, int n5) {
        if (Layers.isShallowOcean(n5) && Layers.isShallowOcean(n) && Layers.isShallowOcean(n2) && Layers.isShallowOcean(n4) && Layers.isShallowOcean(n3) && context.nextRandom(2) == 0) {
            return 1;
        }
        return n5;
    }
}

