/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceLocation;

public class ServerboundCustomPayloadPacket
implements Packet<ServerGamePacketListener> {
    public static final ResourceLocation BRAND = new ResourceLocation("brand");
    private ResourceLocation identifier;
    private FriendlyByteBuf data;

    public ServerboundCustomPayloadPacket() {
    }

    public ServerboundCustomPayloadPacket(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
        this.identifier = resourceLocation;
        this.data = friendlyByteBuf;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.identifier = friendlyByteBuf.readResourceLocation();
        int n = friendlyByteBuf.readableBytes();
        if (n < 0 || n > 32767) {
            throw new IOException("Payload may not be larger than 32767 bytes");
        }
        this.data = new FriendlyByteBuf(friendlyByteBuf.readBytes(n));
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeResourceLocation(this.identifier);
        friendlyByteBuf.writeBytes(this.data);
    }

    @Override
    public void handle(ServerGamePacketListener serverGamePacketListener) {
        serverGamePacketListener.handleCustomPayload(this);
        if (this.data != null) {
            this.data.release();
        }
    }
}

