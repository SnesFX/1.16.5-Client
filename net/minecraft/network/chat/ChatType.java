/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.chat;

public enum ChatType {
    CHAT(0, false),
    SYSTEM(1, true),
    GAME_INFO(2, true);
    
    private final byte index;
    private final boolean interrupt;

    private ChatType(byte by, boolean bl) {
        this.index = by;
        this.interrupt = bl;
    }

    public byte getIndex() {
        return this.index;
    }

    public static ChatType getForIndex(byte by) {
        for (ChatType chatType : ChatType.values()) {
            if (by != chatType.index) continue;
            return chatType;
        }
        return CHAT;
    }

    public boolean shouldInterrupt() {
        return this.interrupt;
    }
}

