/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;

public class StopAdmiringIfTiredOfTryingToReachItem<E extends Piglin>
extends Behavior<E> {
    private final int maxTimeToReachItem;
    private final int disableTime;

    public StopAdmiringIfTiredOfTryingToReachItem(int n, int n2) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, (Object)((Object)MemoryStatus.REGISTERED), MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, (Object)((Object)MemoryStatus.REGISTERED)));
        this.maxTimeToReachItem = n;
        this.disableTime = n2;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, E e) {
        return ((LivingEntity)e).getOffhandItem().isEmpty();
    }

    @Override
    protected void start(ServerLevel serverLevel, E e, long l) {
        Brain<Piglin> brain = ((Piglin)e).getBrain();
        Optional<Integer> optional = brain.getMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
        if (!optional.isPresent()) {
            brain.setMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, 0);
        } else {
            int n = optional.get();
            if (n > this.maxTimeToReachItem) {
                brain.eraseMemory(MemoryModuleType.ADMIRING_ITEM);
                brain.eraseMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
                brain.setMemoryWithExpiry(MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, true, this.disableTime);
            } else {
                brain.setMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM, n + 1);
            }
        }
    }
}

