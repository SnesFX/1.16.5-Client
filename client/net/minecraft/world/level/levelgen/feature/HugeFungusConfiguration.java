/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
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
package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class HugeFungusConfiguration
implements FeatureConfiguration {
    public static final Codec<HugeFungusConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockState.CODEC.fieldOf("valid_base_block").forGetter(hugeFungusConfiguration -> hugeFungusConfiguration.validBaseState), (App)BlockState.CODEC.fieldOf("stem_state").forGetter(hugeFungusConfiguration -> hugeFungusConfiguration.stemState), (App)BlockState.CODEC.fieldOf("hat_state").forGetter(hugeFungusConfiguration -> hugeFungusConfiguration.hatState), (App)BlockState.CODEC.fieldOf("decor_state").forGetter(hugeFungusConfiguration -> hugeFungusConfiguration.decorState), (App)Codec.BOOL.fieldOf("planted").orElse((Object)false).forGetter(hugeFungusConfiguration -> hugeFungusConfiguration.planted)).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3, arg_4) -> HugeFungusConfiguration.new(arg_0, arg_1, arg_2, arg_3, arg_4)));
    public static final HugeFungusConfiguration HUGE_CRIMSON_FUNGI_PLANTED_CONFIG = new HugeFungusConfiguration(Blocks.CRIMSON_NYLIUM.defaultBlockState(), Blocks.CRIMSON_STEM.defaultBlockState(), Blocks.NETHER_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), true);
    public static final HugeFungusConfiguration HUGE_CRIMSON_FUNGI_NOT_PLANTED_CONFIG = new HugeFungusConfiguration(HugeFungusConfiguration.HUGE_CRIMSON_FUNGI_PLANTED_CONFIG.validBaseState, HugeFungusConfiguration.HUGE_CRIMSON_FUNGI_PLANTED_CONFIG.stemState, HugeFungusConfiguration.HUGE_CRIMSON_FUNGI_PLANTED_CONFIG.hatState, HugeFungusConfiguration.HUGE_CRIMSON_FUNGI_PLANTED_CONFIG.decorState, false);
    public static final HugeFungusConfiguration HUGE_WARPED_FUNGI_PLANTED_CONFIG = new HugeFungusConfiguration(Blocks.WARPED_NYLIUM.defaultBlockState(), Blocks.WARPED_STEM.defaultBlockState(), Blocks.WARPED_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), true);
    public static final HugeFungusConfiguration HUGE_WARPED_FUNGI_NOT_PLANTED_CONFIG = new HugeFungusConfiguration(HugeFungusConfiguration.HUGE_WARPED_FUNGI_PLANTED_CONFIG.validBaseState, HugeFungusConfiguration.HUGE_WARPED_FUNGI_PLANTED_CONFIG.stemState, HugeFungusConfiguration.HUGE_WARPED_FUNGI_PLANTED_CONFIG.hatState, HugeFungusConfiguration.HUGE_WARPED_FUNGI_PLANTED_CONFIG.decorState, false);
    public final BlockState validBaseState;
    public final BlockState stemState;
    public final BlockState hatState;
    public final BlockState decorState;
    public final boolean planted;

    public HugeFungusConfiguration(BlockState blockState, BlockState blockState2, BlockState blockState3, BlockState blockState4, boolean bl) {
        this.validBaseState = blockState;
        this.stemState = blockState2;
        this.hatState = blockState3;
        this.decorState = blockState4;
        this.planted = bl;
    }
}

