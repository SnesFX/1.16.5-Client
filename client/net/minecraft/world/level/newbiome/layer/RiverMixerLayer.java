/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer2;
import net.minecraft.world.level.newbiome.layer.traits.DimensionOffset0Transformer;

public enum RiverMixerLayer implements AreaTransformer2,
DimensionOffset0Transformer
{
    INSTANCE;
    

    @Override
    public int applyPixel(Context context, Area area, Area area2, int n, int n2) {
        int n3 = area.get(this.getParentX(n), this.getParentY(n2));
        int n4 = area2.get(this.getParentX(n), this.getParentY(n2));
        if (Layers.isOcean(n3)) {
            return n3;
        }
        if (n4 == 7) {
            if (n3 == 12) {
                return 11;
            }
            if (n3 == 14 || n3 == 15) {
                return 15;
            }
            return n4 & 0xFF;
        }
        return n3;
    }
}

