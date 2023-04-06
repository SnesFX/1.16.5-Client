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
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class GiantTrunkPlacer
extends TrunkPlacer {
    public static final Codec<GiantTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> GiantTrunkPlacer.trunkPlacerParts(instance).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> GiantTrunkPlacer.new(arg_0, arg_1, arg_2)));

    public GiantTrunkPlacer(int n, int n2, int n3) {
        super(n, n2, n3);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TrunkPlacerType.GIANT_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedRW levelSimulatedRW, Random random, int n, BlockPos blockPos, Set<BlockPos> set, BoundingBox boundingBox, TreeConfiguration treeConfiguration) {
        BlockPos blockPos2 = blockPos.below();
        GiantTrunkPlacer.setDirtAt(levelSimulatedRW, blockPos2);
        GiantTrunkPlacer.setDirtAt(levelSimulatedRW, blockPos2.east());
        GiantTrunkPlacer.setDirtAt(levelSimulatedRW, blockPos2.south());
        GiantTrunkPlacer.setDirtAt(levelSimulatedRW, blockPos2.south().east());
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < n; ++i) {
            GiantTrunkPlacer.placeLogIfFreeWithOffset(levelSimulatedRW, random, mutableBlockPos, set, boundingBox, treeConfiguration, blockPos, 0, i, 0);
            if (i >= n - 1) continue;
            GiantTrunkPlacer.placeLogIfFreeWithOffset(levelSimulatedRW, random, mutableBlockPos, set, boundingBox, treeConfiguration, blockPos, 1, i, 0);
            GiantTrunkPlacer.placeLogIfFreeWithOffset(levelSimulatedRW, random, mutableBlockPos, set, boundingBox, treeConfiguration, blockPos, 1, i, 1);
            GiantTrunkPlacer.placeLogIfFreeWithOffset(levelSimulatedRW, random, mutableBlockPos, set, boundingBox, treeConfiguration, blockPos, 0, i, 1);
        }
        return ImmutableList.of((Object)new FoliagePlacer.FoliageAttachment(blockPos.above(n), 0, true));
    }

    private static void placeLogIfFreeWithOffset(LevelSimulatedRW levelSimulatedRW, Random random, BlockPos.MutableBlockPos mutableBlockPos, Set<BlockPos> set, BoundingBox boundingBox, TreeConfiguration treeConfiguration, BlockPos blockPos, int n, int n2, int n3) {
        mutableBlockPos.setWithOffset(blockPos, n, n2, n3);
        GiantTrunkPlacer.placeLogIfFree(levelSimulatedRW, random, mutableBlockPos, set, boundingBox, treeConfiguration);
    }
}

