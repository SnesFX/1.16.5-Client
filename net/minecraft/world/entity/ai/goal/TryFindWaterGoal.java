/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class TryFindWaterGoal
extends Goal {
    private final PathfinderMob mob;

    public TryFindWaterGoal(PathfinderMob pathfinderMob) {
        this.mob = pathfinderMob;
    }

    @Override
    public boolean canUse() {
        return this.mob.isOnGround() && !this.mob.level.getFluidState(this.mob.blockPosition()).is(FluidTags.WATER);
    }

    @Override
    public void start() {
        Vec3i vec3i = null;
        Iterable<BlockPos> iterable = BlockPos.betweenClosed(Mth.floor(this.mob.getX() - 2.0), Mth.floor(this.mob.getY() - 2.0), Mth.floor(this.mob.getZ() - 2.0), Mth.floor(this.mob.getX() + 2.0), Mth.floor(this.mob.getY()), Mth.floor(this.mob.getZ() + 2.0));
        for (BlockPos blockPos : iterable) {
            if (!this.mob.level.getFluidState(blockPos).is(FluidTags.WATER)) continue;
            vec3i = blockPos;
            break;
        }
        if (vec3i != null) {
            this.mob.getMoveControl().setWantedPosition(vec3i.getX(), vec3i.getY(), vec3i.getZ(), 1.0);
        }
    }
}

