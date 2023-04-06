/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer1;
import net.minecraft.world.level.newbiome.layer.traits.DimensionOffset1Transformer;

public interface C1Transformer
extends AreaTransformer1,
DimensionOffset1Transformer {
    public int apply(Context var1, int var2);

    @Override
    default public int applyPixel(BigContext<?> bigContext, Area area, int n, int n2) {
        int n3 = area.get(this.getParentX(n + 1), this.getParentY(n2 + 1));
        return this.apply(bigContext, n3);
    }
}

