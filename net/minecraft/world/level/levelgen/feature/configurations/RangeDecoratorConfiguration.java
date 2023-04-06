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

public class RangeDecoratorConfiguration
implements DecoratorConfiguration {
    public static final Codec<RangeDecoratorConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("bottom_offset").orElse((Object)0).forGetter(rangeDecoratorConfiguration -> rangeDecoratorConfiguration.bottomOffset), (App)Codec.INT.fieldOf("top_offset").orElse((Object)0).forGetter(rangeDecoratorConfiguration -> rangeDecoratorConfiguration.topOffset), (App)Codec.INT.fieldOf("maximum").orElse((Object)0).forGetter(rangeDecoratorConfiguration -> rangeDecoratorConfiguration.maximum)).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> RangeDecoratorConfiguration.new(arg_0, arg_1, arg_2)));
    public final int bottomOffset;
    public final int topOffset;
    public final int maximum;

    public RangeDecoratorConfiguration(int n, int n2, int n3) {
        this.bottomOffset = n;
        this.topOffset = n2;
        this.maximum = n3;
    }
}

