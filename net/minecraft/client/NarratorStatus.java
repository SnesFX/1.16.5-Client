/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;

public enum NarratorStatus {
    OFF(0, "options.narrator.off"),
    ALL(1, "options.narrator.all"),
    CHAT(2, "options.narrator.chat"),
    SYSTEM(3, "options.narrator.system");
    
    private static final NarratorStatus[] BY_ID;
    private final int id;
    private final Component name;

    private NarratorStatus(int n2, String string2) {
        this.id = n2;
        this.name = new TranslatableComponent(string2);
    }

    public int getId() {
        return this.id;
    }

    public Component getName() {
        return this.name;
    }

    public static NarratorStatus byId(int n) {
        return BY_ID[Mth.positiveModulo(n, BY_ID.length)];
    }

    static {
        BY_ID = (NarratorStatus[])Arrays.stream(NarratorStatus.values()).sorted(Comparator.comparingInt(NarratorStatus::getId)).toArray(n -> new NarratorStatus[n]);
    }
}

