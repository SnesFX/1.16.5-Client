/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.realmsclient;

import java.util.Arrays;

public class KeyCombo {
    private final char[] chars;
    private int matchIndex;
    private final Runnable onCompletion;

    public KeyCombo(char[] arrc, Runnable runnable) {
        this.onCompletion = runnable;
        if (arrc.length < 1) {
            throw new IllegalArgumentException("Must have at least one char");
        }
        this.chars = arrc;
    }

    public boolean keyPressed(char c) {
        if (c == this.chars[this.matchIndex++]) {
            if (this.matchIndex == this.chars.length) {
                this.reset();
                this.onCompletion.run();
                return true;
            }
        } else {
            this.reset();
        }
        return false;
    }

    public void reset() {
        this.matchIndex = 0;
    }

    public String toString() {
        return "KeyCombo{chars=" + Arrays.toString(this.chars) + ", matchIndex=" + this.matchIndex + '}';
    }
}

