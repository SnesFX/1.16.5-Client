/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.resources.sounds;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class EntityBoundSoundInstance
extends AbstractTickableSoundInstance {
    private final Entity entity;

    public EntityBoundSoundInstance(SoundEvent soundEvent, SoundSource soundSource, Entity entity) {
        this(soundEvent, soundSource, 1.0f, 1.0f, entity);
    }

    public EntityBoundSoundInstance(SoundEvent soundEvent, SoundSource soundSource, float f, float f2, Entity entity) {
        super(soundEvent, soundSource);
        this.volume = f;
        this.pitch = f2;
        this.entity = entity;
        this.x = (float)this.entity.getX();
        this.y = (float)this.entity.getY();
        this.z = (float)this.entity.getZ();
    }

    @Override
    public boolean canPlaySound() {
        return !this.entity.isSilent();
    }

    @Override
    public void tick() {
        if (this.entity.removed) {
            this.stop();
            return;
        }
        this.x = (float)this.entity.getX();
        this.y = (float)this.entity.getY();
        this.z = (float)this.entity.getZ();
    }
}

