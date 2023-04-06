/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.resources.sounds;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.phys.Vec3;

public class RidingMinecartSoundInstance
extends AbstractTickableSoundInstance {
    private final Player player;
    private final AbstractMinecart minecart;

    public RidingMinecartSoundInstance(Player player, AbstractMinecart abstractMinecart) {
        super(SoundEvents.MINECART_INSIDE, SoundSource.NEUTRAL);
        this.player = player;
        this.minecart = abstractMinecart;
        this.attenuation = SoundInstance.Attenuation.NONE;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.0f;
    }

    @Override
    public boolean canPlaySound() {
        return !this.minecart.isSilent();
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        if (this.minecart.removed || !this.player.isPassenger() || this.player.getVehicle() != this.minecart) {
            this.stop();
            return;
        }
        float f = Mth.sqrt(Entity.getHorizontalDistanceSqr(this.minecart.getDeltaMovement()));
        this.volume = (double)f >= 0.01 ? 0.0f + Mth.clamp(f, 0.0f, 1.0f) * 0.75f : 0.0f;
    }
}

