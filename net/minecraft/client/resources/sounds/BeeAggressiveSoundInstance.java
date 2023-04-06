/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.resources.sounds;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.BeeFlyingSoundInstance;
import net.minecraft.client.resources.sounds.BeeSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.Bee;

public class BeeAggressiveSoundInstance
extends BeeSoundInstance {
    public BeeAggressiveSoundInstance(Bee bee) {
        super(bee, SoundEvents.BEE_LOOP_AGGRESSIVE, SoundSource.NEUTRAL);
        this.delay = 0;
    }

    @Override
    protected AbstractTickableSoundInstance getAlternativeSoundInstance() {
        return new BeeFlyingSoundInstance(this.bee);
    }

    @Override
    protected boolean shouldSwitchSounds() {
        return !this.bee.isAngry();
    }
}

