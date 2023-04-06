/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class OreBlock
extends Block {
    public OreBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected int xpOnDrop(Random random) {
        if (this == Blocks.COAL_ORE) {
            return Mth.nextInt(random, 0, 2);
        }
        if (this == Blocks.DIAMOND_ORE) {
            return Mth.nextInt(random, 3, 7);
        }
        if (this == Blocks.EMERALD_ORE) {
            return Mth.nextInt(random, 3, 7);
        }
        if (this == Blocks.LAPIS_ORE) {
            return Mth.nextInt(random, 2, 5);
        }
        if (this == Blocks.NETHER_QUARTZ_ORE) {
            return Mth.nextInt(random, 2, 5);
        }
        if (this == Blocks.NETHER_GOLD_ORE) {
            return Mth.nextInt(random, 0, 1);
        }
        return 0;
    }

    @Override
    public void spawnAfterBreak(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack) {
        int n;
        super.spawnAfterBreak(blockState, serverLevel, blockPos, itemStack);
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, itemStack) == 0 && (n = this.xpOnDrop(serverLevel.random)) > 0) {
            this.popExperience(serverLevel, blockPos, n);
        }
    }
}

