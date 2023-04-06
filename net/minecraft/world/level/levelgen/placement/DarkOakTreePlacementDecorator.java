/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.EdgeDecorator;

public class DarkOakTreePlacementDecorator
extends EdgeDecorator<NoneDecoratorConfiguration> {
    public DarkOakTreePlacementDecorator(Codec<NoneDecoratorConfiguration> codec) {
        super(codec);
    }

    @Override
    protected Heightmap.Types type(NoneDecoratorConfiguration noneDecoratorConfiguration) {
        return Heightmap.Types.MOTION_BLOCKING;
    }

    @Override
    public Stream<BlockPos> getPositions(DecorationContext decorationContext, Random random, NoneDecoratorConfiguration noneDecoratorConfiguration, BlockPos blockPos) {
        return IntStream.range(0, 16).mapToObj(n -> {
            int n2 = n / 4;
            int n3 = n % 4;
            int n4 = n2 * 4 + 1 + random.nextInt(3) + blockPos.getX();
            int n5 = n3 * 4 + 1 + random.nextInt(3) + blockPos.getZ();
            int n6 = decorationContext.getHeight(this.type(noneDecoratorConfiguration), n4, n5);
            return new BlockPos(n4, n6, n5);
        });
    }
}

