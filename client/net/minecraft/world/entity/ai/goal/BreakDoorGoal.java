/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.goal;

import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.DoorInteractGoal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BreakDoorGoal
extends DoorInteractGoal {
    private final Predicate<Difficulty> validDifficulties;
    protected int breakTime;
    protected int lastBreakProgress = -1;
    protected int doorBreakTime = -1;

    public BreakDoorGoal(Mob mob, Predicate<Difficulty> predicate) {
        super(mob);
        this.validDifficulties = predicate;
    }

    public BreakDoorGoal(Mob mob, int n, Predicate<Difficulty> predicate) {
        this(mob, predicate);
        this.doorBreakTime = n;
    }

    protected int getDoorBreakTime() {
        return Math.max(240, this.doorBreakTime);
    }

    @Override
    public boolean canUse() {
        if (!super.canUse()) {
            return false;
        }
        if (!this.mob.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return false;
        }
        return this.isValidDifficulty(this.mob.level.getDifficulty()) && !this.isOpen();
    }

    @Override
    public void start() {
        super.start();
        this.breakTime = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.breakTime <= this.getDoorBreakTime() && !this.isOpen() && this.doorPos.closerThan(this.mob.position(), 2.0) && this.isValidDifficulty(this.mob.level.getDifficulty());
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.level.destroyBlockProgress(this.mob.getId(), this.doorPos, -1);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.mob.getRandom().nextInt(20) == 0) {
            this.mob.level.levelEvent(1019, this.doorPos, 0);
            if (!this.mob.swinging) {
                this.mob.swing(this.mob.getUsedItemHand());
            }
        }
        ++this.breakTime;
        int n = (int)((float)this.breakTime / (float)this.getDoorBreakTime() * 10.0f);
        if (n != this.lastBreakProgress) {
            this.mob.level.destroyBlockProgress(this.mob.getId(), this.doorPos, n);
            this.lastBreakProgress = n;
        }
        if (this.breakTime == this.getDoorBreakTime() && this.isValidDifficulty(this.mob.level.getDifficulty())) {
            this.mob.level.removeBlock(this.doorPos, false);
            this.mob.level.levelEvent(1021, this.doorPos, 0);
            this.mob.level.levelEvent(2001, this.doorPos, Block.getId(this.mob.level.getBlockState(this.doorPos)));
        }
    }

    private boolean isValidDifficulty(Difficulty difficulty) {
        return this.validDifficulties.test(difficulty);
    }
}

