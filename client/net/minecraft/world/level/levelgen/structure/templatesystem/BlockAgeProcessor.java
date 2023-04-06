/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class BlockAgeProcessor
extends StructureProcessor {
    public static final Codec<BlockAgeProcessor> CODEC = Codec.FLOAT.fieldOf("mossiness").xmap(BlockAgeProcessor::new, blockAgeProcessor -> Float.valueOf(blockAgeProcessor.mossiness)).codec();
    private final float mossiness;

    public BlockAgeProcessor(float f) {
        this.mossiness = f;
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader, BlockPos blockPos, BlockPos blockPos2, StructureTemplate.StructureBlockInfo structureBlockInfo, StructureTemplate.StructureBlockInfo structureBlockInfo2, StructurePlaceSettings structurePlaceSettings) {
        Random random = structurePlaceSettings.getRandom(structureBlockInfo2.pos);
        BlockState blockState = structureBlockInfo2.state;
        BlockPos blockPos3 = structureBlockInfo2.pos;
        BlockState blockState2 = null;
        if (blockState.is(Blocks.STONE_BRICKS) || blockState.is(Blocks.STONE) || blockState.is(Blocks.CHISELED_STONE_BRICKS)) {
            blockState2 = this.maybeReplaceFullStoneBlock(random);
        } else if (blockState.is(BlockTags.STAIRS)) {
            blockState2 = this.maybeReplaceStairs(random, structureBlockInfo2.state);
        } else if (blockState.is(BlockTags.SLABS)) {
            blockState2 = this.maybeReplaceSlab(random);
        } else if (blockState.is(BlockTags.WALLS)) {
            blockState2 = this.maybeReplaceWall(random);
        } else if (blockState.is(Blocks.OBSIDIAN)) {
            blockState2 = this.maybeReplaceObsidian(random);
        }
        if (blockState2 != null) {
            return new StructureTemplate.StructureBlockInfo(blockPos3, blockState2, structureBlockInfo2.nbt);
        }
        return structureBlockInfo2;
    }

    @Nullable
    private BlockState maybeReplaceFullStoneBlock(Random random) {
        if (random.nextFloat() >= 0.5f) {
            return null;
        }
        BlockState[] arrblockState = new BlockState[]{Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), BlockAgeProcessor.getRandomFacingStairs(random, Blocks.STONE_BRICK_STAIRS)};
        BlockState[] arrblockState2 = new BlockState[]{Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), BlockAgeProcessor.getRandomFacingStairs(random, Blocks.MOSSY_STONE_BRICK_STAIRS)};
        return this.getRandomBlock(random, arrblockState, arrblockState2);
    }

    @Nullable
    private BlockState maybeReplaceStairs(Random random, BlockState blockState) {
        Direction direction = blockState.getValue(StairBlock.FACING);
        Half half = blockState.getValue(StairBlock.HALF);
        if (random.nextFloat() >= 0.5f) {
            return null;
        }
        BlockState[] arrblockState = new BlockState[]{Blocks.STONE_SLAB.defaultBlockState(), Blocks.STONE_BRICK_SLAB.defaultBlockState()};
        BlockState[] arrblockState2 = new BlockState[]{(BlockState)((BlockState)Blocks.MOSSY_STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, direction)).setValue(StairBlock.HALF, half), Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState()};
        return this.getRandomBlock(random, arrblockState, arrblockState2);
    }

    @Nullable
    private BlockState maybeReplaceSlab(Random random) {
        if (random.nextFloat() < this.mossiness) {
            return Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState();
        }
        return null;
    }

    @Nullable
    private BlockState maybeReplaceWall(Random random) {
        if (random.nextFloat() < this.mossiness) {
            return Blocks.MOSSY_STONE_BRICK_WALL.defaultBlockState();
        }
        return null;
    }

    @Nullable
    private BlockState maybeReplaceObsidian(Random random) {
        if (random.nextFloat() < 0.15f) {
            return Blocks.CRYING_OBSIDIAN.defaultBlockState();
        }
        return null;
    }

    private static BlockState getRandomFacingStairs(Random random, Block block) {
        return (BlockState)((BlockState)block.defaultBlockState().setValue(StairBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random))).setValue(StairBlock.HALF, Half.values()[random.nextInt(Half.values().length)]);
    }

    private BlockState getRandomBlock(Random random, BlockState[] arrblockState, BlockState[] arrblockState2) {
        if (random.nextFloat() < this.mossiness) {
            return BlockAgeProcessor.getRandomBlock(random, arrblockState2);
        }
        return BlockAgeProcessor.getRandomBlock(random, arrblockState);
    }

    private static BlockState getRandomBlock(Random random, BlockState[] arrblockState) {
        return arrblockState[random.nextInt(arrblockState.length)];
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLOCK_AGE;
    }
}

