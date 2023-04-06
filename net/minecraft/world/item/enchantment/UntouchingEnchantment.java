/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

public class UntouchingEnchantment
extends Enchantment {
    protected UntouchingEnchantment(Enchantment.Rarity rarity, EquipmentSlot ... arrequipmentSlot) {
        super(rarity, EnchantmentCategory.DIGGER, arrequipmentSlot);
    }

    @Override
    public int getMinCost(int n) {
        return 15;
    }

    @Override
    public int getMaxCost(int n) {
        return super.getMinCost(n) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment != Enchantments.BLOCK_FORTUNE;
    }
}

