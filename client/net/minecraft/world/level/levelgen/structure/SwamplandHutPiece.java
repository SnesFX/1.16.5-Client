/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class SwamplandHutPiece
extends ScatteredFeaturePiece {
    private boolean spawnedWitch;
    private boolean spawnedCat;

    public SwamplandHutPiece(Random random, int n, int n2) {
        super(StructurePieceType.SWAMPLAND_HUT, random, n, 64, n2, 7, 7, 9);
    }

    public SwamplandHutPiece(StructureManager structureManager, CompoundTag compoundTag) {
        super(StructurePieceType.SWAMPLAND_HUT, compoundTag);
        this.spawnedWitch = compoundTag.getBoolean("Witch");
        this.spawnedCat = compoundTag.getBoolean("Cat");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("Witch", this.spawnedWitch);
        compoundTag.putBoolean("Cat", this.spawnedCat);
    }

    @Override
    public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
        int n;
        int n2;
        int n3;
        if (!this.updateAverageGroundHeight(worldGenLevel, boundingBox, 0)) {
            return false;
        }
        this.generateBox(worldGenLevel, boundingBox, 1, 1, 1, 5, 1, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        this.generateBox(worldGenLevel, boundingBox, 1, 4, 2, 5, 4, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        this.generateBox(worldGenLevel, boundingBox, 2, 1, 0, 4, 1, 0, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        this.generateBox(worldGenLevel, boundingBox, 2, 2, 2, 3, 3, 2, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        this.generateBox(worldGenLevel, boundingBox, 1, 2, 3, 1, 3, 6, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        this.generateBox(worldGenLevel, boundingBox, 5, 2, 3, 5, 3, 6, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        this.generateBox(worldGenLevel, boundingBox, 2, 2, 7, 4, 3, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        this.generateBox(worldGenLevel, boundingBox, 1, 0, 2, 1, 3, 2, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
        this.generateBox(worldGenLevel, boundingBox, 5, 0, 2, 5, 3, 2, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
        this.generateBox(worldGenLevel, boundingBox, 1, 0, 7, 1, 3, 7, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
        this.generateBox(worldGenLevel, boundingBox, 5, 0, 7, 5, 3, 7, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
        this.placeBlock(worldGenLevel, Blocks.OAK_FENCE.defaultBlockState(), 2, 3, 2, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.OAK_FENCE.defaultBlockState(), 3, 3, 7, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.AIR.defaultBlockState(), 1, 3, 4, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.AIR.defaultBlockState(), 5, 3, 4, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.AIR.defaultBlockState(), 5, 3, 5, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.POTTED_RED_MUSHROOM.defaultBlockState(), 1, 3, 5, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.CRAFTING_TABLE.defaultBlockState(), 3, 2, 6, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.CAULDRON.defaultBlockState(), 4, 2, 6, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.OAK_FENCE.defaultBlockState(), 1, 2, 1, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.OAK_FENCE.defaultBlockState(), 5, 2, 1, boundingBox);
        BlockState blockState = (BlockState)Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
        BlockState blockState2 = (BlockState)Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST);
        BlockState blockState3 = (BlockState)Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST);
        BlockState blockState4 = (BlockState)Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
        this.generateBox(worldGenLevel, boundingBox, 0, 4, 1, 6, 4, 1, blockState, blockState, false);
        this.generateBox(worldGenLevel, boundingBox, 0, 4, 2, 0, 4, 7, blockState2, blockState2, false);
        this.generateBox(worldGenLevel, boundingBox, 6, 4, 2, 6, 4, 7, blockState3, blockState3, false);
        this.generateBox(worldGenLevel, boundingBox, 0, 4, 8, 6, 4, 8, blockState4, blockState4, false);
        this.placeBlock(worldGenLevel, (BlockState)blockState.setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT), 0, 4, 1, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)blockState.setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT), 6, 4, 1, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)blockState4.setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT), 0, 4, 8, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)blockState4.setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT), 6, 4, 8, boundingBox);
        for (n3 = 2; n3 <= 7; n3 += 5) {
            for (n = 1; n <= 5; n += 4) {
                this.fillColumnDown(worldGenLevel, Blocks.OAK_LOG.defaultBlockState(), n, -1, n3, boundingBox);
            }
        }
        if (!this.spawnedWitch && boundingBox.isInside(new BlockPos(n3 = this.getWorldX(2, 5), n = this.getWorldY(2), n2 = this.getWorldZ(2, 5)))) {
            this.spawnedWitch = true;
            Witch witch = EntityType.WITCH.create(worldGenLevel.getLevel());
            witch.setPersistenceRequired();
            witch.moveTo((double)n3 + 0.5, n, (double)n2 + 0.5, 0.0f, 0.0f);
            witch.finalizeSpawn(worldGenLevel, worldGenLevel.getCurrentDifficultyAt(new BlockPos(n3, n, n2)), MobSpawnType.STRUCTURE, null, null);
            worldGenLevel.addFreshEntityWithPassengers(witch);
        }
        this.spawnCat(worldGenLevel, boundingBox);
        return true;
    }

    private void spawnCat(ServerLevelAccessor serverLevelAccessor, BoundingBox boundingBox) {
        int n;
        int n2;
        int n3;
        if (!this.spawnedCat && boundingBox.isInside(new BlockPos(n2 = this.getWorldX(2, 5), n3 = this.getWorldY(2), n = this.getWorldZ(2, 5)))) {
            this.spawnedCat = true;
            Cat cat = EntityType.CAT.create(serverLevelAccessor.getLevel());
            cat.setPersistenceRequired();
            cat.moveTo((double)n2 + 0.5, n3, (double)n + 0.5, 0.0f, 0.0f);
            cat.finalizeSpawn(serverLevelAccessor, serverLevelAccessor.getCurrentDifficultyAt(new BlockPos(n2, n3, n)), MobSpawnType.STRUCTURE, null, null);
            serverLevelAccessor.addFreshEntityWithPassengers(cat);
        }
    }
}

