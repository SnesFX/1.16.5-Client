/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P5
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function5
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.material.FluidState;

public class SpringConfiguration
implements FeatureConfiguration {
    public static final Codec<SpringConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)FluidState.CODEC.fieldOf("state").forGetter(springConfiguration -> springConfiguration.state), (App)Codec.BOOL.fieldOf("requires_block_below").orElse((Object)true).forGetter(springConfiguration -> springConfiguration.requiresBlockBelow), (App)Codec.INT.fieldOf("rock_count").orElse((Object)4).forGetter(springConfiguration -> springConfiguration.rockCount), (App)Codec.INT.fieldOf("hole_count").orElse((Object)1).forGetter(springConfiguration -> springConfiguration.holeCount), (App)Registry.BLOCK.listOf().fieldOf("valid_blocks").xmap(ImmutableSet::copyOf, ImmutableList::copyOf).forGetter(springConfiguration -> springConfiguration.validBlocks)).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3, arg_4) -> SpringConfiguration.new(arg_0, arg_1, arg_2, arg_3, arg_4)));
    public final FluidState state;
    public final boolean requiresBlockBelow;
    public final int rockCount;
    public final int holeCount;
    public final Set<Block> validBlocks;

    public SpringConfiguration(FluidState fluidState, boolean bl, int n, int n2, Set<Block> set) {
        this.state = fluidState;
        this.requiresBlockBelow = bl;
        this.rockCount = n;
        this.holeCount = n2;
        this.validBlocks = set;
    }
}

