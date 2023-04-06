/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.configurations.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class OceanRuinPieces {
    private static final ResourceLocation[] WARM_RUINS = new ResourceLocation[]{new ResourceLocation("underwater_ruin/warm_1"), new ResourceLocation("underwater_ruin/warm_2"), new ResourceLocation("underwater_ruin/warm_3"), new ResourceLocation("underwater_ruin/warm_4"), new ResourceLocation("underwater_ruin/warm_5"), new ResourceLocation("underwater_ruin/warm_6"), new ResourceLocation("underwater_ruin/warm_7"), new ResourceLocation("underwater_ruin/warm_8")};
    private static final ResourceLocation[] RUINS_BRICK = new ResourceLocation[]{new ResourceLocation("underwater_ruin/brick_1"), new ResourceLocation("underwater_ruin/brick_2"), new ResourceLocation("underwater_ruin/brick_3"), new ResourceLocation("underwater_ruin/brick_4"), new ResourceLocation("underwater_ruin/brick_5"), new ResourceLocation("underwater_ruin/brick_6"), new ResourceLocation("underwater_ruin/brick_7"), new ResourceLocation("underwater_ruin/brick_8")};
    private static final ResourceLocation[] RUINS_CRACKED = new ResourceLocation[]{new ResourceLocation("underwater_ruin/cracked_1"), new ResourceLocation("underwater_ruin/cracked_2"), new ResourceLocation("underwater_ruin/cracked_3"), new ResourceLocation("underwater_ruin/cracked_4"), new ResourceLocation("underwater_ruin/cracked_5"), new ResourceLocation("underwater_ruin/cracked_6"), new ResourceLocation("underwater_ruin/cracked_7"), new ResourceLocation("underwater_ruin/cracked_8")};
    private static final ResourceLocation[] RUINS_MOSSY = new ResourceLocation[]{new ResourceLocation("underwater_ruin/mossy_1"), new ResourceLocation("underwater_ruin/mossy_2"), new ResourceLocation("underwater_ruin/mossy_3"), new ResourceLocation("underwater_ruin/mossy_4"), new ResourceLocation("underwater_ruin/mossy_5"), new ResourceLocation("underwater_ruin/mossy_6"), new ResourceLocation("underwater_ruin/mossy_7"), new ResourceLocation("underwater_ruin/mossy_8")};
    private static final ResourceLocation[] BIG_RUINS_BRICK = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_brick_1"), new ResourceLocation("underwater_ruin/big_brick_2"), new ResourceLocation("underwater_ruin/big_brick_3"), new ResourceLocation("underwater_ruin/big_brick_8")};
    private static final ResourceLocation[] BIG_RUINS_MOSSY = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_mossy_1"), new ResourceLocation("underwater_ruin/big_mossy_2"), new ResourceLocation("underwater_ruin/big_mossy_3"), new ResourceLocation("underwater_ruin/big_mossy_8")};
    private static final ResourceLocation[] BIG_RUINS_CRACKED = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_cracked_1"), new ResourceLocation("underwater_ruin/big_cracked_2"), new ResourceLocation("underwater_ruin/big_cracked_3"), new ResourceLocation("underwater_ruin/big_cracked_8")};
    private static final ResourceLocation[] BIG_WARM_RUINS = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_warm_4"), new ResourceLocation("underwater_ruin/big_warm_5"), new ResourceLocation("underwater_ruin/big_warm_6"), new ResourceLocation("underwater_ruin/big_warm_7")};

    private static ResourceLocation getSmallWarmRuin(Random random) {
        return Util.getRandom(WARM_RUINS, random);
    }

    private static ResourceLocation getBigWarmRuin(Random random) {
        return Util.getRandom(BIG_WARM_RUINS, random);
    }

    public static void addPieces(StructureManager structureManager, BlockPos blockPos, Rotation rotation, List<StructurePiece> list, Random random, OceanRuinConfiguration oceanRuinConfiguration) {
        boolean bl = random.nextFloat() <= oceanRuinConfiguration.largeProbability;
        float f = bl ? 0.9f : 0.8f;
        OceanRuinPieces.addPiece(structureManager, blockPos, rotation, list, random, oceanRuinConfiguration, bl, f);
        if (bl && random.nextFloat() <= oceanRuinConfiguration.clusterProbability) {
            OceanRuinPieces.addClusterRuins(structureManager, random, rotation, blockPos, oceanRuinConfiguration, list);
        }
    }

    private static void addClusterRuins(StructureManager structureManager, Random random, Rotation rotation, BlockPos blockPos, OceanRuinConfiguration oceanRuinConfiguration, List<StructurePiece> list) {
        int n = blockPos.getX();
        int n2 = blockPos.getZ();
        BlockPos blockPos2 = StructureTemplate.transform(new BlockPos(15, 0, 15), Mirror.NONE, rotation, BlockPos.ZERO).offset(n, 0, n2);
        BoundingBox boundingBox = BoundingBox.createProper(n, 0, n2, blockPos2.getX(), 0, blockPos2.getZ());
        BlockPos blockPos3 = new BlockPos(Math.min(n, blockPos2.getX()), 0, Math.min(n2, blockPos2.getZ()));
        List<BlockPos> list2 = OceanRuinPieces.allPositions(random, blockPos3.getX(), blockPos3.getZ());
        int n3 = Mth.nextInt(random, 4, 8);
        for (int i = 0; i < n3; ++i) {
            BlockPos blockPos4;
            BoundingBox boundingBox2;
            int n4;
            int n5;
            BlockPos blockPos5;
            Rotation rotation2;
            int n6;
            if (list2.isEmpty() || (boundingBox2 = BoundingBox.createProper(n4 = (blockPos5 = list2.remove(n6 = random.nextInt(list2.size()))).getX(), 0, n5 = blockPos5.getZ(), (blockPos4 = StructureTemplate.transform(new BlockPos(5, 0, 6), Mirror.NONE, rotation2 = Rotation.getRandom(random), BlockPos.ZERO).offset(n4, 0, n5)).getX(), 0, blockPos4.getZ())).intersects(boundingBox)) continue;
            OceanRuinPieces.addPiece(structureManager, blockPos5, rotation2, list, random, oceanRuinConfiguration, false, 0.8f);
        }
    }

    private static List<BlockPos> allPositions(Random random, int n, int n2) {
        ArrayList arrayList = Lists.newArrayList();
        arrayList.add(new BlockPos(n - 16 + Mth.nextInt(random, 1, 8), 90, n2 + 16 + Mth.nextInt(random, 1, 7)));
        arrayList.add(new BlockPos(n - 16 + Mth.nextInt(random, 1, 8), 90, n2 + Mth.nextInt(random, 1, 7)));
        arrayList.add(new BlockPos(n - 16 + Mth.nextInt(random, 1, 8), 90, n2 - 16 + Mth.nextInt(random, 4, 8)));
        arrayList.add(new BlockPos(n + Mth.nextInt(random, 1, 7), 90, n2 + 16 + Mth.nextInt(random, 1, 7)));
        arrayList.add(new BlockPos(n + Mth.nextInt(random, 1, 7), 90, n2 - 16 + Mth.nextInt(random, 4, 6)));
        arrayList.add(new BlockPos(n + 16 + Mth.nextInt(random, 1, 7), 90, n2 + 16 + Mth.nextInt(random, 3, 8)));
        arrayList.add(new BlockPos(n + 16 + Mth.nextInt(random, 1, 7), 90, n2 + Mth.nextInt(random, 1, 7)));
        arrayList.add(new BlockPos(n + 16 + Mth.nextInt(random, 1, 7), 90, n2 - 16 + Mth.nextInt(random, 4, 8)));
        return arrayList;
    }

    private static void addPiece(StructureManager structureManager, BlockPos blockPos, Rotation rotation, List<StructurePiece> list, Random random, OceanRuinConfiguration oceanRuinConfiguration, boolean bl, float f) {
        if (oceanRuinConfiguration.biomeTemp == OceanRuinFeature.Type.WARM) {
            ResourceLocation resourceLocation = bl ? OceanRuinPieces.getBigWarmRuin(random) : OceanRuinPieces.getSmallWarmRuin(random);
            list.add(new OceanRuinPiece(structureManager, resourceLocation, blockPos, rotation, f, oceanRuinConfiguration.biomeTemp, bl));
        } else if (oceanRuinConfiguration.biomeTemp == OceanRuinFeature.Type.COLD) {
            ResourceLocation[] arrresourceLocation = bl ? BIG_RUINS_BRICK : RUINS_BRICK;
            ResourceLocation[] arrresourceLocation2 = bl ? BIG_RUINS_CRACKED : RUINS_CRACKED;
            ResourceLocation[] arrresourceLocation3 = bl ? BIG_RUINS_MOSSY : RUINS_MOSSY;
            int n = random.nextInt(arrresourceLocation.length);
            list.add(new OceanRuinPiece(structureManager, arrresourceLocation[n], blockPos, rotation, f, oceanRuinConfiguration.biomeTemp, bl));
            list.add(new OceanRuinPiece(structureManager, arrresourceLocation2[n], blockPos, rotation, 0.7f, oceanRuinConfiguration.biomeTemp, bl));
            list.add(new OceanRuinPiece(structureManager, arrresourceLocation3[n], blockPos, rotation, 0.5f, oceanRuinConfiguration.biomeTemp, bl));
        }
    }

    public static class OceanRuinPiece
    extends TemplateStructurePiece {
        private final OceanRuinFeature.Type biomeType;
        private final float integrity;
        private final ResourceLocation templateLocation;
        private final Rotation rotation;
        private final boolean isLarge;

        public OceanRuinPiece(StructureManager structureManager, ResourceLocation resourceLocation, BlockPos blockPos, Rotation rotation, float f, OceanRuinFeature.Type type, boolean bl) {
            super(StructurePieceType.OCEAN_RUIN, 0);
            this.templateLocation = resourceLocation;
            this.templatePosition = blockPos;
            this.rotation = rotation;
            this.integrity = f;
            this.biomeType = type;
            this.isLarge = bl;
            this.loadTemplate(structureManager);
        }

        public OceanRuinPiece(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.OCEAN_RUIN, compoundTag);
            this.templateLocation = new ResourceLocation(compoundTag.getString("Template"));
            this.rotation = Rotation.valueOf(compoundTag.getString("Rot"));
            this.integrity = compoundTag.getFloat("Integrity");
            this.biomeType = OceanRuinFeature.Type.valueOf(compoundTag.getString("BiomeType"));
            this.isLarge = compoundTag.getBoolean("IsLarge");
            this.loadTemplate(structureManager);
        }

        private void loadTemplate(StructureManager structureManager) {
            StructureTemplate structureTemplate = structureManager.getOrCreate(this.templateLocation);
            StructurePlaceSettings structurePlaceSettings = new StructurePlaceSettings().setRotation(this.rotation).setMirror(Mirror.NONE).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
            this.setup(structureTemplate, this.templatePosition, structurePlaceSettings);
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
            super.addAdditionalSaveData(compoundTag);
            compoundTag.putString("Template", this.templateLocation.toString());
            compoundTag.putString("Rot", this.rotation.name());
            compoundTag.putFloat("Integrity", this.integrity);
            compoundTag.putString("BiomeType", this.biomeType.toString());
            compoundTag.putBoolean("IsLarge", this.isLarge);
        }

        @Override
        protected void handleDataMarker(String string, BlockPos blockPos, ServerLevelAccessor serverLevelAccessor, Random random, BoundingBox boundingBox) {
            if ("chest".equals(string)) {
                serverLevelAccessor.setBlock(blockPos, (BlockState)Blocks.CHEST.defaultBlockState().setValue(ChestBlock.WATERLOGGED, serverLevelAccessor.getFluidState(blockPos).is(FluidTags.WATER)), 2);
                BlockEntity blockEntity = serverLevelAccessor.getBlockEntity(blockPos);
                if (blockEntity instanceof ChestBlockEntity) {
                    ((ChestBlockEntity)blockEntity).setLootTable(this.isLarge ? BuiltInLootTables.UNDERWATER_RUIN_BIG : BuiltInLootTables.UNDERWATER_RUIN_SMALL, random.nextLong());
                }
            } else if ("drowned".equals(string)) {
                Drowned drowned = EntityType.DROWNED.create(serverLevelAccessor.getLevel());
                drowned.setPersistenceRequired();
                drowned.moveTo(blockPos, 0.0f, 0.0f);
                drowned.finalizeSpawn(serverLevelAccessor, serverLevelAccessor.getCurrentDifficultyAt(blockPos), MobSpawnType.STRUCTURE, null, null);
                serverLevelAccessor.addFreshEntityWithPassengers(drowned);
                if (blockPos.getY() > serverLevelAccessor.getSeaLevel()) {
                    serverLevelAccessor.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 2);
                } else {
                    serverLevelAccessor.setBlock(blockPos, Blocks.WATER.defaultBlockState(), 2);
                }
            }
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.placeSettings.clearProcessors().addProcessor(new BlockRotProcessor(this.integrity)).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
            int n = worldGenLevel.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, this.templatePosition.getX(), this.templatePosition.getZ());
            this.templatePosition = new BlockPos(this.templatePosition.getX(), n, this.templatePosition.getZ());
            BlockPos blockPos2 = StructureTemplate.transform(new BlockPos(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1), Mirror.NONE, this.rotation, BlockPos.ZERO).offset(this.templatePosition);
            this.templatePosition = new BlockPos(this.templatePosition.getX(), this.getHeight(this.templatePosition, worldGenLevel, blockPos2), this.templatePosition.getZ());
            return super.postProcess(worldGenLevel, structureFeatureManager, chunkGenerator, random, boundingBox, chunkPos, blockPos);
        }

        private int getHeight(BlockPos blockPos, BlockGetter blockGetter, BlockPos blockPos2) {
            int n = blockPos.getY();
            int n2 = 512;
            int n3 = n - 1;
            int n4 = 0;
            for (BlockPos blockPos3 : BlockPos.betweenClosed(blockPos, blockPos2)) {
                int n5 = blockPos3.getX();
                int n6 = blockPos3.getZ();
                int n7 = blockPos.getY() - 1;
                BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(n5, n7, n6);
                BlockState blockState = blockGetter.getBlockState(mutableBlockPos);
                FluidState fluidState = blockGetter.getFluidState(mutableBlockPos);
                while ((blockState.isAir() || fluidState.is(FluidTags.WATER) || blockState.getBlock().is(BlockTags.ICE)) && n7 > 1) {
                    mutableBlockPos.set(n5, --n7, n6);
                    blockState = blockGetter.getBlockState(mutableBlockPos);
                    fluidState = blockGetter.getFluidState(mutableBlockPos);
                }
                n2 = Math.min(n2, n7);
                if (n7 >= n3 - 2) continue;
                ++n4;
            }
            int n8 = Math.abs(blockPos.getX() - blockPos2.getX());
            if (n3 - n2 > 2 && n4 > n8 - 2) {
                n = n2 + 1;
            }
            return n;
        }
    }

}

