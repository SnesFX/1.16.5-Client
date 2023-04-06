/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.sounds;

import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class Musics {
    public static final Music MENU = new Music(SoundEvents.MUSIC_MENU, 20, 600, true);
    public static final Music CREATIVE = new Music(SoundEvents.MUSIC_CREATIVE, 12000, 24000, false);
    public static final Music CREDITS = new Music(SoundEvents.MUSIC_CREDITS, 0, 0, true);
    public static final Music END_BOSS = new Music(SoundEvents.MUSIC_DRAGON, 0, 0, true);
    public static final Music END = new Music(SoundEvents.MUSIC_END, 6000, 24000, true);
    public static final Music UNDER_WATER = Musics.createGameMusic(SoundEvents.MUSIC_UNDER_WATER);
    public static final Music GAME = Musics.createGameMusic(SoundEvents.MUSIC_GAME);

    public static Music createGameMusic(SoundEvent soundEvent) {
        return new Music(soundEvent, 12000, 24000, false);
    }
}

