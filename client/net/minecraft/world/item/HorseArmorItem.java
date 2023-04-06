/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class HorseArmorItem
extends Item {
    private final int protection;
    private final String texture;

    public HorseArmorItem(int n, String string, Item.Properties properties) {
        super(properties);
        this.protection = n;
        this.texture = "textures/entity/horse/armor/horse_armor_" + string + ".png";
    }

    public ResourceLocation getTexture() {
        return new ResourceLocation(this.texture);
    }

    public int getProtection() {
        return this.protection;
    }
}

