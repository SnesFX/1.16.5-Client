/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class EndPodiumFeature
extends Feature<NoneFeatureConfiguration> {
    public static final BlockPos END_PODIUM_LOCATION = BlockPos.ZERO;
    private final boolean active;

    public EndPodiumFeature(boolean bl) {
        super(NoneFeatureConfiguration.CODEC);
        this.active = bl;
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
        for (BlockPos object : BlockPos.betweenClosed(new BlockPos(blockPos.getX() - 4, blockPos.getY() - 1, blockPos.getZ() - 4), new BlockPos(blockPos.getX() + 4, blockPos.getY() + 32, blockPos.getZ() + 4))) {
            boolean direction = object.closerThan(blockPos, 2.5);
            if (!direction && !object.closerThan(blockPos, 3.5)) continue;
            if (object.getY() < blockPos.getY()) {
                if (direction) {
                    this.setBlock(worldGenLevel, object, Blocks.BEDROCK.defaultBlockState());
                    continue;
                }
                if (object.getY() >= blockPos.getY()) continue;
                this.setBlock(worldGenLevel, object, Blocks.END_STONE.defaultBlockState());
                continue;
            }
            if (object.getY() > blockPos.getY()) {
                this.setBlock(worldGenLevel, object, Blocks.AIR.defaultBlockState());
                continue;
            }
            if (!direction) {
                this.setBlock(worldGenLevel, object, Blocks.BEDROCK.defaultBlockState());
                continue;
            }
            if (this.active) {
                this.setBlock(worldGenLevel, new BlockPos(object), Blocks.END_PORTAL.defaultBlockState());
                continue;
            }
            this.setBlock(worldGenLevel, new BlockPos(object), Blocks.AIR.defaultBlockState());
        }
        for (int i = 0; i < 4; ++i) {
            this.setBlock(worldGenLevel, blockPos.above(i), Blocks.BEDROCK.defaultBlockState());
        }
        BlockPos blockPos2 = blockPos.above(2);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            this.setBlock(worldGenLevel, blockPos2.relative(direction), (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, direction));
        }
        return true;
    }
}

