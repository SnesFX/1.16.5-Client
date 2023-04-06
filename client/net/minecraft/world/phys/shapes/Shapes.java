/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.math.DoubleMath
 *  com.google.common.math.IntMath
 *  it.unimi.dsi.fastutil.doubles.DoubleArrayList
 *  it.unimi.dsi.fastutil.doubles.DoubleList
 */
package net.minecraft.world.phys.shapes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.CubePointRange;
import net.minecraft.world.phys.shapes.CubeVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteCubeMerger;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.IdenticalMerger;
import net.minecraft.world.phys.shapes.IndexMerger;
import net.minecraft.world.phys.shapes.IndirectMerger;
import net.minecraft.world.phys.shapes.NonOverlappingMerger;
import net.minecraft.world.phys.shapes.SliceShape;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class Shapes {
    private static final VoxelShape BLOCK = Util.make(() -> {
        BitSetDiscreteVoxelShape bitSetDiscreteVoxelShape = new BitSetDiscreteVoxelShape(1, 1, 1);
        ((DiscreteVoxelShape)bitSetDiscreteVoxelShape).setFull(0, 0, 0, true, true);
        return new CubeVoxelShape(bitSetDiscreteVoxelShape);
    });
    public static final VoxelShape INFINITY = Shapes.box(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    private static final VoxelShape EMPTY = new ArrayVoxelShape((DiscreteVoxelShape)new BitSetDiscreteVoxelShape(0, 0, 0), (DoubleList)new DoubleArrayList(new double[]{0.0}), (DoubleList)new DoubleArrayList(new double[]{0.0}), (DoubleList)new DoubleArrayList(new double[]{0.0}));

    public static VoxelShape empty() {
        return EMPTY;
    }

    public static VoxelShape block() {
        return BLOCK;
    }

    public static VoxelShape box(double d, double d2, double d3, double d4, double d5, double d6) {
        return Shapes.create(new AABB(d, d2, d3, d4, d5, d6));
    }

    public static VoxelShape create(AABB aABB) {
        int n = Shapes.findBits(aABB.minX, aABB.maxX);
        int n2 = Shapes.findBits(aABB.minY, aABB.maxY);
        int n3 = Shapes.findBits(aABB.minZ, aABB.maxZ);
        if (n < 0 || n2 < 0 || n3 < 0) {
            return new ArrayVoxelShape(Shapes.BLOCK.shape, new double[]{aABB.minX, aABB.maxX}, new double[]{aABB.minY, aABB.maxY}, new double[]{aABB.minZ, aABB.maxZ});
        }
        if (n == 0 && n2 == 0 && n3 == 0) {
            return aABB.contains(0.5, 0.5, 0.5) ? Shapes.block() : Shapes.empty();
        }
        int n4 = 1 << n;
        int n5 = 1 << n2;
        int n6 = 1 << n3;
        int n7 = (int)Math.round(aABB.minX * (double)n4);
        int n8 = (int)Math.round(aABB.maxX * (double)n4);
        int n9 = (int)Math.round(aABB.minY * (double)n5);
        int n10 = (int)Math.round(aABB.maxY * (double)n5);
        int n11 = (int)Math.round(aABB.minZ * (double)n6);
        int n12 = (int)Math.round(aABB.maxZ * (double)n6);
        BitSetDiscreteVoxelShape bitSetDiscreteVoxelShape = new BitSetDiscreteVoxelShape(n4, n5, n6, n7, n9, n11, n8, n10, n12);
        for (long i = (long)n7; i < (long)n8; ++i) {
            for (long j = (long)n9; j < (long)n10; ++j) {
                for (long k = (long)n11; k < (long)n12; ++k) {
                    bitSetDiscreteVoxelShape.setFull((int)i, (int)j, (int)k, false, true);
                }
            }
        }
        return new CubeVoxelShape(bitSetDiscreteVoxelShape);
    }

    private static int findBits(double d, double d2) {
        if (d < -1.0E-7 || d2 > 1.0000001) {
            return -1;
        }
        for (int i = 0; i <= 3; ++i) {
            boolean bl;
            double d3 = d * (double)(1 << i);
            double d4 = d2 * (double)(1 << i);
            boolean bl2 = Math.abs(d3 - Math.floor(d3)) < 1.0E-7;
            boolean bl3 = bl = Math.abs(d4 - Math.floor(d4)) < 1.0E-7;
            if (!bl2 || !bl) continue;
            return i;
        }
        return -1;
    }

    protected static long lcm(int n, int n2) {
        return (long)n * (long)(n2 / IntMath.gcd((int)n, (int)n2));
    }

    public static VoxelShape or(VoxelShape voxelShape, VoxelShape voxelShape2) {
        return Shapes.join(voxelShape, voxelShape2, BooleanOp.OR);
    }

    public static VoxelShape or(VoxelShape voxelShape, VoxelShape ... arrvoxelShape) {
        return Arrays.stream(arrvoxelShape).reduce(voxelShape, (arg_0, arg_1) -> Shapes.or(arg_0, arg_1));
    }

    public static VoxelShape join(VoxelShape voxelShape, VoxelShape voxelShape2, BooleanOp booleanOp) {
        return Shapes.joinUnoptimized(voxelShape, voxelShape2, booleanOp).optimize();
    }

    public static VoxelShape joinUnoptimized(VoxelShape voxelShape, VoxelShape voxelShape2, BooleanOp booleanOp) {
        if (booleanOp.apply(false, false)) {
            throw Util.pauseInIde(new IllegalArgumentException());
        }
        if (voxelShape == voxelShape2) {
            return booleanOp.apply(true, true) ? voxelShape : Shapes.empty();
        }
        boolean bl = booleanOp.apply(true, false);
        boolean bl2 = booleanOp.apply(false, true);
        if (voxelShape.isEmpty()) {
            return bl2 ? voxelShape2 : Shapes.empty();
        }
        if (voxelShape2.isEmpty()) {
            return bl ? voxelShape : Shapes.empty();
        }
        IndexMerger indexMerger = Shapes.createIndexMerger(1, voxelShape.getCoords(Direction.Axis.X), voxelShape2.getCoords(Direction.Axis.X), bl, bl2);
        IndexMerger indexMerger2 = Shapes.createIndexMerger(indexMerger.getList().size() - 1, voxelShape.getCoords(Direction.Axis.Y), voxelShape2.getCoords(Direction.Axis.Y), bl, bl2);
        IndexMerger indexMerger3 = Shapes.createIndexMerger((indexMerger.getList().size() - 1) * (indexMerger2.getList().size() - 1), voxelShape.getCoords(Direction.Axis.Z), voxelShape2.getCoords(Direction.Axis.Z), bl, bl2);
        BitSetDiscreteVoxelShape bitSetDiscreteVoxelShape = BitSetDiscreteVoxelShape.join(voxelShape.shape, voxelShape2.shape, indexMerger, indexMerger2, indexMerger3, booleanOp);
        if (indexMerger instanceof DiscreteCubeMerger && indexMerger2 instanceof DiscreteCubeMerger && indexMerger3 instanceof DiscreteCubeMerger) {
            return new CubeVoxelShape(bitSetDiscreteVoxelShape);
        }
        return new ArrayVoxelShape((DiscreteVoxelShape)bitSetDiscreteVoxelShape, indexMerger.getList(), indexMerger2.getList(), indexMerger3.getList());
    }

    public static boolean joinIsNotEmpty(VoxelShape voxelShape, VoxelShape voxelShape2, BooleanOp booleanOp) {
        if (booleanOp.apply(false, false)) {
            throw Util.pauseInIde(new IllegalArgumentException());
        }
        if (voxelShape == voxelShape2) {
            return booleanOp.apply(true, true);
        }
        if (voxelShape.isEmpty()) {
            return booleanOp.apply(false, !voxelShape2.isEmpty());
        }
        if (voxelShape2.isEmpty()) {
            return booleanOp.apply(!voxelShape.isEmpty(), false);
        }
        boolean bl = booleanOp.apply(true, false);
        boolean bl2 = booleanOp.apply(false, true);
        for (Direction.Axis axis : AxisCycle.AXIS_VALUES) {
            if (voxelShape.max(axis) < voxelShape2.min(axis) - 1.0E-7) {
                return bl || bl2;
            }
            if (!(voxelShape2.max(axis) < voxelShape.min(axis) - 1.0E-7)) continue;
            return bl || bl2;
        }
        IndexMerger indexMerger = Shapes.createIndexMerger(1, voxelShape.getCoords(Direction.Axis.X), voxelShape2.getCoords(Direction.Axis.X), bl, bl2);
        IndexMerger indexMerger2 = Shapes.createIndexMerger(indexMerger.getList().size() - 1, voxelShape.getCoords(Direction.Axis.Y), voxelShape2.getCoords(Direction.Axis.Y), bl, bl2);
        IndexMerger indexMerger3 = Shapes.createIndexMerger((indexMerger.getList().size() - 1) * (indexMerger2.getList().size() - 1), voxelShape.getCoords(Direction.Axis.Z), voxelShape2.getCoords(Direction.Axis.Z), bl, bl2);
        return Shapes.joinIsNotEmpty(indexMerger, indexMerger2, indexMerger3, voxelShape.shape, voxelShape2.shape, booleanOp);
    }

    private static boolean joinIsNotEmpty(IndexMerger indexMerger, IndexMerger indexMerger2, IndexMerger indexMerger3, DiscreteVoxelShape discreteVoxelShape, DiscreteVoxelShape discreteVoxelShape2, BooleanOp booleanOp) {
        return !indexMerger.forMergedIndexes((n, n2, n5) -> indexMerger2.forMergedIndexes((n3, n4, n8) -> indexMerger3.forMergedIndexes((n5, n6, n7) -> !booleanOp.apply(discreteVoxelShape.isFullWide(n, n3, n5), discreteVoxelShape2.isFullWide(n2, n4, n6)))));
    }

    public static double collide(Direction.Axis axis, AABB aABB, Stream<VoxelShape> stream, double d) {
        Iterator iterator = stream.iterator();
        while (iterator.hasNext()) {
            if (Math.abs(d) < 1.0E-7) {
                return 0.0;
            }
            d = ((VoxelShape)iterator.next()).collide(axis, aABB, d);
        }
        return d;
    }

    public static double collide(Direction.Axis axis, AABB aABB, LevelReader levelReader, double d, CollisionContext collisionContext, Stream<VoxelShape> stream) {
        return Shapes.collide(aABB, levelReader, d, collisionContext, AxisCycle.between(axis, Direction.Axis.Z), stream);
    }

    private static double collide(AABB aABB, LevelReader levelReader, double d, CollisionContext collisionContext, AxisCycle axisCycle, Stream<VoxelShape> stream) {
        if (aABB.getXsize() < 1.0E-6 || aABB.getYsize() < 1.0E-6 || aABB.getZsize() < 1.0E-6) {
            return d;
        }
        if (Math.abs(d) < 1.0E-7) {
            return 0.0;
        }
        AxisCycle axisCycle2 = axisCycle.inverse();
        Direction.Axis axis = axisCycle2.cycle(Direction.Axis.X);
        Direction.Axis axis2 = axisCycle2.cycle(Direction.Axis.Y);
        Direction.Axis axis3 = axisCycle2.cycle(Direction.Axis.Z);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        int n = Mth.floor(aABB.min(axis) - 1.0E-7) - 1;
        int n2 = Mth.floor(aABB.max(axis) + 1.0E-7) + 1;
        int n3 = Mth.floor(aABB.min(axis2) - 1.0E-7) - 1;
        int n4 = Mth.floor(aABB.max(axis2) + 1.0E-7) + 1;
        double d2 = aABB.min(axis3) - 1.0E-7;
        double d3 = aABB.max(axis3) + 1.0E-7;
        boolean bl = d > 0.0;
        int n5 = bl ? Mth.floor(aABB.max(axis3) - 1.0E-7) - 1 : Mth.floor(aABB.min(axis3) + 1.0E-7) + 1;
        int n6 = Shapes.lastC(d, d2, d3);
        int n7 = bl ? 1 : -1;
        int n8 = n5;
        while (bl ? n8 <= n6 : n8 >= n6) {
            for (int i = n; i <= n2; ++i) {
                for (int j = n3; j <= n4; ++j) {
                    int n9 = 0;
                    if (i == n || i == n2) {
                        ++n9;
                    }
                    if (j == n3 || j == n4) {
                        ++n9;
                    }
                    if (n8 == n5 || n8 == n6) {
                        ++n9;
                    }
                    if (n9 >= 3) continue;
                    mutableBlockPos.set(axisCycle2, i, j, n8);
                    BlockState blockState = levelReader.getBlockState(mutableBlockPos);
                    if (n9 == 1 && !blockState.hasLargeCollisionShape() || n9 == 2 && !blockState.is(Blocks.MOVING_PISTON)) continue;
                    d = blockState.getCollisionShape(levelReader, mutableBlockPos, collisionContext).collide(axis3, aABB.move(-mutableBlockPos.getX(), -mutableBlockPos.getY(), -mutableBlockPos.getZ()), d);
                    if (Math.abs(d) < 1.0E-7) {
                        return 0.0;
                    }
                    n6 = Shapes.lastC(d, d2, d3);
                }
            }
            n8 += n7;
        }
        double[] arrd = new double[]{d};
        stream.forEach(voxelShape -> {
            arrd[0] = voxelShape.collide(axis3, aABB, arrd[0]);
        });
        return arrd[0];
    }

    private static int lastC(double d, double d2, double d3) {
        return d > 0.0 ? Mth.floor(d3 + d) + 1 : Mth.floor(d2 + d) - 1;
    }

    public static boolean blockOccudes(VoxelShape voxelShape, VoxelShape voxelShape2, Direction direction) {
        if (voxelShape == Shapes.block() && voxelShape2 == Shapes.block()) {
            return true;
        }
        if (voxelShape2.isEmpty()) {
            return false;
        }
        Direction.Axis axis = direction.getAxis();
        Direction.AxisDirection axisDirection = direction.getAxisDirection();
        VoxelShape voxelShape3 = axisDirection == Direction.AxisDirection.POSITIVE ? voxelShape : voxelShape2;
        VoxelShape voxelShape4 = axisDirection == Direction.AxisDirection.POSITIVE ? voxelShape2 : voxelShape;
        BooleanOp booleanOp = axisDirection == Direction.AxisDirection.POSITIVE ? BooleanOp.ONLY_FIRST : BooleanOp.ONLY_SECOND;
        return DoubleMath.fuzzyEquals((double)voxelShape3.max(axis), (double)1.0, (double)1.0E-7) && DoubleMath.fuzzyEquals((double)voxelShape4.min(axis), (double)0.0, (double)1.0E-7) && !Shapes.joinIsNotEmpty(new SliceShape(voxelShape3, axis, voxelShape3.shape.getSize(axis) - 1), new SliceShape(voxelShape4, axis, 0), booleanOp);
    }

    public static VoxelShape getFaceShape(VoxelShape voxelShape, Direction direction) {
        int n;
        boolean bl;
        if (voxelShape == Shapes.block()) {
            return Shapes.block();
        }
        Direction.Axis axis = direction.getAxis();
        if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            bl = DoubleMath.fuzzyEquals((double)voxelShape.max(axis), (double)1.0, (double)1.0E-7);
            n = voxelShape.shape.getSize(axis) - 1;
        } else {
            bl = DoubleMath.fuzzyEquals((double)voxelShape.min(axis), (double)0.0, (double)1.0E-7);
            n = 0;
        }
        if (!bl) {
            return Shapes.empty();
        }
        return new SliceShape(voxelShape, axis, n);
    }

    public static boolean mergedFaceOccludes(VoxelShape voxelShape, VoxelShape voxelShape2, Direction direction) {
        VoxelShape voxelShape3;
        if (voxelShape == Shapes.block() || voxelShape2 == Shapes.block()) {
            return true;
        }
        Direction.Axis axis = direction.getAxis();
        Direction.AxisDirection axisDirection = direction.getAxisDirection();
        VoxelShape voxelShape4 = axisDirection == Direction.AxisDirection.POSITIVE ? voxelShape : voxelShape2;
        VoxelShape voxelShape5 = voxelShape3 = axisDirection == Direction.AxisDirection.POSITIVE ? voxelShape2 : voxelShape;
        if (!DoubleMath.fuzzyEquals((double)voxelShape4.max(axis), (double)1.0, (double)1.0E-7)) {
            voxelShape4 = Shapes.empty();
        }
        if (!DoubleMath.fuzzyEquals((double)voxelShape3.min(axis), (double)0.0, (double)1.0E-7)) {
            voxelShape3 = Shapes.empty();
        }
        return !Shapes.joinIsNotEmpty(Shapes.block(), Shapes.joinUnoptimized(new SliceShape(voxelShape4, axis, voxelShape4.shape.getSize(axis) - 1), new SliceShape(voxelShape3, axis, 0), BooleanOp.OR), BooleanOp.ONLY_FIRST);
    }

    public static boolean faceShapeOccludes(VoxelShape voxelShape, VoxelShape voxelShape2) {
        if (voxelShape == Shapes.block() || voxelShape2 == Shapes.block()) {
            return true;
        }
        if (voxelShape.isEmpty() && voxelShape2.isEmpty()) {
            return false;
        }
        return !Shapes.joinIsNotEmpty(Shapes.block(), Shapes.joinUnoptimized(voxelShape, voxelShape2, BooleanOp.OR), BooleanOp.ONLY_FIRST);
    }

    @VisibleForTesting
    protected static IndexMerger createIndexMerger(int n, DoubleList doubleList, DoubleList doubleList2, boolean bl, boolean bl2) {
        long l;
        int n2 = doubleList.size() - 1;
        int n3 = doubleList2.size() - 1;
        if (doubleList instanceof CubePointRange && doubleList2 instanceof CubePointRange && (long)n * (l = Shapes.lcm(n2, n3)) <= 256L) {
            return new DiscreteCubeMerger(n2, n3);
        }
        if (doubleList.getDouble(n2) < doubleList2.getDouble(0) - 1.0E-7) {
            return new NonOverlappingMerger(doubleList, doubleList2, false);
        }
        if (doubleList2.getDouble(n3) < doubleList.getDouble(0) - 1.0E-7) {
            return new NonOverlappingMerger(doubleList2, doubleList, true);
        }
        if (n2 == n3 && Objects.equals((Object)doubleList, (Object)doubleList2)) {
            if (doubleList instanceof IdenticalMerger) {
                return (IndexMerger)doubleList;
            }
            if (doubleList2 instanceof IdenticalMerger) {
                return (IndexMerger)doubleList2;
            }
            return new IdenticalMerger(doubleList);
        }
        return new IndirectMerger(doubleList, doubleList2, bl, bl2);
    }

    public static interface DoubleLineConsumer {
        public void consume(double var1, double var3, double var5, double var7, double var9, double var11);
    }

}

