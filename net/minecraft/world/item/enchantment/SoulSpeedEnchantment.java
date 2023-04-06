/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SoulSpeedEnchantment
extends Enchantment {
    public SoulSpeedEnchantment(Enchantment.Rarity rarity, EquipmentSlot ... arrequipmentSlot) {
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
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}

