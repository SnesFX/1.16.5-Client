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

public class ByteTag
extends NumericTag {
    public static final TagType<ByteTag> TYPE = new TagType<ByteTag>(){

        @Override
        public ByteTag load(DataInput dataInput, int n, NbtAccounter nbtAccounter) throws IOException {
            nbtAccounter.accountBits(72L);
            return ByteTag.valueOf(dataInput.readByte());
        }

        @Override
        public String getName() {
            return "BYTE";
        }

        @Override
        public String getPrettyName() {
            return "TAG_Byte";
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
    public static final ByteTag ZERO = ByteTag.valueOf((byte)0);
    public static final ByteTag ONE = ByteTag.valueOf((byte)1);
    private final byte data;

    private ByteTag(byte by) {
        this.data = by;
    }

    public static ByteTag valueOf(byte by) {
        return Cache.cache[128 + by];
    }

    public static ByteTag valueOf(boolean bl) {
        return bl ? ONE : ZERO;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeByte(this.data);
    }

    @Override
    public byte getId() {
        return 1;
    }

    public TagType<ByteTag> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return this.data + "b";
    }

    @Override
    public ByteTag copy() {
        return this;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof ByteTag && this.data == ((ByteTag)object).data;
    }

    public int hashCode() {
        return this.data;
    }

    @Override
    public Component getPrettyDisplay(String string, int n) {
        MutableComponent mutableComponent = new TextComponent("b").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        return new TextComponent(String.valueOf(this.data)).append(mutableComponent).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public long getAsLong() {
        return this.data;
    }

    @Override
    public int getAsInt() {
        return this.data;
    }

    @Override
    public short getAsShort() {
        return this.data;
    }

    @Override
    public byte getAsByte() {
        return this.data;
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
        private static final ByteTag[] cache = new ByteTag[256];

        static {
            for (int i = 0; i < cache.length; ++i) {
                Cache.cache[i] = new ByteTag((byte)(i - 128));
            }
        }
    }

}

