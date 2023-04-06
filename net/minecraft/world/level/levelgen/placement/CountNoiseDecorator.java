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
import net.minecraft.world.level.levelgen.feature.configurations.NoiseDependantDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

public class CountNoiseDecorator
extends FeatureDecorator<NoiseDependantDecoratorConfiguration> {
    public CountNoiseDecorator(Codec<NoiseDependantDecoratorConfiguration> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecorationContext decorationContext, Random random, NoiseDependantDecoratorConfiguration noiseDependantDecoratorConfiguration, BlockPos blockPos) {
        double d = Biome.BIOME_INFO_NOISE.getValue((double)blockPos.getX() / 200.0, (double)blockPos.getZ() / 200.0, false);
        int n2 = d < noiseDependantDecoratorConfiguration.noiseLevel ? noiseDependantDecoratorConfiguration.belowNoise : noiseDependantDecoratorConfiguration.aboveNoise;
        return IntStream.range(0, n2).mapToObj(n -> blockPos);
    }
}

