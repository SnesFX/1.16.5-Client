/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P3
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function3
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

public class NoiseDependantDecoratorConfiguration
implements DecoratorConfiguration {
    public static final Codec<NoiseDependantDecoratorConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.DOUBLE.fieldOf("noise_level").forGetter(noiseDependantDecoratorConfiguration -> noiseDependantDecoratorConfiguration.noiseLevel), (App)Codec.INT.fieldOf("below_noise").forGetter(noiseDependantDecoratorConfiguration -> noiseDependantDecoratorConfiguration.belowNoise), (App)Codec.INT.fieldOf("above_noise").forGetter(noiseDependantDecoratorConfiguration -> noiseDependantDecoratorConfiguration.aboveNoise)).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> NoiseDependantDecoratorConfiguration.new(arg_0, arg_1, arg_2)));
    public final double noiseLevel;
    public final int belowNoise;
    public final int aboveNoise;

    public NoiseDependantDecoratorConfiguration(double d, int n, int n2) {
        this.noiseLevel = d;
        this.belowNoise = n;
        this.aboveNoise = n2;
    }
}

