/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.world.level.block.state.BlockState;

public class NetherVines {
    public static boolean isValidGrowthState(BlockState blockState) {
        return blockState.isAir();
    }

    public static int getBlocksToGrowWhenBonemealed(Random random) {
        double d = 1.0;
        int n = 0;
        while (random.nextDouble() < d) {
            d *= 0.826;
            ++n;
        }
        return n;
    }
}

