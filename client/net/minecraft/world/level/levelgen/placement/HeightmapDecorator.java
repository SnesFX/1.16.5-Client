/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.BaseHeightmapDecorator;

public class HeightmapDecorator<DC extends DecoratorConfiguration>
extends BaseHeightmapDecorator<DC> {
    public HeightmapDecorator(Codec<DC> codec) {
        super(codec);
    }

    @Override
    protected Heightmap.Types type(DC DC) {
        return Heightmap.Types.MOTION_BLOCKING;
    }
}

