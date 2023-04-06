/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

public class BreathAirGoal
extends Goal {
    private final PathfinderMob mob;

    public BreathAirGoal(PathfinderMob pathfinderMob) {
        this.mob = pathfinderMob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.mob.getAirSupply() < 140;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        this.findAirPosition();
    }

    private void findAirPosition() {
        Iterable<BlockPos> iterable = BlockPos.betweenClosed(Mth.floor(this.mob.getX() - 1.0), Mth.floor(this.mob.getY()), Mth.floor(this.mob.getZ() - 1.0), Mth.floor(this.mob.getX() + 1.0), Mth.floor(this.mob.getY() + 8.0), Mth.floor(this.mob.getZ() + 1.0));
        Vec3i vec3i = null;
        for (BlockPos blockPos : iterable) {
            if (!this.givesAir(this.mob.level, blockPos)) continue;
            vec3i = blockPos;
            break;
        }
        if (vec3i == null) {
            vec3i = new BlockPos(this.mob.getX(), this.mob.getY() + 8.0, this.mob.getZ());
        }
        this.mob.getNavigation().moveTo(vec3i.getX(), vec3i.getY() + 1, vec3i.getZ(), 1.0);
    }

    @Override
    public void tick() {
        this.findAirPosition();
        this.mob.moveRelative(0.02f, new Vec3(this.mob.xxa, this.mob.yya, this.mob.zza));
        this.mob.move(MoverType.SELF, this.mob.getDeltaMovement());
    }

    private boolean givesAir(LevelReader levelReader, BlockPos blockPos) {
        BlockState blockState = levelReader.getBlockState(blockPos);
        return (levelReader.getFluidState(blockPos).isEmpty() || blockState.is(Blocks.BUBBLE_COLUMN)) && blockState.isPathfindable(levelReader, blockPos, PathComputationType.LAND);
    }
}

