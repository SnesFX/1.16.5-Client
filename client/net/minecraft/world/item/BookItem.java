/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BookItem
extends Item {
    public BookItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnchantable(ItemStack itemStack) {
        return itemStack.getCount() == 1;
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }
}

