/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import net.minecraft.util.Mth;

public enum GraphicsStatus {
    FAST(0, "options.graphics.fast"),
    FANCY(1, "options.graphics.fancy"),
    FABULOUS(2, "options.graphics.fabulous");
    
    private static final GraphicsStatus[] BY_ID;
    private final int id;
    private final String key;

    private GraphicsStatus(int n2, String string2) {
        this.id = n2;
        this.key = string2;
    }

    public int getId() {
        return this.id;
    }

    public String getKey() {
        return this.key;
    }

    public GraphicsStatus cycleNext() {
        return GraphicsStatus.byId(this.getId() + 1);
    }

    public String toString() {
        switch (this) {
            case FAST: {
                return "fast";
            }
            case FANCY: {
                return "fancy";
            }
            case FABULOUS: {
                return "fabulous";
            }
        }
        throw new IllegalArgumentException();
    }

    public static GraphicsStatus byId(int n) {
        return BY_ID[Mth.positiveModulo(n, BY_ID.length)];
    }

    static {
        BY_ID = (GraphicsStatus[])Arrays.stream(GraphicsStatus.values()).sorted(Comparator.comparingInt(GraphicsStatus::getId)).toArray(n -> new GraphicsStatus[n]);
    }

}

