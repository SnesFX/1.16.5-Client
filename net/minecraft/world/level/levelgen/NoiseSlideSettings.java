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
 *  com.mojang.serialization.codecs.PrimitiveCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;

public class NoiseSlideSettings {
    public static final Codec<NoiseSlideSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("target").forGetter(NoiseSlideSettings::target), (App)Codec.intRange((int)0, (int)256).fieldOf("size").forGetter(NoiseSlideSettings::size), (App)Codec.INT.fieldOf("offset").forGetter(NoiseSlideSettings::offset)).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> NoiseSlideSettings.new(arg_0, arg_1, arg_2)));
    private final int target;
    private final int size;
    private final int offset;

    public NoiseSlideSettings(int n, int n2, int n3) {
        this.target = n;
        this.size = n2;
        this.offset = n3;
    }

    public int target() {
        return this.target;
    }

    public int size() {
        return this.size;
    }

    public int offset() {
        return this.offset;
    }
}

