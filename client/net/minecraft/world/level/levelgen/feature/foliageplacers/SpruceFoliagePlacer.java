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

public class SpruceFoliagePlacer
extends FoliagePlacer {
    public static final Codec<SpruceFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> SpruceFoliagePlacer.foliagePlacerParts(instance).and((App)UniformInt.codec(0, 16, 8).fieldOf("trunk_height").forGetter(spruceFoliagePlacer -> spruceFoliagePlacer.trunkHeight)).apply((Applicative)instance, (arg_0, arg_1, arg_2) -> SpruceFoliagePlacer.new(arg_0, arg_1, arg_2)));
    private final UniformInt trunkHeight;

    public SpruceFoliagePlacer(UniformInt uniformInt, UniformInt uniformInt2, UniformInt uniformInt3) {
        super(uniformInt, uniformInt2);
        this.trunkHeight = uniformInt3;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return FoliagePlacerType.SPRUCE_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(LevelSimulatedRW levelSimulatedRW, Random random, TreeConfiguration treeConfiguration, int n, FoliagePlacer.FoliageAttachment foliageAttachment, int n2, int n3, Set<BlockPos> set, int n4, BoundingBox boundingBox) {
        BlockPos blockPos = foliageAttachment.foliagePos();
        int n5 = random.nextInt(2);
        int n6 = 1;
        int n7 = 0;
        for (int i = n4; i >= -n2; --i) {
            this.placeLeavesRow(levelSimulatedRW, random, treeConfiguration, blockPos, n5, set, i, foliageAttachment.doubleTrunk(), boundingBox);
            if (n5 >= n6) {
                n5 = n7;
                n7 = 1;
                n6 = Math.min(n6 + 1, n3 + foliageAttachment.radiusOffset());
                continue;
            }
            ++n5;
        }
    }

    @Override
    public int foliageHeight(Random random, int n, TreeConfiguration treeConfiguration) {
        return Math.max(4, n - this.trunkHeight.sample(random));
    }

    @Override
    protected boolean shouldSkipLocation(Random random, int n, int n2, int n3, int n4, boolean bl) {
        return n == n4 && n3 == n4 && n4 > 0;
    }
}

