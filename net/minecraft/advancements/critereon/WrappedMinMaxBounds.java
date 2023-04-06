/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.BuiltInExceptionProvider
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.network.chat.TranslatableComponent;

public class WrappedMinMaxBounds {
    public static final WrappedMinMaxBounds ANY = new WrappedMinMaxBounds(null, null);
    public static final SimpleCommandExceptionType ERROR_INTS_ONLY = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.range.ints"));
    private final Float min;
    private final Float max;

    public WrappedMinMaxBounds(@Nullable Float f, @Nullable Float f2) {
        this.min = f;
        this.max = f2;
    }

    @Nullable
    public Float getMin() {
        return this.min;
    }

    @Nullable
    public Float getMax() {
        return this.max;
    }

    public static WrappedMinMaxBounds fromReader(StringReader stringReader, boolean bl, Function<Float, Float> function) throws CommandSyntaxException {
        Float f;
        if (!stringReader.canRead()) {
            throw MinMaxBounds.ERROR_EMPTY.createWithContext((ImmutableStringReader)stringReader);
        }
        int n = stringReader.getCursor();
        Float f2 = WrappedMinMaxBounds.optionallyFormat(WrappedMinMaxBounds.readNumber(stringReader, bl), function);
        if (stringReader.canRead(2) && stringReader.peek() == '.' && stringReader.peek(1) == '.') {
            stringReader.skip();
            stringReader.skip();
            f = WrappedMinMaxBounds.optionallyFormat(WrappedMinMaxBounds.readNumber(stringReader, bl), function);
            if (f2 == null && f == null) {
                stringReader.setCursor(n);
                throw MinMaxBounds.ERROR_EMPTY.createWithContext((ImmutableStringReader)stringReader);
            }
        } else {
            if (!bl && stringReader.canRead() && stringReader.peek() == '.') {
                stringReader.setCursor(n);
                throw ERROR_INTS_ONLY.createWithContext((ImmutableStringReader)stringReader);
            }
            f = f2;
        }
        if (f2 == null && f == null) {
            stringReader.setCursor(n);
            throw MinMaxBounds.ERROR_EMPTY.createWithContext((ImmutableStringReader)stringReader);
        }
        return new WrappedMinMaxBounds(f2, f);
    }

    @Nullable
    private static Float readNumber(StringReader stringReader, boolean bl) throws CommandSyntaxException {
        int n = stringReader.getCursor();
        while (stringReader.canRead() && WrappedMinMaxBounds.isAllowedNumber(stringReader, bl)) {
            stringReader.skip();
        }
        String string = stringReader.getString().substring(n, stringReader.getCursor());
        if (string.isEmpty()) {
            return null;
        }
        try {
            return Float.valueOf(Float.parseFloat(string));
        }
        catch (NumberFormatException numberFormatException) {
            if (bl) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble().createWithContext((ImmutableStringReader)stringReader, (Object)string);
            }
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().createWithContext((ImmutableStringReader)stringReader, (Object)string);
        }
    }

    private static boolean isAllowedNumber(StringReader stringReader, boolean bl) {
        char c = stringReader.peek();
        if (c >= '0' && c <= '9' || c == '-') {
            return true;
        }
        if (bl && c == '.') {
            return !stringReader.canRead(2) || stringReader.peek(1) != '.';
        }
        return false;
    }

    @Nullable
    private static Float optionallyFormat(@Nullable Float f, Function<Float, Float> function) {
        return f == null ? null : function.apply(f);
    }
}

