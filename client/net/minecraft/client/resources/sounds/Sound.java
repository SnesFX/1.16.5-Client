/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.resources.sounds;

import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.resources.ResourceLocation;

public class Sound
implements Weighted<Sound> {
    private final ResourceLocation location;
    private final float volume;
    private final float pitch;
    private final int weight;
    private final Type type;
    private final boolean stream;
    private final boolean preload;
    private final int attenuationDistance;

    public Sound(String string, float f, float f2, int n, Type type, boolean bl, boolean bl2, int n2) {
        this.location = new ResourceLocation(string);
        this.volume = f;
        this.pitch = f2;
        this.weight = n;
        this.type = type;
        this.stream = bl;
        this.preload = bl2;
        this.attenuationDistance = n2;
    }

    public ResourceLocation getLocation() {
        return this.location;
    }

    public ResourceLocation getPath() {
        return new ResourceLocation(this.location.getNamespace(), "sounds/" + this.location.getPath() + ".ogg");
    }

    public float getVolume() {
        return this.volume;
    }

    public float getPitch() {
        return this.pitch;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }

    @Override
    public Sound getSound() {
        return this;
    }

    @Override
    public void preloadIfRequired(SoundEngine soundEngine) {
        if (this.preload) {
            soundEngine.requestPreload(this);
        }
    }

    public Type getType() {
        return this.type;
    }

    public boolean shouldStream() {
        return this.stream;
    }

    public boolean shouldPreload() {
        return this.preload;
    }

    public int getAttenuationDistance() {
        return this.attenuationDistance;
    }

    public String toString() {
        return "Sound[" + this.location + "]";
    }

    @Override
    public /* synthetic */ Object getSound() {
        return this.getSound();
    }

    public static enum Type {
        FILE("file"),
        SOUND_EVENT("event");
        
        private final String name;

        private Type(String string2) {
            this.name = string2;
        }

        public static Type getByName(String string) {
            for (Type type : Type.values()) {
                if (!type.name.equals(string)) continue;
                return type;
            }
            return null;
        }
    }

}

