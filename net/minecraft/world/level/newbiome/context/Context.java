/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.context;

import net.minecraft.world.level.levelgen.synth.ImprovedNoise;

public interface Context {
    public int nextRandom(int var1);

    public ImprovedNoise getBiomeNoise();
}

