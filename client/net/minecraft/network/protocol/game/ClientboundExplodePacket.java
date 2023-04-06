/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ClientboundExplodePacket
implements Packet<ClientGamePacketListener> {
    private double x;
    private double y;
    private double z;
    private float power;
    private List<BlockPos> toBlow;
    private float knockbackX;
    private float knockbackY;
    private float knockbackZ;

    public ClientboundExplodePacket() {
    }

    public ClientboundExplodePacket(double d, double d2, double d3, float f, List<BlockPos> list, Vec3 vec3) {
        this.x = d;
        this.y = d2;
        this.z = d3;
        this.power = f;
        this.toBlow = Lists.newArrayList(list);
        if (vec3 != null) {
            this.knockbackX = (float)vec3.x;
            this.knockbackY = (float)vec3.y;
            this.knockbackZ = (float)vec3.z;
        }
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.x = friendlyByteBuf.readFloat();
        this.y = friendlyByteBuf.readFloat();
        this.z = friendlyByteBuf.readFloat();
        this.power = friendlyByteBuf.readFloat();
        int n = friendlyByteBuf.readInt();
        this.toBlow = Lists.newArrayListWithCapacity((int)n);
        int n2 = Mth.floor(this.x);
        int n3 = Mth.floor(this.y);
        int n4 = Mth.floor(this.z);
        for (int i = 0; i < n; ++i) {
            int n5 = friendlyByteBuf.readByte() + n2;
            int n6 = friendlyByteBuf.readByte() + n3;
            int n7 = friendlyByteBuf.readByte() + n4;
            this.toBlow.add(new BlockPos(n5, n6, n7));
        }
        this.knockbackX = friendlyByteBuf.readFloat();
        this.knockbackY = friendlyByteBuf.readFloat();
        this.knockbackZ = friendlyByteBuf.readFloat();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeFloat((float)this.x);
        friendlyByteBuf.writeFloat((float)this.y);
        friendlyByteBuf.writeFloat((float)this.z);
        friendlyByteBuf.writeFloat(this.power);
        friendlyByteBuf.writeInt(this.toBlow.size());
        int n = Mth.floor(this.x);
        int n2 = Mth.floor(this.y);
        int n3 = Mth.floor(this.z);
        for (BlockPos blockPos : this.toBlow) {
            int n4 = blockPos.getX() - n;
            int n5 = blockPos.getY() - n2;
            int n6 = blockPos.getZ() - n3;
            friendlyByteBuf.writeByte(n4);
            friendlyByteBuf.writeByte(n5);
            friendlyByteBuf.writeByte(n6);
        }
        friendlyByteBuf.writeFloat(this.knockbackX);
        friendlyByteBuf.writeFloat(this.knockbackY);
        friendlyByteBuf.writeFloat(this.knockbackZ);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleExplosion(this);
    }

    public float getKnockbackX() {
        return this.knockbackX;
    }

    public float getKnockbackY() {
        return this.knockbackY;
    }

    public float getKnockbackZ() {
        return this.knockbackZ;
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

    public float getPower() {
        return this.power;
    }

    public List<BlockPos> getToBlow() {
        return this.toBlow;
    }
}

