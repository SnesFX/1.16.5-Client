/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandExceptionType
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.gametest.framework;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.network.chat.TextComponent;

public class TestClassNameArgument
implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = Arrays.asList("techtests", "mobtests");

    public String parse(StringReader stringReader) throws CommandSyntaxException {
        String string = stringReader.readUnquotedString();
        if (GameTestRegistry.isTestClass(string)) {
            return string;
        }
        TextComponent textComponent = new TextComponent("No such test class: " + string);
        throw new CommandSyntaxException((CommandExceptionType)new SimpleCommandExceptionType((Message)textComponent), (Message)textComponent);
    }

    public static TestClassNameArgument testClassName() {
        return new TestClassNameArgument();
    }

    public static String getTestClassName(CommandContext<CommandSourceStack> commandContext, String string) {
        return (String)commandContext.getArgument(string, String.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggest(GameTestRegistry.getAllTestClassNames().stream(), suggestionsBuilder);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}

