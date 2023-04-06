/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.goal;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RestrictSunGoal
extends Goal {
    private final PathfinderMob mob;

    public RestrictSunGoal(PathfinderMob pathfinderMob) {
        this.mob = pathfinderMob;
    }

    @Override
    public boolean canUse() {
        return this.mob.level.isDay() && this.mob.getItemBySlot(EquipmentSlot.HEAD).isEmpty() && GoalUtils.hasGroundPathNavigation(this.mob);
    }

    @Override
    public void start() {
        ((GroundPathNavigation)this.mob.getNavigation()).setAvoidSun(true);
    }

    @Override
    public void stop() {
        if (GoalUtils.hasGroundPathNavigation(this.mob)) {
            ((GroundPathNavigation)this.mob.getNavigation()).setAvoidSun(false);
        }
    }
}

