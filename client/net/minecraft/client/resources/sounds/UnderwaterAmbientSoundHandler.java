/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.resources.sounds;

import java.util.Random;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundInstances;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;

public class UnderwaterAmbientSoundHandler
implements AmbientSoundHandler {
    private final LocalPlayer player;
    private final SoundManager soundManager;
    private int tickDelay = 0;

    public UnderwaterAmbientSoundHandler(LocalPlayer localPlayer, SoundManager soundManager) {
        this.player = localPlayer;
        this.soundManager = soundManager;
    }

    @Override
    public void tick() {
        --this.tickDelay;
        if (this.tickDelay <= 0 && this.player.isUnderWater()) {
            float f = this.player.level.random.nextFloat();
            if (f < 1.0E-4f) {
                this.tickDelay = 0;
                this.soundManager.play(new UnderwaterAmbientSoundInstances.SubSound(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE));
            } else if (f < 0.001f) {
                this.tickDelay = 0;
                this.soundManager.play(new UnderwaterAmbientSoundInstances.SubSound(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_RARE));
            } else if (f < 0.01f) {
                this.tickDelay = 0;
                this.soundManager.play(new UnderwaterAmbientSoundInstances.SubSound(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS));
            }
        }
    }
}

