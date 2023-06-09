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
import net.minecraft.resources.ResourceLocation;

public class ClientboundCustomPayloadPacket
implements Packet<ClientGamePacketListener> {
    public static final ResourceLocation BRAND = new ResourceLocation("brand");
    public static final ResourceLocation DEBUG_PATHFINDING_PACKET = new ResourceLocation("debug/path");
    public static final ResourceLocation DEBUG_NEIGHBORSUPDATE_PACKET = new ResourceLocation("debug/neighbors_update");
    public static final ResourceLocation DEBUG_CAVES_PACKET = new ResourceLocation("debug/caves");
    public static final ResourceLocation DEBUG_STRUCTURES_PACKET = new ResourceLocation("debug/structures");
    public static final ResourceLocation DEBUG_WORLDGENATTEMPT_PACKET = new ResourceLocation("debug/worldgen_attempt");
    public static final ResourceLocation DEBUG_POI_TICKET_COUNT_PACKET = new ResourceLocation("debug/poi_ticket_count");
    public static final ResourceLocation DEBUG_POI_ADDED_PACKET = new ResourceLocation("debug/poi_added");
    public static final ResourceLocation DEBUG_POI_REMOVED_PACKET = new ResourceLocation("debug/poi_removed");
    public static final ResourceLocation DEBUG_VILLAGE_SECTIONS = new ResourceLocation("debug/village_sections");
    public static final ResourceLocation DEBUG_GOAL_SELECTOR = new ResourceLocation("debug/goal_selector");
    public static final ResourceLocation DEBUG_BRAIN = new ResourceLocation("debug/brain");
    public static final ResourceLocation DEBUG_BEE = new ResourceLocation("debug/bee");
    public static final ResourceLocation DEBUG_HIVE = new ResourceLocation("debug/hive");
    public static final ResourceLocation DEBUG_GAME_TEST_ADD_MARKER = new ResourceLocation("debug/game_test_add_marker");
    public static final ResourceLocation DEBUG_GAME_TEST_CLEAR = new ResourceLocation("debug/game_test_clear");
    public static final ResourceLocation DEBUG_RAIDS = new ResourceLocation("debug/raids");
    private ResourceLocation identifier;
    private FriendlyByteBuf data;

    public ClientboundCustomPayloadPacket() {
    }

    public ClientboundCustomPayloadPacket(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
        this.identifier = resourceLocation;
        this.data = friendlyByteBuf;
        if (friendlyByteBuf.writerIndex() > 1048576) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.identifier = friendlyByteBuf.readResourceLocation();
        int n = friendlyByteBuf.readableBytes();
        if (n < 0 || n > 1048576) {
            throw new IOException("Payload may not be larger than 1048576 bytes");
        }
        this.data = new FriendlyByteBuf(friendlyByteBuf.readBytes(n));
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeResourceLocation(this.identifier);
        friendlyByteBuf.writeBytes(this.data.copy());
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleCustomPayload(this);
    }

    public ResourceLocation getIdentifier() {
        return this.identifier;
    }

    public FriendlyByteBuf getData() {
        return new FriendlyByteBuf(this.data.copy());
    }
}

