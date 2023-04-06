/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class GroundPathNavigation
extends PathNavigation {
    private boolean avoidSun;

    public GroundPathNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int n) {
        this.nodeEvaluator = new WalkNodeEvaluator();
        this.nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(this.nodeEvaluator, n);
    }

    @Override
    protected boolean canUpdatePath() {
        return this.mob.isOnGround() || this.isInLiquid() || this.mob.isPassenger();
    }

    @Override
    protected Vec3 getTempMobPos() {
        return new Vec3(this.mob.getX(), this.getSurfaceY(), this.mob.getZ());
    }

    @Override
    public Path createPath(BlockPos blockPos, int n) {
        BlockPos blockPos2;
        if (this.level.getBlockState(blockPos).isAir()) {
            blockPos2 = blockPos.below();
            while (blockPos2.getY() > 0 && this.level.getBlockState(blockPos2).isAir()) {
                blockPos2 = blockPos2.below();
            }
            if (blockPos2.getY() > 0) {
                return super.createPath(blockPos2.above(), n);
            }
            while (blockPos2.getY() < this.level.getMaxBuildHeight() && this.level.getBlockState(blockPos2).isAir()) {
                blockPos2 = blockPos2.above();
            }
            blockPos = blockPos2;
        }
        if (this.level.getBlockState(blockPos).getMaterial().isSolid()) {
            blockPos2 = blockPos.above();
            while (blockPos2.getY() < this.level.getMaxBuildHeight() && this.level.getBlockState(blockPos2).getMaterial().isSolid()) {
                blockPos2 = blockPos2.above();
            }
            return super.createPath(blockPos2, n);
        }
        return super.createPath(blockPos, n);
    }

    @Override
    public Path createPath(Entity entity, int n) {
        return this.createPath(entity.blockPosition(), n);
    }

    private int getSurfaceY() {
        if (!this.mob.isInWater() || !this.canFloat()) {
            return Mth.floor(this.mob.getY() + 0.5);
        }
        int n = Mth.floor(this.mob.getY());
        Block block = this.level.getBlockState(new BlockPos(this.mob.getX(), (double)n, this.mob.getZ())).getBlock();
        int n2 = 0;
        while (block == Blocks.WATER) {
            block = this.level.getBlockState(new BlockPos(this.mob.getX(), (double)(++n), this.mob.getZ())).getBlock();
            if (++n2 <= 16) continue;
            return Mth.floor(this.mob.getY());
        }
        return n;
    }

    @Override
    protected void trimPath() {
        super.trimPath();
        if (this.avoidSun) {
            if (this.level.canSeeSky(new BlockPos(this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ()))) {
                return;
            }
            for (int i = 0; i < this.path.getNodeCount(); ++i) {
                Node node = this.path.getNode(i);
                if (!this.level.canSeeSky(new BlockPos(node.x, node.y, node.z))) continue;
                this.path.truncateNodes(i);
                return;
            }
        }
    }

    @Override
    protected boolean canMoveDirectly(Vec3 vec3, Vec3 vec32, int n, int n2, int n3) {
        int n4 = Mth.floor(vec3.x);
        int n5 = Mth.floor(vec3.z);
        double d = vec32.x - vec3.x;
        double d2 = vec32.z - vec3.z;
        double d3 = d * d + d2 * d2;
        if (d3 < 1.0E-8) {
            return false;
        }
        double d4 = 1.0 / Math.sqrt(d3);
        if (!this.canWalkOn(n4, Mth.floor(vec3.y), n5, n += 2, n2, n3 += 2, vec3, d *= d4, d2 *= d4)) {
            return false;
        }
        n -= 2;
        n3 -= 2;
        double d5 = 1.0 / Math.abs(d);
        double d6 = 1.0 / Math.abs(d2);
        double d7 = (double)n4 - vec3.x;
        double d8 = (double)n5 - vec3.z;
        if (d >= 0.0) {
            d7 += 1.0;
        }
        if (d2 >= 0.0) {
            d8 += 1.0;
        }
        d7 /= d;
        d8 /= d2;
        int n6 = d < 0.0 ? -1 : 1;
        int n7 = d2 < 0.0 ? -1 : 1;
        int n8 = Mth.floor(vec32.x);
        int n9 = Mth.floor(vec32.z);
        int n10 = n8 - n4;
        int n11 = n9 - n5;
        while (n10 * n6 > 0 || n11 * n7 > 0) {
            if (d7 < d8) {
                d7 += d5;
                n10 = n8 - (n4 += n6);
            } else {
                d8 += d6;
                n11 = n9 - (n5 += n7);
            }
            if (this.canWalkOn(n4, Mth.floor(vec3.y), n5, n, n2, n3, vec3, d, d2)) continue;
            return false;
        }
        return true;
    }

    private boolean canWalkOn(int n, int n2, int n3, int n4, int n5, int n6, Vec3 vec3, double d, double d2) {
        int n7 = n - n4 / 2;
        int n8 = n3 - n6 / 2;
        if (!this.canWalkAbove(n7, n2, n8, n4, n5, n6, vec3, d, d2)) {
            return false;
        }
        for (int i = n7; i < n7 + n4; ++i) {
            for (int j = n8; j < n8 + n6; ++j) {
                double d3 = (double)i + 0.5 - vec3.x;
                double d4 = (double)j + 0.5 - vec3.z;
                if (d3 * d + d4 * d2 < 0.0) continue;
                BlockPathTypes blockPathTypes = this.nodeEvaluator.getBlockPathType(this.level, i, n2 - 1, j, this.mob, n4, n5, n6, true, true);
                if (!this.hasValidPathType(blockPathTypes)) {
                    return false;
                }
                blockPathTypes = this.nodeEvaluator.getBlockPathType(this.level, i, n2, j, this.mob, n4, n5, n6, true, true);
                float f = this.mob.getPathfindingMalus(blockPathTypes);
                if (f < 0.0f || f >= 8.0f) {
                    return false;
                }
                if (blockPathTypes != BlockPathTypes.DAMAGE_FIRE && blockPathTypes != BlockPathTypes.DANGER_FIRE && blockPathTypes != BlockPathTypes.DAMAGE_OTHER) continue;
                return false;
            }
        }
        return true;
    }

    protected boolean hasValidPathType(BlockPathTypes blockPathTypes) {
        if (blockPathTypes == BlockPathTypes.WATER) {
            return false;
        }
        if (blockPathTypes == BlockPathTypes.LAVA) {
            return false;
        }
        return blockPathTypes != BlockPathTypes.OPEN;
    }

    private boolean canWalkAbove(int n, int n2, int n3, int n4, int n5, int n6, Vec3 vec3, double d, double d2) {
        for (BlockPos blockPos : BlockPos.betweenClosed(new BlockPos(n, n2, n3), new BlockPos(n + n4 - 1, n2 + n5 - 1, n3 + n6 - 1))) {
            double d3;
            double d4 = (double)blockPos.getX() + 0.5 - vec3.x;
            if (d4 * d + (d3 = (double)blockPos.getZ() + 0.5 - vec3.z) * d2 < 0.0 || this.level.getBlockState(blockPos).isPathfindable(this.level, blockPos, PathComputationType.LAND)) continue;
            return false;
        }
        return true;
    }

    public void setCanOpenDoors(boolean bl) {
        this.nodeEvaluator.setCanOpenDoors(bl);
    }

    public boolean canOpenDoors() {
        return this.nodeEvaluator.canPassDoors();
    }

    public void setAvoidSun(boolean bl) {
        this.avoidSun = bl;
    }
}

