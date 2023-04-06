/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;

public class ListPlayersCommand {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("list").executes(commandContext -> ListPlayersCommand.listPlayers((CommandSourceStack)commandContext.getSource()))).then(Commands.literal("uuids").executes(commandContext -> ListPlayersCommand.listPlayersWithUuids((CommandSourceStack)commandContext.getSource()))));
    }

    private static int listPlayers(CommandSourceStack commandSourceStack) {
        return ListPlayersCommand.format(commandSourceStack, Player::getDisplayName);
    }

    private static int listPlayersWithUuids(CommandSourceStack commandSourceStack) {
        return ListPlayersCommand.format(commandSourceStack, serverPlayer -> new TranslatableComponent("commands.list.nameAndId", serverPlayer.getName(), serverPlayer.getGameProfile().getId()));
    }

    private static int format(CommandSourceStack commandSourceStack, Function<ServerPlayer, Component> function) {
        PlayerList playerList = commandSourceStack.getServer().getPlayerList();
        List<ServerPlayer> list = playerList.getPlayers();
        MutableComponent mutableComponent = ComponentUtils.formatList(list, function);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.list.players", list.size(), playerList.getMaxPlayers(), mutableComponent), false);
        return list.size();
    }
}

