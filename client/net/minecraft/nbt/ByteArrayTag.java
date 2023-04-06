/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.lang3.ArrayUtils;

public class ByteArrayTag
extends CollectionTag<ByteTag> {
    public static final TagType<ByteArrayTag> TYPE = new TagType<ByteArrayTag>(){

        @Override
        public ByteArrayTag load(DataInput dataInput, int n, NbtAccounter nbtAccounter) throws IOException {
            nbtAccounter.accountBits(192L);
            int n2 = dataInput.readInt();
            nbtAccounter.accountBits(8L * (long)n2);
            byte[] arrby = new byte[n2];
            dataInput.readFully(arrby);
            return new ByteArrayTag(arrby);
        }

        @Override
        public String getName() {
            return "BYTE[]";
        }

        @Override
        public String getPrettyName() {
            return "TAG_Byte_Array";
        }

        @Override
        public /* synthetic */ Tag load(DataInput dataInput, int n, NbtAccounter nbtAccounter) throws IOException {
            return this.load(dataInput, n, nbtAccounter);
        }
    };
    private byte[] data;

    public ByteArrayTag(byte[] arrby) {
        this.data = arrby;
    }

    public ByteArrayTag(List<Byte> list) {
        this(ByteArrayTag.toArray(list));
    }

    private static byte[] toArray(List<Byte> list) {
        byte[] arrby = new byte[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            Byte by = list.get(i);
            arrby[i] = by == null ? (byte)0 : by;
        }
        return arrby;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.data.length);
        dataOutput.write(this.data);
    }

    @Override
    public byte getId() {
        return 7;
    }

    public TagType<ByteArrayTag> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[B;");
        for (int i = 0; i < this.data.length; ++i) {
            if (i != 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(this.data[i]).append('B');
        }
        return stringBuilder.append(']').toString();
    }

    @Override
    public Tag copy() {
        byte[] arrby = new byte[this.data.length];
        System.arraycopy(this.data, 0, arrby, 0, this.data.length);
        return new ByteArrayTag(arrby);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof ByteArrayTag && Arrays.equals(this.data, ((ByteArrayTag)object).data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.data);
    }

    @Override
    public Component getPrettyDisplay(String string, int n) {
        MutableComponent mutableComponent = new TextComponent("B").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        MutableComponent mutableComponent2 = new TextComponent("[").append(mutableComponent).append(";");
        for (int i = 0; i < this.data.length; ++i) {
            MutableComponent mutableComponent3 = new TextComponent(String.valueOf(this.data[i])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
            mutableComponent2.append(" ").append(mutableComponent3).append(mutableComponent);
            if (i == this.data.length - 1) continue;
            mutableComponent2.append(",");
        }
        mutableComponent2.append("]");
        return mutableComponent2;
    }

    public byte[] getAsByteArray() {
        return this.data;
    }

    @Override
    public int size() {
        return this.data.length;
    }

    @Override
    public ByteTag get(int n) {
        return ByteTag.valueOf(this.data[n]);
    }

    @Override
    public ByteTag set(int n, ByteTag byteTag) {
        byte by = this.data[n];
        this.data[n] = byteTag.getAsByte();
        return ByteTag.valueOf(by);
    }

    @Override
    public void add(int n, ByteTag byteTag) {
        this.data = ArrayUtils.add((byte[])this.data, (int)n, (byte)byteTag.getAsByte());
    }

    @Override
    public boolean setTag(int n, Tag tag) {
        if (tag instanceof NumericTag) {
            this.data[n] = ((NumericTag)tag).getAsByte();
            return true;
        }
        return false;
    }

    @Override
    public boolean addTag(int n, Tag tag) {
        if (tag instanceof NumericTag) {
            this.data = ArrayUtils.add((byte[])this.data, (int)n, (byte)((NumericTag)tag).getAsByte());
            return true;
        }
        return false;
    }

    @Override
    public ByteTag remove(int n) {
        byte by = this.data[n];
        this.data = ArrayUtils.remove((byte[])this.data, (int)n);
        return ByteTag.valueOf(by);
    }

    @Override
    public byte getElementType() {
        return 1;
    }

    @Override
    public void clear() {
        this.data = new byte[0];
    }

    @Override
    public /* synthetic */ Tag remove(int n) {
        return this.remove(n);
    }

    @Override
    public /* synthetic */ void add(int n, Tag tag) {
        this.add(n, (ByteTag)tag);
    }

    @Override
    public /* synthetic */ Tag set(int n, Tag tag) {
        return this.set(n, (ByteTag)tag);
    }

    @Override
    public /* synthetic */ Object remove(int n) {
        return this.remove(n);
    }

    @Override
    public /* synthetic */ void add(int n, Object object) {
        this.add(n, (ByteTag)object);
    }

    @Override
    public /* synthetic */ Object set(int n, Object object) {
        return this.set(n, (ByteTag)object);
    }

    @Override
    public /* synthetic */ Object get(int n) {
        return this.get(n);
    }

}

