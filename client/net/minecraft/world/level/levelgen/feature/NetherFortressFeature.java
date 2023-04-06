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
import java.util.Random;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.NetherBridgePieces;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class NetherFortressFeature
extends StructureFeature<NoneFeatureConfiguration> {
    private static final List<MobSpawnSettings.SpawnerData> FORTRESS_ENEMIES = ImmutableList.of((Object)new MobSpawnSettings.SpawnerData(EntityType.BLAZE, 10, 2, 3), (Object)new MobSpawnSettings.SpawnerData(EntityType.ZOMBIFIED_PIGLIN, 5, 4, 4), (Object)new MobSpawnSettings.SpawnerData(EntityType.WITHER_SKELETON, 8, 5, 5), (Object)new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 2, 5, 5), (Object)new MobSpawnSettings.SpawnerData(EntityType.MAGMA_CUBE, 3, 4, 4));

    public NetherFortressFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, WorldgenRandom worldgenRandom, int n, int n2, Biome biome, ChunkPos chunkPos, NoneFeatureConfiguration noneFeatureConfiguration) {
        return worldgenRandom.nextInt(5) < 2;
    }

    @Override
    public StructureFeature.StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
        return (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5) -> NetherBridgeStart.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5);
    }

    @Override
    public List<MobSpawnSettings.SpawnerData> getSpecialEnemies() {
        return FORTRESS_ENEMIES;
    }

    public static class NetherBridgeStart
    extends StructureStart<NoneFeatureConfiguration> {
        public NetherBridgeStart(StructureFeature<NoneFeatureConfiguration> structureFeature, int n, int n2, BoundingBox boundingBox, int n3, long l) {
            super(structureFeature, n, n2, boundingBox, n3, l);
        }

        @Override
        public void generatePieces(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, StructureManager structureManager, int n, int n2, Biome biome, NoneFeatureConfiguration noneFeatureConfiguration) {
            NetherBridgePieces.StartPiece startPiece = new NetherBridgePieces.StartPiece(this.random, (n << 4) + 2, (n2 << 4) + 2);
            this.pieces.add(startPiece);
            startPiece.addChildren(startPiece, this.pieces, this.random);
            List<StructurePiece> list = startPiece.pendingChildren;
            while (!list.isEmpty()) {
                int n3 = this.random.nextInt(list.size());
                StructurePiece structurePiece = list.remove(n3);
                structurePiece.addChildren(startPiece, this.pieces, this.random);
            }
            this.calculateBoundingBox();
            this.moveInsideHeights(this.random, 48, 70);
        }
    }

}

