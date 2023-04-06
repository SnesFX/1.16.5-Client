/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.sounds;

import net.minecraft.client.sounds.SoundEngine;

public interface Weighted<T> {
    public int getWeight();

    public T getSound();

    public void preloadIfRequired(SoundEngine var1);
}

