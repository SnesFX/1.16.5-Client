/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class CocoaDecorator
extends TreeDecorator {
    public static final Codec<CocoaDecorator> CODEC = Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("probability").xmap(CocoaDecorator::new, cocoaDecorator -> Float.valueOf(cocoaDecorator.probability)).codec();
    private final float probability;

    public CocoaDecorator(float f) {
        this.probability = f;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.COCOA;
    }

    @Override
    public void place(WorldGenLevel worldGenLevel, Random random, List<BlockPos> list, List<BlockPos> list2, Set<BlockPos> set, BoundingBox boundingBox) {
        if (random.nextFloat() >= this.probability) {
            return;
        }
        int n = list.get(0).getY();
        list.stream().filter(blockPos -> blockPos.getY() - n <= 2).forEach(blockPos -> {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos blockPos2;
                Direction direction2;
                if (!(random.nextFloat() <= 0.25f) || !Feature.isAir(worldGenLevel, blockPos2 = blockPos.offset((direction2 = direction.getOpposite()).getStepX(), 0, direction2.getStepZ()))) continue;
                BlockState blockState = (BlockState)((BlockState)Blocks.COCOA.defaultBlockState().setValue(CocoaBlock.AGE, random.nextInt(3))).setValue(CocoaBlock.FACING, direction);
                this.setBlock(worldGenLevel, blockPos2, blockState, set, boundingBox);
            }
        });
    }
}

