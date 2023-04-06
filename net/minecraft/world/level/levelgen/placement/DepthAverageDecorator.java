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
import net.minecraft.world.level.levelgen.placement.DepthAverageConfigation;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class DepthAverageDecorator
extends SimpleFeatureDecorator<DepthAverageConfigation> {
    public DepthAverageDecorator(Codec<DepthAverageConfigation> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> place(Random random, DepthAverageConfigation depthAverageConfigation, BlockPos blockPos) {
        int n = depthAverageConfigation.baseline;
        int n2 = depthAverageConfigation.spread;
        int n3 = blockPos.getX();
        int n4 = blockPos.getZ();
        int n5 = random.nextInt(n2) + random.nextInt(n2) - n2 + n;
        return Stream.of(new BlockPos(n3, n5, n4));
    }
}

