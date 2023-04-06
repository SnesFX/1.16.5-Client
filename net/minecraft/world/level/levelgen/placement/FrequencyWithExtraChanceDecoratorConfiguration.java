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

public class FrequencyWithExtraChanceDecoratorConfiguration
implements DecoratorConfiguration {
    public static final Codec<FrequencyWithExtraChanceDecoratorConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("count").forGetter(frequencyWithExtraChanceDecoratorConfiguration -> frequencyWithExtraChanceDecoratorConfiguration.count), (App)Codec.FLOAT.fieldOf("extra_chance").forGetter(frequencyWithExtraChanceDecoratorConfiguration -> Float.valueOf(frequencyWithExtraChanceDecoratorConfiguration.extraChance)), (App)Codec.INT.fieldOf("extra_count").forGetter(frequencyWithExtraChanceDecoratorConfiguration -> frequencyWithExtraChanceDecoratorConfiguration.extraCount)).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> FrequencyWithExtraChanceDecoratorConfiguration.new(arg_0, arg_1, arg_2)));
    public final int count;
    public final float extraChance;
    public final int extraCount;

    public FrequencyWithExtraChanceDecoratorConfiguration(int n, float f, int n2) {
        this.count = n;
        this.extraChance = f;
        this.extraCount = n2;
    }
}

