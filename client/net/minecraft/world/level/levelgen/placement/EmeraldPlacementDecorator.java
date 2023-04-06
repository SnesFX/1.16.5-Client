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
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class EmeraldPlacementDecorator
extends SimpleFeatureDecorator<NoneDecoratorConfiguration> {
    public EmeraldPlacementDecorator(Codec<NoneDecoratorConfiguration> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> place(Random random, NoneDecoratorConfiguration noneDecoratorConfiguration, BlockPos blockPos) {
        int n2 = 3 + random.nextInt(6);
        return IntStream.range(0, n2).mapToObj(n -> {
            int n2 = random.nextInt(16) + blockPos.getX();
            int n3 = random.nextInt(16) + blockPos.getZ();
            int n4 = random.nextInt(28) + 4;
            return new BlockPos(n2, n4, n3);
        });
    }
}

