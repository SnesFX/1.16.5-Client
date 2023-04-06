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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;

public class WhitelistCommand {
    private static final SimpleCommandExceptionType ERROR_ALREADY_ENABLED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.whitelist.alreadyOn"));
    private static final SimpleCommandExceptionType ERROR_ALREADY_DISABLED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.whitelist.alreadyOff"));
    private static final SimpleCommandExceptionType ERROR_ALREADY_WHITELISTED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.whitelist.add.failed"));
    private static final SimpleCommandExceptionType ERROR_NOT_WHITELISTED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.whitelist.remove.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("whitelist").requires(commandSourceStack -> commandSourceStack.hasPermission(3))).then(Commands.literal("on").executes(commandContext -> WhitelistCommand.enableWhitelist((CommandSourceStack)commandContext.getSource())))).then(Commands.literal("off").executes(commandContext -> WhitelistCommand.disableWhitelist((CommandSourceStack)commandContext.getSource())))).then(Commands.literal("list").executes(commandContext -> WhitelistCommand.showList((CommandSourceStack)commandContext.getSource())))).then(Commands.literal("add").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((commandContext, suggestionsBuilder) -> {
            PlayerList playerList = ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList();
            return SharedSuggestionProvider.suggest(playerList.getPlayers().stream().filter(serverPlayer -> !playerList.getWhiteList().isWhiteListed(serverPlayer.getGameProfile())).map(serverPlayer -> serverPlayer.getGameProfile().getName()), suggestionsBuilder);
        }).executes(commandContext -> WhitelistCommand.addPlayers((CommandSourceStack)commandContext.getSource(), GameProfileArgument.getGameProfiles((CommandContext<CommandSourceStack>)commandContext, "targets")))))).then(Commands.literal("remove").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().getWhiteListNames(), suggestionsBuilder)).executes(commandContext -> WhitelistCommand.removePlayers((CommandSourceStack)commandContext.getSource(), GameProfileArgument.getGameProfiles((CommandContext<CommandSourceStack>)commandContext, "targets")))))).then(Commands.literal("reload").executes(commandContext -> WhitelistCommand.reload((CommandSourceStack)commandContext.getSource()))));
    }

    private static int reload(CommandSourceStack commandSourceStack) {
        commandSourceStack.getServer().getPlayerList().reloadWhiteList();
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.whitelist.reloaded"), true);
        commandSourceStack.getServer().kickUnlistedPlayers(commandSourceStack);
        return 1;
    }

    private static int addPlayers(CommandSourceStack commandSourceStack, Collection<GameProfile> collection) throws CommandSyntaxException {
        UserWhiteList userWhiteList = commandSourceStack.getServer().getPlayerList().getWhiteList();
        int n = 0;
        for (GameProfile gameProfile : collection) {
            if (userWhiteList.isWhiteListed(gameProfile)) continue;
            UserWhiteListEntry userWhiteListEntry = new UserWhiteListEntry(gameProfile);
            userWhiteList.add(userWhiteListEntry);
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.whitelist.add.success", ComponentUtils.getDisplayName(gameProfile)), true);
            ++n;
        }
        if (n == 0) {
            throw ERROR_ALREADY_WHITELISTED.create();
        }
        return n;
    }

    private static int removePlayers(CommandSourceStack commandSourceStack, Collection<GameProfile> collection) throws CommandSyntaxException {
        UserWhiteList userWhiteList = commandSourceStack.getServer().getPlayerList().getWhiteList();
        int n = 0;
        for (GameProfile gameProfile : collection) {
            if (!userWhiteList.isWhiteListed(gameProfile)) continue;
            UserWhiteListEntry userWhiteListEntry = new UserWhiteListEntry(gameProfile);
            userWhiteList.remove(userWhiteListEntry);
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.whitelist.remove.success", ComponentUtils.getDisplayName(gameProfile)), true);
            ++n;
        }
        if (n == 0) {
            throw ERROR_NOT_WHITELISTED.create();
        }
        commandSourceStack.getServer().kickUnlistedPlayers(commandSourceStack);
        return n;
    }

    private static int enableWhitelist(CommandSourceStack commandSourceStack) throws CommandSyntaxException {
        PlayerList playerList = commandSourceStack.getServer().getPlayerList();
        if (playerList.isUsingWhitelist()) {
            throw ERROR_ALREADY_ENABLED.create();
        }
        playerList.setUsingWhiteList(true);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.whitelist.enabled"), true);
        commandSourceStack.getServer().kickUnlistedPlayers(commandSourceStack);
        return 1;
    }

    private static int disableWhitelist(CommandSourceStack commandSourceStack) throws CommandSyntaxException {
        PlayerList playerList = commandSourceStack.getServer().getPlayerList();
        if (!playerList.isUsingWhitelist()) {
            throw ERROR_ALREADY_DISABLED.create();
        }
        playerList.setUsingWhiteList(false);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.whitelist.disabled"), true);
        return 1;
    }

    private static int showList(CommandSourceStack commandSourceStack) {
        CharSequence[] arrcharSequence = commandSourceStack.getServer().getPlayerList().getWhiteListNames();
        if (arrcharSequence.length == 0) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.whitelist.none"), false);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.whitelist.list", arrcharSequence.length, String.join((CharSequence)", ", arrcharSequence)), false);
        }
        return arrcharSequence.length;
    }
}

