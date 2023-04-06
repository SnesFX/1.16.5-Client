/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomBooleanFeatureConfiguration;

public class RandomBooleanSelectorFeature
extends Feature<RandomBooleanFeatureConfiguration> {
    public RandomBooleanSelectorFeature(Codec<RandomBooleanFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, RandomBooleanFeatureConfiguration randomBooleanFeatureConfiguration) {
        boolean bl = random.nextBoolean();
        if (bl) {
            return randomBooleanFeatureConfiguration.featureTrue.get().place(worldGenLevel, chunkGenerator, random, blockPos);
        }
        return randomBooleanFeatureConfiguration.featureFalse.get().place(worldGenLevel, chunkGenerator, random, blockPos);
    }
}

