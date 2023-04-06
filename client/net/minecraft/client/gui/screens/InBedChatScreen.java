/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.entity.Entity;

public class InBedChatScreen
extends ChatScreen {
    public InBedChatScreen() {
        super("");
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new Button(this.width / 2 - 100, this.height - 40, 200, 20, new TranslatableComponent("multiplayer.stopSleeping"), button -> this.sendWakeUp()));
    }

    @Override
    public void onClose() {
        this.sendWakeUp();
    }

    @Override
    public boolean keyPressed(int n, int n2, int n3) {
        if (n == 256) {
            this.sendWakeUp();
        } else if (n == 257 || n == 335) {
            String string = this.input.getValue().trim();
            if (!string.isEmpty()) {
                this.sendMessage(string);
            }
            this.input.setValue("");
            this.minecraft.gui.getChat().resetChatScroll();
            return true;
        }
        return super.keyPressed(n, n2, n3);
    }

    private void sendWakeUp() {
        ClientPacketListener clientPacketListener = this.minecraft.player.connection;
        clientPacketListener.send(new ServerboundPlayerCommandPacket(this.minecraft.player, ServerboundPlayerCommandPacket.Action.STOP_SLEEPING));
    }
}

