/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.layer.traits.DimensionTransformer;
import net.minecraft.world.level.newbiome.layer.traits.PixelTransformer;

public interface AreaTransformer1
extends DimensionTransformer {
    default public <R extends Area> AreaFactory<R> run(BigContext<R> bigContext, AreaFactory<R> areaFactory) {
        return () -> {
            Object a = areaFactory.make();
            return bigContext.createResult((n, n2) -> {
                bigContext.initRandom(n, n2);
                return this.applyPixel(bigContext, (Area)a, n, n2);
            }, a);
        };
    }

    public int applyPixel(BigContext<?> var1, Area var2, int var3, int var4);
}

