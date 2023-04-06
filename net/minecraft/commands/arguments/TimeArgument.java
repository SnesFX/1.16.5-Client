/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 */
package net.minecraft.commands.arguments;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;

public class TimeArgument
implements ArgumentType<Integer> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0d", "0s", "0t", "0");
    private static final SimpleCommandExceptionType ERROR_INVALID_UNIT = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.time.invalid_unit"));
    private static final DynamicCommandExceptionType ERROR_INVALID_TICK_COUNT = new DynamicCommandExceptionType(object -> new TranslatableComponent("argument.time.invalid_tick_count", object));
    private static final Object2IntMap<String> UNITS = new Object2IntOpenHashMap();

    public static TimeArgument time() {
        return new TimeArgument();
    }

    public Integer parse(StringReader stringReader) throws CommandSyntaxException {
        float f = stringReader.readFloat();
        String string = stringReader.readUnquotedString();
        int n = UNITS.getOrDefault((Object)string, 0);
        if (n == 0) {
            throw ERROR_INVALID_UNIT.create();
        }
        int n2 = Math.round(f * (float)n);
        if (n2 < 0) {
            throw ERROR_INVALID_TICK_COUNT.create((Object)n2);
        }
        return n2;
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        StringReader stringReader = new StringReader(suggestionsBuilder.getRemaining());
        try {
            stringReader.readFloat();
        }
        catch (CommandSyntaxException commandSyntaxException) {
            return suggestionsBuilder.buildFuture();
        }
        return SharedSuggestionProvider.suggest((Iterable<String>)UNITS.keySet(), suggestionsBuilder.createOffset(suggestionsBuilder.getStart() + stringReader.getCursor()));
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    static {
        UNITS.put((Object)"d", 24000);
        UNITS.put((Object)"s", 20);
        UNITS.put((Object)"t", 1);
        UNITS.put((Object)"", 1);
    }
}

