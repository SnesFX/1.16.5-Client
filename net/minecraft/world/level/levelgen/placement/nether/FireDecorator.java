/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.placement.nether;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.SimpleFeatureDecorator;

public class FireDecorator
extends SimpleFeatureDecorator<CountConfiguration> {
    public FireDecorator(Codec<CountConfiguration> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> place(Random random, CountConfiguration countConfiguration, BlockPos blockPos) {
        ArrayList arrayList = Lists.newArrayList();
        for (int i = 0; i < random.nextInt(random.nextInt(countConfiguration.count().sample(random)) + 1) + 1; ++i) {
            int n = random.nextInt(16) + blockPos.getX();
            int n2 = random.nextInt(16) + blockPos.getZ();
            int n3 = random.nextInt(120) + 4;
            arrayList.add(new BlockPos(n, n3, n2));
        }
        return arrayList.stream();
    }
}

