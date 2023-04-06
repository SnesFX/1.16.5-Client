/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;

public abstract class MoveToBlockGoal
extends Goal {
    protected final PathfinderMob mob;
    public final double speedModifier;
    protected int nextStartTick;
    protected int tryTicks;
    private int maxStayTicks;
    protected BlockPos blockPos = BlockPos.ZERO;
    private boolean reachedTarget;
    private final int searchRange;
    private final int verticalSearchRange;
    protected int verticalSearchStart;

    public MoveToBlockGoal(PathfinderMob pathfinderMob, double d, int n) {
        this(pathfinderMob, d, n, 1);
    }

    public MoveToBlockGoal(PathfinderMob pathfinderMob, double d, int n, int n2) {
        this.mob = pathfinderMob;
        this.speedModifier = d;
        this.searchRange = n;
        this.verticalSearchStart = 0;
        this.verticalSearchRange = n2;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (this.nextStartTick > 0) {
            --this.nextStartTick;
            return false;
        }
        this.nextStartTick = this.nextStartTick(this.mob);
        return this.findNearestBlock();
    }

    protected int nextStartTick(PathfinderMob pathfinderMob) {
        return 200 + pathfinderMob.getRandom().nextInt(200);
    }

    @Override
    public boolean canContinueToUse() {
        return this.tryTicks >= -this.maxStayTicks && this.tryTicks <= 1200 && this.isValidTarget(this.mob.level, this.blockPos);
    }

    @Override
    public void start() {
        this.moveMobToBlock();
        this.tryTicks = 0;
        this.maxStayTicks = this.mob.getRandom().nextInt(this.mob.getRandom().nextInt(1200) + 1200) + 1200;
    }

    protected void moveMobToBlock() {
        this.mob.getNavigation().moveTo((double)this.blockPos.getX() + 0.5, this.blockPos.getY() + 1, (double)this.blockPos.getZ() + 0.5, this.speedModifier);
    }

    public double acceptedDistance() {
        return 1.0;
    }

    protected BlockPos getMoveToTarget() {
        return this.blockPos.above();
    }

    @Override
    public void tick() {
        BlockPos blockPos = this.getMoveToTarget();
        if (!blockPos.closerThan(this.mob.position(), this.acceptedDistance())) {
            this.reachedTarget = false;
            ++this.tryTicks;
            if (this.shouldRecalculatePath()) {
                this.mob.getNavigation().moveTo((double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5, this.speedModifier);
            }
        } else {
            this.reachedTarget = true;
            --this.tryTicks;
        }
    }

    public boolean shouldRecalculatePath() {
        return this.tryTicks % 40 == 0;
    }

    protected boolean isReachedTarget() {
        return this.reachedTarget;
    }

    protected boolean findNearestBlock() {
        int n = this.searchRange;
        int n2 = this.verticalSearchRange;
        BlockPos blockPos = this.mob.blockPosition();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        int n3 = this.verticalSearchStart;
        while (n3 <= n2) {
            for (int i = 0; i < n; ++i) {
                int n4 = 0;
                while (n4 <= i) {
                    int n5;
                    int n6 = n5 = n4 < i && n4 > -i ? i : 0;
                    while (n5 <= i) {
                        mutableBlockPos.setWithOffset(blockPos, n4, n3 - 1, n5);
                        if (this.mob.isWithinRestriction(mutableBlockPos) && this.isValidTarget(this.mob.level, mutableBlockPos)) {
                            this.blockPos = mutableBlockPos;
                            return true;
                        }
                        n5 = n5 > 0 ? -n5 : 1 - n5;
                    }
                    n4 = n4 > 0 ? -n4 : 1 - n4;
                }
            }
            n3 = n3 > 0 ? -n3 : 1 - n3;
        }
        return false;
    }

    protected abstract boolean isValidTarget(LevelReader var1, BlockPos var2);
}

