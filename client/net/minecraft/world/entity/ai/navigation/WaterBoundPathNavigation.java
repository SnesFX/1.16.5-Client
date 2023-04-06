/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.navigation;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.SwimNodeEvaluator;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class WaterBoundPathNavigation
extends PathNavigation {
    private boolean allowBreaching;

    public WaterBoundPathNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int n) {
        this.allowBreaching = this.mob instanceof Dolphin;
        this.nodeEvaluator = new SwimNodeEvaluator(this.allowBreaching);
        return new PathFinder(this.nodeEvaluator, n);
    }

    @Override
    protected boolean canUpdatePath() {
        return this.allowBreaching || this.isInLiquid();
    }

    @Override
    protected Vec3 getTempMobPos() {
        return new Vec3(this.mob.getX(), this.mob.getY(0.5), this.mob.getZ());
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
    protected void followThePath() {
        if (this.path == null) {
            return;
        }
        Vec3 vec3 = this.getTempMobPos();
        float f = this.mob.getBbWidth();
        float f2 = f > 0.75f ? f / 2.0f : 0.75f - f / 2.0f;
        Vec3 vec32 = this.mob.getDeltaMovement();
        if (Math.abs(vec32.x) > 0.2 || Math.abs(vec32.z) > 0.2) {
            f2 = (float)((double)f2 * (vec32.length() * 6.0));
        }
        int n = 6;
        Vec3 vec33 = Vec3.atBottomCenterOf(this.path.getNextNodePos());
        if (Math.abs(this.mob.getX() - vec33.x) < (double)f2 && Math.abs(this.mob.getZ() - vec33.z) < (double)f2 && Math.abs(this.mob.getY() - vec33.y) < (double)(f2 * 2.0f)) {
            this.path.advance();
        }
        for (int i = Math.min((int)(this.path.getNextNodeIndex() + 6), (int)(this.path.getNodeCount() - 1)); i > this.path.getNextNodeIndex(); --i) {
            vec33 = this.path.getEntityPosAtNode(this.mob, i);
            if (vec33.distanceToSqr(vec3) > 36.0 || !this.canMoveDirectly(vec3, vec33, 0, 0, 0)) continue;
            this.path.setNextNodeIndex(i);
            break;
        }
        this.doStuckDetection(vec3);
    }

    @Override
    protected void doStuckDetection(Vec3 vec3) {
        if (this.tick - this.lastStuckCheck > 100) {
            if (vec3.distanceToSqr(this.lastStuckCheckPos) < 2.25) {
                this.stop();
            }
            this.lastStuckCheck = this.tick;
            this.lastStuckCheckPos = vec3;
        }
        if (this.path != null && !this.path.isDone()) {
            BlockPos blockPos = this.path.getNextNodePos();
            if (blockPos.equals(this.timeoutCachedNode)) {
                this.timeoutTimer += Util.getMillis() - this.lastTimeoutCheck;
            } else {
                this.timeoutCachedNode = blockPos;
                double d = vec3.distanceTo(Vec3.atCenterOf(this.timeoutCachedNode));
                double d2 = this.timeoutLimit = this.mob.getSpeed() > 0.0f ? d / (double)this.mob.getSpeed() * 100.0 : 0.0;
            }
            if (this.timeoutLimit > 0.0 && (double)this.timeoutTimer > this.timeoutLimit * 2.0) {
                this.timeoutCachedNode = Vec3i.ZERO;
                this.timeoutTimer = 0L;
                this.timeoutLimit = 0.0;
                this.stop();
            }
            this.lastTimeoutCheck = Util.getMillis();
        }
    }

    @Override
    protected boolean canMoveDirectly(Vec3 vec3, Vec3 vec32, int n, int n2, int n3) {
        Vec3 vec33 = new Vec3(vec32.x, vec32.y + (double)this.mob.getBbHeight() * 0.5, vec32.z);
        return this.level.clip(new ClipContext(vec3, vec33, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.mob)).getType() == HitResult.Type.MISS;
    }

    @Override
    public boolean isStableDestination(BlockPos blockPos) {
        return !this.level.getBlockState(blockPos).isSolidRender(this.level, blockPos);
    }

    @Override
    public void setCanFloat(boolean bl) {
    }
}

