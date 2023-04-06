/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import net.minecraft.util.Mth;

public enum ParticleStatus {
    ALL(0, "options.particles.all"),
    DECREASED(1, "options.particles.decreased"),
    MINIMAL(2, "options.particles.minimal");
    
    private static final ParticleStatus[] BY_ID;
    private final int id;
    private final String key;

    private ParticleStatus(int n2, String string2) {
        this.id = n2;
        this.key = string2;
    }

    public String getKey() {
        return this.key;
    }

    public int getId() {
        return this.id;
    }

    public static ParticleStatus byId(int n) {
        return BY_ID[Mth.positiveModulo(n, BY_ID.length)];
    }

    static {
        BY_ID = (ParticleStatus[])Arrays.stream(ParticleStatus.values()).sorted(Comparator.comparingInt(ParticleStatus::getId)).toArray(n -> new ParticleStatus[n]);
    }
}

