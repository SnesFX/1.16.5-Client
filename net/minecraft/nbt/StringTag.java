/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class StringTag
implements Tag {
    public static final TagType<StringTag> TYPE = new TagType<StringTag>(){

        @Override
        public StringTag load(DataInput dataInput, int n, NbtAccounter nbtAccounter) throws IOException {
            nbtAccounter.accountBits(288L);
            String string = dataInput.readUTF();
            nbtAccounter.accountBits(16 * string.length());
            return StringTag.valueOf(string);
        }

        @Override
        public String getName() {
            return "STRING";
        }

        @Override
        public String getPrettyName() {
            return "TAG_String";
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
    private static final StringTag EMPTY = new StringTag("");
    private final String data;

    private StringTag(String string) {
        Objects.requireNonNull(string, "Null string not allowed");
        this.data = string;
    }

    public static StringTag valueOf(String string) {
        if (string.isEmpty()) {
            return EMPTY;
        }
        return new StringTag(string);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(this.data);
    }

    @Override
    public byte getId() {
        return 8;
    }

    public TagType<StringTag> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return StringTag.quoteAndEscape(this.data);
    }

    @Override
    public StringTag copy() {
        return this;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof StringTag && Objects.equals(this.data, ((StringTag)object).data);
    }

    public int hashCode() {
        return this.data.hashCode();
    }

    @Override
    public String getAsString() {
        return this.data;
    }

    @Override
    public Component getPrettyDisplay(String string, int n) {
        String string2 = StringTag.quoteAndEscape(this.data);
        String string3 = string2.substring(0, 1);
        MutableComponent mutableComponent = new TextComponent(string2.substring(1, string2.length() - 1)).withStyle(SYNTAX_HIGHLIGHTING_STRING);
        return new TextComponent(string3).append(mutableComponent).append(string3);
    }

    public static String quoteAndEscape(String string) {
        StringBuilder stringBuilder = new StringBuilder(" ");
        int n = 0;
        for (int i = 0; i < string.length(); ++i) {
            int n2 = string.charAt(i);
            if (n2 == 92) {
                stringBuilder.append('\\');
            } else if (n2 == 34 || n2 == 39) {
                if (n == 0) {
                    int n3 = n = n2 == 34 ? 39 : 34;
                }
                if (n == n2) {
                    stringBuilder.append('\\');
                }
            }
            stringBuilder.append((char)n2);
        }
        if (n == 0) {
            n = 34;
        }
        stringBuilder.setCharAt(0, (char)n);
        stringBuilder.append((char)n);
        return stringBuilder.toString();
    }

    @Override
    public /* synthetic */ Tag copy() {
        return this.copy();
    }

}

