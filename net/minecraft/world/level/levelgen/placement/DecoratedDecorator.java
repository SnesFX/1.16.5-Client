/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.ConfiguredDecorator;
import net.minecraft.world.level.levelgen.placement.DecoratedDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class DecoratedDecorator
extends FeatureDecorator<DecoratedDecoratorConfiguration> {
    public DecoratedDecorator(Codec<DecoratedDecoratorConfiguration> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecorationContext decorationContext, Random random, DecoratedDecoratorConfiguration decoratedDecoratorConfiguration, BlockPos blockPos2) {
        return decoratedDecoratorConfiguration.outer().getPositions(decorationContext, random, blockPos2).flatMap(blockPos -> decoratedDecoratorConfiguration.inner().getPositions(decorationContext, random, (BlockPos)blockPos));
    }
}

