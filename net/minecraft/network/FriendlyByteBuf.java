/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$PartialResult
 *  com.mojang.serialization.DynamicOps
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufAllocator
 *  io.netty.buffer.ByteBufInputStream
 *  io.netty.buffer.ByteBufOutputStream
 *  io.netty.handler.codec.DecoderException
 *  io.netty.handler.codec.EncoderException
 *  io.netty.util.ByteProcessor
 *  io.netty.util.ReferenceCounted
 *  javax.annotation.Nullable
 */
package net.minecraft.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import io.netty.util.ReferenceCounted;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FriendlyByteBuf
extends ByteBuf {
    private final ByteBuf source;

    public FriendlyByteBuf(ByteBuf byteBuf) {
        this.source = byteBuf;
    }

    public static int getVarIntSize(int n) {
        for (int i = 1; i < 5; ++i) {
            if ((n & -1 << i * 7) != 0) continue;
            return i;
        }
        return 5;
    }

    public <T> T readWithCodec(Codec<T> codec) throws IOException {
        CompoundTag compoundTag = this.readAnySizeNbt();
        DataResult dataResult = codec.parse((DynamicOps)NbtOps.INSTANCE, (Object)compoundTag);
        if (dataResult.error().isPresent()) {
            throw new IOException("Failed to decode: " + ((DataResult.PartialResult)dataResult.error().get()).message() + " " + compoundTag);
        }
        return dataResult.result().get();
    }

    public <T> void writeWithCodec(Codec<T> codec, T t) throws IOException {
        DataResult dataResult = codec.encodeStart((DynamicOps)NbtOps.INSTANCE, t);
        if (dataResult.error().isPresent()) {
            throw new IOException("Failed to encode: " + ((DataResult.PartialResult)dataResult.error().get()).message() + " " + t);
        }
        this.writeNbt((CompoundTag)dataResult.result().get());
    }

    public FriendlyByteBuf writeByteArray(byte[] arrby) {
        this.writeVarInt(arrby.length);
        this.writeBytes(arrby);
        return this;
    }

    public byte[] readByteArray() {
        return this.readByteArray(this.readableBytes());
    }

    public byte[] readByteArray(int n) {
        int n2 = this.readVarInt();
        if (n2 > n) {
            throw new DecoderException("ByteArray with size " + n2 + " is bigger than allowed " + n);
        }
        byte[] arrby = new byte[n2];
        this.readBytes(arrby);
        return arrby;
    }

    public FriendlyByteBuf writeVarIntArray(int[] arrn) {
        this.writeVarInt(arrn.length);
        for (int n : arrn) {
            this.writeVarInt(n);
        }
        return this;
    }

    public int[] readVarIntArray() {
        return this.readVarIntArray(this.readableBytes());
    }

    public int[] readVarIntArray(int n) {
        int n2 = this.readVarInt();
        if (n2 > n) {
            throw new DecoderException("VarIntArray with size " + n2 + " is bigger than allowed " + n);
        }
        int[] arrn = new int[n2];
        for (int i = 0; i < arrn.length; ++i) {
            arrn[i] = this.readVarInt();
        }
        return arrn;
    }

    public FriendlyByteBuf writeLongArray(long[] arrl) {
        this.writeVarInt(arrl.length);
        for (long l : arrl) {
            this.writeLong(l);
        }
        return this;
    }

    public long[] readLongArray(@Nullable long[] arrl) {
        return this.readLongArray(arrl, this.readableBytes() / 8);
    }

    public long[] readLongArray(@Nullable long[] arrl, int n) {
        int n2 = this.readVarInt();
        if (arrl == null || arrl.length != n2) {
            if (n2 > n) {
                throw new DecoderException("LongArray with size " + n2 + " is bigger than allowed " + n);
            }
            arrl = new long[n2];
        }
        for (int i = 0; i < arrl.length; ++i) {
            arrl[i] = this.readLong();
        }
        return arrl;
    }

    public BlockPos readBlockPos() {
        return BlockPos.of(this.readLong());
    }

    public FriendlyByteBuf writeBlockPos(BlockPos blockPos) {
        this.writeLong(blockPos.asLong());
        return this;
    }

    public SectionPos readSectionPos() {
        return SectionPos.of(this.readLong());
    }

    public Component readComponent() {
        return Component.Serializer.fromJson(this.readUtf(262144));
    }

    public FriendlyByteBuf writeComponent(Component component) {
        return this.writeUtf(Component.Serializer.toJson(component), 262144);
    }

    public <T extends Enum<T>> T readEnum(Class<T> class_) {
        return (T)((Enum[])class_.getEnumConstants())[this.readVarInt()];
    }

    public FriendlyByteBuf writeEnum(Enum<?> enum_) {
        return this.writeVarInt(enum_.ordinal());
    }

    public int readVarInt() {
        byte by;
        int n = 0;
        int n2 = 0;
        do {
            by = this.readByte();
            n |= (by & 0x7F) << n2++ * 7;
            if (n2 <= 5) continue;
            throw new RuntimeException("VarInt too big");
        } while ((by & 0x80) == 128);
        return n;
    }

    public long readVarLong() {
        byte by;
        long l = 0L;
        int n = 0;
        do {
            by = this.readByte();
            l |= (long)(by & 0x7F) << n++ * 7;
            if (n <= 10) continue;
            throw new RuntimeException("VarLong too big");
        } while ((by & 0x80) == 128);
        return l;
    }

    public FriendlyByteBuf writeUUID(UUID uUID) {
        this.writeLong(uUID.getMostSignificantBits());
        this.writeLong(uUID.getLeastSignificantBits());
        return this;
    }

    public UUID readUUID() {
        return new UUID(this.readLong(), this.readLong());
    }

    public FriendlyByteBuf writeVarInt(int n) {
        do {
            if ((n & 0xFFFFFF80) == 0) {
                this.writeByte(n);
                return this;
            }
            this.writeByte(n & 0x7F | 0x80);
            n >>>= 7;
        } while (true);
    }

    public FriendlyByteBuf writeVarLong(long l) {
        do {
            if ((l & 0xFFFFFFFFFFFFFF80L) == 0L) {
                this.writeByte((int)l);
                return this;
            }
            this.writeByte((int)(l & 0x7FL) | 0x80);
            l >>>= 7;
        } while (true);
    }

    public FriendlyByteBuf writeNbt(@Nullable CompoundTag compoundTag) {
        if (compoundTag == null) {
            this.writeByte(0);
        } else {
            try {
                NbtIo.write(compoundTag, (DataOutput)new ByteBufOutputStream((ByteBuf)this));
            }
            catch (IOException iOException) {
                throw new EncoderException((Throwable)iOException);
            }
        }
        return this;
    }

    @Nullable
    public CompoundTag readNbt() {
        return this.readNbt(new NbtAccounter(0x200000L));
    }

    @Nullable
    public CompoundTag readAnySizeNbt() {
        return this.readNbt(NbtAccounter.UNLIMITED);
    }

    @Nullable
    public CompoundTag readNbt(NbtAccounter nbtAccounter) {
        int n = this.readerIndex();
        byte by = this.readByte();
        if (by == 0) {
            return null;
        }
        this.readerIndex(n);
        try {
            return NbtIo.read((DataInput)new ByteBufInputStream((ByteBuf)this), nbtAccounter);
        }
        catch (IOException iOException) {
            throw new EncoderException((Throwable)iOException);
        }
    }

    public FriendlyByteBuf writeItem(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            this.writeBoolean(false);
        } else {
            this.writeBoolean(true);
            Item item = itemStack.getItem();
            this.writeVarInt(Item.getId(item));
            this.writeByte(itemStack.getCount());
            CompoundTag compoundTag = null;
            if (item.canBeDepleted() || item.shouldOverrideMultiplayerNbt()) {
                compoundTag = itemStack.getTag();
            }
            this.writeNbt(compoundTag);
        }
        return this;
    }

    public ItemStack readItem() {
        if (!this.readBoolean()) {
            return ItemStack.EMPTY;
        }
        int n = this.readVarInt();
        byte by = this.readByte();
        ItemStack itemStack = new ItemStack(Item.byId(n), by);
        itemStack.setTag(this.readNbt());
        return itemStack;
    }

    public String readUtf() {
        return this.readUtf(32767);
    }

    public String readUtf(int n) {
        int n2 = this.readVarInt();
        if (n2 > n * 4) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + n2 + " > " + n * 4 + ")");
        }
        if (n2 < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        }
        String string = this.toString(this.readerIndex(), n2, StandardCharsets.UTF_8);
        this.readerIndex(this.readerIndex() + n2);
        if (string.length() > n) {
            throw new DecoderException("The received string length is longer than maximum allowed (" + n2 + " > " + n + ")");
        }
        return string;
    }

    public FriendlyByteBuf writeUtf(String string) {
        return this.writeUtf(string, 32767);
    }

    public FriendlyByteBuf writeUtf(String string, int n) {
        byte[] arrby = string.getBytes(StandardCharsets.UTF_8);
        if (arrby.length > n) {
            throw new EncoderException("String too big (was " + arrby.length + " bytes encoded, max " + n + ")");
        }
        this.writeVarInt(arrby.length);
        this.writeBytes(arrby);
        return this;
    }

    public ResourceLocation readResourceLocation() {
        return new ResourceLocation(this.readUtf(32767));
    }

    public FriendlyByteBuf writeResourceLocation(ResourceLocation resourceLocation) {
        this.writeUtf(resourceLocation.toString());
        return this;
    }

    public Date readDate() {
        return new Date(this.readLong());
    }

    public FriendlyByteBuf writeDate(Date date) {
        this.writeLong(date.getTime());
        return this;
    }

    public BlockHitResult readBlockHitResult() {
        BlockPos blockPos = this.readBlockPos();
        Direction direction = this.readEnum(Direction.class);
        float f = this.readFloat();
        float f2 = this.readFloat();
        float f3 = this.readFloat();
        boolean bl = this.readBoolean();
        return new BlockHitResult(new Vec3((double)blockPos.getX() + (double)f, (double)blockPos.getY() + (double)f2, (double)blockPos.getZ() + (double)f3), direction, blockPos, bl);
    }

    public void writeBlockHitResult(BlockHitResult blockHitResult) {
        BlockPos blockPos = blockHitResult.getBlockPos();
        this.writeBlockPos(blockPos);
        this.writeEnum(blockHitResult.getDirection());
        Vec3 vec3 = blockHitResult.getLocation();
        this.writeFloat((float)(vec3.x - (double)blockPos.getX()));
        this.writeFloat((float)(vec3.y - (double)blockPos.getY()));
        this.writeFloat((float)(vec3.z - (double)blockPos.getZ()));
        this.writeBoolean(blockHitResult.isInside());
    }

    public int capacity() {
        return this.source.capacity();
    }

    public ByteBuf capacity(int n) {
        return this.source.capacity(n);
    }

    public int maxCapacity() {
        return this.source.maxCapacity();
    }

    public ByteBufAllocator alloc() {
        return this.source.alloc();
    }

    public ByteOrder order() {
        return this.source.order();
    }

    public ByteBuf order(ByteOrder byteOrder) {
        return this.source.order(byteOrder);
    }

    public ByteBuf unwrap() {
        return this.source.unwrap();
    }

    public boolean isDirect() {
        return this.source.isDirect();
    }

    public boolean isReadOnly() {
        return this.source.isReadOnly();
    }

    public ByteBuf asReadOnly() {
        return this.source.asReadOnly();
    }

    public int readerIndex() {
        return this.source.readerIndex();
    }

    public ByteBuf readerIndex(int n) {
        return this.source.readerIndex(n);
    }

    public int writerIndex() {
        return this.source.writerIndex();
    }

    public ByteBuf writerIndex(int n) {
        return this.source.writerIndex(n);
    }

    public ByteBuf setIndex(int n, int n2) {
        return this.source.setIndex(n, n2);
    }

    public int readableBytes() {
        return this.source.readableBytes();
    }

    public int writableBytes() {
        return this.source.writableBytes();
    }

    public int maxWritableBytes() {
        return this.source.maxWritableBytes();
    }

    public boolean isReadable() {
        return this.source.isReadable();
    }

    public boolean isReadable(int n) {
        return this.source.isReadable(n);
    }

    public boolean isWritable() {
        return this.source.isWritable();
    }

    public boolean isWritable(int n) {
        return this.source.isWritable(n);
    }

    public ByteBuf clear() {
        return this.source.clear();
    }

    public ByteBuf markReaderIndex() {
        return this.source.markReaderIndex();
    }

    public ByteBuf resetReaderIndex() {
        return this.source.resetReaderIndex();
    }

    public ByteBuf markWriterIndex() {
        return this.source.markWriterIndex();
    }

    public ByteBuf resetWriterIndex() {
        return this.source.resetWriterIndex();
    }

    public ByteBuf discardReadBytes() {
        return this.source.discardReadBytes();
    }

    public ByteBuf discardSomeReadBytes() {
        return this.source.discardSomeReadBytes();
    }

    public ByteBuf ensureWritable(int n) {
        return this.source.ensureWritable(n);
    }

    public int ensureWritable(int n, boolean bl) {
        return this.source.ensureWritable(n, bl);
    }

    public boolean getBoolean(int n) {
        return this.source.getBoolean(n);
    }

    public byte getByte(int n) {
        return this.source.getByte(n);
    }

    public short getUnsignedByte(int n) {
        return this.source.getUnsignedByte(n);
    }

    public short getShort(int n) {
        return this.source.getShort(n);
    }

    public short getShortLE(int n) {
        return this.source.getShortLE(n);
    }

    public int getUnsignedShort(int n) {
        return this.source.getUnsignedShort(n);
    }

    public int getUnsignedShortLE(int n) {
        return this.source.getUnsignedShortLE(n);
    }

    public int getMedium(int n) {
        return this.source.getMedium(n);
    }

    public int getMediumLE(int n) {
        return this.source.getMediumLE(n);
    }

    public int getUnsignedMedium(int n) {
        return this.source.getUnsignedMedium(n);
    }

    public int getUnsignedMediumLE(int n) {
        return this.source.getUnsignedMediumLE(n);
    }

    public int getInt(int n) {
        return this.source.getInt(n);
    }

    public int getIntLE(int n) {
        return this.source.getIntLE(n);
    }

    public long getUnsignedInt(int n) {
        return this.source.getUnsignedInt(n);
    }

    public long getUnsignedIntLE(int n) {
        return this.source.getUnsignedIntLE(n);
    }

    public long getLong(int n) {
        return this.source.getLong(n);
    }

    public long getLongLE(int n) {
        return this.source.getLongLE(n);
    }

    public char getChar(int n) {
        return this.source.getChar(n);
    }

    public float getFloat(int n) {
        return this.source.getFloat(n);
    }

    public double getDouble(int n) {
        return this.source.getDouble(n);
    }

    public ByteBuf getBytes(int n, ByteBuf byteBuf) {
        return this.source.getBytes(n, byteBuf);
    }

    public ByteBuf getBytes(int n, ByteBuf byteBuf, int n2) {
        return this.source.getBytes(n, byteBuf, n2);
    }

    public ByteBuf getBytes(int n, ByteBuf byteBuf, int n2, int n3) {
        return this.source.getBytes(n, byteBuf, n2, n3);
    }

    public ByteBuf getBytes(int n, byte[] arrby) {
        return this.source.getBytes(n, arrby);
    }

    public ByteBuf getBytes(int n, byte[] arrby, int n2, int n3) {
        return this.source.getBytes(n, arrby, n2, n3);
    }

    public ByteBuf getBytes(int n, ByteBuffer byteBuffer) {
        return this.source.getBytes(n, byteBuffer);
    }

    public ByteBuf getBytes(int n, OutputStream outputStream, int n2) throws IOException {
        return this.source.getBytes(n, outputStream, n2);
    }

    public int getBytes(int n, GatheringByteChannel gatheringByteChannel, int n2) throws IOException {
        return this.source.getBytes(n, gatheringByteChannel, n2);
    }

    public int getBytes(int n, FileChannel fileChannel, long l, int n2) throws IOException {
        return this.source.getBytes(n, fileChannel, l, n2);
    }

    public CharSequence getCharSequence(int n, int n2, Charset charset) {
        return this.source.getCharSequence(n, n2, charset);
    }

    public ByteBuf setBoolean(int n, boolean bl) {
        return this.source.setBoolean(n, bl);
    }

    public ByteBuf setByte(int n, int n2) {
        return this.source.setByte(n, n2);
    }

    public ByteBuf setShort(int n, int n2) {
        return this.source.setShort(n, n2);
    }

    public ByteBuf setShortLE(int n, int n2) {
        return this.source.setShortLE(n, n2);
    }

    public ByteBuf setMedium(int n, int n2) {
        return this.source.setMedium(n, n2);
    }

    public ByteBuf setMediumLE(int n, int n2) {
        return this.source.setMediumLE(n, n2);
    }

    public ByteBuf setInt(int n, int n2) {
        return this.source.setInt(n, n2);
    }

    public ByteBuf setIntLE(int n, int n2) {
        return this.source.setIntLE(n, n2);
    }

    public ByteBuf setLong(int n, long l) {
        return this.source.setLong(n, l);
    }

    public ByteBuf setLongLE(int n, long l) {
        return this.source.setLongLE(n, l);
    }

    public ByteBuf setChar(int n, int n2) {
        return this.source.setChar(n, n2);
    }

    public ByteBuf setFloat(int n, float f) {
        return this.source.setFloat(n, f);
    }

    public ByteBuf setDouble(int n, double d) {
        return this.source.setDouble(n, d);
    }

    public ByteBuf setBytes(int n, ByteBuf byteBuf) {
        return this.source.setBytes(n, byteBuf);
    }

    public ByteBuf setBytes(int n, ByteBuf byteBuf, int n2) {
        return this.source.setBytes(n, byteBuf, n2);
    }

    public ByteBuf setBytes(int n, ByteBuf byteBuf, int n2, int n3) {
        return this.source.setBytes(n, byteBuf, n2, n3);
    }

    public ByteBuf setBytes(int n, byte[] arrby) {
        return this.source.setBytes(n, arrby);
    }

    public ByteBuf setBytes(int n, byte[] arrby, int n2, int n3) {
        return this.source.setBytes(n, arrby, n2, n3);
    }

    public ByteBuf setBytes(int n, ByteBuffer byteBuffer) {
        return this.source.setBytes(n, byteBuffer);
    }

    public int setBytes(int n, InputStream inputStream, int n2) throws IOException {
        return this.source.setBytes(n, inputStream, n2);
    }

    public int setBytes(int n, ScatteringByteChannel scatteringByteChannel, int n2) throws IOException {
        return this.source.setBytes(n, scatteringByteChannel, n2);
    }

    public int setBytes(int n, FileChannel fileChannel, long l, int n2) throws IOException {
        return this.source.setBytes(n, fileChannel, l, n2);
    }

    public ByteBuf setZero(int n, int n2) {
        return this.source.setZero(n, n2);
    }

    public int setCharSequence(int n, CharSequence charSequence, Charset charset) {
        return this.source.setCharSequence(n, charSequence, charset);
    }

    public boolean readBoolean() {
        return this.source.readBoolean();
    }

    public byte readByte() {
        return this.source.readByte();
    }

    public short readUnsignedByte() {
        return this.source.readUnsignedByte();
    }

    public short readShort() {
        return this.source.readShort();
    }

    public short readShortLE() {
        return this.source.readShortLE();
    }

    public int readUnsignedShort() {
        return this.source.readUnsignedShort();
    }

    public int readUnsignedShortLE() {
        return this.source.readUnsignedShortLE();
    }

    public int readMedium() {
        return this.source.readMedium();
    }

    public int readMediumLE() {
        return this.source.readMediumLE();
    }

    public int readUnsignedMedium() {
        return this.source.readUnsignedMedium();
    }

    public int readUnsignedMediumLE() {
        return this.source.readUnsignedMediumLE();
    }

    public int readInt() {
        return this.source.readInt();
    }

    public int readIntLE() {
        return this.source.readIntLE();
    }

    public long readUnsignedInt() {
        return this.source.readUnsignedInt();
    }

    public long readUnsignedIntLE() {
        return this.source.readUnsignedIntLE();
    }

    public long readLong() {
        return this.source.readLong();
    }

    public long readLongLE() {
        return this.source.readLongLE();
    }

    public char readChar() {
        return this.source.readChar();
    }

    public float readFloat() {
        return this.source.readFloat();
    }

    public double readDouble() {
        return this.source.readDouble();
    }

    public ByteBuf readBytes(int n) {
        return this.source.readBytes(n);
    }

    public ByteBuf readSlice(int n) {
        return this.source.readSlice(n);
    }

    public ByteBuf readRetainedSlice(int n) {
        return this.source.readRetainedSlice(n);
    }

    public ByteBuf readBytes(ByteBuf byteBuf) {
        return this.source.readBytes(byteBuf);
    }

    public ByteBuf readBytes(ByteBuf byteBuf, int n) {
        return this.source.readBytes(byteBuf, n);
    }

    public ByteBuf readBytes(ByteBuf byteBuf, int n, int n2) {
        return this.source.readBytes(byteBuf, n, n2);
    }

    public ByteBuf readBytes(byte[] arrby) {
        return this.source.readBytes(arrby);
    }

    public ByteBuf readBytes(byte[] arrby, int n, int n2) {
        return this.source.readBytes(arrby, n, n2);
    }

    public ByteBuf readBytes(ByteBuffer byteBuffer) {
        return this.source.readBytes(byteBuffer);
    }

    public ByteBuf readBytes(OutputStream outputStream, int n) throws IOException {
        return this.source.readBytes(outputStream, n);
    }

    public int readBytes(GatheringByteChannel gatheringByteChannel, int n) throws IOException {
        return this.source.readBytes(gatheringByteChannel, n);
    }

    public CharSequence readCharSequence(int n, Charset charset) {
        return this.source.readCharSequence(n, charset);
    }

    public int readBytes(FileChannel fileChannel, long l, int n) throws IOException {
        return this.source.readBytes(fileChannel, l, n);
    }

    public ByteBuf skipBytes(int n) {
        return this.source.skipBytes(n);
    }

    public ByteBuf writeBoolean(boolean bl) {
        return this.source.writeBoolean(bl);
    }

    public ByteBuf writeByte(int n) {
        return this.source.writeByte(n);
    }

    public ByteBuf writeShort(int n) {
        return this.source.writeShort(n);
    }

    public ByteBuf writeShortLE(int n) {
        return this.source.writeShortLE(n);
    }

    public ByteBuf writeMedium(int n) {
        return this.source.writeMedium(n);
    }

    public ByteBuf writeMediumLE(int n) {
        return this.source.writeMediumLE(n);
    }

    public ByteBuf writeInt(int n) {
        return this.source.writeInt(n);
    }

    public ByteBuf writeIntLE(int n) {
        return this.source.writeIntLE(n);
    }

    public ByteBuf writeLong(long l) {
        return this.source.writeLong(l);
    }

    public ByteBuf writeLongLE(long l) {
        return this.source.writeLongLE(l);
    }

    public ByteBuf writeChar(int n) {
        return this.source.writeChar(n);
    }

    public ByteBuf writeFloat(float f) {
        return this.source.writeFloat(f);
    }

    public ByteBuf writeDouble(double d) {
        return this.source.writeDouble(d);
    }

    public ByteBuf writeBytes(ByteBuf byteBuf) {
        return this.source.writeBytes(byteBuf);
    }

    public ByteBuf writeBytes(ByteBuf byteBuf, int n) {
        return this.source.writeBytes(byteBuf, n);
    }

    public ByteBuf writeBytes(ByteBuf byteBuf, int n, int n2) {
        return this.source.writeBytes(byteBuf, n, n2);
    }

    public ByteBuf writeBytes(byte[] arrby) {
        return this.source.writeBytes(arrby);
    }

    public ByteBuf writeBytes(byte[] arrby, int n, int n2) {
        return this.source.writeBytes(arrby, n, n2);
    }

    public ByteBuf writeBytes(ByteBuffer byteBuffer) {
        return this.source.writeBytes(byteBuffer);
    }

    public int writeBytes(InputStream inputStream, int n) throws IOException {
        return this.source.writeBytes(inputStream, n);
    }

    public int writeBytes(ScatteringByteChannel scatteringByteChannel, int n) throws IOException {
        return this.source.writeBytes(scatteringByteChannel, n);
    }

    public int writeBytes(FileChannel fileChannel, long l, int n) throws IOException {
        return this.source.writeBytes(fileChannel, l, n);
    }

    public ByteBuf writeZero(int n) {
        return this.source.writeZero(n);
    }

    public int writeCharSequence(CharSequence charSequence, Charset charset) {
        return this.source.writeCharSequence(charSequence, charset);
    }

    public int indexOf(int n, int n2, byte by) {
        return this.source.indexOf(n, n2, by);
    }

    public int bytesBefore(byte by) {
        return this.source.bytesBefore(by);
    }

    public int bytesBefore(int n, byte by) {
        return this.source.bytesBefore(n, by);
    }

    public int bytesBefore(int n, int n2, byte by) {
        return this.source.bytesBefore(n, n2, by);
    }

    public int forEachByte(ByteProcessor byteProcessor) {
        return this.source.forEachByte(byteProcessor);
    }

    public int forEachByte(int n, int n2, ByteProcessor byteProcessor) {
        return this.source.forEachByte(n, n2, byteProcessor);
    }

    public int forEachByteDesc(ByteProcessor byteProcessor) {
        return this.source.forEachByteDesc(byteProcessor);
    }

    public int forEachByteDesc(int n, int n2, ByteProcessor byteProcessor) {
        return this.source.forEachByteDesc(n, n2, byteProcessor);
    }

    public ByteBuf copy() {
        return this.source.copy();
    }

    public ByteBuf copy(int n, int n2) {
        return this.source.copy(n, n2);
    }

    public ByteBuf slice() {
        return this.source.slice();
    }

    public ByteBuf retainedSlice() {
        return this.source.retainedSlice();
    }

    public ByteBuf slice(int n, int n2) {
        return this.source.slice(n, n2);
    }

    public ByteBuf retainedSlice(int n, int n2) {
        return this.source.retainedSlice(n, n2);
    }

    public ByteBuf duplicate() {
        return this.source.duplicate();
    }

    public ByteBuf retainedDuplicate() {
        return this.source.retainedDuplicate();
    }

    public int nioBufferCount() {
        return this.source.nioBufferCount();
    }

    public ByteBuffer nioBuffer() {
        return this.source.nioBuffer();
    }

    public ByteBuffer nioBuffer(int n, int n2) {
        return this.source.nioBuffer(n, n2);
    }

    public ByteBuffer internalNioBuffer(int n, int n2) {
        return this.source.internalNioBuffer(n, n2);
    }

    public ByteBuffer[] nioBuffers() {
        return this.source.nioBuffers();
    }

    public ByteBuffer[] nioBuffers(int n, int n2) {
        return this.source.nioBuffers(n, n2);
    }

    public boolean hasArray() {
        return this.source.hasArray();
    }

    public byte[] array() {
        return this.source.array();
    }

    public int arrayOffset() {
        return this.source.arrayOffset();
    }

    public boolean hasMemoryAddress() {
        return this.source.hasMemoryAddress();
    }

    public long memoryAddress() {
        return this.source.memoryAddress();
    }

    public String toString(Charset charset) {
        return this.source.toString(charset);
    }

    public String toString(int n, int n2, Charset charset) {
        return this.source.toString(n, n2, charset);
    }

    public int hashCode() {
        return this.source.hashCode();
    }

    public boolean equals(Object object) {
        return this.source.equals(object);
    }

    public int compareTo(ByteBuf byteBuf) {
        return this.source.compareTo(byteBuf);
    }

    public String toString() {
        return this.source.toString();
    }

    public ByteBuf retain(int n) {
        return this.source.retain(n);
    }

    public ByteBuf retain() {
        return this.source.retain();
    }

    public ByteBuf touch() {
        return this.source.touch();
    }

    public ByteBuf touch(Object object) {
        return this.source.touch(object);
    }

    public int refCnt() {
        return this.source.refCnt();
    }

    public boolean release() {
        return this.source.release();
    }

    public boolean release(int n) {
        return this.source.release(n);
    }
}

