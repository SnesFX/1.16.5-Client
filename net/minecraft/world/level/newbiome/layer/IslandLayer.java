/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer0;

public enum IslandLayer implements AreaTransformer0
{
    INSTANCE;
    

    @Override
    public int applyPixel(Context context, int n, int n2) {
        if (n == 0 && n2 == 0) {
            return 1;
        }
        return context.nextRandom(10) == 0 ? 1 : 0;
    }
}

