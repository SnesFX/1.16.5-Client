/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.behavior;

import java.util.Map;
import java.util.Random;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.IntRange;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class RunSometimes<E extends LivingEntity>
extends Behavior<E> {
    private boolean resetTicks;
    private boolean wasRunning;
    private final IntRange interval;
    private final Behavior<? super E> wrappedBehavior;
    private int ticksUntilNextStart;

    public RunSometimes(Behavior<? super E> behavior, IntRange intRange) {
        this(behavior, false, intRange);
    }

    public RunSometimes(Behavior<? super E> behavior, boolean bl, IntRange intRange) {
        super(behavior.entryCondition);
        this.wrappedBehavior = behavior;
        this.resetTicks = !bl;
        this.interval = intRange;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, E e) {
        if (!this.wrappedBehavior.checkExtraStartConditions(serverLevel, e)) {
            return false;
        }
        if (this.resetTicks) {
            this.resetTicksUntilNextStart(serverLevel);
            this.resetTicks = false;
        }
        if (this.ticksUntilNextStart > 0) {
            --this.ticksUntilNextStart;
        }
        return !this.wasRunning && this.ticksUntilNextStart == 0;
    }

    @Override
    protected void start(ServerLevel serverLevel, E e, long l) {
        this.wrappedBehavior.start(serverLevel, e, l);
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, E e, long l) {
        return this.wrappedBehavior.canStillUse(serverLevel, e, l);
    }

    @Override
    protected void tick(ServerLevel serverLevel, E e, long l) {
        this.wrappedBehavior.tick(serverLevel, e, l);
        this.wasRunning = this.wrappedBehavior.getStatus() == Behavior.Status.RUNNING;
    }

    @Override
    protected void stop(ServerLevel serverLevel, E e, long l) {
        this.resetTicksUntilNextStart(serverLevel);
        this.wrappedBehavior.stop(serverLevel, e, l);
    }

    private void resetTicksUntilNextStart(ServerLevel serverLevel) {
        this.ticksUntilNextStart = this.interval.randomValue(serverLevel.random);
    }

    @Override
    protected boolean timedOut(long l) {
        return false;
    }

    @Override
    public String toString() {
        return "RunSometimes: " + this.wrappedBehavior;
    }
}

