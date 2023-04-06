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

public class FloatTag
extends NumericTag {
    public static final FloatTag ZERO = new FloatTag(0.0f);
    public static final TagType<FloatTag> TYPE = new TagType<FloatTag>(){

        @Override
        public FloatTag load(DataInput dataInput, int n, NbtAccounter nbtAccounter) throws IOException {
            nbtAccounter.accountBits(96L);
            return FloatTag.valueOf(dataInput.readFloat());
        }

        @Override
        public String getName() {
            return "FLOAT";
        }

        @Override
        public String getPrettyName() {
            return "TAG_Float";
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
    private final float data;

    private FloatTag(float f) {
        this.data = f;
    }

    public static FloatTag valueOf(float f) {
        if (f == 0.0f) {
            return ZERO;
        }
        return new FloatTag(f);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeFloat(this.data);
    }

    @Override
    public byte getId() {
        return 5;
    }

    public TagType<FloatTag> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return this.data + "f";
    }

    @Override
    public FloatTag copy() {
        return this;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof FloatTag && this.data == ((FloatTag)object).data;
    }

    public int hashCode() {
        return Float.floatToIntBits(this.data);
    }

    @Override
    public Component getPrettyDisplay(String string, int n) {
        MutableComponent mutableComponent = new TextComponent("f").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        return new TextComponent(String.valueOf(this.data)).append(mutableComponent).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public long getAsLong() {
        return (long)this.data;
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
        return this.data;
    }

    @Override
    public Number getAsNumber() {
        return Float.valueOf(this.data);
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }

}

