/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;

public class FunctionCommand {
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_FUNCTION = (commandContext, suggestionsBuilder) -> {
        ServerFunctionManager serverFunctionManager = ((CommandSourceStack)commandContext.getSource()).getServer().getFunctions();
        SharedSuggestionProvider.suggestResource(serverFunctionManager.getTagNames(), suggestionsBuilder, "#");
        return SharedSuggestionProvider.suggestResource(serverFunctionManager.getFunctionNames(), suggestionsBuilder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("function").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(Commands.argument("name", FunctionArgument.functions()).suggests(SUGGEST_FUNCTION).executes(commandContext -> FunctionCommand.runFunction((CommandSourceStack)commandContext.getSource(), FunctionArgument.getFunctions((CommandContext<CommandSourceStack>)commandContext, "name")))));
    }

    private static int runFunction(CommandSourceStack commandSourceStack, Collection<CommandFunction> collection) {
        int n = 0;
        for (CommandFunction commandFunction : collection) {
            n += commandSourceStack.getServer().getFunctions().execute(commandFunction, commandSourceStack.withSuppressedOutput().withMaximumPermission(2));
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.function.success.single", n, collection.iterator().next().getId()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.function.success.multiple", n, collection.size()), true);
        }
        return n;
    }
}

