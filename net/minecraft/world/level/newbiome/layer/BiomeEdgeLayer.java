/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public enum BiomeEdgeLayer implements CastleTransformer
{
    INSTANCE;
    

    @Override
    public int apply(Context context, int n, int n2, int n3, int n4, int n5) {
        int[] arrn = new int[1];
        if (this.checkEdge(arrn, n5) || this.checkEdgeStrict(arrn, n, n2, n3, n4, n5, 38, 37) || this.checkEdgeStrict(arrn, n, n2, n3, n4, n5, 39, 37) || this.checkEdgeStrict(arrn, n, n2, n3, n4, n5, 32, 5)) {
            return arrn[0];
        }
        if (n5 == 2 && (n == 12 || n2 == 12 || n4 == 12 || n3 == 12)) {
            return 34;
        }
        if (n5 == 6) {
            if (n == 2 || n2 == 2 || n4 == 2 || n3 == 2 || n == 30 || n2 == 30 || n4 == 30 || n3 == 30 || n == 12 || n2 == 12 || n4 == 12 || n3 == 12) {
                return 1;
            }
            if (n == 21 || n3 == 21 || n2 == 21 || n4 == 21 || n == 168 || n3 == 168 || n2 == 168 || n4 == 168) {
                return 23;
            }
        }
        return n5;
    }

    private boolean checkEdge(int[] arrn, int n) {
        if (!Layers.isSame(n, 3)) {
            return false;
        }
        arrn[0] = n;
        return true;
    }

    private boolean checkEdgeStrict(int[] arrn, int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        if (n5 != n6) {
            return false;
        }
        arrn[0] = Layers.isSame(n, n6) && Layers.isSame(n2, n6) && Layers.isSame(n4, n6) && Layers.isSame(n3, n6) ? n5 : n7;
        return true;
    }
}

