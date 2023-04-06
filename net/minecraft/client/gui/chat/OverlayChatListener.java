/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.chat;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.chat.ChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;

public class OverlayChatListener
implements ChatListener {
    private final Minecraft minecraft;

    public OverlayChatListener(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public void handle(ChatType chatType, Component component, UUID uUID) {
        this.minecraft.gui.setOverlayMessage(component, false);
    }
}

