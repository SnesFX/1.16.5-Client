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
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;

public class OceanRuinConfiguration
implements FeatureConfiguration {
    public static final Codec<OceanRuinConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)OceanRuinFeature.Type.CODEC.fieldOf("biome_temp").forGetter(oceanRuinConfiguration -> oceanRuinConfiguration.biomeTemp), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("large_probability").forGetter(oceanRuinConfiguration -> Float.valueOf(oceanRuinConfiguration.largeProbability)), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("cluster_probability").forGetter(oceanRuinConfiguration -> Float.valueOf(oceanRuinConfiguration.clusterProbability))).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> OceanRuinConfiguration.new(arg_0, arg_1, arg_2)));
    public final OceanRuinFeature.Type biomeTemp;
    public final float largeProbability;
    public final float clusterProbability;

    public OceanRuinConfiguration(OceanRuinFeature.Type type, float f, float f2) {
        this.biomeTemp = type;
        this.largeProbability = f;
        this.clusterProbability = f2;
    }
}

