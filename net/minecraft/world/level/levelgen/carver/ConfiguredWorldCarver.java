/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

public class ConfiguredWorldCarver<WC extends CarverConfiguration> {
    public static final Codec<ConfiguredWorldCarver<?>> DIRECT_CODEC = Registry.CARVER.dispatch(configuredWorldCarver -> configuredWorldCarver.worldCarver, WorldCarver::configuredCodec);
    public static final Codec<Supplier<ConfiguredWorldCarver<?>>> CODEC = RegistryFileCodec.create(Registry.CONFIGURED_CARVER_REGISTRY, DIRECT_CODEC);
    public static final Codec<List<Supplier<ConfiguredWorldCarver<?>>>> LIST_CODEC = RegistryFileCodec.homogeneousList(Registry.CONFIGURED_CARVER_REGISTRY, DIRECT_CODEC);
    private final WorldCarver<WC> worldCarver;
    private final WC config;

    public ConfiguredWorldCarver(WorldCarver<WC> worldCarver, WC WC) {
        this.worldCarver = worldCarver;
        this.config = WC;
    }

    public WC config() {
        return this.config;
    }

    public boolean isStartChunk(Random random, int n, int n2) {
        return this.worldCarver.isStartChunk(random, n, n2, this.config);
    }

    public boolean carve(ChunkAccess chunkAccess, Function<BlockPos, Biome> function, Random random, int n, int n2, int n3, int n4, int n5, BitSet bitSet) {
        return this.worldCarver.carve(chunkAccess, function, random, n, n2, n3, n4, n5, bitSet, this.config);
    }
}

