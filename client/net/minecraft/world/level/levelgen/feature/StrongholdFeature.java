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
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StrongholdPieces;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class StrongholdFeature
extends StructureFeature<NoneFeatureConfiguration> {
    public StrongholdFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public StructureFeature.StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
        return (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5) -> StrongholdStart.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5);
    }

    @Override
    protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, WorldgenRandom worldgenRandom, int n, int n2, Biome biome, ChunkPos chunkPos, NoneFeatureConfiguration noneFeatureConfiguration) {
        return chunkGenerator.hasStronghold(new ChunkPos(n, n2));
    }

    public static class StrongholdStart
    extends StructureStart<NoneFeatureConfiguration> {
        private final long seed;

        public StrongholdStart(StructureFeature<NoneFeatureConfiguration> structureFeature, int n, int n2, BoundingBox boundingBox, int n3, long l) {
            super(structureFeature, n, n2, boundingBox, n3, l);
            this.seed = l;
        }

        @Override
        public void generatePieces(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, StructureManager structureManager, int n, int n2, Biome biome, NoneFeatureConfiguration noneFeatureConfiguration) {
            StrongholdPieces.StartPiece startPiece;
            int n3 = 0;
            do {
                this.pieces.clear();
                this.boundingBox = BoundingBox.getUnknownBox();
                this.random.setLargeFeatureSeed(this.seed + (long)n3++, n, n2);
                StrongholdPieces.resetPieces();
                startPiece = new StrongholdPieces.StartPiece(this.random, (n << 4) + 2, (n2 << 4) + 2);
                this.pieces.add(startPiece);
                startPiece.addChildren(startPiece, this.pieces, this.random);
                List<StructurePiece> list = startPiece.pendingChildren;
                while (!list.isEmpty()) {
                    int n4 = this.random.nextInt(list.size());
                    StructurePiece structurePiece = list.remove(n4);
                    structurePiece.addChildren(startPiece, this.pieces, this.random);
                }
                this.calculateBoundingBox();
                this.moveBelowSeaLevel(chunkGenerator.getSeaLevel(), this.random, 10);
            } while (this.pieces.isEmpty() || startPiece.portalRoomPiece == null);
        }
    }

}

