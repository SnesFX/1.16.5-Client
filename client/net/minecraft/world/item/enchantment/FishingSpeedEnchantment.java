/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class FishingSpeedEnchantment
extends Enchantment {
    protected FishingSpeedEnchantment(Enchantment.Rarity rarity, EnchantmentCategory enchantmentCategory, EquipmentSlot ... arrequipmentSlot) {
        super(rarity, enchantmentCategory, arrequipmentSlot);
    }

    @Override
    public int getMinCost(int n) {
        return 15 + (n - 1) * 9;
    }

    @Override
    public int getMaxCost(int n) {
        return super.getMinCost(n) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}

