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
import net.minecraft.data.worldgen.Pools;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.BeardedStructureStart;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class JigsawFeature
extends StructureFeature<JigsawConfiguration> {
    private final int startY;
    private final boolean doExpansionHack;
    private final boolean projectStartToHeightmap;

    public JigsawFeature(Codec<JigsawConfiguration> codec, int n, boolean bl, boolean bl2) {
        super(codec);
        this.startY = n;
        this.doExpansionHack = bl;
        this.projectStartToHeightmap = bl2;
    }

    @Override
    public StructureFeature.StructureStartFactory<JigsawConfiguration> getStartFactory() {
        return (structureFeature, n, n2, boundingBox, n3, l) -> new FeatureStart(this, n, n2, boundingBox, n3, l);
    }

    public static class FeatureStart
    extends BeardedStructureStart<JigsawConfiguration> {
        private final JigsawFeature feature;

        public FeatureStart(JigsawFeature jigsawFeature, int n, int n2, BoundingBox boundingBox, int n3, long l) {
            super(jigsawFeature, n, n2, boundingBox, n3, l);
            this.feature = jigsawFeature;
        }

        @Override
        public void generatePieces(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, StructureManager structureManager, int n, int n2, Biome biome, JigsawConfiguration jigsawConfiguration) {
            BlockPos blockPos = new BlockPos(n * 16, this.feature.startY, n2 * 16);
            Pools.bootstrap();
            JigsawPlacement.addPieces(registryAccess, jigsawConfiguration, (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5) -> PoolElementStructurePiece.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5), chunkGenerator, structureManager, blockPos, this.pieces, this.random, this.feature.doExpansionHack, this.feature.projectStartToHeightmap);
            this.calculateBoundingBox();
        }
    }

}

