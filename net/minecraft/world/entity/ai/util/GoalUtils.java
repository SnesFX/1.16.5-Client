/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.util;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

public class GoalUtils {
    public static boolean hasGroundPathNavigation(Mob mob) {
        return mob.getNavigation() instanceof GroundPathNavigation;
    }
}

