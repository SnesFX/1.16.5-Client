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
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundSetHealthPacket
implements Packet<ClientGamePacketListener> {
    private float health;
    private int food;
    private float saturation;

    public ClientboundSetHealthPacket() {
    }

    public ClientboundSetHealthPacket(float f, int n, float f2) {
        this.health = f;
        this.food = n;
        this.saturation = f2;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.health = friendlyByteBuf.readFloat();
        this.food = friendlyByteBuf.readVarInt();
        this.saturation = friendlyByteBuf.readFloat();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeFloat(this.health);
        friendlyByteBuf.writeVarInt(this.food);
        friendlyByteBuf.writeFloat(this.saturation);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleSetHealth(this);
    }

    public float getHealth() {
        return this.health;
    }

    public int getFood() {
        return this.food;
    }

    public float getSaturation() {
        return this.saturation;
    }
}

