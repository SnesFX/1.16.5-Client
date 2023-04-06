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
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.lang3.ArrayUtils;

public class IntArrayTag
extends CollectionTag<IntTag> {
    public static final TagType<IntArrayTag> TYPE = new TagType<IntArrayTag>(){

        @Override
        public IntArrayTag load(DataInput dataInput, int n, NbtAccounter nbtAccounter) throws IOException {
            nbtAccounter.accountBits(192L);
            int n2 = dataInput.readInt();
            nbtAccounter.accountBits(32L * (long)n2);
            int[] arrn = new int[n2];
            for (int i = 0; i < n2; ++i) {
                arrn[i] = dataInput.readInt();
            }
            return new IntArrayTag(arrn);
        }

        @Override
        public String getName() {
            return "INT[]";
        }

        @Override
        public String getPrettyName() {
            return "TAG_Int_Array";
        }

        @Override
        public /* synthetic */ Tag load(DataInput dataInput, int n, NbtAccounter nbtAccounter) throws IOException {
            return this.load(dataInput, n, nbtAccounter);
        }
    };
    private int[] data;

    public IntArrayTag(int[] arrn) {
        this.data = arrn;
    }

    public IntArrayTag(List<Integer> list) {
        this(IntArrayTag.toArray(list));
    }

    private static int[] toArray(List<Integer> list) {
        int[] arrn = new int[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            Integer n = list.get(i);
            arrn[i] = n == null ? 0 : n;
        }
        return arrn;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.data.length);
        for (int n : this.data) {
            dataOutput.writeInt(n);
        }
    }

    @Override
    public byte getId() {
        return 11;
    }

    public TagType<IntArrayTag> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[I;");
        for (int i = 0; i < this.data.length; ++i) {
            if (i != 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(this.data[i]);
        }
        return stringBuilder.append(']').toString();
    }

    @Override
    public IntArrayTag copy() {
        int[] arrn = new int[this.data.length];
        System.arraycopy(this.data, 0, arrn, 0, this.data.length);
        return new IntArrayTag(arrn);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof IntArrayTag && Arrays.equals(this.data, ((IntArrayTag)object).data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.data);
    }

    public int[] getAsIntArray() {
        return this.data;
    }

    @Override
    public Component getPrettyDisplay(String string, int n) {
        MutableComponent mutableComponent = new TextComponent("I").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        MutableComponent mutableComponent2 = new TextComponent("[").append(mutableComponent).append(";");
        for (int i = 0; i < this.data.length; ++i) {
            mutableComponent2.append(" ").append(new TextComponent(String.valueOf(this.data[i])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER));
            if (i == this.data.length - 1) continue;
            mutableComponent2.append(",");
        }
        mutableComponent2.append("]");
        return mutableComponent2;
    }

    @Override
    public int size() {
        return this.data.length;
    }

    @Override
    public IntTag get(int n) {
        return IntTag.valueOf(this.data[n]);
    }

    @Override
    public IntTag set(int n, IntTag intTag) {
        int n2 = this.data[n];
        this.data[n] = intTag.getAsInt();
        return IntTag.valueOf(n2);
    }

    @Override
    public void add(int n, IntTag intTag) {
        this.data = ArrayUtils.add((int[])this.data, (int)n, (int)intTag.getAsInt());
    }

    @Override
    public boolean setTag(int n, Tag tag) {
        if (tag instanceof NumericTag) {
            this.data[n] = ((NumericTag)tag).getAsInt();
            return true;
        }
        return false;
    }

    @Override
    public boolean addTag(int n, Tag tag) {
        if (tag instanceof NumericTag) {
            this.data = ArrayUtils.add((int[])this.data, (int)n, (int)((NumericTag)tag).getAsInt());
            return true;
        }
        return false;
    }

    @Override
    public IntTag remove(int n) {
        int n2 = this.data[n];
        this.data = ArrayUtils.remove((int[])this.data, (int)n);
        return IntTag.valueOf(n2);
    }

    @Override
    public byte getElementType() {
        return 3;
    }

    @Override
    public void clear() {
        this.data = new int[0];
    }

    @Override
    public /* synthetic */ Tag remove(int n) {
        return this.remove(n);
    }

    @Override
    public /* synthetic */ void add(int n, Tag tag) {
        this.add(n, (IntTag)tag);
    }

    @Override
    public /* synthetic */ Tag set(int n, Tag tag) {
        return this.set(n, (IntTag)tag);
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
        this.add(n, (IntTag)object);
    }

    @Override
    public /* synthetic */ Object set(int n, Object object) {
        return this.set(n, (IntTag)object);
    }

    @Override
    public /* synthetic */ Object get(int n) {
        return this.get(n);
    }

}

