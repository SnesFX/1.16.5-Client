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
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.CoralFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CoralClawFeature
extends CoralFeature {
    public CoralClawFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean placeFeature(LevelAccessor levelAccessor, Random random, BlockPos blockPos, BlockState blockState) {
        if (!this.placeCoralBlock(levelAccessor, random, blockPos, blockState)) {
            return false;
        }
        Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        int n = random.nextInt(2) + 2;
        ArrayList arrayList = Lists.newArrayList((Object[])new Direction[]{direction, direction.getClockWise(), direction.getCounterClockWise()});
        Collections.shuffle(arrayList, random);
        List list = arrayList.subList(0, n);
        block0 : for (Direction direction2 : list) {
            int n2;
            int n3;
            Direction direction3;
            BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable();
            int n4 = random.nextInt(2) + 1;
            mutableBlockPos.move(direction2);
            if (direction2 == direction) {
                direction3 = direction;
                n2 = random.nextInt(3) + 2;
            } else {
                mutableBlockPos.move(Direction.UP);
                Direction[] arrdirection = new Direction[]{direction2, Direction.UP};
                direction3 = Util.getRandom(arrdirection, random);
                n2 = random.nextInt(3) + 3;
            }
            for (n3 = 0; n3 < n4 && this.placeCoralBlock(levelAccessor, random, mutableBlockPos, blockState); ++n3) {
                mutableBlockPos.move(direction3);
            }
            mutableBlockPos.move(direction3.getOpposite());
            mutableBlockPos.move(Direction.UP);
            for (n3 = 0; n3 < n2; ++n3) {
                mutableBlockPos.move(direction);
                if (!this.placeCoralBlock(levelAccessor, random, mutableBlockPos, blockState)) continue block0;
                if (!(random.nextFloat() < 0.25f)) continue;
                mutableBlockPos.move(Direction.UP);
            }
        }
        return true;
    }
}

