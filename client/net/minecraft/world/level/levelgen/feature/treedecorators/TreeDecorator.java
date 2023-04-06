/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public abstract class TreeDecorator {
    public static final Codec<TreeDecorator> CODEC = Registry.TREE_DECORATOR_TYPES.dispatch(TreeDecorator::type, TreeDecoratorType::codec);

    protected abstract TreeDecoratorType<?> type();

    public abstract void place(WorldGenLevel var1, Random var2, List<BlockPos> var3, List<BlockPos> var4, Set<BlockPos> var5, BoundingBox var6);

    protected void placeVine(LevelWriter levelWriter, BlockPos blockPos, BooleanProperty booleanProperty, Set<BlockPos> set, BoundingBox boundingBox) {
        this.setBlock(levelWriter, blockPos, (BlockState)Blocks.VINE.defaultBlockState().setValue(booleanProperty, true), set, boundingBox);
    }

    protected void setBlock(LevelWriter levelWriter, BlockPos blockPos, BlockState blockState, Set<BlockPos> set, BoundingBox boundingBox) {
        levelWriter.setBlock(blockPos, blockState, 19);
        set.add(blockPos);
        boundingBox.expand(new BoundingBox(blockPos, blockPos));
    }
}

