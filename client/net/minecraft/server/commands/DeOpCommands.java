/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;

public class DeOpCommands {
    private static final SimpleCommandExceptionType ERROR_NOT_OP = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.deop.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("deop").requires(commandSourceStack -> commandSourceStack.hasPermission(3))).then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().getOpNames(), suggestionsBuilder)).executes(commandContext -> DeOpCommands.deopPlayers((CommandSourceStack)commandContext.getSource(), GameProfileArgument.getGameProfiles((CommandContext<CommandSourceStack>)commandContext, "targets")))));
    }

    private static int deopPlayers(CommandSourceStack commandSourceStack, Collection<GameProfile> collection) throws CommandSyntaxException {
        PlayerList playerList = commandSourceStack.getServer().getPlayerList();
        int n = 0;
        for (GameProfile gameProfile : collection) {
            if (!playerList.isOp(gameProfile)) continue;
            playerList.deop(gameProfile);
            ++n;
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.deop.success", collection.iterator().next().getName()), true);
        }
        if (n == 0) {
            throw ERROR_NOT_OP.create();
        }
        commandSourceStack.getServer().kickUnlistedPlayers(commandSourceStack);
        return n;
    }
}

