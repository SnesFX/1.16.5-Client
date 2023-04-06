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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.network.chat.TextComponent;

public class TestFunctionArgument
implements ArgumentType<TestFunction> {
    private static final Collection<String> EXAMPLES = Arrays.asList("techtests.piston", "techtests");

    public TestFunction parse(StringReader stringReader) throws CommandSyntaxException {
        String string = stringReader.readUnquotedString();
        Optional<TestFunction> optional = GameTestRegistry.findTestFunction(string);
        if (optional.isPresent()) {
            return optional.get();
        }
        TextComponent textComponent = new TextComponent("No such test: " + string);
        throw new CommandSyntaxException((CommandExceptionType)new SimpleCommandExceptionType((Message)textComponent), (Message)textComponent);
    }

    public static TestFunctionArgument testFunctionArgument() {
        return new TestFunctionArgument();
    }

    public static TestFunction getTestFunction(CommandContext<CommandSourceStack> commandContext, String string) {
        return (TestFunction)commandContext.getArgument(string, TestFunction.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        Stream<String> stream = GameTestRegistry.getAllTestFunctions().stream().map(TestFunction::getTestName);
        return SharedSuggestionProvider.suggest(stream, suggestionsBuilder);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}

