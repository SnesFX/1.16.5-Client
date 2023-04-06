/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class EndCityPieces {
    private static final StructurePlaceSettings OVERWRITE = new StructurePlaceSettings().setIgnoreEntities(true).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
    private static final StructurePlaceSettings INSERT = new StructurePlaceSettings().setIgnoreEntities(true).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
    private static final SectionGenerator HOUSE_TOWER_GENERATOR = new SectionGenerator(){

        @Override
        public void init() {
        }

        @Override
        public boolean generate(StructureManager structureManager, int n, EndCityPiece endCityPiece, BlockPos blockPos, List<StructurePiece> list, Random random) {
            if (n > 8) {
                return false;
            }
            Rotation rotation = endCityPiece.placeSettings.getRotation();
            EndCityPiece endCityPiece2 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece, blockPos, "base_floor", rotation, true));
            int n2 = random.nextInt(3);
            if (n2 == 0) {
                endCityPiece2 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece2, new BlockPos(-1, 4, -1), "base_roof", rotation, true));
            } else if (n2 == 1) {
                endCityPiece2 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece2, new BlockPos(-1, 0, -1), "second_floor_2", rotation, false));
                endCityPiece2 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece2, new BlockPos(-1, 8, -1), "second_roof", rotation, false));
                EndCityPieces.recursiveChildren(structureManager, TOWER_GENERATOR, n + 1, endCityPiece2, null, list, random);
            } else if (n2 == 2) {
                endCityPiece2 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece2, new BlockPos(-1, 0, -1), "second_floor_2", rotation, false));
                endCityPiece2 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece2, new BlockPos(-1, 4, -1), "third_floor_2", rotation, false));
                endCityPiece2 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece2, new BlockPos(-1, 8, -1), "third_roof", rotation, true));
                EndCityPieces.recursiveChildren(structureManager, TOWER_GENERATOR, n + 1, endCityPiece2, null, list, random);
            }
            return true;
        }
    };
    private static final List<Tuple<Rotation, BlockPos>> TOWER_BRIDGES = Lists.newArrayList((Object[])new Tuple[]{new Tuple<Rotation, BlockPos>(Rotation.NONE, new BlockPos(1, -1, 0)), new Tuple<Rotation, BlockPos>(Rotation.CLOCKWISE_90, new BlockPos(6, -1, 1)), new Tuple<Rotation, BlockPos>(Rotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 5)), new Tuple<Rotation, BlockPos>(Rotation.CLOCKWISE_180, new BlockPos(5, -1, 6))});
    private static final SectionGenerator TOWER_GENERATOR = new SectionGenerator(){

        @Override
        public void init() {
        }

        @Override
        public boolean generate(StructureManager structureManager, int n, EndCityPiece endCityPiece, BlockPos blockPos, List<StructurePiece> list, Random random) {
            Rotation rotation = endCityPiece.placeSettings.getRotation();
            EndCityPiece endCityPiece2 = endCityPiece;
            endCityPiece2 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece2, new BlockPos(3 + random.nextInt(2), -3, 3 + random.nextInt(2)), "tower_base", rotation, true));
            endCityPiece2 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece2, new BlockPos(0, 7, 0), "tower_piece", rotation, true));
            EndCityPiece endCityPiece3 = random.nextInt(3) == 0 ? endCityPiece2 : null;
            int n2 = 1 + random.nextInt(3);
            for (int i = 0; i < n2; ++i) {
                endCityPiece2 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece2, new BlockPos(0, 4, 0), "tower_piece", rotation, true));
                if (i >= n2 - 1 || !random.nextBoolean()) continue;
                endCityPiece3 = endCityPiece2;
            }
            if (endCityPiece3 != null) {
                for (Tuple tuple : TOWER_BRIDGES) {
                    if (!random.nextBoolean()) continue;
                    EndCityPiece endCityPiece4 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece3, (BlockPos)tuple.getB(), "bridge_end", rotation.getRotated((Rotation)((Object)tuple.getA())), true));
                    EndCityPieces.recursiveChildren(structureManager, TOWER_BRIDGE_GENERATOR, n + 1, endCityPiece4, null, list, random);
                }
                endCityPiece2 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece2, new BlockPos(-1, 4, -1), "tower_top", rotation, true));
            } else if (n == 7) {
                endCityPiece2 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece2, new BlockPos(-1, 4, -1), "tower_top", rotation, true));
            } else {
                return EndCityPieces.recursiveChildren(structureManager, FAT_TOWER_GENERATOR, n + 1, endCityPiece2, null, list, random);
            }
            return true;
        }
    };
    private static final SectionGenerator TOWER_BRIDGE_GENERATOR = new SectionGenerator(){
        public boolean shipCreated;

        @Override
        public void init() {
            this.shipCreated = false;
        }

        @Override
        public boolean generate(StructureManager structureManager, int n, EndCityPiece endCityPiece, BlockPos blockPos, List<StructurePiece> list, Random random) {
            Rotation rotation = endCityPiece.placeSettings.getRotation();
            int n2 = random.nextInt(4) + 1;
            EndCityPiece endCityPiece2 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece, new BlockPos(0, 0, -4), "bridge_piece", rotation, true));
            endCityPiece2.genDepth = -1;
            int n3 = 0;
            for (int i = 0; i < n2; ++i) {
                if (random.nextBoolean()) {
                    endCityPiece2 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece2, new BlockPos(0, n3, -4), "bridge_piece", rotation, true));
                    n3 = 0;
                    continue;
                }
                endCityPiece2 = random.nextBoolean() ? EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece2, new BlockPos(0, n3, -4), "bridge_steep_stairs", rotation, true)) : EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece2, new BlockPos(0, n3, -8), "bridge_gentle_stairs", rotation, true));
                n3 = 4;
            }
            if (this.shipCreated || random.nextInt(10 - n) != 0) {
                if (!EndCityPieces.recursiveChildren(structureManager, HOUSE_TOWER_GENERATOR, n + 1, endCityPiece2, new BlockPos(-3, n3 + 1, -11), list, random)) {
                    return false;
                }
            } else {
                EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece2, new BlockPos(-8 + random.nextInt(8), n3, -70 + random.nextInt(10)), "ship", rotation, true));
                this.shipCreated = true;
            }
            endCityPiece2 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece2, new BlockPos(4, n3, 0), "bridge_end", rotation.getRotated(Rotation.CLOCKWISE_180), true));
            endCityPiece2.genDepth = -1;
            return true;
        }
    };
    private static final List<Tuple<Rotation, BlockPos>> FAT_TOWER_BRIDGES = Lists.newArrayList((Object[])new Tuple[]{new Tuple<Rotation, BlockPos>(Rotation.NONE, new BlockPos(4, -1, 0)), new Tuple<Rotation, BlockPos>(Rotation.CLOCKWISE_90, new BlockPos(12, -1, 4)), new Tuple<Rotation, BlockPos>(Rotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 8)), new Tuple<Rotation, BlockPos>(Rotation.CLOCKWISE_180, new BlockPos(8, -1, 12))});
    private static final SectionGenerator FAT_TOWER_GENERATOR = new SectionGenerator(){

        @Override
        public void init() {
        }

        @Override
        public boolean generate(StructureManager structureManager, int n, EndCityPiece endCityPiece, BlockPos blockPos, List<StructurePiece> list, Random random) {
            Rotation rotation = endCityPiece.placeSettings.getRotation();
            EndCityPiece endCityPiece2 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece, new BlockPos(-3, 4, -3), "fat_tower_base", rotation, true));
            endCityPiece2 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece2, new BlockPos(0, 4, 0), "fat_tower_middle", rotation, true));
            for (int i = 0; i < 2 && random.nextInt(3) != 0; ++i) {
                endCityPiece2 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece2, new BlockPos(0, 8, 0), "fat_tower_middle", rotation, true));
                for (Tuple tuple : FAT_TOWER_BRIDGES) {
                    if (!random.nextBoolean()) continue;
                    EndCityPiece endCityPiece3 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece2, (BlockPos)tuple.getB(), "bridge_end", rotation.getRotated((Rotation)((Object)tuple.getA())), true));
                    EndCityPieces.recursiveChildren(structureManager, TOWER_BRIDGE_GENERATOR, n + 1, endCityPiece3, null, list, random);
                }
            }
            endCityPiece2 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece2, new BlockPos(-2, 8, -2), "fat_tower_top", rotation, true));
            return true;
        }
    };

    private static EndCityPiece addPiece(StructureManager structureManager, EndCityPiece endCityPiece, BlockPos blockPos, String string, Rotation rotation, boolean bl) {
        EndCityPiece endCityPiece2 = new EndCityPiece(structureManager, string, endCityPiece.templatePosition, rotation, bl);
        BlockPos blockPos2 = endCityPiece.template.calculateConnectedPosition(endCityPiece.placeSettings, blockPos, endCityPiece2.placeSettings, BlockPos.ZERO);
        endCityPiece2.move(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
        return endCityPiece2;
    }

    public static void startHouseTower(StructureManager structureManager, BlockPos blockPos, Rotation rotation, List<StructurePiece> list, Random random) {
        FAT_TOWER_GENERATOR.init();
        HOUSE_TOWER_GENERATOR.init();
        TOWER_BRIDGE_GENERATOR.init();
        TOWER_GENERATOR.init();
        EndCityPiece endCityPiece = EndCityPieces.addHelper(list, new EndCityPiece(structureManager, "base_floor", blockPos, rotation, true));
        endCityPiece = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece, new BlockPos(-1, 0, -1), "second_floor_1", rotation, false));
        endCityPiece = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece, new BlockPos(-1, 4, -1), "third_floor_1", rotation, false));
        endCityPiece = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPiece, new BlockPos(-1, 8, -1), "third_roof", rotation, true));
        EndCityPieces.recursiveChildren(structureManager, TOWER_GENERATOR, 1, endCityPiece, null, list, random);
    }

    private static EndCityPiece addHelper(List<StructurePiece> list, EndCityPiece endCityPiece) {
        list.add(endCityPiece);
        return endCityPiece;
    }

    private static boolean recursiveChildren(StructureManager structureManager, SectionGenerator sectionGenerator, int n, EndCityPiece endCityPiece, BlockPos blockPos, List<StructurePiece> list, Random random) {
        if (n > 8) {
            return false;
        }
        ArrayList arrayList = Lists.newArrayList();
        if (sectionGenerator.generate(structureManager, n, endCityPiece, blockPos, arrayList, random)) {
            boolean bl = false;
            int n2 = random.nextInt();
            for (StructurePiece structurePiece : arrayList) {
                structurePiece.genDepth = n2;
                StructurePiece structurePiece2 = StructurePiece.findCollisionPiece(list, structurePiece.getBoundingBox());
                if (structurePiece2 == null || structurePiece2.genDepth == endCityPiece.genDepth) continue;
                bl = true;
                break;
            }
            if (!bl) {
                list.addAll(arrayList);
                return true;
            }
        }
        return false;
    }

    static interface SectionGenerator {
        public void init();

        public boolean generate(StructureManager var1, int var2, EndCityPiece var3, BlockPos var4, List<StructurePiece> var5, Random var6);
    }

    public static class EndCityPiece
    extends TemplateStructurePiece {
        private final String templateName;
        private final Rotation rotation;
        private final boolean overwrite;

        public EndCityPiece(StructureManager structureManager, String string, BlockPos blockPos, Rotation rotation, boolean bl) {
            super(StructurePieceType.END_CITY_PIECE, 0);
            this.templateName = string;
            this.templatePosition = blockPos;
            this.rotation = rotation;
            this.overwrite = bl;
            this.loadTemplate(structureManager);
        }

        public EndCityPiece(StructureManager structureManager, CompoundTag compoundTag) {
            super(StructurePieceType.END_CITY_PIECE, compoundTag);
            this.templateName = compoundTag.getString("Template");
            this.rotation = Rotation.valueOf(compoundTag.getString("Rot"));
            this.overwrite = compoundTag.getBoolean("OW");
            this.loadTemplate(structureManager);
        }

        private void loadTemplate(StructureManager structureManager) {
            StructureTemplate structureTemplate = structureManager.getOrCreate(new ResourceLocation("end_city/" + this.templateName));
            StructurePlaceSettings structurePlaceSettings = (this.overwrite ? OVERWRITE : INSERT).copy().setRotation(this.rotation);
            this.setup(structureTemplate, this.templatePosition, structurePlaceSettings);
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag compoundTag) {
            super.addAdditionalSaveData(compoundTag);
            compoundTag.putString("Template", this.templateName);
            compoundTag.putString("Rot", this.rotation.name());
            compoundTag.putBoolean("OW", this.overwrite);
        }

        @Override
        protected void handleDataMarker(String string, BlockPos blockPos, ServerLevelAccessor serverLevelAccessor, Random random, BoundingBox boundingBox) {
            if (string.startsWith("Chest")) {
                BlockPos blockPos2 = blockPos.below();
                if (boundingBox.isInside(blockPos2)) {
                    RandomizableContainerBlockEntity.setLootTable(serverLevelAccessor, random, blockPos2, BuiltInLootTables.END_CITY_TREASURE);
                }
            } else if (string.startsWith("Sentry")) {
                Shulker shulker = EntityType.SHULKER.create(serverLevelAccessor.getLevel());
                shulker.setPos((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
                shulker.setAttachPosition(blockPos);
                serverLevelAccessor.addFreshEntity(shulker);
            } else if (string.startsWith("Elytra")) {
                ItemFrame itemFrame = new ItemFrame(serverLevelAccessor.getLevel(), blockPos, this.rotation.rotate(Direction.SOUTH));
                itemFrame.setItem(new ItemStack(Items.ELYTRA), false);
                serverLevelAccessor.addFreshEntity(itemFrame);
            }
        }
    }

}

