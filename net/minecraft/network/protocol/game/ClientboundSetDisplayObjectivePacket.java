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
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.scores.Objective;

public class ClientboundSetDisplayObjectivePacket
implements Packet<ClientGamePacketListener> {
    private int slot;
    private String objectiveName;

    public ClientboundSetDisplayObjectivePacket() {
    }

    public ClientboundSetDisplayObjectivePacket(int n, @Nullable Objective objective) {
        this.slot = n;
        this.objectiveName = objective == null ? "" : objective.getName();
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.slot = friendlyByteBuf.readByte();
        this.objectiveName = friendlyByteBuf.readUtf(16);
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeByte(this.slot);
        friendlyByteBuf.writeUtf(this.objectiveName);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleSetDisplayObjective(this);
    }

    public int getSlot() {
        return this.slot;
    }

    @Nullable
    public String getObjectiveName() {
        return Objects.equals(this.objectiveName, "") ? null : this.objectiveName;
    }
}

