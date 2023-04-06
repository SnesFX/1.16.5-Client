/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.structure;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.OceanRuinPieces;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class OceanRuinFeature
extends StructureFeature<OceanRuinConfiguration> {
    public OceanRuinFeature(Codec<OceanRuinConfiguration> codec) {
        super(codec);
    }

    @Override
    public StructureFeature.StructureStartFactory<OceanRuinConfiguration> getStartFactory() {
        return (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5) -> OceanRuinStart.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5);
    }

    public static enum Type implements StringRepresentable
    {
        WARM("warm"),
        COLD("cold");
        
        public static final Codec<Type> CODEC;
        private static final Map<String, Type> BY_NAME;
        private final String name;

        private Type(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        @Nullable
        public static Type byName(String string) {
            return BY_NAME.get(string);
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

    public static class OceanRuinStart
    extends StructureStart<OceanRuinConfiguration> {
        public OceanRuinStart(StructureFeature<OceanRuinConfiguration> structureFeature, int n, int n2, BoundingBox boundingBox, int n3, long l) {
            super(structureFeature, n, n2, boundingBox, n3, l);
        }

        @Override
        public void generatePieces(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, StructureManager structureManager, int n, int n2, Biome biome, OceanRuinConfiguration oceanRuinConfiguration) {
            int n3 = n * 16;
            int n4 = n2 * 16;
            BlockPos blockPos = new BlockPos(n3, 90, n4);
            Rotation rotation = Rotation.getRandom(this.random);
            OceanRuinPieces.addPieces(structureManager, blockPos, rotation, this.pieces, this.random, oceanRuinConfiguration);
            this.calculateBoundingBox();
        }
    }

}

