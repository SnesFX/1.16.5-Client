/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType$Function
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagType;
import net.minecraft.network.chat.TranslatableComponent;

public class TagParser {
    public static final SimpleCommandExceptionType ERROR_TRAILING_DATA = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.nbt.trailing"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_KEY = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.nbt.expected.key"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_VALUE = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.nbt.expected.value"));
    public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_LIST = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableComponent("argument.nbt.list.mixed", object, object2));
    public static final Dynamic2CommandExceptionType ERROR_INSERT_MIXED_ARRAY = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableComponent("argument.nbt.array.mixed", object, object2));
    public static final DynamicCommandExceptionType ERROR_INVALID_ARRAY = new DynamicCommandExceptionType(object -> new TranslatableComponent("argument.nbt.array.invalid", object));
    private static final Pattern DOUBLE_PATTERN_NOSUFFIX = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
    private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
    private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
    private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
    private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
    private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
    private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
    private final StringReader reader;

    public static CompoundTag parseTag(String string) throws CommandSyntaxException {
        return new TagParser(new StringReader(string)).readSingleStruct();
    }

    @VisibleForTesting
    CompoundTag readSingleStruct() throws CommandSyntaxException {
        CompoundTag compoundTag = this.readStruct();
        this.reader.skipWhitespace();
        if (this.reader.canRead()) {
            throw ERROR_TRAILING_DATA.createWithContext((ImmutableStringReader)this.reader);
        }
        return compoundTag;
    }

    public TagParser(StringReader stringReader) {
        this.reader = stringReader;
    }

    protected String readKey() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_KEY.createWithContext((ImmutableStringReader)this.reader);
        }
        return this.reader.readString();
    }

    protected Tag readTypedValue() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        int n = this.reader.getCursor();
        if (StringReader.isQuotedStringStart((char)this.reader.peek())) {
            return StringTag.valueOf(this.reader.readQuotedString());
        }
        String string = this.reader.readUnquotedString();
        if (string.isEmpty()) {
            this.reader.setCursor(n);
            throw ERROR_EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        return this.type(string);
    }

    private Tag type(String string) {
        try {
            if (FLOAT_PATTERN.matcher(string).matches()) {
                return FloatTag.valueOf(Float.parseFloat(string.substring(0, string.length() - 1)));
            }
            if (BYTE_PATTERN.matcher(string).matches()) {
                return ByteTag.valueOf(Byte.parseByte(string.substring(0, string.length() - 1)));
            }
            if (LONG_PATTERN.matcher(string).matches()) {
                return LongTag.valueOf(Long.parseLong(string.substring(0, string.length() - 1)));
            }
            if (SHORT_PATTERN.matcher(string).matches()) {
                return ShortTag.valueOf(Short.parseShort(string.substring(0, string.length() - 1)));
            }
            if (INT_PATTERN.matcher(string).matches()) {
                return IntTag.valueOf(Integer.parseInt(string));
            }
            if (DOUBLE_PATTERN.matcher(string).matches()) {
                return DoubleTag.valueOf(Double.parseDouble(string.substring(0, string.length() - 1)));
            }
            if (DOUBLE_PATTERN_NOSUFFIX.matcher(string).matches()) {
                return DoubleTag.valueOf(Double.parseDouble(string));
            }
            if ("true".equalsIgnoreCase(string)) {
                return ByteTag.ONE;
            }
            if ("false".equalsIgnoreCase(string)) {
                return ByteTag.ZERO;
            }
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        return StringTag.valueOf(string);
    }

    public Tag readValue() throws CommandSyntaxException {
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        char c = this.reader.peek();
        if (c == '{') {
            return this.readStruct();
        }
        if (c == '[') {
            return this.readList();
        }
        return this.readTypedValue();
    }

    protected Tag readList() throws CommandSyntaxException {
        if (this.reader.canRead(3) && !StringReader.isQuotedStringStart((char)this.reader.peek(1)) && this.reader.peek(2) == ';') {
            return this.readArrayTag();
        }
        return this.readListTag();
    }

    public CompoundTag readStruct() throws CommandSyntaxException {
        this.expect('{');
        CompoundTag compoundTag = new CompoundTag();
        this.reader.skipWhitespace();
        while (this.reader.canRead() && this.reader.peek() != '}') {
            int n = this.reader.getCursor();
            String string = this.readKey();
            if (string.isEmpty()) {
                this.reader.setCursor(n);
                throw ERROR_EXPECTED_KEY.createWithContext((ImmutableStringReader)this.reader);
            }
            this.expect(':');
            compoundTag.put(string, this.readValue());
            if (!this.hasElementSeparator()) break;
            if (this.reader.canRead()) continue;
            throw ERROR_EXPECTED_KEY.createWithContext((ImmutableStringReader)this.reader);
        }
        this.expect('}');
        return compoundTag;
    }

    private Tag readListTag() throws CommandSyntaxException {
        this.expect('[');
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        ListTag listTag = new ListTag();
        TagType<?> tagType = null;
        while (this.reader.peek() != ']') {
            int n = this.reader.getCursor();
            Tag tag = this.readValue();
            TagType<?> tagType2 = tag.getType();
            if (tagType == null) {
                tagType = tagType2;
            } else if (tagType2 != tagType) {
                this.reader.setCursor(n);
                throw ERROR_INSERT_MIXED_LIST.createWithContext((ImmutableStringReader)this.reader, (Object)tagType2.getPrettyName(), (Object)tagType.getPrettyName());
            }
            listTag.add(tag);
            if (!this.hasElementSeparator()) break;
            if (this.reader.canRead()) continue;
            throw ERROR_EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        this.expect(']');
        return listTag;
    }

    private Tag readArrayTag() throws CommandSyntaxException {
        this.expect('[');
        int n = this.reader.getCursor();
        char c = this.reader.read();
        this.reader.read();
        this.reader.skipWhitespace();
        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        if (c == 'B') {
            return new ByteArrayTag(this.readArray(ByteArrayTag.TYPE, ByteTag.TYPE));
        }
        if (c == 'L') {
            return new LongArrayTag(this.readArray(LongArrayTag.TYPE, LongTag.TYPE));
        }
        if (c == 'I') {
            return new IntArrayTag(this.readArray(IntArrayTag.TYPE, IntTag.TYPE));
        }
        this.reader.setCursor(n);
        throw ERROR_INVALID_ARRAY.createWithContext((ImmutableStringReader)this.reader, (Object)String.valueOf(c));
    }

    private <T extends Number> List<T> readArray(TagType<?> tagType, TagType<?> tagType2) throws CommandSyntaxException {
        ArrayList arrayList = Lists.newArrayList();
        while (this.reader.peek() != ']') {
            int n = this.reader.getCursor();
            Tag tag = this.readValue();
            TagType<?> tagType3 = tag.getType();
            if (tagType3 != tagType2) {
                this.reader.setCursor(n);
                throw ERROR_INSERT_MIXED_ARRAY.createWithContext((ImmutableStringReader)this.reader, (Object)tagType3.getPrettyName(), (Object)tagType.getPrettyName());
            }
            if (tagType2 == ByteTag.TYPE) {
                arrayList.add(((NumericTag)tag).getAsByte());
            } else if (tagType2 == LongTag.TYPE) {
                arrayList.add(((NumericTag)tag).getAsLong());
            } else {
                arrayList.add(((NumericTag)tag).getAsInt());
            }
            if (!this.hasElementSeparator()) break;
            if (this.reader.canRead()) continue;
            throw ERROR_EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader);
        }
        this.expect(']');
        return arrayList;
    }

    private boolean hasElementSeparator() {
        this.reader.skipWhitespace();
        if (this.reader.canRead() && this.reader.peek() == ',') {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        }
        return false;
    }

    private void expect(char c) throws CommandSyntaxException {
        this.reader.skipWhitespace();
        this.reader.expect(c);
    }
}

