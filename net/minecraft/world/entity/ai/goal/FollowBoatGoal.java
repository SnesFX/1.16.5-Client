/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.goal;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.BoatGoals;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FollowBoatGoal
extends Goal {
    private int timeToRecalcPath;
    private final PathfinderMob mob;
    private Player following;
    private BoatGoals currentGoal;

    public FollowBoatGoal(PathfinderMob pathfinderMob) {
        this.mob = pathfinderMob;
    }

    @Override
    public boolean canUse() {
        List<Boat> list = this.mob.level.getEntitiesOfClass(Boat.class, this.mob.getBoundingBox().inflate(5.0));
        boolean bl = false;
        for (Boat boat : list) {
            Entity entity = boat.getControllingPassenger();
            if (!(entity instanceof Player) || !(Mth.abs(((Player)entity).xxa) > 0.0f) && !(Mth.abs(((Player)entity).zza) > 0.0f)) continue;
            bl = true;
            break;
        }
        return this.following != null && (Mth.abs(this.following.xxa) > 0.0f || Mth.abs(this.following.zza) > 0.0f) || bl;
    }

    @Override
    public boolean isInterruptable() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.following != null && this.following.isPassenger() && (Mth.abs(this.following.xxa) > 0.0f || Mth.abs(this.following.zza) > 0.0f);
    }

    @Override
    public void start() {
        List<Boat> list = this.mob.level.getEntitiesOfClass(Boat.class, this.mob.getBoundingBox().inflate(5.0));
        for (Boat boat : list) {
            if (boat.getControllingPassenger() == null || !(boat.getControllingPassenger() instanceof Player)) continue;
            this.following = (Player)boat.getControllingPassenger();
            break;
        }
        this.timeToRecalcPath = 0;
        this.currentGoal = BoatGoals.GO_TO_BOAT;
    }

    @Override
    public void stop() {
        this.following = null;
    }

    @Override
    public void tick() {
        boolean bl;
        boolean bl2 = bl = Mth.abs(this.following.xxa) > 0.0f || Mth.abs(this.following.zza) > 0.0f;
        float f = this.currentGoal == BoatGoals.GO_IN_BOAT_DIRECTION ? (bl ? 0.01f : 0.0f) : 0.015f;
        this.mob.moveRelative(f, new Vec3(this.mob.xxa, this.mob.yya, this.mob.zza));
        this.mob.move(MoverType.SELF, this.mob.getDeltaMovement());
        if (--this.timeToRecalcPath > 0) {
            return;
        }
        this.timeToRecalcPath = 10;
        if (this.currentGoal == BoatGoals.GO_TO_BOAT) {
            BlockPos blockPos = this.following.blockPosition().relative(this.following.getDirection().getOpposite());
            blockPos = blockPos.offset(0, -1, 0);
            this.mob.getNavigation().moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0);
            if (this.mob.distanceTo(this.following) < 4.0f) {
                this.timeToRecalcPath = 0;
                this.currentGoal = BoatGoals.GO_IN_BOAT_DIRECTION;
            }
        } else if (this.currentGoal == BoatGoals.GO_IN_BOAT_DIRECTION) {
            Direction direction = this.following.getMotionDirection();
            BlockPos blockPos = this.following.blockPosition().relative(direction, 10);
            this.mob.getNavigation().moveTo(blockPos.getX(), blockPos.getY() - 1, blockPos.getZ(), 1.0);
            if (this.mob.distanceTo(this.following) > 12.0f) {
                this.timeToRecalcPath = 0;
                this.currentGoal = BoatGoals.GO_TO_BOAT;
            }
        }
    }
}

