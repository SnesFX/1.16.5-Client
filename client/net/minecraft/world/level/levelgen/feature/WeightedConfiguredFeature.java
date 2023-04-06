/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P2
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class WeightedConfiguredFeature {
    public static final Codec<WeightedConfiguredFeature> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)ConfiguredFeature.CODEC.fieldOf("feature").forGetter(weightedConfiguredFeature -> weightedConfiguredFeature.feature), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("chance").forGetter(weightedConfiguredFeature -> Float.valueOf(weightedConfiguredFeature.chance))).apply((Applicative)instance, (arg_0, arg_1) -> WeightedConfiguredFeature.new(arg_0, arg_1)));
    public final Supplier<ConfiguredFeature<?, ?>> feature;
    public final float chance;

    public WeightedConfiguredFeature(ConfiguredFeature<?, ?> configuredFeature, float f) {
        this(() -> configuredFeature, f);
    }

    private WeightedConfiguredFeature(Supplier<ConfiguredFeature<?, ?>> supplier, float f) {
        this.feature = supplier;
        this.chance = f;
    }

    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos) {
        return this.feature.get().place(worldGenLevel, chunkGenerator, random, blockPos);
    }
}

