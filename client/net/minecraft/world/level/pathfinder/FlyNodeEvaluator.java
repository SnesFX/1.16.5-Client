/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.pathfinder;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.HashSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Target;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;

public class FlyNodeEvaluator
extends WalkNodeEvaluator {
    @Override
    public void prepare(PathNavigationRegion pathNavigationRegion, Mob mob) {
        super.prepare(pathNavigationRegion, mob);
        this.oldWaterCost = mob.getPathfindingMalus(BlockPathTypes.WATER);
    }

    @Override
    public void done() {
        this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
        super.done();
    }

    @Override
    public Node getStart() {
        BlockPos blockPos;
        int n;
        Object object;
        if (this.canFloat() && this.mob.isInWater()) {
            n = Mth.floor(this.mob.getY());
            blockPos = new BlockPos.MutableBlockPos(this.mob.getX(), (double)n, this.mob.getZ());
            object = this.level.getBlockState(blockPos).getBlock();
            while (object == Blocks.WATER) {
                ((BlockPos.MutableBlockPos)blockPos).set(this.mob.getX(), (double)(++n), this.mob.getZ());
                object = this.level.getBlockState(blockPos).getBlock();
            }
        } else {
            n = Mth.floor(this.mob.getY() + 0.5);
        }
        if (this.mob.getPathfindingMalus((BlockPathTypes)((Object)(object = this.getBlockPathType(this.mob, (blockPos = this.mob.blockPosition()).getX(), n, blockPos.getZ())))) < 0.0f) {
            HashSet hashSet = Sets.newHashSet();
            hashSet.add(new BlockPos(this.mob.getBoundingBox().minX, (double)n, this.mob.getBoundingBox().minZ));
            hashSet.add(new BlockPos(this.mob.getBoundingBox().minX, (double)n, this.mob.getBoundingBox().maxZ));
            hashSet.add(new BlockPos(this.mob.getBoundingBox().maxX, (double)n, this.mob.getBoundingBox().minZ));
            hashSet.add(new BlockPos(this.mob.getBoundingBox().maxX, (double)n, this.mob.getBoundingBox().maxZ));
            for (BlockPos blockPos2 : hashSet) {
                BlockPathTypes blockPathTypes = this.getBlockPathType(this.mob, blockPos2);
                if (!(this.mob.getPathfindingMalus(blockPathTypes) >= 0.0f)) continue;
                return super.getNode(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
            }
        }
        return super.getNode(blockPos.getX(), n, blockPos.getZ());
    }

    @Override
    public Target getGoal(double d, double d2, double d3) {
        return new Target(super.getNode(Mth.floor(d), Mth.floor(d2), Mth.floor(d3)));
    }

    @Override
    public int getNeighbors(Node[] arrnode, Node node) {
        Node node2;
        Node node3;
        Node node4;
        Node node5;
        Node node6;
        Node node7;
        Node node8;
        Node node9;
        Node node10;
        Node node11;
        Node node12;
        Node node13;
        Node node14;
        Node node15;
        Node node16;
        Node node17;
        Node node18;
        Node node19;
        Node node20;
        Node node21;
        Node node22;
        Node node23;
        Node node24;
        Node node25;
        Node node26;
        int n = 0;
        Node node27 = this.getNode(node.x, node.y, node.z + 1);
        if (this.isOpen(node27)) {
            arrnode[n++] = node27;
        }
        if (this.isOpen(node6 = this.getNode(node.x - 1, node.y, node.z))) {
            arrnode[n++] = node6;
        }
        if (this.isOpen(node26 = this.getNode(node.x + 1, node.y, node.z))) {
            arrnode[n++] = node26;
        }
        if (this.isOpen(node9 = this.getNode(node.x, node.y, node.z - 1))) {
            arrnode[n++] = node9;
        }
        if (this.isOpen(node8 = this.getNode(node.x, node.y + 1, node.z))) {
            arrnode[n++] = node8;
        }
        if (this.isOpen(node22 = this.getNode(node.x, node.y - 1, node.z))) {
            arrnode[n++] = node22;
        }
        if (this.isOpen(node12 = this.getNode(node.x, node.y + 1, node.z + 1)) && this.hasMalus(node27) && this.hasMalus(node8)) {
            arrnode[n++] = node12;
        }
        if (this.isOpen(node24 = this.getNode(node.x - 1, node.y + 1, node.z)) && this.hasMalus(node6) && this.hasMalus(node8)) {
            arrnode[n++] = node24;
        }
        if (this.isOpen(node15 = this.getNode(node.x + 1, node.y + 1, node.z)) && this.hasMalus(node26) && this.hasMalus(node8)) {
            arrnode[n++] = node15;
        }
        if (this.isOpen(node21 = this.getNode(node.x, node.y + 1, node.z - 1)) && this.hasMalus(node9) && this.hasMalus(node8)) {
            arrnode[n++] = node21;
        }
        if (this.isOpen(node10 = this.getNode(node.x, node.y - 1, node.z + 1)) && this.hasMalus(node27) && this.hasMalus(node22)) {
            arrnode[n++] = node10;
        }
        if (this.isOpen(node17 = this.getNode(node.x - 1, node.y - 1, node.z)) && this.hasMalus(node6) && this.hasMalus(node22)) {
            arrnode[n++] = node17;
        }
        if (this.isOpen(node4 = this.getNode(node.x + 1, node.y - 1, node.z)) && this.hasMalus(node26) && this.hasMalus(node22)) {
            arrnode[n++] = node4;
        }
        if (this.isOpen(node14 = this.getNode(node.x, node.y - 1, node.z - 1)) && this.hasMalus(node9) && this.hasMalus(node22)) {
            arrnode[n++] = node14;
        }
        if (this.isOpen(node19 = this.getNode(node.x + 1, node.y, node.z - 1)) && this.hasMalus(node9) && this.hasMalus(node26)) {
            arrnode[n++] = node19;
        }
        if (this.isOpen(node5 = this.getNode(node.x + 1, node.y, node.z + 1)) && this.hasMalus(node27) && this.hasMalus(node26)) {
            arrnode[n++] = node5;
        }
        if (this.isOpen(node11 = this.getNode(node.x - 1, node.y, node.z - 1)) && this.hasMalus(node9) && this.hasMalus(node6)) {
            arrnode[n++] = node11;
        }
        if (this.isOpen(node25 = this.getNode(node.x - 1, node.y, node.z + 1)) && this.hasMalus(node27) && this.hasMalus(node6)) {
            arrnode[n++] = node25;
        }
        if (this.isOpen(node3 = this.getNode(node.x + 1, node.y + 1, node.z - 1)) && this.hasMalus(node19) && this.hasMalus(node9) && this.hasMalus(node26) && this.hasMalus(node8) && this.hasMalus(node21) && this.hasMalus(node15)) {
            arrnode[n++] = node3;
        }
        if (this.isOpen(node16 = this.getNode(node.x + 1, node.y + 1, node.z + 1)) && this.hasMalus(node5) && this.hasMalus(node27) && this.hasMalus(node26) && this.hasMalus(node8) && this.hasMalus(node12) && this.hasMalus(node15)) {
            arrnode[n++] = node16;
        }
        if (this.isOpen(node7 = this.getNode(node.x - 1, node.y + 1, node.z - 1)) && this.hasMalus(node11) && this.hasMalus(node9) && this.hasMalus(node6) & this.hasMalus(node8) && this.hasMalus(node21) && this.hasMalus(node24)) {
            arrnode[n++] = node7;
        }
        if (this.isOpen(node13 = this.getNode(node.x - 1, node.y + 1, node.z + 1)) && this.hasMalus(node25) && this.hasMalus(node27) && this.hasMalus(node6) & this.hasMalus(node8) && this.hasMalus(node12) && this.hasMalus(node24)) {
            arrnode[n++] = node13;
        }
        if (this.isOpen(node23 = this.getNode(node.x + 1, node.y - 1, node.z - 1)) && this.hasMalus(node19) && this.hasMalus(node9) && this.hasMalus(node26) && this.hasMalus(node22) && this.hasMalus(node14) && this.hasMalus(node4)) {
            arrnode[n++] = node23;
        }
        if (this.isOpen(node2 = this.getNode(node.x + 1, node.y - 1, node.z + 1)) && this.hasMalus(node5) && this.hasMalus(node27) && this.hasMalus(node26) && this.hasMalus(node22) && this.hasMalus(node10) && this.hasMalus(node4)) {
            arrnode[n++] = node2;
        }
        if (this.isOpen(node20 = this.getNode(node.x - 1, node.y - 1, node.z - 1)) && this.hasMalus(node11) && this.hasMalus(node9) && this.hasMalus(node6) && this.hasMalus(node22) && this.hasMalus(node14) && this.hasMalus(node17)) {
            arrnode[n++] = node20;
        }
        if (this.isOpen(node18 = this.getNode(node.x - 1, node.y - 1, node.z + 1)) && this.hasMalus(node25) && this.hasMalus(node27) && this.hasMalus(node6) && this.hasMalus(node22) && this.hasMalus(node10) && this.hasMalus(node17)) {
            arrnode[n++] = node18;
        }
        return n;
    }

    private boolean hasMalus(@Nullable Node node) {
        return node != null && node.costMalus >= 0.0f;
    }

    private boolean isOpen(@Nullable Node node) {
        return node != null && !node.closed;
    }

    @Nullable
    @Override
    protected Node getNode(int n, int n2, int n3) {
        Node node = null;
        BlockPathTypes blockPathTypes = this.getBlockPathType(this.mob, n, n2, n3);
        float f = this.mob.getPathfindingMalus(blockPathTypes);
        if (f >= 0.0f) {
            node = super.getNode(n, n2, n3);
            node.type = blockPathTypes;
            node.costMalus = Math.max(node.costMalus, f);
            if (blockPathTypes == BlockPathTypes.WALKABLE) {
                node.costMalus += 1.0f;
            }
        }
        if (blockPathTypes == BlockPathTypes.OPEN || blockPathTypes == BlockPathTypes.WALKABLE) {
            return node;
        }
        return node;
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter blockGetter, int n, int n2, int n3, Mob mob, int n4, int n5, int n6, boolean bl, boolean bl2) {
        EnumSet<BlockPathTypes> enumSet = EnumSet.noneOf(BlockPathTypes.class);
        BlockPathTypes blockPathTypes = BlockPathTypes.BLOCKED;
        BlockPos blockPos = mob.blockPosition();
        blockPathTypes = this.getBlockPathTypes(blockGetter, n, n2, n3, n4, n5, n6, bl, bl2, enumSet, blockPathTypes, blockPos);
        if (enumSet.contains((Object)BlockPathTypes.FENCE)) {
            return BlockPathTypes.FENCE;
        }
        BlockPathTypes blockPathTypes2 = BlockPathTypes.BLOCKED;
        for (BlockPathTypes blockPathTypes3 : enumSet) {
            if (mob.getPathfindingMalus(blockPathTypes3) < 0.0f) {
                return blockPathTypes3;
            }
            if (!(mob.getPathfindingMalus(blockPathTypes3) >= mob.getPathfindingMalus(blockPathTypes2))) continue;
            blockPathTypes2 = blockPathTypes3;
        }
        if (blockPathTypes == BlockPathTypes.OPEN && mob.getPathfindingMalus(blockPathTypes2) == 0.0f) {
            return BlockPathTypes.OPEN;
        }
        return blockPathTypes2;
    }

    @Override
    public BlockPathTypes getBlockPathType(BlockGetter blockGetter, int n, int n2, int n3) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        BlockPathTypes blockPathTypes = FlyNodeEvaluator.getBlockPathTypeRaw(blockGetter, mutableBlockPos.set(n, n2, n3));
        if (blockPathTypes == BlockPathTypes.OPEN && n2 >= 1) {
            BlockState blockState = blockGetter.getBlockState(mutableBlockPos.set(n, n2 - 1, n3));
            BlockPathTypes blockPathTypes2 = FlyNodeEvaluator.getBlockPathTypeRaw(blockGetter, mutableBlockPos.set(n, n2 - 1, n3));
            if (blockPathTypes2 == BlockPathTypes.DAMAGE_FIRE || blockState.is(Blocks.MAGMA_BLOCK) || blockPathTypes2 == BlockPathTypes.LAVA || blockState.is(BlockTags.CAMPFIRES)) {
                blockPathTypes = BlockPathTypes.DAMAGE_FIRE;
            } else if (blockPathTypes2 == BlockPathTypes.DAMAGE_CACTUS) {
                blockPathTypes = BlockPathTypes.DAMAGE_CACTUS;
            } else if (blockPathTypes2 == BlockPathTypes.DAMAGE_OTHER) {
                blockPathTypes = BlockPathTypes.DAMAGE_OTHER;
            } else if (blockPathTypes2 == BlockPathTypes.COCOA) {
                blockPathTypes = BlockPathTypes.COCOA;
            } else if (blockPathTypes2 == BlockPathTypes.FENCE) {
                blockPathTypes = BlockPathTypes.FENCE;
            } else {
                BlockPathTypes blockPathTypes3 = blockPathTypes = blockPathTypes2 == BlockPathTypes.WALKABLE || blockPathTypes2 == BlockPathTypes.OPEN || blockPathTypes2 == BlockPathTypes.WATER ? BlockPathTypes.OPEN : BlockPathTypes.WALKABLE;
            }
        }
        if (blockPathTypes == BlockPathTypes.WALKABLE || blockPathTypes == BlockPathTypes.OPEN) {
            blockPathTypes = FlyNodeEvaluator.checkNeighbourBlocks(blockGetter, mutableBlockPos.set(n, n2, n3), blockPathTypes);
        }
        return blockPathTypes;
    }

    private BlockPathTypes getBlockPathType(Mob mob, BlockPos blockPos) {
        return this.getBlockPathType(mob, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    private BlockPathTypes getBlockPathType(Mob mob, int n, int n2, int n3) {
        return this.getBlockPathType(this.level, n, n2, n3, mob, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors());
    }
}

