/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import net.minecraft.util.Mth;

public enum AttackIndicatorStatus {
    OFF(0, "options.off"),
    CROSSHAIR(1, "options.attack.crosshair"),
    HOTBAR(2, "options.attack.hotbar");
    
    private static final AttackIndicatorStatus[] BY_ID;
    private final int id;
    private final String key;

    private AttackIndicatorStatus(int n2, String string2) {
        this.id = n2;
        this.key = string2;
    }

    public int getId() {
        return this.id;
    }

    public String getKey() {
        return this.key;
    }

    public static AttackIndicatorStatus byId(int n) {
        return BY_ID[Mth.positiveModulo(n, BY_ID.length)];
    }

    static {
        BY_ID = (AttackIndicatorStatus[])Arrays.stream(AttackIndicatorStatus.values()).sorted(Comparator.comparingInt(AttackIndicatorStatus::getId)).toArray(n -> new AttackIndicatorStatus[n]);
    }
}

