/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P4
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function4
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.sounds;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.sounds.SoundEvent;

public class Music {
    public static final Codec<Music> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)SoundEvent.CODEC.fieldOf("sound").forGetter(music -> music.event), (App)Codec.INT.fieldOf("min_delay").forGetter(music -> music.minDelay), (App)Codec.INT.fieldOf("max_delay").forGetter(music -> music.maxDelay), (App)Codec.BOOL.fieldOf("replace_current_music").forGetter(music -> music.replaceCurrentMusic)).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3) -> Music.new(arg_0, arg_1, arg_2, arg_3)));
    private final SoundEvent event;
    private final int minDelay;
    private final int maxDelay;
    private final boolean replaceCurrentMusic;

    public Music(SoundEvent soundEvent, int n, int n2, boolean bl) {
        this.event = soundEvent;
        this.minDelay = n;
        this.maxDelay = n2;
        this.replaceCurrentMusic = bl;
    }

    public SoundEvent getEvent() {
        return this.event;
    }

    public int getMinDelay() {
        return this.minDelay;
    }

    public int getMaxDelay() {
        return this.maxDelay;
    }

    public boolean replaceCurrentMusic() {
        return this.replaceCurrentMusic;
    }
}

