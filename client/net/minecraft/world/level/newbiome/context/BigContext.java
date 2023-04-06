/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.context;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.PixelTransformer;

public interface BigContext<R extends Area>
extends Context {
    public void initRandom(long var1, long var3);

    public R createResult(PixelTransformer var1);

    default public R createResult(PixelTransformer pixelTransformer, R r) {
        return this.createResult(pixelTransformer);
    }

    default public R createResult(PixelTransformer pixelTransformer, R r, R r2) {
        return this.createResult(pixelTransformer);
    }

    default public int random(int n, int n2) {
        return this.nextRandom(2) == 0 ? n : n2;
    }

    default public int random(int n, int n2, int n3, int n4) {
        int n5 = this.nextRandom(4);
        if (n5 == 0) {
            return n;
        }
        if (n5 == 1) {
            return n2;
        }
        if (n5 == 2) {
            return n3;
        }
        return n4;
    }
}

