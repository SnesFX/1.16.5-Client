/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.google.common.collect.Sets;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;

public class TagCommand {
    private static final SimpleCommandExceptionType ERROR_ADD_FAILED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.tag.add.failed"));
    private static final SimpleCommandExceptionType ERROR_REMOVE_FAILED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.tag.remove.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tag").requires(commandSourceStack -> commandSourceStack.hasPermission(2))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.entities()).then(Commands.literal("add").then(Commands.argument("name", StringArgumentType.word()).executes(commandContext -> TagCommand.addTag((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), StringArgumentType.getString((CommandContext)commandContext, (String)"name")))))).then(Commands.literal("remove").then(Commands.argument("name", StringArgumentType.word()).suggests((commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(TagCommand.getTags(EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets")), suggestionsBuilder)).executes(commandContext -> TagCommand.removeTag((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"), StringArgumentType.getString((CommandContext)commandContext, (String)"name")))))).then(Commands.literal("list").executes(commandContext -> TagCommand.listTags((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets"))))));
    }

    private static Collection<String> getTags(Collection<? extends Entity> collection) {
        HashSet hashSet = Sets.newHashSet();
        for (Entity entity : collection) {
            hashSet.addAll(entity.getTags());
        }
        return hashSet;
    }

    private static int addTag(CommandSourceStack commandSourceStack, Collection<? extends Entity> collection, String string) throws CommandSyntaxException {
        int n = 0;
        for (Entity entity : collection) {
            if (!entity.addTag(string)) continue;
            ++n;
        }
        if (n == 0) {
            throw ERROR_ADD_FAILED.create();
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.tag.add.success.single", string, collection.iterator().next().getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.tag.add.success.multiple", string, collection.size()), true);
        }
        return n;
    }

    private static int removeTag(CommandSourceStack commandSourceStack, Collection<? extends Entity> collection, String string) throws CommandSyntaxException {
        int n = 0;
        for (Entity entity : collection) {
            if (!entity.removeTag(string)) continue;
            ++n;
        }
        if (n == 0) {
            throw ERROR_REMOVE_FAILED.create();
        }
        if (collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.tag.remove.success.single", string, collection.iterator().next().getDisplayName()), true);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.tag.remove.success.multiple", string, collection.size()), true);
        }
        return n;
    }

    private static int listTags(CommandSourceStack commandSourceStack, Collection<? extends Entity> collection) {
        HashSet hashSet = Sets.newHashSet();
        for (Entity entity : collection) {
            hashSet.addAll(entity.getTags());
        }
        if (collection.size() == 1) {
            Entity entity = collection.iterator().next();
            if (hashSet.isEmpty()) {
                commandSourceStack.sendSuccess(new TranslatableComponent("commands.tag.list.single.empty", entity.getDisplayName()), false);
            } else {
                commandSourceStack.sendSuccess(new TranslatableComponent("commands.tag.list.single.success", entity.getDisplayName(), hashSet.size(), ComponentUtils.formatList(hashSet)), false);
            }
        } else if (hashSet.isEmpty()) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.tag.list.multiple.empty", collection.size()), false);
        } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.tag.list.multiple.success", collection.size(), hashSet.size(), ComponentUtils.formatList(hashSet)), false);
        }
        return hashSet.size();
    }
}

