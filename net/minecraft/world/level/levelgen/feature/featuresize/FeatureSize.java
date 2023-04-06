/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.level.levelgen.feature.featuresize;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;

public abstract class FeatureSize {
    public static final Codec<FeatureSize> CODEC = Registry.FEATURE_SIZE_TYPES.dispatch(FeatureSize::type, FeatureSizeType::codec);
    protected final OptionalInt minClippedHeight;

    protected static <S extends FeatureSize> RecordCodecBuilder<S, OptionalInt> minClippedHeightCodec() {
        return Codec.intRange((int)0, (int)80).optionalFieldOf("min_clipped_height").xmap(optional -> optional.map(OptionalInt::of).orElse(OptionalInt.empty()), optionalInt -> optionalInt.isPresent() ? Optional.of(optionalInt.getAsInt()) : Optional.empty()).forGetter(featureSize -> featureSize.minClippedHeight);
    }

    public FeatureSize(OptionalInt optionalInt) {
        this.minClippedHeight = optionalInt;
    }

    protected abstract FeatureSizeType<?> type();

    public abstract int getSizeAtHeight(int var1, int var2);

    public OptionalInt minClippedHeight() {
        return this.minClippedHeight;
    }
}

