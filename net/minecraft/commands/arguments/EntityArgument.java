/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.commands.arguments;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class EntityArgument
implements ArgumentType<EntitySelector> {
    private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");
    public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_ENTITY = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.entity.toomany"));
    public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_PLAYER = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.player.toomany"));
    public static final SimpleCommandExceptionType ERROR_ONLY_PLAYERS_ALLOWED = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.player.entities"));
    public static final SimpleCommandExceptionType NO_ENTITIES_FOUND = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.entity.notfound.entity"));
    public static final SimpleCommandExceptionType NO_PLAYERS_FOUND = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.entity.notfound.player"));
    public static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType((Message)new TranslatableComponent("argument.entity.selector.not_allowed"));
    private final boolean single;
    private final boolean playersOnly;

    protected EntityArgument(boolean bl, boolean bl2) {
        this.single = bl;
        this.playersOnly = bl2;
    }

    public static EntityArgument entity() {
        return new EntityArgument(true, false);
    }

    public static Entity getEntity(CommandContext<CommandSourceStack> commandContext, String string) throws CommandSyntaxException {
        return ((EntitySelector)commandContext.getArgument(string, EntitySelector.class)).findSingleEntity((CommandSourceStack)commandContext.getSource());
    }

    public static EntityArgument entities() {
        return new EntityArgument(false, false);
    }

    public static Collection<? extends Entity> getEntities(CommandContext<CommandSourceStack> commandContext, String string) throws CommandSyntaxException {
        Collection<? extends Entity> collection = EntityArgument.getOptionalEntities(commandContext, string);
        if (collection.isEmpty()) {
            throw NO_ENTITIES_FOUND.create();
        }
        return collection;
    }

    public static Collection<? extends Entity> getOptionalEntities(CommandContext<CommandSourceStack> commandContext, String string) throws CommandSyntaxException {
        return ((EntitySelector)commandContext.getArgument(string, EntitySelector.class)).findEntities((CommandSourceStack)commandContext.getSource());
    }

    public static Collection<ServerPlayer> getOptionalPlayers(CommandContext<CommandSourceStack> commandContext, String string) throws CommandSyntaxException {
        return ((EntitySelector)commandContext.getArgument(string, EntitySelector.class)).findPlayers((CommandSourceStack)commandContext.getSource());
    }

    public static EntityArgument player() {
        return new EntityArgument(true, true);
    }

    public static ServerPlayer getPlayer(CommandContext<CommandSourceStack> commandContext, String string) throws CommandSyntaxException {
        return ((EntitySelector)commandContext.getArgument(string, EntitySelector.class)).findSinglePlayer((CommandSourceStack)commandContext.getSource());
    }

    public static EntityArgument players() {
        return new EntityArgument(false, true);
    }

    public static Collection<ServerPlayer> getPlayers(CommandContext<CommandSourceStack> commandContext, String string) throws CommandSyntaxException {
        List<ServerPlayer> list = ((EntitySelector)commandContext.getArgument(string, EntitySelector.class)).findPlayers((CommandSourceStack)commandContext.getSource());
        if (list.isEmpty()) {
            throw NO_PLAYERS_FOUND.create();
        }
        return list;
    }

    public EntitySelector parse(StringReader stringReader) throws CommandSyntaxException {
        boolean bl = false;
        EntitySelectorParser entitySelectorParser = new EntitySelectorParser(stringReader);
        EntitySelector entitySelector = entitySelectorParser.parse();
        if (entitySelector.getMaxResults() > 1 && this.single) {
            if (this.playersOnly) {
                stringReader.setCursor(0);
                throw ERROR_NOT_SINGLE_PLAYER.createWithContext((ImmutableStringReader)stringReader);
            }
            stringReader.setCursor(0);
            throw ERROR_NOT_SINGLE_ENTITY.createWithContext((ImmutableStringReader)stringReader);
        }
        if (entitySelector.includesEntities() && this.playersOnly && !entitySelector.isSelfSelector()) {
            stringReader.setCursor(0);
            throw ERROR_ONLY_PLAYERS_ALLOWED.createWithContext((ImmutableStringReader)stringReader);
        }
        return entitySelector;
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder2) {
        if (commandContext.getSource() instanceof SharedSuggestionProvider) {
            StringReader stringReader = new StringReader(suggestionsBuilder2.getInput());
            stringReader.setCursor(suggestionsBuilder2.getStart());
            SharedSuggestionProvider sharedSuggestionProvider = (SharedSuggestionProvider)commandContext.getSource();
            EntitySelectorParser entitySelectorParser = new EntitySelectorParser(stringReader, sharedSuggestionProvider.hasPermission(2));
            try {
                entitySelectorParser.parse();
            }
            catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
            return entitySelectorParser.fillSuggestions(suggestionsBuilder2, suggestionsBuilder -> {
                Collection<String> collection = sharedSuggestionProvider.getOnlinePlayerNames();
                Collection<String> collection2 = this.playersOnly ? collection : Iterables.concat(collection, sharedSuggestionProvider.getSelectedEntities());
                SharedSuggestionProvider.suggest(collection2, suggestionsBuilder);
            });
        }
        return Suggestions.empty();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    public static class Serializer
    implements ArgumentSerializer<EntityArgument> {
        @Override
        public void serializeToNetwork(EntityArgument entityArgument, FriendlyByteBuf friendlyByteBuf) {
            byte by = 0;
            if (entityArgument.single) {
                by = (byte)(by | true ? 1 : 0);
            }
            if (entityArgument.playersOnly) {
                by = (byte)(by | 2);
            }
            friendlyByteBuf.writeByte(by);
        }

        @Override
        public EntityArgument deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
            byte by = friendlyByteBuf.readByte();
            return new EntityArgument((by & 1) != 0, (by & 2) != 0);
        }

        @Override
        public void serializeToJson(EntityArgument entityArgument, JsonObject jsonObject) {
            jsonObject.addProperty("amount", entityArgument.single ? "single" : "multiple");
            jsonObject.addProperty("type", entityArgument.playersOnly ? "players" : "entities");
        }

        @Override
        public /* synthetic */ ArgumentType deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
            return this.deserializeFromNetwork(friendlyByteBuf);
        }
    }

}

