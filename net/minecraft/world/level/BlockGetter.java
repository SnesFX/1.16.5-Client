/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface BlockGetter {
    @Nullable
    public BlockEntity getBlockEntity(BlockPos var1);

    public BlockState getBlockState(BlockPos var1);

    public FluidState getFluidState(BlockPos var1);

    default public int getLightEmission(BlockPos blockPos) {
        return this.getBlockState(blockPos).getLightEmission();
    }

    default public int getMaxLightLevel() {
        return 15;
    }

    default public int getMaxBuildHeight() {
        return 256;
    }

    default public Stream<BlockState> getBlockStates(AABB aABB) {
        return BlockPos.betweenClosedStream(aABB).map(this::getBlockState);
    }

    default public BlockHitResult clip(ClipContext clipContext2) {
        return BlockGetter.traverseBlocks(clipContext2, (clipContext, blockPos) -> {
            BlockState blockState = this.getBlockState((BlockPos)blockPos);
            FluidState fluidState = this.getFluidState((BlockPos)blockPos);
            Vec3 vec3 = clipContext.getFrom();
            Vec3 vec32 = clipContext.getTo();
            VoxelShape voxelShape = clipContext.getBlockShape(blockState, this, (BlockPos)blockPos);
            BlockHitResult blockHitResult = this.clipWithInteractionOverride(vec3, vec32, (BlockPos)blockPos, voxelShape, blockState);
            VoxelShape voxelShape2 = clipContext.getFluidShape(fluidState, this, (BlockPos)blockPos);
            BlockHitResult blockHitResult2 = voxelShape2.clip(vec3, vec32, (BlockPos)blockPos);
            double d = blockHitResult == null ? Double.MAX_VALUE : clipContext.getFrom().distanceToSqr(blockHitResult.getLocation());
            double d2 = blockHitResult2 == null ? Double.MAX_VALUE : clipContext.getFrom().distanceToSqr(blockHitResult2.getLocation());
            return d <= d2 ? blockHitResult : blockHitResult2;
        }, clipContext -> {
            Vec3 vec3 = clipContext.getFrom().subtract(clipContext.getTo());
            return BlockHitResult.miss(clipContext.getTo(), Direction.getNearest(vec3.x, vec3.y, vec3.z), new BlockPos(clipContext.getTo()));
        });
    }

    @Nullable
    default public BlockHitResult clipWithInteractionOverride(Vec3 vec3, Vec3 vec32, BlockPos blockPos, VoxelShape voxelShape, BlockState blockState) {
        BlockHitResult blockHitResult;
        BlockHitResult blockHitResult2 = voxelShape.clip(vec3, vec32, blockPos);
        if (blockHitResult2 != null && (blockHitResult = blockState.getInteractionShape(this, blockPos).clip(vec3, vec32, blockPos)) != null && blockHitResult.getLocation().subtract(vec3).lengthSqr() < blockHitResult2.getLocation().subtract(vec3).lengthSqr()) {
            return blockHitResult2.withDirection(blockHitResult.getDirection());
        }
        return blockHitResult2;
    }

    default public double getBlockFloorHeight(VoxelShape voxelShape, Supplier<VoxelShape> supplier) {
        if (!voxelShape.isEmpty()) {
            return voxelShape.max(Direction.Axis.Y);
        }
        double d = supplier.get().max(Direction.Axis.Y);
        if (d >= 1.0) {
            return d - 1.0;
        }
        return Double.NEGATIVE_INFINITY;
    }

    default public double getBlockFloorHeight(BlockPos blockPos) {
        return this.getBlockFloorHeight(this.getBlockState(blockPos).getCollisionShape(this, blockPos), () -> {
            BlockPos blockPos2 = blockPos.below();
            return this.getBlockState(blockPos2).getCollisionShape(this, blockPos2);
        });
    }

    public static <T> T traverseBlocks(ClipContext clipContext, BiFunction<ClipContext, BlockPos, T> biFunction, Function<ClipContext, T> function) {
        int n;
        Vec3 vec3;
        int n2;
        Vec3 vec32 = clipContext.getFrom();
        if (vec32.equals(vec3 = clipContext.getTo())) {
            return function.apply(clipContext);
        }
        double d = Mth.lerp(-1.0E-7, vec3.x, vec32.x);
        double d2 = Mth.lerp(-1.0E-7, vec3.y, vec32.y);
        double d3 = Mth.lerp(-1.0E-7, vec3.z, vec32.z);
        double d4 = Mth.lerp(-1.0E-7, vec32.x, vec3.x);
        double d5 = Mth.lerp(-1.0E-7, vec32.y, vec3.y);
        double d6 = Mth.lerp(-1.0E-7, vec32.z, vec3.z);
        int n3 = Mth.floor(d4);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(n3, n = Mth.floor(d5), n2 = Mth.floor(d6));
        T t = biFunction.apply(clipContext, mutableBlockPos);
        if (t != null) {
            return t;
        }
        double d7 = d - d4;
        double d8 = d2 - d5;
        double d9 = d3 - d6;
        int n4 = Mth.sign(d7);
        int n5 = Mth.sign(d8);
        int n6 = Mth.sign(d9);
        double d10 = n4 == 0 ? Double.MAX_VALUE : (double)n4 / d7;
        double d11 = n5 == 0 ? Double.MAX_VALUE : (double)n5 / d8;
        double d12 = n6 == 0 ? Double.MAX_VALUE : (double)n6 / d9;
        double d13 = d10 * (n4 > 0 ? 1.0 - Mth.frac(d4) : Mth.frac(d4));
        double d14 = d11 * (n5 > 0 ? 1.0 - Mth.frac(d5) : Mth.frac(d5));
        double d15 = d12 * (n6 > 0 ? 1.0 - Mth.frac(d6) : Mth.frac(d6));
        while (d13 <= 1.0 || d14 <= 1.0 || d15 <= 1.0) {
            T t2;
            if (d13 < d14) {
                if (d13 < d15) {
                    n3 += n4;
                    d13 += d10;
                } else {
                    n2 += n6;
                    d15 += d12;
                }
            } else if (d14 < d15) {
                n += n5;
                d14 += d11;
            } else {
                n2 += n6;
                d15 += d12;
            }
            if ((t2 = biFunction.apply(clipContext, mutableBlockPos.set(n3, n, n2))) == null) continue;
            return t2;
        }
        return function.apply(clipContext);
    }
}

