/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer1;
import net.minecraft.world.level.newbiome.layer.traits.DimensionOffset1Transformer;

public interface BishopTransformer
extends AreaTransformer1,
DimensionOffset1Transformer {
    public int apply(Context var1, int var2, int var3, int var4, int var5, int var6);

    @Override
    default public int applyPixel(BigContext<?> bigContext, Area area, int n, int n2) {
        return this.apply(bigContext, area.get(this.getParentX(n + 0), this.getParentY(n2 + 2)), area.get(this.getParentX(n + 2), this.getParentY(n2 + 2)), area.get(this.getParentX(n + 2), this.getParentY(n2 + 0)), area.get(this.getParentX(n + 0), this.getParentY(n2 + 0)), area.get(this.getParentX(n + 1), this.getParentY(n2 + 1)));
    }
}

