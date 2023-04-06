/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class EnchantedGoldenAppleItem
extends Item {
    public EnchantedGoldenAppleItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }
}

