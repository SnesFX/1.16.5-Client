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
package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class DiskConfiguration
implements FeatureConfiguration {
    public static final Codec<DiskConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockState.CODEC.fieldOf("state").forGetter(diskConfiguration -> diskConfiguration.state), (App)UniformInt.codec(0, 4, 4).fieldOf("radius").forGetter(diskConfiguration -> diskConfiguration.radius), (App)Codec.intRange((int)0, (int)4).fieldOf("half_height").forGetter(diskConfiguration -> diskConfiguration.halfHeight), (App)BlockState.CODEC.listOf().fieldOf("targets").forGetter(diskConfiguration -> diskConfiguration.targets)).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3) -> DiskConfiguration.new(arg_0, arg_1, arg_2, arg_3)));
    public final BlockState state;
    public final UniformInt radius;
    public final int halfHeight;
    public final List<BlockState> targets;

    public DiskConfiguration(BlockState blockState, UniformInt uniformInt, int n, List<BlockState> list) {
        this.state = blockState;
        this.radius = uniformInt;
        this.halfHeight = n;
        this.targets = list;
    }
}

