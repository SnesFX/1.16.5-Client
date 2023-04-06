/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.resources.sounds;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SimpleSoundInstance
extends AbstractSoundInstance {
    public SimpleSoundInstance(SoundEvent soundEvent, SoundSource soundSource, float f, float f2, BlockPos blockPos) {
        this(soundEvent, soundSource, f, f2, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
    }

    public static SimpleSoundInstance forUI(SoundEvent soundEvent, float f) {
        return SimpleSoundInstance.forUI(soundEvent, f, 0.25f);
    }

    public static SimpleSoundInstance forUI(SoundEvent soundEvent, float f, float f2) {
        return new SimpleSoundInstance(soundEvent.getLocation(), SoundSource.MASTER, f2, f, false, 0, SoundInstance.Attenuation.NONE, 0.0, 0.0, 0.0, true);
    }

    public static SimpleSoundInstance forMusic(SoundEvent soundEvent) {
        return new SimpleSoundInstance(soundEvent.getLocation(), SoundSource.MUSIC, 1.0f, 1.0f, false, 0, SoundInstance.Attenuation.NONE, 0.0, 0.0, 0.0, true);
    }

    public static SimpleSoundInstance forRecord(SoundEvent soundEvent, double d, double d2, double d3) {
        return new SimpleSoundInstance(soundEvent, SoundSource.RECORDS, 4.0f, 1.0f, false, 0, SoundInstance.Attenuation.LINEAR, d, d2, d3);
    }

    public static SimpleSoundInstance forLocalAmbience(SoundEvent soundEvent, float f, float f2) {
        return new SimpleSoundInstance(soundEvent.getLocation(), SoundSource.AMBIENT, f2, f, false, 0, SoundInstance.Attenuation.NONE, 0.0, 0.0, 0.0, true);
    }

    public static SimpleSoundInstance forAmbientAddition(SoundEvent soundEvent) {
        return SimpleSoundInstance.forLocalAmbience(soundEvent, 1.0f, 1.0f);
    }

    public static SimpleSoundInstance forAmbientMood(SoundEvent soundEvent, double d, double d2, double d3) {
        return new SimpleSoundInstance(soundEvent, SoundSource.AMBIENT, 1.0f, 1.0f, false, 0, SoundInstance.Attenuation.LINEAR, d, d2, d3);
    }

    public SimpleSoundInstance(SoundEvent soundEvent, SoundSource soundSource, float f, float f2, double d, double d2, double d3) {
        this(soundEvent, soundSource, f, f2, false, 0, SoundInstance.Attenuation.LINEAR, d, d2, d3);
    }

    private SimpleSoundInstance(SoundEvent soundEvent, SoundSource soundSource, float f, float f2, boolean bl, int n, SoundInstance.Attenuation attenuation, double d, double d2, double d3) {
        this(soundEvent.getLocation(), soundSource, f, f2, bl, n, attenuation, d, d2, d3, false);
    }

    public SimpleSoundInstance(ResourceLocation resourceLocation, SoundSource soundSource, float f, float f2, boolean bl, int n, SoundInstance.Attenuation attenuation, double d, double d2, double d3, boolean bl2) {
        super(resourceLocation, soundSource);
        this.volume = f;
        this.pitch = f2;
        this.x = d;
        this.y = d2;
        this.z = d3;
        this.looping = bl;
        this.delay = n;
        this.attenuation = attenuation;
        this.relative = bl2;
    }
}

