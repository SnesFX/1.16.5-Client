/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class WallClimberNavigation
extends GroundPathNavigation {
    private BlockPos pathToPosition;

    public WallClimberNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    public Path createPath(BlockPos blockPos, int n) {
        this.pathToPosition = blockPos;
        return super.createPath(blockPos, n);
    }

    @Override
    public Path createPath(Entity entity, int n) {
        this.pathToPosition = entity.blockPosition();
        return super.createPath(entity, n);
    }

    @Override
    public boolean moveTo(Entity entity, double d) {
        Path path = this.createPath(entity, 0);
        if (path != null) {
            return this.moveTo(path, d);
        }
        this.pathToPosition = entity.blockPosition();
        this.speedModifier = d;
        return true;
    }

    @Override
    public void tick() {
        if (this.isDone()) {
            if (this.pathToPosition != null) {
                if (this.pathToPosition.closerThan(this.mob.position(), (double)this.mob.getBbWidth()) || this.mob.getY() > (double)this.pathToPosition.getY() && new BlockPos((double)this.pathToPosition.getX(), this.mob.getY(), (double)this.pathToPosition.getZ()).closerThan(this.mob.position(), (double)this.mob.getBbWidth())) {
                    this.pathToPosition = null;
                } else {
                    this.mob.getMoveControl().setWantedPosition(this.pathToPosition.getX(), this.pathToPosition.getY(), this.pathToPosition.getZ(), this.speedModifier);
                }
            }
            return;
        }
        super.tick();
    }
}

