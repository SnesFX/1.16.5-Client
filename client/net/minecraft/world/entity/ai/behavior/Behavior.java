/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.behavior;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public abstract class Behavior<E extends LivingEntity> {
    protected final Map<MemoryModuleType<?>, MemoryStatus> entryCondition;
    private Status status = Status.STOPPED;
    private long endTimestamp;
    private final int minDuration;
    private final int maxDuration;

    public Behavior(Map<MemoryModuleType<?>, MemoryStatus> map) {
        this(map, 60);
    }

    public Behavior(Map<MemoryModuleType<?>, MemoryStatus> map, int n) {
        this(map, n, n);
    }

    public Behavior(Map<MemoryModuleType<?>, MemoryStatus> map, int n, int n2) {
        this.minDuration = n;
        this.maxDuration = n2;
        this.entryCondition = map;
    }

    public Status getStatus() {
        return this.status;
    }

    public final boolean tryStart(ServerLevel serverLevel, E e, long l) {
        if (this.hasRequiredMemories(e) && this.checkExtraStartConditions(serverLevel, e)) {
            this.status = Status.RUNNING;
            int n = this.minDuration + serverLevel.getRandom().nextInt(this.maxDuration + 1 - this.minDuration);
            this.endTimestamp = l + (long)n;
            this.start(serverLevel, e, l);
            return true;
        }
        return false;
    }

    protected void start(ServerLevel serverLevel, E e, long l) {
    }

    public final void tickOrStop(ServerLevel serverLevel, E e, long l) {
        if (!this.timedOut(l) && this.canStillUse(serverLevel, e, l)) {
            this.tick(serverLevel, e, l);
        } else {
            this.doStop(serverLevel, e, l);
        }
    }

    protected void tick(ServerLevel serverLevel, E e, long l) {
    }

    public final void doStop(ServerLevel serverLevel, E e, long l) {
        this.status = Status.STOPPED;
        this.stop(serverLevel, e, l);
    }

    protected void stop(ServerLevel serverLevel, E e, long l) {
    }

    protected boolean canStillUse(ServerLevel serverLevel, E e, long l) {
        return false;
    }

    protected boolean timedOut(long l) {
        return l > this.endTimestamp;
    }

    protected boolean checkExtraStartConditions(ServerLevel serverLevel, E e) {
        return true;
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

    private boolean hasRequiredMemories(E e) {
        for (Map.Entry<MemoryModuleType<?>, MemoryStatus> entry : this.entryCondition.entrySet()) {
            MemoryModuleType<?> memoryModuleType = entry.getKey();
            MemoryStatus memoryStatus = entry.getValue();
            if (((LivingEntity)e).getBrain().checkMemory(memoryModuleType, memoryStatus)) continue;
            return false;
        }
        return true;
    }

    public static enum Status {
        STOPPED,
        RUNNING;
        
    }

}

