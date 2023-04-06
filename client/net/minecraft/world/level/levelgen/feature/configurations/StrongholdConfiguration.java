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

public class StrongholdConfiguration {
    public static final Codec<StrongholdConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.intRange((int)0, (int)1023).fieldOf("distance").forGetter(StrongholdConfiguration::distance), (App)Codec.intRange((int)0, (int)1023).fieldOf("spread").forGetter(StrongholdConfiguration::spread), (App)Codec.intRange((int)1, (int)4095).fieldOf("count").forGetter(StrongholdConfiguration::count)).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> StrongholdConfiguration.new(arg_0, arg_1, arg_2)));
    private final int distance;
    private final int spread;
    private final int count;

    public StrongholdConfiguration(int n, int n2, int n3) {
        this.distance = n;
        this.spread = n2;
        this.count = n3;
    }

    public int distance() {
        return this.distance;
    }

    public int spread() {
        return this.spread;
    }

    public int count() {
        return this.count;
    }
}

