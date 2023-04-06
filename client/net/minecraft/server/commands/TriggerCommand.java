/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class TriggerCommand {
    private static final SimpleCommandExceptionType ERROR_NOT_PRIMED = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.trigger.failed.unprimed"));
    private static final SimpleCommandExceptionType ERROR_INVALID_OBJECTIVE = new SimpleCommandExceptionType((Message)new TranslatableComponent("commands.trigger.failed.invalid"));

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)Commands.literal("trigger").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("objective", ObjectiveArgument.objective()).suggests((commandContext, suggestionsBuilder) -> TriggerCommand.suggestObjectives((CommandSourceStack)commandContext.getSource(), suggestionsBuilder)).executes(commandContext -> TriggerCommand.simpleTrigger((CommandSourceStack)commandContext.getSource(), TriggerCommand.getScore(((CommandSourceStack)commandContext.getSource()).getPlayerOrException(), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "objective"))))).then(Commands.literal("add").then(Commands.argument("value", IntegerArgumentType.integer()).executes(commandContext -> TriggerCommand.addValue((CommandSourceStack)commandContext.getSource(), TriggerCommand.getScore(((CommandSourceStack)commandContext.getSource()).getPlayerOrException(), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "objective")), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"value")))))).then(Commands.literal("set").then(Commands.argument("value", IntegerArgumentType.integer()).executes(commandContext -> TriggerCommand.setValue((CommandSourceStack)commandContext.getSource(), TriggerCommand.getScore(((CommandSourceStack)commandContext.getSource()).getPlayerOrException(), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)commandContext, "objective")), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"value")))))));
    }

    public static CompletableFuture<Suggestions> suggestObjectives(CommandSourceStack commandSourceStack, SuggestionsBuilder suggestionsBuilder) {
        Entity entity = commandSourceStack.getEntity();
        ArrayList arrayList = Lists.newArrayList();
        if (entity != null) {
            ServerScoreboard serverScoreboard = commandSourceStack.getServer().getScoreboard();
            String string = entity.getScoreboardName();
            for (Objective objective : serverScoreboard.getObjectives()) {
                Score score;
                if (objective.getCriteria() != ObjectiveCriteria.TRIGGER || !serverScoreboard.hasPlayerScore(string, objective) || (score = serverScoreboard.getOrCreatePlayerScore(string, objective)).isLocked()) continue;
                arrayList.add(objective.getName());
            }
        }
        return SharedSuggestionProvider.suggest(arrayList, suggestionsBuilder);
    }

    private static int addValue(CommandSourceStack commandSourceStack, Score score, int n) {
        score.add(n);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.trigger.add.success", score.getObjective().getFormattedDisplayName(), n), true);
        return score.getScore();
    }

    private static int setValue(CommandSourceStack commandSourceStack, Score score, int n) {
        score.setScore(n);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.trigger.set.success", score.getObjective().getFormattedDisplayName(), n), true);
        return n;
    }

    private static int simpleTrigger(CommandSourceStack commandSourceStack, Score score) {
        score.add(1);
        commandSourceStack.sendSuccess(new TranslatableComponent("commands.trigger.simple.success", score.getObjective().getFormattedDisplayName()), true);
        return score.getScore();
    }

    private static Score getScore(ServerPlayer serverPlayer, Objective objective) throws CommandSyntaxException {
        String string;
        if (objective.getCriteria() != ObjectiveCriteria.TRIGGER) {
            throw ERROR_INVALID_OBJECTIVE.create();
        }
        Scoreboard scoreboard = serverPlayer.getScoreboard();
        if (!scoreboard.hasPlayerScore(string = serverPlayer.getScoreboardName(), objective)) {
            throw ERROR_NOT_PRIMED.create();
        }
        Score score = scoreboard.getOrCreatePlayerScore(string, objective);
        if (score.isLocked()) {
            throw ERROR_NOT_PRIMED.create();
        }
        score.setLocked(true);
        return score;
    }
}

