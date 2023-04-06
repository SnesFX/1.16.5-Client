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
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.BaseHeightmapDecorator;

public class HeightMapWorldSurfaceDecorator
extends BaseHeightmapDecorator<NoneDecoratorConfiguration> {
    public HeightMapWorldSurfaceDecorator(Codec<NoneDecoratorConfiguration> codec) {
        super(codec);
    }

    @Override
    protected Heightmap.Types type(NoneDecoratorConfiguration noneDecoratorConfiguration) {
        return Heightmap.Types.WORLD_SURFACE_WG;
    }
}

