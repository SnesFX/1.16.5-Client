/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  org.apache.commons.lang3.ArrayUtils
 */
package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.lang3.ArrayUtils;

public class LongArrayTag
extends CollectionTag<LongTag> {
    public static final TagType<LongArrayTag> TYPE = new TagType<LongArrayTag>(){

        @Override
        public LongArrayTag load(DataInput dataInput, int n, NbtAccounter nbtAccounter) throws IOException {
            nbtAccounter.accountBits(192L);
            int n2 = dataInput.readInt();
            nbtAccounter.accountBits(64L * (long)n2);
            long[] arrl = new long[n2];
            for (int i = 0; i < n2; ++i) {
                arrl[i] = dataInput.readLong();
            }
            return new LongArrayTag(arrl);
        }

        @Override
        public String getName() {
            return "LONG[]";
        }

        @Override
        public String getPrettyName() {
            return "TAG_Long_Array";
        }

        @Override
        public /* synthetic */ Tag load(DataInput dataInput, int n, NbtAccounter nbtAccounter) throws IOException {
            return this.load(dataInput, n, nbtAccounter);
        }
    };
    private long[] data;

    public LongArrayTag(long[] arrl) {
        this.data = arrl;
    }

    public LongArrayTag(LongSet longSet) {
        this.data = longSet.toLongArray();
    }

    public LongArrayTag(List<Long> list) {
        this(LongArrayTag.toArray(list));
    }

    private static long[] toArray(List<Long> list) {
        long[] arrl = new long[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            Long l = list.get(i);
            arrl[i] = l == null ? 0L : l;
        }
        return arrl;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.data.length);
        for (long l : this.data) {
            dataOutput.writeLong(l);
        }
    }

    @Override
    public byte getId() {
        return 12;
    }

    public TagType<LongArrayTag> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[L;");
        for (int i = 0; i < this.data.length; ++i) {
            if (i != 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(this.data[i]).append('L');
        }
        return stringBuilder.append(']').toString();
    }

    @Override
    public LongArrayTag copy() {
        long[] arrl = new long[this.data.length];
        System.arraycopy(this.data, 0, arrl, 0, this.data.length);
        return new LongArrayTag(arrl);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof LongArrayTag && Arrays.equals(this.data, ((LongArrayTag)object).data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.data);
    }

    @Override
    public Component getPrettyDisplay(String string, int n) {
        MutableComponent mutableComponent = new TextComponent("L").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
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

    public long[] getAsLongArray() {
        return this.data;
    }

    @Override
    public int size() {
        return this.data.length;
    }

    @Override
    public LongTag get(int n) {
        return LongTag.valueOf(this.data[n]);
    }

    @Override
    public LongTag set(int n, LongTag longTag) {
        long l = this.data[n];
        this.data[n] = longTag.getAsLong();
        return LongTag.valueOf(l);
    }

    @Override
    public void add(int n, LongTag longTag) {
        this.data = ArrayUtils.add((long[])this.data, (int)n, (long)longTag.getAsLong());
    }

    @Override
    public boolean setTag(int n, Tag tag) {
        if (tag instanceof NumericTag) {
            this.data[n] = ((NumericTag)tag).getAsLong();
            return true;
        }
        return false;
    }

    @Override
    public boolean addTag(int n, Tag tag) {
        if (tag instanceof NumericTag) {
            this.data = ArrayUtils.add((long[])this.data, (int)n, (long)((NumericTag)tag).getAsLong());
            return true;
        }
        return false;
    }

    @Override
    public LongTag remove(int n) {
        long l = this.data[n];
        this.data = ArrayUtils.remove((long[])this.data, (int)n);
        return LongTag.valueOf(l);
    }

    @Override
    public byte getElementType() {
        return 4;
    }

    @Override
    public void clear() {
        this.data = new long[0];
    }

    @Override
    public /* synthetic */ Tag remove(int n) {
        return this.remove(n);
    }

    @Override
    public /* synthetic */ void add(int n, Tag tag) {
        this.add(n, (LongTag)tag);
    }

    @Override
    public /* synthetic */ Tag set(int n, Tag tag) {
        return this.set(n, (LongTag)tag);
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }

    @Override
    public /* synthetic */ Object remove(int n) {
        return this.remove(n);
    }

    @Override
    public /* synthetic */ void add(int n, Object object) {
        this.add(n, (LongTag)object);
    }

    @Override
    public /* synthetic */ Object set(int n, Object object) {
        return this.set(n, (LongTag)object);
    }

    @Override
    public /* synthetic */ Object get(int n) {
        return this.get(n);
    }

}

