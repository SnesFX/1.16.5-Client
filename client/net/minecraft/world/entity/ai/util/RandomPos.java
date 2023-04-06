/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.util;

import java.util.Random;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class RandomPos {
    @Nullable
    public static Vec3 getPos(PathfinderMob pathfinderMob, int n, int n2) {
        return RandomPos.generateRandomPos(pathfinderMob, n, n2, 0, null, true, 1.5707963705062866, pathfinderMob::getWalkTargetValue, false, 0, 0, true);
    }

    @Nullable
    public static Vec3 getAirPos(PathfinderMob pathfinderMob, int n, int n2, int n3, @Nullable Vec3 vec3, double d) {
        return RandomPos.generateRandomPos(pathfinderMob, n, n2, n3, vec3, true, d, pathfinderMob::getWalkTargetValue, true, 0, 0, false);
    }

    @Nullable
    public static Vec3 getLandPos(PathfinderMob pathfinderMob, int n, int n2) {
        return RandomPos.getLandPos(pathfinderMob, n, n2, pathfinderMob::getWalkTargetValue);
    }

    @Nullable
    public static Vec3 getLandPos(PathfinderMob pathfinderMob, int n, int n2, ToDoubleFunction<BlockPos> toDoubleFunction) {
        return RandomPos.generateRandomPos(pathfinderMob, n, n2, 0, null, false, 0.0, toDoubleFunction, true, 0, 0, true);
    }

    @Nullable
    public static Vec3 getAboveLandPos(PathfinderMob pathfinderMob, int n, int n2, Vec3 vec3, float f, int n3, int n4) {
        return RandomPos.generateRandomPos(pathfinderMob, n, n2, 0, vec3, false, f, pathfinderMob::getWalkTargetValue, true, n3, n4, true);
    }

    @Nullable
    public static Vec3 getLandPosTowards(PathfinderMob pathfinderMob, int n, int n2, Vec3 vec3) {
        Vec3 vec32 = vec3.subtract(pathfinderMob.getX(), pathfinderMob.getY(), pathfinderMob.getZ());
        return RandomPos.generateRandomPos(pathfinderMob, n, n2, 0, vec32, false, 1.5707963705062866, pathfinderMob::getWalkTargetValue, true, 0, 0, true);
    }

    @Nullable
    public static Vec3 getPosTowards(PathfinderMob pathfinderMob, int n, int n2, Vec3 vec3) {
        Vec3 vec32 = vec3.subtract(pathfinderMob.getX(), pathfinderMob.getY(), pathfinderMob.getZ());
        return RandomPos.generateRandomPos(pathfinderMob, n, n2, 0, vec32, true, 1.5707963705062866, pathfinderMob::getWalkTargetValue, false, 0, 0, true);
    }

    @Nullable
    public static Vec3 getPosTowards(PathfinderMob pathfinderMob, int n, int n2, Vec3 vec3, double d) {
        Vec3 vec32 = vec3.subtract(pathfinderMob.getX(), pathfinderMob.getY(), pathfinderMob.getZ());
        return RandomPos.generateRandomPos(pathfinderMob, n, n2, 0, vec32, true, d, pathfinderMob::getWalkTargetValue, false, 0, 0, true);
    }

    @Nullable
    public static Vec3 getAirPosTowards(PathfinderMob pathfinderMob, int n, int n2, int n3, Vec3 vec3, double d) {
        Vec3 vec32 = vec3.subtract(pathfinderMob.getX(), pathfinderMob.getY(), pathfinderMob.getZ());
        return RandomPos.generateRandomPos(pathfinderMob, n, n2, n3, vec32, false, d, pathfinderMob::getWalkTargetValue, true, 0, 0, false);
    }

    @Nullable
    public static Vec3 getPosAvoid(PathfinderMob pathfinderMob, int n, int n2, Vec3 vec3) {
        Vec3 vec32 = pathfinderMob.position().subtract(vec3);
        return RandomPos.generateRandomPos(pathfinderMob, n, n2, 0, vec32, true, 1.5707963705062866, pathfinderMob::getWalkTargetValue, false, 0, 0, true);
    }

    @Nullable
    public static Vec3 getLandPosAvoid(PathfinderMob pathfinderMob, int n, int n2, Vec3 vec3) {
        Vec3 vec32 = pathfinderMob.position().subtract(vec3);
        return RandomPos.generateRandomPos(pathfinderMob, n, n2, 0, vec32, false, 1.5707963705062866, pathfinderMob::getWalkTargetValue, true, 0, 0, true);
    }

    @Nullable
    private static Vec3 generateRandomPos(PathfinderMob pathfinderMob, int n, int n2, int n3, @Nullable Vec3 vec3, boolean bl, double d, ToDoubleFunction<BlockPos> toDoubleFunction, boolean bl2, int n4, int n5, boolean bl3) {
        PathNavigation pathNavigation = pathfinderMob.getNavigation();
        Random random = pathfinderMob.getRandom();
        boolean bl4 = pathfinderMob.hasRestriction() ? pathfinderMob.getRestrictCenter().closerThan(pathfinderMob.position(), (double)(pathfinderMob.getRestrictRadius() + (float)n) + 1.0) : false;
        boolean bl5 = false;
        double d2 = Double.NEGATIVE_INFINITY;
        BlockPos blockPos2 = pathfinderMob.blockPosition();
        for (int i = 0; i < 10; ++i) {
            BlockPathTypes blockPathTypes;
            BlockPos blockPos3;
            double d3;
            BlockPos blockPos4 = RandomPos.getRandomDelta(random, n, n2, n3, vec3, d);
            if (blockPos4 == null) continue;
            int n6 = blockPos4.getX();
            int n7 = blockPos4.getY();
            int n8 = blockPos4.getZ();
            if (pathfinderMob.hasRestriction() && n > 1) {
                blockPos3 = pathfinderMob.getRestrictCenter();
                n6 = pathfinderMob.getX() > (double)blockPos3.getX() ? (n6 -= random.nextInt(n / 2)) : (n6 += random.nextInt(n / 2));
                n8 = pathfinderMob.getZ() > (double)blockPos3.getZ() ? (n8 -= random.nextInt(n / 2)) : (n8 += random.nextInt(n / 2));
            }
            if ((blockPos3 = new BlockPos((double)n6 + pathfinderMob.getX(), (double)n7 + pathfinderMob.getY(), (double)n8 + pathfinderMob.getZ())).getY() < 0 || blockPos3.getY() > pathfinderMob.level.getMaxBuildHeight() || bl4 && !pathfinderMob.isWithinRestriction(blockPos3) || bl3 && !pathNavigation.isStableDestination(blockPos3)) continue;
            if (bl2) {
                blockPos3 = RandomPos.moveUpToAboveSolid(blockPos3, random.nextInt(n4 + 1) + n5, pathfinderMob.level.getMaxBuildHeight(), blockPos -> pathfinderMob.level.getBlockState((BlockPos)blockPos).getMaterial().isSolid());
            }
            if (!bl && pathfinderMob.level.getFluidState(blockPos3).is(FluidTags.WATER) || pathfinderMob.getPathfindingMalus(blockPathTypes = WalkNodeEvaluator.getBlockPathTypeStatic(pathfinderMob.level, blockPos3.mutable())) != 0.0f || !((d3 = toDoubleFunction.applyAsDouble(blockPos3)) > d2)) continue;
            d2 = d3;
            blockPos2 = blockPos3;
            bl5 = true;
        }
        if (bl5) {
            return Vec3.atBottomCenterOf(blockPos2);
        }
        return null;
    }

    @Nullable
    private static BlockPos getRandomDelta(Random random, int n, int n2, int n3, @Nullable Vec3 vec3, double d) {
        if (vec3 == null || d >= 3.141592653589793) {
            int n4 = random.nextInt(2 * n + 1) - n;
            int n5 = random.nextInt(2 * n2 + 1) - n2 + n3;
            int n6 = random.nextInt(2 * n + 1) - n;
            return new BlockPos(n4, n5, n6);
        }
        double d2 = Mth.atan2(vec3.z, vec3.x) - 1.5707963705062866;
        double d3 = d2 + (double)(2.0f * random.nextFloat() - 1.0f) * d;
        double d4 = Math.sqrt(random.nextDouble()) * (double)Mth.SQRT_OF_TWO * (double)n;
        double d5 = -d4 * Math.sin(d3);
        double d6 = d4 * Math.cos(d3);
        if (Math.abs(d5) > (double)n || Math.abs(d6) > (double)n) {
            return null;
        }
        int n7 = random.nextInt(2 * n2 + 1) - n2 + n3;
        return new BlockPos(d5, (double)n7, d6);
    }

    static BlockPos moveUpToAboveSolid(BlockPos blockPos, int n, int n2, Predicate<BlockPos> predicate) {
        if (n < 0) {
            throw new IllegalArgumentException("aboveSolidAmount was " + n + ", expected >= 0");
        }
        if (predicate.test(blockPos)) {
            BlockPos blockPos2;
            BlockPos blockPos3 = blockPos.above();
            while (blockPos3.getY() < n2 && predicate.test(blockPos3)) {
                blockPos3 = blockPos3.above();
            }
            BlockPos blockPos4 = blockPos3;
            while (blockPos4.getY() < n2 && blockPos4.getY() - blockPos3.getY() < n && !predicate.test(blockPos2 = blockPos4.above())) {
                blockPos4 = blockPos2;
            }
            return blockPos4;
        }
        return blockPos;
    }
}

