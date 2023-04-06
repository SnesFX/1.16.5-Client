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
import net.minecraft.world.entity.player.Abilities;

public class ClientboundPlayerAbilitiesPacket
implements Packet<ClientGamePacketListener> {
    private boolean invulnerable;
    private boolean isFlying;
    private boolean canFly;
    private boolean instabuild;
    private float flyingSpeed;
    private float walkingSpeed;

    public ClientboundPlayerAbilitiesPacket() {
    }

    public ClientboundPlayerAbilitiesPacket(Abilities abilities) {
        this.invulnerable = abilities.invulnerable;
        this.isFlying = abilities.flying;
        this.canFly = abilities.mayfly;
        this.instabuild = abilities.instabuild;
        this.flyingSpeed = abilities.getFlyingSpeed();
        this.walkingSpeed = abilities.getWalkingSpeed();
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        byte by = friendlyByteBuf.readByte();
        this.invulnerable = (by & 1) != 0;
        this.isFlying = (by & 2) != 0;
        this.canFly = (by & 4) != 0;
        this.instabuild = (by & 8) != 0;
        this.flyingSpeed = friendlyByteBuf.readFloat();
        this.walkingSpeed = friendlyByteBuf.readFloat();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        byte by = 0;
        if (this.invulnerable) {
            by = (byte)(by | true ? 1 : 0);
        }
        if (this.isFlying) {
            by = (byte)(by | 2);
        }
        if (this.canFly) {
            by = (byte)(by | 4);
        }
        if (this.instabuild) {
            by = (byte)(by | 8);
        }
        friendlyByteBuf.writeByte(by);
        friendlyByteBuf.writeFloat(this.flyingSpeed);
        friendlyByteBuf.writeFloat(this.walkingSpeed);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handlePlayerAbilities(this);
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public boolean isFlying() {
        return this.isFlying;
    }

    public boolean canFly() {
        return this.canFly;
    }

    public boolean canInstabuild() {
        return this.instabuild;
    }

    public float getFlyingSpeed() {
        return this.flyingSpeed;
    }

    public float getWalkingSpeed() {
        return this.walkingSpeed;
    }
}

