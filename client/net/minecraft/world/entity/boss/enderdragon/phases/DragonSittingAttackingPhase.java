/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.boss.enderdragon.phases;

import java.util.Random;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonSittingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonSittingFlamingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import net.minecraft.world.level.Level;

public class DragonSittingAttackingPhase
extends AbstractDragonSittingPhase {
    private int attackingTicks;

    public DragonSittingAttackingPhase(EnderDragon enderDragon) {
        super(enderDragon);
    }

    @Override
    public void doClientTick() {
        this.dragon.level.playLocalSound(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ(), SoundEvents.ENDER_DRAGON_GROWL, this.dragon.getSoundSource(), 2.5f, 0.8f + this.dragon.getRandom().nextFloat() * 0.3f, false);
    }

    @Override
    public void doServerTick() {
        if (this.attackingTicks++ >= 40) {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.SITTING_FLAMING);
        }
    }

    @Override
    public void begin() {
        this.attackingTicks = 0;
    }

    public EnderDragonPhase<DragonSittingAttackingPhase> getPhase() {
        return EnderDragonPhase.SITTING_ATTACKING;
    }
}

