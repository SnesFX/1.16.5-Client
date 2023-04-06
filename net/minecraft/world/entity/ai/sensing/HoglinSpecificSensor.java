/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 */
package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class HoglinSpecificSensor
extends Sensor<Hoglin> {
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_REPELLENT, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, (Object[])new MemoryModuleType[0]);
    }

    @Override
    protected void doTick(ServerLevel serverLevel, Hoglin hoglin) {
        Brain<Hoglin> brain = hoglin.getBrain();
        brain.setMemory(MemoryModuleType.NEAREST_REPELLENT, this.findNearestRepellent(serverLevel, hoglin));
        Optional<Object> optional = Optional.empty();
        int n = 0;
        ArrayList arrayList = Lists.newArrayList();
        List<LivingEntity> list = brain.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).orElse(Lists.newArrayList());
        for (LivingEntity livingEntity : list) {
            if (livingEntity instanceof Piglin && !livingEntity.isBaby()) {
                ++n;
                if (!optional.isPresent()) {
                    optional = Optional.of((Piglin)livingEntity);
                }
            }
            if (!(livingEntity instanceof Hoglin) || livingEntity.isBaby()) continue;
            arrayList.add((Hoglin)livingEntity);
        }
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, optional);
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, arrayList);
        brain.setMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, n);
        brain.setMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, arrayList.size());
    }

    private Optional<BlockPos> findNearestRepellent(ServerLevel serverLevel, Hoglin hoglin) {
        return BlockPos.findClosestMatch(hoglin.blockPosition(), 8, 4, blockPos -> serverLevel.getBlockState((BlockPos)blockPos).is(BlockTags.HOGLIN_REPELLENTS));
    }
}

