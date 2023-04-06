/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class FireAspectEnchantment
extends Enchantment {
    protected FireAspectEnchantment(Enchantment.Rarity rarity, EquipmentSlot ... arrequipmentSlot) {
        super(rarity, EnchantmentCategory.WEAPON, arrequipmentSlot);
    }

    @Override
    public int getMinCost(int n) {
        return 10 + 20 * (n - 1);
    }

    @Override
    public int getMaxCost(int n) {
        return super.getMinCost(n) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
}

