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
import net.minecraft.util.Mth;

public class DoubleTag
extends NumericTag {
    public static final DoubleTag ZERO = new DoubleTag(0.0);
    public static final TagType<DoubleTag> TYPE = new TagType<DoubleTag>(){

        @Override
        public DoubleTag load(DataInput dataInput, int n, NbtAccounter nbtAccounter) throws IOException {
            nbtAccounter.accountBits(128L);
            return DoubleTag.valueOf(dataInput.readDouble());
        }

        @Override
        public String getName() {
            return "DOUBLE";
        }

        @Override
        public String getPrettyName() {
            return "TAG_Double";
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
    private final double data;

    private DoubleTag(double d) {
        this.data = d;
    }

    public static DoubleTag valueOf(double d) {
        if (d == 0.0) {
            return ZERO;
        }
        return new DoubleTag(d);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeDouble(this.data);
    }

    @Override
    public byte getId() {
        return 6;
    }

    public TagType<DoubleTag> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return this.data + "d";
    }

    @Override
    public DoubleTag copy() {
        return this;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof DoubleTag && this.data == ((DoubleTag)object).data;
    }

    public int hashCode() {
        long l = Double.doubleToLongBits(this.data);
        return (int)(l ^ l >>> 32);
    }

    @Override
    public Component getPrettyDisplay(String string, int n) {
        MutableComponent mutableComponent = new TextComponent("d").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        return new TextComponent(String.valueOf(this.data)).append(mutableComponent).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public long getAsLong() {
        return (long)Math.floor(this.data);
    }

    @Override
    public int getAsInt() {
        return Mth.floor(this.data);
    }

    @Override
    public short getAsShort() {
        return (short)(Mth.floor(this.data) & 0xFFFF);
    }

    @Override
    public byte getAsByte() {
        return (byte)(Mth.floor(this.data) & 0xFF);
    }

    @Override
    public double getAsDouble() {
        return this.data;
    }

    @Override
    public float getAsFloat() {
        return (float)this.data;
    }

    @Override
    public Number getAsNumber() {
        return this.data;
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }

}

