/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.WoodlandMansionPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.material.Material;

public class WoodlandMansionFeature
extends StructureFeature<NoneFeatureConfiguration> {
    public WoodlandMansionFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean linearSeparation() {
        return false;
    }

    @Override
    protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, WorldgenRandom worldgenRandom, int n, int n2, Biome biome, ChunkPos chunkPos, NoneFeatureConfiguration noneFeatureConfiguration) {
        Set<Biome> set = biomeSource.getBiomesWithin(n * 16 + 9, chunkGenerator.getSeaLevel(), n2 * 16 + 9, 32);
        for (Biome biome2 : set) {
            if (biome2.getGenerationSettings().isValidStart(this)) continue;
            return false;
        }
        return true;
    }

    @Override
    public StructureFeature.StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
        return (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5) -> WoodlandMansionStart.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5);
    }

    public static class WoodlandMansionStart
    extends StructureStart<NoneFeatureConfiguration> {
        public WoodlandMansionStart(StructureFeature<NoneFeatureConfiguration> structureFeature, int n, int n2, BoundingBox boundingBox, int n3, long l) {
            super(structureFeature, n, n2, boundingBox, n3, l);
        }

        @Override
        public void generatePieces(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, StructureManager structureManager, int n, int n2, Biome biome, NoneFeatureConfiguration noneFeatureConfiguration) {
            Rotation rotation = Rotation.getRandom(this.random);
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
            int n11 = Math.min(Math.min(n7, n8), Math.min(n9, n10));
            if (n11 < 60) {
                return;
            }
            BlockPos blockPos = new BlockPos(n * 16 + 8, n11 + 1, n2 * 16 + 8);
            LinkedList linkedList = Lists.newLinkedList();
            WoodlandMansionPieces.generateMansion(structureManager, blockPos, rotation, linkedList, this.random);
            this.pieces.addAll(linkedList);
            this.calculateBoundingBox();
        }

        @Override
        public void placeInChunk(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
            super.placeInChunk(worldGenLevel, structureFeatureManager, chunkGenerator, random, boundingBox, chunkPos);
            int n = this.boundingBox.y0;
            for (int i = boundingBox.x0; i <= boundingBox.x1; ++i) {
                for (int j = boundingBox.z0; j <= boundingBox.z1; ++j) {
                    Object object2;
                    BlockPos blockPos = new BlockPos(i, n, j);
                    if (worldGenLevel.isEmptyBlock(blockPos) || !this.boundingBox.isInside(blockPos)) continue;
                    boolean bl = false;
                    for (Object object2 : this.pieces) {
                        if (!((StructurePiece)object2).getBoundingBox().isInside(blockPos)) continue;
                        bl = true;
                        break;
                    }
                    if (!bl) continue;
                    for (int k = n - 1; k > 1 && (worldGenLevel.isEmptyBlock((BlockPos)(object2 = new BlockPos(i, k, j))) || worldGenLevel.getBlockState((BlockPos)object2).getMaterial().isLiquid()); --k) {
                        worldGenLevel.setBlock((BlockPos)object2, Blocks.COBBLESTONE.defaultBlockState(), 2);
                    }
                }
            }
        }
    }

}

