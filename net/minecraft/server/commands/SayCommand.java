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
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;

public class SayCommand {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("say").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(Commands.argument("message", MessageArgument.message()).executes(commandContext -> {
            Component component = MessageArgument.getMessage((CommandContext<CommandSourceStack>)commandContext, "message");
            TranslatableComponent translatableComponent = new TranslatableComponent("chat.type.announcement", ((CommandSourceStack)commandContext.getSource()).getDisplayName(), component);
            Entity entity = ((CommandSourceStack)commandContext.getSource()).getEntity();
            if (entity != null) {
                ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().broadcastMessage(translatableComponent, ChatType.CHAT, entity.getUUID());
            } else {
                ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().broadcastMessage(translatableComponent, ChatType.SYSTEM, Util.NIL_UUID);
            }
            return 1;
        })));
    }
}

