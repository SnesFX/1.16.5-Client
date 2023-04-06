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
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class WoodlandMansionPieces {
    public static void generateMansion(StructureManager structureManager, BlockPos blockPos, Rotation rotation, List<WoodlandMansionPiece> list, Random random) {
        MansionGrid mansionGrid = new MansionGrid(random);
        MansionPiecePlacer mansionPiecePlacer = new MansionPiecePlacer(structureManager, random);
        mansionPiecePlacer.createMansion(blockPos, rotation, list, mansionGrid);
    }

    static class ThirdFloorRoomCollection
    extends SecondFloorRoomCollection {
        private ThirdFloorRoomCollection() {
        }
    }

    static class SecondFloorRoomCollection
    extends FloorRoomCollection {
        private SecondFloorRoomCollection() {
        }

        @Override
        public String get1x1(Random random) {
            return "1x1_b" + (random.nextInt(4) + 1);
        }

        @Override
        public String get1x1Secret(Random random) {
            return "1x1_as" + (random.nextInt(4) + 1);
        }

        @Override
        public String get1x2SideEntrance(Random random, boolean bl) {
            if (bl) {
                return "1x2_c_stairs";
            }
            return "1x2_c" + (random.nextInt(4) + 1);
        }

        @Override
        public String get1x2FrontEntrance(Random random, boolean bl) {
            if (bl) {
                return "1x2_d_stairs";
            }
            return "1x2_d" + (random.nextInt(5) + 1);
        }

        @Override
        public String get1x2Secret(Random random) {
            return "1x2_se" + (random.nextInt(1) + 1);
        }

        @Override
        public String get2x2(Random random) {
            return "2x2_b" + (random.nextInt(5) + 1);
        }

        @Override
        public String get2x2Secret(Random random) {
            return "2x2_s1";
        }
    }

    static class FirstFloorRoomCollection
    extends FloorRoomCollection {
        private FirstFloorRoomCollection() {
        }

        @Override
        public String get1x1(Random random) {
            return "1x1_a" + (random.nextInt(5) + 1);
        }

        @Override
        public String get1x1Secret(Random random) {
            return "1x1_as" + (random.nextInt(4) + 1);
        }

        @Override
        public String get1x2SideEntrance(Random random, boolean bl) {
            return "1x2_a" + (random.nextInt(9) + 1);
        }

        @Override
        public String get1x2FrontEntrance(Random random, boolean bl) {
            return "1x2_b" + (random.nextInt(5) + 1);
        }

        @Override
        public String get1x2Secret(Random random) {
            return "1x2_s" + (random.nextInt(2) + 1);
        }

        @Override
        public String get2x2(Random random) {
            return "2x2_a" + (random.nextInt(4) + 1);
        }

        @Override
        public String get2x2Secret(Random random) {
            return "2x2_s1";
        }
    }

    static abstract class FloorRoomCollection {
        private FloorRoomCollection() {
        }

        public abstract String get1x1(Random var1);

        public abstract String get1x1Secret(Random var1);

        public abstract String get1x2SideEntrance(Random var1, boolean var2);

        public abstract String get1x2FrontEntrance(Random var1, boolean var2);

        public abstract String get1x2Secret(Random var1);

        public abstract String get2x2(Random var1);

        public abstract String get2x2Secret(Random var1);
    }

    static class SimpleGrid {
        private final int[][] grid;
        private final int width;
        private final int height;
        private final int valueIfOutside;

        public SimpleGrid(int n, int n2, int n3) {
            this.width = n;
            this.height = n2;
            this.valueIfOutside = n3;
            this.grid = new int[n][n2];
        }

        public void set(int n, int n2, int n3) {
            if (n >= 0 && n < this.width && n2 >= 0 && n2 < this.height) {
                this.grid[n][n2] = n3;
            }
        }

        public void set(int n, int n2, int n3, int n4, int n5) {
            for (int i = n2; i <= n4; ++i) {
                for (int j = n; j <= n3; ++j) {
                    this.set(j, i, n5);
                }
            }
        }

        public int get(int n, int n2) {
            if (n >= 0 && n < this.width && n2 >= 0 && n2 < this.height) {
                return this.grid[n][n2];
            }
            return this.valueIfOutside;
        }

        public void setif(int n, int n2, int n3, int n4) {
            if (this.get(n, n2) == n3) {
                this.set(n, n2, n4);
            }
        }

        public boolean edgesTo(int n, int n2, int n3) {
            return this.get(n - 1, n2) == n3 || this.get(n + 1, n2) == n3 || this.get(n, n2 + 1) == n3 || this.get(n, n2 - 1) == n3;
        }
    }

    static class MansionGrid {
        private final Random random;
        private final SimpleGrid baseGrid;
        private final SimpleGrid thirdFloorGrid;
        private final SimpleGrid[] floorRooms;
        private final int entranceX;
        private final int entranceY;

        public MansionGrid(Random random) {
            this.random = random;
            int n = 11;
            this.entranceX = 7;
            this.entranceY = 4;
            this.baseGrid = new SimpleGrid(11, 11, 5);
            this.baseGrid.set(this.entranceX, this.entranceY, this.entranceX + 1, this.entranceY + 1, 3);
            this.baseGrid.set(this.entranceX - 1, this.entranceY, this.entranceX - 1, this.entranceY + 1, 2);
            this.baseGrid.set(this.entranceX + 2, this.entranceY - 2, this.entranceX + 3, this.entranceY + 3, 5);
            this.baseGrid.set(this.entranceX + 1, this.entranceY - 2, this.entranceX + 1, this.entranceY - 1, 1);
            this.baseGrid.set(this.entranceX + 1, this.entranceY + 2, this.entranceX + 1, this.entranceY + 3, 1);
            this.baseGrid.set(this.entranceX - 1, this.entranceY - 1, 1);
            this.baseGrid.set(this.entranceX - 1, this.entranceY + 2, 1);
            this.baseGrid.set(0, 0, 11, 1, 5);
            this.baseGrid.set(0, 9, 11, 11, 5);
            this.recursiveCorridor(this.baseGrid, this.entranceX, this.entranceY - 2, Direction.WEST, 6);
            this.recursiveCorridor(this.baseGrid, this.entranceX, this.entranceY + 3, Direction.WEST, 6);
            this.recursiveCorridor(this.baseGrid, this.entranceX - 2, this.entranceY - 1, Direction.WEST, 3);
            this.recursiveCorridor(this.baseGrid, this.entranceX - 2, this.entranceY + 2, Direction.WEST, 3);
            while (this.cleanEdges(this.baseGrid)) {
            }
            this.floorRooms = new SimpleGrid[3];
            this.floorRooms[0] = new SimpleGrid(11, 11, 5);
            this.floorRooms[1] = new SimpleGrid(11, 11, 5);
            this.floorRooms[2] = new SimpleGrid(11, 11, 5);
            this.identifyRooms(this.baseGrid, this.floorRooms[0]);
            this.identifyRooms(this.baseGrid, this.floorRooms[1]);
            this.floorRooms[0].set(this.entranceX + 1, this.entranceY, this.entranceX + 1, this.entranceY + 1, 8388608);
            this.floorRooms[1].set(this.entranceX + 1, this.entranceY, this.entranceX + 1, this.entranceY + 1, 8388608);
            this.thirdFloorGrid = new SimpleGrid(this.baseGrid.width, this.baseGrid.height, 5);
            this.setupThirdFloor();
            this.identifyRooms(this.thirdFloorGrid, this.floorRooms[2]);
        }

        public static boolean isHouse(SimpleGrid simpleGrid, int n, int n2) {
            int n3 = simpleGrid.get(n, n2);
            return n3 == 1 || n3 == 2 || n3 == 3 || n3 == 4;
        }

        public boolean isRoomId(SimpleGrid simpleGrid, int n, int n2, int n3, int n4) {
            return (this.floorRooms[n3].get(n, n2) & 0xFFFF) == n4;
        }

        @Nullable
        public Direction get1x2RoomDirection(SimpleGrid simpleGrid, int n, int n2, int n3, int n4) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                if (!this.isRoomId(simpleGrid, n + direction.getStepX(), n2 + direction.getStepZ(), n3, n4)) continue;
                return direction;
            }
            return null;
        }

        private void recursiveCorridor(SimpleGrid simpleGrid, int n, int n2, Direction direction, int n3) {
            Direction direction2;
            if (n3 <= 0) {
                return;
            }
            simpleGrid.set(n, n2, 1);
            simpleGrid.setif(n + direction.getStepX(), n2 + direction.getStepZ(), 0, 1);
            for (int i = 0; i < 8; ++i) {
                direction2 = Direction.from2DDataValue(this.random.nextInt(4));
                if (direction2 == direction.getOpposite() || direction2 == Direction.EAST && this.random.nextBoolean()) continue;
                int n4 = n + direction.getStepX();
                int n5 = n2 + direction.getStepZ();
                if (simpleGrid.get(n4 + direction2.getStepX(), n5 + direction2.getStepZ()) != 0 || simpleGrid.get(n4 + direction2.getStepX() * 2, n5 + direction2.getStepZ() * 2) != 0) continue;
                this.recursiveCorridor(simpleGrid, n + direction.getStepX() + direction2.getStepX(), n2 + direction.getStepZ() + direction2.getStepZ(), direction2, n3 - 1);
                break;
            }
            Direction direction3 = direction.getClockWise();
            direction2 = direction.getCounterClockWise();
            simpleGrid.setif(n + direction3.getStepX(), n2 + direction3.getStepZ(), 0, 2);
            simpleGrid.setif(n + direction2.getStepX(), n2 + direction2.getStepZ(), 0, 2);
            simpleGrid.setif(n + direction.getStepX() + direction3.getStepX(), n2 + direction.getStepZ() + direction3.getStepZ(), 0, 2);
            simpleGrid.setif(n + direction.getStepX() + direction2.getStepX(), n2 + direction.getStepZ() + direction2.getStepZ(), 0, 2);
            simpleGrid.setif(n + direction.getStepX() * 2, n2 + direction.getStepZ() * 2, 0, 2);
            simpleGrid.setif(n + direction3.getStepX() * 2, n2 + direction3.getStepZ() * 2, 0, 2);
            simpleGrid.setif(n + direction2.getStepX() * 2, n2 + direction2.getStepZ() * 2, 0, 2);
        }

        private boolean cleanEdges(SimpleGrid simpleGrid) {
            boolean bl = false;
            for (int i = 0; i < simpleGrid.height; ++i) {
                for (int j = 0; j < simpleGrid.width; ++j) {
                    if (simpleGrid.get(j, i) != 0) continue;
                    int n = 0;
                    n += MansionGrid.isHouse(simpleGrid, j + 1, i) ? 1 : 0;
                    n += MansionGrid.isHouse(simpleGrid, j - 1, i) ? 1 : 0;
                    n += MansionGrid.isHouse(simpleGrid, j, i + 1) ? 1 : 0;
                    if ((n += MansionGrid.isHouse(simpleGrid, j, i - 1) ? 1 : 0) >= 3) {
                        simpleGrid.set(j, i, 2);
                        bl = true;
                        continue;
                    }
                    if (n != 2) continue;
                    int n2 = 0;
                    n2 += MansionGrid.isHouse(simpleGrid, j + 1, i + 1) ? 1 : 0;
                    n2 += MansionGrid.isHouse(simpleGrid, j - 1, i + 1) ? 1 : 0;
                    n2 += MansionGrid.isHouse(simpleGrid, j + 1, i - 1) ? 1 : 0;
                    if ((n2 += MansionGrid.isHouse(simpleGrid, j - 1, i - 1) ? 1 : 0) > 1) continue;
                    simpleGrid.set(j, i, 2);
                    bl = true;
                }
            }
            return bl;
        }

        private void setupThirdFloor() {
            int n;
            int n2;
            ArrayList arrayList = Lists.newArrayList();
            SimpleGrid simpleGrid = this.floorRooms[1];
            for (int i = 0; i < this.thirdFloorGrid.height; ++i) {
                for (n = 0; n < this.thirdFloorGrid.width; ++n) {
                    int n3 = simpleGrid.get(n, i);
                    n2 = n3 & 0xF0000;
                    if (n2 != 131072 || (n3 & 0x200000) != 2097152) continue;
                    arrayList.add(new Tuple<Integer, Integer>(n, i));
                }
            }
            if (arrayList.isEmpty()) {
                this.thirdFloorGrid.set(0, 0, this.thirdFloorGrid.width, this.thirdFloorGrid.height, 5);
                return;
            }
            Tuple tuple = (Tuple)arrayList.get(this.random.nextInt(arrayList.size()));
            n = simpleGrid.get((Integer)tuple.getA(), (Integer)tuple.getB());
            simpleGrid.set((Integer)tuple.getA(), (Integer)tuple.getB(), n | 0x400000);
            Direction direction = this.get1x2RoomDirection(this.baseGrid, (Integer)tuple.getA(), (Integer)tuple.getB(), 1, n & 0xFFFF);
            n2 = (Integer)tuple.getA() + direction.getStepX();
            int n4 = (Integer)tuple.getB() + direction.getStepZ();
            for (int i = 0; i < this.thirdFloorGrid.height; ++i) {
                for (int j = 0; j < this.thirdFloorGrid.width; ++j) {
                    if (!MansionGrid.isHouse(this.baseGrid, j, i)) {
                        this.thirdFloorGrid.set(j, i, 5);
                        continue;
                    }
                    if (j == (Integer)tuple.getA() && i == (Integer)tuple.getB()) {
                        this.thirdFloorGrid.set(j, i, 3);
                        continue;
                    }
                    if (j != n2 || i != n4) continue;
                    this.thirdFloorGrid.set(j, i, 3);
                    this.floorRooms[2].set(j, i, 8388608);
                }
            }
            ArrayList arrayList2 = Lists.newArrayList();
            for (Direction direction2 : Direction.Plane.HORIZONTAL) {
                if (this.thirdFloorGrid.get(n2 + direction2.getStepX(), n4 + direction2.getStepZ()) != 0) continue;
                arrayList2.add(direction2);
            }
            if (arrayList2.isEmpty()) {
                this.thirdFloorGrid.set(0, 0, this.thirdFloorGrid.width, this.thirdFloorGrid.height, 5);
                simpleGrid.set((Integer)tuple.getA(), (Integer)tuple.getB(), n);
                return;
            }
            Direction direction3 = (Direction)arrayList2.get(this.random.nextInt(arrayList2.size()));
            this.recursiveCorridor(this.thirdFloorGrid, n2 + direction3.getStepX(), n4 + direction3.getStepZ(), direction3, 4);
            while (this.cleanEdges(this.thirdFloorGrid)) {
            }
        }

        private void identifyRooms(SimpleGrid simpleGrid, SimpleGrid simpleGrid2) {
            int n;
            ArrayList arrayList = Lists.newArrayList();
            for (n = 0; n < simpleGrid.height; ++n) {
                for (int i = 0; i < simpleGrid.width; ++i) {
                    if (simpleGrid.get(i, n) != 2) continue;
                    arrayList.add(new Tuple<Integer, Integer>(i, n));
                }
            }
            Collections.shuffle(arrayList, this.random);
            n = 10;
            for (Tuple tuple : arrayList) {
                int n2;
                int n3 = (Integer)tuple.getA();
                if (simpleGrid2.get(n3, n2 = ((Integer)tuple.getB()).intValue()) != 0) continue;
                int n4 = n3;
                int n5 = n3;
                int n6 = n2;
                int n7 = n2;
                int n8 = 65536;
                if (simpleGrid2.get(n3 + 1, n2) == 0 && simpleGrid2.get(n3, n2 + 1) == 0 && simpleGrid2.get(n3 + 1, n2 + 1) == 0 && simpleGrid.get(n3 + 1, n2) == 2 && simpleGrid.get(n3, n2 + 1) == 2 && simpleGrid.get(n3 + 1, n2 + 1) == 2) {
                    ++n5;
                    ++n7;
                    n8 = 262144;
                } else if (simpleGrid2.get(n3 - 1, n2) == 0 && simpleGrid2.get(n3, n2 + 1) == 0 && simpleGrid2.get(n3 - 1, n2 + 1) == 0 && simpleGrid.get(n3 - 1, n2) == 2 && simpleGrid.get(n3, n2 + 1) == 2 && simpleGrid.get(n3 - 1, n2 + 1) == 2) {
                    --n4;
                    ++n7;
                    n8 = 262144;
                } else if (simpleGrid2.get(n3 - 1, n2) == 0 && simpleGrid2.get(n3, n2 - 1) == 0 && simpleGrid2.get(n3 - 1, n2 - 1) == 0 && simpleGrid.get(n3 - 1, n2) == 2 && simpleGrid.get(n3, n2 - 1) == 2 && simpleGrid.get(n3 - 1, n2 - 1) == 2) {
                    --n4;
                    --n6;
                    n8 = 262144;
                } else if (simpleGrid2.get(n3 + 1, n2) == 0 && simpleGrid.get(n3 + 1, n2) == 2) {
                    ++n5;
                    n8 = 131072;
                } else if (simpleGrid2.get(n3, n2 + 1) == 0 && simpleGrid.get(n3, n2 + 1) == 2) {
                    ++n7;
                    n8 = 131072;
                } else if (simpleGrid2.get(n3 - 1, n2) == 0 && simpleGrid.get(n3 - 1, n2) == 2) {
                    --n4;
                    n8 = 131072;
                } else if (simpleGrid2.get(n3, n2 - 1) == 0 && simpleGrid.get(n3, n2 - 1) == 2) {
                    --n6;
                    n8 = 131072;
                }
                int n9 = this.random.nextBoolean() ? n4 : n5;
                int n10 = this.random.nextBoolean() ? n6 : n7;
                int n11 = 2097152;
                if (!simpleGrid.edgesTo(n9, n10, 1)) {
                    n9 = n9 == n4 ? n5 : n4;
                    int n12 = n10 = n10 == n6 ? n7 : n6;
                    if (!simpleGrid.edgesTo(n9, n10, 1)) {
                        int n13 = n10 = n10 == n6 ? n7 : n6;
                        if (!simpleGrid.edgesTo(n9, n10, 1)) {
                            n9 = n9 == n4 ? n5 : n4;
                            int n14 = n10 = n10 == n6 ? n7 : n6;
                            if (!simpleGrid.edgesTo(n9, n10, 1)) {
                                n11 = 0;
                                n9 = n4;
                                n10 = n6;
                            }
                        }
                    }
                }
                for (int i = n6; i <= n7; ++i) {
                    for (int j = n4; j <= n5; ++j) {
                        if (j == n9 && i == n10) {
                            simpleGrid2.set(j, i, 0x100000 | n11 | n8 | n);
                            continue;
                        }
                        simpleGrid2.set(j, i, n8 | n);
                    }
                }
                ++n;
            }
        }
    }

    static class MansionPiecePlacer {
        private final StructureManager structureManager;
        private final Random random;
        private int startX;
        private int startY;

        public MansionPiecePlacer(StructureManager structureManager, Random random) {
            this.structureManager = structureManager;
            this.random = random;
        }

        public void createMansion(BlockPos blockPos, Rotation rotation, List<WoodlandMansionPiece> list, MansionGrid mansionGrid) {
            int n;
            PlacementData placementData = new PlacementData();
            placementData.position = blockPos;
            placementData.rotation = rotation;
            placementData.wallType = "wall_flat";
            PlacementData placementData2 = new PlacementData();
            this.entrance(list, placementData);
            placementData2.position = placementData.position.above(8);
            placementData2.rotation = placementData.rotation;
            placementData2.wallType = "wall_window";
            if (!list.isEmpty()) {
                // empty if block
            }
            SimpleGrid simpleGrid = mansionGrid.baseGrid;
            SimpleGrid simpleGrid2 = mansionGrid.thirdFloorGrid;
            this.startX = mansionGrid.entranceX + 1;
            this.startY = mansionGrid.entranceY + 1;
            int n2 = mansionGrid.entranceX + 1;
            int n3 = mansionGrid.entranceY;
            this.traverseOuterWalls(list, placementData, simpleGrid, Direction.SOUTH, this.startX, this.startY, n2, n3);
            this.traverseOuterWalls(list, placementData2, simpleGrid, Direction.SOUTH, this.startX, this.startY, n2, n3);
            PlacementData placementData3 = new PlacementData();
            placementData3.position = placementData.position.above(19);
            placementData3.rotation = placementData.rotation;
            placementData3.wallType = "wall_window";
            boolean bl = false;
            for (int i = 0; i < simpleGrid2.height && !bl; ++i) {
                for (n = SimpleGrid.access$600((SimpleGrid)simpleGrid2) - 1; n >= 0 && !bl; --n) {
                    if (!MansionGrid.isHouse(simpleGrid2, n, i)) continue;
                    placementData3.position = placementData3.position.relative(rotation.rotate(Direction.SOUTH), 8 + (i - this.startY) * 8);
                    placementData3.position = placementData3.position.relative(rotation.rotate(Direction.EAST), (n - this.startX) * 8);
                    this.traverseWallPiece(list, placementData3);
                    this.traverseOuterWalls(list, placementData3, simpleGrid2, Direction.SOUTH, n, i, n, i);
                    bl = true;
                }
            }
            this.createRoof(list, blockPos.above(16), rotation, simpleGrid, simpleGrid2);
            this.createRoof(list, blockPos.above(27), rotation, simpleGrid2, null);
            if (!list.isEmpty()) {
                // empty if block
            }
            FloorRoomCollection[] arrfloorRoomCollection = new FloorRoomCollection[]{new FirstFloorRoomCollection(), new SecondFloorRoomCollection(), new ThirdFloorRoomCollection()};
            for (n = 0; n < 3; ++n) {
                Object object;
                Object object2;
                BlockPos blockPos2 = blockPos.above(8 * n + (n == 2 ? 3 : 0));
                SimpleGrid simpleGrid3 = mansionGrid.floorRooms[n];
                SimpleGrid simpleGrid4 = n == 2 ? simpleGrid2 : simpleGrid;
                String string = n == 0 ? "carpet_south_1" : "carpet_south_2";
                String string2 = n == 0 ? "carpet_west_1" : "carpet_west_2";
                for (int i = 0; i < simpleGrid4.height; ++i) {
                    for (object2 = 0; object2 < simpleGrid4.width; ++object2) {
                        if (simpleGrid4.get((int)object2, i) != 1) continue;
                        object = blockPos2.relative(rotation.rotate(Direction.SOUTH), 8 + (i - this.startY) * 8);
                        object = ((BlockPos)object).relative(rotation.rotate(Direction.EAST), (object2 - this.startX) * 8);
                        list.add(new WoodlandMansionPiece(this.structureManager, "corridor_floor", (BlockPos)object, rotation));
                        if (simpleGrid4.get((int)object2, i - 1) == 1 || (simpleGrid3.get((int)object2, i - 1) & 0x800000) == 8388608) {
                            list.add(new WoodlandMansionPiece(this.structureManager, "carpet_north", ((BlockPos)object).relative(rotation.rotate(Direction.EAST), 1).above(), rotation));
                        }
                        if (simpleGrid4.get(object2 + 1, i) == 1 || (simpleGrid3.get(object2 + 1, i) & 0x800000) == 8388608) {
                            list.add(new WoodlandMansionPiece(this.structureManager, "carpet_east", ((BlockPos)object).relative(rotation.rotate(Direction.SOUTH), 1).relative(rotation.rotate(Direction.EAST), 5).above(), rotation));
                        }
                        if (simpleGrid4.get((int)object2, i + 1) == 1 || (simpleGrid3.get((int)object2, i + 1) & 0x800000) == 8388608) {
                            list.add(new WoodlandMansionPiece(this.structureManager, string, ((BlockPos)object).relative(rotation.rotate(Direction.SOUTH), 5).relative(rotation.rotate(Direction.WEST), 1), rotation));
                        }
                        if (simpleGrid4.get(object2 - 1, i) != 1 && (simpleGrid3.get(object2 - 1, i) & 0x800000) != 8388608) continue;
                        list.add(new WoodlandMansionPiece(this.structureManager, string2, ((BlockPos)object).relative(rotation.rotate(Direction.WEST), 1).relative(rotation.rotate(Direction.NORTH), 1), rotation));
                    }
                }
                String string3 = n == 0 ? "indoors_wall_1" : "indoors_wall_2";
                object2 = n == 0 ? "indoors_door_1" : "indoors_door_2";
                object = Lists.newArrayList();
                for (int i = 0; i < simpleGrid4.height; ++i) {
                    for (int j = 0; j < simpleGrid4.width; ++j) {
                        Object object32;
                        boolean bl2;
                        Object object4;
                        boolean bl3 = bl2 = n == 2 && simpleGrid4.get(j, i) == 3;
                        if (simpleGrid4.get(j, i) != 2 && !bl2) continue;
                        int n4 = simpleGrid3.get(j, i);
                        int n5 = n4 & 0xF0000;
                        int n6 = n4 & 0xFFFF;
                        bl2 = bl2 && (n4 & 0x800000) == 8388608;
                        object.clear();
                        if ((n4 & 0x200000) == 2097152) {
                            for (Object object32 : Direction.Plane.HORIZONTAL) {
                                if (simpleGrid4.get(j + ((Direction)object32).getStepX(), i + ((Direction)object32).getStepZ()) != 1) continue;
                                object.add(object32);
                            }
                        }
                        Object object5 = null;
                        if (!object.isEmpty()) {
                            object5 = (Direction)object.get(this.random.nextInt(object.size()));
                        } else if ((n4 & 0x100000) == 1048576) {
                            object5 = Direction.UP;
                        }
                        object32 = blockPos2.relative(rotation.rotate(Direction.SOUTH), 8 + (i - this.startY) * 8);
                        object32 = ((BlockPos)object32).relative(rotation.rotate(Direction.EAST), -1 + (j - this.startX) * 8);
                        if (MansionGrid.isHouse(simpleGrid4, j - 1, i) && !mansionGrid.isRoomId(simpleGrid4, j - 1, i, n, n6)) {
                            list.add(new WoodlandMansionPiece(this.structureManager, (String)(object5 == Direction.WEST ? object2 : (int)string3), (BlockPos)object32, rotation));
                        }
                        if (simpleGrid4.get(j + 1, i) == 1 && !bl2) {
                            object4 = ((BlockPos)object32).relative(rotation.rotate(Direction.EAST), 8);
                            list.add(new WoodlandMansionPiece(this.structureManager, (String)(object5 == Direction.EAST ? object2 : (int)string3), (BlockPos)object4, rotation));
                        }
                        if (MansionGrid.isHouse(simpleGrid4, j, i + 1) && !mansionGrid.isRoomId(simpleGrid4, j, i + 1, n, n6)) {
                            object4 = ((BlockPos)object32).relative(rotation.rotate(Direction.SOUTH), 7);
                            object4 = ((BlockPos)object4).relative(rotation.rotate(Direction.EAST), 7);
                            list.add(new WoodlandMansionPiece(this.structureManager, (String)(object5 == Direction.SOUTH ? object2 : (int)string3), (BlockPos)object4, rotation.getRotated(Rotation.CLOCKWISE_90)));
                        }
                        if (simpleGrid4.get(j, i - 1) == 1 && !bl2) {
                            object4 = ((BlockPos)object32).relative(rotation.rotate(Direction.NORTH), 1);
                            object4 = ((BlockPos)object4).relative(rotation.rotate(Direction.EAST), 7);
                            list.add(new WoodlandMansionPiece(this.structureManager, (String)(object5 == Direction.NORTH ? object2 : (int)string3), (BlockPos)object4, rotation.getRotated(Rotation.CLOCKWISE_90)));
                        }
                        if (n5 == 65536) {
                            this.addRoom1x1(list, (BlockPos)object32, rotation, (Direction)object5, arrfloorRoomCollection[n]);
                            continue;
                        }
                        if (n5 == 131072 && object5 != null) {
                            object4 = mansionGrid.get1x2RoomDirection(simpleGrid4, j, i, n, n6);
                            boolean bl4 = (n4 & 0x400000) == 4194304;
                            this.addRoom1x2(list, (BlockPos)object32, rotation, (Direction)object4, (Direction)object5, arrfloorRoomCollection[n], bl4);
                            continue;
                        }
                        if (n5 == 262144 && object5 != null && object5 != Direction.UP) {
                            object4 = ((Direction)object5).getClockWise();
                            if (!mansionGrid.isRoomId(simpleGrid4, j + ((Direction)object4).getStepX(), i + ((Direction)object4).getStepZ(), n, n6)) {
                                object4 = ((Direction)object4).getOpposite();
                            }
                            this.addRoom2x2(list, (BlockPos)object32, rotation, (Direction)object4, (Direction)object5, arrfloorRoomCollection[n]);
                            continue;
                        }
                        if (n5 != 262144 || object5 != Direction.UP) continue;
                        this.addRoom2x2Secret(list, (BlockPos)object32, rotation, arrfloorRoomCollection[n]);
                    }
                }
            }
        }

        private void traverseOuterWalls(List<WoodlandMansionPiece> list, PlacementData placementData, SimpleGrid simpleGrid, Direction direction, int n, int n2, int n3, int n4) {
            int n5 = n;
            int n6 = n2;
            Direction direction2 = direction;
            do {
                if (!MansionGrid.isHouse(simpleGrid, n5 + direction.getStepX(), n6 + direction.getStepZ())) {
                    this.traverseTurn(list, placementData);
                    direction = direction.getClockWise();
                    if (n5 == n3 && n6 == n4 && direction2 == direction) continue;
                    this.traverseWallPiece(list, placementData);
                    continue;
                }
                if (MansionGrid.isHouse(simpleGrid, n5 + direction.getStepX(), n6 + direction.getStepZ()) && MansionGrid.isHouse(simpleGrid, n5 + direction.getStepX() + direction.getCounterClockWise().getStepX(), n6 + direction.getStepZ() + direction.getCounterClockWise().getStepZ())) {
                    this.traverseInnerTurn(list, placementData);
                    n5 += direction.getStepX();
                    n6 += direction.getStepZ();
                    direction = direction.getCounterClockWise();
                    continue;
                }
                if ((n5 += direction.getStepX()) == n3 && (n6 += direction.getStepZ()) == n4 && direction2 == direction) continue;
                this.traverseWallPiece(list, placementData);
            } while (n5 != n3 || n6 != n4 || direction2 != direction);
        }

        private void createRoof(List<WoodlandMansionPiece> list, BlockPos blockPos, Rotation rotation, SimpleGrid simpleGrid, @Nullable SimpleGrid simpleGrid2) {
            int n;
            BlockPos blockPos2;
            BlockPos blockPos3;
            int n2;
            boolean bl;
            for (n = 0; n < simpleGrid.height; ++n) {
                for (n2 = 0; n2 < simpleGrid.width; ++n2) {
                    blockPos2 = blockPos;
                    blockPos2 = blockPos2.relative(rotation.rotate(Direction.SOUTH), 8 + (n - this.startY) * 8);
                    blockPos2 = blockPos2.relative(rotation.rotate(Direction.EAST), (n2 - this.startX) * 8);
                    boolean bl2 = bl = simpleGrid2 != null && MansionGrid.isHouse(simpleGrid2, n2, n);
                    if (!MansionGrid.isHouse(simpleGrid, n2, n) || bl) continue;
                    list.add(new WoodlandMansionPiece(this.structureManager, "roof", blockPos2.above(3), rotation));
                    if (!MansionGrid.isHouse(simpleGrid, n2 + 1, n)) {
                        blockPos3 = blockPos2.relative(rotation.rotate(Direction.EAST), 6);
                        list.add(new WoodlandMansionPiece(this.structureManager, "roof_front", blockPos3, rotation));
                    }
                    if (!MansionGrid.isHouse(simpleGrid, n2 - 1, n)) {
                        blockPos3 = blockPos2.relative(rotation.rotate(Direction.EAST), 0);
                        blockPos3 = blockPos3.relative(rotation.rotate(Direction.SOUTH), 7);
                        list.add(new WoodlandMansionPiece(this.structureManager, "roof_front", blockPos3, rotation.getRotated(Rotation.CLOCKWISE_180)));
                    }
                    if (!MansionGrid.isHouse(simpleGrid, n2, n - 1)) {
                        blockPos3 = blockPos2.relative(rotation.rotate(Direction.WEST), 1);
                        list.add(new WoodlandMansionPiece(this.structureManager, "roof_front", blockPos3, rotation.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                    }
                    if (MansionGrid.isHouse(simpleGrid, n2, n + 1)) continue;
                    blockPos3 = blockPos2.relative(rotation.rotate(Direction.EAST), 6);
                    blockPos3 = blockPos3.relative(rotation.rotate(Direction.SOUTH), 6);
                    list.add(new WoodlandMansionPiece(this.structureManager, "roof_front", blockPos3, rotation.getRotated(Rotation.CLOCKWISE_90)));
                }
            }
            if (simpleGrid2 != null) {
                for (n = 0; n < simpleGrid.height; ++n) {
                    for (n2 = 0; n2 < simpleGrid.width; ++n2) {
                        blockPos2 = blockPos;
                        blockPos2 = blockPos2.relative(rotation.rotate(Direction.SOUTH), 8 + (n - this.startY) * 8);
                        blockPos2 = blockPos2.relative(rotation.rotate(Direction.EAST), (n2 - this.startX) * 8);
                        bl = MansionGrid.isHouse(simpleGrid2, n2, n);
                        if (!MansionGrid.isHouse(simpleGrid, n2, n) || !bl) continue;
                        if (!MansionGrid.isHouse(simpleGrid, n2 + 1, n)) {
                            blockPos3 = blockPos2.relative(rotation.rotate(Direction.EAST), 7);
                            list.add(new WoodlandMansionPiece(this.structureManager, "small_wall", blockPos3, rotation));
                        }
                        if (!MansionGrid.isHouse(simpleGrid, n2 - 1, n)) {
                            blockPos3 = blockPos2.relative(rotation.rotate(Direction.WEST), 1);
                            blockPos3 = blockPos3.relative(rotation.rotate(Direction.SOUTH), 6);
                            list.add(new WoodlandMansionPiece(this.structureManager, "small_wall", blockPos3, rotation.getRotated(Rotation.CLOCKWISE_180)));
                        }
                        if (!MansionGrid.isHouse(simpleGrid, n2, n - 1)) {
                            blockPos3 = blockPos2.relative(rotation.rotate(Direction.WEST), 0);
                            blockPos3 = blockPos3.relative(rotation.rotate(Direction.NORTH), 1);
                            list.add(new WoodlandMansionPiece(this.structureManager, "small_wall", blockPos3, rotation.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                        }
                        if (!MansionGrid.isHouse(simpleGrid, n2, n + 1)) {
                            blockPos3 = blockPos2.relative(rotation.rotate(Direction.EAST), 6);
                            blockPos3 = blockPos3.relative(rotation.rotate(Direction.SOUTH), 7);
                            list.add(new WoodlandMansionPiece(this.structureManager, "small_wall", blockPos3, rotation.getRotated(Rotation.CLOCKWISE_90)));
                        }
                        if (!MansionGrid.isHouse(simpleGrid, n2 + 1, n)) {
                            if (!MansionGrid.isHouse(simpleGrid, n2, n - 1)) {
                                blockPos3 = blockPos2.relative(rotation.rotate(Direction.EAST), 7);
                                blockPos3 = blockPos3.relative(rotation.rotate(Direction.NORTH), 2);
                                list.add(new WoodlandMansionPiece(this.structureManager, "small_wall_corner", blockPos3, rotation));
                            }
                            if (!MansionGrid.isHouse(simpleGrid, n2, n + 1)) {
                                blockPos3 = blockPos2.relative(rotation.rotate(Direction.EAST), 8);
                                blockPos3 = blockPos3.relative(rotation.rotate(Direction.SOUTH), 7);
                                list.add(new WoodlandMansionPiece(this.structureManager, "small_wall_corner", blockPos3, rotation.getRotated(Rotation.CLOCKWISE_90)));
                            }
                        }
                        if (MansionGrid.isHouse(simpleGrid, n2 - 1, n)) continue;
                        if (!MansionGrid.isHouse(simpleGrid, n2, n - 1)) {
                            blockPos3 = blockPos2.relative(rotation.rotate(Direction.WEST), 2);
                            blockPos3 = blockPos3.relative(rotation.rotate(Direction.NORTH), 1);
                            list.add(new WoodlandMansionPiece(this.structureManager, "small_wall_corner", blockPos3, rotation.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                        }
                        if (MansionGrid.isHouse(simpleGrid, n2, n + 1)) continue;
                        blockPos3 = blockPos2.relative(rotation.rotate(Direction.WEST), 1);
                        blockPos3 = blockPos3.relative(rotation.rotate(Direction.SOUTH), 8);
                        list.add(new WoodlandMansionPiece(this.structureManager, "small_wall_corner", blockPos3, rotation.getRotated(Rotation.CLOCKWISE_180)));
                    }
                }
            }
            for (n = 0; n < simpleGrid.height; ++n) {
                for (n2 = 0; n2 < simpleGrid.width; ++n2) {
                    BlockPos blockPos4;
                    blockPos2 = blockPos;
                    blockPos2 = blockPos2.relative(rotation.rotate(Direction.SOUTH), 8 + (n - this.startY) * 8);
                    blockPos2 = blockPos2.relative(rotation.rotate(Direction.EAST), (n2 - this.startX) * 8);
                    boolean bl3 = bl = simpleGrid2 != null && MansionGrid.isHouse(simpleGrid2, n2, n);
                    if (!MansionGrid.isHouse(simpleGrid, n2, n) || bl) continue;
                    if (!MansionGrid.isHouse(simpleGrid, n2 + 1, n)) {
                        blockPos3 = blockPos2.relative(rotation.rotate(Direction.EAST), 6);
                        if (!MansionGrid.isHouse(simpleGrid, n2, n + 1)) {
                            blockPos4 = blockPos3.relative(rotation.rotate(Direction.SOUTH), 6);
                            list.add(new WoodlandMansionPiece(this.structureManager, "roof_corner", blockPos4, rotation));
                        } else if (MansionGrid.isHouse(simpleGrid, n2 + 1, n + 1)) {
                            blockPos4 = blockPos3.relative(rotation.rotate(Direction.SOUTH), 5);
                            list.add(new WoodlandMansionPiece(this.structureManager, "roof_inner_corner", blockPos4, rotation));
                        }
                        if (!MansionGrid.isHouse(simpleGrid, n2, n - 1)) {
                            list.add(new WoodlandMansionPiece(this.structureManager, "roof_corner", blockPos3, rotation.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                        } else if (MansionGrid.isHouse(simpleGrid, n2 + 1, n - 1)) {
                            blockPos4 = blockPos2.relative(rotation.rotate(Direction.EAST), 9);
                            blockPos4 = blockPos4.relative(rotation.rotate(Direction.NORTH), 2);
                            list.add(new WoodlandMansionPiece(this.structureManager, "roof_inner_corner", blockPos4, rotation.getRotated(Rotation.CLOCKWISE_90)));
                        }
                    }
                    if (MansionGrid.isHouse(simpleGrid, n2 - 1, n)) continue;
                    blockPos3 = blockPos2.relative(rotation.rotate(Direction.EAST), 0);
                    blockPos3 = blockPos3.relative(rotation.rotate(Direction.SOUTH), 0);
                    if (!MansionGrid.isHouse(simpleGrid, n2, n + 1)) {
                        blockPos4 = blockPos3.relative(rotation.rotate(Direction.SOUTH), 6);
                        list.add(new WoodlandMansionPiece(this.structureManager, "roof_corner", blockPos4, rotation.getRotated(Rotation.CLOCKWISE_90)));
                    } else if (MansionGrid.isHouse(simpleGrid, n2 - 1, n + 1)) {
                        blockPos4 = blockPos3.relative(rotation.rotate(Direction.SOUTH), 8);
                        blockPos4 = blockPos4.relative(rotation.rotate(Direction.WEST), 3);
                        list.add(new WoodlandMansionPiece(this.structureManager, "roof_inner_corner", blockPos4, rotation.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                    }
                    if (!MansionGrid.isHouse(simpleGrid, n2, n - 1)) {
                        list.add(new WoodlandMansionPiece(this.structureManager, "roof_corner", blockPos3, rotation.getRotated(Rotation.CLOCKWISE_180)));
                        continue;
                    }
                    if (!MansionGrid.isHouse(simpleGrid, n2 - 1, n - 1)) continue;
                    blockPos4 = blockPos3.relative(rotation.rotate(Direction.SOUTH), 1);
                    list.add(new WoodlandMansionPiece(this.structureManager, "roof_inner_corner", blockPos4, rotation.getRotated(Rotation.CLOCKWISE_180)));
                }
            }
        }

        private void entrance(List<WoodlandMansionPiece> list, PlacementData placementData) {
            Direction direction = placementData.rotation.rotate(Direction.WEST);
            list.add(new WoodlandMansionPiece(this.structureManager, "entrance", placementData.position.relative(direction, 9), placementData.rotation));
            placementData.position = placementData.position.relative(placementData.rotation.rotate(Direction.SOUTH), 16);
        }

        private void traverseWallPiece(List<WoodlandMansionPiece> list, PlacementData placementData) {
            list.add(new WoodlandMansionPiece(this.structureManager, placementData.wallType, placementData.position.relative(placementData.rotation.rotate(Direction.EAST), 7), placementData.rotation));
            placementData.position = placementData.position.relative(placementData.rotation.rotate(Direction.SOUTH), 8);
        }

        private void traverseTurn(List<WoodlandMansionPiece> list, PlacementData placementData) {
            placementData.position = placementData.position.relative(placementData.rotation.rotate(Direction.SOUTH), -1);
            list.add(new WoodlandMansionPiece(this.structureManager, "wall_corner", placementData.position, placementData.rotation));
            placementData.position = placementData.position.relative(placementData.rotation.rotate(Direction.SOUTH), -7);
            placementData.position = placementData.position.relative(placementData.rotation.rotate(Direction.WEST), -6);
            placementData.rotation = placementData.rotation.getRotated(Rotation.CLOCKWISE_90);
        }

        private void traverseInnerTurn(List<WoodlandMansionPiece> list, PlacementData placementData) {
            placementData.position = placementData.position.relative(placementData.rotation.rotate(Direction.SOUTH), 6);
            placementData.position = placementData.position.relative(placementData.rotation.rotate(Direction.EAST), 8);
            placementData.rotation = placementData.rotation.getRotated(Rotation.COUNTERCLOCKWISE_90);
        }

        private void addRoom1x1(List<WoodlandMansionPiece> list, BlockPos blockPos, Rotation rotation, Direction direction, FloorRoomCollection floorRoomCollection) {
            Rotation rotation2 = Rotation.NONE;
            String string = floorRoomCollection.get1x1(this.random);
            if (direction != Direction.EAST) {
                if (direction == Direction.NORTH) {
                    rotation2 = rotation2.getRotated(Rotation.COUNTERCLOCKWISE_90);
                } else if (direction == Direction.WEST) {
                    rotation2 = rotation2.getRotated(Rotation.CLOCKWISE_180);
                } else if (direction == Direction.SOUTH) {
                    rotation2 = rotation2.getRotated(Rotation.CLOCKWISE_90);
                } else {
                    string = floorRoomCollection.get1x1Secret(this.random);
                }
            }
            BlockPos blockPos2 = StructureTemplate.getZeroPositionWithTransform(new BlockPos(1, 0, 0), Mirror.NONE, rotation2, 7, 7);
            rotation2 = rotation2.getRotated(rotation);
            blockPos2 = blockPos2.rotate(rotation);
            BlockPos blockPos3 = blockPos.offset(blockPos2.getX(), 0, blockPos2.getZ());
            list.add(new WoodlandMansionPiece(this.structureManager, string, blockPos3, rotation2));
        }

        private void addRoom1x2(List<WoodlandMansionPiece> list, BlockPos blockPos, Rotation rotation, Direction direction, Direction direction2, FloorRoomCollection floorRoomCollection, boolean bl) {
            if (direction2 == Direction.EAST && direction == Direction.SOUTH) {
                BlockPos blockPos2 = blockPos.relative(rotation.rotate(Direction.EAST), 1);
                list.add(new WoodlandMansionPiece(this.structureManager, floorRoomCollection.get1x2SideEntrance(this.random, bl), blockPos2, rotation));
            } else if (direction2 == Direction.EAST && direction == Direction.NORTH) {
                BlockPos blockPos3 = blockPos.relative(rotation.rotate(Direction.EAST), 1);
                blockPos3 = blockPos3.relative(rotation.rotate(Direction.SOUTH), 6);
                list.add(new WoodlandMansionPiece(this.structureManager, floorRoomCollection.get1x2SideEntrance(this.random, bl), blockPos3, rotation, Mirror.LEFT_RIGHT));
            } else if (direction2 == Direction.WEST && direction == Direction.NORTH) {
                BlockPos blockPos4 = blockPos.relative(rotation.rotate(Direction.EAST), 7);
                blockPos4 = blockPos4.relative(rotation.rotate(Direction.SOUTH), 6);
                list.add(new WoodlandMansionPiece(this.structureManager, floorRoomCollection.get1x2SideEntrance(this.random, bl), blockPos4, rotation.getRotated(Rotation.CLOCKWISE_180)));
            } else if (direction2 == Direction.WEST && direction == Direction.SOUTH) {
                BlockPos blockPos5 = blockPos.relative(rotation.rotate(Direction.EAST), 7);
                list.add(new WoodlandMansionPiece(this.structureManager, floorRoomCollection.get1x2SideEntrance(this.random, bl), blockPos5, rotation, Mirror.FRONT_BACK));
            } else if (direction2 == Direction.SOUTH && direction == Direction.EAST) {
                BlockPos blockPos6 = blockPos.relative(rotation.rotate(Direction.EAST), 1);
                list.add(new WoodlandMansionPiece(this.structureManager, floorRoomCollection.get1x2SideEntrance(this.random, bl), blockPos6, rotation.getRotated(Rotation.CLOCKWISE_90), Mirror.LEFT_RIGHT));
            } else if (direction2 == Direction.SOUTH && direction == Direction.WEST) {
                BlockPos blockPos7 = blockPos.relative(rotation.rotate(Direction.EAST), 7);
                list.add(new WoodlandMansionPiece(this.structureManager, floorRoomCollection.get1x2SideEntrance(this.random, bl), blockPos7, rotation.getRotated(Rotation.CLOCKWISE_90)));
            } else if (direction2 == Direction.NORTH && direction == Direction.WEST) {
                BlockPos blockPos8 = blockPos.relative(rotation.rotate(Direction.EAST), 7);
                blockPos8 = blockPos8.relative(rotation.rotate(Direction.SOUTH), 6);
                list.add(new WoodlandMansionPiece(this.structureManager, floorRoomCollection.get1x2SideEntrance(this.random, bl), blockPos8, rotation.getRotated(Rotation.CLOCKWISE_90), Mirror.FRONT_BACK));
            } else if (direction2 == Direction.NORTH && direction == Direction.EAST) {
                BlockPos blockPos9 = blockPos.relative(rotation.rotate(Direction.EAST), 1);
                blockPos9 = blockPos9.relative(rotation.rotate(Direction.SOUTH), 6);
                list.add(new WoodlandMansionPiece(this.structureManager, floorRoomCollection.get1x2SideEntrance(this.random, bl), blockPos9, rotation.getRotated(Rotation.COUNTERCLOCKWISE_90)));
            } else if (direction2 == Direction.SOUTH && direction == Direction.NORTH) {
                BlockPos blockPos10 = blockPos.relative(rotation.rotate(Direction.EAST), 1);
                blockPos10 = blockPos10.relative(rotation.rotate(Direction.NORTH), 8);
                list.add(new WoodlandMansionPiece(this.structureManager, floorRoomCollection.get1x2FrontEntrance(this.random, bl), blockPos10, rotation));
            } else if (direction2 == Direction.NORTH && direction == Direction.SOUTH) {
                BlockPos blockPos11 = blockPos.relative(rotation.rotate(Direction.EAST), 7);
                blockPos11 = blockPos11.relative(rotation.rotate(Direction.SOUTH), 14);
                list.add(new WoodlandMansionPiece(this.structureManager, floorRoomCollection.get1x2FrontEntrance(this.random, bl), blockPos11, rotation.getRotated(Rotation.CLOCKWISE_180)));
            } else if (direction2 == Direction.WEST && direction == Direction.EAST) {
                BlockPos blockPos12 = blockPos.relative(rotation.rotate(Direction.EAST), 15);
                list.add(new WoodlandMansionPiece(this.structureManager, floorRoomCollection.get1x2FrontEntrance(this.random, bl), blockPos12, rotation.getRotated(Rotation.CLOCKWISE_90)));
            } else if (direction2 == Direction.EAST && direction == Direction.WEST) {
                BlockPos blockPos13 = blockPos.relative(rotation.rotate(Direction.WEST), 7);
                blockPos13 = blockPos13.relative(rotation.rotate(Direction.SOUTH), 6);
                list.add(new WoodlandMansionPiece(this.structureManager, floorRoomCollection.get1x2FrontEntrance(this.random, bl), blockPos13, rotation.getRotated(Rotation.COUNTERCLOCKWISE_90)));
            } else if (direction2 == Direction.UP && direction == Direction.EAST) {
                BlockPos blockPos14 = blockPos.relative(rotation.rotate(Direction.EAST), 15);
                list.add(new WoodlandMansionPiece(this.structureManager, floorRoomCollection.get1x2Secret(this.random), blockPos14, rotation.getRotated(Rotation.CLOCKWISE_90)));
            } else if (direction2 == Direction.UP && direction == Direction.SOUTH) {
                BlockPos blockPos15 = blockPos.relative(rotation.rotate(Direction.EAST), 1);
                blockPos15 = blockPos15.relative(rotation.rotate(Direction.NORTH), 0);
                list.add(new WoodlandMansionPiece(this.structureManager, floorRoomCollection.get1x2Secret(this.random), blockPos15, rotation));
            }
        }

        private void addRoom2x2(List<WoodlandMansionPiece> list, BlockPos blockPos, Rotation rotation, Direction direction, Direction direction2, FloorRoomCollection floorRoomCollection) {
            int n = 0;
            int n2 = 0;
            Rotation rotation2 = rotation;
            Mirror mirror = Mirror.NONE;
            if (direction2 == Direction.EAST && direction == Direction.SOUTH) {
                n = -7;
            } else if (direction2 == Direction.EAST && direction == Direction.NORTH) {
                n = -7;
                n2 = 6;
                mirror = Mirror.LEFT_RIGHT;
            } else if (direction2 == Direction.NORTH && direction == Direction.EAST) {
                n = 1;
                n2 = 14;
                rotation2 = rotation.getRotated(Rotation.COUNTERCLOCKWISE_90);
            } else if (direction2 == Direction.NORTH && direction == Direction.WEST) {
                n = 7;
                n2 = 14;
                rotation2 = rotation.getRotated(Rotation.COUNTERCLOCKWISE_90);
                mirror = Mirror.LEFT_RIGHT;
            } else if (direction2 == Direction.SOUTH && direction == Direction.WEST) {
                n = 7;
                n2 = -8;
                rotation2 = rotation.getRotated(Rotation.CLOCKWISE_90);
            } else if (direction2 == Direction.SOUTH && direction == Direction.EAST) {
                n = 1;
                n2 = -8;
                rotation2 = rotation.getRotated(Rotation.CLOCKWISE_90);
                mirror = Mirror.LEFT_RIGHT;
            } else if (direction2 == Direction.WEST && direction == Direction.NORTH) {
                n = 15;
                n2 = 6;
                rotation2 = rotation.getRotated(Rotation.CLOCKWISE_180);
            } else if (direction2 == Direction.WEST && direction == Direction.SOUTH) {
                n = 15;
                mirror = Mirror.FRONT_BACK;
            }
            BlockPos blockPos2 = blockPos.relative(rotation.rotate(Direction.EAST), n);
            blockPos2 = blockPos2.relative(rotation.rotate(Direction.SOUTH), n2);
            list.add(new WoodlandMansionPiece(this.structureManager, floorRoomCollection.get2x2(this.random), blockPos2, rotation2, mirror));
        }

        private void addRoom2x2Secret(List<WoodlandMansionPiece> list, BlockPos blockPos, Rotation rotation, FloorRoomCollection floorRoomCollection) {
            BlockPos blockPos2 = blockPos.relative(rotation.rotate(Direction.EAST), 1);
            list.add(new WoodlandMansionPiece(this.structureManager, floorRoomCollection.get2x2Secret(this.random), blockPos2, rotation, Mirror.NONE));
        }
    }

    static class PlacementData {
        public Rotation rotation;
        public BlockPos position;
        public String wallType;

        private PlacementData() {
        }
    }

    public static class WoodlandMansionPiece
    extends TemplateStructurePiece {
        private final String templateName;
        private final Rotation rotation;
        private final Mirror mirror;

        public WoodlandMansionPiece(StructureManager structureManager, String string, BlockPos blockPos, Rotation rotation) {
            this(structureManager, string, blockPos, rotation, Mirror.NONE);
        }

        public WoodlandMansionPiece(StructureManager structureManager, String string, BlockPos blockPos, Rotation rotation, Mirror mirror) {
            super(StructurePieceType.WOODLAND_MANSION_PIECE, 0);
            this.templateName = string;
            this.templatePosition = blockPos;
            this.rotation = rotation;
            this.mirror = mirror;
            this.loadTemplate(structureManager);
        }

        public WoodlandMansionPiece(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.WOODLAND_MANSION_PIECE, compoundTag);
            this.templateName = compoundTag.getString("Template");
            this.rotation = Rotation.valueOf(compoundTag.getString("Rot"));
            this.mirror = Mirror.valueOf(compoundTag.getString("Mi"));
            this.loadTemplate(structureManager);
        }

        private void loadTemplate(StructureManager structureManager) {
            StructureTemplate structureTemplate = structureManager.getOrCreate(new ResourceLocation("woodland_mansion/" + this.templateName));
            StructurePlaceSettings structurePlaceSettings = new StructurePlaceSettings().setIgnoreEntities(true).setRotation(this.rotation).setMirror(this.mirror).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
            this.setup(structureTemplate, this.templatePosition, structurePlaceSettings);
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
            super.addAdditionalSaveData(compoundTag);
            compoundTag.putString("Template", this.templateName);
            compoundTag.putString("Rot", this.placeSettings.getRotation().name());
            compoundTag.putString("Mi", this.placeSettings.getMirror().name());
        }

        @Override
        protected void handleDataMarker(String string, BlockPos blockPos, ServerLevelAccessor serverLevelAccessor, Random random, BoundingBox boundingBox) {
            if (string.startsWith("Chest")) {
                Rotation rotation = this.placeSettings.getRotation();
                BlockState blockState = Blocks.CHEST.defaultBlockState();
                if ("ChestWest".equals(string)) {
                    blockState = (BlockState)blockState.setValue(ChestBlock.FACING, rotation.rotate(Direction.WEST));
                } else if ("ChestEast".equals(string)) {
                    blockState = (BlockState)blockState.setValue(ChestBlock.FACING, rotation.rotate(Direction.EAST));
                } else if ("ChestSouth".equals(string)) {
                    blockState = (BlockState)blockState.setValue(ChestBlock.FACING, rotation.rotate(Direction.SOUTH));
                } else if ("ChestNorth".equals(string)) {
                    blockState = (BlockState)blockState.setValue(ChestBlock.FACING, rotation.rotate(Direction.NORTH));
                }
                this.createChest(serverLevelAccessor, boundingBox, random, blockPos, BuiltInLootTables.WOODLAND_MANSION, blockState);
            } else {
                AbstractIllager abstractIllager;
                switch (string) {
                    case "Mage": {
                        abstractIllager = EntityType.EVOKER.create(serverLevelAccessor.getLevel());
                        break;
                    }
                    case "Warrior": {
                        abstractIllager = EntityType.VINDICATOR.create(serverLevelAccessor.getLevel());
                        break;
                    }
                    default: {
                        return;
                    }
                }
                abstractIllager.setPersistenceRequired();
                abstractIllager.moveTo(blockPos, 0.0f, 0.0f);
                abstractIllager.finalizeSpawn(serverLevelAccessor, serverLevelAccessor.getCurrentDifficultyAt(abstractIllager.blockPosition()), MobSpawnType.STRUCTURE, null, null);
                serverLevelAccessor.addFreshEntityWithPassengers(abstractIllager);
                serverLevelAccessor.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 2);
            }
        }
    }

}

