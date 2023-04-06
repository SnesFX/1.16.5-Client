/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.layer.traits.DimensionTransformer;

public interface DimensionOffset0Transformer
extends DimensionTransformer {
    @Override
    default public int getParentX(int n) {
        return n;
    }

    @Override
    default public int getParentY(int n) {
        return n;
    }
}

