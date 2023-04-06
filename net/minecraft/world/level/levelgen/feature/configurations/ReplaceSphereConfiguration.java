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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class ReplaceSphereConfiguration
implements FeatureConfiguration {
    public static final Codec<ReplaceSphereConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockState.CODEC.fieldOf("target").forGetter(replaceSphereConfiguration -> replaceSphereConfiguration.targetState), (App)BlockState.CODEC.fieldOf("state").forGetter(replaceSphereConfiguration -> replaceSphereConfiguration.replaceState), (App)UniformInt.CODEC.fieldOf("radius").forGetter(replaceSphereConfiguration -> replaceSphereConfiguration.radius)).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> ReplaceSphereConfiguration.new(arg_0, arg_1, arg_2)));
    public final BlockState targetState;
    public final BlockState replaceState;
    private final UniformInt radius;

    public ReplaceSphereConfiguration(BlockState blockState, BlockState blockState2, UniformInt uniformInt) {
        this.targetState = blockState;
        this.replaceState = blockState2;
        this.radius = uniformInt;
    }

    public UniformInt radius() {
        return this.radius;
    }
}

