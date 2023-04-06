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
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public abstract class SimpleFeatureDecorator<DC extends DecoratorConfiguration>
extends FeatureDecorator<DC> {
    public SimpleFeatureDecorator(Codec<DC> codec) {
        super(codec);
    }

    @Override
    public final Stream<BlockPos> getPositions(DecorationContext decorationContext, Random random, DC DC, BlockPos blockPos) {
        return this.place(random, DC, blockPos);
    }

    protected abstract Stream<BlockPos> place(Random var1, DC var2, BlockPos var3);
}

