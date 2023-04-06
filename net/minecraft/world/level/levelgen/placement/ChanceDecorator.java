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
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class ChanceDecorator
extends SimpleFeatureDecorator<ChanceDecoratorConfiguration> {
    public ChanceDecorator(Codec<ChanceDecoratorConfiguration> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> place(Random random, ChanceDecoratorConfiguration chanceDecoratorConfiguration, BlockPos blockPos) {
        if (random.nextFloat() < 1.0f / (float)chanceDecoratorConfiguration.chance) {
            return Stream.of(blockPos);
        }
        return Stream.empty();
    }
}

