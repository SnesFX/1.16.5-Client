/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P2
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Mu
 */
package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public abstract class FoliagePlacer {
    public static final Codec<FoliagePlacer> CODEC = Registry.FOLIAGE_PLACER_TYPES.dispatch(FoliagePlacer::type, FoliagePlacerType::codec);
    protected final UniformInt radius;
    protected final UniformInt offset;

    protected static <P extends FoliagePlacer> Products.P2<RecordCodecBuilder.Mu<P>, UniformInt, UniformInt> foliagePlacerParts(RecordCodecBuilder.Instance<P> instance) {
        return instance.group((App)UniformInt.codec(0, 8, 8).fieldOf("radius").forGetter(foliagePlacer -> foliagePlacer.radius), (App)UniformInt.codec(0, 8, 8).fieldOf("offset").forGetter(foliagePlacer -> foliagePlacer.offset));
    }

    public FoliagePlacer(UniformInt uniformInt, UniformInt uniformInt2) {
        this.radius = uniformInt;
        this.offset = uniformInt2;
    }

    protected abstract FoliagePlacerType<?> type();

    public void createFoliage(LevelSimulatedRW levelSimulatedRW, Random random, TreeConfiguration treeConfiguration, int n, FoliageAttachment foliageAttachment, int n2, int n3, Set<BlockPos> set, BoundingBox boundingBox) {
        this.createFoliage(levelSimulatedRW, random, treeConfiguration, n, foliageAttachment, n2, n3, set, this.offset(random), boundingBox);
    }

    protected abstract void createFoliage(LevelSimulatedRW var1, Random var2, TreeConfiguration var3, int var4, FoliageAttachment var5, int var6, int var7, Set<BlockPos> var8, int var9, BoundingBox var10);

    public abstract int foliageHeight(Random var1, int var2, TreeConfiguration var3);

    public int foliageRadius(Random random, int n) {
        return this.radius.sample(random);
    }

    private int offset(Random random) {
        return this.offset.sample(random);
    }

    protected abstract boolean shouldSkipLocation(Random var1, int var2, int var3, int var4, int var5, boolean var6);

    protected boolean shouldSkipLocationSigned(Random random, int n, int n2, int n3, int n4, boolean bl) {
        int n5;
        int n6;
        if (bl) {
            n6 = Math.min(Math.abs(n), Math.abs(n - 1));
            n5 = Math.min(Math.abs(n3), Math.abs(n3 - 1));
        } else {
            n6 = Math.abs(n);
            n5 = Math.abs(n3);
        }
        return this.shouldSkipLocation(random, n6, n2, n5, n4, bl);
    }

    protected void placeLeavesRow(LevelSimulatedRW levelSimulatedRW, Random random, TreeConfiguration treeConfiguration, BlockPos blockPos, int n, Set<BlockPos> set, int n2, boolean bl, BoundingBox boundingBox) {
        int n3 = bl ? 1 : 0;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = -n; i <= n + n3; ++i) {
            for (int j = -n; j <= n + n3; ++j) {
                if (this.shouldSkipLocationSigned(random, i, n2, j, n, bl)) continue;
                mutableBlockPos.setWithOffset(blockPos, i, n2, j);
                if (!TreeFeature.validTreePos(levelSimulatedRW, mutableBlockPos)) continue;
                levelSimulatedRW.setBlock(mutableBlockPos, treeConfiguration.leavesProvider.getState(random, mutableBlockPos), 19);
                boundingBox.expand(new BoundingBox(mutableBlockPos, mutableBlockPos));
                set.add(mutableBlockPos.immutable());
            }
        }
    }

    public static final class FoliageAttachment {
        private final BlockPos foliagePos;
        private final int radiusOffset;
        private final boolean doubleTrunk;

        public FoliageAttachment(BlockPos blockPos, int n, boolean bl) {
            this.foliagePos = blockPos;
            this.radiusOffset = n;
            this.doubleTrunk = bl;
        }

        public BlockPos foliagePos() {
            return this.foliagePos;
        }

        public int radiusOffset() {
            return this.radiusOffset;
        }

        public boolean doubleTrunk() {
            return this.doubleTrunk;
        }
    }

}

