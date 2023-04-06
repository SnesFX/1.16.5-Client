/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuriedTreasurePieces;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class BuriedTreasureFeature
extends StructureFeature<ProbabilityFeatureConfiguration> {
    public BuriedTreasureFeature(Codec<ProbabilityFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, WorldgenRandom worldgenRandom, int n, int n2, Biome biome, ChunkPos chunkPos, ProbabilityFeatureConfiguration probabilityFeatureConfiguration) {
        worldgenRandom.setLargeFeatureWithSalt(l, n, n2, 10387320);
        return worldgenRandom.nextFloat() < probabilityFeatureConfiguration.probability;
    }

    @Override
    public StructureFeature.StructureStartFactory<ProbabilityFeatureConfiguration> getStartFactory() {
        return (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5) -> BuriedTreasureStart.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5);
    }

    public static class BuriedTreasureStart
    extends StructureStart<ProbabilityFeatureConfiguration> {
        public BuriedTreasureStart(StructureFeature<ProbabilityFeatureConfiguration> structureFeature, int n, int n2, BoundingBox boundingBox, int n3, long l) {
            super(structureFeature, n, n2, boundingBox, n3, l);
        }

        @Override
        public void generatePieces(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, StructureManager structureManager, int n, int n2, Biome biome, ProbabilityFeatureConfiguration probabilityFeatureConfiguration) {
            int n3 = n * 16;
            int n4 = n2 * 16;
            BlockPos blockPos = new BlockPos(n3 + 9, 90, n4 + 9);
            this.pieces.add(new BuriedTreasurePieces.BuriedTreasurePiece(blockPos));
            this.calculateBoundingBox();
        }

        @Override
        public BlockPos getLocatePos() {
            return new BlockPos((this.getChunkX() << 4) + 9, 0, (this.getChunkZ() << 4) + 9);
        }
    }

}

