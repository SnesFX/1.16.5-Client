/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum ChestType implements StringRepresentable
{
    SINGLE("single", 0),
    LEFT("left", 2),
    RIGHT("right", 1);
    
    public static final ChestType[] BY_ID;
    private final String name;
    private final int opposite;

    private ChestType(String string2, int n2) {
        this.name = string2;
        this.opposite = n2;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public ChestType getOpposite() {
        return BY_ID[this.opposite];
    }

    static {
        BY_ID = ChestType.values();
    }
}

