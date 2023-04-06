/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ClientboundBlockUpdatePacket
implements Packet<ClientGamePacketListener> {
    private BlockPos pos;
    private BlockState blockState;

    public ClientboundBlockUpdatePacket() {
    }

    public ClientboundBlockUpdatePacket(BlockPos blockPos, BlockState blockState) {
        this.pos = blockPos;
        this.blockState = blockState;
    }

    public ClientboundBlockUpdatePacket(BlockGetter blockGetter, BlockPos blockPos) {
        this(blockPos, blockGetter.getBlockState(blockPos));
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.pos = friendlyByteBuf.readBlockPos();
        this.blockState = Block.BLOCK_STATE_REGISTRY.byId(friendlyByteBuf.readVarInt());
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeBlockPos(this.pos);
        friendlyByteBuf.writeVarInt(Block.getId(this.blockState));
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleBlockUpdate(this);
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    public BlockPos getPos() {
        return this.pos;
    }
}

