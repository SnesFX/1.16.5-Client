/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.resources.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.phys.Vec3;

public abstract class BeeSoundInstance
extends AbstractTickableSoundInstance {
    protected final Bee bee;
    private boolean hasSwitched;

    public BeeSoundInstance(Bee bee, SoundEvent soundEvent, SoundSource soundSource) {
        super(soundEvent, soundSource);
        this.bee = bee;
        this.x = (float)bee.getX();
        this.y = (float)bee.getY();
        this.z = (float)bee.getZ();
        this.looping = true;
        this.delay = 0;
        this.volume = 0.0f;
    }

    @Override
    public void tick() {
        boolean bl = this.shouldSwitchSounds();
        if (bl && !this.isStopped()) {
            Minecraft.getInstance().getSoundManager().queueTickingSound(this.getAlternativeSoundInstance());
            this.hasSwitched = true;
        }
        if (this.bee.removed || this.hasSwitched) {
            this.stop();
            return;
        }
        this.x = (float)this.bee.getX();
        this.y = (float)this.bee.getY();
        this.z = (float)this.bee.getZ();
        float f = Mth.sqrt(Entity.getHorizontalDistanceSqr(this.bee.getDeltaMovement()));
        if ((double)f >= 0.01) {
            this.pitch = Mth.lerp(Mth.clamp(f, this.getMinPitch(), this.getMaxPitch()), this.getMinPitch(), this.getMaxPitch());
            this.volume = Mth.lerp(Mth.clamp(f, 0.0f, 0.5f), 0.0f, 1.2f);
        } else {
            this.pitch = 0.0f;
            this.volume = 0.0f;
        }
    }

    private float getMinPitch() {
        if (this.bee.isBaby()) {
            return 1.1f;
        }
        return 0.7f;
    }

    private float getMaxPitch() {
        if (this.bee.isBaby()) {
            return 1.5f;
        }
        return 1.1f;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public boolean canPlaySound() {
        return !this.bee.isSilent();
    }

    protected abstract AbstractTickableSoundInstance getAlternativeSoundInstance();

    protected abstract boolean shouldSwitchSounds();
}

