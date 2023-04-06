/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.Vec3;

public class GoToClosestVillage
extends Behavior<Villager> {
    private final float speedModifier;
    private final int closeEnoughDistance;

    public GoToClosestVillage(float f, int n) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT)));
        this.speedModifier = f;
        this.closeEnoughDistance = n;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, Villager villager) {
        return !serverLevel.isVillage(villager.blockPosition());
    }

    @Override
    protected void start(ServerLevel serverLevel, Villager villager, long l) {
        PoiManager poiManager = serverLevel.getPoiManager();
        int n = poiManager.sectionsToVillage(SectionPos.of(villager.blockPosition()));
        Vec3 vec3 = null;
        for (int i = 0; i < 5; ++i) {
            Vec3 vec32 = RandomPos.getLandPos(villager, 15, 7, blockPos -> -serverLevel.sectionsToVillage(SectionPos.of(blockPos)));
            if (vec32 == null) continue;
            int n2 = poiManager.sectionsToVillage(SectionPos.of(new BlockPos(vec32)));
            if (n2 < n) {
                vec3 = vec32;
                break;
            }
            if (n2 != n) continue;
            vec3 = vec32;
        }
        if (vec3 != null) {
            villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3, this.speedModifier, this.closeEnoughDistance));
        }
    }
}

