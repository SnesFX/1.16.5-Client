/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

public class FlyingPathNavigation
extends PathNavigation {
    public FlyingPathNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int n) {
        this.nodeEvaluator = new FlyNodeEvaluator();
        this.nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(this.nodeEvaluator, n);
    }

    @Override
    protected boolean canUpdatePath() {
        return this.canFloat() && this.isInLiquid() || !this.mob.isPassenger();
    }

    @Override
    protected Vec3 getTempMobPos() {
        return this.mob.position();
    }

    @Override
    public Path createPath(Entity entity, int n) {
        return this.createPath(entity.blockPosition(), n);
    }

    @Override
    public void tick() {
        Vec3 vec3;
        ++this.tick;
        if (this.hasDelayedRecomputation) {
            this.recomputePath();
        }
        if (this.isDone()) {
            return;
        }
        if (this.canUpdatePath()) {
            this.followThePath();
        } else if (this.path != null && !this.path.isDone()) {
            vec3 = this.path.getNextEntityPos(this.mob);
            if (Mth.floor(this.mob.getX()) == Mth.floor(vec3.x) && Mth.floor(this.mob.getY()) == Mth.floor(vec3.y) && Mth.floor(this.mob.getZ()) == Mth.floor(vec3.z)) {
                this.path.advance();
            }
        }
        DebugPackets.sendPathFindingPacket(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
        if (this.isDone()) {
            return;
        }
        vec3 = this.path.getNextEntityPos(this.mob);
        this.mob.getMoveControl().setWantedPosition(vec3.x, vec3.y, vec3.z, this.speedModifier);
    }

    @Override
    protected boolean canMoveDirectly(Vec3 vec3, Vec3 vec32, int n, int n2, int n3) {
        int n4 = Mth.floor(vec3.x);
        int n5 = Mth.floor(vec3.y);
        int n6 = Mth.floor(vec3.z);
        double d = vec32.x - vec3.x;
        double d2 = vec32.y - vec3.y;
        double d3 = vec32.z - vec3.z;
        double d4 = d * d + d2 * d2 + d3 * d3;
        if (d4 < 1.0E-8) {
            return false;
        }
        double d5 = 1.0 / Math.sqrt(d4);
        double d6 = 1.0 / Math.abs(d *= d5);
        double d7 = 1.0 / Math.abs(d2 *= d5);
        double d8 = 1.0 / Math.abs(d3 *= d5);
        double d9 = (double)n4 - vec3.x;
        double d10 = (double)n5 - vec3.y;
        double d11 = (double)n6 - vec3.z;
        if (d >= 0.0) {
            d9 += 1.0;
        }
        if (d2 >= 0.0) {
            d10 += 1.0;
        }
        if (d3 >= 0.0) {
            d11 += 1.0;
        }
        d9 /= d;
        d10 /= d2;
        d11 /= d3;
        int n7 = d < 0.0 ? -1 : 1;
        int n8 = d2 < 0.0 ? -1 : 1;
        int n9 = d3 < 0.0 ? -1 : 1;
        int n10 = Mth.floor(vec32.x);
        int n11 = Mth.floor(vec32.y);
        int n12 = Mth.floor(vec32.z);
        int n13 = n10 - n4;
        int n14 = n11 - n5;
        int n15 = n12 - n6;
        while (n13 * n7 > 0 || n14 * n8 > 0 || n15 * n9 > 0) {
            if (d9 < d11 && d9 <= d10) {
                d9 += d6;
                n13 = n10 - (n4 += n7);
                continue;
            }
            if (d10 < d9 && d10 <= d11) {
                d10 += d7;
                n14 = n11 - (n5 += n8);
                continue;
            }
            d11 += d8;
            n15 = n12 - (n6 += n9);
        }
        return true;
    }

    public void setCanOpenDoors(boolean bl) {
        this.nodeEvaluator.setCanOpenDoors(bl);
    }

    public void setCanPassDoors(boolean bl) {
        this.nodeEvaluator.setCanPassDoors(bl);
    }

    @Override
    public boolean isStableDestination(BlockPos blockPos) {
        return this.level.getBlockState(blockPos).entityCanStandOn(this.level, blockPos, this.mob);
    }
}

