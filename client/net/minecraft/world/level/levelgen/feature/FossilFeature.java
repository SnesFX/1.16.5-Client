/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class FossilFeature
extends Feature<NoneFeatureConfiguration> {
    private static final ResourceLocation SPINE_1 = new ResourceLocation("fossil/spine_1");
    private static final ResourceLocation SPINE_2 = new ResourceLocation("fossil/spine_2");
    private static final ResourceLocation SPINE_3 = new ResourceLocation("fossil/spine_3");
    private static final ResourceLocation SPINE_4 = new ResourceLocation("fossil/spine_4");
    private static final ResourceLocation SPINE_1_COAL = new ResourceLocation("fossil/spine_1_coal");
    private static final ResourceLocation SPINE_2_COAL = new ResourceLocation("fossil/spine_2_coal");
    private static final ResourceLocation SPINE_3_COAL = new ResourceLocation("fossil/spine_3_coal");
    private static final ResourceLocation SPINE_4_COAL = new ResourceLocation("fossil/spine_4_coal");
    private static final ResourceLocation SKULL_1 = new ResourceLocation("fossil/skull_1");
    private static final ResourceLocation SKULL_2 = new ResourceLocation("fossil/skull_2");
    private static final ResourceLocation SKULL_3 = new ResourceLocation("fossil/skull_3");
    private static final ResourceLocation SKULL_4 = new ResourceLocation("fossil/skull_4");
    private static final ResourceLocation SKULL_1_COAL = new ResourceLocation("fossil/skull_1_coal");
    private static final ResourceLocation SKULL_2_COAL = new ResourceLocation("fossil/skull_2_coal");
    private static final ResourceLocation SKULL_3_COAL = new ResourceLocation("fossil/skull_3_coal");
    private static final ResourceLocation SKULL_4_COAL = new ResourceLocation("fossil/skull_4_coal");
    private static final ResourceLocation[] fossils = new ResourceLocation[]{SPINE_1, SPINE_2, SPINE_3, SPINE_4, SKULL_1, SKULL_2, SKULL_3, SKULL_4};
    private static final ResourceLocation[] fossilsCoal = new ResourceLocation[]{SPINE_1_COAL, SPINE_2_COAL, SPINE_3_COAL, SPINE_4_COAL, SKULL_1_COAL, SKULL_2_COAL, SKULL_3_COAL, SKULL_4_COAL};

    public FossilFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
        int n;
        Rotation rotation = Rotation.getRandom(random);
        int n2 = random.nextInt(fossils.length);
        StructureManager structureManager = worldGenLevel.getLevel().getServer().getStructureManager();
        StructureTemplate structureTemplate = structureManager.getOrCreate(fossils[n2]);
        StructureTemplate structureTemplate2 = structureManager.getOrCreate(fossilsCoal[n2]);
        ChunkPos chunkPos = new ChunkPos(blockPos);
        BoundingBox boundingBox = new BoundingBox(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), 256, chunkPos.getMaxBlockZ());
        StructurePlaceSettings structurePlaceSettings = new StructurePlaceSettings().setRotation(rotation).setBoundingBox(boundingBox).setRandom(random).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
        BlockPos blockPos2 = structureTemplate.getSize(rotation);
        int n3 = random.nextInt(16 - blockPos2.getX());
        int n4 = random.nextInt(16 - blockPos2.getZ());
        int n5 = 256;
        for (n = 0; n < blockPos2.getX(); ++n) {
            for (int i = 0; i < blockPos2.getZ(); ++i) {
                n5 = Math.min(n5, worldGenLevel.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, blockPos.getX() + n + n3, blockPos.getZ() + i + n4));
            }
        }
        n = Math.max(n5 - 15 - random.nextInt(10), 10);
        BlockPos blockPos3 = structureTemplate.getZeroPositionWithTransform(blockPos.offset(n3, n, n4), Mirror.NONE, rotation);
        BlockRotProcessor blockRotProcessor = new BlockRotProcessor(0.9f);
        structurePlaceSettings.clearProcessors().addProcessor(blockRotProcessor);
        structureTemplate.placeInWorld(worldGenLevel, blockPos3, blockPos3, structurePlaceSettings, random, 4);
        structurePlaceSettings.popProcessor(blockRotProcessor);
        BlockRotProcessor blockRotProcessor2 = new BlockRotProcessor(0.1f);
        structurePlaceSettings.clearProcessors().addProcessor(blockRotProcessor2);
        structureTemplate2.placeInWorld(worldGenLevel, blockPos3, blockPos3, structurePlaceSettings, random, 4);
        return true;
    }
}

