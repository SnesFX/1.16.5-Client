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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Position;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AssignProfessionFromJobSite
extends Behavior<Villager> {
    public AssignProfessionFromJobSite() {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, (Object)((Object)MemoryStatus.VALUE_PRESENT)));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, Villager villager) {
        BlockPos blockPos = villager.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().pos();
        return blockPos.closerThan(villager.position(), 2.0) || villager.assignProfessionWhenSpawned();
    }

    @Override
    protected void start(ServerLevel serverLevel2, Villager villager, long l) {
        GlobalPos globalPos = villager.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get();
        villager.getBrain().eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
        villager.getBrain().setMemory(MemoryModuleType.JOB_SITE, globalPos);
        serverLevel2.broadcastEntityEvent(villager, (byte)14);
        if (villager.getVillagerData().getProfession() != VillagerProfession.NONE) {
            return;
        }
        MinecraftServer minecraftServer = serverLevel2.getServer();
        Optional.ofNullable(minecraftServer.getLevel(globalPos.dimension())).flatMap(serverLevel -> serverLevel.getPoiManager().getType(globalPos.pos())).flatMap(poiType -> Registry.VILLAGER_PROFESSION.stream().filter(villagerProfession -> villagerProfession.getJobPoiType() == poiType).findFirst()).ifPresent(villagerProfession -> {
            villager.setVillagerData(villager.getVillagerData().setProfession((VillagerProfession)villagerProfession));
            villager.refreshBrain(serverLevel2);
        });
    }
}

