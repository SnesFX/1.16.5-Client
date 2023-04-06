/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  it.unimi.dsi.fastutil.shorts.ShortIterator
 *  it.unimi.dsi.fastutil.shorts.ShortSet
 */
package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.io.IOException;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;

public class ClientboundSectionBlocksUpdatePacket
implements Packet<ClientGamePacketListener> {
    private SectionPos sectionPos;
    private short[] positions;
    private BlockState[] states;
    private boolean suppressLightUpdates;

    public ClientboundSectionBlocksUpdatePacket() {
    }

    public ClientboundSectionBlocksUpdatePacket(SectionPos sectionPos, ShortSet shortSet, LevelChunkSection levelChunkSection, boolean bl) {
        this.sectionPos = sectionPos;
        this.suppressLightUpdates = bl;
        this.initFields(shortSet.size());
        int n = 0;
        ShortIterator shortIterator = shortSet.iterator();
        while (shortIterator.hasNext()) {
            short s;
            this.positions[n] = s = ((Short)shortIterator.next()).shortValue();
            this.states[n] = levelChunkSection.getBlockState(SectionPos.sectionRelativeX(s), SectionPos.sectionRelativeY(s), SectionPos.sectionRelativeZ(s));
            ++n;
        }
    }

    private void initFields(int n) {
        this.positions = new short[n];
        this.states = new BlockState[n];
    }

    @Override
    public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
        this.sectionPos = SectionPos.of(friendlyByteBuf.readLong());
        this.suppressLightUpdates = friendlyByteBuf.readBoolean();
        int n = friendlyByteBuf.readVarInt();
        this.initFields(n);
        for (int i = 0; i < this.positions.length; ++i) {
            long l = friendlyByteBuf.readVarLong();
            this.positions[i] = (short)(l & 0xFFFL);
            this.states[i] = Block.BLOCK_STATE_REGISTRY.byId((int)(l >>> 12));
        }
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
        friendlyByteBuf.writeLong(this.sectionPos.asLong());
        friendlyByteBuf.writeBoolean(this.suppressLightUpdates);
        friendlyByteBuf.writeVarInt(this.positions.length);
        for (int i = 0; i < this.positions.length; ++i) {
            friendlyByteBuf.writeVarLong(Block.getId(this.states[i]) << 12 | this.positions[i]);
        }
    }

    @Override
    public void handle(ClientGamePacketListener clientGamePacketListener) {
        clientGamePacketListener.handleChunkBlocksUpdate(this);
    }

    public void runUpdates(BiConsumer<BlockPos, BlockState> biConsumer) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < this.positions.length; ++i) {
            short s = this.positions[i];
            mutableBlockPos.set(this.sectionPos.relativeToBlockX(s), this.sectionPos.relativeToBlockY(s), this.sectionPos.relativeToBlockZ(s));
            biConsumer.accept(mutableBlockPos, this.states[i]);
        }
    }

    public boolean shouldSuppressLightUpdates() {
        return this.suppressLightUpdates;
    }
}

