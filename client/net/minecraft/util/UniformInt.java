/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P2
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.util;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

public class UniformInt {
    public static final Codec<UniformInt> CODEC = Codec.either((Codec)Codec.INT, (Codec)RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("base").forGetter(uniformInt -> uniformInt.baseValue), (App)Codec.INT.fieldOf("spread").forGetter(uniformInt -> uniformInt.spread)).apply((Applicative)instance, (arg_0, arg_1) -> UniformInt.new(arg_0, arg_1))).comapFlatMap(uniformInt -> {
        if (uniformInt.spread < 0) {
            return DataResult.error((String)("Spread must be non-negative, got: " + uniformInt.spread));
        }
        return DataResult.success((Object)uniformInt);
    }, Function.identity())).xmap(either -> (UniformInt)either.map(UniformInt::fixed, uniformInt -> uniformInt), uniformInt -> uniformInt.spread == 0 ? Either.left((Object)uniformInt.baseValue) : Either.right((Object)uniformInt));
    private final int baseValue;
    private final int spread;

    public static Codec<UniformInt> codec(int n, int n2, int n3) {
        Function<UniformInt, DataResult> function = uniformInt -> {
            if (uniformInt.baseValue >= n && uniformInt.baseValue <= n2) {
                if (uniformInt.spread <= n3) {
                    return DataResult.success((Object)uniformInt);
                }
                return DataResult.error((String)("Spread too big: " + uniformInt.spread + " > " + n3));
            }
            return DataResult.error((String)("Base value out of range: " + uniformInt.baseValue + " [" + n + "-" + n2 + "]"));
        };
        return CODEC.flatXmap(function, function);
    }

    private UniformInt(int n, int n2) {
        this.baseValue = n;
        this.spread = n2;
    }

    public static UniformInt fixed(int n) {
        return new UniformInt(n, 0);
    }

    public static UniformInt of(int n, int n2) {
        return new UniformInt(n, n2);
    }

    public int sample(Random random) {
        if (this.spread == 0) {
            return this.baseValue;
        }
        return this.baseValue + random.nextInt(this.spread + 1);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        UniformInt uniformInt = (UniformInt)object;
        return this.baseValue == uniformInt.baseValue && this.spread == uniformInt.spread;
    }

    public int hashCode() {
        return Objects.hash(this.baseValue, this.spread);
    }

    public String toString() {
        return "[" + this.baseValue + '-' + (this.baseValue + this.spread) + ']';
    }
}

