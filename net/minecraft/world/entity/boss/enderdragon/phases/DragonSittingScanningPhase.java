/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.boss.enderdragon.phases;

import java.util.function.Predicate;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonSittingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonChargePlayerPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonSittingAttackingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonTakeoffPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DragonSittingScanningPhase
extends AbstractDragonSittingPhase {
    private static final TargetingConditions CHARGE_TARGETING = new TargetingConditions().range(150.0);
    private final TargetingConditions scanTargeting = new TargetingConditions().range(20.0).selector(livingEntity -> Math.abs(livingEntity.getY() - enderDragon.getY()) <= 10.0);
    private int scanningTime;

    public DragonSittingScanningPhase(EnderDragon enderDragon) {
        super(enderDragon);
    }

    @Override
    public void doServerTick() {
        ++this.scanningTime;
        Player player = this.dragon.level.getNearestPlayer(this.scanTargeting, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
        if (player != null) {
            if (this.scanningTime > 25) {
                this.dragon.getPhaseManager().setPhase(EnderDragonPhase.SITTING_ATTACKING);
            } else {
                Vec3 vec3 = new Vec3(player.getX() - this.dragon.getX(), 0.0, player.getZ() - this.dragon.getZ()).normalize();
                Vec3 vec32 = new Vec3(Mth.sin(this.dragon.yRot * 0.017453292f), 0.0, -Mth.cos(this.dragon.yRot * 0.017453292f)).normalize();
                float f = (float)vec32.dot(vec3);
                float f2 = (float)(Math.acos(f) * 57.2957763671875) + 0.5f;
                if (f2 < 0.0f || f2 > 10.0f) {
                    float f3;
                    double d = player.getX() - this.dragon.head.getX();
                    double d2 = player.getZ() - this.dragon.head.getZ();
                    double d3 = Mth.clamp(Mth.wrapDegrees(180.0 - Mth.atan2(d, d2) * 57.2957763671875 - (double)this.dragon.yRot), -100.0, 100.0);
                    this.dragon.yRotA *= 0.8f;
                    float f4 = f3 = Mth.sqrt(d * d + d2 * d2) + 1.0f;
                    if (f3 > 40.0f) {
                        f3 = 40.0f;
                    }
                    this.dragon.yRotA = (float)((double)this.dragon.yRotA + d3 * (double)(0.7f / f3 / f4));
                    this.dragon.yRot += this.dragon.yRotA;
                }
            }
        } else if (this.scanningTime >= 100) {
            player = this.dragon.level.getNearestPlayer(CHARGE_TARGETING, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.TAKEOFF);
            if (player != null) {
                this.dragon.getPhaseManager().setPhase(EnderDragonPhase.CHARGING_PLAYER);
                this.dragon.getPhaseManager().getPhase(EnderDragonPhase.CHARGING_PLAYER).setTarget(new Vec3(player.getX(), player.getY(), player.getZ()));
            }
        }
    }

    @Override
    public void begin() {
        this.scanningTime = 0;
    }

    public EnderDragonPhase<DragonSittingScanningPhase> getPhase() {
        return EnderDragonPhase.SITTING_SCANNING;
    }
}

