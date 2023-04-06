/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.enchantment;

import java.util.Random;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class DigDurabilityEnchantment
extends Enchantment {
    protected DigDurabilityEnchantment(Enchantment.Rarity rarity, EquipmentSlot ... arrequipmentSlot) {
        super(rarity, EnchantmentCategory.BREAKABLE, arrequipmentSlot);
    }

    @Override
    public int getMinCost(int n) {
        return 5 + (n - 1) * 8;
    }

    @Override
    public int getMaxCost(int n) {
        return super.getMinCost(n) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canEnchant(ItemStack itemStack) {
        if (itemStack.isDamageableItem()) {
            return true;
        }
        return super.canEnchant(itemStack);
    }

    public static boolean shouldIgnoreDurabilityDrop(ItemStack itemStack, int n, Random random) {
        if (itemStack.getItem() instanceof ArmorItem && random.nextFloat() < 0.6f) {
            return false;
        }
        return random.nextInt(n + 1) > 0;
    }
}

