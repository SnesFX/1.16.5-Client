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
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

public class NoSurfaceOreFeature
extends Feature<OreConfiguration> {
    NoSurfaceOreFeature(Codec<OreConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, OreConfiguration oreConfiguration) {
        int n = random.nextInt(oreConfiguration.size + 1);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < n; ++i) {
            this.offsetTargetPos(mutableBlockPos, random, blockPos, Math.min(i, 7));
            if (!oreConfiguration.target.test(worldGenLevel.getBlockState(mutableBlockPos), random) || this.isFacingAir(worldGenLevel, mutableBlockPos)) continue;
            worldGenLevel.setBlock(mutableBlockPos, oreConfiguration.state, 2);
        }
        return true;
    }

    private void offsetTargetPos(BlockPos.MutableBlockPos mutableBlockPos, Random random, BlockPos blockPos, int n) {
        int n2 = this.getRandomPlacementInOneAxisRelativeToOrigin(random, n);
        int n3 = this.getRandomPlacementInOneAxisRelativeToOrigin(random, n);
        int n4 = this.getRandomPlacementInOneAxisRelativeToOrigin(random, n);
        mutableBlockPos.setWithOffset(blockPos, n2, n3, n4);
    }

    private int getRandomPlacementInOneAxisRelativeToOrigin(Random random, int n) {
        return Math.round((random.nextFloat() - random.nextFloat()) * (float)n);
    }

    private boolean isFacingAir(LevelAccessor levelAccessor, BlockPos blockPos) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.values()) {
            mutableBlockPos.setWithOffset(blockPos, direction);
            if (!levelAccessor.getBlockState(mutableBlockPos).isAir()) continue;
            return true;
        }
        return false;
    }
}

