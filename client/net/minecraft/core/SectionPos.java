/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.core;

import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

public class SectionPos
extends Vec3i {
    private SectionPos(int n, int n2, int n3) {
        super(n, n2, n3);
    }

    public static SectionPos of(int n, int n2, int n3) {
        return new SectionPos(n, n2, n3);
    }

    public static SectionPos of(BlockPos blockPos) {
        return new SectionPos(SectionPos.blockToSectionCoord(blockPos.getX()), SectionPos.blockToSectionCoord(blockPos.getY()), SectionPos.blockToSectionCoord(blockPos.getZ()));
    }

    public static SectionPos of(ChunkPos chunkPos, int n) {
        return new SectionPos(chunkPos.x, n, chunkPos.z);
    }

    public static SectionPos of(Entity entity) {
        return new SectionPos(SectionPos.blockToSectionCoord(Mth.floor(entity.getX())), SectionPos.blockToSectionCoord(Mth.floor(entity.getY())), SectionPos.blockToSectionCoord(Mth.floor(entity.getZ())));
    }

    public static SectionPos of(long l) {
        return new SectionPos(SectionPos.x(l), SectionPos.y(l), SectionPos.z(l));
    }

    public static long offset(long l, Direction direction) {
        return SectionPos.offset(l, direction.getStepX(), direction.getStepY(), direction.getStepZ());
    }

    public static long offset(long l, int n, int n2, int n3) {
        return SectionPos.asLong(SectionPos.x(l) + n, SectionPos.y(l) + n2, SectionPos.z(l) + n3);
    }

    public static int blockToSectionCoord(int n) {
        return n >> 4;
    }

    public static int sectionRelative(int n) {
        return n & 0xF;
    }

    public static short sectionRelativePos(BlockPos blockPos) {
        int n = SectionPos.sectionRelative(blockPos.getX());
        int n2 = SectionPos.sectionRelative(blockPos.getY());
        int n3 = SectionPos.sectionRelative(blockPos.getZ());
        return (short)(n << 8 | n3 << 4 | n2 << 0);
    }

    public static int sectionRelativeX(short s) {
        return s >>> 8 & 0xF;
    }

    public static int sectionRelativeY(short s) {
        return s >>> 0 & 0xF;
    }

    public static int sectionRelativeZ(short s) {
        return s >>> 4 & 0xF;
    }

    public int relativeToBlockX(short s) {
        return this.minBlockX() + SectionPos.sectionRelativeX(s);
    }

    public int relativeToBlockY(short s) {
        return this.minBlockY() + SectionPos.sectionRelativeY(s);
    }

    public int relativeToBlockZ(short s) {
        return this.minBlockZ() + SectionPos.sectionRelativeZ(s);
    }

    public BlockPos relativeToBlockPos(short s) {
        return new BlockPos(this.relativeToBlockX(s), this.relativeToBlockY(s), this.relativeToBlockZ(s));
    }

    public static int sectionToBlockCoord(int n) {
        return n << 4;
    }

    public static int x(long l) {
        return (int)(l << 0 >> 42);
    }

    public static int y(long l) {
        return (int)(l << 44 >> 44);
    }

    public static int z(long l) {
        return (int)(l << 22 >> 42);
    }

    public int x() {
        return this.getX();
    }

    public int y() {
        return this.getY();
    }

    public int z() {
        return this.getZ();
    }

    public int minBlockX() {
        return this.x() << 4;
    }

    public int minBlockY() {
        return this.y() << 4;
    }

    public int minBlockZ() {
        return this.z() << 4;
    }

    public int maxBlockX() {
        return (this.x() << 4) + 15;
    }

    public int maxBlockY() {
        return (this.y() << 4) + 15;
    }

    public int maxBlockZ() {
        return (this.z() << 4) + 15;
    }

    public static long blockToSection(long l) {
        return SectionPos.asLong(SectionPos.blockToSectionCoord(BlockPos.getX(l)), SectionPos.blockToSectionCoord(BlockPos.getY(l)), SectionPos.blockToSectionCoord(BlockPos.getZ(l)));
    }

    public static long getZeroNode(long l) {
        return l & 0xFFFFFFFFFFF00000L;
    }

    public BlockPos origin() {
        return new BlockPos(SectionPos.sectionToBlockCoord(this.x()), SectionPos.sectionToBlockCoord(this.y()), SectionPos.sectionToBlockCoord(this.z()));
    }

    public BlockPos center() {
        int n = 8;
        return this.origin().offset(8, 8, 8);
    }

    public ChunkPos chunk() {
        return new ChunkPos(this.x(), this.z());
    }

    public static long asLong(int n, int n2, int n3) {
        long l = 0L;
        l |= ((long)n & 0x3FFFFFL) << 42;
        l |= ((long)n2 & 0xFFFFFL) << 0;
        return l |= ((long)n3 & 0x3FFFFFL) << 20;
    }

    public long asLong() {
        return SectionPos.asLong(this.x(), this.y(), this.z());
    }

    public Stream<BlockPos> blocksInside() {
        return BlockPos.betweenClosedStream(this.minBlockX(), this.minBlockY(), this.minBlockZ(), this.maxBlockX(), this.maxBlockY(), this.maxBlockZ());
    }

    public static Stream<SectionPos> cube(SectionPos sectionPos, int n) {
        int n2 = sectionPos.x();
        int n3 = sectionPos.y();
        int n4 = sectionPos.z();
        return SectionPos.betweenClosedStream(n2 - n, n3 - n, n4 - n, n2 + n, n3 + n, n4 + n);
    }

    public static Stream<SectionPos> aroundChunk(ChunkPos chunkPos, int n) {
        int n2 = chunkPos.x;
        int n3 = chunkPos.z;
        return SectionPos.betweenClosedStream(n2 - n, 0, n3 - n, n2 + n, 15, n3 + n);
    }

    public static Stream<SectionPos> betweenClosedStream(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<SectionPos>((long)((n4 - n + 1) * (n5 - n2 + 1) * (n6 - n3 + 1)), 64){
            final Cursor3D cursor;
            {
                super(l, n8);
                this.cursor = new Cursor3D(n, n2, n3, n4, n5, n6);
            }

            @Override
            public boolean tryAdvance(Consumer<? super SectionPos> consumer) {
                if (this.cursor.advance()) {
                    consumer.accept(new SectionPos(this.cursor.nextX(), this.cursor.nextY(), this.cursor.nextZ()));
                    return true;
                }
                return false;
            }
        }, false);
    }

}

