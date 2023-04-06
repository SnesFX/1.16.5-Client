/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products
 *  com.mojang.datafixers.Products$P3
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Mu
 */
package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public abstract class TrunkPlacer {
    public static final Codec<TrunkPlacer> CODEC = Registry.TRUNK_PLACER_TYPES.dispatch(TrunkPlacer::type, TrunkPlacerType::codec);
    protected final int baseHeight;
    protected final int heightRandA;
    protected final int heightRandB;

    protected static <P extends TrunkPlacer> Products.P3<RecordCodecBuilder.Mu<P>, Integer, Integer, Integer> trunkPlacerParts(RecordCodecBuilder.Instance<P> instance) {
        return instance.group((App)Codec.intRange((int)0, (int)32).fieldOf("base_height").forGetter(trunkPlacer -> trunkPlacer.baseHeight), (App)Codec.intRange((int)0, (int)24).fieldOf("height_rand_a").forGetter(trunkPlacer -> trunkPlacer.heightRandA), (App)Codec.intRange((int)0, (int)24).fieldOf("height_rand_b").forGetter(trunkPlacer -> trunkPlacer.heightRandB));
    }

    public TrunkPlacer(int n, int n2, int n3) {
        this.baseHeight = n;
        this.heightRandA = n2;
        this.heightRandB = n3;
    }

    protected abstract TrunkPlacerType<?> type();

    public abstract List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedRW var1, Random var2, int var3, BlockPos var4, Set<BlockPos> var5, BoundingBox var6, TreeConfiguration var7);

    public int getTreeHeight(Random random) {
        return this.baseHeight + random.nextInt(this.heightRandA + 1) + random.nextInt(this.heightRandB + 1);
    }

    protected static void setBlock(LevelWriter levelWriter, BlockPos blockPos, BlockState blockState, BoundingBox boundingBox) {
        TreeFeature.setBlockKnownShape(levelWriter, blockPos, blockState);
        boundingBox.expand(new BoundingBox(blockPos, blockPos));
    }

    private static boolean isDirt(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
        return levelSimulatedReader.isStateAtPosition(blockPos, blockState -> {
            Block block = blockState.getBlock();
            return Feature.isDirt(block) && !blockState.is(Blocks.GRASS_BLOCK) && !blockState.is(Blocks.MYCELIUM);
        });
    }

    protected static void setDirtAt(LevelSimulatedRW levelSimulatedRW, BlockPos blockPos) {
        if (!TrunkPlacer.isDirt(levelSimulatedRW, blockPos)) {
            TreeFeature.setBlockKnownShape(levelSimulatedRW, blockPos, Blocks.DIRT.defaultBlockState());
        }
    }

    protected static boolean placeLog(LevelSimulatedRW levelSimulatedRW, Random random, BlockPos blockPos, Set<BlockPos> set, BoundingBox boundingBox, TreeConfiguration treeConfiguration) {
        if (TreeFeature.validTreePos(levelSimulatedRW, blockPos)) {
            TrunkPlacer.setBlock(levelSimulatedRW, blockPos, treeConfiguration.trunkProvider.getState(random, blockPos), boundingBox);
            set.add(blockPos.immutable());
            return true;
        }
        return false;
    }

    protected static void placeLogIfFree(LevelSimulatedRW levelSimulatedRW, Random random, BlockPos.MutableBlockPos mutableBlockPos, Set<BlockPos> set, BoundingBox boundingBox, TreeConfiguration treeConfiguration) {
        if (TreeFeature.isFree(levelSimulatedRW, mutableBlockPos)) {
            TrunkPlacer.placeLog(levelSimulatedRW, random, mutableBlockPos, set, boundingBox, treeConfiguration);
        }
    }
}

