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
import net.minecraft.util.Mth;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class MegaPineFoliagePlacer
extends FoliagePlacer {
    public static final Codec<MegaPineFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> MegaPineFoliagePlacer.foliagePlacerParts(instance).and((App)UniformInt.codec(0, 16, 8).fieldOf("crown_height").forGetter(megaPineFoliagePlacer -> megaPineFoliagePlacer.crownHeight)).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> MegaPineFoliagePlacer.new(arg_0, arg_1, arg_2)));
    private final UniformInt crownHeight;

    public MegaPineFoliagePlacer(UniformInt uniformInt, UniformInt uniformInt2, UniformInt uniformInt3) {
        super(uniformInt, uniformInt2);
        this.crownHeight = uniformInt3;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return FoliagePlacerType.MEGA_PINE_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(LevelSimulatedRW levelSimulatedRW, Random random, TreeConfiguration treeConfiguration, int n, FoliagePlacer.FoliageAttachment foliageAttachment, int n2, int n3, Set<BlockPos> set, int n4, BoundingBox boundingBox) {
        BlockPos blockPos = foliageAttachment.foliagePos();
        int n5 = 0;
        for (int i = blockPos.getY() - n2 + n4; i <= blockPos.getY() + n4; ++i) {
            int n6 = blockPos.getY() - i;
            int n7 = n3 + foliageAttachment.radiusOffset() + Mth.floor((float)n6 / (float)n2 * 3.5f);
            int n8 = n6 > 0 && n7 == n5 && (i & 1) == 0 ? n7 + 1 : n7;
            this.placeLeavesRow(levelSimulatedRW, random, treeConfiguration, new BlockPos(blockPos.getX(), i, blockPos.getZ()), n8, set, 0, foliageAttachment.doubleTrunk(), boundingBox);
            n5 = n7;
        }
    }

    @Override
    public int foliageHeight(Random random, int n, TreeConfiguration treeConfiguration) {
        return this.crownHeight.sample(random);
    }

    @Override
    protected boolean shouldSkipLocation(Random random, int n, int n2, int n3, int n4, boolean bl) {
        if (n + n3 >= 7) {
            return true;
        }
        return n * n + n3 * n3 > n4 * n4;
    }
}

