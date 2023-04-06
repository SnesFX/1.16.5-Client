/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P2
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
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
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

public class DepthAverageConfigation
implements DecoratorConfiguration {
    public static final Codec<DepthAverageConfigation> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("baseline").forGetter(depthAverageConfigation -> depthAverageConfigation.baseline), (App)Codec.INT.fieldOf("spread").forGetter(depthAverageConfigation -> depthAverageConfigation.spread)).apply((Applicative)instance, (arg_0, arg_1) -> DepthAverageConfigation.new(arg_0, arg_1)));
    public final int baseline;
    public final int spread;

    public DepthAverageConfigation(int n, int n2) {
        this.baseline = n;
        this.spread = n2;
    }
}

