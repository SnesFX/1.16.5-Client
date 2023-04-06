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
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  javax.annotation.Nullable
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
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;

public class BanPlayerCommands {
    private static final SimpleCommandExceptionType ERROR_ALREADY_BANNED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.ban.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("ban").requires(commandSourceStack -> commandSourceStack.hasPermission(3))).then(((RequiredArgumentBuilder)Commands.argument("targets", GameProfileArgument.gameProfile()).executes(commandContext -> BanPlayerCommands.banPlayers((CommandSourceStack)commandContext.getSource(), GameProfileArgument.getGameProfiles((CommandContext<CommandSourceStack>)commandContext, "targets"), null))).then(Commands.argument("reason", MessageArgument.message()).executes(commandContext -> BanPlayerCommands.banPlayers((CommandSourceStack)commandContext.getSource(), GameProfileArgument.getGameProfiles((CommandContext<CommandSourceStack>)commandContext, "targets"), MessageArgument.getMessage((CommandContext<CommandSourceStack>)commandContext, "reason"))))));
    }

    private static int banPlayers(CommandSourceStack commandSourceStack, Collection<GameProfile> collection, @Nullable Component component) throws CommandSyntaxException {
        UserBanList userBanList = commandSourceStack.getServer().getPlayerList().getBans();
        int n = 0;
        for (GameProfile gameProfile : collection) {
            if (userBanList.isBanned(gameProfile)) continue;
            UserBanListEntry userBanListEntry = new UserBanListEntry(gameProfile, null, commandSourceStack.getTextName(), null, component == null ? null : component.getString());
            userBanList.add(userBanListEntry);
            ++n;
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.ban.success", ComponentUtils.getDisplayName(gameProfile), userBanListEntry.getReason()), true);
            ServerPlayer serverPlayer = commandSourceStack.getServer().getPlayerList().getPlayer(gameProfile.getId());
            if (serverPlayer == null) continue;
            serverPlayer.connection.disconnect(new TranslatableComponent("multiplayer.disconnect.banned"));
        }
        if (n == 0) {
            throw ERROR_ALREADY_BANNED.create();
        }
        return n;
    }
}

