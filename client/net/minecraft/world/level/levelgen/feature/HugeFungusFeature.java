/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.level.levelgen.feature.WeepingVinesFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.material.Material;

public class HugeFungusFeature
extends Feature<HugeFungusConfiguration> {
    public HugeFungusFeature(Codec<HugeFungusConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, HugeFungusConfiguration hugeFungusConfiguration) {
        int n;
        Block block = hugeFungusConfiguration.validBaseState.getBlock();
        BlockPos blockPos2 = null;
        Block block2 = worldGenLevel.getBlockState(blockPos.below()).getBlock();
        if (block2 == block) {
            blockPos2 = blockPos;
        }
        if (blockPos2 == null) {
            return false;
        }
        int n2 = Mth.nextInt(random, 4, 13);
        if (random.nextInt(12) == 0) {
            n2 *= 2;
        }
        if (!hugeFungusConfiguration.planted) {
            n = chunkGenerator.getGenDepth();
            if (blockPos2.getY() + n2 + 1 >= n) {
                return false;
            }
        }
        n = !hugeFungusConfiguration.planted && random.nextFloat() < 0.06f;
        worldGenLevel.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 4);
        this.placeStem(worldGenLevel, random, hugeFungusConfiguration, blockPos2, n2, n != 0);
        this.placeHat(worldGenLevel, random, hugeFungusConfiguration, blockPos2, n2, n != 0);
        return true;
    }

    private static boolean isReplaceable(LevelAccessor levelAccessor, BlockPos blockPos, boolean bl) {
        return levelAccessor.isStateAtPosition(blockPos, blockState -> {
            Material material = blockState.getMaterial();
            return blockState.getMaterial().isReplaceable() || bl && material == Material.PLANT;
        });
    }

    private void placeStem(LevelAccessor levelAccessor, Random random, HugeFungusConfiguration hugeFungusConfiguration, BlockPos blockPos, int n, boolean bl) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        BlockState blockState = hugeFungusConfiguration.stemState;
        int n2 = bl ? 1 : 0;
        for (int i = -n2; i <= n2; ++i) {
            for (int j = -n2; j <= n2; ++j) {
                boolean bl2 = bl && Mth.abs(i) == n2 && Mth.abs(j) == n2;
                for (int k = 0; k < n; ++k) {
                    mutableBlockPos.setWithOffset(blockPos, i, k, j);
                    if (!HugeFungusFeature.isReplaceable(levelAccessor, mutableBlockPos, true)) continue;
                    if (hugeFungusConfiguration.planted) {
                        if (!levelAccessor.getBlockState((BlockPos)mutableBlockPos.below()).isAir()) {
                            levelAccessor.destroyBlock(mutableBlockPos, true);
                        }
                        levelAccessor.setBlock(mutableBlockPos, blockState, 3);
                        continue;
                    }
                    if (bl2) {
                        if (!(random.nextFloat() < 0.1f)) continue;
                        this.setBlock(levelAccessor, mutableBlockPos, blockState);
                        continue;
                    }
                    this.setBlock(levelAccessor, mutableBlockPos, blockState);
                }
            }
        }
    }

    private void placeHat(LevelAccessor levelAccessor, Random random, HugeFungusConfiguration hugeFungusConfiguration, BlockPos blockPos, int n, boolean bl) {
        int n2;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        boolean bl2 = hugeFungusConfiguration.hatState.is(Blocks.NETHER_WART_BLOCK);
        int n3 = Math.min(random.nextInt(1 + n / 3) + 5, n);
        for (int i = n2 = n - n3; i <= n; ++i) {
            int n4;
            int n5 = n4 = i < n - random.nextInt(3) ? 2 : 1;
            if (n3 > 8 && i < n2 + 4) {
                n4 = 3;
            }
            if (bl) {
                ++n4;
            }
            for (int j = -n4; j <= n4; ++j) {
                for (int k = -n4; k <= n4; ++k) {
                    boolean bl3 = j == -n4 || j == n4;
                    boolean bl4 = k == -n4 || k == n4;
                    boolean bl5 = !bl3 && !bl4 && i != n;
                    boolean bl6 = bl3 && bl4;
                    boolean bl7 = i < n2 + 3;
                    mutableBlockPos.setWithOffset(blockPos, j, i, k);
                    if (!HugeFungusFeature.isReplaceable(levelAccessor, mutableBlockPos, false)) continue;
                    if (hugeFungusConfiguration.planted && !levelAccessor.getBlockState((BlockPos)mutableBlockPos.below()).isAir()) {
                        levelAccessor.destroyBlock(mutableBlockPos, true);
                    }
                    if (bl7) {
                        if (bl5) continue;
                        this.placeHatDropBlock(levelAccessor, random, mutableBlockPos, hugeFungusConfiguration.hatState, bl2);
                        continue;
                    }
                    if (bl5) {
                        this.placeHatBlock(levelAccessor, random, hugeFungusConfiguration, mutableBlockPos, 0.1f, 0.2f, bl2 ? 0.1f : 0.0f);
                        continue;
                    }
                    if (bl6) {
                        this.placeHatBlock(levelAccessor, random, hugeFungusConfiguration, mutableBlockPos, 0.01f, 0.7f, bl2 ? 0.083f : 0.0f);
                        continue;
                    }
                    this.placeHatBlock(levelAccessor, random, hugeFungusConfiguration, mutableBlockPos, 5.0E-4f, 0.98f, bl2 ? 0.07f : 0.0f);
                }
            }
        }
    }

    private void placeHatBlock(LevelAccessor levelAccessor, Random random, HugeFungusConfiguration hugeFungusConfiguration, BlockPos.MutableBlockPos mutableBlockPos, float f, float f2, float f3) {
        if (random.nextFloat() < f) {
            this.setBlock(levelAccessor, mutableBlockPos, hugeFungusConfiguration.decorState);
        } else if (random.nextFloat() < f2) {
            this.setBlock(levelAccessor, mutableBlockPos, hugeFungusConfiguration.hatState);
            if (random.nextFloat() < f3) {
                HugeFungusFeature.tryPlaceWeepingVines(mutableBlockPos, levelAccessor, random);
            }
        }
    }

    private void placeHatDropBlock(LevelAccessor levelAccessor, Random random, BlockPos blockPos, BlockState blockState, boolean bl) {
        if (levelAccessor.getBlockState(blockPos.below()).is(blockState.getBlock())) {
            this.setBlock(levelAccessor, blockPos, blockState);
        } else if ((double)random.nextFloat() < 0.15) {
            this.setBlock(levelAccessor, blockPos, blockState);
            if (bl && random.nextInt(11) == 0) {
                HugeFungusFeature.tryPlaceWeepingVines(blockPos, levelAccessor, random);
            }
        }
    }

    private static void tryPlaceWeepingVines(BlockPos blockPos, LevelAccessor levelAccessor, Random random) {
        BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable().move(Direction.DOWN);
        if (!levelAccessor.isEmptyBlock(mutableBlockPos)) {
            return;
        }
        int n = Mth.nextInt(random, 1, 5);
        if (random.nextInt(7) == 0) {
            n *= 2;
        }
        int n2 = 23;
        int n3 = 25;
        WeepingVinesFeature.placeWeepingVinesColumn(levelAccessor, random, mutableBlockPos, n, 23, 25);
    }
}

