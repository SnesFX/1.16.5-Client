/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.LocateHidingPlace;
import net.minecraft.world.entity.raid.Raid;

public class LocateHidingPlaceDuringRaid
extends LocateHidingPlace {
    public LocateHidingPlaceDuringRaid(int n, float f) {
        super(n, f, 1);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
        Raid raid = serverLevel.getRaidAt(livingEntity.blockPosition());
        return super.checkExtraStartConditions(serverLevel, livingEntity) && raid != null && raid.isActive() && !raid.isVictory() && !raid.isLoss();
    }
}

