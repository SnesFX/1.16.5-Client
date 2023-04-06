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
import net.minecraft.world.level.levelgen.feature.RuinedPortalFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class RuinedPortalConfiguration
implements FeatureConfiguration {
    public static final Codec<RuinedPortalConfiguration> CODEC = RuinedPortalFeature.Type.CODEC.fieldOf("portal_type").xmap(RuinedPortalConfiguration::new, ruinedPortalConfiguration -> ruinedPortalConfiguration.portalType).codec();
    public final RuinedPortalFeature.Type portalType;

    public RuinedPortalConfiguration(RuinedPortalFeature.Type type) {
        this.portalType = type;
    }
}

