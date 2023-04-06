/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;

public class ResetProfession
extends Behavior<Villager> {
    public ResetProfession() {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.JOB_SITE, (Object)((Object)MemoryStatus.VALUE_ABSENT)));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, Villager villager) {
        VillagerData villagerData = villager.getVillagerData();
        return villagerData.getProfession() != VillagerProfession.NONE && villagerData.getProfession() != VillagerProfession.NITWIT && villager.getVillagerXp() == 0 && villagerData.getLevel() <= 1;
    }

    @Override
    protected void start(ServerLevel serverLevel, Villager villager, long l) {
        villager.setVillagerData(villager.getVillagerData().setProfession(VillagerProfession.NONE));
        villager.refreshBrain(serverLevel);
    }
}

