/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P6
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function6
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.levelgen.feature.featuresize;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function6;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;

public class ThreeLayersFeatureSize
extends FeatureSize {
    public static final Codec<ThreeLayersFeatureSize> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.intRange((int)0, (int)80).fieldOf("limit").orElse((Object)1).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.limit), (App)Codec.intRange((int)0, (int)80).fieldOf("upper_limit").orElse((Object)1).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.upperLimit), (App)Codec.intRange((int)0, (int)16).fieldOf("lower_size").orElse((Object)0).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.lowerSize), (App)Codec.intRange((int)0, (int)16).fieldOf("middle_size").orElse((Object)1).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.middleSize), (App)Codec.intRange((int)0, (int)16).fieldOf("upper_size").orElse((Object)1).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.upperSize), ThreeLayersFeatureSize.minClippedHeightCodec()).apply((Applicative)instance, (arg_0, arg_1, arg_2, arg_3, arg_4, arg_5) -> ThreeLayersFeatureSize.new(arg_0, arg_1, arg_2, arg_3, arg_4, arg_5)));
    private final int limit;
    private final int upperLimit;
    private final int lowerSize;
    private final int middleSize;
    private final int upperSize;

    public ThreeLayersFeatureSize(int n, int n2, int n3, int n4, int n5, OptionalInt optionalInt) {
        super(optionalInt);
        this.limit = n;
        this.upperLimit = n2;
        this.lowerSize = n3;
        this.middleSize = n4;
        this.upperSize = n5;
    }

    @Override
    protected FeatureSizeType<?> type() {
        return FeatureSizeType.THREE_LAYERS_FEATURE_SIZE;
    }

    @Override
    public int getSizeAtHeight(int n, int n2) {
        if (n2 < this.limit) {
            return this.lowerSize;
        }
        if (n2 >= n - this.upperLimit) {
            return this.upperSize;
        }
        return this.middleSize;
    }
}

