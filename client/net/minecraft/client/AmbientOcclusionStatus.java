/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import net.minecraft.util.Mth;

public enum AmbientOcclusionStatus {
    OFF(0, "options.ao.off"),
    MIN(1, "options.ao.min"),
    MAX(2, "options.ao.max");
    
    private static final AmbientOcclusionStatus[] BY_ID;
    private final int id;
    private final String key;

    private AmbientOcclusionStatus(int n2, String string2) {
        this.id = n2;
        this.key = string2;
    }

    public int getId() {
        return this.id;
    }

    public String getKey() {
        return this.key;
    }

    public static AmbientOcclusionStatus byId(int n) {
        return BY_ID[Mth.positiveModulo(n, BY_ID.length)];
    }

    static {
        BY_ID = (AmbientOcclusionStatus[])Arrays.stream(AmbientOcclusionStatus.values()).sorted(Comparator.comparingInt(AmbientOcclusionStatus::getId)).toArray(n -> new AmbientOcclusionStatus[n]);
    }
}

