/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.BuiltInExceptionProvider
 *  com.mojang.brigadier.exceptions.CommandExceptionType
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  javax.annotation.Nullable
 */
package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.GsonHelper;

public abstract class MinMaxBounds<T extends Number> {
    public static final SimpleCommandExceptionType ERROR_EMPTY = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.range.empty"));
    public static final SimpleCommandExceptionType ERROR_SWAPPED = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.range.swapped"));
    protected final T min;
    protected final T max;

    protected MinMaxBounds(@Nullable T t, @Nullable T t2) {
        this.min = t;
        this.max = t2;
    }

    @Nullable
    public T getMin() {
        return this.min;
    }

    @Nullable
    public T getMax() {
        return this.max;
    }

    public boolean isAny() {
        return this.min == null && this.max == null;
    }

    public JsonElement serializeToJson() {
        if (this.isAny()) {
            return JsonNull.INSTANCE;
        }
        if (this.min != null && this.min.equals(this.max)) {
            return new JsonPrimitive(this.min);
        }
        JsonObject jsonObject = new JsonObject();
        if (this.min != null) {
            jsonObject.addProperty("min", this.min);
        }
        if (this.max != null) {
            jsonObject.addProperty("max", this.max);
        }
        return jsonObject;
    }

    protected static <T extends Number, R extends MinMaxBounds<T>> R fromJson(@Nullable JsonElement jsonElement, R r, BiFunction<JsonElement, String, T> biFunction, BoundsFactory<T, R> boundsFactory) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return r;
        }
        if (GsonHelper.isNumberValue(jsonElement)) {
            Number number = (Number)biFunction.apply(jsonElement, "value");
            return boundsFactory.create(number, number);
        }
        JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "value");
        Number number = jsonObject.has("min") ? (Number)((Number)biFunction.apply(jsonObject.get("min"), "min")) : (Number)null;
        Number number2 = jsonObject.has("max") ? (Number)((Number)biFunction.apply(jsonObject.get("max"), "max")) : (Number)null;
        return boundsFactory.create(number, number2);
    }

    protected static <T extends Number, R extends MinMaxBounds<T>> R fromReader(StringReader stringReader, BoundsFromReaderFactory<T, R> boundsFromReaderFactory, Function<String, T> function, Supplier<DynamicCommandExceptionType> supplier, Function<T, T> function2) throws CommandSyntaxException {
        if (!stringReader.canRead()) {
            throw ERROR_EMPTY.createWithContext((ImmutableStringReader)stringReader);
        }
        int n = stringReader.getCursor();
        try {
            Number number;
            Number number2 = (Number)MinMaxBounds.optionallyFormat(MinMaxBounds.readNumber(stringReader, function, supplier), function2);
            if (stringReader.canRead(2) && stringReader.peek() == '.' && stringReader.peek(1) == '.') {
                stringReader.skip();
                stringReader.skip();
                number = (Number)MinMaxBounds.optionallyFormat(MinMaxBounds.readNumber(stringReader, function, supplier), function2);
                if (number2 == null && number == null) {
                    throw ERROR_EMPTY.createWithContext((ImmutableStringReader)stringReader);
                }
            } else {
                number = number2;
            }
            if (number2 == null && number == null) {
                throw ERROR_EMPTY.createWithContext((ImmutableStringReader)stringReader);
            }
            return boundsFromReaderFactory.create(stringReader, number2, number);
        }
        catch (CommandSyntaxException commandSyntaxException) {
            stringReader.setCursor(n);
            throw new CommandSyntaxException(commandSyntaxException.getType(), commandSyntaxException.getRawMessage(), commandSyntaxException.getInput(), n);
        }
    }

    @Nullable
    private static <T extends Number> T readNumber(StringReader stringReader, Function<String, T> function, Supplier<DynamicCommandExceptionType> supplier) throws CommandSyntaxException {
        int n = stringReader.getCursor();
        while (stringReader.canRead() && MinMaxBounds.isAllowedInputChat(stringReader)) {
            stringReader.skip();
        }
        String string = stringReader.getString().substring(n, stringReader.getCursor());
        if (string.isEmpty()) {
            return null;
        }
        try {
            return (T)((Number)function.apply(string));
        }
        catch (NumberFormatException numberFormatException) {
            throw supplier.get().createWithContext((ImmutableStringReader)stringReader, (Object)string);
        }
    }

    private static boolean isAllowedInputChat(StringReader stringReader) {
        char c = stringReader.peek();
        if (c >= '0' && c <= '9' || c == '-') {
            return true;
        }
        if (c == '.') {
            return !stringReader.canRead(2) || stringReader.peek(1) != '.';
        }
        return false;
    }

    @Nullable
    private static <T> T optionallyFormat(@Nullable T t, Function<T, T> function) {
        return t == null ? null : (T)function.apply(t);
    }

    @FunctionalInterface
    public static interface BoundsFromReaderFactory<T extends Number, R extends MinMaxBounds<T>> {
        public R create(StringReader var1, @Nullable T var2, @Nullable T var3) throws CommandSyntaxException;
    }

    @FunctionalInterface
    public static interface BoundsFactory<T extends Number, R extends MinMaxBounds<T>> {
        public R create(@Nullable T var1, @Nullable T var2);
    }

    public static class Floats
    extends MinMaxBounds<Float> {
        public static final Floats ANY = new Floats(null, null);
        private final Double minSq;
        private final Double maxSq;

        private static Floats create(StringReader stringReader, @Nullable Float f, @Nullable Float f2) throws CommandSyntaxException {
            if (f != null && f2 != null && f.floatValue() > f2.floatValue()) {
                throw ERROR_SWAPPED.createWithContext((ImmutableStringReader)stringReader);
            }
            return new Floats(f, f2);
        }

        @Nullable
        private static Double squareOpt(@Nullable Float f) {
            return f == null ? null : Double.valueOf(f.doubleValue() * f.doubleValue());
        }

        private Floats(@Nullable Float f, @Nullable Float f2) {
            super(f, f2);
            this.minSq = Floats.squareOpt(f);
            this.maxSq = Floats.squareOpt(f2);
        }

        public static Floats atLeast(float f) {
            return new Floats(Float.valueOf(f), null);
        }

        public boolean matches(float f) {
            if (this.min != null && ((Float)this.min).floatValue() > f) {
                return false;
            }
            return this.max == null || !(((Float)this.max).floatValue() < f);
        }

        public boolean matchesSqr(double d) {
            if (this.minSq != null && this.minSq > d) {
                return false;
            }
            return this.maxSq == null || !(this.maxSq < d);
        }

        public static Floats fromJson(@Nullable JsonElement jsonElement) {
            return Floats.fromJson(jsonElement, ANY, (arg_0, arg_1) -> GsonHelper.convertToFloat(arg_0, arg_1), (arg_0, arg_1) -> Floats.new(arg_0, arg_1));
        }

        public static Floats fromReader(StringReader stringReader) throws CommandSyntaxException {
            return Floats.fromReader(stringReader, f -> f);
        }

        public static Floats fromReader(StringReader stringReader, Function<Float, Float> function) throws CommandSyntaxException {
            return Floats.fromReader(stringReader, (arg_0, arg_1, arg_2) -> Floats.create(arg_0, arg_1, arg_2), Float::parseFloat, ((BuiltInExceptionProvider)CommandSyntaxException.BUILT_IN_EXCEPTIONS)::readerInvalidFloat, function);
        }
    }

    public static class Ints
    extends MinMaxBounds<Integer> {
        public static final Ints ANY = new Ints(null, null);
        private final Long minSq;
        private final Long maxSq;

        private static Ints create(StringReader stringReader, @Nullable Integer n, @Nullable Integer n2) throws CommandSyntaxException {
            if (n != null && n2 != null && n > n2) {
                throw ERROR_SWAPPED.createWithContext((ImmutableStringReader)stringReader);
            }
            return new Ints(n, n2);
        }

        @Nullable
        private static Long squareOpt(@Nullable Integer n) {
            return n == null ? null : Long.valueOf(n.longValue() * n.longValue());
        }

        private Ints(@Nullable Integer n, @Nullable Integer n2) {
            super(n, n2);
            this.minSq = Ints.squareOpt(n);
            this.maxSq = Ints.squareOpt(n2);
        }

        public static Ints exactly(int n) {
            return new Ints(n, n);
        }

        public static Ints atLeast(int n) {
            return new Ints(n, null);
        }

        public boolean matches(int n) {
            if (this.min != null && (Integer)this.min > n) {
                return false;
            }
            return this.max == null || (Integer)this.max >= n;
        }

        public static Ints fromJson(@Nullable JsonElement jsonElement) {
            return Ints.fromJson(jsonElement, ANY, (arg_0, arg_1) -> GsonHelper.convertToInt(arg_0, arg_1), (arg_0, arg_1) -> Ints.new(arg_0, arg_1));
        }

        public static Ints fromReader(StringReader stringReader) throws CommandSyntaxException {
            return Ints.fromReader(stringReader, n -> n);
        }

        public static Ints fromReader(StringReader stringReader, Function<Integer, Integer> function) throws CommandSyntaxException {
            return Ints.fromReader(stringReader, (arg_0, arg_1, arg_2) -> Ints.create(arg_0, arg_1, arg_2), Integer::parseInt, ((BuiltInExceptionProvider)CommandSyntaxException.BUILT_IN_EXCEPTIONS)::readerInvalidInt, function);
        }
    }

}

