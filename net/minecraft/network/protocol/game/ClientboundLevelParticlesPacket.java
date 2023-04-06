/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundLevelParticlesPacket
implements Packet<ClientGamePacketListener> {
    private double x;
    private double y;
    private double z;
    private float xDist;
    private float yDist;
    private float zDist;
    private float maxSpeed;
    private int count;
    private boolean overrideLimiter;
    private ParticleOptions particle;

    public ClientboundLevelParticlesPacket() {
    }

    public <T extends ParticleOptions> ClientboundLevelParticlesPacket(T t, boolean bl, double d, double d2, double d3, float f, float f2, float f3, float f4, int n) {
        this.particle = t;
        this.overrideLimiter = bl;
        this.x = d;
        this.y = d2;
        this.z = d3;
        this.xDist = f;
        this.yDist = f2;
        this.zDist = f3;
        this.maxSpeed = f4;
        this.count = n;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        ParticleType particleType = (ParticleType)Registry.PARTICLE_TYPE.byId(friendlyByteBuf.readInt());
        if (particleType == null) {
            particleType = ParticleTypes.BARRIER;
        }
        this.overrideLimiter = friendlyByteBuf.readBoolean();
        this.x = friendlyByteBuf.readDouble();
        this.y = friendlyByteBuf.readDouble();
        this.z = friendlyByteBuf.readDouble();
        this.xDist = friendlyByteBuf.readFloat();
        this.yDist = friendlyByteBuf.readFloat();
        this.zDist = friendlyByteBuf.readFloat();
        this.maxSpeed = friendlyByteBuf.readFloat();
        this.count = friendlyByteBuf.readInt();
        this.particle = this.readParticle(friendlyByteBuf, particleType);
    }

    private <T extends ParticleOptions> T readParticle(FriendlyByteBuf friendlyByteBuf, ParticleType<T> particleType) {
        return particleType.getDeserializer().fromNetwork(particleType, friendlyByteBuf);
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeInt(Registry.PARTICLE_TYPE.getId(this.particle.getType()));
        friendlyByteBuf.writeBoolean(this.overrideLimiter);
        friendlyByteBuf.writeDouble(this.x);
        friendlyByteBuf.writeDouble(this.y);
        friendlyByteBuf.writeDouble(this.z);
        friendlyByteBuf.writeFloat(this.xDist);
        friendlyByteBuf.writeFloat(this.yDist);
        friendlyByteBuf.writeFloat(this.zDist);
        friendlyByteBuf.writeFloat(this.maxSpeed);
        friendlyByteBuf.writeInt(this.count);
        this.particle.writeToNetwork(friendlyByteBuf);
    }

    public boolean isOverrideLimiter() {
        return this.overrideLimiter;
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

    public float getXDist() {
        return this.xDist;
    }

    public float getYDist() {
        return this.yDist;
    }

    public float getZDist() {
        return this.zDist;
    }

    public float getMaxSpeed() {
        return this.maxSpeed;
    }

    public int getCount() {
        return this.count;
    }

    public ParticleOptions getParticle() {
        return this.particle;
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleParticleEvent(this);
    }
}

