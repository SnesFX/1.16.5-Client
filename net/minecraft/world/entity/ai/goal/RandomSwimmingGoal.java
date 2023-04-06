/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

public class RandomSwimmingGoal
extends RandomStrollGoal {
    public RandomSwimmingGoal(PathfinderMob pathfinderMob, double d, int n) {
        super(pathfinderMob, d, n);
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        Vec3 vec3 = RandomPos.getPos(this.mob, 10, 7);
        int n = 0;
        while (vec3 != null && !this.mob.level.getBlockState(new BlockPos(vec3)).isPathfindable(this.mob.level, new BlockPos(vec3), PathComputationType.WATER) && n++ < 10) {
            vec3 = RandomPos.getPos(this.mob, 10, 7);
        }
        return vec3;
    }
}

