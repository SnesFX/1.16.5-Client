/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.EdgeDecorator;

public class HeightmapDoubleDecorator<DC extends DecoratorConfiguration>
extends EdgeDecorator<DC> {
    public HeightmapDoubleDecorator(Codec<DC> codec) {
        super(codec);
    }

    @Override
    protected Heightmap.Types type(DC DC) {
        return Heightmap.Types.MOTION_BLOCKING;
    }

    @Override
    public Stream<BlockPos> getPositions(DecorationContext decorationContext, Random random, DC DC, BlockPos blockPos) {
        int n = blockPos.getX();
        int n2 = blockPos.getZ();
        int n3 = decorationContext.getHeight(this.type(DC), n, n2);
        if (n3 == 0) {
            return Stream.of(new BlockPos[0]);
        }
        return Stream.of(new BlockPos(n, random.nextInt(n3 * 2), n2));
    }
}

