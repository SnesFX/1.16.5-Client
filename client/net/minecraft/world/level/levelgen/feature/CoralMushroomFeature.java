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
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.CoralFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CoralMushroomFeature
extends CoralFeature {
    public CoralMushroomFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean placeFeature(LevelAccessor levelAccessor, Random random, BlockPos blockPos, BlockState blockState) {
        int n = random.nextInt(3) + 3;
        int n2 = random.nextInt(3) + 3;
        int n3 = random.nextInt(3) + 3;
        int n4 = random.nextInt(3) + 1;
        BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();
        for (int i = 0; i <= n2; ++i) {
            for (int j = 0; j <= n; ++j) {
                for (int k = 0; k <= n3; ++k) {
                    mutableBlockPos.set(i + blockPos.getX(), j + blockPos.getY(), k + blockPos.getZ());
                    mutableBlockPos.move(Direction.DOWN, n4);
                    if ((i != 0 && i != n2 || j != 0 && j != n) && (k != 0 && k != n3 || j != 0 && j != n) && (i != 0 && i != n2 || k != 0 && k != n3) && (i == 0 || i == n2 || j == 0 || j == n || k == 0 || k == n3) && !(random.nextFloat() < 0.1f) && this.placeCoralBlock(levelAccessor, random, mutableBlockPos, blockState)) continue;
                }
            }
        }
        return true;
    }
}

