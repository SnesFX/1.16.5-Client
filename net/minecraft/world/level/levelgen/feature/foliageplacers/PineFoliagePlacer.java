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
package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class PineFoliagePlacer
extends FoliagePlacer {
    public static final Codec<PineFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> PineFoliagePlacer.foliagePlacerParts(instance).and((App)UniformInt.codec(0, 16, 8).fieldOf("height").forGetter(pineFoliagePlacer -> pineFoliagePlacer.height)).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> PineFoliagePlacer.new(arg_0, arg_1, arg_2)));
    private final UniformInt height;

    public PineFoliagePlacer(UniformInt uniformInt, UniformInt uniformInt2, UniformInt uniformInt3) {
        super(uniformInt, uniformInt2);
        this.height = uniformInt3;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return FoliagePlacerType.PINE_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(LevelSimulatedRW levelSimulatedRW, Random random, TreeConfiguration treeConfiguration, int n, FoliagePlacer.FoliageAttachment foliageAttachment, int n2, int n3, Set<BlockPos> set, int n4, BoundingBox boundingBox) {
        int n5 = 0;
        for (int i = n4; i >= n4 - n2; --i) {
            this.placeLeavesRow(levelSimulatedRW, random, treeConfiguration, foliageAttachment.foliagePos(), n5, set, i, foliageAttachment.doubleTrunk(), boundingBox);
            if (n5 >= 1 && i == n4 - n2 + 1) {
                --n5;
                continue;
            }
            if (n5 >= n3 + foliageAttachment.radiusOffset()) continue;
            ++n5;
        }
    }

    @Override
    public int foliageRadius(Random random, int n) {
        return super.foliageRadius(random, n) + random.nextInt(n + 1);
    }

    @Override
    public int foliageHeight(Random random, int n, TreeConfiguration treeConfiguration) {
        return this.height.sample(random);
    }

    @Override
    protected boolean shouldSkipLocation(Random random, int n, int n2, int n3, int n4, boolean bl) {
        return n == n4 && n3 == n4 && n4 > 0;
    }
}

