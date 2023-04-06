/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class NearestItemSensor
extends Sensor<Mob> {
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
    }

    @Override
    protected void doTick(ServerLevel serverLevel, Mob mob) {
        Brain<?> brain = mob.getBrain();
        List<ItemEntity> list = serverLevel.getEntitiesOfClass(ItemEntity.class, mob.getBoundingBox().inflate(8.0, 4.0, 8.0), itemEntity -> true);
        list.sort(Comparator.comparingDouble(mob::distanceToSqr));
        Optional<ItemEntity> optional = list.stream().filter(itemEntity -> mob.wantsToPickUp(itemEntity.getItem())).filter(itemEntity -> itemEntity.closerThan(mob, 9.0)).filter(mob::canSee).findFirst();
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, optional);
    }
}

