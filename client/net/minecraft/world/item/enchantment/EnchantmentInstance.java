/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.util.WeighedRandom;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentInstance
extends WeighedRandom.WeighedRandomItem {
    public final Enchantment enchantment;
    public final int level;

    public EnchantmentInstance(Enchantment enchantment, int n) {
        super(enchantment.getRarity().getWeight());
        this.enchantment = enchantment;
        this.level = n;
    }
}

