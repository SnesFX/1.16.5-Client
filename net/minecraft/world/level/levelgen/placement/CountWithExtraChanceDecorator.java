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
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FrequencyWithExtraChanceDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class CountWithExtraChanceDecorator
extends SimpleFeatureDecorator<FrequencyWithExtraChanceDecoratorConfiguration> {
    public CountWithExtraChanceDecorator(Codec<FrequencyWithExtraChanceDecoratorConfiguration> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> place(Random random, FrequencyWithExtraChanceDecoratorConfiguration frequencyWithExtraChanceDecoratorConfiguration, BlockPos blockPos) {
        int n2 = frequencyWithExtraChanceDecoratorConfiguration.count + (random.nextFloat() < frequencyWithExtraChanceDecoratorConfiguration.extraChance ? frequencyWithExtraChanceDecoratorConfiguration.extraCount : 0);
        return IntStream.range(0, n2).mapToObj(n -> blockPos);
    }
}

