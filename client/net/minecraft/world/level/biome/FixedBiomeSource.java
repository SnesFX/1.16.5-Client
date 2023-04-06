/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Sets
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;

public class FixedBiomeSource
extends BiomeSource {
    public static final Codec<FixedBiomeSource> CODEC = Biome.CODEC.fieldOf("biome").xmap(FixedBiomeSource::new, fixedBiomeSource -> fixedBiomeSource.biome).stable().codec();
    private final Supplier<Biome> biome;

    public FixedBiomeSource(Biome biome) {
        this(() -> biome);
    }

    public FixedBiomeSource(Supplier<Biome> supplier) {
        super((List<Biome>)ImmutableList.of((Object)supplier.get()));
        this.biome = supplier;
    }

    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    public BiomeSource withSeed(long l) {
        return this;
    }

    @Override
    public Biome getNoiseBiome(int n, int n2, int n3) {
        return this.biome.get();
    }

    @Nullable
    @Override
    public BlockPos findBiomeHorizontal(int n, int n2, int n3, int n4, int n5, Predicate<Biome> predicate, Random random, boolean bl) {
        if (predicate.test(this.biome.get())) {
            if (bl) {
                return new BlockPos(n, n2, n3);
            }
            return new BlockPos(n - n4 + random.nextInt(n4 * 2 + 1), n2, n3 - n4 + random.nextInt(n4 * 2 + 1));
        }
        return null;
    }

    @Override
    public Set<Biome> getBiomesWithin(int n, int n2, int n3, int n4) {
        return Sets.newHashSet((Object[])new Biome[]{this.biome.get()});
    }
}

