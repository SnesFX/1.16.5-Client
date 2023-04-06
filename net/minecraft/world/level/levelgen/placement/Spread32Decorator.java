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
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class Spread32Decorator
extends FeatureDecorator<NoneDecoratorConfiguration> {
    public Spread32Decorator(Codec<NoneDecoratorConfiguration> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecorationContext decorationContext, Random random, NoneDecoratorConfiguration noneDecoratorConfiguration, BlockPos blockPos) {
        int n = random.nextInt(blockPos.getY() + 32);
        return Stream.of(new BlockPos(blockPos.getX(), n, blockPos.getZ()));
    }
}

