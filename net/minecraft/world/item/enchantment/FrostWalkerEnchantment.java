/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.enchantment;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class FrostWalkerEnchantment
extends Enchantment {
    public FrostWalkerEnchantment(Enchantment.Rarity rarity, EquipmentSlot ... arrequipmentSlot) {
        super(rarity, EnchantmentCategory.ARMOR_FEET, arrequipmentSlot);
    }

    @Override
    public int getMinCost(int n) {
        return n * 10;
    }

    @Override
    public int getMaxCost(int n) {
        return this.getMinCost(n) + 15;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    public static void onEntityMoved(LivingEntity livingEntity, Level level, BlockPos blockPos, int n) {
        if (!livingEntity.isOnGround()) {
            return;
        }
        BlockState blockState = Blocks.FROSTED_ICE.defaultBlockState();
        float f = Math.min(16, 2 + n);
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (BlockPos blockPos2 : BlockPos.betweenClosed(blockPos.offset(-f, -1.0, -f), blockPos.offset(f, -1.0, f))) {
            BlockState blockState2;
            if (!blockPos2.closerThan(livingEntity.position(), (double)f)) continue;
            mutableBlockPos.set(blockPos2.getX(), blockPos2.getY() + 1, blockPos2.getZ());
            BlockState blockState3 = level.getBlockState(mutableBlockPos);
            if (!blockState3.isAir() || (blockState2 = level.getBlockState(blockPos2)).getMaterial() != Material.WATER || blockState2.getValue(LiquidBlock.LEVEL) != 0 || !blockState.canSurvive(level, blockPos2) || !level.isUnobstructed(blockState, blockPos2, CollisionContext.empty())) continue;
            level.setBlockAndUpdate(blockPos2, blockState);
            level.getBlockTicks().scheduleTick(blockPos2, Blocks.FROSTED_ICE, Mth.nextInt(livingEntity.getRandom(), 60, 120));
        }
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment != Enchantments.DEPTH_STRIDER;
    }
}

