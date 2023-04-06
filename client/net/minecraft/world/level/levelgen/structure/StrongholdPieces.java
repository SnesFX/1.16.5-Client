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
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class StrongholdPieces {
    private static final PieceWeight[] STRONGHOLD_PIECE_WEIGHTS = new PieceWeight[]{new PieceWeight(Straight.class, 40, 0), new PieceWeight(PrisonHall.class, 5, 5), new PieceWeight(LeftTurn.class, 20, 0), new PieceWeight(RightTurn.class, 20, 0), new PieceWeight(RoomCrossing.class, 10, 6), new PieceWeight(StraightStairsDown.class, 5, 5), new PieceWeight(StairsDown.class, 5, 5), new PieceWeight(FiveCrossing.class, 5, 4), new PieceWeight(ChestCorridor.class, 5, 4), new PieceWeight(Library.class, 10, 2){

        @Override
        public boolean doPlace(int n) {
            return super.doPlace(n) && n > 4;
        }
    }, new PieceWeight(PortalRoom.class, 20, 1){

        @Override
        public boolean doPlace(int n) {
            return super.doPlace(n) && n > 5;
        }
    }};
    private static List<PieceWeight> currentPieces;
    private static Class<? extends StrongholdPiece> imposedPiece;
    private static int totalWeight;
    private static final SmoothStoneSelector SMOOTH_STONE_SELECTOR;

    public static void resetPieces() {
        currentPieces = Lists.newArrayList();
        for (PieceWeight pieceWeight : STRONGHOLD_PIECE_WEIGHTS) {
            pieceWeight.placeCount = 0;
            currentPieces.add(pieceWeight);
        }
        imposedPiece = null;
    }

    private static boolean updatePieceWeight() {
        boolean bl = false;
        totalWeight = 0;
        for (PieceWeight pieceWeight : currentPieces) {
            if (pieceWeight.maxPlaceCount > 0 && pieceWeight.placeCount < pieceWeight.maxPlaceCount) {
                bl = true;
            }
            totalWeight += pieceWeight.weight;
        }
        return bl;
    }

    private static StrongholdPiece findAndCreatePieceFactory(Class<? extends StrongholdPiece> class_, List<StructurePiece> list, Random random, int n, int n2, int n3, @Nullable Direction direction, int n4) {
        StrongholdPiece strongholdPiece = null;
        if (class_ == Straight.class) {
            strongholdPiece = Straight.createPiece(list, random, n, n2, n3, direction, n4);
        } else if (class_ == PrisonHall.class) {
            strongholdPiece = PrisonHall.createPiece(list, random, n, n2, n3, direction, n4);
        } else if (class_ == LeftTurn.class) {
            strongholdPiece = LeftTurn.createPiece(list, random, n, n2, n3, direction, n4);
        } else if (class_ == RightTurn.class) {
            strongholdPiece = RightTurn.createPiece(list, random, n, n2, n3, direction, n4);
        } else if (class_ == RoomCrossing.class) {
            strongholdPiece = RoomCrossing.createPiece(list, random, n, n2, n3, direction, n4);
        } else if (class_ == StraightStairsDown.class) {
            strongholdPiece = StraightStairsDown.createPiece(list, random, n, n2, n3, direction, n4);
        } else if (class_ == StairsDown.class) {
            strongholdPiece = StairsDown.createPiece(list, random, n, n2, n3, direction, n4);
        } else if (class_ == FiveCrossing.class) {
            strongholdPiece = FiveCrossing.createPiece(list, random, n, n2, n3, direction, n4);
        } else if (class_ == ChestCorridor.class) {
            strongholdPiece = ChestCorridor.createPiece(list, random, n, n2, n3, direction, n4);
        } else if (class_ == Library.class) {
            strongholdPiece = Library.createPiece(list, random, n, n2, n3, direction, n4);
        } else if (class_ == PortalRoom.class) {
            strongholdPiece = PortalRoom.createPiece(list, n, n2, n3, direction, n4);
        }
        return strongholdPiece;
    }

    private static StrongholdPiece generatePieceFromSmallDoor(StartPiece startPiece, List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction, int n4) {
        if (!StrongholdPieces.updatePieceWeight()) {
            return null;
        }
        if (imposedPiece != null) {
            StrongholdPiece strongholdPiece = StrongholdPieces.findAndCreatePieceFactory(imposedPiece, list, random, n, n2, n3, direction, n4);
            imposedPiece = null;
            if (strongholdPiece != null) {
                return strongholdPiece;
            }
        }
        int n5 = 0;
        block0 : while (n5 < 5) {
            ++n5;
            int n6 = random.nextInt(totalWeight);
            for (PieceWeight pieceWeight : currentPieces) {
                if ((n6 -= pieceWeight.weight) >= 0) continue;
                if (!pieceWeight.doPlace(n4) || pieceWeight == startPiece.previousPiece) continue block0;
                StrongholdPiece strongholdPiece = StrongholdPieces.findAndCreatePieceFactory(pieceWeight.pieceClass, list, random, n, n2, n3, direction, n4);
                if (strongholdPiece == null) continue;
                ++pieceWeight.placeCount;
                startPiece.previousPiece = pieceWeight;
                if (!pieceWeight.isValid()) {
                    currentPieces.remove(pieceWeight);
                }
                return strongholdPiece;
            }
        }
        BoundingBox boundingBox = FillerCorridor.findPieceBox(list, random, n, n2, n3, direction);
        if (boundingBox != null && boundingBox.y0 > 1) {
            return new FillerCorridor(n4, boundingBox, direction);
        }
        return null;
    }

    private static StructurePiece generateAndAddPiece(StartPiece startPiece, List<StructurePiece> list, Random random, int n, int n2, int n3, @Nullable Direction direction, int n4) {
        if (n4 > 50) {
            return null;
        }
        if (Math.abs(n - startPiece.getBoundingBox().x0) > 112 || Math.abs(n3 - startPiece.getBoundingBox().z0) > 112) {
            return null;
        }
        StrongholdPiece strongholdPiece = StrongholdPieces.generatePieceFromSmallDoor(startPiece, list, random, n, n2, n3, direction, n4 + 1);
        if (strongholdPiece != null) {
            list.add(strongholdPiece);
            startPiece.pendingChildren.add(strongholdPiece);
        }
        return strongholdPiece;
    }

    static {
        SMOOTH_STONE_SELECTOR = new SmoothStoneSelector();
    }

    static class SmoothStoneSelector
    extends StructurePiece.BlockSelector {
        private SmoothStoneSelector() {
        }

        @Override
        public void next(Random random, int n, int n2, int n3, boolean bl) {
            float f;
            this.next = bl ? ((f = random.nextFloat()) < 0.2f ? Blocks.CRACKED_STONE_BRICKS.defaultBlockState() : (f < 0.5f ? Blocks.MOSSY_STONE_BRICKS.defaultBlockState() : (f < 0.55f ? Blocks.INFESTED_STONE_BRICKS.defaultBlockState() : Blocks.STONE_BRICKS.defaultBlockState()))) : Blocks.CAVE_AIR.defaultBlockState();
        }
    }

    public static class PortalRoom
    extends StrongholdPiece {
        private boolean hasPlacedSpawner;

        public PortalRoom(int n, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, n);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public PortalRoom(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, compoundTag);
            this.hasPlacedSpawner = compoundTag.getBoolean("Mob");
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
            super.addAdditionalSaveData(compoundTag);
            compoundTag.putBoolean("Mob", this.hasPlacedSpawner);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            if (structurePiece != null) {
                ((StartPiece)structurePiece).portalRoomPiece = this;
            }
        }

        public static PortalRoom createPiece(List<StructurePiece> list, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -4, -1, 0, 11, 8, 16, direction);
            if (!PortalRoom.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new PortalRoom(n4, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            int n;
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 0, 10, 7, 15, false, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(worldGenLevel, random, boundingBox, StrongholdPiece.SmallDoorType.GRATES, 4, 1, 0);
            int n2 = 6;
            this.generateBox(worldGenLevel, boundingBox, 1, n2, 1, 1, n2, 14, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, 9, n2, 1, 9, n2, 14, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, 2, n2, 1, 8, n2, 2, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, 2, n2, 14, 8, n2, 14, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, 1, 1, 1, 2, 1, 4, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, 8, 1, 1, 9, 1, 4, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, 1, 1, 1, 1, 1, 3, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 9, 1, 1, 9, 1, 3, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 3, 1, 8, 7, 1, 12, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, 4, 1, 9, 6, 1, 11, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
            BlockState blockState = (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true);
            BlockState blockState2 = (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true)).setValue(IronBarsBlock.EAST, true);
            for (n = 3; n < 14; n += 2) {
                this.generateBox(worldGenLevel, boundingBox, 0, 3, n, 0, 4, n, blockState, blockState, false);
                this.generateBox(worldGenLevel, boundingBox, 10, 3, n, 10, 4, n, blockState, blockState, false);
            }
            for (n = 2; n < 9; n += 2) {
                this.generateBox(worldGenLevel, boundingBox, n, 3, 15, n, 4, 15, blockState2, blockState2, false);
            }
            BlockState blockState3 = (BlockState)Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
            this.generateBox(worldGenLevel, boundingBox, 4, 1, 5, 6, 1, 7, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, 4, 2, 6, 6, 2, 7, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, 4, 3, 7, 6, 3, 7, false, random, SMOOTH_STONE_SELECTOR);
            for (int i = 4; i <= 6; ++i) {
                this.placeBlock(worldGenLevel, blockState3, i, 1, 4, boundingBox);
                this.placeBlock(worldGenLevel, blockState3, i, 2, 5, boundingBox);
                this.placeBlock(worldGenLevel, blockState3, i, 3, 6, boundingBox);
            }
            BlockState blockState4 = (BlockState)Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.NORTH);
            BlockState blockState5 = (BlockState)Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.SOUTH);
            BlockState blockState6 = (BlockState)Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.EAST);
            BlockState blockState7 = (BlockState)Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.WEST);
            boolean bl = true;
            boolean[] arrbl = new boolean[12];
            for (int i = 0; i < arrbl.length; ++i) {
                arrbl[i] = random.nextFloat() > 0.9f;
                bl &= arrbl[i];
            }
            this.placeBlock(worldGenLevel, (BlockState)blockState4.setValue(EndPortalFrameBlock.HAS_EYE, arrbl[0]), 4, 3, 8, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)blockState4.setValue(EndPortalFrameBlock.HAS_EYE, arrbl[1]), 5, 3, 8, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)blockState4.setValue(EndPortalFrameBlock.HAS_EYE, arrbl[2]), 6, 3, 8, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)blockState5.setValue(EndPortalFrameBlock.HAS_EYE, arrbl[3]), 4, 3, 12, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)blockState5.setValue(EndPortalFrameBlock.HAS_EYE, arrbl[4]), 5, 3, 12, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)blockState5.setValue(EndPortalFrameBlock.HAS_EYE, arrbl[5]), 6, 3, 12, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)blockState6.setValue(EndPortalFrameBlock.HAS_EYE, arrbl[6]), 3, 3, 9, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)blockState6.setValue(EndPortalFrameBlock.HAS_EYE, arrbl[7]), 3, 3, 10, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)blockState6.setValue(EndPortalFrameBlock.HAS_EYE, arrbl[8]), 3, 3, 11, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)blockState7.setValue(EndPortalFrameBlock.HAS_EYE, arrbl[9]), 7, 3, 9, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)blockState7.setValue(EndPortalFrameBlock.HAS_EYE, arrbl[10]), 7, 3, 10, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)blockState7.setValue(EndPortalFrameBlock.HAS_EYE, arrbl[11]), 7, 3, 11, boundingBox);
            if (bl) {
                BlockState blockState8 = Blocks.END_PORTAL.defaultBlockState();
                this.placeBlock(worldGenLevel, blockState8, 4, 3, 9, boundingBox);
                this.placeBlock(worldGenLevel, blockState8, 5, 3, 9, boundingBox);
                this.placeBlock(worldGenLevel, blockState8, 6, 3, 9, boundingBox);
                this.placeBlock(worldGenLevel, blockState8, 4, 3, 10, boundingBox);
                this.placeBlock(worldGenLevel, blockState8, 5, 3, 10, boundingBox);
                this.placeBlock(worldGenLevel, blockState8, 6, 3, 10, boundingBox);
                this.placeBlock(worldGenLevel, blockState8, 4, 3, 11, boundingBox);
                this.placeBlock(worldGenLevel, blockState8, 5, 3, 11, boundingBox);
                this.placeBlock(worldGenLevel, blockState8, 6, 3, 11, boundingBox);
            }
            if (!this.hasPlacedSpawner) {
                n2 = this.getWorldY(3);
                BlockPos blockPos2 = new BlockPos(this.getWorldX(5, 6), n2, this.getWorldZ(5, 6));
                if (boundingBox.isInside(blockPos2)) {
                    this.hasPlacedSpawner = true;
                    worldGenLevel.setBlock(blockPos2, Blocks.SPAWNER.defaultBlockState(), 2);
                    BlockEntity blockEntity = worldGenLevel.getBlockEntity(blockPos2);
                    if (blockEntity instanceof SpawnerBlockEntity) {
                        ((SpawnerBlockEntity)blockEntity).getSpawner().setEntityId(EntityType.SILVERFISH);
                    }
                }
            }
            return true;
        }
    }

    public static class FiveCrossing
    extends StrongholdPiece {
        private final boolean leftLow;
        private final boolean leftHigh;
        private final boolean rightLow;
        private final boolean rightHigh;

        public FiveCrossing(int n, Random random, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_FIVE_CROSSING, n);
            this.setOrientation(direction);
            this.entryDoor = this.randomSmallDoor(random);
            this.boundingBox = boundingBox;
            this.leftLow = random.nextBoolean();
            this.leftHigh = random.nextBoolean();
            this.rightLow = random.nextBoolean();
            this.rightHigh = random.nextInt(3) > 0;
        }

        public FiveCrossing(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_FIVE_CROSSING, compoundTag);
            this.leftLow = compoundTag.getBoolean("leftLow");
            this.leftHigh = compoundTag.getBoolean("leftHigh");
            this.rightLow = compoundTag.getBoolean("rightLow");
            this.rightHigh = compoundTag.getBoolean("rightHigh");
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
            super.addAdditionalSaveData(compoundTag);
            compoundTag.putBoolean("leftLow", this.leftLow);
            compoundTag.putBoolean("leftHigh", this.leftHigh);
            compoundTag.putBoolean("rightLow", this.rightLow);
            compoundTag.putBoolean("rightHigh", this.rightHigh);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            int n = 3;
            int n2 = 5;
            Direction direction = this.getOrientation();
            if (direction == Direction.WEST || direction == Direction.NORTH) {
                n = 8 - n;
                n2 = 8 - n2;
            }
            this.generateSmallDoorChildForward((StartPiece)structurePiece, list, random, 5, 1);
            if (this.leftLow) {
                this.generateSmallDoorChildLeft((StartPiece)structurePiece, list, random, n, 1);
            }
            if (this.leftHigh) {
                this.generateSmallDoorChildLeft((StartPiece)structurePiece, list, random, n2, 7);
            }
            if (this.rightLow) {
                this.generateSmallDoorChildRight((StartPiece)structurePiece, list, random, n, 1);
            }
            if (this.rightHigh) {
                this.generateSmallDoorChildRight((StartPiece)structurePiece, list, random, n2, 7);
            }
        }

        public static FiveCrossing createPiece(List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -4, -3, 0, 10, 9, 11, direction);
            if (!FiveCrossing.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new FiveCrossing(n4, random, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 0, 9, 8, 10, true, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(worldGenLevel, random, boundingBox, this.entryDoor, 4, 3, 0);
            if (this.leftLow) {
                this.generateBox(worldGenLevel, boundingBox, 0, 3, 1, 0, 5, 3, CAVE_AIR, CAVE_AIR, false);
            }
            if (this.rightLow) {
                this.generateBox(worldGenLevel, boundingBox, 9, 3, 1, 9, 5, 3, CAVE_AIR, CAVE_AIR, false);
            }
            if (this.leftHigh) {
                this.generateBox(worldGenLevel, boundingBox, 0, 5, 7, 0, 7, 9, CAVE_AIR, CAVE_AIR, false);
            }
            if (this.rightHigh) {
                this.generateBox(worldGenLevel, boundingBox, 9, 5, 7, 9, 7, 9, CAVE_AIR, CAVE_AIR, false);
            }
            this.generateBox(worldGenLevel, boundingBox, 5, 1, 10, 7, 3, 10, CAVE_AIR, CAVE_AIR, false);
            this.generateBox(worldGenLevel, boundingBox, 1, 2, 1, 8, 2, 6, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, 4, 1, 5, 4, 4, 9, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, 8, 1, 5, 8, 4, 9, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, 1, 4, 7, 3, 4, 9, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, 1, 3, 5, 3, 3, 6, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, 1, 3, 4, 3, 3, 4, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 1, 4, 6, 3, 4, 6, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 5, 1, 7, 7, 1, 8, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, 5, 1, 9, 7, 1, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 5, 2, 7, 7, 2, 7, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 4, 5, 7, 4, 5, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 8, 5, 7, 8, 5, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
            this.generateBox(worldGenLevel, boundingBox, 5, 5, 7, 7, 5, 9, (BlockState)Blocks.SMOOTH_STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE), (BlockState)Blocks.SMOOTH_STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE), false);
            this.placeBlock(worldGenLevel, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH), 6, 5, 6, boundingBox);
            return true;
        }
    }

    public static class Library
    extends StrongholdPiece {
        private final boolean isTall;

        public Library(int n, Random random, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_LIBRARY, n);
            this.setOrientation(direction);
            this.entryDoor = this.randomSmallDoor(random);
            this.boundingBox = boundingBox;
            this.isTall = boundingBox.getYSpan() > 6;
        }

        public Library(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_LIBRARY, compoundTag);
            this.isTall = compoundTag.getBoolean("Tall");
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
            super.addAdditionalSaveData(compoundTag);
            compoundTag.putBoolean("Tall", this.isTall);
        }

        public static Library createPiece(List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -4, -1, 0, 14, 11, 15, direction);
            if (!(Library.isOkBox(boundingBox) && StructurePiece.findCollisionPiece(list, boundingBox) == null || Library.isOkBox(boundingBox = BoundingBox.orientBox(n, n2, n3, -4, -1, 0, 14, 6, 15, direction)) && StructurePiece.findCollisionPiece(list, boundingBox) == null)) {
                return null;
            }
            return new Library(n4, random, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            int n;
            int n2 = 11;
            if (!this.isTall) {
                n2 = 6;
            }
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 0, 13, n2 - 1, 14, true, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(worldGenLevel, random, boundingBox, this.entryDoor, 4, 1, 0);
            this.generateMaybeBox(worldGenLevel, boundingBox, random, 0.07f, 2, 1, 1, 11, 4, 13, Blocks.COBWEB.defaultBlockState(), Blocks.COBWEB.defaultBlockState(), false, false);
            boolean bl = true;
            int n3 = 12;
            for (n = 1; n <= 13; ++n) {
                if ((n - 1) % 4 == 0) {
                    this.generateBox(worldGenLevel, boundingBox, 1, 1, n, 1, 4, n, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                    this.generateBox(worldGenLevel, boundingBox, 12, 1, n, 12, 4, n, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                    this.placeBlock(worldGenLevel, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST), 2, 3, n, boundingBox);
                    this.placeBlock(worldGenLevel, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST), 11, 3, n, boundingBox);
                    if (!this.isTall) continue;
                    this.generateBox(worldGenLevel, boundingBox, 1, 6, n, 1, 9, n, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                    this.generateBox(worldGenLevel, boundingBox, 12, 6, n, 12, 9, n, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                    continue;
                }
                this.generateBox(worldGenLevel, boundingBox, 1, 1, n, 1, 4, n, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
                this.generateBox(worldGenLevel, boundingBox, 12, 1, n, 12, 4, n, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
                if (!this.isTall) continue;
                this.generateBox(worldGenLevel, boundingBox, 1, 6, n, 1, 9, n, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
                this.generateBox(worldGenLevel, boundingBox, 12, 6, n, 12, 9, n, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
            }
            for (n = 3; n < 12; n += 2) {
                this.generateBox(worldGenLevel, boundingBox, 3, 1, n, 4, 3, n, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
                this.generateBox(worldGenLevel, boundingBox, 6, 1, n, 7, 3, n, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
                this.generateBox(worldGenLevel, boundingBox, 9, 1, n, 10, 3, n, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
            }
            if (this.isTall) {
                this.generateBox(worldGenLevel, boundingBox, 1, 5, 1, 3, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                this.generateBox(worldGenLevel, boundingBox, 10, 5, 1, 12, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                this.generateBox(worldGenLevel, boundingBox, 4, 5, 1, 9, 5, 2, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                this.generateBox(worldGenLevel, boundingBox, 4, 5, 12, 9, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                this.placeBlock(worldGenLevel, Blocks.OAK_PLANKS.defaultBlockState(), 9, 5, 11, boundingBox);
                this.placeBlock(worldGenLevel, Blocks.OAK_PLANKS.defaultBlockState(), 8, 5, 11, boundingBox);
                this.placeBlock(worldGenLevel, Blocks.OAK_PLANKS.defaultBlockState(), 9, 5, 10, boundingBox);
                BlockState blockState = (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
                BlockState blockState2 = (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
                this.generateBox(worldGenLevel, boundingBox, 3, 6, 3, 3, 6, 11, blockState2, blockState2, false);
                this.generateBox(worldGenLevel, boundingBox, 10, 6, 3, 10, 6, 9, blockState2, blockState2, false);
                this.generateBox(worldGenLevel, boundingBox, 4, 6, 2, 9, 6, 2, blockState, blockState, false);
                this.generateBox(worldGenLevel, boundingBox, 4, 6, 12, 7, 6, 12, blockState, blockState, false);
                this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.EAST, true), 3, 6, 2, boundingBox);
                this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, true)).setValue(FenceBlock.EAST, true), 3, 6, 12, boundingBox);
                this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.WEST, true), 10, 6, 2, boundingBox);
                for (int i = 0; i <= 2; ++i) {
                    this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, true)).setValue(FenceBlock.WEST, true), 8 + i, 6, 12 - i, boundingBox);
                    if (i == 2) continue;
                    this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.EAST, true), 8 + i, 6, 11 - i, boundingBox);
                }
                BlockState blockState3 = (BlockState)Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH);
                this.placeBlock(worldGenLevel, blockState3, 10, 1, 13, boundingBox);
                this.placeBlock(worldGenLevel, blockState3, 10, 2, 13, boundingBox);
                this.placeBlock(worldGenLevel, blockState3, 10, 3, 13, boundingBox);
                this.placeBlock(worldGenLevel, blockState3, 10, 4, 13, boundingBox);
                this.placeBlock(worldGenLevel, blockState3, 10, 5, 13, boundingBox);
                this.placeBlock(worldGenLevel, blockState3, 10, 6, 13, boundingBox);
                this.placeBlock(worldGenLevel, blockState3, 10, 7, 13, boundingBox);
                int n4 = 7;
                int n5 = 7;
                BlockState blockState4 = (BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, true);
                this.placeBlock(worldGenLevel, blockState4, 6, 9, 7, boundingBox);
                BlockState blockState5 = (BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true);
                this.placeBlock(worldGenLevel, blockState5, 7, 9, 7, boundingBox);
                this.placeBlock(worldGenLevel, blockState4, 6, 8, 7, boundingBox);
                this.placeBlock(worldGenLevel, blockState5, 7, 8, 7, boundingBox);
                BlockState blockState6 = (BlockState)((BlockState)blockState2.setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
                this.placeBlock(worldGenLevel, blockState6, 6, 7, 7, boundingBox);
                this.placeBlock(worldGenLevel, blockState6, 7, 7, 7, boundingBox);
                this.placeBlock(worldGenLevel, blockState4, 5, 7, 7, boundingBox);
                this.placeBlock(worldGenLevel, blockState5, 8, 7, 7, boundingBox);
                this.placeBlock(worldGenLevel, (BlockState)blockState4.setValue(FenceBlock.NORTH, true), 6, 7, 6, boundingBox);
                this.placeBlock(worldGenLevel, (BlockState)blockState4.setValue(FenceBlock.SOUTH, true), 6, 7, 8, boundingBox);
                this.placeBlock(worldGenLevel, (BlockState)blockState5.setValue(FenceBlock.NORTH, true), 7, 7, 6, boundingBox);
                this.placeBlock(worldGenLevel, (BlockState)blockState5.setValue(FenceBlock.SOUTH, true), 7, 7, 8, boundingBox);
                BlockState blockState7 = Blocks.TORCH.defaultBlockState();
                this.placeBlock(worldGenLevel, blockState7, 5, 8, 7, boundingBox);
                this.placeBlock(worldGenLevel, blockState7, 8, 8, 7, boundingBox);
                this.placeBlock(worldGenLevel, blockState7, 6, 8, 6, boundingBox);
                this.placeBlock(worldGenLevel, blockState7, 6, 8, 8, boundingBox);
                this.placeBlock(worldGenLevel, blockState7, 7, 8, 6, boundingBox);
                this.placeBlock(worldGenLevel, blockState7, 7, 8, 8, boundingBox);
            }
            this.createChest(worldGenLevel, boundingBox, random, 3, 3, 5, BuiltInLootTables.STRONGHOLD_LIBRARY);
            if (this.isTall) {
                this.placeBlock(worldGenLevel, CAVE_AIR, 12, 9, 1, boundingBox);
                this.createChest(worldGenLevel, boundingBox, random, 12, 8, 1, BuiltInLootTables.STRONGHOLD_LIBRARY);
            }
            return true;
        }
    }

    public static class PrisonHall
    extends StrongholdPiece {
        public PrisonHall(int n, Random random, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_PRISON_HALL, n);
            this.setOrientation(direction);
            this.entryDoor = this.randomSmallDoor(random);
            this.boundingBox = boundingBox;
        }

        public PrisonHall(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_PRISON_HALL, compoundTag);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.generateSmallDoorChildForward((StartPiece)structurePiece, list, random, 1, 1);
        }

        public static PrisonHall createPiece(List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -1, -1, 0, 9, 5, 11, direction);
            if (!PrisonHall.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new PrisonHall(n4, random, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 0, 8, 4, 10, true, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(worldGenLevel, random, boundingBox, this.entryDoor, 1, 1, 0);
            this.generateBox(worldGenLevel, boundingBox, 1, 1, 10, 3, 3, 10, CAVE_AIR, CAVE_AIR, false);
            this.generateBox(worldGenLevel, boundingBox, 4, 1, 1, 4, 3, 1, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, 4, 1, 3, 4, 3, 3, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, 4, 1, 7, 4, 3, 7, false, random, SMOOTH_STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, 4, 1, 9, 4, 3, 9, false, random, SMOOTH_STONE_SELECTOR);
            for (int i = 1; i <= 3; ++i) {
                this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true), 4, i, 4, boundingBox);
                this.placeBlock(worldGenLevel, (BlockState)((BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true)).setValue(IronBarsBlock.EAST, true), 4, i, 5, boundingBox);
                this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true), 4, i, 6, boundingBox);
                this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true)).setValue(IronBarsBlock.EAST, true), 5, i, 5, boundingBox);
                this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true)).setValue(IronBarsBlock.EAST, true), 6, i, 5, boundingBox);
                this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true)).setValue(IronBarsBlock.EAST, true), 7, i, 5, boundingBox);
            }
            this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true), 4, 3, 2, boundingBox);
            this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true), 4, 3, 8, boundingBox);
            BlockState blockState = (BlockState)Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.WEST);
            BlockState blockState2 = (BlockState)((BlockState)Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.WEST)).setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);
            this.placeBlock(worldGenLevel, blockState, 4, 1, 2, boundingBox);
            this.placeBlock(worldGenLevel, blockState2, 4, 2, 2, boundingBox);
            this.placeBlock(worldGenLevel, blockState, 4, 1, 8, boundingBox);
            this.placeBlock(worldGenLevel, blockState2, 4, 2, 8, boundingBox);
            return true;
        }
    }

    public static class RoomCrossing
    extends StrongholdPiece {
        protected final int type;

        public RoomCrossing(int n, Random random, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_ROOM_CROSSING, n);
            this.setOrientation(direction);
            this.entryDoor = this.randomSmallDoor(random);
            this.boundingBox = boundingBox;
            this.type = random.nextInt(5);
        }

        public RoomCrossing(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_ROOM_CROSSING, compoundTag);
            this.type = compoundTag.getInt("Type");
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
            super.addAdditionalSaveData(compoundTag);
            compoundTag.putInt("Type", this.type);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.generateSmallDoorChildForward((StartPiece)structurePiece, list, random, 4, 1);
            this.generateSmallDoorChildLeft((StartPiece)structurePiece, list, random, 1, 4);
            this.generateSmallDoorChildRight((StartPiece)structurePiece, list, random, 1, 4);
        }

        public static RoomCrossing createPiece(List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -4, -1, 0, 11, 7, 11, direction);
            if (!RoomCrossing.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new RoomCrossing(n4, random, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 0, 10, 6, 10, true, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(worldGenLevel, random, boundingBox, this.entryDoor, 4, 1, 0);
            this.generateBox(worldGenLevel, boundingBox, 4, 1, 10, 6, 3, 10, CAVE_AIR, CAVE_AIR, false);
            this.generateBox(worldGenLevel, boundingBox, 0, 1, 4, 0, 3, 6, CAVE_AIR, CAVE_AIR, false);
            this.generateBox(worldGenLevel, boundingBox, 10, 1, 4, 10, 3, 6, CAVE_AIR, CAVE_AIR, false);
            switch (this.type) {
                default: {
                    break;
                }
                case 0: {
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 5, 1, 5, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 5, 2, 5, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 5, 3, 5, boundingBox);
                    this.placeBlock(worldGenLevel, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST), 4, 3, 5, boundingBox);
                    this.placeBlock(worldGenLevel, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST), 6, 3, 5, boundingBox);
                    this.placeBlock(worldGenLevel, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH), 5, 3, 4, boundingBox);
                    this.placeBlock(worldGenLevel, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.NORTH), 5, 3, 6, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 4, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 5, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 6, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 4, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 5, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 6, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 5, 1, 4, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 5, 1, 6, boundingBox);
                    break;
                }
                case 1: {
                    for (int i = 0; i < 5; ++i) {
                        this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 3, 1, 3 + i, boundingBox);
                        this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 7, 1, 3 + i, boundingBox);
                        this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 3 + i, 1, 3, boundingBox);
                        this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 3 + i, 1, 7, boundingBox);
                    }
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 5, 1, 5, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 5, 2, 5, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 5, 3, 5, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.WATER.defaultBlockState(), 5, 4, 5, boundingBox);
                    break;
                }
                case 2: {
                    int n;
                    for (n = 1; n <= 9; ++n) {
                        this.placeBlock(worldGenLevel, Blocks.COBBLESTONE.defaultBlockState(), 1, 3, n, boundingBox);
                        this.placeBlock(worldGenLevel, Blocks.COBBLESTONE.defaultBlockState(), 9, 3, n, boundingBox);
                    }
                    for (n = 1; n <= 9; ++n) {
                        this.placeBlock(worldGenLevel, Blocks.COBBLESTONE.defaultBlockState(), n, 3, 1, boundingBox);
                        this.placeBlock(worldGenLevel, Blocks.COBBLESTONE.defaultBlockState(), n, 3, 9, boundingBox);
                    }
                    this.placeBlock(worldGenLevel, Blocks.COBBLESTONE.defaultBlockState(), 5, 1, 4, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.COBBLESTONE.defaultBlockState(), 5, 1, 6, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.COBBLESTONE.defaultBlockState(), 5, 3, 4, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.COBBLESTONE.defaultBlockState(), 5, 3, 6, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.COBBLESTONE.defaultBlockState(), 4, 1, 5, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.COBBLESTONE.defaultBlockState(), 6, 1, 5, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.COBBLESTONE.defaultBlockState(), 4, 3, 5, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.COBBLESTONE.defaultBlockState(), 6, 3, 5, boundingBox);
                    for (n = 1; n <= 3; ++n) {
                        this.placeBlock(worldGenLevel, Blocks.COBBLESTONE.defaultBlockState(), 4, n, 4, boundingBox);
                        this.placeBlock(worldGenLevel, Blocks.COBBLESTONE.defaultBlockState(), 6, n, 4, boundingBox);
                        this.placeBlock(worldGenLevel, Blocks.COBBLESTONE.defaultBlockState(), 4, n, 6, boundingBox);
                        this.placeBlock(worldGenLevel, Blocks.COBBLESTONE.defaultBlockState(), 6, n, 6, boundingBox);
                    }
                    this.placeBlock(worldGenLevel, Blocks.TORCH.defaultBlockState(), 5, 3, 5, boundingBox);
                    for (n = 2; n <= 8; ++n) {
                        this.placeBlock(worldGenLevel, Blocks.OAK_PLANKS.defaultBlockState(), 2, 3, n, boundingBox);
                        this.placeBlock(worldGenLevel, Blocks.OAK_PLANKS.defaultBlockState(), 3, 3, n, boundingBox);
                        if (n <= 3 || n >= 7) {
                            this.placeBlock(worldGenLevel, Blocks.OAK_PLANKS.defaultBlockState(), 4, 3, n, boundingBox);
                            this.placeBlock(worldGenLevel, Blocks.OAK_PLANKS.defaultBlockState(), 5, 3, n, boundingBox);
                            this.placeBlock(worldGenLevel, Blocks.OAK_PLANKS.defaultBlockState(), 6, 3, n, boundingBox);
                        }
                        this.placeBlock(worldGenLevel, Blocks.OAK_PLANKS.defaultBlockState(), 7, 3, n, boundingBox);
                        this.placeBlock(worldGenLevel, Blocks.OAK_PLANKS.defaultBlockState(), 8, 3, n, boundingBox);
                    }
                    BlockState blockState = (BlockState)Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.WEST);
                    this.placeBlock(worldGenLevel, blockState, 9, 1, 3, boundingBox);
                    this.placeBlock(worldGenLevel, blockState, 9, 2, 3, boundingBox);
                    this.placeBlock(worldGenLevel, blockState, 9, 3, 3, boundingBox);
                    this.createChest(worldGenLevel, boundingBox, random, 3, 4, 8, BuiltInLootTables.STRONGHOLD_CROSSING);
                }
            }
            return true;
        }
    }

    public static class RightTurn
    extends Turn {
        public RightTurn(int n, Random random, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_RIGHT_TURN, n);
            this.setOrientation(direction);
            this.entryDoor = this.randomSmallDoor(random);
            this.boundingBox = boundingBox;
        }

        public RightTurn(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_RIGHT_TURN, compoundTag);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            Direction direction = this.getOrientation();
            if (direction == Direction.NORTH || direction == Direction.EAST) {
                this.generateSmallDoorChildRight((StartPiece)structurePiece, list, random, 1, 1);
            } else {
                this.generateSmallDoorChildLeft((StartPiece)structurePiece, list, random, 1, 1);
            }
        }

        public static RightTurn createPiece(List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -1, -1, 0, 5, 5, 5, direction);
            if (!RightTurn.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new RightTurn(n4, random, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 0, 4, 4, 4, true, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(worldGenLevel, random, boundingBox, this.entryDoor, 1, 1, 0);
            Direction direction = this.getOrientation();
            if (direction == Direction.NORTH || direction == Direction.EAST) {
                this.generateBox(worldGenLevel, boundingBox, 4, 1, 1, 4, 3, 3, CAVE_AIR, CAVE_AIR, false);
            } else {
                this.generateBox(worldGenLevel, boundingBox, 0, 1, 1, 0, 3, 3, CAVE_AIR, CAVE_AIR, false);
            }
            return true;
        }
    }

    public static class LeftTurn
    extends Turn {
        public LeftTurn(int n, Random random, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_LEFT_TURN, n);
            this.setOrientation(direction);
            this.entryDoor = this.randomSmallDoor(random);
            this.boundingBox = boundingBox;
        }

        public LeftTurn(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_LEFT_TURN, compoundTag);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            Direction direction = this.getOrientation();
            if (direction == Direction.NORTH || direction == Direction.EAST) {
                this.generateSmallDoorChildLeft((StartPiece)structurePiece, list, random, 1, 1);
            } else {
                this.generateSmallDoorChildRight((StartPiece)structurePiece, list, random, 1, 1);
            }
        }

        public static LeftTurn createPiece(List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -1, -1, 0, 5, 5, 5, direction);
            if (!LeftTurn.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new LeftTurn(n4, random, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 0, 4, 4, 4, true, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(worldGenLevel, random, boundingBox, this.entryDoor, 1, 1, 0);
            Direction direction = this.getOrientation();
            if (direction == Direction.NORTH || direction == Direction.EAST) {
                this.generateBox(worldGenLevel, boundingBox, 0, 1, 1, 0, 3, 3, CAVE_AIR, CAVE_AIR, false);
            } else {
                this.generateBox(worldGenLevel, boundingBox, 4, 1, 1, 4, 3, 3, CAVE_AIR, CAVE_AIR, false);
            }
            return true;
        }
    }

    public static abstract class Turn
    extends StrongholdPiece {
        protected Turn(StructurePieceType structurePieceType, int n) {
            super(structurePieceType, n);
        }

        public Turn(StructurePieceType structurePieceType, CompoundTag compoundTag) {
            super(structurePieceType, compoundTag);
        }
    }

    public static class StraightStairsDown
    extends StrongholdPiece {
        public StraightStairsDown(int n, Random random, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_STRAIGHT_STAIRS_DOWN, n);
            this.setOrientation(direction);
            this.entryDoor = this.randomSmallDoor(random);
            this.boundingBox = boundingBox;
        }

        public StraightStairsDown(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_STRAIGHT_STAIRS_DOWN, compoundTag);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.generateSmallDoorChildForward((StartPiece)structurePiece, list, random, 1, 1);
        }

        public static StraightStairsDown createPiece(List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -1, -7, 0, 5, 11, 8, direction);
            if (!StraightStairsDown.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new StraightStairsDown(n4, random, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 0, 4, 10, 7, true, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(worldGenLevel, random, boundingBox, this.entryDoor, 1, 7, 0);
            this.generateSmallDoor(worldGenLevel, random, boundingBox, StrongholdPiece.SmallDoorType.OPENING, 1, 1, 7);
            BlockState blockState = (BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
            for (int i = 0; i < 6; ++i) {
                this.placeBlock(worldGenLevel, blockState, 1, 6 - i, 1 + i, boundingBox);
                this.placeBlock(worldGenLevel, blockState, 2, 6 - i, 1 + i, boundingBox);
                this.placeBlock(worldGenLevel, blockState, 3, 6 - i, 1 + i, boundingBox);
                if (i >= 5) continue;
                this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5 - i, 1 + i, boundingBox);
                this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 2, 5 - i, 1 + i, boundingBox);
                this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 3, 5 - i, 1 + i, boundingBox);
            }
            return true;
        }
    }

    public static class ChestCorridor
    extends StrongholdPiece {
        private boolean hasPlacedChest;

        public ChestCorridor(int n, Random random, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, n);
            this.setOrientation(direction);
            this.entryDoor = this.randomSmallDoor(random);
            this.boundingBox = boundingBox;
        }

        public ChestCorridor(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, compoundTag);
            this.hasPlacedChest = compoundTag.getBoolean("Chest");
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
            super.addAdditionalSaveData(compoundTag);
            compoundTag.putBoolean("Chest", this.hasPlacedChest);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.generateSmallDoorChildForward((StartPiece)structurePiece, list, random, 1, 1);
        }

        public static ChestCorridor createPiece(List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -1, -1, 0, 5, 5, 7, direction);
            if (!ChestCorridor.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new ChestCorridor(n4, random, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 0, 4, 4, 6, true, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(worldGenLevel, random, boundingBox, this.entryDoor, 1, 1, 0);
            this.generateSmallDoor(worldGenLevel, random, boundingBox, StrongholdPiece.SmallDoorType.OPENING, 1, 1, 6);
            this.generateBox(worldGenLevel, boundingBox, 3, 1, 2, 3, 1, 4, Blocks.STONE_BRICKS.defaultBlockState(), Blocks.STONE_BRICKS.defaultBlockState(), false);
            this.placeBlock(worldGenLevel, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 1, 1, boundingBox);
            this.placeBlock(worldGenLevel, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 1, 5, boundingBox);
            this.placeBlock(worldGenLevel, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 2, 2, boundingBox);
            this.placeBlock(worldGenLevel, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 2, 4, boundingBox);
            for (int i = 2; i <= 4; ++i) {
                this.placeBlock(worldGenLevel, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 2, 1, i, boundingBox);
            }
            if (!this.hasPlacedChest && boundingBox.isInside(new BlockPos(this.getWorldX(3, 3), this.getWorldY(2), this.getWorldZ(3, 3)))) {
                this.hasPlacedChest = true;
                this.createChest(worldGenLevel, boundingBox, random, 3, 2, 3, BuiltInLootTables.STRONGHOLD_CORRIDOR);
            }
            return true;
        }
    }

    public static class Straight
    extends StrongholdPiece {
        private final boolean leftChild;
        private final boolean rightChild;

        public Straight(int n, Random random, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_STRAIGHT, n);
            this.setOrientation(direction);
            this.entryDoor = this.randomSmallDoor(random);
            this.boundingBox = boundingBox;
            this.leftChild = random.nextInt(2) == 0;
            this.rightChild = random.nextInt(2) == 0;
        }

        public Straight(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_STRAIGHT, compoundTag);
            this.leftChild = compoundTag.getBoolean("Left");
            this.rightChild = compoundTag.getBoolean("Right");
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
            super.addAdditionalSaveData(compoundTag);
            compoundTag.putBoolean("Left", this.leftChild);
            compoundTag.putBoolean("Right", this.rightChild);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            this.generateSmallDoorChildForward((StartPiece)structurePiece, list, random, 1, 1);
            if (this.leftChild) {
                this.generateSmallDoorChildLeft((StartPiece)structurePiece, list, random, 1, 2);
            }
            if (this.rightChild) {
                this.generateSmallDoorChildRight((StartPiece)structurePiece, list, random, 1, 2);
            }
        }

        public static Straight createPiece(List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -1, -1, 0, 5, 5, 7, direction);
            if (!Straight.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new Straight(n4, random, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 0, 4, 4, 6, true, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(worldGenLevel, random, boundingBox, this.entryDoor, 1, 1, 0);
            this.generateSmallDoor(worldGenLevel, random, boundingBox, StrongholdPiece.SmallDoorType.OPENING, 1, 1, 6);
            BlockState blockState = (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST);
            BlockState blockState2 = (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST);
            this.maybeGenerateBlock(worldGenLevel, boundingBox, random, 0.1f, 1, 2, 1, blockState);
            this.maybeGenerateBlock(worldGenLevel, boundingBox, random, 0.1f, 3, 2, 1, blockState2);
            this.maybeGenerateBlock(worldGenLevel, boundingBox, random, 0.1f, 1, 2, 5, blockState);
            this.maybeGenerateBlock(worldGenLevel, boundingBox, random, 0.1f, 3, 2, 5, blockState2);
            if (this.leftChild) {
                this.generateBox(worldGenLevel, boundingBox, 0, 1, 2, 0, 3, 4, CAVE_AIR, CAVE_AIR, false);
            }
            if (this.rightChild) {
                this.generateBox(worldGenLevel, boundingBox, 4, 1, 2, 4, 3, 4, CAVE_AIR, CAVE_AIR, false);
            }
            return true;
        }
    }

    public static class StartPiece
    extends StairsDown {
        public PieceWeight previousPiece;
        @Nullable
        public PortalRoom portalRoomPiece;
        public final List<StructurePiece> pendingChildren = Lists.newArrayList();

        public StartPiece(Random random, int n, int n2) {
            super(StructurePieceType.STRONGHOLD_START, 0, random, n, n2);
        }

        public StartPiece(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_START, compoundTag);
        }
    }

    public static class StairsDown
    extends StrongholdPiece {
        private final boolean isSource;

        public StairsDown(StructurePieceType structurePieceType, int n, Random random, int n2, int n3) {
            super(structurePieceType, n);
            this.isSource = true;
            this.setOrientation(Direction.Plane.HORIZONTAL.getRandomDirection(random));
            this.entryDoor = StrongholdPiece.SmallDoorType.OPENING;
            this.boundingBox = this.getOrientation().getAxis() == Direction.Axis.Z ? new BoundingBox(n2, 64, n3, n2 + 5 - 1, 74, n3 + 5 - 1) : new BoundingBox(n2, 64, n3, n2 + 5 - 1, 74, n3 + 5 - 1);
        }

        public StairsDown(int n, Random random, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_STAIRS_DOWN, n);
            this.isSource = false;
            this.setOrientation(direction);
            this.entryDoor = this.randomSmallDoor(random);
            this.boundingBox = boundingBox;
        }

        public StairsDown(StructurePieceType structurePieceType, CompoundTag compoundTag) {
            super(structurePieceType, compoundTag);
            this.isSource = compoundTag.getBoolean("Source");
        }

        public StairsDown(StructureManager structureManager, CompoundTag compoundTag) {
            this(StructurePieceType.STRONGHOLD_STAIRS_DOWN, compoundTag);
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
            super.addAdditionalSaveData(compoundTag);
            compoundTag.putBoolean("Source", this.isSource);
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            if (this.isSource) {
                imposedPiece = FiveCrossing.class;
            }
            this.generateSmallDoorChildForward((StartPiece)structurePiece, list, random, 1, 1);
        }

        public static StairsDown createPiece(List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction, int n4) {
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -1, -7, 0, 5, 11, 5, direction);
            if (!StairsDown.isOkBox(boundingBox) || StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return new StairsDown(n4, random, boundingBox, direction);
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 0, 4, 10, 4, true, random, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor(worldGenLevel, random, boundingBox, this.entryDoor, 1, 7, 0);
            this.generateSmallDoor(worldGenLevel, random, boundingBox, StrongholdPiece.SmallDoorType.OPENING, 1, 1, 4);
            this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 2, 6, 1, boundingBox);
            this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5, 1, boundingBox);
            this.placeBlock(worldGenLevel, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 6, 1, boundingBox);
            this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5, 2, boundingBox);
            this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 1, 4, 3, boundingBox);
            this.placeBlock(worldGenLevel, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 5, 3, boundingBox);
            this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 2, 4, 3, boundingBox);
            this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 3, 3, 3, boundingBox);
            this.placeBlock(worldGenLevel, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 3, 4, 3, boundingBox);
            this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 3, 3, 2, boundingBox);
            this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 3, 2, 1, boundingBox);
            this.placeBlock(worldGenLevel, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 3, 3, 1, boundingBox);
            this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 2, 2, 1, boundingBox);
            this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 1, 1, 1, boundingBox);
            this.placeBlock(worldGenLevel, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 2, 1, boundingBox);
            this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 1, 1, 2, boundingBox);
            this.placeBlock(worldGenLevel, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 1, 3, boundingBox);
            return true;
        }
    }

    public static class FillerCorridor
    extends StrongholdPiece {
        private final int steps;

        public FillerCorridor(int n, BoundingBox boundingBox, Direction direction) {
            super(StructurePieceType.STRONGHOLD_FILLER_CORRIDOR, n);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
            this.steps = direction == Direction.NORTH || direction == Direction.SOUTH ? boundingBox.getZSpan() : boundingBox.getXSpan();
        }

        public FillerCorridor(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.STRONGHOLD_FILLER_CORRIDOR, compoundTag);
            this.steps = compoundTag.getInt("Steps");
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
            super.addAdditionalSaveData(compoundTag);
            compoundTag.putInt("Steps", this.steps);
        }

        public static BoundingBox findPieceBox(List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction) {
            int n4 = 3;
            BoundingBox boundingBox = BoundingBox.orientBox(n, n2, n3, -1, -1, 0, 5, 5, 4, direction);
            StructurePiece structurePiece = StructurePiece.findCollisionPiece(list, boundingBox);
            if (structurePiece == null) {
                return null;
            }
            if (structurePiece.getBoundingBox().y0 == boundingBox.y0) {
                for (int i = 3; i >= 1; --i) {
                    boundingBox = BoundingBox.orientBox(n, n2, n3, -1, -1, 0, 5, 5, i - 1, direction);
                    if (structurePiece.getBoundingBox().intersects(boundingBox)) continue;
                    return BoundingBox.orientBox(n, n2, n3, -1, -1, 0, 5, 5, i, direction);
                }
            }
            return null;
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            for (int i = 0; i < this.steps; ++i) {
                this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 0, 0, i, boundingBox);
                this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 1, 0, i, boundingBox);
                this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 2, 0, i, boundingBox);
                this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 3, 0, i, boundingBox);
                this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 4, 0, i, boundingBox);
                for (int j = 1; j <= 3; ++j) {
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 0, j, i, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.CAVE_AIR.defaultBlockState(), 1, j, i, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.CAVE_AIR.defaultBlockState(), 2, j, i, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.CAVE_AIR.defaultBlockState(), 3, j, i, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 4, j, i, boundingBox);
                }
                this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 0, 4, i, boundingBox);
                this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 1, 4, i, boundingBox);
                this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 2, 4, i, boundingBox);
                this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 3, 4, i, boundingBox);
                this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), 4, 4, i, boundingBox);
            }
            return true;
        }
    }

    static abstract class StrongholdPiece
    extends StructurePiece {
        protected SmallDoorType entryDoor = SmallDoorType.OPENING;

        protected StrongholdPiece(StructurePieceType structurePieceType, int n) {
            super(structurePieceType, n);
        }

        public StrongholdPiece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
            super(structurePieceType, compoundTag);
            this.entryDoor = SmallDoorType.valueOf(compoundTag.getString("EntryDoor"));
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
            compoundTag.putString("EntryDoor", this.entryDoor.name());
        }

        protected void generateSmallDoor(WorldGenLevel worldGenLevel, Random random, BoundingBox boundingBox, SmallDoorType smallDoorType, int n, int n2, int n3) {
            switch (smallDoorType) {
                case OPENING: {
                    this.generateBox(worldGenLevel, boundingBox, n, n2, n3, n + 3 - 1, n2 + 3 - 1, n3, CAVE_AIR, CAVE_AIR, false);
                    break;
                }
                case WOOD_DOOR: {
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), n, n2, n3, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), n, n2 + 1, n3, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), n, n2 + 2, n3, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), n + 1, n2 + 2, n3, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), n + 2, n2 + 2, n3, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), n + 2, n2 + 1, n3, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), n + 2, n2, n3, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.OAK_DOOR.defaultBlockState(), n + 1, n2, n3, boundingBox);
                    this.placeBlock(worldGenLevel, (BlockState)Blocks.OAK_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), n + 1, n2 + 1, n3, boundingBox);
                    break;
                }
                case GRATES: {
                    this.placeBlock(worldGenLevel, Blocks.CAVE_AIR.defaultBlockState(), n + 1, n2, n3, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.CAVE_AIR.defaultBlockState(), n + 1, n2 + 1, n3, boundingBox);
                    this.placeBlock(worldGenLevel, (BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true), n, n2, n3, boundingBox);
                    this.placeBlock(worldGenLevel, (BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true), n, n2 + 1, n3, boundingBox);
                    this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, true)).setValue(IronBarsBlock.WEST, true), n, n2 + 2, n3, boundingBox);
                    this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, true)).setValue(IronBarsBlock.WEST, true), n + 1, n2 + 2, n3, boundingBox);
                    this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, true)).setValue(IronBarsBlock.WEST, true), n + 2, n2 + 2, n3, boundingBox);
                    this.placeBlock(worldGenLevel, (BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, true), n + 2, n2 + 1, n3, boundingBox);
                    this.placeBlock(worldGenLevel, (BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, true), n + 2, n2, n3, boundingBox);
                    break;
                }
                case IRON_DOOR: {
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), n, n2, n3, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), n, n2 + 1, n3, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), n, n2 + 2, n3, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), n + 1, n2 + 2, n3, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), n + 2, n2 + 2, n3, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), n + 2, n2 + 1, n3, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.STONE_BRICKS.defaultBlockState(), n + 2, n2, n3, boundingBox);
                    this.placeBlock(worldGenLevel, Blocks.IRON_DOOR.defaultBlockState(), n + 1, n2, n3, boundingBox);
                    this.placeBlock(worldGenLevel, (BlockState)Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), n + 1, n2 + 1, n3, boundingBox);
                    this.placeBlock(worldGenLevel, (BlockState)Blocks.STONE_BUTTON.defaultBlockState().setValue(ButtonBlock.FACING, Direction.NORTH), n + 2, n2 + 1, n3 + 1, boundingBox);
                    this.placeBlock(worldGenLevel, (BlockState)Blocks.STONE_BUTTON.defaultBlockState().setValue(ButtonBlock.FACING, Direction.SOUTH), n + 2, n2 + 1, n3 - 1, boundingBox);
                }
            }
        }

        protected SmallDoorType randomSmallDoor(Random random) {
            int n = random.nextInt(5);
            switch (n) {
                default: {
                    return SmallDoorType.OPENING;
                }
                case 2: {
                    return SmallDoorType.WOOD_DOOR;
                }
                case 3: {
                    return SmallDoorType.GRATES;
                }
                case 4: 
            }
            return SmallDoorType.IRON_DOOR;
        }

        @Nullable
        protected StructurePiece generateSmallDoorChildForward(StartPiece startPiece, List<StructurePiece> list, Random random, int n, int n2) {
            Direction direction = this.getOrientation();
            if (direction != null) {
                switch (direction) {
                    case NORTH: {
                        return StrongholdPieces.generateAndAddPiece(startPiece, list, random, this.boundingBox.x0 + n, this.boundingBox.y0 + n2, this.boundingBox.z0 - 1, direction, this.getGenDepth());
                    }
                    case SOUTH: {
                        return StrongholdPieces.generateAndAddPiece(startPiece, list, random, this.boundingBox.x0 + n, this.boundingBox.y0 + n2, this.boundingBox.z1 + 1, direction, this.getGenDepth());
                    }
                    case WEST: {
                        return StrongholdPieces.generateAndAddPiece(startPiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + n2, this.boundingBox.z0 + n, direction, this.getGenDepth());
                    }
                    case EAST: {
                        return StrongholdPieces.generateAndAddPiece(startPiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + n2, this.boundingBox.z0 + n, direction, this.getGenDepth());
                    }
                }
            }
            return null;
        }

        @Nullable
        protected StructurePiece generateSmallDoorChildLeft(StartPiece startPiece, List<StructurePiece> list, Random random, int n, int n2) {
            Direction direction = this.getOrientation();
            if (direction != null) {
                switch (direction) {
                    case NORTH: {
                        return StrongholdPieces.generateAndAddPiece(startPiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + n, this.boundingBox.z0 + n2, Direction.WEST, this.getGenDepth());
                    }
                    case SOUTH: {
                        return StrongholdPieces.generateAndAddPiece(startPiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + n, this.boundingBox.z0 + n2, Direction.WEST, this.getGenDepth());
                    }
                    case WEST: {
                        return StrongholdPieces.generateAndAddPiece(startPiece, list, random, this.boundingBox.x0 + n2, this.boundingBox.y0 + n, this.boundingBox.z0 - 1, Direction.NORTH, this.getGenDepth());
                    }
                    case EAST: {
                        return StrongholdPieces.generateAndAddPiece(startPiece, list, random, this.boundingBox.x0 + n2, this.boundingBox.y0 + n, this.boundingBox.z0 - 1, Direction.NORTH, this.getGenDepth());
                    }
                }
            }
            return null;
        }

        @Nullable
        protected StructurePiece generateSmallDoorChildRight(StartPiece startPiece, List<StructurePiece> list, Random random, int n, int n2) {
            Direction direction = this.getOrientation();
            if (direction != null) {
                switch (direction) {
                    case NORTH: {
                        return StrongholdPieces.generateAndAddPiece(startPiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + n, this.boundingBox.z0 + n2, Direction.EAST, this.getGenDepth());
                    }
                    case SOUTH: {
                        return StrongholdPieces.generateAndAddPiece(startPiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + n, this.boundingBox.z0 + n2, Direction.EAST, this.getGenDepth());
                    }
                    case WEST: {
                        return StrongholdPieces.generateAndAddPiece(startPiece, list, random, this.boundingBox.x0 + n2, this.boundingBox.y0 + n, this.boundingBox.z1 + 1, Direction.SOUTH, this.getGenDepth());
                    }
                    case EAST: {
                        return StrongholdPieces.generateAndAddPiece(startPiece, list, random, this.boundingBox.x0 + n2, this.boundingBox.y0 + n, this.boundingBox.z1 + 1, Direction.SOUTH, this.getGenDepth());
                    }
                }
            }
            return null;
        }

        protected static boolean isOkBox(BoundingBox boundingBox) {
            return boundingBox != null && boundingBox.y0 > 10;
        }

        public static enum SmallDoorType {
            OPENING,
            WOOD_DOOR,
            GRATES,
            IRON_DOOR;
            
        }

    }

    static class PieceWeight {
        public final Class<? extends StrongholdPiece> pieceClass;
        public final int weight;
        public int placeCount;
        public final int maxPlaceCount;

        public PieceWeight(Class<? extends StrongholdPiece> class_, int n, int n2) {
            this.pieceClass = class_;
            this.weight = n;
            this.maxPlaceCount = n2;
        }

        public boolean doPlace(int n) {
            return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
        }

        public boolean isValid() {
            return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
        }
    }

}

