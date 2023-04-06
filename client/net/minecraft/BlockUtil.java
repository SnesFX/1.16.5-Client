/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 */
package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

public class BlockUtil {
    public static FoundRectangle getLargestRectangleAround(BlockPos blockPos, Direction.Axis axis, int n, Direction.Axis axis2, int n2, Predicate<BlockPos> predicate) {
        IntBounds intBounds;
        int n3;
        BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();
        Direction direction = Direction.get(Direction.AxisDirection.NEGATIVE, axis);
        Direction direction2 = direction.getOpposite();
        Direction direction3 = Direction.get(Direction.AxisDirection.NEGATIVE, axis2);
        Direction direction4 = direction3.getOpposite();
        int n4 = BlockUtil.getLimit(predicate, mutableBlockPos.set(blockPos), direction, n);
        int n5 = BlockUtil.getLimit(predicate, mutableBlockPos.set(blockPos), direction2, n);
        int n6 = n4;
        IntBounds[] arrintBounds = new IntBounds[n6 + 1 + n5];
        arrintBounds[n6] = new IntBounds(BlockUtil.getLimit(predicate, mutableBlockPos.set(blockPos), direction3, n2), BlockUtil.getLimit(predicate, mutableBlockPos.set(blockPos), direction4, n2));
        int n7 = arrintBounds[n6].min;
        for (n3 = 1; n3 <= n4; ++n3) {
            intBounds = arrintBounds[n6 - (n3 - 1)];
            arrintBounds[n6 - n3] = new IntBounds(BlockUtil.getLimit(predicate, mutableBlockPos.set(blockPos).move(direction, n3), direction3, intBounds.min), BlockUtil.getLimit(predicate, mutableBlockPos.set(blockPos).move(direction, n3), direction4, intBounds.max));
        }
        for (n3 = 1; n3 <= n5; ++n3) {
            intBounds = arrintBounds[n6 + n3 - 1];
            arrintBounds[n6 + n3] = new IntBounds(BlockUtil.getLimit(predicate, mutableBlockPos.set(blockPos).move(direction2, n3), direction3, intBounds.min), BlockUtil.getLimit(predicate, mutableBlockPos.set(blockPos).move(direction2, n3), direction4, intBounds.max));
        }
        n3 = 0;
        int n8 = 0;
        int n9 = 0;
        int n10 = 0;
        int[] arrn = new int[arrintBounds.length];
        for (int i = n7; i >= 0; --i) {
            int n11;
            IntBounds intBounds2;
            int n12;
            for (int j = 0; j < arrintBounds.length; ++j) {
                intBounds2 = arrintBounds[j];
                n12 = n7 - intBounds2.min;
                n11 = n7 + intBounds2.max;
                arrn[j] = i >= n12 && i <= n11 ? n11 + 1 - i : 0;
            }
            Pair<IntBounds, Integer> pair = BlockUtil.getMaxRectangleLocation(arrn);
            intBounds2 = (IntBounds)pair.getFirst();
            n12 = 1 + intBounds2.max - intBounds2.min;
            n11 = (Integer)pair.getSecond();
            if (n12 * n11 <= n9 * n10) continue;
            n3 = intBounds2.min;
            n8 = i;
            n9 = n12;
            n10 = n11;
        }
        return new FoundRectangle(blockPos.relative(axis, n3 - n6).relative(axis2, n8 - n7), n9, n10);
    }

    private static int getLimit(Predicate<BlockPos> predicate, BlockPos.MutableBlockPos mutableBlockPos, Direction direction, int n) {
        int n2;
        for (n2 = 0; n2 < n && predicate.test(mutableBlockPos.move(direction)); ++n2) {
        }
        return n2;
    }

    @VisibleForTesting
    static Pair<IntBounds, Integer> getMaxRectangleLocation(int[] arrn) {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        IntArrayList intArrayList = new IntArrayList();
        intArrayList.push(0);
        for (int i = 1; i <= arrn.length; ++i) {
            int n4;
            int n5 = n4 = i == arrn.length ? 0 : arrn[i];
            while (!intArrayList.isEmpty()) {
                int n6 = arrn[intArrayList.topInt()];
                if (n4 >= n6) {
                    intArrayList.push(i);
                    break;
                }
                intArrayList.popInt();
                int n7 = intArrayList.isEmpty() ? 0 : intArrayList.topInt() + 1;
                if (n6 * (i - n7) <= n3 * (n2 - n)) continue;
                n2 = i;
                n = n7;
                n3 = n6;
            }
            if (!intArrayList.isEmpty()) continue;
            intArrayList.push(i);
        }
        return new Pair((Object)new IntBounds(n, n2 - 1), (Object)n3);
    }

    public static class FoundRectangle {
        public final BlockPos minCorner;
        public final int axis1Size;
        public final int axis2Size;

        public FoundRectangle(BlockPos blockPos, int n, int n2) {
            this.minCorner = blockPos;
            this.axis1Size = n;
            this.axis2Size = n2;
        }
    }

    public static class IntBounds {
        public final int min;
        public final int max;

        public IntBounds(int n, int n2) {
            this.min = n;
            this.max = n2;
        }

        public String toString() {
            return "IntBounds{min=" + this.min + ", max=" + this.max + '}';
        }
    }

}

