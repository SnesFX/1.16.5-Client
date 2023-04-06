/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.HashCode
 *  com.google.common.hash.Hashing
 */
package net.minecraft.world.level.biome;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.BiomeZoomer;

public class BiomeManager {
    private final NoiseBiomeSource noiseBiomeSource;
    private final long biomeZoomSeed;
    private final BiomeZoomer zoomer;

    public BiomeManager(NoiseBiomeSource noiseBiomeSource, long l, BiomeZoomer biomeZoomer) {
        this.noiseBiomeSource = noiseBiomeSource;
        this.biomeZoomSeed = l;
        this.zoomer = biomeZoomer;
    }

    public static long obfuscateSeed(long l) {
        return Hashing.sha256().hashLong(l).asLong();
    }

    public BiomeManager withDifferentSource(BiomeSource biomeSource) {
        return new BiomeManager(biomeSource, this.biomeZoomSeed, this.zoomer);
    }

    public Biome getBiome(BlockPos blockPos) {
        return this.zoomer.getBiome(this.biomeZoomSeed, blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.noiseBiomeSource);
    }

    public Biome getNoiseBiomeAtPosition(double d, double d2, double d3) {
        int n = Mth.floor(d) >> 2;
        int n2 = Mth.floor(d2) >> 2;
        int n3 = Mth.floor(d3) >> 2;
        return this.getNoiseBiomeAtQuart(n, n2, n3);
    }

    public Biome getNoiseBiomeAtPosition(BlockPos blockPos) {
        int n = blockPos.getX() >> 2;
        int n2 = blockPos.getY() >> 2;
        int n3 = blockPos.getZ() >> 2;
        return this.getNoiseBiomeAtQuart(n, n2, n3);
    }

    public Biome getNoiseBiomeAtQuart(int n, int n2, int n3) {
        return this.noiseBiomeSource.getNoiseBiome(n, n2, n3);
    }

    public static interface NoiseBiomeSource {
        public Biome getNoiseBiome(int var1, int var2, int var3);
    }

}

