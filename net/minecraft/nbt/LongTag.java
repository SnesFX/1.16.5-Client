/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class LongTag
extends NumericTag {
    public static final TagType<LongTag> TYPE = new TagType<LongTag>(){

        @Override
        public LongTag load(DataInput dataInput, int n, NbtAccounter nbtAccounter) throws IOException {
            nbtAccounter.accountBits(128L);
            return LongTag.valueOf(dataInput.readLong());
        }

        @Override
        public String getName() {
            return "LONG";
        }

        @Override
        public String getPrettyName() {
            return "TAG_Long";
        }

        @Override
        public boolean isValue() {
            return true;
        }

        @Override
        public /* synthetic */ Tag load(DataInput dataInput, int n, NbtAccounter nbtAccounter) throws IOException {
            return this.load(dataInput, n, nbtAccounter);
        }
    };
    private final long data;

    private LongTag(long l) {
        this.data = l;
    }

    public static LongTag valueOf(long l) {
        if (l >= -128L && l <= 1024L) {
            return Cache.cache[(int)l + 128];
        }
        return new LongTag(l);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeLong(this.data);
    }

    @Override
    public byte getId() {
        return 4;
    }

    public TagType<LongTag> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return this.data + "L";
    }

    @Override
    public LongTag copy() {
        return this;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof LongTag && this.data == ((LongTag)object).data;
    }

    public int hashCode() {
        return (int)(this.data ^ this.data >>> 32);
    }

    @Override
    public Component getPrettyDisplay(String string, int n) {
        MutableComponent mutableComponent = new TextComponent("L").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        return new TextComponent(String.valueOf(this.data)).append(mutableComponent).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public long getAsLong() {
        return this.data;
    }

    @Override
    public int getAsInt() {
        return (int)(this.data & 0xFFFFFFFFFFFFFFFFL);
    }

    @Override
    public short getAsShort() {
        return (short)(this.data & 0xFFFFL);
    }

    @Override
    public byte getAsByte() {
        return (byte)(this.data & 0xFFL);
    }

    @Override
    public double getAsDouble() {
        return this.data;
    }

    @Override
    public float getAsFloat() {
        return this.data;
    }

    @Override
    public Number getAsNumber() {
        return this.data;
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }

    static class Cache {
        static final LongTag[] cache = new LongTag[1153];

        static {
            for (int i = 0; i < cache.length; ++i) {
                Cache.cache[i] = new LongTag(-128 + i);
            }
        }
    }

}

