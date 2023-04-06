/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.pathfinder;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.Target;
import net.minecraft.world.phys.AABB;

public class SwimNodeEvaluator
extends NodeEvaluator {
    private final boolean allowBreaching;

    public SwimNodeEvaluator(boolean bl) {
        this.allowBreaching = bl;
    }

    @Override
    public Node getStart() {
        return super.getNode(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5), Mth.floor(this.mob.getBoundingBox().minZ));
    }

    @Override
    public Target getGoal(double d, double d2, double d3) {
        return new Target(super.getNode(Mth.floor(d - (double)(this.mob.getBbWidth() / 2.0f)), Mth.floor(d2 + 0.5), Mth.floor(d3 - (double)(this.mob.getBbWidth() / 2.0f))));
    }

    @Override
    public int getNeighbors(Node[] arrnode, Node node) {
        int n = 0;
        for (Direction direction : Direction.values()) {
            Node node2 = this.getWaterNode(node.x + direction.getStepX(), node.y + direction.getStepY(), node.z + direction.getStepZ());
            if (node2 == null || node2.closed) continue;
            arrnode[n++] = node2;
        }
        return n;
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter blockGetter, int n, int n2, int n3, Mob mob, int n4, int n5, int n6, boolean bl, boolean bl2) {
        return this.getBlockPathType(blockGetter, n, n2, n3);
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter blockGetter, int n, int n2, int n3) {
        BlockPos blockPos = new BlockPos(n, n2, n3);
        FluidState fluidState = blockGetter.getFluidState(blockPos);
        BlockState blockState = blockGetter.getBlockState(blockPos);
        if (fluidState.isEmpty() && blockState.isPathfindable(blockGetter, blockPos.below(), PathComputationType.WATER) && blockState.isAir()) {
            return BlockPathTypes.BREACH;
        }
        if (!fluidState.is(FluidTags.WATER) || !blockState.isPathfindable(blockGetter, blockPos, PathComputationType.WATER)) {
            return BlockPathTypes.BLOCKED;
        }
        return BlockPathTypes.WATER;
    }

    @Nullable
    private Node getWaterNode(int n, int n2, int n3) {
        BlockPathTypes blockPathTypes = this.isFree(n, n2, n3);
        if (this.allowBreaching && blockPathTypes == BlockPathTypes.BREACH || blockPathTypes == BlockPathTypes.WATER) {
            return this.getNode(n, n2, n3);
        }
        return null;
    }

    @Nullable
    @Override
    protected Node getNode(int n, int n2, int n3) {
        Node node = null;
        BlockPathTypes blockPathTypes = this.getBlockPathType(this.mob.level, n, n2, n3);
        float f = this.mob.getPathfindingMalus(blockPathTypes);
        if (f >= 0.0f) {
            node = super.getNode(n, n2, n3);
            node.type = blockPathTypes;
            node.costMalus = Math.max(node.costMalus, f);
            if (this.level.getFluidState(new BlockPos(n, n2, n3)).isEmpty()) {
                node.costMalus += 8.0f;
            }
        }
        if (blockPathTypes == BlockPathTypes.OPEN) {
            return node;
        }
        return node;
    }

    private BlockPathTypes isFree(int n, int n2, int n3) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = n; i < n + this.entityWidth; ++i) {
            for (int j = n2; j < n2 + this.entityHeight; ++j) {
                for (int k = n3; k < n3 + this.entityDepth; ++k) {
                    FluidState fluidState = this.level.getFluidState(mutableBlockPos.set(i, j, k));
                    BlockState blockState = this.level.getBlockState(mutableBlockPos.set(i, j, k));
                    if (fluidState.isEmpty() && blockState.isPathfindable(this.level, (BlockPos)mutableBlockPos.below(), PathComputationType.WATER) && blockState.isAir()) {
                        return BlockPathTypes.BREACH;
                    }
                    if (fluidState.is(FluidTags.WATER)) continue;
                    return BlockPathTypes.BLOCKED;
                }
            }
        }
        BlockState blockState = this.level.getBlockState(mutableBlockPos);
        if (blockState.isPathfindable(this.level, mutableBlockPos, PathComputationType.WATER)) {
            return BlockPathTypes.WATER;
        }
        return BlockPathTypes.BLOCKED;
    }
}

