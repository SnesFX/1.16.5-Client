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
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class CountDecorator
extends SimpleFeatureDecorator<CountConfiguration> {
    public CountDecorator(Codec<CountConfiguration> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> place(Random random, CountConfiguration countConfiguration, BlockPos blockPos) {
        return IntStream.range(0, countConfiguration.count().sample(random)).mapToObj(n -> blockPos);
    }
}

