/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientboundBlockBreakAckPacket
implements Packet<ClientGamePacketListener> {
    private static final Logger LOGGER = LogManager.getLogger();
    private BlockPos pos;
    private BlockState state;
    ServerboundPlayerActionPacket.Action action;
    private boolean allGood;

    public ClientboundBlockBreakAckPacket() {
    }

    public ClientboundBlockBreakAckPacket(BlockPos blockPos, BlockState blockState, ServerboundPlayerActionPacket.Action action, boolean bl, String string) {
        this.pos = blockPos.immutable();
        this.state = blockState;
        this.action = action;
        this.allGood = bl;
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.pos = friendlyByteBuf.readBlockPos();
        this.state = Block.BLOCK_STATE_REGISTRY.byId(friendlyByteBuf.readVarInt());
        this.action = friendlyByteBuf.readEnum(ServerboundPlayerActionPacket.Action.class);
        this.allGood = friendlyByteBuf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeBlockPos(this.pos);
        friendlyByteBuf.writeVarInt(Block.getId(this.state));
        friendlyByteBuf.writeEnum(this.action);
        friendlyByteBuf.writeBoolean(this.allGood);
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleBlockBreakAck(this);
    }

    public BlockState getState() {
        return this.state;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public boolean allGood() {
        return this.allGood;
    }

    public ServerboundPlayerActionPacket.Action action() {
        return this.action;
    }
}

