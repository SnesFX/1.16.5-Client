/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.resources.sounds;

import net.minecraft.client.resources.sounds.SoundInstance;

public interface TickableSoundInstance
extends SoundInstance {
    public boolean isStopped();

    public void tick();
}

