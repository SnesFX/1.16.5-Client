/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P12
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function12
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.biome;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function12;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

public class BiomeSpecialEffects {
    public static final Codec<BiomeSpecialEffects> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("fog_color").forGetter(biomeSpecialEffects -> biomeSpecialEffects.fogColor), (App)Codec.INT.fieldOf("water_color").forGetter(biomeSpecialEffects -> biomeSpecialEffects.waterColor), (App)Codec.INT.fieldOf("water_fog_color").forGetter(biomeSpecialEffects -> biomeSpecialEffects.waterFogColor), (App)Codec.INT.fieldOf("sky_color").forGetter(biomeSpecialEffects -> biomeSpecialEffects.skyColor), (App)Codec.INT.optionalFieldOf("foliage_color").forGetter(biomeSpecialEffects -> biomeSpecialEffects.foliageColorOverride), (App)Codec.INT.optionalFieldOf("grass_color").forGetter(biomeSpecialEffects -> biomeSpecialEffects.grassColorOverride), (App)GrassColorModifier.CODEC.optionalFieldOf("grass_color_modifier", (Object)GrassColorModifier.NONE).forGetter(biomeSpecialEffects -> biomeSpecialEffects.grassColorModifier), (App)AmbientParticleSettings.CODEC.optionalFieldOf("particle").forGetter(biomeSpecialEffects -> biomeSpecialEffects.ambientParticleSettings), (App)SoundEvent.CODEC.optionalFieldOf("ambient_sound").forGetter(biomeSpecialEffects -> biomeSpecialEffects.ambientLoopSoundEvent), (App)AmbientMoodSettings.CODEC.optionalFieldOf("mood_sound").forGetter(biomeSpecialEffects -> biomeSpecialEffects.ambientMoodSettings), (App)AmbientAdditionsSettings.CODEC.optionalFieldOf("additions_sound").forGetter(biomeSpecialEffects -> biomeSpecialEffects.ambientAdditionsSettings), (App)Music.CODEC.optionalFieldOf("music").forGetter(biomeSpecialEffects -> biomeSpecialEffects.backgroundMusic)).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5, arg_6, arg_7, arg_8, arg_9, arg_10, arg_11) -> BiomeSpecialEffects.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5, arg_6, arg_7, arg_8, arg_9, arg_10, arg_11)));
    private final int fogColor;
    private final int waterColor;
    private final int waterFogColor;
    private final int skyColor;
    private final Optional<Integer> foliageColorOverride;
    private final Optional<Integer> grassColorOverride;
    private final GrassColorModifier grassColorModifier;
    private final Optional<AmbientParticleSettings> ambientParticleSettings;
    private final Optional<SoundEvent> ambientLoopSoundEvent;
    private final Optional<AmbientMoodSettings> ambientMoodSettings;
    private final Optional<AmbientAdditionsSettings> ambientAdditionsSettings;
    private final Optional<Music> backgroundMusic;

    private BiomeSpecialEffects(int n, int n2, int n3, int n4, Optional<Integer> optional, Optional<Integer> optional2, GrassColorModifier grassColorModifier, Optional<AmbientParticleSettings> optional3, Optional<SoundEvent> optional4, Optional<AmbientMoodSettings> optional5, Optional<AmbientAdditionsSettings> optional6, Optional<Music> optional7) {
        this.fogColor = n;
        this.waterColor = n2;
        this.waterFogColor = n3;
        this.skyColor = n4;
        this.foliageColorOverride = optional;
        this.grassColorOverride = optional2;
        this.grassColorModifier = grassColorModifier;
        this.ambientParticleSettings = optional3;
        this.ambientLoopSoundEvent = optional4;
        this.ambientMoodSettings = optional5;
        this.ambientAdditionsSettings = optional6;
        this.backgroundMusic = optional7;
    }

    public int getFogColor() {
        return this.fogColor;
    }

    public int getWaterColor() {
        return this.waterColor;
    }

    public int getWaterFogColor() {
        return this.waterFogColor;
    }

    public int getSkyColor() {
        return this.skyColor;
    }

    public Optional<Integer> getFoliageColorOverride() {
        return this.foliageColorOverride;
    }

    public Optional<Integer> getGrassColorOverride() {
        return this.grassColorOverride;
    }

    public GrassColorModifier getGrassColorModifier() {
        return this.grassColorModifier;
    }

    public Optional<AmbientParticleSettings> getAmbientParticleSettings() {
        return this.ambientParticleSettings;
    }

    public Optional<SoundEvent> getAmbientLoopSoundEvent() {
        return this.ambientLoopSoundEvent;
    }

    public Optional<AmbientMoodSettings> getAmbientMoodSettings() {
        return this.ambientMoodSettings;
    }

    public Optional<AmbientAdditionsSettings> getAmbientAdditionsSettings() {
        return this.ambientAdditionsSettings;
    }

    public Optional<Music> getBackgroundMusic() {
        return this.backgroundMusic;
    }

    public static enum GrassColorModifier implements StringRepresentable
    {
        NONE("none"){

            @Override
            public int modifyColor(double d, double d2, int n) {
                return n;
            }
        }
        ,
        DARK_FOREST("dark_forest"){

            @Override
            public int modifyColor(double d, double d2, int n) {
                return (n & 0xFEFEFE) + 2634762 >> 1;
            }
        }
        ,
        SWAMP("swamp"){

            @Override
            public int modifyColor(double d, double d2, int n) {
                double d3 = Biome.BIOME_INFO_NOISE.getValue(d * 0.0225, d2 * 0.0225, false);
                if (d3 < -0.1) {
                    return 5011004;
                }
                return 6975545;
            }
        };
        
        private final String name;
        public static final Codec<GrassColorModifier> CODEC;
        private static final Map<String, GrassColorModifier> BY_NAME;

        public abstract int modifyColor(double var1, double var3, int var5);

        private GrassColorModifier(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public static GrassColorModifier byName(String string) {
            return BY_NAME.get(string);
        }

        static {
            CODEC = StringRepresentable.fromEnum(GrassColorModifier::values, GrassColorModifier::byName);
            BY_NAME = Arrays.stream(GrassColorModifier.values()).collect(Collectors.toMap(GrassColorModifier::getName, grassColorModifier -> grassColorModifier));
        }

    }

    public static class Builder {
        private OptionalInt fogColor = OptionalInt.empty();
        private OptionalInt waterColor = OptionalInt.empty();
        private OptionalInt waterFogColor = OptionalInt.empty();
        private OptionalInt skyColor = OptionalInt.empty();
        private Optional<Integer> foliageColorOverride = Optional.empty();
        private Optional<Integer> grassColorOverride = Optional.empty();
        private GrassColorModifier grassColorModifier = GrassColorModifier.NONE;
        private Optional<AmbientParticleSettings> ambientParticle = Optional.empty();
        private Optional<SoundEvent> ambientLoopSoundEvent = Optional.empty();
        private Optional<AmbientMoodSettings> ambientMoodSettings = Optional.empty();
        private Optional<AmbientAdditionsSettings> ambientAdditionsSettings = Optional.empty();
        private Optional<Music> backgroundMusic = Optional.empty();

        public Builder fogColor(int n) {
            this.fogColor = OptionalInt.of(n);
            return this;
        }

        public Builder waterColor(int n) {
            this.waterColor = OptionalInt.of(n);
            return this;
        }

        public Builder waterFogColor(int n) {
            this.waterFogColor = OptionalInt.of(n);
            return this;
        }

        public Builder skyColor(int n) {
            this.skyColor = OptionalInt.of(n);
            return this;
        }

        public Builder foliageColorOverride(int n) {
            this.foliageColorOverride = Optional.of(n);
            return this;
        }

        public Builder grassColorOverride(int n) {
            this.grassColorOverride = Optional.of(n);
            return this;
        }

        public Builder grassColorModifier(GrassColorModifier grassColorModifier) {
            this.grassColorModifier = grassColorModifier;
            return this;
        }

        public Builder ambientParticle(AmbientParticleSettings ambientParticleSettings) {
            this.ambientParticle = Optional.of(ambientParticleSettings);
            return this;
        }

        public Builder ambientLoopSound(SoundEvent soundEvent) {
            this.ambientLoopSoundEvent = Optional.of(soundEvent);
            return this;
        }

        public Builder ambientMoodSound(AmbientMoodSettings ambientMoodSettings) {
            this.ambientMoodSettings = Optional.of(ambientMoodSettings);
            return this;
        }

        public Builder ambientAdditionsSound(AmbientAdditionsSettings ambientAdditionsSettings) {
            this.ambientAdditionsSettings = Optional.of(ambientAdditionsSettings);
            return this;
        }

        public Builder backgroundMusic(Music music) {
            this.backgroundMusic = Optional.of(music);
            return this;
        }

        public BiomeSpecialEffects build() {
            return new BiomeSpecialEffects(this.fogColor.orElseThrow(() -> new IllegalStateException("Missing 'fog' color.")), this.waterColor.orElseThrow(() -> new IllegalStateException("Missing 'water' color.")), this.waterFogColor.orElseThrow(() -> new IllegalStateException("Missing 'water fog' color.")), this.skyColor.orElseThrow(() -> new IllegalStateException("Missing 'sky' color.")), this.foliageColorOverride, this.grassColorOverride, this.grassColorModifier, this.ambientParticle, this.ambientLoopSoundEvent, this.ambientMoodSettings, this.ambientAdditionsSettings, this.backgroundMusic);
        }
    }

}

