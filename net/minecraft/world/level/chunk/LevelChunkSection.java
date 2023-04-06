/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.chunk;

import io.netty.buffer.ByteBuf;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.IdMapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.GlobalPalette;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.material.FluidState;

public class LevelChunkSection {
    private static final Palette<BlockState> GLOBAL_BLOCKSTATE_PALETTE = new GlobalPalette<BlockState>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState());
    private final int bottomBlockY;
    private short nonEmptyBlockCount;
    private short tickingBlockCount;
    private short tickingFluidCount;
    private final PalettedContainer<BlockState> states;

    public LevelChunkSection(int n) {
        this(n, 0, 0, 0);
    }

    public LevelChunkSection(int n, short s, short s2, short s3) {
        this.bottomBlockY = n;
        this.nonEmptyBlockCount = s;
        this.tickingBlockCount = s2;
        this.tickingFluidCount = s3;
        this.states = new PalettedContainer<BlockState>(GLOBAL_BLOCKSTATE_PALETTE, Block.BLOCK_STATE_REGISTRY, NbtUtils::readBlockState, NbtUtils::writeBlockState, Blocks.AIR.defaultBlockState());
    }

    public BlockState getBlockState(int n, int n2, int n3) {
        return this.states.get(n, n2, n3);
    }

    public FluidState getFluidState(int n, int n2, int n3) {
        return this.states.get(n, n2, n3).getFluidState();
    }

    public void acquire() {
        this.states.acquire();
    }

    public void release() {
        this.states.release();
    }

    public BlockState setBlockState(int n, int n2, int n3, BlockState blockState) {
        return this.setBlockState(n, n2, n3, blockState, true);
    }

    public BlockState setBlockState(int n, int n2, int n3, BlockState blockState, boolean bl) {
        BlockState blockState2 = bl ? this.states.getAndSet(n, n2, n3, blockState) : this.states.getAndSetUnchecked(n, n2, n3, blockState);
        FluidState fluidState = blockState2.getFluidState();
        FluidState fluidState2 = blockState.getFluidState();
        if (!blockState2.isAir()) {
            this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount - 1);
            if (blockState2.isRandomlyTicking()) {
                this.tickingBlockCount = (short)(this.tickingBlockCount - 1);
            }
        }
        if (!fluidState.isEmpty()) {
            this.tickingFluidCount = (short)(this.tickingFluidCount - 1);
        }
        if (!blockState.isAir()) {
            this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount + 1);
            if (blockState.isRandomlyTicking()) {
                this.tickingBlockCount = (short)(this.tickingBlockCount + 1);
            }
        }
        if (!fluidState2.isEmpty()) {
            this.tickingFluidCount = (short)(this.tickingFluidCount + 1);
        }
        return blockState2;
    }

    public boolean isEmpty() {
        return this.nonEmptyBlockCount == 0;
    }

    public static boolean isEmpty(@Nullable LevelChunkSection levelChunkSection) {
        return levelChunkSection == LevelChunk.EMPTY_SECTION || levelChunkSection.isEmpty();
    }

    public boolean isRandomlyTicking() {
        return this.isRandomlyTickingBlocks() || this.isRandomlyTickingFluids();
    }

    public boolean isRandomlyTickingBlocks() {
        return this.tickingBlockCount > 0;
    }

    public boolean isRandomlyTickingFluids() {
        return this.tickingFluidCount > 0;
    }

    public int bottomBlockY() {
        return this.bottomBlockY;
    }

    public void recalcBlockCounts() {
        this.nonEmptyBlockCount = 0;
        this.tickingBlockCount = 0;
        this.tickingFluidCount = 0;
        this.states.count((blockState, n) -> {
            FluidState fluidState = blockState.getFluidState();
            if (!blockState.isAir()) {
                this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount + n);
                if (blockState.isRandomlyTicking()) {
                    this.tickingBlockCount = (short)(this.tickingBlockCount + n);
                }
            }
            if (!fluidState.isEmpty()) {
                this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount + n);
                if (fluidState.isRandomlyTicking()) {
                    this.tickingFluidCount = (short)(this.tickingFluidCount + n);
                }
            }
        });
    }

    public PalettedContainer<BlockState> getStates() {
        return this.states;
    }

    public void read(FriendlyByteBuf friendlyByteBuf) {
        this.nonEmptyBlockCount = friendlyByteBuf.readShort();
        this.states.read(friendlyByteBuf);
    }

    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeShort(this.nonEmptyBlockCount);
        this.states.write(friendlyByteBuf);
    }

    public int getSerializedSize() {
        return 2 + this.states.getSerializedSize();
    }

    public boolean maybeHas(Predicate<BlockState> predicate) {
        return this.states.maybeHas(predicate);
    }
}

