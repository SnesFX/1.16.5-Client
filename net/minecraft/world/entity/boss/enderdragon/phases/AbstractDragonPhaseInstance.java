/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractDragonPhaseInstance
implements DragonPhaseInstance {
    protected final EnderDragon dragon;

    public AbstractDragonPhaseInstance(EnderDragon enderDragon) {
        this.dragon = enderDragon;
    }

    @Override
    public boolean isSitting() {
        return false;
    }

    @Override
    public void doClientTick() {
    }

    @Override
    public void doServerTick() {
    }

    @Override
    public void onCrystalDestroyed(EndCrystal endCrystal, BlockPos blockPos, DamageSource damageSource, @Nullable Player player) {
    }

    @Override
    public void begin() {
    }

    @Override
    public void end() {
    }

    @Override
    public float getFlySpeed() {
        return 0.6f;
    }

    @Nullable
    @Override
    public Vec3 getFlyTargetLocation() {
        return null;
    }

    @Override
    public float onHurt(DamageSource damageSource, float f) {
        return f;
    }

    @Override
    public float getTurnSpeed() {
        float f = Mth.sqrt(Entity.getHorizontalDistanceSqr(this.dragon.getDeltaMovement())) + 1.0f;
        float f2 = Math.min(f, 40.0f);
        return 0.7f / f2 / f;
    }
}

