/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundPlayerPositionPacket
implements Packet<ClientGamePacketListener> {
    private double x;
    private double y;
    private double z;
    private float yRot;
    private float xRot;
    private Set<RelativeArgument> relativeArguments;
    private int id;

    public ClientboundPlayerPositionPacket() {
    }

    public ClientboundPlayerPositionPacket(double d, double d2, double d3, float f, float f2, Set<RelativeArgument> set, int n) {
        this.x = d;
        this.y = d2;
        this.z = d3;
        this.yRot = f;
        this.xRot = f2;
        this.relativeArguments = set;
        this.id = n;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.x = friendlyByteBuf.readDouble();
        this.y = friendlyByteBuf.readDouble();
        this.z = friendlyByteBuf.readDouble();
        this.yRot = friendlyByteBuf.readFloat();
        this.xRot = friendlyByteBuf.readFloat();
        this.relativeArguments = RelativeArgument.unpack(friendlyByteBuf.readUnsignedByte());
        this.id = friendlyByteBuf.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeDouble(this.x);
        friendlyByteBuf.writeDouble(this.y);
        friendlyByteBuf.writeDouble(this.z);
        friendlyByteBuf.writeFloat(this.yRot);
        friendlyByteBuf.writeFloat(this.xRot);
        friendlyByteBuf.writeByte(RelativeArgument.pack(this.relativeArguments));
        friendlyByteBuf.writeVarInt(this.id);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleMovePlayer(this);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYRot() {
        return this.yRot;
    }

    public float getXRot() {
        return this.xRot;
    }

    public int getId() {
        return this.id;
    }

    public Set<RelativeArgument> getRelativeArguments() {
        return this.relativeArguments;
    }

    public static enum RelativeArgument {
        X(0),
        Y(1),
        Z(2),
        Y_ROT(3),
        X_ROT(4);
        
        private final int bit;

        private RelativeArgument(int n2) {
            this.bit = n2;
        }

        private int getMask() {
            return 1 << this.bit;
        }

        private boolean isSet(int n) {
            return (n & this.getMask()) == this.getMask();
        }

        public static Set<RelativeArgument> unpack(int n) {
            EnumSet<RelativeArgument> enumSet = EnumSet.noneOf(RelativeArgument.class);
            for (RelativeArgument relativeArgument : RelativeArgument.values()) {
                if (!relativeArgument.isSet(n)) continue;
                enumSet.add(relativeArgument);
            }
            return enumSet;
        }

        public static int pack(Set<RelativeArgument> set) {
            int n = 0;
            for (RelativeArgument relativeArgument : set) {
                n |= relativeArgument.getMask();
            }
            return n;
        }
    }

}

