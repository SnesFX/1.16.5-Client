/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.navigation;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public abstract class PathNavigation {
    protected final Mob mob;
    protected final Level level;
    @Nullable
    protected Path path;
    protected double speedModifier;
    protected int tick;
    protected int lastStuckCheck;
    protected Vec3 lastStuckCheckPos = Vec3.ZERO;
    protected Vec3i timeoutCachedNode = Vec3i.ZERO;
    protected long timeoutTimer;
    protected long lastTimeoutCheck;
    protected double timeoutLimit;
    protected float maxDistanceToWaypoint = 0.5f;
    protected boolean hasDelayedRecomputation;
    protected long timeLastRecompute;
    protected NodeEvaluator nodeEvaluator;
    private BlockPos targetPos;
    private int reachRange;
    private float maxVisitedNodesMultiplier = 1.0f;
    private final PathFinder pathFinder;
    private boolean isStuck;

    public PathNavigation(Mob mob, Level level) {
        this.mob = mob;
        this.level = level;
        int n = Mth.floor(mob.getAttributeValue(Attributes.FOLLOW_RANGE) * 16.0);
        this.pathFinder = this.createPathFinder(n);
    }

    public void resetMaxVisitedNodesMultiplier() {
        this.maxVisitedNodesMultiplier = 1.0f;
    }

    public void setMaxVisitedNodesMultiplier(float f) {
        this.maxVisitedNodesMultiplier = f;
    }

    public BlockPos getTargetPos() {
        return this.targetPos;
    }

    protected abstract PathFinder createPathFinder(int var1);

    public void setSpeedModifier(double d) {
        this.speedModifier = d;
    }

    public boolean hasDelayedRecomputation() {
        return this.hasDelayedRecomputation;
    }

    public void recomputePath() {
        if (this.level.getGameTime() - this.timeLastRecompute > 20L) {
            if (this.targetPos != null) {
                this.path = null;
                this.path = this.createPath(this.targetPos, this.reachRange);
                this.timeLastRecompute = this.level.getGameTime();
                this.hasDelayedRecomputation = false;
            }
        } else {
            this.hasDelayedRecomputation = true;
        }
    }

    @Nullable
    public final Path createPath(double d, double d2, double d3, int n) {
        return this.createPath(new BlockPos(d, d2, d3), n);
    }

    @Nullable
    public Path createPath(Stream<BlockPos> stream, int n) {
        return this.createPath(stream.collect(Collectors.toSet()), 8, false, n);
    }

    @Nullable
    public Path createPath(Set<BlockPos> set, int n) {
        return this.createPath(set, 8, false, n);
    }

    @Nullable
    public Path createPath(BlockPos blockPos, int n) {
        return this.createPath((Set<BlockPos>)ImmutableSet.of((Object)blockPos), 8, false, n);
    }

    @Nullable
    public Path createPath(Entity entity, int n) {
        return this.createPath((Set<BlockPos>)ImmutableSet.of((Object)entity.blockPosition()), 16, true, n);
    }

    @Nullable
    protected Path createPath(Set<BlockPos> set, int n, boolean bl, int n2) {
        if (set.isEmpty()) {
            return null;
        }
        if (this.mob.getY() < 0.0) {
            return null;
        }
        if (!this.canUpdatePath()) {
            return null;
        }
        if (this.path != null && !this.path.isDone() && set.contains(this.targetPos)) {
            return this.path;
        }
        this.level.getProfiler().push("pathfind");
        float f = (float)this.mob.getAttributeValue(Attributes.FOLLOW_RANGE);
        BlockPos blockPos = bl ? this.mob.blockPosition().above() : this.mob.blockPosition();
        int n3 = (int)(f + (float)n);
        PathNavigationRegion pathNavigationRegion = new PathNavigationRegion(this.level, blockPos.offset(-n3, -n3, -n3), blockPos.offset(n3, n3, n3));
        Path path = this.pathFinder.findPath(pathNavigationRegion, this.mob, set, f, n2, this.maxVisitedNodesMultiplier);
        this.level.getProfiler().pop();
        if (path != null && path.getTarget() != null) {
            this.targetPos = path.getTarget();
            this.reachRange = n2;
            this.resetStuckTimeout();
        }
        return path;
    }

    public boolean moveTo(double d, double d2, double d3, double d4) {
        return this.moveTo(this.createPath(d, d2, d3, 1), d4);
    }

    public boolean moveTo(Entity entity, double d) {
        Path path = this.createPath(entity, 1);
        return path != null && this.moveTo(path, d);
    }

    public boolean moveTo(@Nullable Path path, double d) {
        if (path == null) {
            this.path = null;
            return false;
        }
        if (!path.sameAs(this.path)) {
            this.path = path;
        }
        if (this.isDone()) {
            return false;
        }
        this.trimPath();
        if (this.path.getNodeCount() <= 0) {
            return false;
        }
        this.speedModifier = d;
        Vec3 vec3 = this.getTempMobPos();
        this.lastStuckCheck = this.tick;
        this.lastStuckCheckPos = vec3;
        return true;
    }

    @Nullable
    public Path getPath() {
        return this.path;
    }

    public void tick() {
        Vec3 vec3;
        Object object;
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
            vec3 = this.getTempMobPos();
            object = this.path.getNextEntityPos(this.mob);
            if (vec3.y > ((Vec3)object).y && !this.mob.isOnGround() && Mth.floor(vec3.x) == Mth.floor(((Vec3)object).x) && Mth.floor(vec3.z) == Mth.floor(((Vec3)object).z)) {
                this.path.advance();
            }
        }
        DebugPackets.sendPathFindingPacket(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
        if (this.isDone()) {
            return;
        }
        this.mob.getMoveControl().setWantedPosition(vec3.x, this.level.getBlockState(((BlockPos)(object = new BlockPos(vec3 = this.path.getNextEntityPos(this.mob)))).below()).isAir() ? vec3.y : WalkNodeEvaluator.getFloorLevel(this.level, (BlockPos)object), vec3.z, this.speedModifier);
    }

    protected void followThePath() {
        boolean bl;
        Vec3 vec3 = this.getTempMobPos();
        this.maxDistanceToWaypoint = this.mob.getBbWidth() > 0.75f ? this.mob.getBbWidth() / 2.0f : 0.75f - this.mob.getBbWidth() / 2.0f;
        BlockPos blockPos = this.path.getNextNodePos();
        double d = Math.abs(this.mob.getX() - ((double)blockPos.getX() + 0.5));
        double d2 = Math.abs(this.mob.getY() - (double)blockPos.getY());
        double d3 = Math.abs(this.mob.getZ() - ((double)blockPos.getZ() + 0.5));
        boolean bl2 = bl = d < (double)this.maxDistanceToWaypoint && d3 < (double)this.maxDistanceToWaypoint && d2 < 1.0;
        if (bl || this.mob.canCutCorner(this.path.getNextNode().type) && this.shouldTargetNextNodeInDirection(vec3)) {
            this.path.advance();
        }
        this.doStuckDetection(vec3);
    }

    private boolean shouldTargetNextNodeInDirection(Vec3 vec3) {
        Vec3 vec32;
        if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
            return false;
        }
        Vec3 vec33 = Vec3.atBottomCenterOf(this.path.getNextNodePos());
        if (!vec3.closerThan(vec33, 2.0)) {
            return false;
        }
        Vec3 vec34 = Vec3.atBottomCenterOf(this.path.getNodePos(this.path.getNextNodeIndex() + 1));
        Vec3 vec35 = vec34.subtract(vec33);
        return vec35.dot(vec32 = vec3.subtract(vec33)) > 0.0;
    }

    protected void doStuckDetection(Vec3 vec3) {
        if (this.tick - this.lastStuckCheck > 100) {
            if (vec3.distanceToSqr(this.lastStuckCheckPos) < 2.25) {
                this.isStuck = true;
                this.stop();
            } else {
                this.isStuck = false;
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
                double d = vec3.distanceTo(Vec3.atBottomCenterOf(this.timeoutCachedNode));
                double d2 = this.timeoutLimit = this.mob.getSpeed() > 0.0f ? d / (double)this.mob.getSpeed() * 1000.0 : 0.0;
            }
            if (this.timeoutLimit > 0.0 && (double)this.timeoutTimer > this.timeoutLimit * 3.0) {
                this.timeoutPath();
            }
            this.lastTimeoutCheck = Util.getMillis();
        }
    }

    private void timeoutPath() {
        this.resetStuckTimeout();
        this.stop();
    }

    private void resetStuckTimeout() {
        this.timeoutCachedNode = Vec3i.ZERO;
        this.timeoutTimer = 0L;
        this.timeoutLimit = 0.0;
        this.isStuck = false;
    }

    public boolean isDone() {
        return this.path == null || this.path.isDone();
    }

    public boolean isInProgress() {
        return !this.isDone();
    }

    public void stop() {
        this.path = null;
    }

    protected abstract Vec3 getTempMobPos();

    protected abstract boolean canUpdatePath();

    protected boolean isInLiquid() {
        return this.mob.isInWaterOrBubble() || this.mob.isInLava();
    }

    protected void trimPath() {
        if (this.path == null) {
            return;
        }
        for (int i = 0; i < this.path.getNodeCount(); ++i) {
            Node node = this.path.getNode(i);
            Node node2 = i + 1 < this.path.getNodeCount() ? this.path.getNode(i + 1) : null;
            BlockState blockState = this.level.getBlockState(new BlockPos(node.x, node.y, node.z));
            if (!blockState.is(Blocks.CAULDRON)) continue;
            this.path.replaceNode(i, node.cloneAndMove(node.x, node.y + 1, node.z));
            if (node2 == null || node.y < node2.y) continue;
            this.path.replaceNode(i + 1, node.cloneAndMove(node2.x, node.y + 1, node2.z));
        }
    }

    protected abstract boolean canMoveDirectly(Vec3 var1, Vec3 var2, int var3, int var4, int var5);

    public boolean isStableDestination(BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.below();
        return this.level.getBlockState(blockPos2).isSolidRender(this.level, blockPos2);
    }

    public NodeEvaluator getNodeEvaluator() {
        return this.nodeEvaluator;
    }

    public void setCanFloat(boolean bl) {
        this.nodeEvaluator.setCanFloat(bl);
    }

    public boolean canFloat() {
        return this.nodeEvaluator.canFloat();
    }

    public void recomputePath(BlockPos blockPos) {
        if (this.path == null || this.path.isDone() || this.path.getNodeCount() == 0) {
            return;
        }
        Node node = this.path.getEndNode();
        Vec3 vec3 = new Vec3(((double)node.x + this.mob.getX()) / 2.0, ((double)node.y + this.mob.getY()) / 2.0, ((double)node.z + this.mob.getZ()) / 2.0);
        if (blockPos.closerThan(vec3, (double)(this.path.getNodeCount() - this.path.getNextNodeIndex()))) {
            this.recomputePath();
        }
    }

    public boolean isStuck() {
        return this.isStuck;
    }
}

