/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class DiggingEnchantment
extends Enchantment {
    protected DiggingEnchantment(Enchantment.Rarity rarity, EquipmentSlot ... arrequipmentSlot) {
        super(rarity, EnchantmentCategory.DIGGER, arrequipmentSlot);
    }

    @Override
    public int getMinCost(int n) {
        return 1 + 10 * (n - 1);
    }

    @Override
    public int getMaxCost(int n) {
        return super.getMinCost(n) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean canEnchant(ItemStack itemStack) {
        if (itemStack.getItem() == Items.SHEARS) {
            return true;
        }
        return super.canEnchant(itemStack);
    }
}

