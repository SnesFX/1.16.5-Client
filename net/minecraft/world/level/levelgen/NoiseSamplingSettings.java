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
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;

public class NoiseSamplingSettings {
    private static final Codec<Double> SCALE_RANGE = Codec.doubleRange((double)0.001, (double)1000.0);
    public static final Codec<NoiseSamplingSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)SCALE_RANGE.fieldOf("xz_scale").forGetter(NoiseSamplingSettings::xzScale), (App)SCALE_RANGE.fieldOf("y_scale").forGetter(NoiseSamplingSettings::yScale), (App)SCALE_RANGE.fieldOf("xz_factor").forGetter(NoiseSamplingSettings::xzFactor), (App)SCALE_RANGE.fieldOf("y_factor").forGetter(NoiseSamplingSettings::yFactor)).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3) -> NoiseSamplingSettings.new(arg_0, arg_1, arg_2, arg_3)));
    private final double xzScale;
    private final double yScale;
    private final double xzFactor;
    private final double yFactor;

    public NoiseSamplingSettings(double d, double d2, double d3, double d4) {
        this.xzScale = d;
        this.yScale = d2;
        this.xzFactor = d3;
        this.yFactor = d4;
    }

    public double xzScale() {
        return this.xzScale;
    }

    public double yScale() {
        return this.yScale;
    }

    public double xzFactor() {
        return this.xzFactor;
    }

    public double yFactor() {
        return this.yFactor;
    }
}

