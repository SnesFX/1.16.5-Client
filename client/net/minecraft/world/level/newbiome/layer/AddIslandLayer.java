/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.newbiome.layer.traits.BishopTransformer;

public enum AddIslandLayer implements BishopTransformer
{
    INSTANCE;
    

    @Override
    public int apply(Context context, int n, int n2, int n3, int n4, int n5) {
        if (!(!Layers.isShallowOcean(n5) || Layers.isShallowOcean(n4) && Layers.isShallowOcean(n3) && Layers.isShallowOcean(n) && Layers.isShallowOcean(n2))) {
            int n6 = 1;
            int n7 = 1;
            if (!Layers.isShallowOcean(n4) && context.nextRandom(n6++) == 0) {
                n7 = n4;
            }
            if (!Layers.isShallowOcean(n3) && context.nextRandom(n6++) == 0) {
                n7 = n3;
            }
            if (!Layers.isShallowOcean(n) && context.nextRandom(n6++) == 0) {
                n7 = n;
            }
            if (!Layers.isShallowOcean(n2) && context.nextRandom(n6++) == 0) {
                n7 = n2;
            }
            if (context.nextRandom(3) == 0) {
                return n7;
            }
            return n7 == 4 ? 4 : n5;
        }
        if (!Layers.isShallowOcean(n5) && (Layers.isShallowOcean(n4) || Layers.isShallowOcean(n) || Layers.isShallowOcean(n3) || Layers.isShallowOcean(n2)) && context.nextRandom(5) == 0) {
            if (Layers.isShallowOcean(n4)) {
                return n5 == 4 ? 4 : n4;
            }
            if (Layers.isShallowOcean(n)) {
                return n5 == 4 ? 4 : n;
            }
            if (Layers.isShallowOcean(n3)) {
                return n5 == 4 ? 4 : n3;
            }
            if (Layers.isShallowOcean(n2)) {
                return n5 == 4 ? 4 : n2;
            }
        }
        return n5;
    }
}

