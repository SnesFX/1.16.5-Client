/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.placement.nether;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class CountMultiLayerDecorator
extends FeatureDecorator<CountConfiguration> {
    public CountMultiLayerDecorator(Codec<CountConfiguration> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecorationContext decorationContext, Random random, CountConfiguration countConfiguration, BlockPos blockPos) {
        boolean bl;
        ArrayList arrayList = Lists.newArrayList();
        int n = 0;
        do {
            bl = false;
            for (int i = 0; i < countConfiguration.count().sample(random); ++i) {
                int n2;
                int n3;
                int n4 = random.nextInt(16) + blockPos.getX();
                int n5 = CountMultiLayerDecorator.findOnGroundYPosition(decorationContext, n4, n2 = decorationContext.getHeight(Heightmap.Types.MOTION_BLOCKING, n4, n3 = random.nextInt(16) + blockPos.getZ()), n3, n);
                if (n5 == Integer.MAX_VALUE) continue;
                arrayList.add(new BlockPos(n4, n5, n3));
                bl = true;
            }
            ++n;
        } while (bl);
        return arrayList.stream();
    }

    private static int findOnGroundYPosition(DecorationContext decorationContext, int n, int n2, int n3, int n4) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(n, n2, n3);
        int n5 = 0;
        BlockState blockState = decorationContext.getBlockState(mutableBlockPos);
        for (int i = n2; i >= 1; --i) {
            mutableBlockPos.setY(i - 1);
            BlockState blockState2 = decorationContext.getBlockState(mutableBlockPos);
            if (!CountMultiLayerDecorator.isEmpty(blockState2) && CountMultiLayerDecorator.isEmpty(blockState) && !blockState2.is(Blocks.BEDROCK)) {
                if (n5 == n4) {
                    return mutableBlockPos.getY() + 1;
                }
                ++n5;
            }
            blockState = blockState2;
        }
        return Integer.MAX_VALUE;
    }

    private static boolean isEmpty(BlockState blockState) {
        return blockState.isAir() || blockState.is(Blocks.WATER) || blockState.is(Blocks.LAVA);
    }
}

