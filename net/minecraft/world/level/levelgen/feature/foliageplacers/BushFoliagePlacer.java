/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function3
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class BushFoliagePlacer
extends BlobFoliagePlacer {
    public static final Codec<BushFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> BushFoliagePlacer.blobParts(instance).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> BushFoliagePlacer.new(arg_0, arg_1, arg_2)));

    public BushFoliagePlacer(UniformInt uniformInt, UniformInt uniformInt2, int n) {
        super(uniformInt, uniformInt2, n);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return FoliagePlacerType.BUSH_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(LevelSimulatedRW levelSimulatedRW, Random random, TreeConfiguration treeConfiguration, int n, FoliagePlacer.FoliageAttachment foliageAttachment, int n2, int n3, Set<BlockPos> set, int n4, BoundingBox boundingBox) {
        for (int i = n4; i >= n4 - n2; --i) {
            int n5 = n3 + foliageAttachment.radiusOffset() - 1 - i;
            this.placeLeavesRow(levelSimulatedRW, random, treeConfiguration, foliageAttachment.foliagePos(), n5, set, i, foliageAttachment.doubleTrunk(), boundingBox);
        }
    }

    @Override
    protected boolean shouldSkipLocation(Random random, int n, int n2, int n3, int n4, boolean bl) {
        return n == n4 && n3 == n4 && random.nextInt(2) == 0;
    }
}

