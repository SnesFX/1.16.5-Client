/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 */
package net.minecraft.world.level.newbiome.layer;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum ShoreLayer implements CastleTransformer
{
    INSTANCE;
    
    private static final IntSet SNOWY;
    private static final IntSet JUNGLES;

    @Override
    public int apply(Context context, int n, int n2, int n3, int n4, int n5) {
        if (n5 == 14) {
            if (Layers.isShallowOcean(n) || Layers.isShallowOcean(n2) || Layers.isShallowOcean(n3) || Layers.isShallowOcean(n4)) {
                return 15;
            }
        } else if (JUNGLES.contains(n5)) {
            if (!(ShoreLayer.isJungleCompatible(n) && ShoreLayer.isJungleCompatible(n2) && ShoreLayer.isJungleCompatible(n3) && ShoreLayer.isJungleCompatible(n4))) {
                return 23;
            }
            if (Layers.isOcean(n) || Layers.isOcean(n2) || Layers.isOcean(n3) || Layers.isOcean(n4)) {
                return 16;
            }
        } else if (n5 == 3 || n5 == 34 || n5 == 20) {
            if (!Layers.isOcean(n5) && (Layers.isOcean(n) || Layers.isOcean(n2) || Layers.isOcean(n3) || Layers.isOcean(n4))) {
                return 25;
            }
        } else if (SNOWY.contains(n5)) {
            if (!Layers.isOcean(n5) && (Layers.isOcean(n) || Layers.isOcean(n2) || Layers.isOcean(n3) || Layers.isOcean(n4))) {
                return 26;
            }
        } else if (n5 == 37 || n5 == 38) {
            if (!(Layers.isOcean(n) || Layers.isOcean(n2) || Layers.isOcean(n3) || Layers.isOcean(n4) || this.isMesa(n) && this.isMesa(n2) && this.isMesa(n3) && this.isMesa(n4))) {
                return 2;
            }
        } else if (!Layers.isOcean(n5) && n5 != 7 && n5 != 6 && (Layers.isOcean(n) || Layers.isOcean(n2) || Layers.isOcean(n3) || Layers.isOcean(n4))) {
            return 16;
        }
        return n5;
    }

    private static boolean isJungleCompatible(int n) {
        return JUNGLES.contains(n) || n == 4 || n == 5 || Layers.isOcean(n);
    }

    private boolean isMesa(int n) {
        return n == 37 || n == 38 || n == 39 || n == 165 || n == 166 || n == 167;
    }

    static {
        SNOWY = new IntOpenHashSet(new int[]{26, 11, 12, 13, 140, 30, 31, 158, 10});
        JUNGLES = new IntOpenHashSet(new int[]{168, 169, 21, 22, 23, 149, 151});
    }
}

