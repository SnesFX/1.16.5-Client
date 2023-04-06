/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer2;
import net.minecraft.world.level.newbiome.layer.traits.DimensionOffset0Transformer;

public enum OceanMixerLayer implements AreaTransformer2,
DimensionOffset0Transformer
{
    INSTANCE;
    

    @Override
    public int applyPixel(Context context, Area area, Area area2, int n, int n2) {
        int n3 = area.get(this.getParentX(n), this.getParentY(n2));
        int n4 = area2.get(this.getParentX(n), this.getParentY(n2));
        if (!Layers.isOcean(n3)) {
            return n3;
        }
        int n5 = 8;
        int n6 = 4;
        for (int i = -8; i <= 8; i += 4) {
            for (int j = -8; j <= 8; j += 4) {
                int n7 = area.get(this.getParentX(n + i), this.getParentY(n2 + j));
                if (Layers.isOcean(n7)) continue;
                if (n4 == 44) {
                    return 45;
                }
                if (n4 != 10) continue;
                return 46;
            }
        }
        if (n3 == 24) {
            if (n4 == 45) {
                return 48;
            }
            if (n4 == 0) {
                return 24;
            }
            if (n4 == 46) {
                return 49;
            }
            if (n4 == 10) {
                return 50;
            }
        }
        return n4;
    }
}

