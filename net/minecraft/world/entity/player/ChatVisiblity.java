/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.player;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import net.minecraft.util.Mth;

public enum ChatVisiblity {
    FULL(0, "options.chat.visibility.full"),
    SYSTEM(1, "options.chat.visibility.system"),
    HIDDEN(2, "options.chat.visibility.hidden");
    
    private static final ChatVisiblity[] BY_ID;
    private final int id;
    private final String key;

    private ChatVisiblity(int n2, String string2) {
        this.id = n2;
        this.key = string2;
    }

    public int getId() {
        return this.id;
    }

    public String getKey() {
        return this.key;
    }

    public static ChatVisiblity byId(int n) {
        return BY_ID[Mth.positiveModulo(n, BY_ID.length)];
    }

    static {
        BY_ID = (ChatVisiblity[])Arrays.stream(ChatVisiblity.values()).sorted(Comparator.comparingInt(ChatVisiblity::getId)).toArray(n -> new ChatVisiblity[n]);
    }
}

