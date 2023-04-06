/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.goal.target;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ResetUniversalAngerTargetGoal<T extends Mob>
extends Goal {
    private final T mob;
    private final boolean alertOthersOfSameType;
    private int lastHurtByPlayerTimestamp;

    public ResetUniversalAngerTargetGoal(T t, boolean bl) {
        this.mob = t;
        this.alertOthersOfSameType = bl;
    }

    @Override
    public boolean canUse() {
        return ((Mob)this.mob).level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER) && this.wasHurtByPlayer();
    }

    private boolean wasHurtByPlayer() {
        return ((LivingEntity)this.mob).getLastHurtByMob() != null && ((LivingEntity)this.mob).getLastHurtByMob().getType() == EntityType.PLAYER && ((LivingEntity)this.mob).getLastHurtByMobTimestamp() > this.lastHurtByPlayerTimestamp;
    }

    @Override
    public void start() {
        this.lastHurtByPlayerTimestamp = ((LivingEntity)this.mob).getLastHurtByMobTimestamp();
        ((NeutralMob)this.mob).forgetCurrentTargetAndRefreshUniversalAnger();
        if (this.alertOthersOfSameType) {
            this.getNearbyMobsOfSameType().stream().filter(mob -> mob != this.mob).map(mob -> (NeutralMob)((Object)mob)).forEach(NeutralMob::forgetCurrentTargetAndRefreshUniversalAnger);
        }
        super.start();
    }

    private List<Mob> getNearbyMobsOfSameType() {
        double d = ((LivingEntity)this.mob).getAttributeValue(Attributes.FOLLOW_RANGE);
        AABB aABB = AABB.unitCubeFromLowerCorner(((Entity)this.mob).position()).inflate(d, 10.0, d);
        return ((Mob)this.mob).level.getLoadedEntitiesOfClass(this.mob.getClass(), aABB);
    }
}

