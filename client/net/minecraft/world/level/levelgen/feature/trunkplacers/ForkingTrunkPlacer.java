/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Function3
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 */
package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class ForkingTrunkPlacer
extends TrunkPlacer {
    public static final Codec<ForkingTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> ForkingTrunkPlacer.trunkPlacerParts(instance).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> ForkingTrunkPlacer.new(arg_0, arg_1, arg_2)));

    public ForkingTrunkPlacer(int n, int n2, int n3) {
        super(n, n2, n3);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TrunkPlacerType.FORKING_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedRW levelSimulatedRW, Random random, int n, BlockPos blockPos, Set<BlockPos> set, BoundingBox boundingBox, TreeConfiguration treeConfiguration) {
        int n2;
        ForkingTrunkPlacer.setDirtAt(levelSimulatedRW, blockPos.below());
        ArrayList arrayList = Lists.newArrayList();
        Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        int n3 = n - random.nextInt(4) - 1;
        int n4 = 3 - random.nextInt(3);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        int n5 = blockPos.getX();
        int n6 = blockPos.getZ();
        int n7 = 0;
        for (int i = 0; i < n; ++i) {
            n2 = blockPos.getY() + i;
            if (i >= n3 && n4 > 0) {
                n5 += direction.getStepX();
                n6 += direction.getStepZ();
                --n4;
            }
            if (!ForkingTrunkPlacer.placeLog(levelSimulatedRW, random, mutableBlockPos.set(n5, n2, n6), set, boundingBox, treeConfiguration)) continue;
            n7 = n2 + 1;
        }
        arrayList.add(new FoliagePlacer.FoliageAttachment(new BlockPos(n5, n7, n6), 1, false));
        n5 = blockPos.getX();
        n6 = blockPos.getZ();
        Direction direction2 = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        if (direction2 != direction) {
            n2 = n3 - random.nextInt(2) - 1;
            int n8 = 1 + random.nextInt(3);
            n7 = 0;
            for (int i = n2; i < n && n8 > 0; ++i, --n8) {
                if (i < 1) continue;
                int n9 = blockPos.getY() + i;
                if (!ForkingTrunkPlacer.placeLog(levelSimulatedRW, random, mutableBlockPos.set(n5 += direction2.getStepX(), n9, n6 += direction2.getStepZ()), set, boundingBox, treeConfiguration)) continue;
                n7 = n9 + 1;
            }
            if (n7 > 1) {
                arrayList.add(new FoliagePlacer.FoliageAttachment(new BlockPos(n5, n7, n6), 0, false));
            }
        }
        return arrayList;
    }
}

