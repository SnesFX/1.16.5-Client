/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundSetTitlesPacket
implements Packet<ClientGamePacketListener> {
    private Type type;
    private Component text;
    private int fadeInTime;
    private int stayTime;
    private int fadeOutTime;

    public ClientboundSetTitlesPacket() {
    }

    public ClientboundSetTitlesPacket(Type type, Component component) {
        this(type, component, -1, -1, -1);
    }

    public ClientboundSetTitlesPacket(int n, int n2, int n3) {
        this(Type.TIMES, null, n, n2, n3);
    }

    public ClientboundSetTitlesPacket(Type type, @Nullable Component component, int n, int n2, int n3) {
        this.type = type;
        this.text = component;
        this.fadeInTime = n;
        this.stayTime = n2;
        this.fadeOutTime = n3;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.type = friendlyByteBuf.readEnum(Type.class);
        if (this.type == Type.TITLE || this.type == Type.SUBTITLE || this.type == Type.ACTIONBAR) {
            this.text = friendlyByteBuf.readComponent();
        }
        if (this.type == Type.TIMES) {
            this.fadeInTime = friendlyByteBuf.readInt();
            this.stayTime = friendlyByteBuf.readInt();
            this.fadeOutTime = friendlyByteBuf.readInt();
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeEnum(this.type);
        if (this.type == Type.TITLE || this.type == Type.SUBTITLE || this.type == Type.ACTIONBAR) {
            friendlyByteBuf.writeComponent(this.text);
        }
        if (this.type == Type.TIMES) {
            friendlyByteBuf.writeInt(this.fadeInTime);
            friendlyByteBuf.writeInt(this.stayTime);
            friendlyByteBuf.writeInt(this.fadeOutTime);
        }
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleSetTitles(this);
    }

    public Type getType() {
        return this.type;
    }

    public Component getText() {
        return this.text;
    }

    public int getFadeInTime() {
        return this.fadeInTime;
    }

    public int getStayTime() {
        return this.stayTime;
    }

    public int getFadeOutTime() {
        return this.fadeOutTime;
    }

    public static enum Type {
        TITLE,
        SUBTITLE,
        ACTIONBAR,
        TIMES,
        CLEAR,
        RESET;
        
    }

}

