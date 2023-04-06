/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.TripWireHookBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class JunglePyramidPiece
extends ScatteredFeaturePiece {
    private boolean placedMainChest;
    private boolean placedHiddenChest;
    private boolean placedTrap1;
    private boolean placedTrap2;
    private static final MossStoneSelector STONE_SELECTOR = new MossStoneSelector();

    public JunglePyramidPiece(Random random, int n, int n2) {
        super(StructurePieceType.JUNGLE_PYRAMID_PIECE, random, n, 64, n2, 12, 10, 15);
    }

    public JunglePyramidPiece(StructureManager structureManager, CompoundTag compoundTag) {
        super(StructurePieceType.JUNGLE_PYRAMID_PIECE, compoundTag);
        this.placedMainChest = compoundTag.getBoolean("placedMainChest");
        this.placedHiddenChest = compoundTag.getBoolean("placedHiddenChest");
        this.placedTrap1 = compoundTag.getBoolean("placedTrap1");
        this.placedTrap2 = compoundTag.getBoolean("placedTrap2");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("placedMainChest", this.placedMainChest);
        compoundTag.putBoolean("placedHiddenChest", this.placedHiddenChest);
        compoundTag.putBoolean("placedTrap1", this.placedTrap1);
        compoundTag.putBoolean("placedTrap2", this.placedTrap2);
    }

    @Override
    public boolean postProcess(WorldGenLevel worldGenLevel, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
        int n;
        int n2;
        if (!this.updateAverageGroundHeight(worldGenLevel, boundingBox, 0)) {
            return false;
        }
        this.generateBox(worldGenLevel, boundingBox, 0, -4, 0, this.width - 1, 0, this.depth - 1, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 2, 1, 2, 9, 2, 2, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 2, 1, 12, 9, 2, 12, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 2, 1, 3, 2, 2, 11, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 9, 1, 3, 9, 2, 11, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 1, 3, 1, 10, 6, 1, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 1, 3, 13, 10, 6, 13, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 1, 3, 2, 1, 6, 12, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 10, 3, 2, 10, 6, 12, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 2, 3, 2, 9, 3, 12, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 2, 6, 2, 9, 6, 12, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 3, 7, 3, 8, 7, 11, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 4, 8, 4, 7, 8, 10, false, random, STONE_SELECTOR);
        this.generateAirBox(worldGenLevel, boundingBox, 3, 1, 3, 8, 2, 11);
        this.generateAirBox(worldGenLevel, boundingBox, 4, 3, 6, 7, 3, 9);
        this.generateAirBox(worldGenLevel, boundingBox, 2, 4, 2, 9, 5, 12);
        this.generateAirBox(worldGenLevel, boundingBox, 4, 6, 5, 7, 6, 9);
        this.generateAirBox(worldGenLevel, boundingBox, 5, 7, 6, 6, 7, 8);
        this.generateAirBox(worldGenLevel, boundingBox, 5, 1, 2, 6, 2, 2);
        this.generateAirBox(worldGenLevel, boundingBox, 5, 2, 12, 6, 2, 12);
        this.generateAirBox(worldGenLevel, boundingBox, 5, 5, 1, 6, 5, 1);
        this.generateAirBox(worldGenLevel, boundingBox, 5, 5, 13, 6, 5, 13);
        this.placeBlock(worldGenLevel, Blocks.AIR.defaultBlockState(), 1, 5, 5, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.AIR.defaultBlockState(), 10, 5, 5, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.AIR.defaultBlockState(), 1, 5, 9, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.AIR.defaultBlockState(), 10, 5, 9, boundingBox);
        for (n = 0; n <= 14; n += 14) {
            this.generateBox(worldGenLevel, boundingBox, 2, 4, n, 2, 5, n, false, random, STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, 4, 4, n, 4, 5, n, false, random, STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, 7, 4, n, 7, 5, n, false, random, STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, 9, 4, n, 9, 5, n, false, random, STONE_SELECTOR);
        }
        this.generateBox(worldGenLevel, boundingBox, 5, 6, 0, 6, 6, 0, false, random, STONE_SELECTOR);
        for (n = 0; n <= 11; n += 11) {
            for (int i = 2; i <= 12; i += 2) {
                this.generateBox(worldGenLevel, boundingBox, n, 4, i, n, 5, i, false, random, STONE_SELECTOR);
            }
            this.generateBox(worldGenLevel, boundingBox, n, 6, 5, n, 6, 5, false, random, STONE_SELECTOR);
            this.generateBox(worldGenLevel, boundingBox, n, 6, 9, n, 6, 9, false, random, STONE_SELECTOR);
        }
        this.generateBox(worldGenLevel, boundingBox, 2, 7, 2, 2, 9, 2, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 9, 7, 2, 9, 9, 2, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 2, 7, 12, 2, 9, 12, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 9, 7, 12, 9, 9, 12, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 4, 9, 4, 4, 9, 4, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 7, 9, 4, 7, 9, 4, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 4, 9, 10, 4, 9, 10, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 7, 9, 10, 7, 9, 10, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 5, 9, 7, 6, 9, 7, false, random, STONE_SELECTOR);
        BlockState blockState = (BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST);
        BlockState blockState2 = (BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST);
        BlockState blockState3 = (BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
        BlockState blockState4 = (BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
        this.placeBlock(worldGenLevel, blockState4, 5, 9, 6, boundingBox);
        this.placeBlock(worldGenLevel, blockState4, 6, 9, 6, boundingBox);
        this.placeBlock(worldGenLevel, blockState3, 5, 9, 8, boundingBox);
        this.placeBlock(worldGenLevel, blockState3, 6, 9, 8, boundingBox);
        this.placeBlock(worldGenLevel, blockState4, 4, 0, 0, boundingBox);
        this.placeBlock(worldGenLevel, blockState4, 5, 0, 0, boundingBox);
        this.placeBlock(worldGenLevel, blockState4, 6, 0, 0, boundingBox);
        this.placeBlock(worldGenLevel, blockState4, 7, 0, 0, boundingBox);
        this.placeBlock(worldGenLevel, blockState4, 4, 1, 8, boundingBox);
        this.placeBlock(worldGenLevel, blockState4, 4, 2, 9, boundingBox);
        this.placeBlock(worldGenLevel, blockState4, 4, 3, 10, boundingBox);
        this.placeBlock(worldGenLevel, blockState4, 7, 1, 8, boundingBox);
        this.placeBlock(worldGenLevel, blockState4, 7, 2, 9, boundingBox);
        this.placeBlock(worldGenLevel, blockState4, 7, 3, 10, boundingBox);
        this.generateBox(worldGenLevel, boundingBox, 4, 1, 9, 4, 1, 9, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 7, 1, 9, 7, 1, 9, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 4, 1, 10, 7, 2, 10, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 5, 4, 5, 6, 4, 5, false, random, STONE_SELECTOR);
        this.placeBlock(worldGenLevel, blockState, 4, 4, 5, boundingBox);
        this.placeBlock(worldGenLevel, blockState2, 7, 4, 5, boundingBox);
        for (n2 = 0; n2 < 4; ++n2) {
            this.placeBlock(worldGenLevel, blockState3, 5, 0 - n2, 6 + n2, boundingBox);
            this.placeBlock(worldGenLevel, blockState3, 6, 0 - n2, 6 + n2, boundingBox);
            this.generateAirBox(worldGenLevel, boundingBox, 5, 0 - n2, 7 + n2, 6, 0 - n2, 9 + n2);
        }
        this.generateAirBox(worldGenLevel, boundingBox, 1, -3, 12, 10, -1, 13);
        this.generateAirBox(worldGenLevel, boundingBox, 1, -3, 1, 3, -1, 13);
        this.generateAirBox(worldGenLevel, boundingBox, 1, -3, 1, 9, -1, 5);
        for (n2 = 1; n2 <= 13; n2 += 2) {
            this.generateBox(worldGenLevel, boundingBox, 1, -3, n2, 1, -2, n2, false, random, STONE_SELECTOR);
        }
        for (n2 = 2; n2 <= 12; n2 += 2) {
            this.generateBox(worldGenLevel, boundingBox, 1, -1, n2, 3, -1, n2, false, random, STONE_SELECTOR);
        }
        this.generateBox(worldGenLevel, boundingBox, 2, -2, 1, 5, -2, 1, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 7, -2, 1, 9, -2, 1, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 6, -3, 1, 6, -3, 1, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 6, -1, 1, 6, -1, 1, false, random, STONE_SELECTOR);
        this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(TripWireHookBlock.FACING, Direction.EAST)).setValue(TripWireHookBlock.ATTACHED, true), 1, -3, 8, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(TripWireHookBlock.FACING, Direction.WEST)).setValue(TripWireHookBlock.ATTACHED, true), 4, -3, 8, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)((BlockState)((BlockState)Blocks.TRIPWIRE.defaultBlockState().setValue(TripWireBlock.EAST, true)).setValue(TripWireBlock.WEST, true)).setValue(TripWireBlock.ATTACHED, true), 2, -3, 8, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)((BlockState)((BlockState)Blocks.TRIPWIRE.defaultBlockState().setValue(TripWireBlock.EAST, true)).setValue(TripWireBlock.WEST, true)).setValue(TripWireBlock.ATTACHED, true), 3, -3, 8, boundingBox);
        BlockState blockState5 = (BlockState)((BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE);
        this.placeBlock(worldGenLevel, blockState5, 5, -3, 7, boundingBox);
        this.placeBlock(worldGenLevel, blockState5, 5, -3, 6, boundingBox);
        this.placeBlock(worldGenLevel, blockState5, 5, -3, 5, boundingBox);
        this.placeBlock(worldGenLevel, blockState5, 5, -3, 4, boundingBox);
        this.placeBlock(worldGenLevel, blockState5, 5, -3, 3, boundingBox);
        this.placeBlock(worldGenLevel, blockState5, 5, -3, 2, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.WEST, RedstoneSide.SIDE), 5, -3, 1, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.EAST, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.WEST, RedstoneSide.SIDE), 4, -3, 1, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 3, -3, 1, boundingBox);
        if (!this.placedTrap1) {
            this.placedTrap1 = this.createDispenser(worldGenLevel, boundingBox, random, 3, -2, 1, Direction.NORTH, BuiltInLootTables.JUNGLE_TEMPLE_DISPENSER);
        }
        this.placeBlock(worldGenLevel, (BlockState)Blocks.VINE.defaultBlockState().setValue(VineBlock.SOUTH, true), 3, -2, 2, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(TripWireHookBlock.FACING, Direction.NORTH)).setValue(TripWireHookBlock.ATTACHED, true), 7, -3, 1, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(TripWireHookBlock.FACING, Direction.SOUTH)).setValue(TripWireHookBlock.ATTACHED, true), 7, -3, 5, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)((BlockState)((BlockState)Blocks.TRIPWIRE.defaultBlockState().setValue(TripWireBlock.NORTH, true)).setValue(TripWireBlock.SOUTH, true)).setValue(TripWireBlock.ATTACHED, true), 7, -3, 2, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)((BlockState)((BlockState)Blocks.TRIPWIRE.defaultBlockState().setValue(TripWireBlock.NORTH, true)).setValue(TripWireBlock.SOUTH, true)).setValue(TripWireBlock.ATTACHED, true), 7, -3, 3, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)((BlockState)((BlockState)Blocks.TRIPWIRE.defaultBlockState().setValue(TripWireBlock.NORTH, true)).setValue(TripWireBlock.SOUTH, true)).setValue(TripWireBlock.ATTACHED, true), 7, -3, 4, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.EAST, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.WEST, RedstoneSide.SIDE), 8, -3, 6, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.WEST, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE), 9, -3, 6, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)((BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.SOUTH, RedstoneSide.UP), 9, -3, 5, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 9, -3, 4, boundingBox);
        this.placeBlock(worldGenLevel, blockState5, 9, -2, 4, boundingBox);
        if (!this.placedTrap2) {
            this.placedTrap2 = this.createDispenser(worldGenLevel, boundingBox, random, 9, -2, 3, Direction.WEST, BuiltInLootTables.JUNGLE_TEMPLE_DISPENSER);
        }
        this.placeBlock(worldGenLevel, (BlockState)Blocks.VINE.defaultBlockState().setValue(VineBlock.EAST, true), 8, -1, 3, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)Blocks.VINE.defaultBlockState().setValue(VineBlock.EAST, true), 8, -2, 3, boundingBox);
        if (!this.placedMainChest) {
            this.placedMainChest = this.createChest(worldGenLevel, boundingBox, random, 8, -3, 3, BuiltInLootTables.JUNGLE_TEMPLE);
        }
        this.placeBlock(worldGenLevel, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 9, -3, 2, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 8, -3, 1, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 4, -3, 5, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 5, -2, 5, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 5, -1, 5, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 6, -3, 5, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 7, -2, 5, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 7, -1, 5, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 8, -3, 5, boundingBox);
        this.generateBox(worldGenLevel, boundingBox, 9, -1, 1, 9, -1, 5, false, random, STONE_SELECTOR);
        this.generateAirBox(worldGenLevel, boundingBox, 8, -3, 8, 10, -1, 10);
        this.placeBlock(worldGenLevel, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 8, -2, 11, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 9, -2, 11, boundingBox);
        this.placeBlock(worldGenLevel, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 10, -2, 11, boundingBox);
        BlockState blockState6 = (BlockState)((BlockState)Blocks.LEVER.defaultBlockState().setValue(LeverBlock.FACING, Direction.NORTH)).setValue(LeverBlock.FACE, AttachFace.WALL);
        this.placeBlock(worldGenLevel, blockState6, 8, -2, 12, boundingBox);
        this.placeBlock(worldGenLevel, blockState6, 9, -2, 12, boundingBox);
        this.placeBlock(worldGenLevel, blockState6, 10, -2, 12, boundingBox);
        this.generateBox(worldGenLevel, boundingBox, 8, -3, 8, 8, -3, 10, false, random, STONE_SELECTOR);
        this.generateBox(worldGenLevel, boundingBox, 10, -3, 8, 10, -3, 10, false, random, STONE_SELECTOR);
        this.placeBlock(worldGenLevel, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 10, -2, 9, boundingBox);
        this.placeBlock(worldGenLevel, blockState5, 8, -2, 9, boundingBox);
        this.placeBlock(worldGenLevel, blockState5, 8, -2, 10, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)((BlockState)((BlockState)((BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.EAST, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.WEST, RedstoneSide.SIDE), 10, -1, 9, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)Blocks.STICKY_PISTON.defaultBlockState().setValue(PistonBaseBlock.FACING, Direction.UP), 9, -2, 8, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)Blocks.STICKY_PISTON.defaultBlockState().setValue(PistonBaseBlock.FACING, Direction.WEST), 10, -2, 8, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)Blocks.STICKY_PISTON.defaultBlockState().setValue(PistonBaseBlock.FACING, Direction.WEST), 10, -1, 8, boundingBox);
        this.placeBlock(worldGenLevel, (BlockState)Blocks.REPEATER.defaultBlockState().setValue(RepeaterBlock.FACING, Direction.NORTH), 10, -2, 10, boundingBox);
        if (!this.placedHiddenChest) {
            this.placedHiddenChest = this.createChest(worldGenLevel, boundingBox, random, 9, -3, 10, BuiltInLootTables.JUNGLE_TEMPLE);
        }
        return true;
    }

    static class MossStoneSelector
    extends StructurePiece.BlockSelector {
        private MossStoneSelector() {
        }

        @Override
        public void next(Random random, int n, int n2, int n3, boolean bl) {
            this.next = random.nextFloat() < 0.4f ? Blocks.COBBLESTONE.defaultBlockState() : Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        }
    }

}

