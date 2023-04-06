/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P2
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.entity.ai.memory;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ExpirableValue<T> {
    private final T value;
    private long timeToLive;

    public ExpirableValue(T t, long l) {
        this.value = t;
        this.timeToLive = l;
    }

    public void tick() {
        if (this.canExpire()) {
            --this.timeToLive;
        }
    }

    public static <T> ExpirableValue<T> of(T t) {
        return new ExpirableValue<T>(t, Long.MAX_VALUE);
    }

    public static <T> ExpirableValue<T> of(T t, long l) {
        return new ExpirableValue<T>(t, l);
    }

    public T getValue() {
        return this.value;
    }

    public boolean hasExpired() {
        return this.timeToLive <= 0L;
    }

    public String toString() {
        return this.value.toString() + (this.canExpire() ? " (ttl: " + this.timeToLive + ")" : "");
    }

    public boolean canExpire() {
        return this.timeToLive != Long.MAX_VALUE;
    }

    public static <T> Codec<ExpirableValue<T>> codec(Codec<T> codec) {
        return RecordCodecBuilder.create(instance -> instance.group((App)codec.fieldOf("value").forGetter(expirableValue -> expirableValue.value), (App)Codec.LONG.optionalFieldOf("ttl").forGetter(expirableValue -> expirableValue.canExpire() ? Optional.of(expirableValue.timeToLive) : Optional.empty())).apply((Applicative)instance, (object, optional) -> new ExpirableValue<Object>(object, optional.orElse(Long.MAX_VALUE))));
    }
}

