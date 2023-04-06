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
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class LayerConfiguration
implements FeatureConfiguration {
    public static final Codec<LayerConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.intRange((int)0, (int)255).fieldOf("height").forGetter(layerConfiguration -> layerConfiguration.height), (App)BlockState.CODEC.fieldOf("state").forGetter(layerConfiguration -> layerConfiguration.state)).apply((Applicative)instance, (arg_0, arg_1) -> LayerConfiguration.new(arg_0, arg_1)));
    public final int height;
    public final BlockState state;

    public LayerConfiguration(int n, BlockState blockState) {
        this.height = n;
        this.state = blockState;
    }
}

