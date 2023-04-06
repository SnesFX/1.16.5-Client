/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundSource;

public interface Saddleable {
    public boolean isSaddleable();

    public void equipSaddle(@Nullable SoundSource var1);

    public boolean isSaddled();
}

