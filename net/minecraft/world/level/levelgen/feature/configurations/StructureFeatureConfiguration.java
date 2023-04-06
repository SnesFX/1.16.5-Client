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
 *  com.mojang.serialization.DataResult
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
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;

public class StructureFeatureConfiguration {
    public static final Codec<StructureFeatureConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.intRange((int)0, (int)4096).fieldOf("spacing").forGetter(structureFeatureConfiguration -> structureFeatureConfiguration.spacing), (App)Codec.intRange((int)0, (int)4096).fieldOf("separation").forGetter(structureFeatureConfiguration -> structureFeatureConfiguration.separation), (App)Codec.intRange((int)0, (int)Integer.MAX_VALUE).fieldOf("salt").forGetter(structureFeatureConfiguration -> structureFeatureConfiguration.salt)).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> StructureFeatureConfiguration.new(arg_0, arg_1, arg_2))).comapFlatMap(structureFeatureConfiguration -> {
        if (structureFeatureConfiguration.spacing <= structureFeatureConfiguration.separation) {
            return DataResult.error((String)"Spacing has to be smaller than separation");
        }
        return DataResult.success((Object)structureFeatureConfiguration);
    }, Function.identity());
    private final int spacing;
    private final int separation;
    private final int salt;

    public StructureFeatureConfiguration(int n, int n2, int n3) {
        this.spacing = n;
        this.separation = n2;
        this.salt = n3;
    }

    public int spacing() {
        return this.spacing;
    }

    public int separation() {
        return this.separation;
    }

    public int salt() {
        return this.salt;
    }
}

