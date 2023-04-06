/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.inventory.MenuType;

public class ClientboundOpenScreenPacket
implements Packet<ClientGamePacketListener> {
    private int containerId;
    private int type;
    private Component title;

    public ClientboundOpenScreenPacket() {
    }

    public ClientboundOpenScreenPacket(int n, MenuType<?> menuType, Component component) {
        this.containerId = n;
        this.type = Registry.MENU.getId(menuType);
        this.title = component;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.containerId = friendlyByteBuf.readVarInt();
        this.type = friendlyByteBuf.readVarInt();
        this.title = friendlyByteBuf.readComponent();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.containerId);
        friendlyByteBuf.writeVarInt(this.type);
        friendlyByteBuf.writeComponent(this.title);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleOpenScreen(this);
    }

    public int getContainerId() {
        return this.containerId;
    }

    @Nullable
    public MenuType<?> getType() {
        return (MenuType)Registry.MENU.byId(this.type);
    }

    public Component getTitle() {
        return this.title;
    }
}

