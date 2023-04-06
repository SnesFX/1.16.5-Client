/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.AbstractHugeMushroomFeature;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class HugeRedMushroomFeature
extends AbstractHugeMushroomFeature {
    public HugeRedMushroomFeature(Codec<HugeMushroomFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    protected void makeCap(LevelAccessor levelAccessor, Random random, BlockPos blockPos, int n, BlockPos.MutableBlockPos mutableBlockPos, HugeMushroomFeatureConfiguration hugeMushroomFeatureConfiguration) {
        for (int i = n - 3; i <= n; ++i) {
            int n2 = i < n ? hugeMushroomFeatureConfiguration.foliageRadius : hugeMushroomFeatureConfiguration.foliageRadius - 1;
            int n3 = hugeMushroomFeatureConfiguration.foliageRadius - 2;
            for (int j = -n2; j <= n2; ++j) {
                for (int k = -n2; k <= n2; ++k) {
                    boolean bl;
                    boolean bl2 = j == -n2;
                    boolean bl3 = j == n2;
                    boolean bl4 = k == -n2;
                    boolean bl5 = k == n2;
                    boolean bl6 = bl2 || bl3;
                    boolean bl7 = bl = bl4 || bl5;
                    if (i < n && bl6 == bl) continue;
                    mutableBlockPos.setWithOffset(blockPos, j, i, k);
                    if (levelAccessor.getBlockState(mutableBlockPos).isSolidRender(levelAccessor, mutableBlockPos)) continue;
                    this.setBlock(levelAccessor, mutableBlockPos, (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)hugeMushroomFeatureConfiguration.capProvider.getState(random, blockPos).setValue(HugeMushroomBlock.UP, i >= n - 1)).setValue(HugeMushroomBlock.WEST, j < -n3)).setValue(HugeMushroomBlock.EAST, j > n3)).setValue(HugeMushroomBlock.NORTH, k < -n3)).setValue(HugeMushroomBlock.SOUTH, k > n3));
                }
            }
        }
    }

    @Override
    protected int getTreeRadiusForHeight(int n, int n2, int n3, int n4) {
        int n5 = 0;
        if (n4 < n2 && n4 >= n2 - 3) {
            n5 = n3;
        } else if (n4 == n2) {
            n5 = n3;
        }
        return n5;
    }
}

