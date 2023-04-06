/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level;

public enum TickPriority {
    EXTREMELY_HIGH(-3),
    VERY_HIGH(-2),
    HIGH(-1),
    NORMAL(0),
    LOW(1),
    VERY_LOW(2),
    EXTREMELY_LOW(3);
    
    private final int value;

    private TickPriority(int n2) {
        this.value = n2;
    }

    public static TickPriority byValue(int n) {
        for (TickPriority tickPriority : TickPriority.values()) {
            if (tickPriority.value != n) continue;
            return tickPriority;
        }
        if (n < TickPriority.EXTREMELY_HIGH.value) {
            return EXTREMELY_HIGH;
        }
        return EXTREMELY_LOW;
    }

    public int getValue() {
        return this.value;
    }
}

