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
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class EndGatewayPlacementDecorator
extends FeatureDecorator<NoneDecoratorConfiguration> {
    public EndGatewayPlacementDecorator(Codec<NoneDecoratorConfiguration> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecorationContext decorationContext, Random random, NoneDecoratorConfiguration noneDecoratorConfiguration, BlockPos blockPos) {
        int n;
        int n2;
        int n3;
        if (random.nextInt(700) == 0 && (n3 = decorationContext.getHeight(Heightmap.Types.MOTION_BLOCKING, n2 = random.nextInt(16) + blockPos.getX(), n = random.nextInt(16) + blockPos.getZ())) > 0) {
            int n4 = n3 + 3 + random.nextInt(7);
            return Stream.of(new BlockPos(n2, n4, n));
        }
        return Stream.empty();
    }
}

