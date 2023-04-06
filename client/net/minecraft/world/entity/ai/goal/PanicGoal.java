/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class PanicGoal
extends Goal {
    protected final PathfinderMob mob;
    protected final double speedModifier;
    protected double posX;
    protected double posY;
    protected double posZ;
    protected boolean isRunning;

    public PanicGoal(PathfinderMob pathfinderMob, double d) {
        this.mob = pathfinderMob;
        this.speedModifier = d;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        BlockPos blockPos;
        if (this.mob.getLastHurtByMob() == null && !this.mob.isOnFire()) {
            return false;
        }
        if (this.mob.isOnFire() && (blockPos = this.lookForWater(this.mob.level, this.mob, 5, 4)) != null) {
            this.posX = blockPos.getX();
            this.posY = blockPos.getY();
            this.posZ = blockPos.getZ();
            return true;
        }
        return this.findRandomPosition();
    }

    protected boolean findRandomPosition() {
        Vec3 vec3 = RandomPos.getPos(this.mob, 5, 4);
        if (vec3 == null) {
            return false;
        }
        this.posX = vec3.x;
        this.posY = vec3.y;
        this.posZ = vec3.z;
        return true;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
        this.isRunning = true;
    }

    @Override
    public void stop() {
        this.isRunning = false;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }

    @Nullable
    protected BlockPos lookForWater(BlockGetter blockGetter, Entity entity, int n, int n2) {
        BlockPos blockPos = entity.blockPosition();
        int n3 = blockPos.getX();
        int n4 = blockPos.getY();
        int n5 = blockPos.getZ();
        float f = n * n * n2 * 2;
        BlockPos blockPos2 = null;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = n3 - n; i <= n3 + n; ++i) {
            for (int j = n4 - n2; j <= n4 + n2; ++j) {
                for (int k = n5 - n; k <= n5 + n; ++k) {
                    float f2;
                    mutableBlockPos.set(i, j, k);
                    if (!blockGetter.getFluidState(mutableBlockPos).is(FluidTags.WATER) || !((f2 = (float)((i - n3) * (i - n3) + (j - n4) * (j - n4) + (k - n5) * (k - n5))) < f)) continue;
                    f = f2;
                    blockPos2 = new BlockPos(mutableBlockPos);
                }
            }
        }
        return blockPos2;
    }
}

