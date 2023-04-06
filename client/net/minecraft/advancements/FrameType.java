/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.advancements;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum FrameType {
    TASK("task", 0, ChatFormatting.GREEN),
    CHALLENGE("challenge", 26, ChatFormatting.DARK_PURPLE),
    GOAL("goal", 52, ChatFormatting.GREEN);
    
    private final String name;
    private final int texture;
    private final ChatFormatting chatColor;
    private final Component displayName;

    private FrameType(String string2, int n2, ChatFormatting chatFormatting) {
        this.name = string2;
        this.texture = n2;
        this.chatColor = chatFormatting;
        this.displayName = new TranslatableComponent("advancements.toast." + string2);
    }

    public String getName() {
        return this.name;
    }

    public int getTexture() {
        return this.texture;
    }

    public static FrameType byName(String string) {
        for (FrameType frameType : FrameType.values()) {
            if (!frameType.name.equals(string)) continue;
            return frameType;
        }
        throw new IllegalArgumentException("Unknown frame type '" + string + "'");
    }

    public ChatFormatting getChatColor() {
        return this.chatColor;
    }

    public Component getDisplayName() {
        return this.displayName;
    }
}

