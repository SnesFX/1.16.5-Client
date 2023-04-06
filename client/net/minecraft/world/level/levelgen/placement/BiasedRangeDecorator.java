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
import net.minecraft.world.level.levelgen.feature.configurations.RangeDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class BiasedRangeDecorator
extends SimpleFeatureDecorator<RangeDecoratorConfiguration> {
    public BiasedRangeDecorator(Codec<RangeDecoratorConfiguration> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> place(Random random, RangeDecoratorConfiguration rangeDecoratorConfiguration, BlockPos blockPos) {
        int n = blockPos.getX();
        int n2 = blockPos.getZ();
        int n3 = random.nextInt(random.nextInt(rangeDecoratorConfiguration.maximum - rangeDecoratorConfiguration.topOffset) + rangeDecoratorConfiguration.bottomOffset);
        return Stream.of(new BlockPos(n, n3, n2));
    }
}

