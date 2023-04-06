/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.newbiome.layer.traits.BishopTransformer;

public enum AddMushroomIslandLayer implements BishopTransformer
{
    INSTANCE;
    

    @Override
    public int apply(Context context, int n, int n2, int n3, int n4, int n5) {
        if (Layers.isShallowOcean(n5) && Layers.isShallowOcean(n4) && Layers.isShallowOcean(n) && Layers.isShallowOcean(n3) && Layers.isShallowOcean(n2) && context.nextRandom(100) == 0) {
            return 14;
        }
        return n5;
    }
}

