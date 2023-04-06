/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;

public class SimpleRandomSelectorFeature
extends Feature<SimpleRandomFeatureConfiguration> {
    public SimpleRandomSelectorFeature(Codec<SimpleRandomFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, SimpleRandomFeatureConfiguration simpleRandomFeatureConfiguration) {
        int n = random.nextInt(simpleRandomFeatureConfiguration.features.size());
        ConfiguredFeature<?, ?> configuredFeature = simpleRandomFeatureConfiguration.features.get(n).get();
        return configuredFeature.place(worldGenLevel, chunkGenerator, random, blockPos);
    }
}

