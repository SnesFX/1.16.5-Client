/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.biome;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeZoomer;
import net.minecraft.world.level.biome.FuzzyOffsetBiomeZoomer;

public enum FuzzyOffsetConstantColumnBiomeZoomer implements BiomeZoomer
{
    INSTANCE;
    

    @Override
    public Biome getBiome(long l, int n, int n2, int n3, BiomeManager.NoiseBiomeSource noiseBiomeSource) {
        return FuzzyOffsetBiomeZoomer.INSTANCE.getBiome(l, n, 0, n3, noiseBiomeSource);
    }
}

