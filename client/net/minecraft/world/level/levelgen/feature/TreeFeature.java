/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public class TreeFeature
extends Feature<TreeConfiguration> {
    public TreeFeature(Codec<TreeConfiguration> codec) {
        super(codec);
    }

    public static boolean isFree(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
        return TreeFeature.validTreePos(levelSimulatedReader, blockPos) || levelSimulatedReader.isStateAtPosition(blockPos, blockState -> blockState.is(BlockTags.LOGS));
    }

    private static boolean isVine(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
        return levelSimulatedReader.isStateAtPosition(blockPos, blockState -> blockState.is(Blocks.VINE));
    }

    private static boolean isBlockWater(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
        return levelSimulatedReader.isStateAtPosition(blockPos, blockState -> blockState.is(Blocks.WATER));
    }

    public static boolean isAirOrLeaves(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
        return levelSimulatedReader.isStateAtPosition(blockPos, blockState -> blockState.isAir() || blockState.is(BlockTags.LEAVES));
    }

    private static boolean isGrassOrDirtOrFarmland(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
        return levelSimulatedReader.isStateAtPosition(blockPos, blockState -> {
            Block block = blockState.getBlock();
            return TreeFeature.isDirt(block) || block == Blocks.FARMLAND;
        });
    }

    private static boolean isReplaceablePlant(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
        return levelSimulatedReader.isStateAtPosition(blockPos, blockState -> {
            Material material = blockState.getMaterial();
            return material == Material.REPLACEABLE_PLANT;
        });
    }

    public static void setBlockKnownShape(LevelWriter levelWriter, BlockPos blockPos, BlockState blockState) {
        levelWriter.setBlock(blockPos, blockState, 19);
    }

    public static boolean validTreePos(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
        return TreeFeature.isAirOrLeaves(levelSimulatedReader, blockPos) || TreeFeature.isReplaceablePlant(levelSimulatedReader, blockPos) || TreeFeature.isBlockWater(levelSimulatedReader, blockPos);
    }

    private boolean doPlace(LevelSimulatedRW levelSimulatedRW, Random random, BlockPos blockPos, Set<BlockPos> set, Set<BlockPos> set2, BoundingBox boundingBox, TreeConfiguration treeConfiguration) {
        BlockPos blockPos2;
        int n;
        int n2 = treeConfiguration.trunkPlacer.getTreeHeight(random);
        int n3 = treeConfiguration.foliagePlacer.foliageHeight(random, n2, treeConfiguration);
        int n4 = n2 - n3;
        int n5 = treeConfiguration.foliagePlacer.foliageRadius(random, n4);
        if (!treeConfiguration.fromSapling) {
            int n6 = levelSimulatedRW.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR, blockPos).getY();
            n = levelSimulatedRW.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, blockPos).getY();
            if (n - n6 > treeConfiguration.maxWaterDepth) {
                return false;
            }
            int n7 = treeConfiguration.heightmap == Heightmap.Types.OCEAN_FLOOR ? n6 : (treeConfiguration.heightmap == Heightmap.Types.WORLD_SURFACE ? n : levelSimulatedRW.getHeightmapPos(treeConfiguration.heightmap, blockPos).getY());
            blockPos2 = new BlockPos(blockPos.getX(), n7, blockPos.getZ());
        } else {
            blockPos2 = blockPos;
        }
        if (blockPos2.getY() < 1 || blockPos2.getY() + n2 + 1 > 256) {
            return false;
        }
        if (!TreeFeature.isGrassOrDirtOrFarmland(levelSimulatedRW, blockPos2.below())) {
            return false;
        }
        OptionalInt optionalInt = treeConfiguration.minimumSize.minClippedHeight();
        n = this.getMaxFreeTreeHeight(levelSimulatedRW, n2, blockPos2, treeConfiguration);
        if (!(n >= n2 || optionalInt.isPresent() && n >= optionalInt.getAsInt())) {
            return false;
        }
        List<FoliagePlacer.FoliageAttachment> list = treeConfiguration.trunkPlacer.placeTrunk(levelSimulatedRW, random, n, blockPos2, set, boundingBox, treeConfiguration);
        list.forEach(foliageAttachment -> treeConfiguration.foliagePlacer.createFoliage(levelSimulatedRW, random, treeConfiguration, n, (FoliagePlacer.FoliageAttachment)foliageAttachment, n3, n5, set2, boundingBox));
        return true;
    }

    private int getMaxFreeTreeHeight(LevelSimulatedReader levelSimulatedReader, int n, BlockPos blockPos, TreeConfiguration treeConfiguration) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i <= n + 1; ++i) {
            int n2 = treeConfiguration.minimumSize.getSizeAtHeight(n, i);
            for (int j = -n2; j <= n2; ++j) {
                for (int k = -n2; k <= n2; ++k) {
                    mutableBlockPos.setWithOffset(blockPos, j, i, k);
                    if (TreeFeature.isFree(levelSimulatedReader, mutableBlockPos) && (treeConfiguration.ignoreVines || !TreeFeature.isVine(levelSimulatedReader, mutableBlockPos))) continue;
                    return i - 2;
                }
            }
        }
        return n;
    }

    @Override
    protected void setBlock(LevelWriter levelWriter, BlockPos blockPos, BlockState blockState) {
        TreeFeature.setBlockKnownShape(levelWriter, blockPos, blockState);
    }

    @Override
    public final boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, TreeConfiguration treeConfiguration) {
        Object object;
        HashSet hashSet = Sets.newHashSet();
        HashSet hashSet2 = Sets.newHashSet();
        HashSet hashSet3 = Sets.newHashSet();
        BoundingBox boundingBox = BoundingBox.getUnknownBox();
        boolean bl = this.doPlace(worldGenLevel, random, blockPos, hashSet, hashSet2, boundingBox, treeConfiguration);
        if (boundingBox.x0 > boundingBox.x1 || !bl || hashSet.isEmpty()) {
            return false;
        }
        if (!treeConfiguration.decorators.isEmpty()) {
            object = Lists.newArrayList((Iterable)hashSet);
            ArrayList arrayList = Lists.newArrayList((Iterable)hashSet2);
            object.sort(Comparator.comparingInt(Vec3i::getY));
            arrayList.sort(Comparator.comparingInt(Vec3i::getY));
            treeConfiguration.decorators.forEach(arg_0 -> TreeFeature.lambda$place$7(worldGenLevel, random, (List)object, arrayList, hashSet3, boundingBox, arg_0));
        }
        object = this.updateLeaves(worldGenLevel, boundingBox, hashSet, hashSet3);
        StructureTemplate.updateShapeAtEdge(worldGenLevel, 3, (DiscreteVoxelShape)object, boundingBox.x0, boundingBox.y0, boundingBox.z0);
        return true;
    }

    private DiscreteVoxelShape updateLeaves(LevelAccessor levelAccessor, BoundingBox boundingBox, Set<BlockPos> set, Set<BlockPos> set2) {
        ArrayList arrayList = Lists.newArrayList();
        BitSetDiscreteVoxelShape bitSetDiscreteVoxelShape = new BitSetDiscreteVoxelShape(boundingBox.getXSpan(), boundingBox.getYSpan(), boundingBox.getZSpan());
        int n = 6;
        for (int i = 0; i < 6; ++i) {
            arrayList.add(Sets.newHashSet());
        }
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (Object object : Lists.newArrayList(set2)) {
            if (!boundingBox.isInside((Vec3i)object)) continue;
            ((DiscreteVoxelShape)bitSetDiscreteVoxelShape).setFull(((Vec3i)object).getX() - boundingBox.x0, ((Vec3i)object).getY() - boundingBox.y0, ((Vec3i)object).getZ() - boundingBox.z0, true, true);
        }
        for (Object object : Lists.newArrayList(set)) {
            if (boundingBox.isInside((Vec3i)object)) {
                ((DiscreteVoxelShape)bitSetDiscreteVoxelShape).setFull(((Vec3i)object).getX() - boundingBox.x0, ((Vec3i)object).getY() - boundingBox.y0, ((Vec3i)object).getZ() - boundingBox.z0, true, true);
            }
            for (Direction arrdirection : Direction.values()) {
                Object object2;
                mutableBlockPos.setWithOffset((Vec3i)object, arrdirection);
                if (set.contains(mutableBlockPos) || !(object2 = levelAccessor.getBlockState(mutableBlockPos)).hasProperty(BlockStateProperties.DISTANCE)) continue;
                ((Set)arrayList.get(0)).add(mutableBlockPos.immutable());
                TreeFeature.setBlockKnownShape(levelAccessor, mutableBlockPos, (BlockState)object2.setValue(BlockStateProperties.DISTANCE, 1));
                if (!boundingBox.isInside(mutableBlockPos)) continue;
                ((DiscreteVoxelShape)bitSetDiscreteVoxelShape).setFull(mutableBlockPos.getX() - boundingBox.x0, mutableBlockPos.getY() - boundingBox.y0, mutableBlockPos.getZ() - boundingBox.z0, true, true);
            }
        }
        for (int i = 1; i < 6; ++i) {
            Object object;
            object = (Set)arrayList.get(i - 1);
            Set set3 = (Set)arrayList.get(i);
            Iterator iterator = object.iterator();
            while (iterator.hasNext()) {
                BlockPos blockPos = (BlockPos)iterator.next();
                if (boundingBox.isInside(blockPos)) {
                    ((DiscreteVoxelShape)bitSetDiscreteVoxelShape).setFull(blockPos.getX() - boundingBox.x0, blockPos.getY() - boundingBox.y0, blockPos.getZ() - boundingBox.z0, true, true);
                }
                for (Direction direction : Direction.values()) {
                    int n2;
                    BlockState blockState;
                    mutableBlockPos.setWithOffset(blockPos, direction);
                    if (object.contains(mutableBlockPos) || set3.contains(mutableBlockPos) || !(blockState = levelAccessor.getBlockState(mutableBlockPos)).hasProperty(BlockStateProperties.DISTANCE) || (n2 = blockState.getValue(BlockStateProperties.DISTANCE).intValue()) <= i + 1) continue;
                    BlockState blockState2 = (BlockState)blockState.setValue(BlockStateProperties.DISTANCE, i + 1);
                    TreeFeature.setBlockKnownShape(levelAccessor, mutableBlockPos, blockState2);
                    if (boundingBox.isInside(mutableBlockPos)) {
                        ((DiscreteVoxelShape)bitSetDiscreteVoxelShape).setFull(mutableBlockPos.getX() - boundingBox.x0, mutableBlockPos.getY() - boundingBox.y0, mutableBlockPos.getZ() - boundingBox.z0, true, true);
                    }
                    set3.add(mutableBlockPos.immutable());
                }
            }
        }
        return bitSetDiscreteVoxelShape;
    }

    private static /* synthetic */ void lambda$place$7(WorldGenLevel worldGenLevel, Random random, List list, List list2, Set set, BoundingBox boundingBox, TreeDecorator treeDecorator) {
        treeDecorator.place(worldGenLevel, random, list, list2, set, boundingBox);
    }
}

