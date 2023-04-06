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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;

public class StopAdmiringIfItemTooFarAway<E extends Piglin>
extends Behavior<E> {
    private final int maxDistanceToItem;

    public StopAdmiringIfItemTooFarAway(int n) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, (Object)((Object)MemoryStatus.REGISTERED)));
        this.maxDistanceToItem = n;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, E e) {
        if (!((LivingEntity)e).getOffhandItem().isEmpty()) {
            return false;
        }
        Optional<ItemEntity> optional = ((Piglin)e).getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
        if (!optional.isPresent()) {
            return true;
        }
        return !optional.get().closerThan((Entity)e, this.maxDistanceToItem);
    }

    @Override
    protected void start(ServerLevel serverLevel, E e, long l) {
        ((Piglin)e).getBrain().eraseMemory(MemoryModuleType.ADMIRING_ITEM);
    }
}

