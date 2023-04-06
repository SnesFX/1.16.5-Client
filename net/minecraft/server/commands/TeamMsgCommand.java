/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.List;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;

public class TeamMsgCommand {
    private static final Style SUGGEST_STYLE = Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.type.team.hover"))).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teammsg "));
    private static final SimpleCommandExceptionType ERROR_NOT_ON_TEAM = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.teammsg.failed.noteam"));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        LiteralCommandNode literalCommandNode = commandDispatcher.register((LiteralArgumentBuilder)Commands.literal("teammsg").then(Commands.argument("message", MessageArgument.message()).executes(commandContext -> TeamMsgCommand.sendMessage((CommandSourceStack)commandContext.getSource(), MessageArgument.getMessage((CommandContext<CommandSourceStack>)commandContext, "message")))));
        commandDispatcher.register((LiteralArgumentBuilder)Commands.literal("tm").redirect((CommandNode)literalCommandNode));
    }

    private static int sendMessage(CommandSourceStack commandSourceStack, Component component) throws CommandSyntaxException {
        Entity entity = commandSourceStack.getEntityOrException();
        PlayerTeam playerTeam = (PlayerTeam)entity.getTeam();
        if (playerTeam == null) {
            throw ERROR_NOT_ON_TEAM.create();
        }
        MutableComponent mutableComponent = playerTeam.getFormattedDisplayName().withStyle(SUGGEST_STYLE);
        List<ServerPlayer> list = commandSourceStack.getServer().getPlayerList().getPlayers();
        for (ServerPlayer serverPlayer : list) {
            if (serverPlayer == entity) {
                serverPlayer.sendMessage(new TranslatableComponent("chat.type.team.sent", mutableComponent, commandSourceStack.getDisplayName(), component), entity.getUUID());
                continue;
            }
            if (serverPlayer.getTeam() != playerTeam) continue;
            serverPlayer.sendMessage(new TranslatableComponent("chat.type.team.text", mutableComponent, commandSourceStack.getDisplayName(), component), entity.getUUID());
        }
        return list.size();
    }
}

