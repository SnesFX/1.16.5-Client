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
package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;

public class SurfaceBuilderBaseConfiguration
implements SurfaceBuilderConfiguration {
    public static final Codec<SurfaceBuilderBaseConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BlockState.CODEC.fieldOf("top_material").forGetter(surfaceBuilderBaseConfiguration -> surfaceBuilderBaseConfiguration.topMaterial), (App)BlockState.CODEC.fieldOf("under_material").forGetter(surfaceBuilderBaseConfiguration -> surfaceBuilderBaseConfiguration.underMaterial), (App)BlockState.CODEC.fieldOf("underwater_material").forGetter(surfaceBuilderBaseConfiguration -> surfaceBuilderBaseConfiguration.underwaterMaterial)).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> SurfaceBuilderBaseConfiguration.new(arg_0, arg_1, arg_2)));
    private final BlockState topMaterial;
    private final BlockState underMaterial;
    private final BlockState underwaterMaterial;

    public SurfaceBuilderBaseConfiguration(BlockState blockState, BlockState blockState2, BlockState blockState3) {
        this.topMaterial = blockState;
        this.underMaterial = blockState2;
        this.underwaterMaterial = blockState3;
    }

    @Override
    public BlockState getTopMaterial() {
        return this.topMaterial;
    }

    @Override
    public BlockState getUnderMaterial() {
        return this.underMaterial;
    }

    public BlockState getUnderwaterMaterial() {
        return this.underwaterMaterial;
    }
}

