/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class DoubleBlockCombiner {
    public static <S extends BlockEntity> NeighborCombineResult<S> combineWithNeigbour(BlockEntityType<S> blockEntityType, Function<BlockState, BlockType> function, Function<BlockState, Direction> function2, DirectionProperty directionProperty, BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, BiPredicate<LevelAccessor, BlockPos> biPredicate) {
        boolean bl;
        BlockType blockType;
        S s = blockEntityType.getBlockEntity(levelAccessor, blockPos);
        if (s == null) {
            return Combiner::acceptNone;
        }
        if (biPredicate.test(levelAccessor, blockPos)) {
            return Combiner::acceptNone;
        }
        BlockType blockType2 = function.apply(blockState);
        boolean bl2 = blockType2 == BlockType.SINGLE;
        boolean bl3 = bl = blockType2 == BlockType.FIRST;
        if (bl2) {
            return new NeighborCombineResult.Single<S>(s);
        }
        BlockPos blockPos2 = blockPos.relative(function2.apply(blockState));
        BlockState blockState2 = levelAccessor.getBlockState(blockPos2);
        if (blockState2.is(blockState.getBlock()) && (blockType = function.apply(blockState2)) != BlockType.SINGLE && blockType2 != blockType && blockState2.getValue(directionProperty) == blockState.getValue(directionProperty)) {
            if (biPredicate.test(levelAccessor, blockPos2)) {
                return Combiner::acceptNone;
            }
            S s2 = blockEntityType.getBlockEntity(levelAccessor, blockPos2);
            if (s2 != null) {
                S s3 = bl ? s : s2;
                S s4 = bl ? s2 : s;
                return new NeighborCombineResult.Double<S>(s3, s4);
            }
        }
        return new NeighborCombineResult.Single<S>(s);
    }

    public static interface NeighborCombineResult<S> {
        public <T> T apply(Combiner<? super S, T> var1);

        public static final class Single<S>
        implements NeighborCombineResult<S> {
            private final S single;

            public Single(S s) {
                this.single = s;
            }

            @Override
            public <T> T apply(Combiner<? super S, T> combiner) {
                return combiner.acceptSingle(this.single);
            }
        }

        public static final class Double<S>
        implements NeighborCombineResult<S> {
            private final S first;
            private final S second;

            public Double(S s, S s2) {
                this.first = s;
                this.second = s2;
            }

            @Override
            public <T> T apply(Combiner<? super S, T> combiner) {
                return combiner.acceptDouble(this.first, this.second);
            }
        }

    }

    public static interface Combiner<S, T> {
        public T acceptDouble(S var1, S var2);

        public T acceptSingle(S var1);

        public T acceptNone();
    }

    public static enum BlockType {
        SINGLE,
        FIRST,
        SECOND;
        
    }

}

