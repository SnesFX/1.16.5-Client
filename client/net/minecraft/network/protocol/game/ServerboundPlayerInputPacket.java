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

public class ServerboundPlayerInputPacket
implements Packet<ServerGamePacketListener> {
    private float xxa;
    private float zza;
    private boolean isJumping;
    private boolean isShiftKeyDown;

    public ServerboundPlayerInputPacket() {
    }

    public ServerboundPlayerInputPacket(float f, float f2, boolean bl, boolean bl2) {
        this.xxa = f;
        this.zza = f2;
        this.isJumping = bl;
        this.isShiftKeyDown = bl2;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.xxa = friendlyByteBuf.readFloat();
        this.zza = friendlyByteBuf.readFloat();
        byte by = friendlyByteBuf.readByte();
        this.isJumping = (by & 1) > 0;
        this.isShiftKeyDown = (by & 2) > 0;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeFloat(this.xxa);
        friendlyByteBuf.writeFloat(this.zza);
        byte by = 0;
        if (this.isJumping) {
            by = (byte)(by | true ? 1 : 0);
        }
        if (this.isShiftKeyDown) {
            by = (byte)(by | 2);
        }
        friendlyByteBuf.writeByte(by);
    }

    @Override
    public void handle(ServerGamePacketListener serverGamePacketListener) {
        serverGamePacketListener.handlePlayerInput(this);
    }

    public float getXxa() {
        return this.xxa;
    }

    public float getZza() {
        return this.zza;
    }

    public boolean isJumping() {
        return this.isJumping;
    }

    public boolean isShiftKeyDown() {
        return this.isShiftKeyDown;
    }
}

