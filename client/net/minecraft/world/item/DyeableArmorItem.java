/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;

public class DyeableArmorItem
extends ArmorItem
implements DyeableLeatherItem {
    public DyeableArmorItem(ArmorMaterial armorMaterial, EquipmentSlot equipmentSlot, Item.Properties properties) {
        super(armorMaterial, equipmentSlot, properties);
    }
}

