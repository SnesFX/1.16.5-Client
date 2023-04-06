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
package net.minecraft.world.level.levelgen.feature.blockplacers;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.blockplacers.BlockPlacer;
import net.minecraft.world.level.levelgen.feature.blockplacers.BlockPlacerType;

public class ColumnPlacer
extends BlockPlacer {
    public static final Codec<ColumnPlacer> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("min_size").forGetter(columnPlacer -> columnPlacer.minSize), (App)Codec.INT.fieldOf("extra_size").forGetter(columnPlacer -> columnPlacer.extraSize)).apply((Applicative)instance, (arg_0, arg_1) -> ColumnPlacer.new(arg_0, arg_1)));
    private final int minSize;
    private final int extraSize;

    public ColumnPlacer(int n, int n2) {
        this.minSize = n;
        this.extraSize = n2;
    }

    @Override
    protected BlockPlacerType<?> type() {
        return BlockPlacerType.COLUMN_PLACER;
    }

    @Override
    public void place(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Random random) {
        BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();
        int n = this.minSize + random.nextInt(random.nextInt(this.extraSize + 1) + 1);
        for (int i = 0; i < n; ++i) {
            levelAccessor.setBlock(mutableBlockPos, blockState, 2);
            mutableBlockPos.move(Direction.UP);
        }
    }
}

