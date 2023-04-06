/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.lighting.LayerLightEventListener;
import net.minecraft.world.level.lighting.LevelLightEngine;

public interface BlockAndTintGetter
extends BlockGetter {
    public float getShade(Direction var1, boolean var2);

    public LevelLightEngine getLightEngine();

    public int getBlockTint(BlockPos var1, ColorResolver var2);

    default public int getBrightness(LightLayer lightLayer, BlockPos blockPos) {
        return this.getLightEngine().getLayerListener(lightLayer).getLightValue(blockPos);
    }

    default public int getRawBrightness(BlockPos blockPos, int n) {
        return this.getLightEngine().getRawBrightness(blockPos, n);
    }

    default public boolean canSeeSky(BlockPos blockPos) {
        return this.getBrightness(LightLayer.SKY, blockPos) >= this.getMaxLightLevel();
    }
}

