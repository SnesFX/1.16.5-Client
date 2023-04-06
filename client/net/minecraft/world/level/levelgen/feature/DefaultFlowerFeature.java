/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractFlowerFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class DefaultFlowerFeature
extends AbstractFlowerFeature<RandomPatchConfiguration> {
    public DefaultFlowerFeature(Codec<RandomPatchConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean isValid(LevelAccessor levelAccessor, BlockPos blockPos, RandomPatchConfiguration randomPatchConfiguration) {
        return !randomPatchConfiguration.blacklist.contains(levelAccessor.getBlockState(blockPos));
    }

    @Override
    public int getCount(RandomPatchConfiguration randomPatchConfiguration) {
        return randomPatchConfiguration.tries;
    }

    @Override
    public BlockPos getPos(Random random, BlockPos blockPos, RandomPatchConfiguration randomPatchConfiguration) {
        return blockPos.offset(random.nextInt(randomPatchConfiguration.xspread) - random.nextInt(randomPatchConfiguration.xspread), random.nextInt(randomPatchConfiguration.yspread) - random.nextInt(randomPatchConfiguration.yspread), random.nextInt(randomPatchConfiguration.zspread) - random.nextInt(randomPatchConfiguration.zspread));
    }

    @Override
    public BlockState getRandomFlower(Random random, BlockPos blockPos, RandomPatchConfiguration randomPatchConfiguration) {
        return randomPatchConfiguration.stateProvider.getState(random, blockPos);
    }
}

