/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.MoveToSkySeeingSpot;
import net.minecraft.world.entity.raid.Raid;

public class GoOutsideToCelebrate
extends MoveToSkySeeingSpot {
    public GoOutsideToCelebrate(float f) {
        super(f);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
        Raid raid = serverLevel.getRaidAt(livingEntity.blockPosition());
        return raid != null && raid.isVictory() && super.checkExtraStartConditions(serverLevel, livingEntity);
    }
}

