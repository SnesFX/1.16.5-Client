/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class BlockStateConfiguration
implements FeatureConfiguration {
    public static final Codec<BlockStateConfiguration> CODEC = BlockState.CODEC.fieldOf("state").xmap(BlockStateConfiguration::new, blockStateConfiguration -> blockStateConfiguration.state).codec();
    public final BlockState state;

    public BlockStateConfiguration(BlockState blockState) {
        this.state = blockState;
    }
}

