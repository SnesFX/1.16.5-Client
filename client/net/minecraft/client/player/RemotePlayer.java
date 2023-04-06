/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.client.player;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RemotePlayer
extends AbstractClientPlayer {
    public RemotePlayer(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
        this.maxUpStep = 1.0f;
        this.noPhysics = true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double d) {
        double d2 = this.getBoundingBox().getSize() * 10.0;
        if (Double.isNaN(d2)) {
            d2 = 1.0;
        }
        return d < (d2 *= 64.0 * RemotePlayer.getViewScale()) * d2;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        this.calculateEntityAnimation(this, false);
    }

    @Override
    public void aiStep() {
        if (this.lerpSteps > 0) {
            double d = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
            double d2 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
            double d3 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
            this.yRot = (float)((double)this.yRot + Mth.wrapDegrees(this.lerpYRot - (double)this.yRot) / (double)this.lerpSteps);
            this.xRot = (float)((double)this.xRot + (this.lerpXRot - (double)this.xRot) / (double)this.lerpSteps);
            --this.lerpSteps;
            this.setPos(d, d2, d3);
            this.setRot(this.yRot, this.xRot);
        }
        if (this.lerpHeadSteps > 0) {
            this.yHeadRot = (float)((double)this.yHeadRot + Mth.wrapDegrees(this.lyHeadRot - (double)this.yHeadRot) / (double)this.lerpHeadSteps);
            --this.lerpHeadSteps;
        }
        this.oBob = this.bob;
        this.updateSwingTime();
        float f = !this.onGround || this.isDeadOrDying() ? 0.0f : Math.min(0.1f, Mth.sqrt(RemotePlayer.getHorizontalDistanceSqr(this.getDeltaMovement())));
        if (this.onGround || this.isDeadOrDying()) {
            float f2 = 0.0f;
        } else {
            float f3 = (float)Math.atan(-this.getDeltaMovement().y * 0.20000000298023224) * 15.0f;
        }
        this.bob += (f - this.bob) * 0.4f;
        this.level.getProfiler().push("push");
        this.pushEntities();
        this.level.getProfiler().pop();
    }

    @Override
    protected void updatePlayerPose() {
    }

    @Override
    public void sendMessage(Component component, UUID uUID) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.isBlocked(uUID)) {
            minecraft.gui.getChat().addMessage(component);
        }
    }
}

