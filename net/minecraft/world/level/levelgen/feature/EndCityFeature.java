/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.EndCityPieces;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class EndCityFeature
extends StructureFeature<NoneFeatureConfiguration> {
    public EndCityFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean linearSeparation() {
        return false;
    }

    @Override
    protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, WorldgenRandom worldgenRandom, int n, int n2, Biome biome, ChunkPos chunkPos, NoneFeatureConfiguration noneFeatureConfiguration) {
        return EndCityFeature.getYPositionForFeature(n, n2, chunkGenerator) >= 60;
    }

    @Override
    public StructureFeature.StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
        return (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5) -> EndCityStart.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5);
    }

    private static int getYPositionForFeature(int n, int n2, ChunkGenerator chunkGenerator) {
        Random random = new Random(n + n2 * 10387313);
        Rotation rotation = Rotation.getRandom(random);
        int n3 = 5;
        int n4 = 5;
        if (rotation == Rotation.CLOCKWISE_90) {
            n3 = -5;
        } else if (rotation == Rotation.CLOCKWISE_180) {
            n3 = -5;
            n4 = -5;
        } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
            n4 = -5;
        }
        int n5 = (n << 4) + 7;
        int n6 = (n2 << 4) + 7;
        int n7 = chunkGenerator.getFirstOccupiedHeight(n5, n6, Heightmap.Types.WORLD_SURFACE_WG);
        int n8 = chunkGenerator.getFirstOccupiedHeight(n5, n6 + n4, Heightmap.Types.WORLD_SURFACE_WG);
        int n9 = chunkGenerator.getFirstOccupiedHeight(n5 + n3, n6, Heightmap.Types.WORLD_SURFACE_WG);
        int n10 = chunkGenerator.getFirstOccupiedHeight(n5 + n3, n6 + n4, Heightmap.Types.WORLD_SURFACE_WG);
        return Math.min(Math.min(n7, n8), Math.min(n9, n10));
    }

    public static class EndCityStart
    extends StructureStart<NoneFeatureConfiguration> {
        public EndCityStart(StructureFeature<NoneFeatureConfiguration> structureFeature, int n, int n2, BoundingBox boundingBox, int n3, long l) {
            super(structureFeature, n, n2, boundingBox, n3, l);
        }

        @Override
        public void generatePieces(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, StructureManager structureManager, int n, int n2, Biome biome, NoneFeatureConfiguration noneFeatureConfiguration) {
            Rotation rotation = Rotation.getRandom(this.random);
            int n3 = EndCityFeature.getYPositionForFeature(n, n2, chunkGenerator);
            if (n3 < 60) {
                return;
            }
            BlockPos blockPos = new BlockPos(n * 16 + 8, n3, n2 * 16 + 8);
            EndCityPieces.startHouseTower(structureManager, blockPos, rotation, this.pieces, this.random);
            this.calculateBoundingBox();
        }
    }

}

