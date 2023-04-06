/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class WaterWorkerEnchantment
extends Enchantment {
    public WaterWorkerEnchantment(Enchantment.Rarity rarity, EquipmentSlot ... arrequipmentSlot) {
        super(rarity, EnchantmentCategory.ARMOR_HEAD, arrequipmentSlot);
    }

    @Override
    public int getMinCost(int n) {
        return 1;
    }

    @Override
    public int getMaxCost(int n) {
        return this.getMinCost(n) + 40;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}

