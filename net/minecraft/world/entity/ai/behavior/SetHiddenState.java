/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class SetHiddenState
extends Behavior<LivingEntity> {
    private final int closeEnoughDist;
    private final int stayHiddenTicks;
    private int ticksHidden;

    public SetHiddenState(int n, int n2) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.HIDING_PLACE, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.HEARD_BELL_TIME, (Object)((Object)MemoryStatus.VALUE_PRESENT)));
        this.stayHiddenTicks = n * 20;
        this.ticksHidden = 0;
        this.closeEnoughDist = n2;
    }

    @Override
    protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long l) {
        boolean bl;
        Brain<?> brain = livingEntity.getBrain();
        Optional<Long> optional = brain.getMemory(MemoryModuleType.HEARD_BELL_TIME);
        boolean bl2 = bl = optional.get() + 300L <= l;
        if (this.ticksHidden > this.stayHiddenTicks || bl) {
            brain.eraseMemory(MemoryModuleType.HEARD_BELL_TIME);
            brain.eraseMemory(MemoryModuleType.HIDING_PLACE);
            brain.updateActivityFromSchedule(serverLevel.getDayTime(), serverLevel.getGameTime());
            this.ticksHidden = 0;
            return;
        }
        BlockPos blockPos = brain.getMemory(MemoryModuleType.HIDING_PLACE).get().pos();
        if (blockPos.closerThan(livingEntity.blockPosition(), (double)this.closeEnoughDist)) {
            ++this.ticksHidden;
        }
    }
}

