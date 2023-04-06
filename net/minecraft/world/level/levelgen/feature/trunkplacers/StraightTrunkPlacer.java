/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function3
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class StraightTrunkPlacer
extends TrunkPlacer {
    public static final Codec<StraightTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> StraightTrunkPlacer.trunkPlacerParts(instance).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> StraightTrunkPlacer.new(arg_0, arg_1, arg_2)));

    public StraightTrunkPlacer(int n, int n2, int n3) {
        super(n, n2, n3);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TrunkPlacerType.STRAIGHT_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedRW levelSimulatedRW, Random random, int n, BlockPos blockPos, Set<BlockPos> set, BoundingBox boundingBox, TreeConfiguration treeConfiguration) {
        StraightTrunkPlacer.setDirtAt(levelSimulatedRW, blockPos.below());
        for (int i = 0; i < n; ++i) {
            StraightTrunkPlacer.placeLog(levelSimulatedRW, random, blockPos.above(i), set, boundingBox, treeConfiguration);
        }
        return ImmutableList.of((Object)new FoliagePlacer.FoliageAttachment(blockPos.above(n), 0, false));
    }
}

