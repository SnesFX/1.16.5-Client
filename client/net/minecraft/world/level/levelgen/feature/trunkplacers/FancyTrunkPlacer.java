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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class FancyTrunkPlacer
extends TrunkPlacer {
    public static final Codec<FancyTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> FancyTrunkPlacer.trunkPlacerParts(instance).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> FancyTrunkPlacer.new(arg_0, arg_1, arg_2)));

    public FancyTrunkPlacer(int n, int n2, int n3) {
        super(n, n2, n3);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TrunkPlacerType.FANCY_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedRW levelSimulatedRW, Random random, int n, BlockPos blockPos, Set<BlockPos> set, BoundingBox boundingBox, TreeConfiguration treeConfiguration) {
        int n2;
        Object object;
        int n3 = 5;
        int n4 = n + 2;
        int n5 = Mth.floor((double)n4 * 0.618);
        if (!treeConfiguration.fromSapling) {
            FancyTrunkPlacer.setDirtAt(levelSimulatedRW, blockPos.below());
        }
        double d = 1.0;
        int n6 = Math.min(1, Mth.floor(1.382 + Math.pow(1.0 * (double)n4 / 13.0, 2.0)));
        int n7 = blockPos.getY() + n5;
        ArrayList arrayList = Lists.newArrayList();
        arrayList.add(new FoliageCoords(blockPos.above(n2), n7));
        for (n2 = n4 - 5; n2 >= 0; --n2) {
            float f = this.treeShape(n4, n2);
            if (f < 0.0f) continue;
            for (int i = 0; i < n6; ++i) {
                double d2;
                double d3;
                BlockPos blockPos2;
                object = 1.0;
                double d4 = 1.0 * (double)f * ((double)random.nextFloat() + 0.328);
                double d5 = d4 * Math.sin(d2 = (double)(random.nextFloat() * 2.0f) * 3.141592653589793) + 0.5;
                BlockPos blockPos3 = blockPos.offset(d5, (double)(n2 - 1), d3 = d4 * Math.cos(d2) + 0.5);
                if (!this.makeLimb(levelSimulatedRW, random, blockPos3, blockPos2 = blockPos3.above(5), false, set, boundingBox, treeConfiguration)) continue;
                int n8 = blockPos.getX() - blockPos3.getX();
                int n9 = blockPos.getZ() - blockPos3.getZ();
                double d6 = (double)blockPos3.getY() - Math.sqrt(n8 * n8 + n9 * n9) * 0.381;
                int n10 = d6 > (double)n7 ? n7 : (int)d6;
                BlockPos blockPos4 = new BlockPos(blockPos.getX(), n10, blockPos.getZ());
                if (!this.makeLimb(levelSimulatedRW, random, blockPos4, blockPos3, false, set, boundingBox, treeConfiguration)) continue;
                arrayList.add(new FoliageCoords(blockPos3, blockPos4.getY()));
            }
        }
        this.makeLimb(levelSimulatedRW, random, blockPos, blockPos.above(n5), true, set, boundingBox, treeConfiguration);
        this.makeBranches(levelSimulatedRW, random, n4, blockPos, arrayList, set, boundingBox, treeConfiguration);
        ArrayList arrayList2 = Lists.newArrayList();
        Iterator iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            object = (FoliageCoords)iterator.next();
            if (!this.trimBranches(n4, ((FoliageCoords)object).getBranchBase() - blockPos.getY())) continue;
            arrayList2.add(((FoliageCoords)object).attachment);
        }
        return arrayList2;
    }

    private boolean makeLimb(LevelSimulatedRW levelSimulatedRW, Random random, BlockPos blockPos, BlockPos blockPos2, boolean bl, Set<BlockPos> set, BoundingBox boundingBox, TreeConfiguration treeConfiguration) {
        if (!bl && Objects.equals(blockPos, blockPos2)) {
            return true;
        }
        BlockPos blockPos3 = blockPos2.offset(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());
        int n = this.getSteps(blockPos3);
        float f = (float)blockPos3.getX() / (float)n;
        float f2 = (float)blockPos3.getY() / (float)n;
        float f3 = (float)blockPos3.getZ() / (float)n;
        for (int i = 0; i <= n; ++i) {
            BlockPos blockPos4 = blockPos.offset(0.5f + (float)i * f, 0.5f + (float)i * f2, 0.5f + (float)i * f3);
            if (bl) {
                FancyTrunkPlacer.setBlock(levelSimulatedRW, blockPos4, (BlockState)treeConfiguration.trunkProvider.getState(random, blockPos4).setValue(RotatedPillarBlock.AXIS, this.getLogAxis(blockPos, blockPos4)), boundingBox);
                set.add(blockPos4.immutable());
                continue;
            }
            if (TreeFeature.isFree(levelSimulatedRW, blockPos4)) continue;
            return false;
        }
        return true;
    }

    private int getSteps(BlockPos blockPos) {
        int n = Mth.abs(blockPos.getX());
        int n2 = Mth.abs(blockPos.getY());
        int n3 = Mth.abs(blockPos.getZ());
        return Math.max(n, Math.max(n2, n3));
    }

    private Direction.Axis getLogAxis(BlockPos blockPos, BlockPos blockPos2) {
        int n;
        Direction.Axis axis = Direction.Axis.Y;
        int n2 = Math.abs(blockPos2.getX() - blockPos.getX());
        int n3 = Math.max(n2, n = Math.abs(blockPos2.getZ() - blockPos.getZ()));
        if (n3 > 0) {
            axis = n2 == n3 ? Direction.Axis.X : Direction.Axis.Z;
        }
        return axis;
    }

    private boolean trimBranches(int n, int n2) {
        return (double)n2 >= (double)n * 0.2;
    }

    private void makeBranches(LevelSimulatedRW levelSimulatedRW, Random random, int n, BlockPos blockPos, List<FoliageCoords> list, Set<BlockPos> set, BoundingBox boundingBox, TreeConfiguration treeConfiguration) {
        for (FoliageCoords foliageCoords : list) {
            int n2 = foliageCoords.getBranchBase();
            BlockPos blockPos2 = new BlockPos(blockPos.getX(), n2, blockPos.getZ());
            if (blockPos2.equals(foliageCoords.attachment.foliagePos()) || !this.trimBranches(n, n2 - blockPos.getY())) continue;
            this.makeLimb(levelSimulatedRW, random, blockPos2, foliageCoords.attachment.foliagePos(), true, set, boundingBox, treeConfiguration);
        }
    }

    private float treeShape(int n, int n2) {
        if ((float)n2 < (float)n * 0.3f) {
            return -1.0f;
        }
        float f = (float)n / 2.0f;
        float f2 = f - (float)n2;
        float f3 = Mth.sqrt(f * f - f2 * f2);
        if (f2 == 0.0f) {
            f3 = f;
        } else if (Math.abs(f2) >= f) {
            return 0.0f;
        }
        return f3 * 0.5f;
    }

    static class FoliageCoords {
        private final FoliagePlacer.FoliageAttachment attachment;
        private final int branchBase;

        public FoliageCoords(BlockPos blockPos, int n) {
            this.attachment = new FoliagePlacer.FoliageAttachment(blockPos, 0, false);
            this.branchBase = n;
        }

        public int getBranchBase() {
            return this.branchBase;
        }
    }

}

