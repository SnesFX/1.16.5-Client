/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundSetDefaultSpawnPositionPacket
implements Packet<ClientGamePacketListener> {
    private BlockPos pos;
    private float angle;

    public ClientboundSetDefaultSpawnPositionPacket() {
    }

    public ClientboundSetDefaultSpawnPositionPacket(BlockPos blockPos, float f) {
        this.pos = blockPos;
        this.angle = f;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.pos = friendlyByteBuf.readBlockPos();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeBlockPos(this.pos);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleSetSpawn(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public float getAngle() {
        return this.angle;
    }
}

