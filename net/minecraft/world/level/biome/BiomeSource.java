/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.serialization.Codec
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.CheckerboardColumnBiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.OverworldBiomeSource;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;

public abstract class BiomeSource
implements BiomeManager.NoiseBiomeSource {
    public static final Codec<BiomeSource> CODEC;
    protected final Map<StructureFeature<?>, Boolean> supportedStructures = Maps.newHashMap();
    protected final Set<BlockState> surfaceBlocks = Sets.newHashSet();
    protected final List<Biome> possibleBiomes;

    protected BiomeSource(Stream<Supplier<Biome>> stream) {
        this((List)stream.map(Supplier::get).collect(ImmutableList.toImmutableList()));
    }

    protected BiomeSource(List<Biome> list) {
        this.possibleBiomes = list;
    }

    protected abstract Codec<? extends BiomeSource> codec();

    public abstract BiomeSource withSeed(long var1);

    public List<Biome> possibleBiomes() {
        return this.possibleBiomes;
    }

    public Set<Biome> getBiomesWithin(int n, int n2, int n3, int n4) {
        int n5 = n - n4 >> 2;
        int n6 = n2 - n4 >> 2;
        int n7 = n3 - n4 >> 2;
        int n8 = n + n4 >> 2;
        int n9 = n2 + n4 >> 2;
        int n10 = n3 + n4 >> 2;
        int n11 = n8 - n5 + 1;
        int n12 = n9 - n6 + 1;
        int n13 = n10 - n7 + 1;
        HashSet hashSet = Sets.newHashSet();
        for (int i = 0; i < n13; ++i) {
            for (int j = 0; j < n11; ++j) {
                for (int k = 0; k < n12; ++k) {
                    int n14 = n5 + j;
                    int n15 = n6 + k;
                    int n16 = n7 + i;
                    hashSet.add(this.getNoiseBiome(n14, n15, n16));
                }
            }
        }
        return hashSet;
    }

    @Nullable
    public BlockPos findBiomeHorizontal(int n, int n2, int n3, int n4, Predicate<Biome> predicate, Random random) {
        return this.findBiomeHorizontal(n, n2, n3, n4, 1, predicate, random, false);
    }

    @Nullable
    public BlockPos findBiomeHorizontal(int n, int n2, int n3, int n4, int n5, Predicate<Biome> predicate, Random random, boolean bl) {
        int n6;
        int n7 = n >> 2;
        int n8 = n3 >> 2;
        int n9 = n4 >> 2;
        int n10 = n2 >> 2;
        BlockPos blockPos = null;
        int n11 = 0;
        for (int i = n6 = bl != false ? 0 : n9; i <= n9; i += n5) {
            for (int j = -i; j <= i; j += n5) {
                boolean bl2 = Math.abs(j) == i;
                for (int k = -i; k <= i; k += n5) {
                    int n12;
                    int n13;
                    if (bl) {
                        int n14 = n13 = Math.abs(k) == i ? 1 : 0;
                        if (n13 == 0 && !bl2) continue;
                    }
                    if (!predicate.test(this.getNoiseBiome(n13 = n7 + k, n10, n12 = n8 + j))) continue;
                    if (blockPos == null || random.nextInt(n11 + 1) == 0) {
                        blockPos = new BlockPos(n13 << 2, n2, n12 << 2);
                        if (bl) {
                            return blockPos;
                        }
                    }
                    ++n11;
                }
            }
        }
        return blockPos;
    }

    public boolean canGenerateStructure(StructureFeature<?> structureFeature2) {
        return this.supportedStructures.computeIfAbsent(structureFeature2, structureFeature -> this.possibleBiomes.stream().anyMatch(biome -> biome.getGenerationSettings().isValidStart((StructureFeature<?>)structureFeature)));
    }

    public Set<BlockState> getSurfaceBlocks() {
        if (this.surfaceBlocks.isEmpty()) {
            for (Biome biome : this.possibleBiomes) {
                this.surfaceBlocks.add(biome.getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial());
            }
        }
        return this.surfaceBlocks;
    }

    static {
        Registry.register(Registry.BIOME_SOURCE, "fixed", FixedBiomeSource.CODEC);
        Registry.register(Registry.BIOME_SOURCE, "multi_noise", MultiNoiseBiomeSource.CODEC);
        Registry.register(Registry.BIOME_SOURCE, "checkerboard", CheckerboardColumnBiomeSource.CODEC);
        Registry.register(Registry.BIOME_SOURCE, "vanilla_layered", OverworldBiomeSource.CODEC);
        Registry.register(Registry.BIOME_SOURCE, "the_end", TheEndBiomeSource.CODEC);
        CODEC = Registry.BIOME_SOURCE.dispatchStable(BiomeSource::codec, Function.identity());
    }
}

