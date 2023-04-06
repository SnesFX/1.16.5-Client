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
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class DarkOakTrunkPlacer
extends TrunkPlacer {
    public static final Codec<DarkOakTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> DarkOakTrunkPlacer.trunkPlacerParts(instance).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> DarkOakTrunkPlacer.new(arg_0, arg_1, arg_2)));

    public DarkOakTrunkPlacer(int n, int n2, int n3) {
        super(n, n2, n3);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TrunkPlacerType.DARK_OAK_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedRW levelSimulatedRW, Random random, int n, BlockPos blockPos, Set<BlockPos> set, BoundingBox boundingBox, TreeConfiguration treeConfiguration) {
        int n2;
        int n3;
        ArrayList arrayList = Lists.newArrayList();
        BlockPos blockPos2 = blockPos.below();
        DarkOakTrunkPlacer.setDirtAt(levelSimulatedRW, blockPos2);
        DarkOakTrunkPlacer.setDirtAt(levelSimulatedRW, blockPos2.east());
        DarkOakTrunkPlacer.setDirtAt(levelSimulatedRW, blockPos2.south());
        DarkOakTrunkPlacer.setDirtAt(levelSimulatedRW, blockPos2.south().east());
        Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        int n4 = n - random.nextInt(4);
        int n5 = 2 - random.nextInt(3);
        int n6 = blockPos.getX();
        int n7 = blockPos.getY();
        int n8 = blockPos.getZ();
        int n9 = n6;
        int n10 = n8;
        int n11 = n7 + n - 1;
        for (n3 = 0; n3 < n; ++n3) {
            BlockPos blockPos3;
            if (n3 >= n4 && n5 > 0) {
                n9 += direction.getStepX();
                n10 += direction.getStepZ();
                --n5;
            }
            if (!TreeFeature.isAirOrLeaves(levelSimulatedRW, blockPos3 = new BlockPos(n9, n2 = n7 + n3, n10))) continue;
            DarkOakTrunkPlacer.placeLog(levelSimulatedRW, random, blockPos3, set, boundingBox, treeConfiguration);
            DarkOakTrunkPlacer.placeLog(levelSimulatedRW, random, blockPos3.east(), set, boundingBox, treeConfiguration);
            DarkOakTrunkPlacer.placeLog(levelSimulatedRW, random, blockPos3.south(), set, boundingBox, treeConfiguration);
            DarkOakTrunkPlacer.placeLog(levelSimulatedRW, random, blockPos3.east().south(), set, boundingBox, treeConfiguration);
        }
        arrayList.add(new FoliagePlacer.FoliageAttachment(new BlockPos(n9, n11, n10), 0, true));
        for (n3 = -1; n3 <= 2; ++n3) {
            for (n2 = -1; n2 <= 2; ++n2) {
                if (n3 >= 0 && n3 <= 1 && n2 >= 0 && n2 <= 1 || random.nextInt(3) > 0) continue;
                int n12 = random.nextInt(3) + 2;
                for (int i = 0; i < n12; ++i) {
                    DarkOakTrunkPlacer.placeLog(levelSimulatedRW, random, new BlockPos(n6 + n3, n11 - i - 1, n8 + n2), set, boundingBox, treeConfiguration);
                }
                arrayList.add(new FoliagePlacer.FoliageAttachment(new BlockPos(n9 + n3, n11, n10 + n2), 0, false));
            }
        }
        return arrayList;
    }
}

