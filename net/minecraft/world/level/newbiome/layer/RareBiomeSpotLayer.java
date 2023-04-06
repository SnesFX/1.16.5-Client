/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.C1Transformer;

public enum RareBiomeSpotLayer implements C1Transformer
{
    INSTANCE;
    

    @Override
    public int apply(Context context, int n) {
        if (context.nextRandom(57) == 0 && n == 1) {
            return 129;
        }
        return n;
    }
}

