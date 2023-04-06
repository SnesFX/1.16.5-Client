/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.goal;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WaterAvoidingRandomFlyingGoal
extends WaterAvoidingRandomStrollGoal {
    public WaterAvoidingRandomFlyingGoal(PathfinderMob pathfinderMob, double d) {
        super(pathfinderMob, d);
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        Vec3 vec3 = null;
        if (this.mob.isInWater()) {
            vec3 = RandomPos.getLandPos(this.mob, 15, 15);
        }
        if (this.mob.getRandom().nextFloat() >= this.probability) {
            vec3 = this.getTreePos();
        }
        return vec3 == null ? super.getPosition() : vec3;
    }

    @Nullable
    private Vec3 getTreePos() {
        BlockPos blockPos = this.mob.blockPosition();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos mutableBlockPos2 = new BlockPos.MutableBlockPos();
        Iterable<BlockPos> iterable = BlockPos.betweenClosed(Mth.floor(this.mob.getX() - 3.0), Mth.floor(this.mob.getY() - 6.0), Mth.floor(this.mob.getZ() - 3.0), Mth.floor(this.mob.getX() + 3.0), Mth.floor(this.mob.getY() + 6.0), Mth.floor(this.mob.getZ() + 3.0));
        for (BlockPos blockPos2 : iterable) {
            boolean bl;
            Block block;
            if (blockPos.equals(blockPos2) || !(bl = (block = this.mob.level.getBlockState(mutableBlockPos2.setWithOffset(blockPos2, Direction.DOWN)).getBlock()) instanceof LeavesBlock || block.is(BlockTags.LOGS)) || !this.mob.level.isEmptyBlock(blockPos2) || !this.mob.level.isEmptyBlock(mutableBlockPos.setWithOffset(blockPos2, Direction.UP))) continue;
            return Vec3.atBottomCenterOf(blockPos2);
        }
        return null;
    }
}

