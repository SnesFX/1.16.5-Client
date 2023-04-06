/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.newbiome.layer.traits.C0Transformer;
import net.minecraft.world.level.newbiome.layer.traits.CastleTransformer;

public class AddEdgeLayer {

    public static enum IntroduceSpecial implements C0Transformer
    {
        INSTANCE;
        

        @Override
        public int apply(Context context, int n) {
            if (!Layers.isShallowOcean(n) && context.nextRandom(13) == 0) {
                n |= 1 + context.nextRandom(15) << 8 & 0xF00;
            }
            return n;
        }
    }

    public static enum HeatIce implements CastleTransformer
    {
        INSTANCE;
        

        @Override
        public int apply(Context context, int n, int n2, int n3, int n4, int n5) {
            if (n5 == 4 && (n == 1 || n2 == 1 || n4 == 1 || n3 == 1 || n == 2 || n2 == 2 || n4 == 2 || n3 == 2)) {
                return 3;
            }
            return n5;
        }
    }

    public static enum CoolWarm implements CastleTransformer
    {
        INSTANCE;
        

        @Override
        public int apply(Context context, int n, int n2, int n3, int n4, int n5) {
            if (n5 == 1 && (n == 3 || n2 == 3 || n4 == 3 || n3 == 3 || n == 4 || n2 == 4 || n4 == 4 || n3 == 4)) {
                return 2;
            }
            return n5;
        }
    }

}

