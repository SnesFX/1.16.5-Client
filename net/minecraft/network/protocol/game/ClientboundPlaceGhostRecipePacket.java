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
import net.minecraft.world.item.crafting.Recipe;

public class ClientboundPlaceGhostRecipePacket
implements Packet<ClientGamePacketListener> {
    private int containerId;
    private ResourceLocation recipe;

    public ClientboundPlaceGhostRecipePacket() {
    }

    public ClientboundPlaceGhostRecipePacket(int n, Recipe<?> recipe) {
        this.containerId = n;
        this.recipe = recipe.getId();
    }

    public ResourceLocation getRecipe() {
        return this.recipe;
    }

    public int getContainerId() {
        return this.containerId;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.containerId = friendlyByteBuf.readByte();
        this.recipe = friendlyByteBuf.readResourceLocation();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeByte(this.containerId);
        friendlyByteBuf.writeResourceLocation(this.recipe);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handlePlaceRecipe(this);
    }
}

