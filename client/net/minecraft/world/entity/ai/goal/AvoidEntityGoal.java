/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AvoidEntityGoal<T extends LivingEntity>
extends Goal {
    protected final PathfinderMob mob;
    private final double walkSpeedModifier;
    private final double sprintSpeedModifier;
    protected T toAvoid;
    protected final float maxDist;
    protected Path path;
    protected final PathNavigation pathNav;
    protected final Class<T> avoidClass;
    protected final Predicate<LivingEntity> avoidPredicate;
    protected final Predicate<LivingEntity> predicateOnAvoidEntity;
    private final TargetingConditions avoidEntityTargeting;

    public AvoidEntityGoal(PathfinderMob pathfinderMob, Class<T> class_, float f, double d, double d2) {
        this(pathfinderMob, class_, livingEntity -> true, f, d, d2, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
    }

    public AvoidEntityGoal(PathfinderMob pathfinderMob, Class<T> class_, Predicate<LivingEntity> predicate, float f, double d, double d2, Predicate<LivingEntity> predicate2) {
        this.mob = pathfinderMob;
        this.avoidClass = class_;
        this.avoidPredicate = predicate;
        this.maxDist = f;
        this.walkSpeedModifier = d;
        this.sprintSpeedModifier = d2;
        this.predicateOnAvoidEntity = predicate2;
        this.pathNav = pathfinderMob.getNavigation();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.avoidEntityTargeting = new TargetingConditions().range(f).selector(predicate2.and(predicate));
    }

    public AvoidEntityGoal(PathfinderMob pathfinderMob, Class<T> class_, float f, double d, double d2, Predicate<LivingEntity> predicate) {
        this(pathfinderMob, class_, livingEntity -> true, f, d, d2, predicate);
    }

    @Override
    public boolean canUse() {
        this.toAvoid = this.mob.level.getNearestLoadedEntity(this.avoidClass, this.avoidEntityTargeting, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ(), this.mob.getBoundingBox().inflate(this.maxDist, 3.0, this.maxDist));
        if (this.toAvoid == null) {
            return false;
        }
        Vec3 vec3 = RandomPos.getPosAvoid(this.mob, 16, 7, ((Entity)this.toAvoid).position());
        if (vec3 == null) {
            return false;
        }
        if (((Entity)this.toAvoid).distanceToSqr(vec3.x, vec3.y, vec3.z) < ((Entity)this.toAvoid).distanceToSqr(this.mob)) {
            return false;
        }
        this.path = this.pathNav.createPath(vec3.x, vec3.y, vec3.z, 0);
        return this.path != null;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.pathNav.isDone();
    }

    @Override
    public void start() {
        this.pathNav.moveTo(this.path, this.walkSpeedModifier);
    }

    @Override
    public void stop() {
        this.toAvoid = null;
    }

    @Override
    public void tick() {
        if (this.mob.distanceToSqr((Entity)this.toAvoid) < 49.0) {
            this.mob.getNavigation().setSpeedModifier(this.sprintSpeedModifier);
        } else {
            this.mob.getNavigation().setSpeedModifier(this.walkSpeedModifier);
        }
    }
}

