/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.CoralFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CoralTreeFeature
extends CoralFeature {
    public CoralTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean placeFeature(LevelAccessor levelAccessor, Random random, BlockPos blockPos, BlockState blockState) {
        BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();
        int n = random.nextInt(3) + 1;
        for (int i = 0; i < n; ++i) {
            if (!this.placeCoralBlock(levelAccessor, random, mutableBlockPos, blockState)) {
                return true;
            }
            mutableBlockPos.move(Direction.UP);
        }
        BlockPos blockPos2 = mutableBlockPos.immutable();
        int n2 = random.nextInt(3) + 2;
        ArrayList arrayList = Lists.newArrayList((Iterable)Direction.Plane.HORIZONTAL);
        Collections.shuffle(arrayList, random);
        List list = arrayList.subList(0, n2);
        for (Direction direction : list) {
            mutableBlockPos.set(blockPos2);
            mutableBlockPos.move(direction);
            int n3 = random.nextInt(5) + 2;
            int n4 = 0;
            for (int i = 0; i < n3 && this.placeCoralBlock(levelAccessor, random, mutableBlockPos, blockState); ++i) {
                mutableBlockPos.move(Direction.UP);
                if (i != 0 && (++n4 < 2 || !(random.nextFloat() < 0.25f))) continue;
                mutableBlockPos.move(direction);
                n4 = 0;
            }
        }
        return true;
    }
}

