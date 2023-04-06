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
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SecondaryPoiSensor
extends Sensor<Villager> {
    public SecondaryPoiSensor() {
        super(40);
    }

    @Override
    protected void doTick(ServerLevel serverLevel, Villager villager) {
        ResourceKey<Level> resourceKey = serverLevel.dimension();
        BlockPos blockPos = villager.blockPosition();
        ArrayList arrayList = Lists.newArrayList();
        int n = 4;
        for (int i = -4; i <= 4; ++i) {
            for (int j = -2; j <= 2; ++j) {
                for (int k = -4; k <= 4; ++k) {
                    BlockPos blockPos2 = blockPos.offset(i, j, k);
                    if (!villager.getVillagerData().getProfession().getSecondaryPoi().contains((Object)serverLevel.getBlockState(blockPos2).getBlock())) continue;
                    arrayList.add(GlobalPos.of(resourceKey, blockPos2));
                }
            }
        }
        Brain<Villager> brain = villager.getBrain();
        if (!arrayList.isEmpty()) {
            brain.setMemory(MemoryModuleType.SECONDARY_JOB_SITE, arrayList);
        } else {
            brain.eraseMemory(MemoryModuleType.SECONDARY_JOB_SITE);
        }
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.SECONDARY_JOB_SITE);
    }
}

