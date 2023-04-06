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
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class CountConfiguration
implements DecoratorConfiguration,
FeatureConfiguration {
    public static final Codec<CountConfiguration> CODEC = UniformInt.codec(-10, 128, 128).fieldOf("count").xmap(CountConfiguration::new, CountConfiguration::count).codec();
    private final UniformInt count;

    public CountConfiguration(int n) {
        this.count = UniformInt.fixed(n);
    }

    public CountConfiguration(UniformInt uniformInt) {
        this.count = uniformInt;
    }

    public UniformInt count() {
        return this.count;
    }
}

