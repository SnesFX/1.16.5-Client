/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class MsgCommand {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        LiteralCommandNode literalCommandNode = commandDispatcher.register((LiteralArgumentBuilder)Commands.literal("msg").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("message", MessageArgument.message()).executes(commandContext -> MsgCommand.sendMessage((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), MessageArgument.getMessage((CommandContext<CommandSourceStack>)commandContext, "message"))))));
        commandDispatcher.register((LiteralArgumentBuilder)Commands.literal("tell").redirect((CommandNode)literalCommandNode));
        commandDispatcher.register((LiteralArgumentBuilder)Commands.literal("w").redirect((CommandNode)literalCommandNode));
    }

    private static int sendMessage(CommandSourceStack commandSourceStack, Collection<ServerPlayer> collection, Component component) {
        Consumer<Component> consumer;
        UUID uUID = commandSourceStack.getEntity() == null ? Util.NIL_UUID : commandSourceStack.getEntity().getUUID();
        Entity entity = commandSourceStack.getEntity();
        if (entity instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)entity;
            consumer = component2 -> serverPlayer.sendMessage(new TranslatableComponent("commands.message.display.outgoing", component2, component).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC), serverPlayer.getUUID());
        } else {
            consumer = component2 -> commandSourceStack.sendSuccess(new TranslatableComponent("commands.message.display.outgoing", component2, component).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC), false);
        }
        for (ServerPlayer serverPlayer : collection) {
            consumer.accept(serverPlayer.getDisplayName());
            serverPlayer.sendMessage(new TranslatableComponent("commands.message.display.incoming", commandSourceStack.getDisplayName(), component).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC), uUID);
        }
        return collection.size();
    }
}

