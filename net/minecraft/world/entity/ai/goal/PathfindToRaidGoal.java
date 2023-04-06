/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package net.minecraft.world.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PathfindToRaidGoal<T extends Raider>
extends Goal {
    private final T mob;

    public PathfindToRaidGoal(T t) {
        this.mob = t;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return ((Mob)this.mob).getTarget() == null && !((Entity)this.mob).isVehicle() && ((Raider)this.mob).hasActiveRaid() && !((Raider)this.mob).getCurrentRaid().isOver() && !((ServerLevel)((Raider)this.mob).level).isVillage(((Entity)this.mob).blockPosition());
    }

    @Override
    public boolean canContinueToUse() {
        return ((Raider)this.mob).hasActiveRaid() && !((Raider)this.mob).getCurrentRaid().isOver() && ((Raider)this.mob).level instanceof ServerLevel && !((ServerLevel)((Raider)this.mob).level).isVillage(((Entity)this.mob).blockPosition());
    }

    @Override
    public void tick() {
        if (((Raider)this.mob).hasActiveRaid()) {
            Vec3 vec3;
            Raid raid = ((Raider)this.mob).getCurrentRaid();
            if (((Raider)this.mob).tickCount % 20 == 0) {
                this.recruitNearby(raid);
            }
            if (!((PathfinderMob)this.mob).isPathFinding() && (vec3 = RandomPos.getPosTowards(this.mob, 15, 4, Vec3.atBottomCenterOf(raid.getCenter()))) != null) {
                ((Mob)this.mob).getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.0);
            }
        }
    }

    private void recruitNearby(Raid raid) {
        if (raid.isActive()) {
            HashSet hashSet = Sets.newHashSet();
            List<Raider> list = ((Raider)this.mob).level.getEntitiesOfClass(Raider.class, ((Entity)this.mob).getBoundingBox().inflate(16.0), raider -> !raider.hasActiveRaid() && Raids.canJoinRaid(raider, raid));
            hashSet.addAll(list);
            for (Raider raider2 : hashSet) {
                raid.joinRaid(raid.getGroupsSpawned(), raider2, null, true);
            }
        }
    }
}

