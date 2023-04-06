/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class ShipwreckConfiguration
implements FeatureConfiguration {
    public static final Codec<ShipwreckConfiguration> CODEC = Codec.BOOL.fieldOf("is_beached").orElse((Object)false).xmap(ShipwreckConfiguration::new, shipwreckConfiguration -> shipwreckConfiguration.isBeached).codec();
    public final boolean isBeached;

    public ShipwreckConfiguration(boolean bl) {
        this.isBeached = bl;
    }
}

