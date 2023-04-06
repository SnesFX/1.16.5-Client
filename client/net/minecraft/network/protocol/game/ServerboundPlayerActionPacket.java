/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundPlayerActionPacket
implements Packet<ServerGamePacketListener> {
    private BlockPos pos;
    private Direction direction;
    private Action action;

    public ServerboundPlayerActionPacket() {
    }

    public ServerboundPlayerActionPacket(Action action, BlockPos blockPos, Direction direction) {
        this.action = action;
        this.pos = blockPos.immutable();
        this.direction = direction;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.action = friendlyByteBuf.readEnum(Action.class);
        this.pos = friendlyByteBuf.readBlockPos();
        this.direction = Direction.from3DDataValue(friendlyByteBuf.readUnsignedByte());
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeEnum(this.action);
        friendlyByteBuf.writeBlockPos(this.pos);
        friendlyByteBuf.writeByte(this.direction.get3DDataValue());
    }

    @Override
    public void handle(ServerGamePacketListener serverGamePacketListener) {
        serverGamePacketListener.handlePlayerAction(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public Action getAction() {
        return this.action;
    }

    public static enum Action {
        START_DESTROY_BLOCK,
        ABORT_DESTROY_BLOCK,
        STOP_DESTROY_BLOCK,
        DROP_ALL_ITEMS,
        DROP_ITEM,
        RELEASE_USE_ITEM,
        SWAP_ITEM_WITH_OFFHAND;
        
    }

}

