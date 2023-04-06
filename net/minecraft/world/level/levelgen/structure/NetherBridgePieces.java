/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class NetherBridgePieces {
    private static final PieceWeight[] BRIDGE_PIECE_WEIGHTS = new PieceWeight[]{new PieceWeight(BridgeStraight.class, 30, 0, true), new PieceWeight(BridgeCrossing.class, 10, 4), new PieceWeight(RoomCrossing.class, 10, 4), new PieceWeight(StairsRoom.class, 10, 3), new PieceWeight(MonsterThrone.class, 5, 2), new PieceWeight(CastleEntrance.class, 5, 1)};
    private static final PieceWeight[] CASTLE_PIECE_WEIGHTS = new PieceWeight[]{new PieceWeight(CastleSmallCorridorPiece.class, 25, 0, true), new PieceWeight(CastleSmallCorridorCrossingPiece.class, 15, 5), new PieceWeight(CastleSmallCorridorRightTurnPiece.class, 5, 10), new PieceWeight(CastleSmallCorridorLeftTurnPiece.class, 5, 10), new PieceWeight(CastleCorridorStairsPiece.class, 10, 3, true), new PieceWeight(CastleCorridorTBalconyPiece.class, 7, 2), new PieceWeight(CastleStalkRoom.class, 5, 2)};

    private static NetherBridgePiece findAndCreateBridgePieceFactory(PieceWeight pieceWeight, List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction, int n4) {
        Class<? extends NetherBridgePiece> class_ = pieceWeight.pieceClass;
        NetherBridgePiece netherBridgePiece = null;
        if (class_ == BridgeStraight.class) {
            netherBridgePiece = BridgeStraight.createPiece(list, random, n, n2, n3, direction, n4);
        } else if (class_ == BridgeCrossing.class) {
            netherBridgePiece = BridgeCrossing.createPiece(list, n, n2, n3, direction, n4);
        } else if (class_ == RoomCrossing.class) {
            netherBridgePiece = RoomCrossing.createPiece(list, n, n2, n3, direction, n4);
        } else if (class_ == StairsRoom.class) {
            netherBridgePiece = StairsRoom.createPiece(list, n, n2, n3, n4, direction);
        } else if (class_ == MonsterThrone.class) {
            netherBridgePiece = MonsterThrone.createPiece(list, n, n2, n3, n4, direction);
        } else if (class_ == CastleEntrance.class) {
            netherBridgePiece = CastleEntrance.createPiece(list, random, n, n2, n3, direction, n4);
        } else if (class_ == CastleSmallCorridorPiece.class) {
            netherBridgePiece = CastleSmallCorridorPiece.createPiece(list, n, n2, n3, direction, n4);
        } else if (class_ == CastleSmallCorridorRightTurnPiece.class) {
            netherBridgePiece = CastleSmallCorridorRightTurnPiece.createPiece(list, random, n, n2, n3, direction, n4);
        } else if (class_ == CastleSmallCorridorLeftTurnPiece.class) {
            netherBridgePiece = CastleSmallCorridorLeftTurnPiece.createPiece(list, random, n, n2, n3, direction, n4);
        } else if (class_ == CastleCorridorStairsPiece.class) {
            netherBridgePiece = CastleCorridorStairsPiece.createPiece(list, n, n2, n3, direction, n4);
        } else if (class_ == CastleCorridorTBalconyPiece.class) {
            netherBridgePiece = CastleCorridorTBalconyPiece.createPiece(list, n, n2, n3, direction, n4);
        } else if (class_ == CastleSmallCorridorCrossingPiece.class) {
            netherBridgePiece = CastleSmallCorridorCrossingPiece.createPiece(list, n, n2, n3, direction, n4);
        } else if (class_ == CastleStalkRoom.class) {
            netherBridgePiece = CastleStalkRoom.createPiece(list, n, n2, n3, direction, n4);
        }
        return netherBridgePiece;
    }

    public static class CastleCorridorTBalconyPiece
    extends NetherBridgePiece {
        public CastleCorridorTBalconyPiece(int n, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_T_BALCONY, n);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public CastleCorridorTBalconyPiece(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_T_BALCONY, compoundTag);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            int n = 1;
            Direction direction = this.getOrientation();
            if (direction == Direction.WEST || direction == Direction.NORTH) {
                n = 5;
            }
            this.generateChildLeft((StartPiece)structurePiece, list, random, 0, n, random.nextInt(8) > 0);
            this.generateChildRight((StartPiece)structurePiece, list, random, 0, n, random.nextInt(8) > 0);
        }

        public static CastleCorridorTBalconyPiece createPiece(List<StructurePiece> list, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -3, 0, 0, 9, 7, 9, direction);
            if (!CastleCorridorTBalconyPiece.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new CastleCorridorTBalconyPiece(n4, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            BlockState blockState = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
            BlockState blockState2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 0, 8, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 0, 8, 5, 8, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 6, 0, 8, 6, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 0, 2, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 6, 2, 0, 8, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 1, 3, 0, 1, 4, 0, blockState2, blockState2, false);
            this.generateBox(worldGenLevel, boundingBox, 7, 3, 0, 7, 4, 0, blockState2, blockState2, false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 4, 8, 2, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 1, 1, 4, 2, 2, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 6, 1, 4, 7, 2, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 1, 3, 8, 7, 3, 8, blockState2, blockState2, false);
            this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, true)).setValue(FenceBlock.SOUTH, true), 0, 3, 8, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.SOUTH, true), 8, 3, 8, boundingBox);
            this.generateBox(worldGenLevel, boundingBox, 0, 3, 6, 0, 3, 7, blockState, blockState, false);
            this.generateBox(worldGenLevel, boundingBox, 8, 3, 6, 8, 3, 7, blockState, blockState, false);
            this.generateBox(worldGenLevel, boundingBox, 0, 3, 4, 0, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 8, 3, 4, 8, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 1, 3, 5, 2, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 6, 3, 5, 7, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 1, 4, 5, 1, 5, 5, blockState2, blockState2, false);
            this.generateBox(worldGenLevel, boundingBox, 7, 4, 5, 7, 5, 5, blockState2, blockState2, false);
            for (int i = 0; i <= 5; ++i) {
                for (int j = 0; j <= 8; ++j) {
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), j, -1, i, boundingBox);
                }
            }
            return true;
        }
    }

    public static class CastleCorridorStairsPiece
    extends NetherBridgePiece {
        public CastleCorridorStairsPiece(int n, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_STAIRS, n);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public CastleCorridorStairsPiece(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_STAIRS, compoundTag);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.generateChildForward((StartPiece)structurePiece, list, random, 1, 0, true);
        }

        public static CastleCorridorStairsPiece createPiece(List<StructurePiece> list, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -1, -7, 0, 5, 14, 10, direction);
            if (!CastleCorridorStairsPiece.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new CastleCorridorStairsPiece(n4, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            BlockState blockState = (BlockState)Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
            BlockState blockState2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
            for (int i = 0; i <= 9; ++i) {
                int n = Math.max(1, 7 - i);
                int n2 = Math.min(Math.max(n + 5, 14 - i), 13);
                int n3 = i;
                this.generateBox(worldGenLevel, boundingBox, 0, 0, n3, 4, n, n3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                this.generateBox(worldGenLevel, boundingBox, 1, n + 1, n3, 3, n2 - 1, n3, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
                if (i <= 6) {
                    this.placeBlock(worldGenLevel, blockState, 1, n + 1, n3, boundingBox);
                    this.placeBlock(worldGenLevel, blockState, 2, n + 1, n3, boundingBox);
                    this.placeBlock(worldGenLevel, blockState, 3, n + 1, n3, boundingBox);
                }
                this.generateBox(worldGenLevel, boundingBox, 0, n2, n3, 4, n2, n3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                this.generateBox(worldGenLevel, boundingBox, 0, n + 1, n3, 0, n2 - 1, n3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                this.generateBox(worldGenLevel, boundingBox, 4, n + 1, n3, 4, n2 - 1, n3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                if ((i & 1) == 0) {
                    this.generateBox(worldGenLevel, boundingBox, 0, n + 2, n3, 0, n + 3, n3, blockState2, blockState2, false);
                    this.generateBox(worldGenLevel, boundingBox, 4, n + 2, n3, 4, n + 3, n3, blockState2, blockState2, false);
                }
                for (int j = 0; j <= 4; ++j) {
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), j, -1, n3, boundingBox);
                }
            }
            return true;
        }
    }

    public static class CastleSmallCorridorLeftTurnPiece
    extends NetherBridgePiece {
        private boolean isNeedingChest;

        public CastleSmallCorridorLeftTurnPiece(int n, Random random, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_LEFT_TURN, n);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
            this.isNeedingChest = random.nextInt(3) == 0;
        }

        public CastleSmallCorridorLeftTurnPiece(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_LEFT_TURN, compoundTag);
            this.isNeedingChest = compoundTag.getBoolean("Chest");
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
            super.addAdditionalSaveData(compoundTag);
            compoundTag.putBoolean("Chest", this.isNeedingChest);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.generateChildLeft((StartPiece)structurePiece, list, random, 0, 1, true);
        }

        public static CastleSmallCorridorLeftTurnPiece createPiece(List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -1, 0, 0, 5, 7, 5, direction);
            if (!CastleSmallCorridorLeftTurnPiece.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new CastleSmallCorridorLeftTurnPiece(n4, random, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            BlockState blockState = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
            BlockState blockState2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
            this.generateBox(worldGenLevel, boundingBox, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 4, 3, 1, 4, 4, 1, blockState2, blockState2, false);
            this.generateBox(worldGenLevel, boundingBox, 4, 3, 3, 4, 4, 3, blockState2, blockState2, false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 4, 3, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 1, 3, 4, 1, 4, 4, blockState, blockState, false);
            this.generateBox(worldGenLevel, boundingBox, 3, 3, 4, 3, 4, 4, blockState, blockState, false);
            if (this.isNeedingChest && boundingBox.isInside(new BlockPos(this.getWorldX(3, 3), this.getWorldY(2), this.getWorldZ(3, 3)))) {
                this.isNeedingChest = false;
                this.createChest(worldGenLevel, boundingBox, random, 3, 2, 3, BuiltInLootTables.NETHER_BRIDGE);
            }
            this.generateBox(worldGenLevel, boundingBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            for (int i = 0; i <= 4; ++i) {
                for (int j = 0; j <= 4; ++j) {
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, boundingBox);
                }
            }
            return true;
        }
    }

    public static class CastleSmallCorridorRightTurnPiece
    extends NetherBridgePiece {
        private boolean isNeedingChest;

        public CastleSmallCorridorRightTurnPiece(int n, Random random, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_RIGHT_TURN, n);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
            this.isNeedingChest = random.nextInt(3) == 0;
        }

        public CastleSmallCorridorRightTurnPiece(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_RIGHT_TURN, compoundTag);
            this.isNeedingChest = compoundTag.getBoolean("Chest");
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
            super.addAdditionalSaveData(compoundTag);
            compoundTag.putBoolean("Chest", this.isNeedingChest);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.generateChildRight((StartPiece)structurePiece, list, random, 0, 1, true);
        }

        public static CastleSmallCorridorRightTurnPiece createPiece(List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -1, 0, 0, 5, 7, 5, direction);
            if (!CastleSmallCorridorRightTurnPiece.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new CastleSmallCorridorRightTurnPiece(n4, random, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            BlockState blockState = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
            BlockState blockState2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 3, 1, 0, 4, 1, blockState2, blockState2, false);
            this.generateBox(worldGenLevel, boundingBox, 0, 3, 3, 0, 4, 3, blockState2, blockState2, false);
            this.generateBox(worldGenLevel, boundingBox, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 1, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 1, 3, 4, 1, 4, 4, blockState, blockState, false);
            this.generateBox(worldGenLevel, boundingBox, 3, 3, 4, 3, 4, 4, blockState, blockState, false);
            if (this.isNeedingChest && boundingBox.isInside(new BlockPos(this.getWorldX(1, 3), this.getWorldY(2), this.getWorldZ(1, 3)))) {
                this.isNeedingChest = false;
                this.createChest(worldGenLevel, boundingBox, random, 1, 2, 3, BuiltInLootTables.NETHER_BRIDGE);
            }
            this.generateBox(worldGenLevel, boundingBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            for (int i = 0; i <= 4; ++i) {
                for (int j = 0; j <= 4; ++j) {
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, boundingBox);
                }
            }
            return true;
        }
    }

    public static class CastleSmallCorridorCrossingPiece
    extends NetherBridgePiece {
        public CastleSmallCorridorCrossingPiece(int n, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_CROSSING, n);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public CastleSmallCorridorCrossingPiece(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_CROSSING, compoundTag);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.generateChildForward((StartPiece)structurePiece, list, random, 1, 0, true);
            this.generateChildLeft((StartPiece)structurePiece, list, random, 0, 1, true);
            this.generateChildRight((StartPiece)structurePiece, list, random, 0, 1, true);
        }

        public static CastleSmallCorridorCrossingPiece createPiece(List<StructurePiece> list, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -1, 0, 0, 5, 7, 5, direction);
            if (!CastleSmallCorridorCrossingPiece.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new CastleSmallCorridorCrossingPiece(n4, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 4, 0, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 4, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            for (int i = 0; i <= 4; ++i) {
                for (int j = 0; j <= 4; ++j) {
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, boundingBox);
                }
            }
            return true;
        }
    }

    public static class CastleSmallCorridorPiece
    extends NetherBridgePiece {
        public CastleSmallCorridorPiece(int n, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR, n);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public CastleSmallCorridorPiece(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR, compoundTag);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.generateChildForward((StartPiece)structurePiece, list, random, 1, 0, true);
        }

        public static CastleSmallCorridorPiece createPiece(List<StructurePiece> list, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -1, 0, 0, 5, 7, 5, direction);
            if (!CastleSmallCorridorPiece.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new CastleSmallCorridorPiece(n4, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            BlockState blockState = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 3, 1, 0, 4, 1, blockState, blockState, false);
            this.generateBox(worldGenLevel, boundingBox, 0, 3, 3, 0, 4, 3, blockState, blockState, false);
            this.generateBox(worldGenLevel, boundingBox, 4, 3, 1, 4, 4, 1, blockState, blockState, false);
            this.generateBox(worldGenLevel, boundingBox, 4, 3, 3, 4, 4, 3, blockState, blockState, false);
            this.generateBox(worldGenLevel, boundingBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            for (int i = 0; i <= 4; ++i) {
                for (int j = 0; j <= 4; ++j) {
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, boundingBox);
                }
            }
            return true;
        }
    }

    public static class CastleStalkRoom
    extends NetherBridgePiece {
        public CastleStalkRoom(int n, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_STALK_ROOM, n);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public CastleStalkRoom(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_STALK_ROOM, compoundTag);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.generateChildForward((StartPiece)structurePiece, list, random, 5, 3, true);
            this.generateChildForward((StartPiece)structurePiece, list, random, 5, 11, true);
        }

        public static CastleStalkRoom createPiece(List<StructurePiece> list, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -5, -3, 0, 13, 14, 13, direction);
            if (!CastleStalkRoom.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new CastleStalkRoom(n4, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            int n;
            int n2;
            int n3;
            int n4;
            this.generateBox(worldGenLevel, boundingBox, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 5, 0, 12, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            BlockState blockState = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
            BlockState blockState2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
            BlockState blockState3 = (BlockState)blockState2.setValue(FenceBlock.WEST, true);
            BlockState blockState4 = (BlockState)blockState2.setValue(FenceBlock.EAST, true);
            for (n2 = 1; n2 <= 11; n2 += 2) {
                this.generateBox(worldGenLevel, boundingBox, n2, 10, 0, n2, 11, 0, blockState, blockState, false);
                this.generateBox(worldGenLevel, boundingBox, n2, 10, 12, n2, 11, 12, blockState, blockState, false);
                this.generateBox(worldGenLevel, boundingBox, 0, 10, n2, 0, 11, n2, blockState2, blockState2, false);
                this.generateBox(worldGenLevel, boundingBox, 12, 10, n2, 12, 11, n2, blockState2, blockState2, false);
                this.placeBlock(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), n2, 13, 0, boundingBox);
                this.placeBlock(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), n2, 13, 12, boundingBox);
                this.placeBlock(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), 0, 13, n2, boundingBox);
                this.placeBlock(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), 12, 13, n2, boundingBox);
                if (n2 == 11) continue;
                this.placeBlock(worldGenLevel, blockState, n2 + 1, 13, 0, boundingBox);
                this.placeBlock(worldGenLevel, blockState, n2 + 1, 13, 12, boundingBox);
                this.placeBlock(worldGenLevel, blockState2, 0, 13, n2 + 1, boundingBox);
                this.placeBlock(worldGenLevel, blockState2, 12, 13, n2 + 1, boundingBox);
            }
            this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.EAST, true), 0, 13, 0, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, true)).setValue(FenceBlock.EAST, true), 0, 13, 12, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, true)).setValue(FenceBlock.WEST, true), 12, 13, 12, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.WEST, true), 12, 13, 0, boundingBox);
            for (n2 = 3; n2 <= 9; n2 += 2) {
                this.generateBox(worldGenLevel, boundingBox, 1, 7, n2, 1, 8, n2, blockState3, blockState3, false);
                this.generateBox(worldGenLevel, boundingBox, 11, 7, n2, 11, 8, n2, blockState4, blockState4, false);
            }
            BlockState blockState5 = (BlockState)Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
            for (n4 = 0; n4 <= 6; ++n4) {
                int n5 = n4 + 4;
                for (n3 = 5; n3 <= 7; ++n3) {
                    this.placeBlock(worldGenLevel, blockState5, n3, 5 + n4, n5, boundingBox);
                }
                if (n5 >= 5 && n5 <= 8) {
                    this.generateBox(worldGenLevel, boundingBox, 5, 5, n5, 7, n4 + 4, n5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                } else if (n5 >= 9 && n5 <= 10) {
                    this.generateBox(worldGenLevel, boundingBox, 5, 8, n5, 7, n4 + 4, n5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                }
                if (n4 < 1) continue;
                this.generateBox(worldGenLevel, boundingBox, 5, 6 + n4, n5, 7, 9 + n4, n5, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            }
            for (n4 = 5; n4 <= 7; ++n4) {
                this.placeBlock(worldGenLevel, blockState5, n4, 12, 11, boundingBox);
            }
            this.generateBox(worldGenLevel, boundingBox, 5, 6, 7, 5, 7, 7, blockState4, blockState4, false);
            this.generateBox(worldGenLevel, boundingBox, 7, 6, 7, 7, 7, 7, blockState3, blockState3, false);
            this.generateBox(worldGenLevel, boundingBox, 5, 13, 12, 7, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 2, 5, 2, 3, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 2, 5, 9, 3, 5, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 2, 5, 4, 2, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 9, 5, 2, 10, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 9, 5, 9, 10, 5, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 10, 5, 4, 10, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            BlockState blockState6 = (BlockState)blockState5.setValue(StairBlock.FACING, Direction.EAST);
            BlockState blockState7 = (BlockState)blockState5.setValue(StairBlock.FACING, Direction.WEST);
            this.placeBlock(worldGenLevel, blockState7, 4, 5, 2, boundingBox);
            this.placeBlock(worldGenLevel, blockState7, 4, 5, 3, boundingBox);
            this.placeBlock(worldGenLevel, blockState7, 4, 5, 9, boundingBox);
            this.placeBlock(worldGenLevel, blockState7, 4, 5, 10, boundingBox);
            this.placeBlock(worldGenLevel, blockState6, 8, 5, 2, boundingBox);
            this.placeBlock(worldGenLevel, blockState6, 8, 5, 3, boundingBox);
            this.placeBlock(worldGenLevel, blockState6, 8, 5, 9, boundingBox);
            this.placeBlock(worldGenLevel, blockState6, 8, 5, 10, boundingBox);
            this.generateBox(worldGenLevel, boundingBox, 3, 4, 4, 4, 4, 8, Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 8, 4, 4, 9, 4, 8, Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 3, 5, 4, 4, 5, 8, Blocks.NETHER_WART.defaultBlockState(), Blocks.NETHER_WART.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 8, 5, 4, 9, 5, 8, Blocks.NETHER_WART.defaultBlockState(), Blocks.NETHER_WART.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            for (n3 = 4; n3 <= 8; ++n3) {
                for (n = 0; n <= 2; ++n) {
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), n3, -1, n, boundingBox);
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), n3, -1, 12 - n, boundingBox);
                }
            }
            for (n3 = 0; n3 <= 2; ++n3) {
                for (n = 4; n <= 8; ++n) {
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), n3, -1, n, boundingBox);
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), 12 - n3, -1, n, boundingBox);
                }
            }
            return true;
        }
    }

    public static class CastleEntrance
    extends NetherBridgePiece {
        public CastleEntrance(int n, Random random, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_ENTRANCE, n);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public CastleEntrance(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_CASTLE_ENTRANCE, compoundTag);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.generateChildForward((StartPiece)structurePiece, list, random, 5, 3, true);
        }

        public static CastleEntrance createPiece(List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -5, -3, 0, 13, 14, 13, direction);
            if (!CastleEntrance.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new CastleEntrance(n4, random, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            int n;
            int n2;
            this.generateBox(worldGenLevel, boundingBox, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 5, 0, 12, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 5, 8, 0, 7, 8, 0, Blocks.NETHER_BRICK_FENCE.defaultBlockState(), Blocks.NETHER_BRICK_FENCE.defaultBlockState(), false);
            BlockState blockState = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
            BlockState blockState2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
            for (n2 = 1; n2 <= 11; n2 += 2) {
                this.generateBox(worldGenLevel, boundingBox, n2, 10, 0, n2, 11, 0, blockState, blockState, false);
                this.generateBox(worldGenLevel, boundingBox, n2, 10, 12, n2, 11, 12, blockState, blockState, false);
                this.generateBox(worldGenLevel, boundingBox, 0, 10, n2, 0, 11, n2, blockState2, blockState2, false);
                this.generateBox(worldGenLevel, boundingBox, 12, 10, n2, 12, 11, n2, blockState2, blockState2, false);
                this.placeBlock(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), n2, 13, 0, boundingBox);
                this.placeBlock(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), n2, 13, 12, boundingBox);
                this.placeBlock(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), 0, 13, n2, boundingBox);
                this.placeBlock(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), 12, 13, n2, boundingBox);
                if (n2 == 11) continue;
                this.placeBlock(worldGenLevel, blockState, n2 + 1, 13, 0, boundingBox);
                this.placeBlock(worldGenLevel, blockState, n2 + 1, 13, 12, boundingBox);
                this.placeBlock(worldGenLevel, blockState2, 0, 13, n2 + 1, boundingBox);
                this.placeBlock(worldGenLevel, blockState2, 12, 13, n2 + 1, boundingBox);
            }
            this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.EAST, true), 0, 13, 0, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, true)).setValue(FenceBlock.EAST, true), 0, 13, 12, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, true)).setValue(FenceBlock.WEST, true), 12, 13, 12, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.WEST, true), 12, 13, 0, boundingBox);
            for (n2 = 3; n2 <= 9; n2 += 2) {
                this.generateBox(worldGenLevel, boundingBox, 1, 7, n2, 1, 8, n2, (BlockState)blockState2.setValue(FenceBlock.WEST, true), (BlockState)blockState2.setValue(FenceBlock.WEST, true), false);
                this.generateBox(worldGenLevel, boundingBox, 11, 7, n2, 11, 8, n2, (BlockState)blockState2.setValue(FenceBlock.EAST, true), (BlockState)blockState2.setValue(FenceBlock.EAST, true), false);
            }
            this.generateBox(worldGenLevel, boundingBox, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            for (n2 = 4; n2 <= 8; ++n2) {
                for (n = 0; n <= 2; ++n) {
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), n2, -1, n, boundingBox);
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), n2, -1, 12 - n, boundingBox);
                }
            }
            for (n2 = 0; n2 <= 2; ++n2) {
                for (n = 4; n <= 8; ++n) {
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), n2, -1, n, boundingBox);
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), 12 - n2, -1, n, boundingBox);
                }
            }
            this.generateBox(worldGenLevel, boundingBox, 5, 5, 5, 7, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 6, 1, 6, 6, 4, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.placeBlock(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), 6, 0, 6, boundingBox);
            this.placeBlock(worldGenLevel, Blocks.LAVA.defaultBlockState(), 6, 5, 6, boundingBox);
            BlockPos blockPos2 = new BlockPos(this.getWorldX(6, 6), this.getWorldY(5), this.getWorldZ(6, 6));
            if (boundingBox.isInside(blockPos2)) {
                worldGenLevel.getLiquidTicks().scheduleTick(blockPos2, Fluids.LAVA, 0);
            }
            return true;
        }
    }

    public static class MonsterThrone
    extends NetherBridgePiece {
        private boolean hasPlacedSpawner;

        public MonsterThrone(int n, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_MONSTER_THRONE, n);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public MonsterThrone(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_MONSTER_THRONE, compoundTag);
            this.hasPlacedSpawner = compoundTag.getBoolean("Mob");
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
            super.addAdditionalSaveData(compoundTag);
            compoundTag.putBoolean("Mob", this.hasPlacedSpawner);
        }

        public static MonsterThrone createPiece(List<StructurePiece> list, int n, int n2, int n3, int n4, Direction direction) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -2, 0, 0, 7, 8, 9, direction);
            if (!MonsterThrone.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new MonsterThrone(n4, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            BlockPos blockPos2;
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 0, 6, 7, 7, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 1, 0, 0, 5, 1, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 1, 2, 1, 5, 2, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 1, 3, 2, 5, 3, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 1, 4, 3, 5, 4, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 1, 2, 0, 1, 4, 2, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 5, 2, 0, 5, 4, 2, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 1, 5, 2, 1, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 5, 5, 2, 5, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 5, 3, 0, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 6, 5, 3, 6, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 1, 5, 8, 5, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            BlockState blockState = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
            BlockState blockState2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
            this.placeBlock(worldGenLevel, (BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true), 1, 6, 3, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, true), 5, 6, 3, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, true)).setValue(FenceBlock.NORTH, true), 0, 6, 3, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.NORTH, true), 6, 6, 3, boundingBox);
            this.generateBox(worldGenLevel, boundingBox, 0, 6, 4, 0, 6, 7, blockState2, blockState2, false);
            this.generateBox(worldGenLevel, boundingBox, 6, 6, 4, 6, 6, 7, blockState2, blockState2, false);
            this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, true)).setValue(FenceBlock.SOUTH, true), 0, 6, 8, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.SOUTH, true), 6, 6, 8, boundingBox);
            this.generateBox(worldGenLevel, boundingBox, 1, 6, 8, 5, 6, 8, blockState, blockState, false);
            this.placeBlock(worldGenLevel, (BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, true), 1, 7, 8, boundingBox);
            this.generateBox(worldGenLevel, boundingBox, 2, 7, 8, 4, 7, 8, blockState, blockState, false);
            this.placeBlock(worldGenLevel, (BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true), 5, 7, 8, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, true), 2, 8, 8, boundingBox);
            this.placeBlock(worldGenLevel, blockState, 3, 8, 8, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true), 4, 8, 8, boundingBox);
            if (!this.hasPlacedSpawner && boundingBox.isInside(blockPos2 = new BlockPos(this.getWorldX(3, 5), this.getWorldY(5), this.getWorldZ(3, 5)))) {
                this.hasPlacedSpawner = true;
                worldGenLevel.setBlock(blockPos2, Blocks.SPAWNER.defaultBlockState(), 2);
                BlockEntity blockEntity = worldGenLevel.getBlockEntity(blockPos2);
                if (blockEntity instanceof SpawnerBlockEntity) {
                    ((SpawnerBlockEntity)blockEntity).getSpawner().setEntityId(EntityType.BLAZE);
                }
            }
            for (int i = 0; i <= 6; ++i) {
                for (int j = 0; j <= 6; ++j) {
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, boundingBox);
                }
            }
            return true;
        }
    }

    public static class StairsRoom
    extends NetherBridgePiece {
        public StairsRoom(int n, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_STAIRS_ROOM, n);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public StairsRoom(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_STAIRS_ROOM, compoundTag);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.generateChildRight((StartPiece)structurePiece, list, random, 6, 2, false);
        }

        public static StairsRoom createPiece(List<StructurePiece> list, int n, int n2, int n3, int n4, Direction direction) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -2, 0, 0, 7, 11, 7, direction);
            if (!StairsRoom.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new StairsRoom(n4, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 0, 6, 10, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 0, 1, 8, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 5, 2, 0, 6, 8, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 1, 0, 8, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 6, 2, 1, 6, 8, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 1, 2, 6, 5, 8, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            BlockState blockState = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
            BlockState blockState2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
            this.generateBox(worldGenLevel, boundingBox, 0, 3, 2, 0, 5, 4, blockState2, blockState2, false);
            this.generateBox(worldGenLevel, boundingBox, 6, 3, 2, 6, 5, 2, blockState2, blockState2, false);
            this.generateBox(worldGenLevel, boundingBox, 6, 3, 4, 6, 5, 4, blockState2, blockState2, false);
            this.placeBlock(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), 5, 2, 5, boundingBox);
            this.generateBox(worldGenLevel, boundingBox, 4, 2, 5, 4, 3, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 3, 2, 5, 3, 4, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 2, 2, 5, 2, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 1, 2, 5, 1, 6, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 1, 7, 1, 5, 7, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 6, 8, 2, 6, 8, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 2, 6, 0, 4, 8, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 2, 5, 0, 4, 5, 0, blockState, blockState, false);
            for (int i = 0; i <= 6; ++i) {
                for (int j = 0; j <= 6; ++j) {
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, boundingBox);
                }
            }
            return true;
        }
    }

    public static class RoomCrossing
    extends NetherBridgePiece {
        public RoomCrossing(int n, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_ROOM_CROSSING, n);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public RoomCrossing(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_ROOM_CROSSING, compoundTag);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.generateChildForward((StartPiece)structurePiece, list, random, 2, 0, false);
            this.generateChildLeft((StartPiece)structurePiece, list, random, 0, 2, false);
            this.generateChildRight((StartPiece)structurePiece, list, random, 0, 2, false);
        }

        public static RoomCrossing createPiece(List<StructurePiece> list, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -2, 0, 0, 7, 9, 7, direction);
            if (!RoomCrossing.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new RoomCrossing(n4, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 0, 6, 7, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 0, 1, 6, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 6, 1, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 5, 2, 0, 6, 6, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 5, 2, 6, 6, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 0, 0, 6, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 5, 0, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 6, 2, 0, 6, 6, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 6, 2, 5, 6, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            BlockState blockState = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
            BlockState blockState2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
            this.generateBox(worldGenLevel, boundingBox, 2, 6, 0, 4, 6, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 2, 5, 0, 4, 5, 0, blockState, blockState, false);
            this.generateBox(worldGenLevel, boundingBox, 2, 6, 6, 4, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 2, 5, 6, 4, 5, 6, blockState, blockState, false);
            this.generateBox(worldGenLevel, boundingBox, 0, 6, 2, 0, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 5, 2, 0, 5, 4, blockState2, blockState2, false);
            this.generateBox(worldGenLevel, boundingBox, 6, 6, 2, 6, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 6, 5, 2, 6, 5, 4, blockState2, blockState2, false);
            for (int i = 0; i <= 6; ++i) {
                for (int j = 0; j <= 6; ++j) {
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, boundingBox);
                }
            }
            return true;
        }
    }

    public static class BridgeCrossing
    extends NetherBridgePiece {
        public BridgeCrossing(int n, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, n);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        protected BridgeCrossing(Random random, int n, int n2) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, 0);
            this.setOrientation(Direction.Plane.HORIZONTAL.getRandomDirection(random));
            this.boundingBox = this.getOrientation().getAxis() == Direction.Axis.Z ? new BoundingBox(n, 64, n2, n + 19 - 1, 73, n2 + 19 - 1) : new BoundingBox(n, 64, n2, n + 19 - 1, 73, n2 + 19 - 1);
        }

        protected BridgeCrossing(StructurePieceType structurePieceType, CompoundTag compoundTag) {
            super(structurePieceType, compoundTag);
        }

        public BridgeCrossing(StructureManager structureManager, CompoundTag compoundTag) {
            this(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, compoundTag);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.generateChildForward((StartPiece)structurePiece, list, random, 8, 3, false);
            this.generateChildLeft((StartPiece)structurePiece, list, random, 3, 8, false);
            this.generateChildRight((StartPiece)structurePiece, list, random, 3, 8, false);
        }

        public static BridgeCrossing createPiece(List<StructurePiece> list, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -8, -3, 0, 19, 10, 19, direction);
            if (!BridgeCrossing.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new BridgeCrossing(n4, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            int n;
            int n2;
            this.generateBox(worldGenLevel, boundingBox, 7, 3, 0, 11, 4, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 3, 7, 18, 4, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 8, 5, 0, 10, 7, 18, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 5, 8, 18, 7, 10, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 7, 5, 0, 7, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 7, 5, 11, 7, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 11, 5, 0, 11, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 11, 5, 11, 11, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 5, 7, 7, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 11, 5, 7, 18, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 5, 11, 7, 5, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 11, 5, 11, 18, 5, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 7, 2, 0, 11, 2, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 7, 2, 13, 11, 2, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 7, 0, 0, 11, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 7, 0, 15, 11, 1, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            for (n2 = 7; n2 <= 11; ++n2) {
                for (n = 0; n <= 2; ++n) {
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), n2, -1, n, boundingBox);
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), n2, -1, 18 - n, boundingBox);
                }
            }
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 7, 5, 2, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 13, 2, 7, 18, 2, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 7, 3, 1, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 15, 0, 7, 18, 1, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            for (n2 = 0; n2 <= 2; ++n2) {
                for (n = 7; n <= 11; ++n) {
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), n2, -1, n, boundingBox);
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), 18 - n2, -1, n, boundingBox);
                }
            }
            return true;
        }
    }

    public static class BridgeEndFiller
    extends NetherBridgePiece {
        private final int selfSeed;

        public BridgeEndFiller(int n, Random random, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END_FILLER, n);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
            this.selfSeed = random.nextInt();
        }

        public BridgeEndFiller(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END_FILLER, compoundTag);
            this.selfSeed = compoundTag.getInt("Seed");
        }

        public static BridgeEndFiller createPiece(List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -1, -3, 0, 5, 10, 8, direction);
            if (!BridgeEndFiller.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new BridgeEndFiller(n4, random, boundingBox, direction);
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
            super.addAdditionalSaveData(compoundTag);
            compoundTag.putInt("Seed", this.selfSeed);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            int n;
            int n2;
            int n3;
            Random random2 = new Random(this.selfSeed);
            for (n = 0; n <= 4; ++n) {
                for (n3 = 3; n3 <= 4; ++n3) {
                    n2 = random2.nextInt(8);
                    this.generateBox(worldGenLevel, boundingBox, n, n3, 0, n, n3, n2, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                }
            }
            n = random2.nextInt(8);
            this.generateBox(worldGenLevel, boundingBox, 0, 5, 0, 0, 5, n, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            n = random2.nextInt(8);
            this.generateBox(worldGenLevel, boundingBox, 4, 5, 0, 4, 5, n, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            for (n = 0; n <= 4; ++n) {
                n3 = random2.nextInt(5);
                this.generateBox(worldGenLevel, boundingBox, n, 2, 0, n, 2, n3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            }
            for (n = 0; n <= 4; ++n) {
                for (n3 = 0; n3 <= 1; ++n3) {
                    n2 = random2.nextInt(3);
                    this.generateBox(worldGenLevel, boundingBox, n, n3, 0, n, n3, n2, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
                }
            }
            return true;
        }
    }

    public static class BridgeStraight
    extends NetherBridgePiece {
        public BridgeStraight(int n, Random random, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STRAIGHT, n);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public BridgeStraight(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STRAIGHT, compoundTag);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.generateChildForward((StartPiece)structurePiece, list, random, 1, 3, false);
        }

        public static BridgeStraight createPiece(List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -1, -3, 0, 5, 10, 19, direction);
            if (!BridgeStraight.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new BridgeStraight(n4, random, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.generateBox(worldGenLevel, boundingBox, 0, 3, 0, 4, 4, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 1, 5, 0, 3, 7, 18, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 5, 0, 0, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 4, 5, 0, 4, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 0, 4, 2, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 2, 13, 4, 2, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 0, 4, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 15, 4, 1, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            for (int i = 0; i <= 4; ++i) {
                for (int j = 0; j <= 2; ++j) {
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, j, boundingBox);
                    this.fillColumnDown(worldGenLevel, Blocks.NETHER_BRICKS.defaultBlockState(), i, -1, 18 - j, boundingBox);
                }
            }
            BlockState blockState = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
            BlockState blockState2 = (BlockState)blockState.setValue(FenceBlock.EAST, true);
            BlockState blockState3 = (BlockState)blockState.setValue(FenceBlock.WEST, true);
            this.generateBox(worldGenLevel, boundingBox, 0, 1, 1, 0, 4, 1, blockState2, blockState2, false);
            this.generateBox(worldGenLevel, boundingBox, 0, 3, 4, 0, 4, 4, blockState2, blockState2, false);
            this.generateBox(worldGenLevel, boundingBox, 0, 3, 14, 0, 4, 14, blockState2, blockState2, false);
            this.generateBox(worldGenLevel, boundingBox, 0, 1, 17, 0, 4, 17, blockState2, blockState2, false);
            this.generateBox(worldGenLevel, boundingBox, 4, 1, 1, 4, 4, 1, blockState3, blockState3, false);
            this.generateBox(worldGenLevel, boundingBox, 4, 3, 4, 4, 4, 4, blockState3, blockState3, false);
            this.generateBox(worldGenLevel, boundingBox, 4, 3, 14, 4, 4, 14, blockState3, blockState3, false);
            this.generateBox(worldGenLevel, boundingBox, 4, 1, 17, 4, 4, 17, blockState3, blockState3, false);
            return true;
        }
    }

    public static class StartPiece
    extends BridgeCrossing {
        public PieceWeight previousPiece;
        public List<PieceWeight> availableBridgePieces;
        public List<PieceWeight> availableCastlePieces;
        public final List<StructurePiece> pendingChildren = Lists.newArrayList();

        public StartPiece(Random random, int n, int n2) {
            super(random, n, n2);
            this.availableBridgePieces = Lists.newArrayList();
            for (PieceWeight pieceWeight : BRIDGE_PIECE_WEIGHTS) {
                pieceWeight.placeCount = 0;
                this.availableBridgePieces.add(pieceWeight);
            }
            this.availableCastlePieces = Lists.newArrayList();
            for (PieceWeight pieceWeight : CASTLE_PIECE_WEIGHTS) {
                pieceWeight.placeCount = 0;
                this.availableCastlePieces.add(pieceWeight);
            }
        }

        public StartPiece(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.NETHER_FORTRESS_START, compoundTag);
        }
    }

    static abstract class NetherBridgePiece
    extends StructurePiece {
        protected NetherBridgePiece(StructurePieceType structurePieceType, int n) {
            super(structurePieceType, n);
        }

        public NetherBridgePiece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
            super(structurePieceType, compoundTag);
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
        }

        private int updatePieceWeight(List<PieceWeight> list) {
            boolean bl = false;
            int n = 0;
            for (PieceWeight pieceWeight : list) {
                if (pieceWeight.maxPlaceCount > 0 && pieceWeight.placeCount < pieceWeight.maxPlaceCount) {
                    bl = true;
                }
                n += pieceWeight.weight;
            }
            return bl ? n : -1;
        }

        private NetherBridgePiece generatePiece(StartPiece startPiece, List<PieceWeight> list, List<StructurePiece> list2, Random random, int n, int n2, int n3, Direction direction, int n4) {
            int n5 = this.updatePieceWeight(list);
            boolean bl = n5 > 0 && n4 <= 30;
            int n6 = 0;
            block0 : while (n6 < 5 && bl) {
                ++n6;
                int n7 = random.nextInt(n5);
                for (PieceWeight pieceWeight : list) {
                    if ((n7 -= pieceWeight.weight) >= 0) continue;
                    if (!pieceWeight.doPlace(n4) || pieceWeight == startPiece.previousPiece && !pieceWeight.allowInRow) continue block0;
                    NetherBridgePiece netherBridgePiece = NetherBridgePieces.findAndCreateBridgePieceFactory(pieceWeight, list2, random, n, n2, n3, direction, n4);
                    if (netherBridgePiece == null) continue;
                    ++pieceWeight.placeCount;
                    startPiece.previousPiece = pieceWeight;
                    if (!pieceWeight.isValid()) {
                        list.remove(pieceWeight);
                    }
                    return netherBridgePiece;
                }
            }
            return BridgeEndFiller.createPiece(list2, random, n, n2, n3, direction, n4);
        }

        private StructurePiece generateAndAddPiece(StartPiece startPiece, List<StructurePiece> list, Random random, int n, int n2, int n3, @Nullable Direction direction, int n4, boolean bl) {
            NetherBridgePiece netherBridgePiece;
            if (Math.abs(n - startPiece.getBoundingBox().x0) > 112 || Math.abs(n3 - startPiece.getBoundingBox().z0) > 112) {
                return BridgeEndFiller.createPiece(list, random, n, n2, n3, direction, n4);
            }
            List<PieceWeight> list2 = startPiece.availableBridgePieces;
            if (bl) {
                list2 = startPiece.availableCastlePieces;
            }
            if ((netherBridgePiece = this.generatePiece(startPiece, list2, list, random, n, n2, n3, direction, n4 + 1)) != null) {
                list.add(netherBridgePiece);
                startPiece.pendingChildren.add(netherBridgePiece);
            }
            return netherBridgePiece;
        }

        @Nullable
        protected StructurePiece generateChildForward(StartPiece startPiece, List<StructurePiece> list, Random random, int n, int n2, boolean bl) {
            Direction direction = this.getOrientation();
            if (direction != null) {
                switch (direction) {
                    case NORTH: {
                        return this.generateAndAddPiece(startPiece, list, random, this.boundingBox.x0 + n, this.boundingBox.y0 + n2, this.boundingBox.z0 - 1, direction, this.getGenDepth(), bl);
                    }
                    case SOUTH: {
                        return this.generateAndAddPiece(startPiece, list, random, this.boundingBox.x0 + n, this.boundingBox.y0 + n2, this.boundingBox.z1 + 1, direction, this.getGenDepth(), bl);
                    }
                    case WEST: {
                        return this.generateAndAddPiece(startPiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + n2, this.boundingBox.z0 + n, direction, this.getGenDepth(), bl);
                    }
                    case EAST: {
                        return this.generateAndAddPiece(startPiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + n2, this.boundingBox.z0 + n, direction, this.getGenDepth(), bl);
                    }
                }
            }
            return null;
        }

        @Nullable
        protected StructurePiece generateChildLeft(StartPiece startPiece, List<StructurePiece> list, Random random, int n, int n2, boolean bl) {
            Direction direction = this.getOrientation();
            if (direction != null) {
                switch (direction) {
                    case NORTH: {
                        return this.generateAndAddPiece(startPiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + n, this.boundingBox.z0 + n2, Direction.WEST, this.getGenDepth(), bl);
                    }
                    case SOUTH: {
                        return this.generateAndAddPiece(startPiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + n, this.boundingBox.z0 + n2, Direction.WEST, this.getGenDepth(), bl);
                    }
                    case WEST: {
                        return this.generateAndAddPiece(startPiece, list, random, this.boundingBox.x0 + n2, this.boundingBox.y0 + n, this.boundingBox.z0 - 1, Direction.NORTH, this.getGenDepth(), bl);
                    }
                    case EAST: {
                        return this.generateAndAddPiece(startPiece, list, random, this.boundingBox.x0 + n2, this.boundingBox.y0 + n, this.boundingBox.z0 - 1, Direction.NORTH, this.getGenDepth(), bl);
                    }
                }
            }
            return null;
        }

        @Nullable
        protected StructurePiece generateChildRight(StartPiece startPiece, List<StructurePiece> list, Random random, int n, int n2, boolean bl) {
            Direction direction = this.getOrientation();
            if (direction != null) {
                switch (direction) {
                    case NORTH: {
                        return this.generateAndAddPiece(startPiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + n, this.boundingBox.z0 + n2, Direction.EAST, this.getGenDepth(), bl);
                    }
                    case SOUTH: {
                        return this.generateAndAddPiece(startPiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + n, this.boundingBox.z0 + n2, Direction.EAST, this.getGenDepth(), bl);
                    }
                    case WEST: {
                        return this.generateAndAddPiece(startPiece, list, random, this.boundingBox.x0 + n2, this.boundingBox.y0 + n, this.boundingBox.z1 + 1, Direction.SOUTH, this.getGenDepth(), bl);
                    }
                    case EAST: {
                        return this.generateAndAddPiece(startPiece, list, random, this.boundingBox.x0 + n2, this.boundingBox.y0 + n, this.boundingBox.z1 + 1, Direction.SOUTH, this.getGenDepth(), bl);
                    }
                }
            }
            return null;
        }

        protected static boolean isOkBox(BoundingBox boundingBox) {
            return boundingBox != null && boundingBox.y0 > 10;
        }
    }

    static class PieceWeight {
        public final Class<? extends NetherBridgePiece> pieceClass;
        public final int weight;
        public int placeCount;
        public final int maxPlaceCount;
        public final boolean allowInRow;

        public PieceWeight(Class<? extends NetherBridgePiece> class_, int n, int n2, boolean bl) {
            this.pieceClass = class_;
            this.weight = n;
            this.maxPlaceCount = n2;
            this.allowInRow = bl;
        }

        public PieceWeight(Class<? extends NetherBridgePiece> class_, int n, int n2) {
            this(class_, n, n2, false);
        }

        public boolean doPlace(int n) {
            return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
        }

        public boolean isValid() {
            return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
        }
    }

}

