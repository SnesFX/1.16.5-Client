/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
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

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;

public class GameProfileArgument
implements ArgumentType<Result> {
    private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e");
    public static final SimpleCommandExceptionType ERROR_UNKNOWN_PLAYER = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.player.unknown"));

    public static Collection<GameProfile> getGameProfiles(CommandContext<CommandSourceStack> commandContext, String string) throws CommandSyntaxException {
        return ((Result)commandContext.getArgument(string, Result.class)).getNames((CommandSourceStack)commandContext.getSource());
    }

    public static GameProfileArgument gameProfile() {
        return new GameProfileArgument();
    }

    public Result parse(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '@') {
            EntitySelectorParser entitySelectorParser = new EntitySelectorParser(stringReader);
            EntitySelector entitySelector = entitySelectorParser.parse();
            if (entitySelector.includesEntities()) {
                throw EntityArgument.ERROR_ONLY_PLAYERS_ALLOWED.create();
            }
            return new SelectorResult(entitySelector);
        }
        int n = stringReader.getCursor();
        while (stringReader.canRead() && stringReader.peek() != ' ') {
            stringReader.skip();
        }
        String string = stringReader.getString().substring(n, stringReader.getCursor());
        return commandSourceStack -> {
            GameProfile gameProfile = commandSourceStack.getServer().getProfileCache().get(string);
            if (gameProfile == null) {
                throw ERROR_UNKNOWN_PLAYER.create();
            }
            return Collections.singleton(gameProfile);
        };
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder2) {
        if (commandContext.getSource() instanceof SharedSuggestionProvider) {
            StringReader stringReader = new StringReader(suggestionsBuilder2.getInput());
            stringReader.setCursor(suggestionsBuilder2.getStart());
            EntitySelectorParser entitySelectorParser = new EntitySelectorParser(stringReader);
            try {
                entitySelectorParser.parse();
            }
            catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
            return entitySelectorParser.fillSuggestions(suggestionsBuilder2, suggestionsBuilder -> SharedSuggestionProvider.suggest(((SharedSuggestionProvider)commandContext.getSource()).getOnlinePlayerNames(), suggestionsBuilder));
        }
        return Suggestions.empty();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    public static class SelectorResult
    implements Result {
        private final EntitySelector selector;

        public SelectorResult(EntitySelector entitySelector) {
            this.selector = entitySelector;
        }

        @Override
        public Collection<GameProfile> getNames(CommandSourceStack commandSourceStack) throws CommandSyntaxException {
            List<ServerPlayer> list = this.selector.findPlayers(commandSourceStack);
            if (list.isEmpty()) {
                throw EntityArgument.NO_PLAYERS_FOUND.create();
            }
            ArrayList arrayList = Lists.newArrayList();
            for (ServerPlayer serverPlayer : list) {
                arrayList.add(serverPlayer.getGameProfile());
            }
            return arrayList;
        }
    }

    @FunctionalInterface
    public static interface Result {
        public Collection<GameProfile> getNames(CommandSourceStack var1) throws CommandSyntaxException;
    }

}

