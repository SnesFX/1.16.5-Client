/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;

public class EmoteCommands {
    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)Commands.literal("me").then(Commands.argument("action", StringArgumentType.greedyString()).executes(commandContext -> {
            String string = StringArgumentType.getString((CommandContext)commandContext, (String)"action");
            Entity entity = ((CommandSourceStack)commandContext.getSource()).getEntity();
            MinecraftServer minecraftServer = ((CommandSourceStack)commandContext.getSource()).getServer();
            if (entity != null) {
                TextFilter textFilter;
                if (entity instanceof ServerPlayer && (textFilter = ((ServerPlayer)entity).getTextFilter()) != null) {
                    textFilter.processStreamMessage(string).thenAcceptAsync(optional -> optional.ifPresent(string -> minecraftServer.getPlayerList().broadcastMessage(EmoteCommands.createMessage((CommandContext<CommandSourceStack>)commandContext, string), ChatType.CHAT, entity.getUUID())), (Executor)minecraftServer);
                    return 1;
                }
                minecraftServer.getPlayerList().broadcastMessage(EmoteCommands.createMessage((CommandContext<CommandSourceStack>)commandContext, string), ChatType.CHAT, entity.getUUID());
            } else {
                minecraftServer.getPlayerList().broadcastMessage(EmoteCommands.createMessage((CommandContext<CommandSourceStack>)commandContext, string), ChatType.SYSTEM, Util.NIL_UUID);
            }
            return 1;
        })));
    }

    private static Component createMessage(CommandContext<CommandSourceStack> commandContext, String string) {
        return new TranslatableComponent("chat.type.emote", ((CommandSourceStack)commandContext.getSource()).getDisplayName(), string);
    }
}

