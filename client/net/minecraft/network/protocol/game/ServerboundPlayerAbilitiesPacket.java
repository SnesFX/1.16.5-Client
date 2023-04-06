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
import net.minecraft.world.entity.player.Abilities;

public class ServerboundPlayerAbilitiesPacket
implements Packet<ServerGamePacketListener> {
    private boolean isFlying;

    public ServerboundPlayerAbilitiesPacket() {
    }

    public ServerboundPlayerAbilitiesPacket(Abilities abilities) {
        this.isFlying = abilities.flying;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        byte by = friendlyByteBuf.readByte();
        this.isFlying = (by & 2) != 0;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        int n = 0;
        if (this.isFlying) {
            n = (byte)(n | 2);
        }
        friendlyByteBuf.writeByte(n);
    }

    @Override
    public void handle(ServerGamePacketListener serverGamePacketListener) {
        serverGamePacketListener.handlePlayerAbilities(this);
    }

    public boolean isFlying() {
        return this.isFlying;
    }
}

