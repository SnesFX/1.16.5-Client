/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.CarvingMaskDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

public class CarvingMaskDecorator
extends FeatureDecorator<CarvingMaskDecoratorConfiguration> {
    public CarvingMaskDecorator(Codec<CarvingMaskDecoratorConfiguration> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecorationContext decorationContext, Random random, CarvingMaskDecoratorConfiguration carvingMaskDecoratorConfiguration, BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        BitSet bitSet = decorationContext.getCarvingMask(chunkPos, carvingMaskDecoratorConfiguration.step);
        return IntStream.range(0, bitSet.length()).filter(n -> bitSet.get(n) && random.nextFloat() < carvingMaskDecoratorConfiguration.probability).mapToObj(n -> {
            int n2 = n & 0xF;
            int n3 = n >> 4 & 0xF;
            int n4 = n >> 8;
            return new BlockPos(chunkPos.getMinBlockX() + n2, n4, chunkPos.getMinBlockZ() + n3);
        });
    }
}

