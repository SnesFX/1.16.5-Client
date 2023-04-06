/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.client.resources.sounds;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.sounds.Sound;

public class SoundEventRegistration {
    private final List<Sound> sounds;
    private final boolean replace;
    private final String subtitle;

    public SoundEventRegistration(List<Sound> list, boolean bl, String string) {
        this.sounds = list;
        this.replace = bl;
        this.subtitle = string;
    }

    public List<Sound> getSounds() {
        return this.sounds;
    }

    public boolean isReplace() {
        return this.replace;
    }

    @Nullable
    public String getSubtitle() {
        return this.subtitle;
    }
}

