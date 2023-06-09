/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class BuriedTreasurePieces {

    public static class BuriedTreasurePiece
    extends StructurePiece {
        public BuriedTreasurePiece(BlockPos blockPos) {
            super(StructurePieceType.BURIED_TREASURE_PIECE, 0);
            this.boundingBox = new BoundingBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }

        public BuriedTreasurePiece(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.BURIED_TREASURE_PIECE, compoundTag);
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            int n = worldGenLevel.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, this.boundingBox.x0, this.boundingBox.z0);
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(this.boundingBox.x0, n, this.boundingBox.z0);
            while (mutableBlockPos.getY() > 0) {
                BlockState blockState = worldGenLevel.getBlockState(mutableBlockPos);
                BlockState blockState2 = worldGenLevel.getBlockState((BlockPos)mutableBlockPos.below());
                if (blockState2 == Blocks.SANDSTONE.defaultBlockState() || blockState2 == Blocks.STONE.defaultBlockState() || blockState2 == Blocks.ANDESITE.defaultBlockState() || blockState2 == Blocks.GRANITE.defaultBlockState() || blockState2 == Blocks.DIORITE.defaultBlockState()) {
                    BlockState blockState3 = blockState.isAir() || this.isLiquid(blockState) ? Blocks.SAND.defaultBlockState() : blockState;
                    for (Direction direction : Direction.values()) {
                        BlockPos blockPos2 = mutableBlockPos.relative(direction);
                        BlockState blockState4 = worldGenLevel.getBlockState(blockPos2);
                        if (!blockState4.isAir() && !this.isLiquid(blockState4)) continue;
                        BlockPos blockPos3 = blockPos2.below();
                        BlockState blockState5 = worldGenLevel.getBlockState(blockPos3);
                        if ((blockState5.isAir() || this.isLiquid(blockState5)) && direction != Direction.UP) {
                            worldGenLevel.setBlock(blockPos2, blockState2, 3);
                            continue;
                        }
                        worldGenLevel.setBlock(blockPos2, blockState3, 3);
                    }
                    this.boundingBox = new BoundingBox(mutableBlockPos.getX(), mutableBlockPos.getY(), mutableBlockPos.getZ(), mutableBlockPos.getX(), mutableBlockPos.getY(), mutableBlockPos.getZ());
                    return this.createChest(worldGenLevel, boundingBox, random, mutableBlockPos, BuiltInLootTables.BURIED_TREASURE, null);
                }
                mutableBlockPos.move(0, -1, 0);
            }
            return false;
        }

        private boolean isLiquid(BlockState blockState) {
            return blockState == Blocks.WATER.defaultBlockState() || blockState == Blocks.LAVA.defaultBlockState();
        }
    }

}

