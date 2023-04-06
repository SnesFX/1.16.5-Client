/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetTitlesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class TitleCommand {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("title").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.literal("clear").executes(commandContext -> TitleCommand.clearTitle((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"))))).then(Commands.literal("reset").executes(commandContext -> TitleCommand.resetTitle((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"))))).then(Commands.literal("title").then(Commands.argument("title", ComponentArgument.textComponent()).executes(commandContext -> TitleCommand.showTitle((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)commandContext, "title"), ClientboundSetTitlesPacket.Type.TITLE))))).then(Commands.literal("subtitle").then(Commands.argument("title", ComponentArgument.textComponent()).executes(commandContext -> TitleCommand.showTitle((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)commandContext, "title"), ClientboundSetTitlesPacket.Type.SUBTITLE))))).then(Commands.literal("actionbar").then(Commands.argument("title", ComponentArgument.textComponent()).executes(commandContext -> TitleCommand.showTitle((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)commandContext, "title"), ClientboundSetTitlesPacket.Type.ACTIONBAR))))).then(Commands.literal("times").then(Commands.argument("fadeIn", IntegerArgumentType.integer((int)0)).then(Commands.argument("stay", IntegerArgumentType.integer((int)0)).then(Commands.argument("fadeOut", IntegerArgumentType.integer((int)0)).executes(commandContext -> TitleCommand.setTimes((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"fadeIn"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"stay"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"fadeOut")))))))));
    }

    private static int clearTitle(CommandSourceStack commandSourceStack, Collection<ServerPlayer> collection) {
        ClientboundSetTitlesPacket clientboundSetTitlesPacket = new ClientboundSetTitlesPacket(ClientboundSetTitlesPacket.Type.CLEAR, null);
        for (ServerPlayer serverPlayer : collection) {
            serverPlayer.connection.send(clientboundSetTitlesPacket);
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.title.cleared.single", collection.iterator().next().getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.title.cleared.multiple", collection.size()), true);
        }
        return collection.size();
    }

    private static int resetTitle(CommandSourceStack commandSourceStack, Collection<ServerPlayer> collection) {
        ClientboundSetTitlesPacket clientboundSetTitlesPacket = new ClientboundSetTitlesPacket(ClientboundSetTitlesPacket.Type.RESET, null);
        for (ServerPlayer serverPlayer : collection) {
            serverPlayer.connection.send(clientboundSetTitlesPacket);
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.title.reset.single", collection.iterator().next().getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.title.reset.multiple", collection.size()), true);
        }
        return collection.size();
    }

    private static int showTitle(CommandSourceStack commandSourceStack, Collection<ServerPlayer> collection, Component component, ClientboundSetTitlesPacket.Type type) throws CommandSyntaxException {
        for (ServerPlayer serverPlayer : collection) {
            serverPlayer.connection.send(new ClientboundSetTitlesPacket(type, ComponentUtils.updateForEntity(commandSourceStack, component, serverPlayer, 0)));
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.title.show." + type.name().toLowerCase(Locale.ROOT) + ".single", collection.iterator().next().getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.title.show." + type.name().toLowerCase(Locale.ROOT) + ".multiple", collection.size()), true);
        }
        return collection.size();
    }

    private static int setTimes(CommandSourceStack commandSourceStack, Collection<ServerPlayer> collection, int n, int n2, int n3) {
        ClientboundSetTitlesPacket clientboundSetTitlesPacket = new ClientboundSetTitlesPacket(n, n2, n3);
        for (ServerPlayer serverPlayer : collection) {
            serverPlayer.connection.send(clientboundSetTitlesPacket);
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.title.times.single", collection.iterator().next().getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.title.times.multiple", collection.size()), true);
        }
        return collection.size();
    }
}

