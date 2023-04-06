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
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.GiantTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class MegaJungleTrunkPlacer
extends GiantTrunkPlacer {
    public static final Codec<MegaJungleTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> MegaJungleTrunkPlacer.trunkPlacerParts(instance).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> MegaJungleTrunkPlacer.new(arg_0, arg_1, arg_2)));

    public MegaJungleTrunkPlacer(int n, int n2, int n3) {
        super(n, n2, n3);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TrunkPlacerType.MEGA_JUNGLE_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedRW levelSimulatedRW, Random random, int n, BlockPos blockPos, Set<BlockPos> set, BoundingBox boundingBox, TreeConfiguration treeConfiguration) {
        ArrayList arrayList = Lists.newArrayList();
        arrayList.addAll(super.placeTrunk(levelSimulatedRW, random, n, blockPos, set, boundingBox, treeConfiguration));
        for (int i = n - 2 - random.nextInt((int)4); i > n / 2; i -= 2 + random.nextInt((int)4)) {
            float f = random.nextFloat() * 6.2831855f;
            int n2 = 0;
            int n3 = 0;
            for (int j = 0; j < 5; ++j) {
                n2 = (int)(1.5f + Mth.cos(f) * (float)j);
                n3 = (int)(1.5f + Mth.sin(f) * (float)j);
                BlockPos blockPos2 = blockPos.offset(n2, i - 3 + j / 2, n3);
                MegaJungleTrunkPlacer.placeLog(levelSimulatedRW, random, blockPos2, set, boundingBox, treeConfiguration);
            }
            arrayList.add(new FoliagePlacer.FoliageAttachment(blockPos.offset(n2, i, n3), -2, false));
        }
        return arrayList;
    }
}

