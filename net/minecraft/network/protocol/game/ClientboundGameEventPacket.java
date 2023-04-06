/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundGameEventPacket
implements Packet<ClientGamePacketListener> {
    public static final Type NO_RESPAWN_BLOCK_AVAILABLE = new Type(0);
    public static final Type START_RAINING = new Type(1);
    public static final Type STOP_RAINING = new Type(2);
    public static final Type CHANGE_GAME_MODE = new Type(3);
    public static final Type WIN_GAME = new Type(4);
    public static final Type DEMO_EVENT = new Type(5);
    public static final Type ARROW_HIT_PLAYER = new Type(6);
    public static final Type RAIN_LEVEL_CHANGE = new Type(7);
    public static final Type THUNDER_LEVEL_CHANGE = new Type(8);
    public static final Type PUFFER_FISH_STING = new Type(9);
    public static final Type GUARDIAN_ELDER_EFFECT = new Type(10);
    public static final Type IMMEDIATE_RESPAWN = new Type(11);
    private Type event;
    private float param;

    public ClientboundGameEventPacket() {
    }

    public ClientboundGameEventPacket(Type type, float f) {
        this.event = type;
        this.param = f;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.event = (Type)Type.TYPES.get((int)friendlyByteBuf.readUnsignedByte());
        this.param = friendlyByteBuf.readFloat();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeByte(this.event.id);
        friendlyByteBuf.writeFloat(this.param);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleGameEvent(this);
    }

    public Type getEvent() {
        return this.event;
    }

    public float getParam() {
        return this.param;
    }

    public static class Type {
        private static final Int2ObjectMap<Type> TYPES = new Int2ObjectOpenHashMap();
        private final int id;

        public Type(int n) {
            this.id = n;
            TYPES.put(n, (Object)this);
        }
    }

}

