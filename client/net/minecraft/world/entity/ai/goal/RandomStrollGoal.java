/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

public class RandomStrollGoal
extends Goal {
    protected final PathfinderMob mob;
    protected double wantedX;
    protected double wantedY;
    protected double wantedZ;
    protected final double speedModifier;
    protected int interval;
    protected boolean forceTrigger;
    private boolean checkNoActionTime;

    public RandomStrollGoal(PathfinderMob pathfinderMob, double d) {
        this(pathfinderMob, d, 120);
    }

    public RandomStrollGoal(PathfinderMob pathfinderMob, double d, int n) {
        this(pathfinderMob, d, n, true);
    }

    public RandomStrollGoal(PathfinderMob pathfinderMob, double d, int n, boolean bl) {
        this.mob = pathfinderMob;
        this.speedModifier = d;
        this.interval = n;
        this.checkNoActionTime = bl;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Vec3 vec3;
        if (this.mob.isVehicle()) {
            return false;
        }
        if (!this.forceTrigger) {
            if (this.checkNoActionTime && this.mob.getNoActionTime() >= 100) {
                return false;
            }
            if (this.mob.getRandom().nextInt(this.interval) != 0) {
                return false;
            }
        }
        if ((vec3 = this.getPosition()) == null) {
            return false;
        }
        this.wantedX = vec3.x;
        this.wantedY = vec3.y;
        this.wantedZ = vec3.z;
        this.forceTrigger = false;
        return true;
    }

    @Nullable
    protected Vec3 getPosition() {
        return RandomPos.getPos(this.mob, 10, 7);
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone() && !this.mob.isVehicle();
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        super.stop();
    }

    public void trigger() {
        this.forceTrigger = true;
    }

    public void setInterval(int n) {
        this.interval = n;
    }
}

