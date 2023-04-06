/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

public class RecipeCommand {
    private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.recipe.give.failed"));
    private static final SimpleCommandExceptionType ERROR_TAKE_FAILED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.recipe.take.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("recipe").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(Commands.literal("give").then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.argument("recipe", ResourceLocationArgument.id()).suggests(SuggestionProviders.ALL_RECIPES).executes(commandContext -> RecipeCommand.giveRecipes((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Collections.singleton(ResourceLocationArgument.getRecipe((CommandContext<CommandSourceStack>)commandContext, "recipe")))))).then(Commands.literal("*").executes(commandContext -> RecipeCommand.giveRecipes((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), ((CommandSourceStack)commandContext.getSource()).getServer().getRecipeManager().getRecipes())))))).then(Commands.literal("take").then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.argument("recipe", ResourceLocationArgument.id()).suggests(SuggestionProviders.ALL_RECIPES).executes(commandContext -> RecipeCommand.takeRecipes((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), Collections.singleton(ResourceLocationArgument.getRecipe((CommandContext<CommandSourceStack>)commandContext, "recipe")))))).then(Commands.literal("*").executes(commandContext -> RecipeCommand.takeRecipes((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)commandContext, "targets"), ((CommandSourceStack)commandContext.getSource()).getServer().getRecipeManager().getRecipes()))))));
    }

    private static int giveRecipes(CommandSourceStack commandSourceStack, Collection<ServerPlayer> collection, Collection<Recipe<?>> collection2) throws CommandSyntaxException {
        int n = 0;
        for (ServerPlayer serverPlayer : collection) {
            n += serverPlayer.awardRecipes(collection2);
        }
        if (n == 0) {
            throw ERROR_GIVE_FAILED.create();
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.recipe.give.success.single", collection2.size(), collection.iterator().next().getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.recipe.give.success.multiple", collection2.size(), collection.size()), true);
        }
        return n;
    }

    private static int takeRecipes(CommandSourceStack commandSourceStack, Collection<ServerPlayer> collection, Collection<Recipe<?>> collection2) throws CommandSyntaxException {
        int n = 0;
        for (ServerPlayer serverPlayer : collection) {
            n += serverPlayer.resetRecipes(collection2);
        }
        if (n == 0) {
            throw ERROR_TAKE_FAILED.create();
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.recipe.take.success.single", collection2.size(), collection.iterator().next().getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.recipe.take.success.multiple", collection2.size(), collection.size()), true);
        }
        return n;
    }
}

