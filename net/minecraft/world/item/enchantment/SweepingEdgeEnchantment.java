/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SweepingEdgeEnchantment
extends Enchantment {
    public SweepingEdgeEnchantment(Enchantment.Rarity rarity, EquipmentSlot ... arrequipmentSlot) {
        super(rarity, EnchantmentCategory.WEAPON, arrequipmentSlot);
    }

    @Override
    public int getMinCost(int n) {
        return 5 + (n - 1) * 9;
    }

    @Override
    public int getMaxCost(int n) {
        return this.getMinCost(n) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    public static float getSweepingDamageRatio(int n) {
        return 1.0f - 1.0f / (float)(n + 1);
    }
}

