/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;

public class InteractGoal
extends LookAtPlayerGoal {
    public InteractGoal(Mob mob, Class<? extends LivingEntity> class_, float f, float f2) {
        super(mob, class_, f, f2);
        this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));
    }
}

