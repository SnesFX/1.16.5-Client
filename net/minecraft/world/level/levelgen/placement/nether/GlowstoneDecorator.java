/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.placement.nether;

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

public class GlowstoneDecorator
extends SimpleFeatureDecorator<CountConfiguration> {
    public GlowstoneDecorator(Codec<CountConfiguration> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> place(Random random, CountConfiguration countConfiguration, BlockPos blockPos) {
        return IntStream.range(0, random.nextInt(random.nextInt(countConfiguration.count().sample(random)) + 1)).mapToObj(n -> {
            int n2 = random.nextInt(16) + blockPos.getX();
            int n3 = random.nextInt(16) + blockPos.getZ();
            int n4 = random.nextInt(120) + 4;
            return new BlockPos(n2, n4, n3);
        });
    }
}

