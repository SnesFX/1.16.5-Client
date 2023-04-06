/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.biome;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeZoomer;

public enum NearestNeighborBiomeZoomer implements BiomeZoomer
{
    INSTANCE;
    

    @Override
    public Biome getBiome(long l, int n, int n2, int n3, BiomeManager.NoiseBiomeSource noiseBiomeSource) {
        return noiseBiomeSource.getNoiseBiome(n >> 2, n2 >> 2, n3 >> 2);
    }
}

