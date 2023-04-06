/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer0;

public enum OceanLayer implements AreaTransformer0
{
    INSTANCE;
    

    @Override
    public int applyPixel(Context context, int n, int n2) {
        ImprovedNoise improvedNoise = context.getBiomeNoise();
        double d = improvedNoise.noise((double)n / 8.0, (double)n2 / 8.0, 0.0, 0.0, 0.0);
        if (d > 0.4) {
            return 44;
        }
        if (d > 0.2) {
            return 45;
        }
        if (d < -0.4) {
            return 10;
        }
        if (d < -0.2) {
            return 46;
        }
        return 0;
    }
}

