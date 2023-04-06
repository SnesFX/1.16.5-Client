/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.area;

import net.minecraft.world.level.newbiome.area.Area;

public interface AreaFactory<A extends Area> {
    public A make();
}

