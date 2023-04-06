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
package net.minecraft.world.level.levelgen.placement;

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

public class NoiseCountFactorDecoratorConfiguration
implements DecoratorConfiguration {
    public static final Codec<NoiseCountFactorDecoratorConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("noise_to_count_ratio").forGetter(noiseCountFactorDecoratorConfiguration -> noiseCountFactorDecoratorConfiguration.noiseToCountRatio), (App)Codec.DOUBLE.fieldOf("noise_factor").forGetter(noiseCountFactorDecoratorConfiguration -> noiseCountFactorDecoratorConfiguration.noiseFactor), (App)Codec.DOUBLE.fieldOf("noise_offset").orElse((Object)0.0).forGetter(noiseCountFactorDecoratorConfiguration -> noiseCountFactorDecoratorConfiguration.noiseOffset)).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> NoiseCountFactorDecoratorConfiguration.new(arg_0, arg_1, arg_2)));
    public final int noiseToCountRatio;
    public final double noiseFactor;
    public final double noiseOffset;

    public NoiseCountFactorDecoratorConfiguration(int n, double d, double d2) {
        this.noiseToCountRatio = n;
        this.noiseFactor = d;
        this.noiseOffset = d2;
    }
}

