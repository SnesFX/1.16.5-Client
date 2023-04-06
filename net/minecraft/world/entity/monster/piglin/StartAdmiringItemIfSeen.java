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
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class StartAdmiringItemIfSeen<E extends Piglin>
extends Behavior<E> {
    private final int admireDuration;

    public StartAdmiringItemIfSeen(int n) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.ADMIRING_ITEM, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.ADMIRING_DISABLED, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM, (Object)((Object)MemoryStatus.VALUE_ABSENT)));
        this.admireDuration = n;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, E e) {
        ItemEntity itemEntity = ((Piglin)e).getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM).get();
        return PiglinAi.isLovedItem(itemEntity.getItem().getItem());
    }

    @Override
    protected void start(ServerLevel serverLevel, E e, long l) {
        ((Piglin)e).getBrain().setMemoryWithExpiry(MemoryModuleType.ADMIRING_ITEM, true, this.admireDuration);
    }
}

