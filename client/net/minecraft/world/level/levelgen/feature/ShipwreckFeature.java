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
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ShipwreckPieces;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class ShipwreckFeature
extends StructureFeature<ShipwreckConfiguration> {
    public ShipwreckFeature(Codec<ShipwreckConfiguration> codec) {
        super(codec);
    }

    @Override
    public StructureFeature.StructureStartFactory<ShipwreckConfiguration> getStartFactory() {
        return (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5) -> FeatureStart.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5);
    }

    public static class FeatureStart
    extends StructureStart<ShipwreckConfiguration> {
        public FeatureStart(StructureFeature<ShipwreckConfiguration> structureFeature, int n, int n2, BoundingBox boundingBox, int n3, long l) {
            super(structureFeature, n, n2, boundingBox, n3, l);
        }

        @Override
        public void generatePieces(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, StructureManager structureManager, int n, int n2, Biome biome, ShipwreckConfiguration shipwreckConfiguration) {
            Rotation rotation = Rotation.getRandom(this.random);
            BlockPos blockPos = new BlockPos(n * 16, 90, n2 * 16);
            ShipwreckPieces.addPieces(structureManager, blockPos, rotation, this.pieces, this.random, shipwreckConfiguration);
            this.calculateBoundingBox();
        }
    }

}

