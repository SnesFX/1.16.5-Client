/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.resources.sounds;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.BeeAggressiveSoundInstance;
import net.minecraft.client.resources.sounds.BeeSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.Bee;

public class BeeFlyingSoundInstance
extends BeeSoundInstance {
    public BeeFlyingSoundInstance(Bee bee) {
        super(bee, SoundEvents.BEE_LOOP, SoundSource.NEUTRAL);
    }

    @Override
    protected AbstractTickableSoundInstance getAlternativeSoundInstance() {
        return new BeeAggressiveSoundInstance(this.bee);
    }

    @Override
    protected boolean shouldSwitchSounds() {
        return this.bee.isAngry();
    }
}

