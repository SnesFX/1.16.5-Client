/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 */
package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

public class ChanceDecoratorConfiguration
implements DecoratorConfiguration {
    public static final Codec<ChanceDecoratorConfiguration> CODEC = Codec.INT.fieldOf("chance").xmap(ChanceDecoratorConfiguration::new, chanceDecoratorConfiguration -> chanceDecoratorConfiguration.chance).codec();
    public final int chance;

    public ChanceDecoratorConfiguration(int n) {
        this.chance = n;
    }
}

