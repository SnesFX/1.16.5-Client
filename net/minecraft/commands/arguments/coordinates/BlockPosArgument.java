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
package net.minecraft.commands.arguments.coordinates;

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
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.LocalCoordinates;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;

public class BlockPosArgument
implements ArgumentType<Coordinates> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
    public static final SimpleCommandExceptionType ERROR_NOT_LOADED = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.pos.unloaded"));
    public static final SimpleCommandExceptionType ERROR_OUT_OF_WORLD = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.pos.outofworld"));

    public static BlockPosArgument blockPos() {
        return new BlockPosArgument();
    }

    public static BlockPos getLoadedBlockPos(CommandContext<CommandSourceStack> commandContext, String string) throws CommandSyntaxException {
        BlockPos blockPos = ((Coordinates)commandContext.getArgument(string, Coordinates.class)).getBlockPos((CommandSourceStack)commandContext.getSource());
        if (!((CommandSourceStack)commandContext.getSource()).getLevel().hasChunkAt(blockPos)) {
            throw ERROR_NOT_LOADED.create();
        }
        ((CommandSourceStack)commandContext.getSource()).getLevel();
        if (!ServerLevel.isInWorldBounds(blockPos)) {
            throw ERROR_OUT_OF_WORLD.create();
        }
        return blockPos;
    }

    public static BlockPos getOrLoadBlockPos(CommandContext<CommandSourceStack> commandContext, String string) throws CommandSyntaxException {
        return ((Coordinates)commandContext.getArgument(string, Coordinates.class)).getBlockPos((CommandSourceStack)commandContext.getSource());
    }

    public Coordinates parse(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '^') {
            return LocalCoordinates.parse(stringReader);
        }
        return WorldCoordinates.parseInt(stringReader);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        if (commandContext.getSource() instanceof SharedSuggestionProvider) {
            String string = suggestionsBuilder.getRemaining();
            Collection<SharedSuggestionProvider.TextCoordinates> collection = !string.isEmpty() && string.charAt(0) == '^' ? Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_LOCAL) : ((SharedSuggestionProvider)commandContext.getSource()).getRelevantCoordinates();
            return SharedSuggestionProvider.suggestCoordinates(string, collection, suggestionsBuilder, Commands.createValidator(this::parse));
        }
        return Suggestions.empty();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}

