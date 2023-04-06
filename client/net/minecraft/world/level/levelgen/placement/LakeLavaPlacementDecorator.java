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
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.ChanceDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class LakeLavaPlacementDecorator
extends FeatureDecorator<ChanceDecoratorConfiguration> {
    public LakeLavaPlacementDecorator(Codec<ChanceDecoratorConfiguration> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecorationContext decorationContext, Random random, ChanceDecoratorConfiguration chanceDecoratorConfiguration, BlockPos blockPos) {
        if (random.nextInt(chanceDecoratorConfiguration.chance / 10) == 0) {
            int n = random.nextInt(16) + blockPos.getX();
            int n2 = random.nextInt(16) + blockPos.getZ();
            int n3 = random.nextInt(random.nextInt(decorationContext.getGenDepth() - 8) + 8);
            if (n3 < decorationContext.getSeaLevel() || random.nextInt(chanceDecoratorConfiguration.chance / 8) == 0) {
                return Stream.of(new BlockPos(n, n3, n2));
            }
        }
        return Stream.empty();
    }
}

