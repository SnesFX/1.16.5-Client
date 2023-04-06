/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MoveBackToVillageGoal
extends RandomStrollGoal {
    public MoveBackToVillageGoal(PathfinderMob pathfinderMob, double d, boolean bl) {
        super(pathfinderMob, d, 10, bl);
    }

    @Override
    public boolean canUse() {
        ServerLevel serverLevel = (ServerLevel)this.mob.level;
        BlockPos blockPos = this.mob.blockPosition();
        if (serverLevel.isVillage(blockPos)) {
            return false;
        }
        return super.canUse();
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        ServerLevel serverLevel = (ServerLevel)this.mob.level;
        BlockPos blockPos = this.mob.blockPosition();
        SectionPos sectionPos = SectionPos.of(blockPos);
        SectionPos sectionPos2 = BehaviorUtils.findSectionClosestToVillage(serverLevel, sectionPos, 2);
        if (sectionPos2 != sectionPos) {
            return RandomPos.getPosTowards(this.mob, 10, 7, Vec3.atBottomCenterOf(sectionPos2.center()));
        }
        return null;
    }
}

