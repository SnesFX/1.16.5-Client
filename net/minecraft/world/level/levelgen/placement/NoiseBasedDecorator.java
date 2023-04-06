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
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.NoiseCountFactorDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

public class NoiseBasedDecorator
extends SimpleFeatureDecorator<NoiseCountFactorDecoratorConfiguration> {
    public NoiseBasedDecorator(Codec<NoiseCountFactorDecoratorConfiguration> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> place(Random random, NoiseCountFactorDecoratorConfiguration noiseCountFactorDecoratorConfiguration, BlockPos blockPos) {
        double d = Biome.BIOME_INFO_NOISE.getValue((double)blockPos.getX() / noiseCountFactorDecoratorConfiguration.noiseFactor, (double)blockPos.getZ() / noiseCountFactorDecoratorConfiguration.noiseFactor, false);
        int n2 = (int)Math.ceil((d + noiseCountFactorDecoratorConfiguration.noiseOffset) * (double)noiseCountFactorDecoratorConfiguration.noiseToCountRatio);
        return IntStream.range(0, n2).mapToObj(n -> blockPos);
    }
}

