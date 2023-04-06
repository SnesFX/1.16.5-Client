/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ArrowKnockbackEnchantment
extends Enchantment {
    public ArrowKnockbackEnchantment(Enchantment.Rarity rarity, EquipmentSlot ... arrequipmentSlot) {
        super(rarity, EnchantmentCategory.BOW, arrequipmentSlot);
    }

    @Override
    public int getMinCost(int n) {
        return 12 + (n - 1) * 20;
    }

    @Override
    public int getMaxCost(int n) {
        return this.getMinCost(n) + 25;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
}

