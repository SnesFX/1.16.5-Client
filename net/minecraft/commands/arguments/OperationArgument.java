/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.commands.arguments;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.scores.Score;

public class OperationArgument
implements ArgumentType<Operation> {
    private static final Collection<String> EXAMPLES = Arrays.asList("=", ">", "<");
    private static final SimpleCommandExceptionType ERROR_INVALID_OPERATION = new SimpleCommandExceptionType((Message)new TranslatableComponent("arguments.operation.invalid"));
    private static final SimpleCommandExceptionType ERROR_DIVIDE_BY_ZERO = new SimpleCommandExceptionType((Message)new TranslatableComponent("arguments.operation.div0"));

    public static OperationArgument operation() {
        return new OperationArgument();
    }

    public static Operation getOperation(CommandContext<CommandSourceStack> commandContext, String string) throws CommandSyntaxException {
        return (Operation)commandContext.getArgument(string, Operation.class);
    }

    public Operation parse(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead()) {
            int n = stringReader.getCursor();
            while (stringReader.canRead() && stringReader.peek() != ' ') {
                stringReader.skip();
            }
            return OperationArgument.getOperation(stringReader.getString().substring(n, stringReader.getCursor()));
        }
        throw ERROR_INVALID_OPERATION.create();
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggest(new String[]{"=", "+=", "-=", "*=", "/=", "%=", "<", ">", "><"}, suggestionsBuilder);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private static Operation getOperation(String string) throws CommandSyntaxException {
        if (string.equals("><")) {
            return (score, score2) -> {
                int n = score.getScore();
                score.setScore(score2.getScore());
                score2.setScore(n);
            };
        }
        return OperationArgument.getSimpleOperation(string);
    }

    private static SimpleOperation getSimpleOperation(String string) throws CommandSyntaxException {
        switch (string) {
            case "=": {
                return (n, n2) -> n2;
            }
            case "+=": {
                return (n, n2) -> n + n2;
            }
            case "-=": {
                return (n, n2) -> n - n2;
            }
            case "*=": {
                return (n, n2) -> n * n2;
            }
            case "/=": {
                return (n, n2) -> {
                    if (n2 == 0) {
                        throw ERROR_DIVIDE_BY_ZERO.create();
                    }
                    return Mth.intFloorDiv(n, n2);
                };
            }
            case "%=": {
                return (n, n2) -> {
                    if (n2 == 0) {
                        throw ERROR_DIVIDE_BY_ZERO.create();
                    }
                    return Mth.positiveModulo(n, n2);
                };
            }
            case "<": {
                return (arg_0, arg_1) -> Math.min(arg_0, arg_1);
            }
            case ">": {
                return (arg_0, arg_1) -> Math.max(arg_0, arg_1);
            }
        }
        throw ERROR_INVALID_OPERATION.create();
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    @FunctionalInterface
    static interface SimpleOperation
    extends Operation {
        public int apply(int var1, int var2) throws CommandSyntaxException;

        @Override
        default public void apply(Score score, Score score2) throws CommandSyntaxException {
            score.setScore(this.apply(score.getScore(), score2.getScore()));
        }
    }

    @FunctionalInterface
    public static interface Operation {
        public void apply(Score var1, Score var2) throws CommandSyntaxException;
    }

}

