/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.world.level.block.piston;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class PistonStructureResolver {
    private final Level level;
    private final BlockPos pistonPos;
    private final boolean extending;
    private final BlockPos startPos;
    private final Direction pushDirection;
    private final List<BlockPos> toPush = Lists.newArrayList();
    private final List<BlockPos> toDestroy = Lists.newArrayList();
    private final Direction pistonDirection;

    public PistonStructureResolver(Level level, BlockPos blockPos, Direction direction, boolean bl) {
        this.level = level;
        this.pistonPos = blockPos;
        this.pistonDirection = direction;
        this.extending = bl;
        if (bl) {
            this.pushDirection = direction;
            this.startPos = blockPos.relative(direction);
        } else {
            this.pushDirection = direction.getOpposite();
            this.startPos = blockPos.relative(direction, 2);
        }
    }

    public boolean resolve() {
        this.toPush.clear();
        this.toDestroy.clear();
        BlockState blockState = this.level.getBlockState(this.startPos);
        if (!PistonBaseBlock.isPushable(blockState, this.level, this.startPos, this.pushDirection, false, this.pistonDirection)) {
            if (this.extending && blockState.getPistonPushReaction() == PushReaction.DESTROY) {
                this.toDestroy.add(this.startPos);
                return true;
            }
            return false;
        }
        if (!this.addBlockLine(this.startPos, this.pushDirection)) {
            return false;
        }
        for (int i = 0; i < this.toPush.size(); ++i) {
            BlockPos blockPos = this.toPush.get(i);
            if (!PistonStructureResolver.isSticky(this.level.getBlockState(blockPos).getBlock()) || this.addBranchingBlocks(blockPos)) continue;
            return false;
        }
        return true;
    }

    private static boolean isSticky(Block block) {
        return block == Blocks.SLIME_BLOCK || block == Blocks.HONEY_BLOCK;
    }

    private static boolean canStickToEachOther(Block block, Block block2) {
        if (block == Blocks.HONEY_BLOCK && block2 == Blocks.SLIME_BLOCK) {
            return false;
        }
        if (block == Blocks.SLIME_BLOCK && block2 == Blocks.HONEY_BLOCK) {
            return false;
        }
        return PistonStructureResolver.isSticky(block) || PistonStructureResolver.isSticky(block2);
    }

    private boolean addBlockLine(BlockPos blockPos, Direction direction) {
        int n;
        BlockState blockState = this.level.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (blockState.isAir()) {
            return true;
        }
        if (!PistonBaseBlock.isPushable(blockState, this.level, blockPos, this.pushDirection, false, direction)) {
            return true;
        }
        if (blockPos.equals(this.pistonPos)) {
            return true;
        }
        if (this.toPush.contains(blockPos)) {
            return true;
        }
        int n2 = 1;
        if (n2 + this.toPush.size() > 12) {
            return false;
        }
        while (PistonStructureResolver.isSticky(block)) {
            BlockPos blockPos2 = blockPos.relative(this.pushDirection.getOpposite(), n2);
            Block block2 = block;
            blockState = this.level.getBlockState(blockPos2);
            block = blockState.getBlock();
            if (blockState.isAir() || !PistonStructureResolver.canStickToEachOther(block2, block) || !PistonBaseBlock.isPushable(blockState, this.level, blockPos2, this.pushDirection, false, this.pushDirection.getOpposite()) || blockPos2.equals(this.pistonPos)) break;
            if (++n2 + this.toPush.size() <= 12) continue;
            return false;
        }
        int n3 = 0;
        for (n = n2 - 1; n >= 0; --n) {
            this.toPush.add(blockPos.relative(this.pushDirection.getOpposite(), n));
            ++n3;
        }
        n = 1;
        do {
            BlockPos blockPos3;
            int n4;
            if ((n4 = this.toPush.indexOf(blockPos3 = blockPos.relative(this.pushDirection, n))) > -1) {
                this.reorderListAtCollision(n3, n4);
                for (int i = 0; i <= n4 + n3; ++i) {
                    BlockPos blockPos4 = this.toPush.get(i);
                    if (!PistonStructureResolver.isSticky(this.level.getBlockState(blockPos4).getBlock()) || this.addBranchingBlocks(blockPos4)) continue;
                    return false;
                }
                return true;
            }
            blockState = this.level.getBlockState(blockPos3);
            if (blockState.isAir()) {
                return true;
            }
            if (!PistonBaseBlock.isPushable(blockState, this.level, blockPos3, this.pushDirection, true, this.pushDirection) || blockPos3.equals(this.pistonPos)) {
                return false;
            }
            if (blockState.getPistonPushReaction() == PushReaction.DESTROY) {
                this.toDestroy.add(blockPos3);
                return true;
            }
            if (this.toPush.size() >= 12) {
                return false;
            }
            this.toPush.add(blockPos3);
            ++n3;
            ++n;
        } while (true);
    }

    private void reorderListAtCollision(int n, int n2) {
        ArrayList arrayList = Lists.newArrayList();
        ArrayList arrayList2 = Lists.newArrayList();
        ArrayList arrayList3 = Lists.newArrayList();
        arrayList.addAll(this.toPush.subList(0, n2));
        arrayList2.addAll(this.toPush.subList(this.toPush.size() - n, this.toPush.size()));
        arrayList3.addAll(this.toPush.subList(n2, this.toPush.size() - n));
        this.toPush.clear();
        this.toPush.addAll(arrayList);
        this.toPush.addAll(arrayList2);
        this.toPush.addAll(arrayList3);
    }

    private boolean addBranchingBlocks(BlockPos blockPos) {
        BlockState blockState = this.level.getBlockState(blockPos);
        for (Direction direction : Direction.values()) {
            BlockState blockState2;
            BlockPos blockPos2;
            if (direction.getAxis() == this.pushDirection.getAxis() || !PistonStructureResolver.canStickToEachOther((blockState2 = this.level.getBlockState(blockPos2 = blockPos.relative(direction))).getBlock(), blockState.getBlock()) || this.addBlockLine(blockPos2, direction)) continue;
            return false;
        }
        return true;
    }

    public List<BlockPos> getToPush() {
        return this.toPush;
    }

    public List<BlockPos> getToDestroy() {
        return this.toDestroy;
    }
}

