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
 *  com.mojang.serialization.Lifecycle
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function12;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.NoiseSamplingSettings;
import net.minecraft.world.level.levelgen.NoiseSlideSettings;

public class NoiseSettings {
    public static final Codec<NoiseSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.intRange((int)0, (int)256).fieldOf("height").forGetter(NoiseSettings::height), (App)NoiseSamplingSettings.CODEC.fieldOf("sampling").forGetter(NoiseSettings::noiseSamplingSettings), (App)NoiseSlideSettings.CODEC.fieldOf("top_slide").forGetter(NoiseSettings::topSlideSettings), (App)NoiseSlideSettings.CODEC.fieldOf("bottom_slide").forGetter(NoiseSettings::bottomSlideSettings), (App)Codec.intRange((int)1, (int)4).fieldOf("size_horizontal").forGetter(NoiseSettings::noiseSizeHorizontal), (App)Codec.intRange((int)1, (int)4).fieldOf("size_vertical").forGetter(NoiseSettings::noiseSizeVertical), (App)Codec.DOUBLE.fieldOf("density_factor").forGetter(NoiseSettings::densityFactor), (App)Codec.DOUBLE.fieldOf("density_offset").forGetter(NoiseSettings::densityOffset), (App)Codec.BOOL.fieldOf("simplex_surface_noise").forGetter(NoiseSettings::useSimplexSurfaceNoise), (App)Codec.BOOL.optionalFieldOf("random_density_offset", (Object)false, Lifecycle.experimental()).forGetter(NoiseSettings::randomDensityOffset), (App)Codec.BOOL.optionalFieldOf("island_noise_override", (Object)false, Lifecycle.experimental()).forGetter(NoiseSettings::islandNoiseOverride), (App)Codec.BOOL.optionalFieldOf("amplified", (Object)false, Lifecycle.experimental()).forGetter(NoiseSettings::isAmplified)).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5, arg_6, arg_7, arg_8, arg_9, arg_10, arg_11) -> NoiseSettings.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5, arg_6, arg_7, arg_8, arg_9, arg_10, arg_11)));
    private final int height;
    private final NoiseSamplingSettings noiseSamplingSettings;
    private final NoiseSlideSettings topSlideSettings;
    private final NoiseSlideSettings bottomSlideSettings;
    private final int noiseSizeHorizontal;
    private final int noiseSizeVertical;
    private final double densityFactor;
    private final double densityOffset;
    private final boolean useSimplexSurfaceNoise;
    private final boolean randomDensityOffset;
    private final boolean islandNoiseOverride;
    private final boolean isAmplified;

    public NoiseSettings(int n, NoiseSamplingSettings noiseSamplingSettings, NoiseSlideSettings noiseSlideSettings, NoiseSlideSettings noiseSlideSettings2, int n2, int n3, double d, double d2, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        this.height = n;
        this.noiseSamplingSettings = noiseSamplingSettings;
        this.topSlideSettings = noiseSlideSettings;
        this.bottomSlideSettings = noiseSlideSettings2;
        this.noiseSizeHorizontal = n2;
        this.noiseSizeVertical = n3;
        this.densityFactor = d;
        this.densityOffset = d2;
        this.useSimplexSurfaceNoise = bl;
        this.randomDensityOffset = bl2;
        this.islandNoiseOverride = bl3;
        this.isAmplified = bl4;
    }

    public int height() {
        return this.height;
    }

    public NoiseSamplingSettings noiseSamplingSettings() {
        return this.noiseSamplingSettings;
    }

    public NoiseSlideSettings topSlideSettings() {
        return this.topSlideSettings;
    }

    public NoiseSlideSettings bottomSlideSettings() {
        return this.bottomSlideSettings;
    }

    public int noiseSizeHorizontal() {
        return this.noiseSizeHorizontal;
    }

    public int noiseSizeVertical() {
        return this.noiseSizeVertical;
    }

    public double densityFactor() {
        return this.densityFactor;
    }

    public double densityOffset() {
        return this.densityOffset;
    }

    @Deprecated
    public boolean useSimplexSurfaceNoise() {
        return this.useSimplexSurfaceNoise;
    }

    @Deprecated
    public boolean randomDensityOffset() {
        return this.randomDensityOffset;
    }

    @Deprecated
    public boolean islandNoiseOverride() {
        return this.islandNoiseOverride;
    }

    @Deprecated
    public boolean isAmplified() {
        return this.isAmplified;
    }
}

