/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.vehicle;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;

public class Minecart
extends AbstractMinecart {
    public Minecart(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public Minecart(Level level, double d, double d2, double d3) {
        super(EntityType.MINECART, level, d, d2, d3);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand interactionHand) {
        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }
        if (this.isVehicle()) {
            return InteractionResult.PASS;
        }
        if (!this.level.isClientSide) {
            return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void activateMinecart(int n, int n2, int n3, boolean bl) {
        if (bl) {
            if (this.isVehicle()) {
                this.ejectPassengers();
            }
            if (this.getHurtTime() == 0) {
                this.setHurtDir(-this.getHurtDir());
                this.setHurtTime(10);
                this.setDamage(50.0f);
                this.markHurt();
            }
        }
    }

    @Override
    public AbstractMinecart.Type getMinecartType() {
        return AbstractMinecart.Type.RIDEABLE;
    }
}

