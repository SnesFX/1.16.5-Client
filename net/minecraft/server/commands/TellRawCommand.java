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
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerPlayer;

public class TellRawCommand {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tellraw").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("message", ComponentArgument.textComponent()).executes(commandContext -> {
            int n = 0;
            for (ServerPlayer serverPlayer : EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets")) {
                serverPlayer.sendMessage(ComponentUtils.updateForEntity((CommandSourceStack)commandContext.getSource(), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)commandContext, "message"), serverPlayer, 0), Util.NIL_UUID);
                ++n;
            }
            return n;
        }))));
    }
}

