/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.MineShaftPieces;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class MineshaftFeature
extends StructureFeature<MineshaftConfiguration> {
    public MineshaftFeature(Codec<MineshaftConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, WorldgenRandom worldgenRandom, int n, int n2, Biome biome, ChunkPos chunkPos, MineshaftConfiguration mineshaftConfiguration) {
        worldgenRandom.setLargeFeatureSeed(l, n, n2);
        double d = mineshaftConfiguration.probability;
        return worldgenRandom.nextDouble() < d;
    }

    @Override
    public StructureFeature.StructureStartFactory<MineshaftConfiguration> getStartFactory() {
        return (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5) -> MineShaftStart.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5);
    }

    public static class MineShaftStart
    extends StructureStart<MineshaftConfiguration> {
        public MineShaftStart(StructureFeature<MineshaftConfiguration> structureFeature, int n, int n2, BoundingBox boundingBox, int n3, long l) {
            super(structureFeature, n, n2, boundingBox, n3, l);
        }

        @Override
        public void generatePieces(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, StructureManager structureManager, int n, int n2, Biome biome, MineshaftConfiguration mineshaftConfiguration) {
            MineShaftPieces.MineShaftRoom mineShaftRoom = new MineShaftPieces.MineShaftRoom(0, this.random, (n << 4) + 2, (n2 << 4) + 2, mineshaftConfiguration.type);
            this.pieces.add(mineShaftRoom);
            mineShaftRoom.addChildren(mineShaftRoom, this.pieces, this.random);
            this.calculateBoundingBox();
            if (mineshaftConfiguration.type == Type.MESA) {
                int n3 = -5;
                int n4 = chunkGenerator.getSeaLevel() - this.boundingBox.y1 + this.boundingBox.getYSpan() / 2 - -5;
                this.boundingBox.move(0, n4, 0);
                for (StructurePiece structurePiece : this.pieces) {
                    structurePiece.move(0, n4, 0);
                }
            } else {
                this.moveBelowSeaLevel(chunkGenerator.getSeaLevel(), this.random, 10);
            }
        }
    }

    public static enum Type implements StringRepresentable
    {
        NORMAL("normal"),
        MESA("mesa");
        
        public static final Codec<Type> CODEC;
        private static final Map<String, Type> BY_NAME;
        private final String name;

        private Type(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        private static Type byName(String string) {
            return BY_NAME.get(string);
        }

        public static Type byId(int n) {
            if (n < 0 || n >= Type.values().length) {
                return NORMAL;
            }
            return Type.values()[n];
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        static {
            CODEC = StringRepresentable.fromEnum(Type::values, Type::byName);
            BY_NAME = Arrays.stream(Type.values()).collect(Collectors.toMap(Type::getName, type -> type));
        }
    }

}

