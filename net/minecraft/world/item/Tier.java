/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item;

import net.minecraft.world.item.crafting.Ingredient;

public interface Tier {
    public int getUses();

    public float getSpeed();

    public float getAttackDamageBonus();

    public int getLevel();

    public int getEnchantmentValue();

    public Ingredient getRepairIngredient();
}

