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
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
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
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.storage.WorldData;

public class DifficultyCommand {
    private static final DynamicCommandExceptionType ERROR_ALREADY_DIFFICULT = new DynamicCommandExceptionType(object -> new TranslatableComponent("commands.difficulty.failure", object));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder = Commands.literal("difficulty");
        for (Difficulty difficulty : Difficulty.values()) {
            literalArgumentBuilder.then(Commands.literal(difficulty.getKey()).executes(commandContext -> DifficultyCommand.setDifficulty((CommandSourceStack)commandContext.getSource(), difficulty)));
        }
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder.requires(commandSourceStack -> commandSourceStack.hasPermission(2))).executes(commandContext -> {
            Difficulty difficulty = ((CommandSourceStack)commandContext.getSource()).getLevel().getDifficulty();
            ((CommandSourceStack)commandContext.getSource()).sendSuccess(new TranslatableComponent("commands.difficulty.query", difficulty.getDisplayName()), false);
            return difficulty.getId();
        }));
    }

    public static int setDifficulty(CommandSourceStack commandSourceStack, Difficulty difficulty) throws CommandSyntaxException {
        MinecraftServer minecraftServer = commandSourceStack.getServer();
        if (minecraftServer.getWorldData().getDifficulty() == difficulty) {
            throw ERROR_ALREADY_DIFFICULT.create((Object)difficulty.getKey());
        }
        minecraftServer.setDifficulty(difficulty, true);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.difficulty.success", difficulty.getDisplayName()), true);
        return 0;
    }
}

