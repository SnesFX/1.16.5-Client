/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.DimensionTransformer;
import net.minecraft.world.level.newbiome.layer.traits.PixelTransformer;

public interface AreaTransformer2
extends DimensionTransformer {
    default public <R extends Area> AreaFactory<R> run(BigContext<R> bigContext, AreaFactory<R> areaFactory, AreaFactory<R> areaFactory2) {
        return () -> {
            Object a = areaFactory.make();
            Object a2 = areaFactory2.make();
            return bigContext.createResult((n, n2) -> {
                bigContext.initRandom(n, n2);
                return this.applyPixel(bigContext, (Area)a, (Area)a2, n, n2);
            }, a, a2);
        };
    }

    public int applyPixel(Context var1, Area var2, Area var3, int var4, int var5);
}

