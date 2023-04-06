/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.player;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum PlayerModelPart {
    CAPE(0, "cape"),
    JACKET(1, "jacket"),
    LEFT_SLEEVE(2, "left_sleeve"),
    RIGHT_SLEEVE(3, "right_sleeve"),
    LEFT_PANTS_LEG(4, "left_pants_leg"),
    RIGHT_PANTS_LEG(5, "right_pants_leg"),
    HAT(6, "hat");
    
    private final int bit;
    private final int mask;
    private final String id;
    private final Component name;

    private PlayerModelPart(int n2, String string2) {
        this.bit = n2;
        this.mask = 1 << n2;
        this.id = string2;
        this.name = new TranslatableComponent("options.modelPart." + string2);
    }

    public int getMask() {
        return this.mask;
    }

    public String getId() {
        return this.id;
    }

    public Component getName() {
        return this.name;
    }
}

