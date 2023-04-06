/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class AcaciaFoliagePlacer
extends FoliagePlacer {
    public static final Codec<AcaciaFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> AcaciaFoliagePlacer.foliagePlacerParts(instance).apply((Applicative)instance, (arg_0, arg_1) -> AcaciaFoliagePlacer.new(arg_0, arg_1)));

    public AcaciaFoliagePlacer(UniformInt uniformInt, UniformInt uniformInt2) {
        super(uniformInt, uniformInt2);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return FoliagePlacerType.ACACIA_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(LevelSimulatedRW levelSimulatedRW, Random random, TreeConfiguration treeConfiguration, int n, FoliagePlacer.FoliageAttachment foliageAttachment, int n2, int n3, Set<BlockPos> set, int n4, BoundingBox boundingBox) {
        boolean bl = foliageAttachment.doubleTrunk();
        BlockPos blockPos = foliageAttachment.foliagePos().above(n4);
        this.placeLeavesRow(levelSimulatedRW, random, treeConfiguration, blockPos, n3 + foliageAttachment.radiusOffset(), set, -1 - n2, bl, boundingBox);
        this.placeLeavesRow(levelSimulatedRW, random, treeConfiguration, blockPos, n3 - 1, set, -n2, bl, boundingBox);
        this.placeLeavesRow(levelSimulatedRW, random, treeConfiguration, blockPos, n3 + foliageAttachment.radiusOffset() - 1, set, 0, bl, boundingBox);
    }

    @Override
    public int foliageHeight(Random random, int n, TreeConfiguration treeConfiguration) {
        return 0;
    }

    @Override
    protected boolean shouldSkipLocation(Random random, int n, int n2, int n3, int n4, boolean bl) {
        if (n2 == 0) {
            return (n > 1 || n3 > 1) && n != 0 && n3 != 0;
        }
        return n == n4 && n3 == n4 && n4 > 0;
    }
}

