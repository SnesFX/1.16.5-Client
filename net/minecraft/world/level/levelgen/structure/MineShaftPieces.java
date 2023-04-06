/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class MineShaftPieces {
    private static MineShaftPiece createRandomShaftPiece(List<StructurePiece> list, Random random, int n, int n2, int n3, @Nullable Direction direction, int n4, MineshaftFeature.Type type) {
        int n5 = random.nextInt(100);
        if (n5 >= 80) {
            BoundingBox boundingBox = MineShaftCrossing.findCrossing(list, random, n, n2, n3, direction);
            if (boundingBox != null) {
                return new MineShaftCrossing(n4, boundingBox, direction, type);
            }
        } else if (n5 >= 70) {
            BoundingBox boundingBox = MineShaftStairs.findStairs(list, random, n, n2, n3, direction);
            if (boundingBox != null) {
                return new MineShaftStairs(n4, boundingBox, direction, type);
            }
        } else {
            BoundingBox boundingBox = MineShaftCorridor.findCorridorSize(list, random, n, n2, n3, direction);
            if (boundingBox != null) {
                return new MineShaftCorridor(n4, random, boundingBox, direction, type);
            }
        }
        return null;
    }

    private static MineShaftPiece generateAndAddPiece(StructurePiece structurePiece, List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction, int n4) {
        if (n4 > 8) {
            return null;
        }
        if (Math.abs(n - structurePiece.getBoundingBox().x0) > 80 || Math.abs(n3 - structurePiece.getBoundingBox().z0) > 80) {
            return null;
        }
        MineshaftFeature.Type type = ((MineShaftPiece)structurePiece).type;
        MineShaftPiece mineShaftPiece = MineShaftPieces.createRandomShaftPiece(list, random, n, n2, n3, direction, n4 + 1, type);
        if (mineShaftPiece != null) {
            list.add(mineShaftPiece);
            mineShaftPiece.addChildren(structurePiece, list, random);
        }
        return mineShaftPiece;
    }

    public static class MineShaftStairs
    extends MineShaftPiece {
        public MineShaftStairs(int n, BoundingBox boundingBox, Direction direction, MineshaftFeature.Type type) {
            super(StructurePieceType.MINE_SHAFT_STAIRS, n, type);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
        }

        public MineShaftStairs(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.MINE_SHAFT_STAIRS, compoundTag);
        }

        public static BoundingBox findStairs(List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction) {
            BoundingBox boundingBox = new BoundingBox(n, n2 - 5, n3, n, n2 + 3 - 1, n3);
            switch (direction) {
                default: {
                    boundingBox.x1 = n + 3 - 1;
                    boundingBox.z0 = n3 - 8;
                    break;
                }
                case SOUTH: {
                    boundingBox.x1 = n + 3 - 1;
                    boundingBox.z1 = n3 + 8;
                    break;
                }
                case WEST: {
                    boundingBox.x0 = n - 8;
                    boundingBox.z1 = n3 + 3 - 1;
                    break;
                }
                case EAST: {
                    boundingBox.x1 = n + 8;
                    boundingBox.z1 = n3 + 3 - 1;
                }
            }
            if (StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return boundingBox;
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            int n = this.getGenDepth();
            Direction direction = this.getOrientation();
            if (direction != null) {
                switch (direction) {
                    default: {
                        MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, n);
                        break;
                    }
                    case SOUTH: {
                        MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, n);
                        break;
                    }
                    case WEST: {
                        MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0, Direction.WEST, n);
                        break;
                    }
                    case EAST: {
                        MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0, Direction.EAST, n);
                    }
                }
            }
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            if (this.edgesLiquid(worldGenLevel, boundingBox)) {
                return false;
            }
            this.generateBox(worldGenLevel, boundingBox, 0, 5, 0, 2, 7, 1, CAVE_AIR, CAVE_AIR, false);
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 7, 2, 2, 8, CAVE_AIR, CAVE_AIR, false);
            for (int i = 0; i < 5; ++i) {
                this.generateBox(worldGenLevel, boundingBox, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, CAVE_AIR, CAVE_AIR, false);
            }
            return true;
        }
    }

    public static class MineShaftCrossing
    extends MineShaftPiece {
        private final Direction direction;
        private final boolean isTwoFloored;

        public MineShaftCrossing(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.MINE_SHAFT_CROSSING, compoundTag);
            this.isTwoFloored = compoundTag.getBoolean("tf");
            this.direction = Direction.from2DDataValue(compoundTag.getInt("D"));
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
            super.addAdditionalSaveData(compoundTag);
            compoundTag.putBoolean("tf", this.isTwoFloored);
            compoundTag.putInt("D", this.direction.get2DDataValue());
        }

        public MineShaftCrossing(int n, BoundingBox boundingBox, @Nullable Direction direction, MineshaftFeature.Type type) {
            super(StructurePieceType.MINE_SHAFT_CROSSING, n, type);
            this.direction = direction;
            this.boundingBox = boundingBox;
            this.isTwoFloored = boundingBox.getYSpan() > 3;
        }

        public static BoundingBox findCrossing(List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction) {
            BoundingBox boundingBox = new BoundingBox(n, n2, n3, n, n2 + 3 - 1, n3);
            if (random.nextInt(4) == 0) {
                boundingBox.y1 += 4;
            }
            switch (direction) {
                default: {
                    boundingBox.x0 = n - 1;
                    boundingBox.x1 = n + 3;
                    boundingBox.z0 = n3 - 4;
                    break;
                }
                case SOUTH: {
                    boundingBox.x0 = n - 1;
                    boundingBox.x1 = n + 3;
                    boundingBox.z1 = n3 + 3 + 1;
                    break;
                }
                case WEST: {
                    boundingBox.x0 = n - 4;
                    boundingBox.z0 = n3 - 1;
                    boundingBox.z1 = n3 + 3;
                    break;
                }
                case EAST: {
                    boundingBox.x1 = n + 3 + 1;
                    boundingBox.z0 = n3 - 1;
                    boundingBox.z1 = n3 + 3;
                }
            }
            if (StructurePiece.findCollisionPiece(list, boundingBox) != null) {
                return null;
            }
            return boundingBox;
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            int n = this.getGenDepth();
            switch (this.direction) {
                default: {
                    MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, n);
                    MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.WEST, n);
                    MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.EAST, n);
                    break;
                }
                case SOUTH: {
                    MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, n);
                    MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.WEST, n);
                    MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.EAST, n);
                    break;
                }
                case WEST: {
                    MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, n);
                    MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, n);
                    MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.WEST, n);
                    break;
                }
                case EAST: {
                    MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, n);
                    MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, n);
                    MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.EAST, n);
                }
            }
            if (this.isTwoFloored) {
                if (random.nextBoolean()) {
                    MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z0 - 1, Direction.NORTH, n);
                }
                if (random.nextBoolean()) {
                    MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z0 + 1, Direction.WEST, n);
                }
                if (random.nextBoolean()) {
                    MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z0 + 1, Direction.EAST, n);
                }
                if (random.nextBoolean()) {
                    MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z1 + 1, Direction.SOUTH, n);
                }
            }
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            if (this.edgesLiquid(worldGenLevel, boundingBox)) {
                return false;
            }
            BlockState blockState = this.getPlanksBlock();
            if (this.isTwoFloored) {
                this.generateBox(worldGenLevel, boundingBox, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0, this.boundingBox.x1 - 1, this.boundingBox.y0 + 3 - 1, this.boundingBox.z1, CAVE_AIR, CAVE_AIR, false);
                this.generateBox(worldGenLevel, boundingBox, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.x1, this.boundingBox.y0 + 3 - 1, this.boundingBox.z1 - 1, CAVE_AIR, CAVE_AIR, false);
                this.generateBox(worldGenLevel, boundingBox, this.boundingBox.x0 + 1, this.boundingBox.y1 - 2, this.boundingBox.z0, this.boundingBox.x1 - 1, this.boundingBox.y1, this.boundingBox.z1, CAVE_AIR, CAVE_AIR, false);
                this.generateBox(worldGenLevel, boundingBox, this.boundingBox.x0, this.boundingBox.y1 - 2, this.boundingBox.z0 + 1, this.boundingBox.x1, this.boundingBox.y1, this.boundingBox.z1 - 1, CAVE_AIR, CAVE_AIR, false);
                this.generateBox(worldGenLevel, boundingBox, this.boundingBox.x0 + 1, this.boundingBox.y0 + 3, this.boundingBox.z0 + 1, this.boundingBox.x1 - 1, this.boundingBox.y0 + 3, this.boundingBox.z1 - 1, CAVE_AIR, CAVE_AIR, false);
            } else {
                this.generateBox(worldGenLevel, boundingBox, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0, this.boundingBox.x1 - 1, this.boundingBox.y1, this.boundingBox.z1, CAVE_AIR, CAVE_AIR, false);
                this.generateBox(worldGenLevel, boundingBox, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.x1, this.boundingBox.y1, this.boundingBox.z1 - 1, CAVE_AIR, CAVE_AIR, false);
            }
            this.placeSupportPillar(worldGenLevel, boundingBox, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.y1);
            this.placeSupportPillar(worldGenLevel, boundingBox, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 - 1, this.boundingBox.y1);
            this.placeSupportPillar(worldGenLevel, boundingBox, this.boundingBox.x1 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.y1);
            this.placeSupportPillar(worldGenLevel, boundingBox, this.boundingBox.x1 - 1, this.boundingBox.y0, this.boundingBox.z1 - 1, this.boundingBox.y1);
            for (int i = this.boundingBox.x0; i <= this.boundingBox.x1; ++i) {
                for (int j = this.boundingBox.z0; j <= this.boundingBox.z1; ++j) {
                    if (!this.getBlock(worldGenLevel, i, this.boundingBox.y0 - 1, j, boundingBox).isAir() || !this.isInterior(worldGenLevel, i, this.boundingBox.y0 - 1, j, boundingBox)) continue;
                    this.placeBlock(worldGenLevel, blockState, i, this.boundingBox.y0 - 1, j, boundingBox);
                }
            }
            return true;
        }

        private void placeSupportPillar(WorldGenLevel worldGenLevel, BoundingBox boundingBox, int n, int n2, int n3, int n4) {
            if (!this.getBlock(worldGenLevel, n, n4 + 1, n3, boundingBox).isAir()) {
                this.generateBox(worldGenLevel, boundingBox, n, n2, n3, n, n4, n3, this.getPlanksBlock(), CAVE_AIR, false);
            }
        }
    }

    public static class MineShaftCorridor
    extends MineShaftPiece {
        private final boolean hasRails;
        private final boolean spiderCorridor;
        private boolean hasPlacedSpider;
        private final int numSections;

        public MineShaftCorridor(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.MINE_SHAFT_CORRIDOR, compoundTag);
            this.hasRails = compoundTag.getBoolean("hr");
            this.spiderCorridor = compoundTag.getBoolean("sc");
            this.hasPlacedSpider = compoundTag.getBoolean("hps");
            this.numSections = compoundTag.getInt("Num");
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
            super.addAdditionalSaveData(compoundTag);
            compoundTag.putBoolean("hr", this.hasRails);
            compoundTag.putBoolean("sc", this.spiderCorridor);
            compoundTag.putBoolean("hps", this.hasPlacedSpider);
            compoundTag.putInt("Num", this.numSections);
        }

        public MineShaftCorridor(int n, Random random, BoundingBox boundingBox, Direction direction, MineshaftFeature.Type type) {
            super(StructurePieceType.MINE_SHAFT_CORRIDOR, n, type);
            this.setOrientation(direction);
            this.boundingBox = boundingBox;
            this.hasRails = random.nextInt(3) == 0;
            this.spiderCorridor = !this.hasRails && random.nextInt(23) == 0;
            this.numSections = this.getOrientation().getAxis() == Direction.Axis.Z ? boundingBox.getZSpan() / 5 : boundingBox.getXSpan() / 5;
        }

        public static BoundingBox findCorridorSize(List<StructurePiece> list, Random random, int n, int n2, int n3, Direction direction) {
            int n4;
            BoundingBox boundingBox = new BoundingBox(n, n2, n3, n, n2 + 3 - 1, n3);
            for (n4 = random.nextInt((int)3) + 2; n4 > 0; --n4) {
                int n5 = n4 * 5;
                switch (direction) {
                    default: {
                        boundingBox.x1 = n + 3 - 1;
                        boundingBox.z0 = n3 - (n5 - 1);
                        break;
                    }
                    case SOUTH: {
                        boundingBox.x1 = n + 3 - 1;
                        boundingBox.z1 = n3 + n5 - 1;
                        break;
                    }
                    case WEST: {
                        boundingBox.x0 = n - (n5 - 1);
                        boundingBox.z1 = n3 + 3 - 1;
                        break;
                    }
                    case EAST: {
                        boundingBox.x1 = n + n5 - 1;
                        boundingBox.z1 = n3 + 3 - 1;
                    }
                }
                if (StructurePiece.findCollisionPiece(list, boundingBox) == null) break;
            }
            if (n4 > 0) {
                return boundingBox;
            }
            return null;
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            block24 : {
                int n = this.getGenDepth();
                int n2 = random.nextInt(4);
                Direction direction = this.getOrientation();
                if (direction != null) {
                    switch (direction) {
                        default: {
                            if (n2 <= 1) {
                                MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z0 - 1, direction, n);
                                break;
                            }
                            if (n2 == 2) {
                                MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z0, Direction.WEST, n);
                                break;
                            }
                            MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z0, Direction.EAST, n);
                            break;
                        }
                        case SOUTH: {
                            if (n2 <= 1) {
                                MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z1 + 1, direction, n);
                                break;
                            }
                            if (n2 == 2) {
                                MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z1 - 3, Direction.WEST, n);
                                break;
                            }
                            MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z1 - 3, Direction.EAST, n);
                            break;
                        }
                        case WEST: {
                            if (n2 <= 1) {
                                MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z0, direction, n);
                                break;
                            }
                            if (n2 == 2) {
                                MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z0 - 1, Direction.NORTH, n);
                                break;
                            }
                            MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z1 + 1, Direction.SOUTH, n);
                            break;
                        }
                        case EAST: {
                            if (n2 <= 1) {
                                MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z0, direction, n);
                                break;
                            }
                            if (n2 == 2) {
                                MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 - 3, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z0 - 1, Direction.NORTH, n);
                                break;
                            }
                            MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 - 3, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z1 + 1, Direction.SOUTH, n);
                        }
                    }
                }
                if (n >= 8) break block24;
                if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                    int n3 = this.boundingBox.z0 + 3;
                    while (n3 + 3 <= this.boundingBox.z1) {
                        int n4 = random.nextInt(5);
                        if (n4 == 0) {
                            MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0, n3, Direction.WEST, n + 1);
                        } else if (n4 == 1) {
                            MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0, n3, Direction.EAST, n + 1);
                        }
                        n3 += 5;
                    }
                } else {
                    int n5 = this.boundingBox.x0 + 3;
                    while (n5 + 3 <= this.boundingBox.x1) {
                        int n6 = random.nextInt(5);
                        if (n6 == 0) {
                            MineShaftPieces.generateAndAddPiece(structurePiece, list, random, n5, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, n + 1);
                        } else if (n6 == 1) {
                            MineShaftPieces.generateAndAddPiece(structurePiece, list, random, n5, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, n + 1);
                        }
                        n5 += 5;
                    }
                }
            }
        }

        @Override
        protected boolean createChest(WorldGenLevel worldGenLevel, BoundingBox boundingBox, Random random, int n, int n2, int n3, ResourceLocation resourceLocation) {
            BlockPos blockPos = new BlockPos(this.getWorldX(n, n3), this.getWorldY(n2), this.getWorldZ(n, n3));
            if (boundingBox.isInside(blockPos) && worldGenLevel.getBlockState(blockPos).isAir() && !worldGenLevel.getBlockState(blockPos.below()).isAir()) {
                BlockState blockState = (BlockState)Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, random.nextBoolean() ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);
                this.placeBlock(worldGenLevel, blockState, n, n2, n3, boundingBox);
                MinecartChest minecartChest = new MinecartChest(worldGenLevel.getLevel(), (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
                minecartChest.setLootTable(resourceLocation, random.nextLong());
                worldGenLevel.addFreshEntity(minecartChest);
                return true;
            }
            return false;
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            int n;
            int n2;
            int n3;
            int n4;
            if (this.edgesLiquid(worldGenLevel, boundingBox)) {
                return false;
            }
            boolean bl = false;
            int n5 = 2;
            boolean bl2 = false;
            int n6 = 2;
            int n7 = this.numSections * 5 - 1;
            BlockState blockState = this.getPlanksBlock();
            this.generateBox(worldGenLevel, boundingBox, 0, 0, 0, 2, 1, n7, CAVE_AIR, CAVE_AIR, false);
            this.generateMaybeBox(worldGenLevel, boundingBox, random, 0.8f, 0, 2, 0, 2, 2, n7, CAVE_AIR, CAVE_AIR, false, false);
            if (this.spiderCorridor) {
                this.generateMaybeBox(worldGenLevel, boundingBox, random, 0.6f, 0, 0, 0, 2, 1, n7, Blocks.COBWEB.defaultBlockState(), CAVE_AIR, false, true);
            }
            for (n3 = 0; n3 < this.numSections; ++n3) {
                int n8;
                n = 2 + n3 * 5;
                this.placeSupport(worldGenLevel, boundingBox, 0, 0, n, 2, 2, random);
                this.placeCobWeb(worldGenLevel, boundingBox, random, 0.1f, 0, 2, n - 1);
                this.placeCobWeb(worldGenLevel, boundingBox, random, 0.1f, 2, 2, n - 1);
                this.placeCobWeb(worldGenLevel, boundingBox, random, 0.1f, 0, 2, n + 1);
                this.placeCobWeb(worldGenLevel, boundingBox, random, 0.1f, 2, 2, n + 1);
                this.placeCobWeb(worldGenLevel, boundingBox, random, 0.05f, 0, 2, n - 2);
                this.placeCobWeb(worldGenLevel, boundingBox, random, 0.05f, 2, 2, n - 2);
                this.placeCobWeb(worldGenLevel, boundingBox, random, 0.05f, 0, 2, n + 2);
                this.placeCobWeb(worldGenLevel, boundingBox, random, 0.05f, 2, 2, n + 2);
                if (random.nextInt(100) == 0) {
                    this.createChest(worldGenLevel, boundingBox, random, 2, 0, n - 1, BuiltInLootTables.ABANDONED_MINESHAFT);
                }
                if (random.nextInt(100) == 0) {
                    this.createChest(worldGenLevel, boundingBox, random, 0, 0, n + 1, BuiltInLootTables.ABANDONED_MINESHAFT);
                }
                if (!this.spiderCorridor || this.hasPlacedSpider) continue;
                n2 = this.getWorldY(0);
                int n9 = n - 1 + random.nextInt(3);
                n4 = this.getWorldX(1, n9);
                BlockPos blockPos2 = new BlockPos(n4, n2, n8 = this.getWorldZ(1, n9));
                if (!boundingBox.isInside(blockPos2) || !this.isInterior(worldGenLevel, 1, 0, n9, boundingBox)) continue;
                this.hasPlacedSpider = true;
                worldGenLevel.setBlock(blockPos2, Blocks.SPAWNER.defaultBlockState(), 2);
                BlockEntity blockEntity = worldGenLevel.getBlockEntity(blockPos2);
                if (!(blockEntity instanceof SpawnerBlockEntity)) continue;
                ((SpawnerBlockEntity)blockEntity).getSpawner().setEntityId(EntityType.CAVE_SPIDER);
            }
            for (n3 = 0; n3 <= 2; ++n3) {
                for (n = 0; n <= n7; ++n) {
                    n2 = -1;
                    BlockState blockState2 = this.getBlock(worldGenLevel, n3, -1, n, boundingBox);
                    if (!blockState2.isAir() || !this.isInterior(worldGenLevel, n3, -1, n, boundingBox)) continue;
                    n4 = -1;
                    this.placeBlock(worldGenLevel, blockState, n3, -1, n, boundingBox);
                }
            }
            if (this.hasRails) {
                BlockState blockState3 = (BlockState)Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.NORTH_SOUTH);
                for (n = 0; n <= n7; ++n) {
                    BlockState blockState4 = this.getBlock(worldGenLevel, 1, -1, n, boundingBox);
                    if (blockState4.isAir() || !blockState4.isSolidRender(worldGenLevel, new BlockPos(this.getWorldX(1, n), this.getWorldY(-1), this.getWorldZ(1, n)))) continue;
                    float f = this.isInterior(worldGenLevel, 1, 0, n, boundingBox) ? 0.7f : 0.9f;
                    this.maybeGenerateBlock(worldGenLevel, boundingBox, random, f, 1, 0, n, blockState3);
                }
            }
            return true;
        }

        private void placeSupport(WorldGenLevel worldGenLevel, BoundingBox boundingBox, int n, int n2, int n3, int n4, int n5, Random random) {
            if (!this.isSupportingBox(worldGenLevel, boundingBox, n, n5, n4, n3)) {
                return;
            }
            BlockState blockState = this.getPlanksBlock();
            BlockState blockState2 = this.getFenceBlock();
            this.generateBox(worldGenLevel, boundingBox, n, n2, n3, n, n4 - 1, n3, (BlockState)blockState2.setValue(FenceBlock.WEST, true), CAVE_AIR, false);
            this.generateBox(worldGenLevel, boundingBox, n5, n2, n3, n5, n4 - 1, n3, (BlockState)blockState2.setValue(FenceBlock.EAST, true), CAVE_AIR, false);
            if (random.nextInt(4) == 0) {
                this.generateBox(worldGenLevel, boundingBox, n, n4, n3, n, n4, n3, blockState, CAVE_AIR, false);
                this.generateBox(worldGenLevel, boundingBox, n5, n4, n3, n5, n4, n3, blockState, CAVE_AIR, false);
            } else {
                this.generateBox(worldGenLevel, boundingBox, n, n4, n3, n5, n4, n3, blockState, CAVE_AIR, false);
                this.maybeGenerateBlock(worldGenLevel, boundingBox, random, 0.05f, n + 1, n4, n3 - 1, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.NORTH));
                this.maybeGenerateBlock(worldGenLevel, boundingBox, random, 0.05f, n + 1, n4, n3 + 1, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH));
            }
        }

        private void placeCobWeb(WorldGenLevel worldGenLevel, BoundingBox boundingBox, Random random, float f, int n, int n2, int n3) {
            if (this.isInterior(worldGenLevel, n, n2, n3, boundingBox)) {
                this.maybeGenerateBlock(worldGenLevel, boundingBox, random, f, n, n2, n3, Blocks.COBWEB.defaultBlockState());
            }
        }
    }

    public static class MineShaftRoom
    extends MineShaftPiece {
        private final List<BoundingBox> childEntranceBoxes = Lists.newLinkedList();

        public MineShaftRoom(int n, Random random, int n2, int n3, MineshaftFeature.Type type) {
            super(StructurePieceType.MINE_SHAFT_ROOM, n, type);
            this.type = type;
            this.boundingBox = new BoundingBox(n2, 50, n3, n2 + 7 + random.nextInt(6), 54 + random.nextInt(6), n3 + 7 + random.nextInt(6));
        }

        public MineShaftRoom(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.MINE_SHAFT_ROOM, compoundTag);
            ListTag listTag = compoundTag.getList("Entrances", 11);
            for (int i = 0; i < listTag.size(); ++i) {
                this.childEntranceBoxes.add(new BoundingBox(listTag.getIntArray(i)));
            }
        }

        @Override
        public void addChildren(StructurePiece structurePiece, List<StructurePiece> list, Random random) {
            BoundingBox boundingBox;
            MineShaftPiece mineShaftPiece;
            int n = this.getGenDepth();
            int n2 = this.boundingBox.getYSpan() - 3 - 1;
            if (n2 <= 0) {
                n2 = 1;
            }
            int n3 = 0;
            while (n3 < this.boundingBox.getXSpan() && (n3 += random.nextInt(this.boundingBox.getXSpan())) + 3 <= this.boundingBox.getXSpan()) {
                mineShaftPiece = MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 + n3, this.boundingBox.y0 + random.nextInt(n2) + 1, this.boundingBox.z0 - 1, Direction.NORTH, n);
                if (mineShaftPiece != null) {
                    boundingBox = mineShaftPiece.getBoundingBox();
                    this.childEntranceBoxes.add(new BoundingBox(boundingBox.x0, boundingBox.y0, this.boundingBox.z0, boundingBox.x1, boundingBox.y1, this.boundingBox.z0 + 1));
                }
                n3 += 4;
            }
            n3 = 0;
            while (n3 < this.boundingBox.getXSpan() && (n3 += random.nextInt(this.boundingBox.getXSpan())) + 3 <= this.boundingBox.getXSpan()) {
                mineShaftPiece = MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 + n3, this.boundingBox.y0 + random.nextInt(n2) + 1, this.boundingBox.z1 + 1, Direction.SOUTH, n);
                if (mineShaftPiece != null) {
                    boundingBox = mineShaftPiece.getBoundingBox();
                    this.childEntranceBoxes.add(new BoundingBox(boundingBox.x0, boundingBox.y0, this.boundingBox.z1 - 1, boundingBox.x1, boundingBox.y1, this.boundingBox.z1));
                }
                n3 += 4;
            }
            n3 = 0;
            while (n3 < this.boundingBox.getZSpan() && (n3 += random.nextInt(this.boundingBox.getZSpan())) + 3 <= this.boundingBox.getZSpan()) {
                mineShaftPiece = MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + random.nextInt(n2) + 1, this.boundingBox.z0 + n3, Direction.WEST, n);
                if (mineShaftPiece != null) {
                    boundingBox = mineShaftPiece.getBoundingBox();
                    this.childEntranceBoxes.add(new BoundingBox(this.boundingBox.x0, boundingBox.y0, boundingBox.z0, this.boundingBox.x0 + 1, boundingBox.y1, boundingBox.z1));
                }
                n3 += 4;
            }
            n3 = 0;
            while (n3 < this.boundingBox.getZSpan() && (n3 += random.nextInt(this.boundingBox.getZSpan())) + 3 <= this.boundingBox.getZSpan()) {
                mineShaftPiece = MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + random.nextInt(n2) + 1, this.boundingBox.z0 + n3, Direction.EAST, n);
                if (mineShaftPiece != null) {
                    boundingBox = mineShaftPiece.getBoundingBox();
                    this.childEntranceBoxes.add(new BoundingBox(this.boundingBox.x1 - 1, boundingBox.y0, boundingBox.z0, this.boundingBox.x1, boundingBox.y1, boundingBox.z1));
                }
                n3 += 4;
            }
        }

        @Override
        public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
            if (this.edgesLiquid(worldGenLevel, boundingBox)) {
                return false;
            }
            this.generateBox(worldGenLevel, boundingBox, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0, this.boundingBox.x1, this.boundingBox.y0, this.boundingBox.z1, Blocks.DIRT.defaultBlockState(), CAVE_AIR, true);
            this.generateBox(worldGenLevel, boundingBox, this.boundingBox.x0, this.boundingBox.y0 + 1, this.boundingBox.z0, this.boundingBox.x1, Math.min(this.boundingBox.y0 + 3, this.boundingBox.y1), this.boundingBox.z1, CAVE_AIR, CAVE_AIR, false);
            for (BoundingBox boundingBox2 : this.childEntranceBoxes) {
                this.generateBox(worldGenLevel, boundingBox, boundingBox2.x0, boundingBox2.y1 - 2, boundingBox2.z0, boundingBox2.x1, boundingBox2.y1, boundingBox2.z1, CAVE_AIR, CAVE_AIR, false);
            }
            this.generateUpperHalfSphere(worldGenLevel, boundingBox, this.boundingBox.x0, this.boundingBox.y0 + 4, this.boundingBox.z0, this.boundingBox.x1, this.boundingBox.y1, this.boundingBox.z1, CAVE_AIR, false);
            return true;
        }

        @Override
        public void move(int n, int n2, int n3) {
            super.move(n, n2, n3);
            for (BoundingBox boundingBox : this.childEntranceBoxes) {
                boundingBox.move(n, n2, n3);
            }
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
            super.addAdditionalSaveData(compoundTag);
            ListTag listTag = new ListTag();
            for (BoundingBox boundingBox : this.childEntranceBoxes) {
                listTag.add(boundingBox.createTag());
            }
            compoundTag.put("Entrances", listTag);
        }
    }

    static abstract class MineShaftPiece
    extends StructurePiece {
        protected MineshaftFeature.Type type;

        public MineShaftPiece(StructurePieceType structurePieceType, int n, MineshaftFeature.Type type) {
            super(structurePieceType, n);
            this.type = type;
        }

        public MineShaftPiece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
            super(structurePieceType, compoundTag);
            this.type = MineshaftFeature.Type.byId(compoundTag.getInt("MST"));
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
            compoundTag.putInt("MST", this.type.ordinal());
        }

        protected BlockState getPlanksBlock() {
            switch (this.type) {
                default: {
                    return Blocks.OAK_PLANKS.defaultBlockState();
                }
                case MESA: 
            }
            return Blocks.DARK_OAK_PLANKS.defaultBlockState();
        }

        protected BlockState getFenceBlock() {
            switch (this.type) {
                default: {
                    return Blocks.OAK_FENCE.defaultBlockState();
                }
                case MESA: 
            }
            return Blocks.DARK_OAK_FENCE.defaultBlockState();
        }

        protected boolean isSupportingBox(BlockGetter blockGetter, BoundingBox boundingBox, int n, int n2, int n3, int n4) {
            for (int i = n; i <= n2; ++i) {
                if (!this.getBlock(blockGetter, i, n3 + 1, n4, boundingBox).isAir()) continue;
                return false;
            }
            return true;
        }
    }

}

