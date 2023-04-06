/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.chunk;

import javax.annotation.Nullable;
import net.minecraft.core.IdMap;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkBiomeContainer
implements BiomeManager.NoiseBiomeSource {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int WIDTH_BITS = (int)Math.round(Math.log(16.0) / Math.log(2.0)) - 2;
    private static final int HEIGHT_BITS = (int)Math.round(Math.log(256.0) / Math.log(2.0)) - 2;
    public static final int BIOMES_SIZE = 1 << WIDTH_BITS + WIDTH_BITS + HEIGHT_BITS;
    public static final int HORIZONTAL_MASK = (1 << WIDTH_BITS) - 1;
    public static final int VERTICAL_MASK = (1 << HEIGHT_BITS) - 1;
    private final IdMap<Biome> biomeRegistry;
    private final Biome[] biomes;

    public ChunkBiomeContainer(IdMap<Biome> idMap, Biome[] arrbiome) {
        this.biomeRegistry = idMap;
        this.biomes = arrbiome;
    }

    private ChunkBiomeContainer(IdMap<Biome> idMap) {
        this(idMap, new Biome[BIOMES_SIZE]);
    }

    public ChunkBiomeContainer(IdMap<Biome> idMap, int[] arrn) {
        this(idMap);
        for (int i = 0; i < this.biomes.length; ++i) {
            int n = arrn[i];
            Biome biome = idMap.byId(n);
            if (biome == null) {
                LOGGER.warn("Received invalid biome id: " + n);
                this.biomes[i] = idMap.byId(0);
                continue;
            }
            this.biomes[i] = biome;
        }
    }

    public ChunkBiomeContainer(IdMap<Biome> idMap, ChunkPos chunkPos, BiomeSource biomeSource) {
        this(idMap);
        int n = chunkPos.getMinBlockX() >> 2;
        int n2 = chunkPos.getMinBlockZ() >> 2;
        for (int i = 0; i < this.biomes.length; ++i) {
            int n3 = i & HORIZONTAL_MASK;
            int n4 = i >> WIDTH_BITS + WIDTH_BITS & VERTICAL_MASK;
            int n5 = i >> WIDTH_BITS & HORIZONTAL_MASK;
            this.biomes[i] = biomeSource.getNoiseBiome(n + n3, n4, n2 + n5);
        }
    }

    public ChunkBiomeContainer(IdMap<Biome> idMap, ChunkPos chunkPos, BiomeSource biomeSource, @Nullable int[] arrn) {
        this(idMap);
        int n = chunkPos.getMinBlockX() >> 2;
        int n2 = chunkPos.getMinBlockZ() >> 2;
        if (arrn != null) {
            for (int i = 0; i < arrn.length; ++i) {
                this.biomes[i] = idMap.byId(arrn[i]);
                if (this.biomes[i] != null) continue;
                int n3 = i & HORIZONTAL_MASK;
                int n4 = i >> WIDTH_BITS + WIDTH_BITS & VERTICAL_MASK;
                int n5 = i >> WIDTH_BITS & HORIZONTAL_MASK;
                this.biomes[i] = biomeSource.getNoiseBiome(n + n3, n4, n2 + n5);
            }
        } else {
            for (int i = 0; i < this.biomes.length; ++i) {
                int n6 = i & HORIZONTAL_MASK;
                int n7 = i >> WIDTH_BITS + WIDTH_BITS & VERTICAL_MASK;
                int n8 = i >> WIDTH_BITS & HORIZONTAL_MASK;
                this.biomes[i] = biomeSource.getNoiseBiome(n + n6, n7, n2 + n8);
            }
        }
    }

    public int[] writeBiomes() {
        int[] arrn = new int[this.biomes.length];
        for (int i = 0; i < this.biomes.length; ++i) {
            arrn[i] = this.biomeRegistry.getId(this.biomes[i]);
        }
        return arrn;
    }

    @Override
    public Biome getNoiseBiome(int n, int n2, int n3) {
        int n4 = n & HORIZONTAL_MASK;
        int n5 = Mth.clamp(n2, 0, VERTICAL_MASK);
        int n6 = n3 & HORIZONTAL_MASK;
        return this.biomes[n5 << WIDTH_BITS + WIDTH_BITS | n6 << WIDTH_BITS | n4];
    }
}

