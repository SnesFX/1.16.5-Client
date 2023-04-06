/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens.advancements;

public enum AdvancementWidgetType {
    OBTAINED(0),
    UNOBTAINED(1);
    
    private final int y;

    private AdvancementWidgetType(int n2) {
        this.y = n2;
    }

    public int getIndex() {
        return this.y;
    }
}

