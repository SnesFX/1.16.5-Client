/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class TradeWithPlayerGoal
extends Goal {
    private final AbstractVillager mob;

    public TradeWithPlayerGoal(AbstractVillager abstractVillager) {
        this.mob = abstractVillager;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!this.mob.isAlive()) {
            return false;
        }
        if (this.mob.isInWater()) {
            return false;
        }
        if (!this.mob.isOnGround()) {
            return false;
        }
        if (this.mob.hurtMarked) {
            return false;
        }
        Player player = this.mob.getTradingPlayer();
        if (player == null) {
            return false;
        }
        if (this.mob.distanceToSqr(player) > 16.0) {
            return false;
        }
        return player.containerMenu != null;
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.mob.setTradingPlayer(null);
    }
}

