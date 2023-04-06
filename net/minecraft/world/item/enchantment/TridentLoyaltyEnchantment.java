/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class TridentLoyaltyEnchantment
extends Enchantment {
    public TridentLoyaltyEnchantment(Enchantment.Rarity rarity, EquipmentSlot ... arrequipmentSlot) {
        super(rarity, EnchantmentCategory.TRIDENT, arrequipmentSlot);
    }

    @Override
    public int getMinCost(int n) {
        return 5 + n * 7;
    }

    @Override
    public int getMaxCost(int n) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment);
    }
}

