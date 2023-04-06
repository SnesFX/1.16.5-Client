/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class ClientboundMapItemDataPacket
implements Packet<ClientGamePacketListener> {
    private int mapId;
    private byte scale;
    private boolean trackingPosition;
    private boolean locked;
    private MapDecoration[] decorations;
    private int startX;
    private int startY;
    private int width;
    private int height;
    private byte[] mapColors;

    public ClientboundMapItemDataPacket() {
    }

    public ClientboundMapItemDataPacket(int n, byte by, boolean bl, boolean bl2, Collection<MapDecoration> collection, byte[] arrby, int n2, int n3, int n4, int n5) {
        this.mapId = n;
        this.scale = by;
        this.trackingPosition = bl;
        this.locked = bl2;
        this.decorations = collection.toArray(new MapDecoration[collection.size()]);
        this.startX = n2;
        this.startY = n3;
        this.width = n4;
        this.height = n5;
        this.mapColors = new byte[n4 * n5];
        for (int i = 0; i < n4; ++i) {
            for (int j = 0; j < n5; ++j) {
                this.mapColors[i + j * n4] = arrby[n2 + i + (n3 + j) * 128];
            }
        }
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.mapId = friendlyByteBuf.readVarInt();
        this.scale = friendlyByteBuf.readByte();
        this.trackingPosition = friendlyByteBuf.readBoolean();
        this.locked = friendlyByteBuf.readBoolean();
        this.decorations = new MapDecoration[friendlyByteBuf.readVarInt()];
        for (int i = 0; i < this.decorations.length; ++i) {
            MapDecoration.Type type = friendlyByteBuf.readEnum(MapDecoration.Type.class);
            this.decorations[i] = new MapDecoration(type, friendlyByteBuf.readByte(), friendlyByteBuf.readByte(), (byte)(friendlyByteBuf.readByte() & 0xF), friendlyByteBuf.readBoolean() ? friendlyByteBuf.readComponent() : null);
        }
        this.width = friendlyByteBuf.readUnsignedByte();
        if (this.width > 0) {
            this.height = friendlyByteBuf.readUnsignedByte();
            this.startX = friendlyByteBuf.readUnsignedByte();
            this.startY = friendlyByteBuf.readUnsignedByte();
            this.mapColors = friendlyByteBuf.readByteArray();
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeVarInt(this.mapId);
        friendlyByteBuf.writeByte(this.scale);
        friendlyByteBuf.writeBoolean(this.trackingPosition);
        friendlyByteBuf.writeBoolean(this.locked);
        friendlyByteBuf.writeVarInt(this.decorations.length);
        for (MapDecoration mapDecoration : this.decorations) {
            friendlyByteBuf.writeEnum(mapDecoration.getType());
            friendlyByteBuf.writeByte(mapDecoration.getX());
            friendlyByteBuf.writeByte(mapDecoration.getY());
            friendlyByteBuf.writeByte(mapDecoration.getRot() & 0xF);
            if (mapDecoration.getName() != null) {
                friendlyByteBuf.writeBoolean(true);
                friendlyByteBuf.writeComponent(mapDecoration.getName());
                continue;
            }
            friendlyByteBuf.writeBoolean(false);
        }
        friendlyByteBuf.writeByte(this.width);
        if (this.width > 0) {
            friendlyByteBuf.writeByte(this.height);
            friendlyByteBuf.writeByte(this.startX);
            friendlyByteBuf.writeByte(this.startY);
            friendlyByteBuf.writeByteArray(this.mapColors);
        }
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleMapItemData(this);
    }

    public int getMapId() {
        return this.mapId;
    }

    public void applyToMap(MapItemSavedData mapItemSavedData) {
        int n;
        mapItemSavedData.scale = this.scale;
        mapItemSavedData.trackingPosition = this.trackingPosition;
        mapItemSavedData.locked = this.locked;
        mapItemSavedData.decorations.clear();
        for (n = 0; n < this.decorations.length; ++n) {
            MapDecoration mapDecoration = this.decorations[n];
            mapItemSavedData.decorations.put("icon-" + n, mapDecoration);
        }
        for (n = 0; n < this.width; ++n) {
            for (int i = 0; i < this.height; ++i) {
                mapItemSavedData.colors[this.startX + n + (this.startY + i) * 128] = this.mapColors[n + i * this.width];
            }
        }
    }
}

