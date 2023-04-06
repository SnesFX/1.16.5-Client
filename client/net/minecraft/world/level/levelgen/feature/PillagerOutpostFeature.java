/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.JigsawFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;

public class PillagerOutpostFeature
extends JigsawFeature {
    private static final List<MobSpawnSettings.SpawnerData> OUTPOST_ENEMIES = ImmutableList.of((Object)new MobSpawnSettings.SpawnerData(EntityType.PILLAGER, 1, 1, 1));

    public PillagerOutpostFeature(Codec<JigsawConfiguration> codec) {
        super(codec, 0, true, true);
    }

    @Override
    public List<MobSpawnSettings.SpawnerData> getSpecialEnemies() {
        return OUTPOST_ENEMIES;
    }

    @Override
    protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, WorldgenRandom worldgenRandom, int n, int n2, Biome biome, ChunkPos chunkPos, JigsawConfiguration jigsawConfiguration) {
        int n3 = n >> 4;
        int n4 = n2 >> 4;
        worldgenRandom.setSeed((long)(n3 ^ n4 << 4) ^ l);
        worldgenRandom.nextInt();
        if (worldgenRandom.nextInt(5) != 0) {
            return false;
        }
        return !this.isNearVillage(chunkGenerator, l, worldgenRandom, n, n2);
    }

    private boolean isNearVillage(ChunkGenerator chunkGenerator, long l, WorldgenRandom worldgenRandom, int n, int n2) {
        StructureFeatureConfiguration structureFeatureConfiguration = chunkGenerator.getSettings().getConfig(StructureFeature.VILLAGE);
        if (structureFeatureConfiguration == null) {
            return false;
        }
        for (int i = n - 10; i <= n + 10; ++i) {
            for (int j = n2 - 10; j <= n2 + 10; ++j) {
                ChunkPos chunkPos = StructureFeature.VILLAGE.getPotentialFeatureChunk(structureFeatureConfiguration, l, worldgenRandom, i, j);
                if (i != chunkPos.x || j != chunkPos.z) continue;
                return true;
            }
        }
        return false;
    }
}

