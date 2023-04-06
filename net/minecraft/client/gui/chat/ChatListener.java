/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.chat;

import java.util.UUID;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;

public interface ChatListener {
    public void handle(ChatType var1, Component var2, UUID var3);
}

