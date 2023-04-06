/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class TridentImpalerEnchantment
extends Enchantment {
    public TridentImpalerEnchantment(Enchantment.Rarity rarity, EquipmentSlot ... arrequipmentSlot) {
        super(rarity, EnchantmentCategory.TRIDENT, arrequipmentSlot);
    }

    @Override
    public int getMinCost(int n) {
        return 1 + (n - 1) * 8;
    }

    @Override
    public int getMaxCost(int n) {
        return this.getMinCost(n) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public float getDamageBonus(int n, MobType mobType) {
        if (mobType == MobType.WATER) {
            return (float)n * 2.5f;
        }
        return 0.0f;
    }
}

