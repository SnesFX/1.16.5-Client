/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.MendingEnchantment;

public class ArrowInfiniteEnchantment
extends Enchantment {
    public ArrowInfiniteEnchantment(Enchantment.Rarity rarity, EquipmentSlot ... arrequipmentSlot) {
        super(rarity, EnchantmentCategory.BOW, arrequipmentSlot);
    }

    @Override
    public int getMinCost(int n) {
        return 20;
    }

    @Override
    public int getMaxCost(int n) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        if (enchantment instanceof MendingEnchantment) {
            return false;
        }
        return super.checkCompatibility(enchantment);
    }
}

