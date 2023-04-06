/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.animal.horse;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public enum Markings {
    NONE(0),
    WHITE(1),
    WHITE_FIELD(2),
    WHITE_DOTS(3),
    BLACK_DOTS(4);
    
    private static final Markings[] BY_ID;
    private final int id;

    private Markings(int n2) {
        this.id = n2;
    }

    public int getId() {
        return this.id;
    }

    public static Markings byId(int n) {
        return BY_ID[n % BY_ID.length];
    }

    static {
        BY_ID = (Markings[])Arrays.stream(Markings.values()).sorted(Comparator.comparingInt(Markings::getId)).toArray(n -> new Markings[n]);
    }
}

