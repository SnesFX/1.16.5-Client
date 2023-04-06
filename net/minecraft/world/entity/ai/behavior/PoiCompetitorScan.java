/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;

public class PoiCompetitorScan
extends Behavior<Villager> {
    final VillagerProfession profession;

    public PoiCompetitorScan(VillagerProfession villagerProfession) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.JOB_SITE, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.LIVING_ENTITIES, (Object)((Object)MemoryStatus.VALUE_PRESENT)));
        this.profession = villagerProfession;
    }

    @Override
    protected void start(ServerLevel serverLevel, Villager villager, long l) {
        GlobalPos globalPos = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE).get();
        serverLevel.getPoiManager().getType(globalPos.pos()).ifPresent(poiType -> BehaviorUtils.getNearbyVillagersWithCondition(villager, villager -> this.competesForSameJobsite(globalPos, (PoiType)poiType, (Villager)villager)).reduce(villager, (arg_0, arg_1) -> PoiCompetitorScan.selectWinner(arg_0, arg_1)));
    }

    private static Villager selectWinner(Villager villager, Villager villager2) {
        Villager villager3;
        Villager villager4;
        if (villager.getVillagerXp() > villager2.getVillagerXp()) {
            villager4 = villager;
            villager3 = villager2;
        } else {
            villager4 = villager2;
            villager3 = villager;
        }
        villager3.getBrain().eraseMemory(MemoryModuleType.JOB_SITE);
        return villager4;
    }

    private boolean competesForSameJobsite(GlobalPos globalPos, PoiType poiType, Villager villager) {
        return this.hasJobSite(villager) && globalPos.equals(villager.getBrain().getMemory(MemoryModuleType.JOB_SITE).get()) && this.hasMatchingProfession(poiType, villager.getVillagerData().getProfession());
    }

    private boolean hasMatchingProfession(PoiType poiType, VillagerProfession villagerProfession) {
        return villagerProfession.getJobPoiType().getPredicate().test(poiType);
    }

    private boolean hasJobSite(Villager villager) {
        return villager.getBrain().getMemory(MemoryModuleType.JOB_SITE).isPresent();
    }
}

