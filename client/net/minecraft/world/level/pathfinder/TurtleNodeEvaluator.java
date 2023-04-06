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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Target;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TurtleNodeEvaluator
extends WalkNodeEvaluator {
    private float oldWalkableCost;
    private float oldWaterBorderCost;

    @Override
    public void prepare(PathNavigationRegion pathNavigationRegion, Mob mob) {
        super.prepare(pathNavigationRegion, mob);
        mob.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
        this.oldWalkableCost = mob.getPathfindingMalus(BlockPathTypes.WALKABLE);
        mob.setPathfindingMalus(BlockPathTypes.WALKABLE, 6.0f);
        this.oldWaterBorderCost = mob.getPathfindingMalus(BlockPathTypes.WATER_BORDER);
        mob.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 4.0f);
    }

    @Override
    public void done() {
        this.mob.setPathfindingMalus(BlockPathTypes.WALKABLE, this.oldWalkableCost);
        this.mob.setPathfindingMalus(BlockPathTypes.WATER_BORDER, this.oldWaterBorderCost);
        super.done();
    }

    @Override
    public Node getStart() {
        return this.getNode(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5), Mth.floor(this.mob.getBoundingBox().minZ));
    }

    @Override
    public Target getGoal(double d, double d2, double d3) {
        return new Target(this.getNode(Mth.floor(d), Mth.floor(d2 + 0.5), Mth.floor(d3)));
    }

    @Override
    public int getNeighbors(Node[] arrnode, Node node) {
        boolean bl;
        Node node2;
        int n = 0;
        boolean bl2 = true;
        BlockPos blockPos = new BlockPos(node.x, node.y, node.z);
        double d = this.inWaterDependentPosHeight(blockPos);
        Node node3 = this.getAcceptedNode(node.x, node.y, node.z + 1, 1, d);
        Node node4 = this.getAcceptedNode(node.x - 1, node.y, node.z, 1, d);
        Node node5 = this.getAcceptedNode(node.x + 1, node.y, node.z, 1, d);
        Node node6 = this.getAcceptedNode(node.x, node.y, node.z - 1, 1, d);
        Node node7 = this.getAcceptedNode(node.x, node.y + 1, node.z, 0, d);
        Node node8 = this.getAcceptedNode(node.x, node.y - 1, node.z, 1, d);
        if (node3 != null && !node3.closed) {
            arrnode[n++] = node3;
        }
        if (node4 != null && !node4.closed) {
            arrnode[n++] = node4;
        }
        if (node5 != null && !node5.closed) {
            arrnode[n++] = node5;
        }
        if (node6 != null && !node6.closed) {
            arrnode[n++] = node6;
        }
        if (node7 != null && !node7.closed) {
            arrnode[n++] = node7;
        }
        if (node8 != null && !node8.closed) {
            arrnode[n++] = node8;
        }
        boolean bl3 = node6 == null || node6.type == BlockPathTypes.OPEN || node6.costMalus != 0.0f;
        boolean bl4 = node3 == null || node3.type == BlockPathTypes.OPEN || node3.costMalus != 0.0f;
        boolean bl5 = node5 == null || node5.type == BlockPathTypes.OPEN || node5.costMalus != 0.0f;
        boolean bl6 = bl = node4 == null || node4.type == BlockPathTypes.OPEN || node4.costMalus != 0.0f;
        if (bl3 && bl && (node2 = this.getAcceptedNode(node.x - 1, node.y, node.z - 1, 1, d)) != null && !node2.closed) {
            arrnode[n++] = node2;
        }
        if (bl3 && bl5 && (node2 = this.getAcceptedNode(node.x + 1, node.y, node.z - 1, 1, d)) != null && !node2.closed) {
            arrnode[n++] = node2;
        }
        if (bl4 && bl && (node2 = this.getAcceptedNode(node.x - 1, node.y, node.z + 1, 1, d)) != null && !node2.closed) {
            arrnode[n++] = node2;
        }
        if (bl4 && bl5 && (node2 = this.getAcceptedNode(node.x + 1, node.y, node.z + 1, 1, d)) != null && !node2.closed) {
            arrnode[n++] = node2;
        }
        return n;
    }

    private double inWaterDependentPosHeight(BlockPos blockPos) {
        if (!this.mob.isInWater()) {
            BlockPos blockPos2;
            VoxelShape voxelShape;
            return (double)blockPos2.getY() + ((voxelShape = this.level.getBlockState(blockPos2 = blockPos.below()).getCollisionShape(this.level, blockPos2)).isEmpty() ? 0.0 : voxelShape.max(Direction.Axis.Y));
        }
        return (double)blockPos.getY() + 0.5;
    }

    @Nullable
    private Node getAcceptedNode(int n, int n2, int n3, int n4, double d) {
        Node node = null;
        BlockPos blockPos = new BlockPos(n, n2, n3);
        double d2 = this.inWaterDependentPosHeight(blockPos);
        if (d2 - d > 1.125) {
            return null;
        }
        BlockPathTypes blockPathTypes = this.getBlockPathType(this.level, n, n2, n3, this.mob, this.entityWidth, this.entityHeight, this.entityDepth, false, false);
        float f = this.mob.getPathfindingMalus(blockPathTypes);
        double d3 = (double)this.mob.getBbWidth() / 2.0;
        if (f >= 0.0f) {
            node = this.getNode(n, n2, n3);
            node.type = blockPathTypes;
            node.costMalus = Math.max(node.costMalus, f);
        }
        if (blockPathTypes == BlockPathTypes.WATER || blockPathTypes == BlockPathTypes.WALKABLE) {
            if (n2 < this.mob.level.getSeaLevel() - 10 && node != null) {
                node.costMalus += 1.0f;
            }
            return node;
        }
        if (node == null && n4 > 0 && blockPathTypes != BlockPathTypes.FENCE && blockPathTypes != BlockPathTypes.UNPASSABLE_RAIL && blockPathTypes != BlockPathTypes.TRAPDOOR) {
            node = this.getAcceptedNode(n, n2 + 1, n3, n4 - 1, d);
        }
        if (blockPathTypes == BlockPathTypes.OPEN) {
            AABB aABB = new AABB((double)n - d3 + 0.5, (double)n2 + 0.001, (double)n3 - d3 + 0.5, (double)n + d3 + 0.5, (float)n2 + this.mob.getBbHeight(), (double)n3 + d3 + 0.5);
            if (!this.mob.level.noCollision(this.mob, aABB)) {
                return null;
            }
            BlockPathTypes blockPathTypes2 = this.getBlockPathType(this.level, n, n2 - 1, n3, this.mob, this.entityWidth, this.entityHeight, this.entityDepth, false, false);
            if (blockPathTypes2 == BlockPathTypes.BLOCKED) {
                node = this.getNode(n, n2, n3);
                node.type = BlockPathTypes.WALKABLE;
                node.costMalus = Math.max(node.costMalus, f);
                return node;
            }
            if (blockPathTypes2 == BlockPathTypes.WATER) {
                node = this.getNode(n, n2, n3);
                node.type = BlockPathTypes.WATER;
                node.costMalus = Math.max(node.costMalus, f);
                return node;
            }
            int n5 = 0;
            while (n2 > 0 && blockPathTypes == BlockPathTypes.OPEN) {
                --n2;
                if (n5++ >= this.mob.getMaxFallDistance()) {
                    return null;
                }
                blockPathTypes = this.getBlockPathType(this.level, n, n2, n3, this.mob, this.entityWidth, this.entityHeight, this.entityDepth, false, false);
                f = this.mob.getPathfindingMalus(blockPathTypes);
                if (blockPathTypes != BlockPathTypes.OPEN && f >= 0.0f) {
                    node = this.getNode(n, n2, n3);
                    node.type = blockPathTypes;
                    node.costMalus = Math.max(node.costMalus, f);
                    break;
                }
                if (!(f < 0.0f)) continue;
                return null;
            }
        }
        return node;
    }

    @Override
    protected BlockPathTypes evaluateBlockPathType(BlockGetter blockGetter, boolean bl, boolean bl2, BlockPos blockPos, BlockPathTypes blockPathTypes) {
        if (blockPathTypes == BlockPathTypes.RAIL && !(blockGetter.getBlockState(blockPos).getBlock() instanceof BaseRailBlock) && !(blockGetter.getBlockState(blockPos.below()).getBlock() instanceof BaseRailBlock)) {
            blockPathTypes = BlockPathTypes.UNPASSABLE_RAIL;
        }
        if (blockPathTypes == BlockPathTypes.DOOR_OPEN || blockPathTypes == BlockPathTypes.DOOR_WOOD_CLOSED || blockPathTypes == BlockPathTypes.DOOR_IRON_CLOSED) {
            blockPathTypes = BlockPathTypes.BLOCKED;
        }
        if (blockPathTypes == BlockPathTypes.LEAVES) {
            blockPathTypes = BlockPathTypes.BLOCKED;
        }
        return blockPathTypes;
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter blockGetter, int n, int n2, int n3) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        BlockPathTypes blockPathTypes = TurtleNodeEvaluator.getBlockPathTypeRaw(blockGetter, mutableBlockPos.set(n, n2, n3));
        if (blockPathTypes == BlockPathTypes.WATER) {
            for (Direction direction : Direction.values()) {
                BlockPathTypes blockPathTypes2 = TurtleNodeEvaluator.getBlockPathTypeRaw(blockGetter, mutableBlockPos.set(n, n2, n3).move(direction));
                if (blockPathTypes2 != BlockPathTypes.BLOCKED) continue;
                return BlockPathTypes.WATER_BORDER;
            }
            return BlockPathTypes.WATER;
        }
        if (blockPathTypes == BlockPathTypes.OPEN && n2 >= 1) {
            BlockState blockState = blockGetter.getBlockState(new BlockPos(n, n2 - 1, n3));
            BlockPathTypes blockPathTypes3 = TurtleNodeEvaluator.getBlockPathTypeRaw(blockGetter, mutableBlockPos.set(n, n2 - 1, n3));
            blockPathTypes = blockPathTypes3 == BlockPathTypes.WALKABLE || blockPathTypes3 == BlockPathTypes.OPEN || blockPathTypes3 == BlockPathTypes.LAVA ? BlockPathTypes.OPEN : BlockPathTypes.WALKABLE;
            if (blockPathTypes3 == BlockPathTypes.DAMAGE_FIRE || blockState.is(Blocks.MAGMA_BLOCK) || blockState.is(BlockTags.CAMPFIRES)) {
                blockPathTypes = BlockPathTypes.DAMAGE_FIRE;
            }
            if (blockPathTypes3 == BlockPathTypes.DAMAGE_CACTUS) {
                blockPathTypes = BlockPathTypes.DAMAGE_CACTUS;
            }
            if (blockPathTypes3 == BlockPathTypes.DAMAGE_OTHER) {
                blockPathTypes = BlockPathTypes.DAMAGE_OTHER;
            }
        }
        if (blockPathTypes == BlockPathTypes.WALKABLE) {
            blockPathTypes = TurtleNodeEvaluator.checkNeighbourBlocks(blockGetter, mutableBlockPos.set(n, n2, n3), blockPathTypes);
        }
        return blockPathTypes;
    }
}

