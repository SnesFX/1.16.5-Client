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
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.levelgen.flat;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class FlatLayerInfo {
    public static final Codec<FlatLayerInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.intRange((int)0, (int)256).fieldOf("height").forGetter(FlatLayerInfo::getHeight), (App)Registry.BLOCK.fieldOf("block").orElse((Object)Blocks.AIR).forGetter(flatLayerInfo -> flatLayerInfo.getBlockState().getBlock())).apply((Applicative)instance, (arg_0, arg_1) -> FlatLayerInfo.new(arg_0, arg_1)));
    private final BlockState blockState;
    private final int height;
    private int start;

    public FlatLayerInfo(int n, Block block) {
        this.height = n;
        this.blockState = block.defaultBlockState();
    }

    public int getHeight() {
        return this.height;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    public int getStart() {
        return this.start;
    }

    public void setStart(int n) {
        this.start = n;
    }

    public String toString() {
        return (this.height != 1 ? this.height + "*" : "") + Registry.BLOCK.getKey(this.blockState.getBlock());
    }
}

