/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.newbiome.layer.traits.C0Transformer;

public enum RiverInitLayer implements C0Transformer
{
    INSTANCE;
    

    @Override
    public int apply(Context context, int n) {
        return Layers.isShallowOcean(n) ? n : context.nextRandom(299999) + 2;
    }
}

