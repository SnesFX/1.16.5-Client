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
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.OceanMonumentPieces;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class OceanMonumentFeature
extends StructureFeature<NoneFeatureConfiguration> {
    private static final List<MobSpawnSettings.SpawnerData> MONUMENT_ENEMIES = ImmutableList.of((Object)new MobSpawnSettings.SpawnerData(EntityType.GUARDIAN, 1, 2, 4));

    public OceanMonumentFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean linearSeparation() {
        return false;
    }

    @Override
    protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, WorldgenRandom worldgenRandom, int n, int n2, Biome biome, ChunkPos chunkPos, NoneFeatureConfiguration noneFeatureConfiguration) {
        Set<Biome> set = biomeSource.getBiomesWithin(n * 16 + 9, chunkGenerator.getSeaLevel(), n2 * 16 + 9, 16);
        for (Biome object2 : set) {
            if (object2.getGenerationSettings().isValidStart(this)) continue;
            return false;
        }
        Set<Biome> set2 = biomeSource.getBiomesWithin(n * 16 + 9, chunkGenerator.getSeaLevel(), n2 * 16 + 9, 29);
        Iterator iterator = set2.iterator();
        while (iterator.hasNext()) {
            Biome biome2 = (Biome)iterator.next();
            if (biome2.getBiomeCategory() == Biome.BiomeCategory.OCEAN || biome2.getBiomeCategory() == Biome.BiomeCategory.RIVER) continue;
            return false;
        }
        return true;
    }

    @Override
    public StructureFeature.StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
        return (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5) -> OceanMonumentStart.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5);
    }

    @Override
    public List<MobSpawnSettings.SpawnerData> getSpecialEnemies() {
        return MONUMENT_ENEMIES;
    }

    public static class OceanMonumentStart
    extends StructureStart<NoneFeatureConfiguration> {
        private boolean isCreated;

        public OceanMonumentStart(StructureFeature<NoneFeatureConfiguration> structureFeature, int n, int n2, BoundingBox boundingBox, int n3, long l) {
            super(structureFeature, n, n2, boundingBox, n3, l);
        }

        @Override
        public void generatePieces(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, StructureManager structureManager, int n, int n2, Biome biome, NoneFeatureConfiguration noneFeatureConfiguration) {
            this.generatePieces(n, n2);
        }

        private void generatePieces(int n, int n2) {
            int n3 = n * 16 - 29;
            int n4 = n2 * 16 - 29;
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(this.random);
            this.pieces.add(new OceanMonumentPieces.MonumentBuilding(this.random, n3, n4, direction));
            this.calculateBoundingBox();
            this.isCreated = true;
        }

        @Override
        public void placeInChunk(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
            if (!this.isCreated) {
                this.pieces.clear();
                this.generatePieces(this.getChunkX(), this.getChunkZ());
            }
            super.placeInChunk(worldGenLevel, structureFeatureManager, chunkGenerator, random, boundingBox, chunkPos);
        }
    }

}

