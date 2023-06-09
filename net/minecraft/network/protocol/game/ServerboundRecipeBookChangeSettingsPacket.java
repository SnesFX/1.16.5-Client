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
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.inventory.RecipeBookType;

public class ServerboundRecipeBookChangeSettingsPacket
implements Packet<ServerGamePacketListener> {
    private RecipeBookType bookType;
    private boolean isOpen;
    private boolean isFiltering;

    public ServerboundRecipeBookChangeSettingsPacket() {
    }

    public ServerboundRecipeBookChangeSettingsPacket(RecipeBookType recipeBookType, boolean bl, boolean bl2) {
        this.bookType = recipeBookType;
        this.isOpen = bl;
        this.isFiltering = bl2;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.bookType = friendlyByteBuf.readEnum(RecipeBookType.class);
        this.isOpen = friendlyByteBuf.readBoolean();
        this.isFiltering = friendlyByteBuf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeEnum(this.bookType);
        friendlyByteBuf.writeBoolean(this.isOpen);
        friendlyByteBuf.writeBoolean(this.isFiltering);
    }

    @Override
    public void handle(ServerGamePacketListener serverGamePacketListener) {
        serverGamePacketListener.handleRecipeBookChangeSettingsPacket(this);
    }

    public RecipeBookType getBookType() {
        return this.bookType;
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public boolean isFiltering() {
        return this.isFiltering;
    }
}

