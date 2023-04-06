/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class MendingEnchantment
extends Enchantment {
    public MendingEnchantment(Enchantment.Rarity rarity, EquipmentSlot ... arrequipmentSlot) {
        super(rarity, EnchantmentCategory.BREAKABLE, arrequipmentSlot);
    }

    @Override
    public int getMinCost(int n) {
        return n * 25;
    }

    @Override
    public int getMaxCost(int n) {
        return this.getMinCost(n) + 50;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}

