/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P2
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public class TheEndBiomeSource
extends BiomeSource {
    public static final Codec<TheEndBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(theEndBiomeSource -> theEndBiomeSource.biomes), (App)Codec.LONG.fieldOf("seed").stable().forGetter(theEndBiomeSource -> theEndBiomeSource.seed)).apply((Applicative)instance, instance.stable((arg_0, arg_1) -> TheEndBiomeSource.new(arg_0, arg_1))));
    private final SimplexNoise islandNoise;
    private final Registry<Biome> biomes;
    private final long seed;
    private final Biome end;
    private final Biome highlands;
    private final Biome midlands;
    private final Biome islands;
    private final Biome barrens;

    public TheEndBiomeSource(Registry<Biome> registry, long l) {
        this(registry, l, registry.getOrThrow(Biomes.THE_END), registry.getOrThrow(Biomes.END_HIGHLANDS), registry.getOrThrow(Biomes.END_MIDLANDS), registry.getOrThrow(Biomes.SMALL_END_ISLANDS), registry.getOrThrow(Biomes.END_BARRENS));
    }

    private TheEndBiomeSource(Registry<Biome> registry, long l, Biome biome, Biome biome2, Biome biome3, Biome biome4, Biome biome5) {
        super((List<Biome>)ImmutableList.of((Object)biome, (Object)biome2, (Object)biome3, (Object)biome4, (Object)biome5));
        this.biomes = registry;
        this.seed = l;
        this.end = biome;
        this.highlands = biome2;
        this.midlands = biome3;
        this.islands = biome4;
        this.barrens = biome5;
        WorldgenRandom worldgenRandom = new WorldgenRandom(l);
        worldgenRandom.consumeCount(17292);
        this.islandNoise = new SimplexNoise(worldgenRandom);
    }

    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    public BiomeSource withSeed(long l) {
        return new TheEndBiomeSource(this.biomes, l, this.end, this.highlands, this.midlands, this.islands, this.barrens);
    }

    @Override
    public Biome getNoiseBiome(int n, int n2, int n3) {
        int n4 = n >> 2;
        int n5 = n3 >> 2;
        if ((long)n4 * (long)n4 + (long)n5 * (long)n5 <= 4096L) {
            return this.end;
        }
        float f = TheEndBiomeSource.getHeightValue(this.islandNoise, n4 * 2 + 1, n5 * 2 + 1);
        if (f > 40.0f) {
            return this.highlands;
        }
        if (f >= 0.0f) {
            return this.midlands;
        }
        if (f < -20.0f) {
            return this.islands;
        }
        return this.barrens;
    }

    public boolean stable(long l) {
        return this.seed == l;
    }

    public static float getHeightValue(SimplexNoise simplexNoise, int n, int n2) {
        int n3 = n / 2;
        int n4 = n2 / 2;
        int n5 = n % 2;
        int n6 = n2 % 2;
        float f = 100.0f - Mth.sqrt(n * n + n2 * n2) * 8.0f;
        f = Mth.clamp(f, -100.0f, 80.0f);
        for (int i = -12; i <= 12; ++i) {
            for (int j = -12; j <= 12; ++j) {
                long l = n3 + i;
                long l2 = n4 + j;
                if (l * l + l2 * l2 <= 4096L || !(simplexNoise.getValue(l, l2) < -0.8999999761581421)) continue;
                float f2 = (Mth.abs(l) * 3439.0f + Mth.abs(l2) * 147.0f) % 13.0f + 9.0f;
                float f3 = n5 - i * 2;
                float f4 = n6 - j * 2;
                float f5 = 100.0f - Mth.sqrt(f3 * f3 + f4 * f4) * f2;
                f5 = Mth.clamp(f5, -100.0f, 80.0f);
                f = Math.max(f, f5);
            }
        }
        return f;
    }
}

