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
package net.minecraft.world.level.levelgen.feature.configurations;

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
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class EndGatewayConfiguration
implements FeatureConfiguration {
    public static final Codec<EndGatewayConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockPos.CODEC.optionalFieldOf("exit").forGetter(endGatewayConfiguration -> endGatewayConfiguration.exit), (App)Codec.BOOL.fieldOf("exact").forGetter(endGatewayConfiguration -> endGatewayConfiguration.exact)).apply((Applicative)instance, (arg_0, arg_1) -> EndGatewayConfiguration.new(arg_0, arg_1)));
    private final Optional<BlockPos> exit;
    private final boolean exact;

    private EndGatewayConfiguration(Optional<BlockPos> optional, boolean bl) {
        this.exit = optional;
        this.exact = bl;
    }

    public static EndGatewayConfiguration knownExit(BlockPos blockPos, boolean bl) {
        return new EndGatewayConfiguration(Optional.of(blockPos), bl);
    }

    public static EndGatewayConfiguration delayedExitSearch() {
        return new EndGatewayConfiguration(Optional.empty(), false);
    }

    public Optional<BlockPos> getExit() {
        return this.exit;
    }

    public boolean isExitExact() {
        return this.exact;
    }
}

