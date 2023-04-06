/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum Difficulty {
    PEACEFUL(0, "peaceful"),
    EASY(1, "easy"),
    NORMAL(2, "normal"),
    HARD(3, "hard");
    
    private static final Difficulty[] BY_ID;
    private final int id;
    private final String key;

    private Difficulty(int n2, String string2) {
        this.id = n2;
        this.key = string2;
    }

    public int getId() {
        return this.id;
    }

    public Component getDisplayName() {
        return new TranslatableComponent("options.difficulty." + this.key);
    }

    public static Difficulty byId(int n) {
        return BY_ID[n % BY_ID.length];
    }

    @Nullable
    public static Difficulty byName(String string) {
        for (Difficulty difficulty : Difficulty.values()) {
            if (!difficulty.key.equals(string)) continue;
            return difficulty;
        }
        return null;
    }

    public String getKey() {
        return this.key;
    }

    public Difficulty nextById() {
        return BY_ID[(this.id + 1) % BY_ID.length];
    }

    static {
        BY_ID = (Difficulty[])Arrays.stream(Difficulty.values()).sorted(Comparator.comparingInt(Difficulty::getId)).toArray(n -> new Difficulty[n]);
    }
}

